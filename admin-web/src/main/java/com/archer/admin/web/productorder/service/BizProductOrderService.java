package com.archer.admin.web.productorder.service;

import com.archer.admin.base.entities.Product;
import com.archer.admin.base.entities.ProductOrder;
import com.archer.admin.base.entities.ProductPic;
import com.archer.admin.base.entities.ProductSpecValue;
import com.archer.admin.base.entities.ProductSubOrder;
import com.archer.admin.base.entities.Sku;
import com.archer.admin.base.service.ProductOrderService;
import com.archer.admin.base.service.ProductPicService;
import com.archer.admin.base.service.ProductService;
import com.archer.admin.base.service.ProductSpecValueService;
import com.archer.admin.base.service.ProductSubOrderService;
import com.archer.admin.base.service.SkuService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.productpic.entities.FileTypeEnum;
import com.archer.admin.web.productorder.entities.LogisticsStatusEnum;
import com.archer.admin.web.productorder.entities.ProductOrderTransform;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderDetailRes;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderEditReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderQueryReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderQueryRes;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderRes;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderSelectProductReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderSelectSkuReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderShippingReq;
import com.archer.admin.web.productorder.entities.ProductOrderTransform.ProductOrderShippingItem;
import com.archer.admin.web.productorder.entities.SourceEnum;
import com.archer.admin.web.util.Utils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizProductOrderService {

    @Resource
    private ProductOrderService productOrderService;
    @Resource
    private ProductSubOrderService productSubOrderService;
    @Resource
    private ProductService productService;
    @Resource
    private ProductPicService productPicService;
    @Resource
    private SkuService skuService;
    @Resource
    private ProductSpecValueService productSpecValueService;

    public ProductOrderRes query(String product_orderId) {
        // 获取订单信息
        ProductOrder productOrder = productOrderService.getById(product_orderId);
        if (productOrder == null) {
            return null;
        }
        
        // 查询该订单的子订单
        List<ProductSubOrder> subOrders = productSubOrderService.lambdaQuery()
                .eq(ProductSubOrder::getOrderId, product_orderId)
                .eq(ProductSubOrder::getValid, 1)
                .list();
        
        List<ProductOrderDetailRes> details = new ArrayList<>();
        
        if (CollectionUtils.isNotEmpty(subOrders)) {
            // 获取所有商品ID
            List<String> productIds = subOrders.stream()
                    .map(ProductSubOrder::getProductId)
                    .collect(Collectors.toList());
                    
            // 查询商品信息
            Map<String, Product> productMap = productService.lambdaQuery()
                    .in(Product::getId, productIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
                    
            // 查询商品图片
            List<ProductPic> productPics = productPicService.lambdaQuery()
                    .in(ProductPic::getProductId, productIds)
                    .eq(ProductPic::getFileType, FileTypeEnum.PIC_MAIN.getValue())
                    .list();
                    
            Map<String, List<ProductPic>> productPicMap = productPics.stream()
                    .collect(Collectors.groupingBy(ProductPic::getProductId));
                    
            // 获取所有SKU ID
            List<String> skuIds = subOrders.stream()
                    .map(ProductSubOrder::getSkuId)
                    .collect(Collectors.toList());
                    
            // 查询SKU信息
            Map<String, Sku> skuMap = skuService.lambdaQuery()
                    .in(Sku::getId, skuIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Sku::getId, Function.identity()));
                    
            // 获取所有规格值ID
            List<String> specValueIds = skuMap.values().stream()
                    .filter(sku -> StringUtils.isNotBlank(sku.getSpecValueIds()))
                    .flatMap(sku -> {
                        List<String> ids = new ArrayList<>();
                        try {
                            ids = com.alibaba.fastjson2.JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                        } catch (Exception e) {
                            // 忽略解析错误
                        }
                        return ids != null ? ids.stream() : new ArrayList<String>().stream();
                    })
                    .collect(Collectors.toList());
                    
            // 查询规格值
            Map<String, ProductSpecValue> specValueMap = productSpecValueService.lambdaQuery()
                    .in(ProductSpecValue::getId, specValueIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(ProductSpecValue::getId, Function.identity()));
            
            // 处理每个子订单
            for (ProductSubOrder subOrder : subOrders) {
                // 获取商品信息
                Product product = productMap.get(subOrder.getProductId());
                List<ProductPic> productPicList = productPicMap.get(subOrder.getProductId());
                
                // 获取SKU信息并构建规格值字符串
                String specValues = "";
                Sku sku = skuMap.get(subOrder.getSkuId());
                if (sku != null && StringUtils.isNotBlank(sku.getSpecValueIds())) {
                    try {
                        List<String> valueIds = com.alibaba.fastjson2.JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                        if (CollectionUtils.isNotEmpty(valueIds)) {
                            List<String> specValueStrings = valueIds.stream()
                                    .map(id -> {
                                        ProductSpecValue specValue = specValueMap.get(id);
                                        return specValue != null ? specValue.getSpecValue() : "";
                                    })
                                    .filter(value -> StringUtils.isNotBlank(value))
                                    .collect(Collectors.toList());
                            specValues = specValueStrings.stream()
                                    .collect(Collectors.joining("，"));
                        }
                    } catch (Exception e) {
                        // 忽略解析错误
                    }
                }
                
                // 构建订单详情响应
                ProductOrderDetailRes detailRes = ProductOrderDetailRes.builder()
                        .id(subOrder.getId()) // 使用子订单ID
                        .productPic(productPicList != null && !productPicList.isEmpty() ? productPicList.get(0).getFileUrl() : "")
                        .productName(product != null ? product.getProductName() : "")
                        .specValues(specValues) // SKU的规格值
                        .subOrderId(subOrder.getId()) // 子订单ID
                        .price(subOrder.getPrice()) // SKU的价格
                        .num(subOrder.getNumber()) // SKU的数量
                        .logisticsStatus(subOrder.getLogisticsStatus())
                        .build();
                
                details.add(detailRes);
            }
        }
        
        // 返回包含详情的订单信息
        return ProductOrderRes.parseRes(productOrder, details);
    }

    public Result modify(ProductOrder product_order) {
        return productOrderService.updateById(product_order) ? Result.success() : Result.error();
    }

    public ProductOrderQueryRes list(ProductOrderQueryReq product_orderQueryReq) {
        Page<ProductOrder> page = productOrderService.lambdaQuery()
                .like(StringUtils.isNotBlank(product_orderQueryReq.getId()), ProductOrder::getId,
                        product_orderQueryReq.getId())

                .eq(product_orderQueryReq.getSource() != 0, ProductOrder::getSource, product_orderQueryReq.getSource())
                .eq(product_orderQueryReq.getLogisticsStatus() != 0, ProductOrder::getLogisticsStatus,
                        product_orderQueryReq.getLogisticsStatus())

                .eq(product_orderQueryReq.getOperatorId() != 0, ProductOrder::getOperatorId,
                        product_orderQueryReq.getOperatorId())
                .eq(ProductOrder::getValid, 1)

                .page(new Page<>(product_orderQueryReq.getPageNo(), product_orderQueryReq.getPageSize()));

        // 获取所有订单ID
        List<String> orderIds = page.getRecords().stream()
                .map(ProductOrder::getId)
                .collect(Collectors.toList());
                
        // 查询所有子订单
        Map<String, List<ProductSubOrder>> subOrderMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(orderIds)) {
            List<ProductSubOrder> subOrders = productSubOrderService.lambdaQuery()
                    .in(ProductSubOrder::getOrderId, orderIds)
                    .eq(ProductSubOrder::getValid, 1)
                    .list();
            
            subOrderMap = subOrders.stream()
                    .collect(Collectors.groupingBy(ProductSubOrder::getOrderId));
        }
        
        // 获取所有子订单
        List<ProductSubOrder> allSubOrders = subOrderMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        
        // 获取所有订单详情，按订单ID分组
        Map<String, List<ProductOrderDetailRes>> orderDetailsMap = new HashMap<>();
                
        if (CollectionUtils.isNotEmpty(allSubOrders)) {
            // 获取所有商品ID
            List<String> productIds = allSubOrders.stream()
                    .map(ProductSubOrder::getProductId)
                    .collect(Collectors.toList());
                    
            // 查询商品信息
            Map<String, Product> productMap = productService.lambdaQuery()
                    .in(Product::getId, productIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
                    
            // 查询商品图片
            List<ProductPic> productPics = productPicService.lambdaQuery()
                    .in(ProductPic::getProductId, productIds)
                    .eq(ProductPic::getFileType, FileTypeEnum.PIC_MAIN.getValue())
                    .list();
                    
            Map<String, List<ProductPic>> productPicMap = productPics.stream()
                    .collect(Collectors.groupingBy(ProductPic::getProductId));
                    
            // 获取所有SKU ID
            List<String> skuIds = allSubOrders.stream()
                    .map(ProductSubOrder::getSkuId)
                    .collect(Collectors.toList());
                    
            // 查询SKU信息
            Map<String, Sku> skuMap = skuService.lambdaQuery()
                    .in(Sku::getId, skuIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(Sku::getId, Function.identity()));
                    
            // 获取所有规格值ID
            List<String> specValueIds = skuMap.values().stream()
                    .filter(sku -> StringUtils.isNotBlank(sku.getSpecValueIds()))
                    .flatMap(sku -> {
                        List<String> ids = new ArrayList<>();
                        try {
                            ids = com.alibaba.fastjson2.JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                        } catch (Exception e) {
                            // 忽略解析错误
                        }
                        return ids != null ? ids.stream() : new ArrayList<String>().stream();
                    })
                    .collect(Collectors.toList());
                    
            // 查询规格值
            Map<String, ProductSpecValue> specValueMap = productSpecValueService.lambdaQuery()
                    .in(ProductSpecValue::getId, specValueIds)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(ProductSpecValue::getId, Function.identity()));
            
            // 按订单循环处理详情
            for (String orderId : subOrderMap.keySet()) {
                List<ProductSubOrder> subOrders = subOrderMap.get(orderId);
                if (CollectionUtils.isEmpty(subOrders)) {
                    continue;
                }
                
                List<ProductOrderDetailRes> details = new ArrayList<>();
                
                for (ProductSubOrder subOrder : subOrders) {
                    // 获取商品信息
                    Product product = productMap.get(subOrder.getProductId());
                    List<ProductPic> productPicList = productPicMap.get(subOrder.getProductId());
                    
                    // 获取SKU信息并构建规格值字符串
                    String specValues = "";
                    Sku sku = skuMap.get(subOrder.getSkuId());
                    if (sku != null && StringUtils.isNotBlank(sku.getSpecValueIds())) {
                        try {
                            List<String> valueIds = com.alibaba.fastjson2.JSONArray.parseArray(sku.getSpecValueIds(), String.class);
                            if (CollectionUtils.isNotEmpty(valueIds)) {
                                List<String> specValueStrings = valueIds.stream()
                                        .map(id -> {
                                            ProductSpecValue specValue = specValueMap.get(id);
                                            return specValue != null ? specValue.getSpecValue() : "";
                                        })
                                        .filter(value -> StringUtils.isNotBlank(value))
                                        .collect(Collectors.toList());
                                specValues = specValueStrings.stream()
                                        .collect(Collectors.joining("，"));
                            }
                        } catch (Exception e) {
                            // 忽略解析错误
                        }
                    }
                    
                    // 构建订单详情响应，每个SKU一个详情
                    ProductOrderDetailRes detailRes = ProductOrderDetailRes.builder()
                            .id(subOrder.getId()) // 使用子订单ID
                            .productPic(productPicList != null && !productPicList.isEmpty() ? productPicList.get(0).getFileUrl() : "")
                            .productName(product != null ? product.getProductName() : "")
                            .specValues(specValues) // SKU的规格值
                            .subOrderId(subOrder.getId()) // 子订单ID
                            .price(subOrder.getPrice()) // SKU的价格
                            .num(subOrder.getNumber()) // SKU的数量
                            .logisticsStatus(subOrder.getLogisticsStatus())
                            .build();
                    
                    details.add(detailRes);
                }
                
                orderDetailsMap.put(orderId, details);
            }
        }
        
        // 构建订单响应列表，包含详情
        List<ProductOrderRes> list = page.getRecords().stream().map(order -> {
            List<ProductOrderDetailRes> details = orderDetailsMap.get(order.getId());
            return ProductOrderRes.parseRes(order, details);
        }).collect(Collectors.toList());
        
        return ProductOrderQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(String id) {
        ProductOrder productOrder = new ProductOrder();
        productOrder.setId(id);
        productOrder.setValid(-1);
        return productOrderService.updateById(productOrder) ? Result.success() : Result.error();
    }

    public Result save(ProductOrderEditReq req) {
        // 计算总数量和总价格
        Double totalCount = 0.0;
        Double totalPrice = 0.0;

        if (CollectionUtils.isNotEmpty(req.getPurchaseProducts())) {
            for (ProductOrderSelectProductReq product : req.getPurchaseProducts()) {
                if (CollectionUtils.isNotEmpty(product.getSkuList())) {
                    for (ProductOrderSelectSkuReq sku : product.getSkuList()) {
                        totalCount += sku.getPurchaseQuantity();
                        totalPrice += sku.getPurchasePrice() * sku.getPurchaseQuantity();
                    }
                }
            }
        }

        // 第一步：创建ProductOrder记录
        ProductOrder productOrder = new ProductOrder();
        productOrder.setId(Utils.getRandomUuid());
        productOrder.setTotalCount(totalCount);
        productOrder.setTotalPrice(totalPrice);
        productOrder.setSource(SourceEnum.BACKEND.getValue());
        productOrder.setLogisticsStatus(LogisticsStatusEnum.WAIT_DELIVERY.getValue());
        productOrder.setPurchaser(req.getPurchaser());
        productOrder.setPurchaseContractFile(req.getPurchaseContractFile());
        productOrder.setReceiver(req.getReceiver());
        productOrder.setReceiverPhone(req.getReceiverPhone());
        productOrder.setReceiverProvince(req.getReceiverProvince());
        productOrder.setReceiverCity(req.getReceiverCity());
        productOrder.setReceiverArea(req.getReceiverArea());
        productOrder.setReceiverAddress(req.getReceiverAddress());
        productOrder.setOperatorId(req.getOperatorId());
        productOrder.setValid(1);

        if (!productOrderService.save(productOrder)) {
            return Result.error();
        }

        // 第二步：创建ProductSubOrder记录，每个SKU一个子订单
        if (CollectionUtils.isNotEmpty(req.getPurchaseProducts())) {
            for (ProductOrderSelectProductReq product : req.getPurchaseProducts()) {
                if (CollectionUtils.isNotEmpty(product.getSkuList())) {
                    for (ProductOrderSelectSkuReq sku : product.getSkuList()) {
                        ProductSubOrder subOrder = new ProductSubOrder();
                        subOrder.setId(Utils.getRandomUuid());
                        subOrder.setOrderId(productOrder.getId());
                        subOrder.setProductId(product.getProductId());
                        subOrder.setSkuId(sku.getId());
                        subOrder.setNumber(sku.getPurchaseQuantity());
                        subOrder.setPrice(sku.getPurchasePrice());
                        subOrder.setLogisticsStatus(LogisticsStatusEnum.WAIT_DELIVERY.getValue());
                        subOrder.setReceiver(req.getReceiver());
                        subOrder.setReceiverPhone(req.getReceiverPhone());
                        subOrder.setReceiverProvince(req.getReceiverProvince());
                        subOrder.setReceiverCity(req.getReceiverCity());
                        subOrder.setReceiverArea(req.getReceiverArea());
                        subOrder.setReceiverAddress(req.getReceiverAddress());
                        subOrder.setOperatorId(req.getOperatorId());
                        subOrder.setValid(1);

                        if (!productSubOrderService.save(subOrder)) {
                            return Result.error();
                        }
                    }
                }
            }
        }

        return Result.success();
    }

    public Result ship(ProductOrderShippingReq req) {
        // 验证订单是否存在
        ProductOrder productOrder = productOrderService.getById(req.getOrderId());
        if (productOrder == null) {
            return Result.errorMsg("订单不存在");
        }
        
        // 整单发货
        if (req.getShippingType() == 1) {
            // 更新订单状态
            productOrder.setLogisticsStatus(LogisticsStatusEnum.DELIVERED.getValue());
            
            if (!productOrderService.updateById(productOrder)) {
                return Result.errorMsg("更新订单状态失败");
            }
            
            // 更新所有子订单状态
            boolean updateResult = productSubOrderService.lambdaUpdate()
                    .eq(ProductSubOrder::getOrderId, req.getOrderId())
                    .set(ProductSubOrder::getLogisticsStatus, LogisticsStatusEnum.DELIVERED.getValue())
                    .set(ProductSubOrder::getShippingCompany, req.getLogisticsCompany())
                    .set(ProductSubOrder::getTrackingNumber, req.getLogisticsTrackingNumber())
                    .ne(ProductSubOrder::getLogisticsStatus, LogisticsStatusEnum.DELIVERED.getValue()) // 只更新未发货的子订单
                    .update();
            
            if (!updateResult) {
                return Result.errorMsg("更新子订单状态失败");
            }
        } 
        // 拆单发货
        else if (req.getShippingType() == 2) {
            // 验证子订单列表
            if (CollectionUtils.isEmpty(req.getProducts())) {
                return Result.errorMsg("子订单列表不能为空");
            }
        
            // 更新指定的子订单状态
            for (ProductOrderShippingItem item : req.getProducts()) {
                ProductSubOrder subOrder = productSubOrderService.getById(item.getSubOrderId());
                if (subOrder == null) {
                    return Result.errorMsg("子订单不存在: " + item.getSubOrderId());
                }
                
                // 更新子订单状态
                boolean updateResult = productSubOrderService.lambdaUpdate()
                        .eq(ProductSubOrder::getId, item.getSubOrderId())
                        .set(ProductSubOrder::getLogisticsStatus, LogisticsStatusEnum.DELIVERED.getValue())
                        .set(ProductSubOrder::getShippingCompany, req.getLogisticsCompany())
                        .set(ProductSubOrder::getTrackingNumber, req.getLogisticsTrackingNumber())
                        .update();
                
                if (!updateResult) {
                    return Result.errorMsg("更新子订单状态失败: " + item.getSubOrderId());
                }
            }
            
            // 检查该订单下所有子订单是否都已发货
            long undeliveredCount = productSubOrderService.lambdaQuery()
                    .eq(ProductSubOrder::getOrderId, req.getOrderId())
                    .eq(ProductSubOrder::getValid, 1)
                    .ne(ProductSubOrder::getLogisticsStatus, LogisticsStatusEnum.DELIVERED.getValue())
                    .count();
                    
            // 如果所有子订单都已发货，则更新主订单状态为已发货
            if (undeliveredCount == 0) {
                productOrder.setLogisticsStatus(LogisticsStatusEnum.DELIVERED.getValue());
                if (!productOrderService.updateById(productOrder)) {
                    return Result.errorMsg("更新主订单状态失败");
                }
            }
        } else {
            return Result.errorMsg("无效的发货类型");
        }
        
        return Result.success();
    }
}