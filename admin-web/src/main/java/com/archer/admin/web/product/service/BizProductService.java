package com.archer.admin.web.product.service;

import com.alibaba.fastjson2.JSONArray;
import com.archer.admin.base.entities.*;
import com.archer.admin.base.service.*;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.component.WebContext;
import com.archer.admin.web.product.entities.ProductTransform;
import com.archer.admin.web.product.entities.ProductTransform.ProductChannelConfigRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductChannelSkuConfigRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductModifyStatusReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductPostReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductQueryReq;
import com.archer.admin.web.product.entities.ProductTransform.ProductQueryRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductRes;
import com.archer.admin.web.product.entities.ProductTransform.ProductSpecAttrRes;
import com.archer.admin.web.product.entities.ProductTransform.SaveChannelSkuLockConfig;
import com.archer.admin.web.product.entities.ProductTransform.SaveProductChannelConfig;
import com.archer.admin.web.product.entities.ProductTransform.SkuTransform;
import com.archer.admin.web.productchannel.entities.StockStrategyEnum;
import com.archer.admin.web.product.entities.StatusEnum;
import com.archer.admin.web.productpic.entities.FileTypeEnum;
import com.archer.admin.web.util.Utils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.g;
import org.springframework.stereotype.Component;

@Component
public class BizProductService {

    @Resource
    private ProductService productService;
    @Resource
    private CopyrightService copyrightService;
    @Resource
    private ProductPicService productPicService;
    @Resource
    private ProductSpecGroupService productSpecGroupService;
    @Resource
    private ProductSpecValueService productSpecValueService;
    @Resource
    private SkuService skuService;
    @Resource
    private ChannelService channelService;
    @Resource
    private ProductChannelService productChannelService;
    @Resource
    private ProductChannelSkuService productChannelSkuService;

    public ProductRes query(String productId) {
        return ProductRes.parseRes(productService.getById(productId));
    }

    public Result modify(ProductPostReq productPostReq) {
        handleProductPics(productPostReq.getId(), productPostReq.getPicMainUrls(), productPostReq.getPicIntroUrls());

        return productService.updateById(productPostReq) ? Result.success() : Result.error();
    }

    private void handleProductPics(String id, List<String> mainUrls, List<String> introUrls) {
        productPicService.lambdaUpdate()
                .eq(ProductPic::getProductId, id)
                .remove();

        List<ProductPic> mainList = mainUrls.stream()
                .map(s -> toProductPic(id, s, FileTypeEnum.PIC_MAIN))
                .collect(Collectors.toList());

        List<ProductPic> introList = introUrls.stream()
                .map(s -> toProductPic(id, s, FileTypeEnum.PIC_INTRO))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(mainList)) {
            productPicService.saveBatch(mainList);
        }

        if (CollectionUtils.isNotEmpty(introList)) {
            productPicService.saveBatch(introList);
        }
    }

    private ProductPic toProductPic(String id, String s, FileTypeEnum fileTypeEnum) {
        ProductPic productPic = new ProductPic();
        productPic.setProductId(id);
        productPic.setFileUrl(s);
        productPic.setFileType(fileTypeEnum.getValue());
        return productPic;
    }

    public ProductQueryRes list(ProductQueryReq productQueryReq) {
        Page<Product> page = productService.lambdaQuery()

                .eq(productQueryReq.getCopyrightId() != 0, Product::getCopyrightId, productQueryReq.getCopyrightId())
                .like(StringUtils.isNotBlank(productQueryReq.getProductName()), Product::getProductName,
                        productQueryReq.getProductName())
                .eq(productQueryReq.getThirdCategoryId() != 0, Product::getThirdCategoryId,
                        productQueryReq.getThirdCategoryId())

                .eq(productQueryReq.getStatus() != 0, Product::getStatus, productQueryReq.getStatus())

                .eq(Product::getValid, 1)

                .page(new Page<>(productQueryReq.getPageNo(), productQueryReq.getPageSize()));

        // 补充IP名称
        List<Integer> copyrightIds = page.getRecords()
                .stream()
                .map(Product::getCopyrightId)
                .collect(Collectors.toList());

        Map<Integer, Copyright> copyrightMap = CollectionUtils.isEmpty(copyrightIds)
                ? Maps.newHashMapWithExpectedSize(1)
                : copyrightService.lambdaQuery()
                        .in(Copyright::getId, copyrightIds)
                        .list().stream()
                        .collect(Collectors.toMap(Copyright::getId, Function.identity()));

        // 补充首图名称
        List<String> productIds = page.getRecords().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        List<ProductPic> pics = productPicService.lambdaQuery()
                .in(ProductPic::getProductId, productIds)
                .eq(ProductPic::getFileType, FileTypeEnum.PIC_MAIN.getValue())
                .list();

        Map<String, List<ProductPic>> picMap = pics.stream()
                .collect(Collectors.groupingBy(ProductPic::getProductId));

        List<ProductRes> list = page.getRecords().stream()
                .map(product -> ProductRes.parseRes(product,
                        copyrightMap.getOrDefault(product.getCopyrightId(), new Copyright()).getCopyrightName(),
                        picMap.getOrDefault(product.getId(), Lists.newArrayList())))
                .collect(Collectors.toList());

        return ProductQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(String productId) {
        Product product = new Product();
        product.setId(productId);
        product.setValid(-1);
        return productService.updateById(product) ? Result.success() : Result.error();
    }

    public Result save(ProductPostReq productPostReq) {
        productPostReq.setId(Utils.getRandomUuid());
        productPostReq.setStatus(StatusEnum.UNLISTED.getValue()); // 新增是未上架

        handleProductPics(productPostReq.getId(), productPostReq.getPicMainUrls(), productPostReq.getPicIntroUrls());
        return productService.save(productPostReq) ? Result.success() : Result.error();
    }

    public Result modifyStatus(ProductModifyStatusReq productModifyStatusReq) {
        return productService.lambdaUpdate()
                .set(Product::getStatus, productModifyStatusReq.getStatus())
                .eq(Product::getId, productModifyStatusReq.getId())
                .update() ? Result.success() : Result.error();
    }

    public Result settingSpecAndStock(WebContext webContext, ProductTransform.ProductSpecAggrReq productSpecAggrReq) {
        String productId = productSpecAggrReq.getProductId();
        AtomicInteger sort = new AtomicInteger(0);

        // 处理规格组
        List<ProductTransform.ProductSpecGroupTransform> specGroupReqs = productSpecAggrReq.getSpecs();
        List<String> submitSpecGroupIds = specGroupReqs.stream()
                .map(ProductTransform.ProductSpecGroupTransform::getId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        // 查询数据库中现有的规格组
        List<ProductSpecGroup> existingSpecGroups = productSpecGroupService.lambdaQuery()
                .eq(ProductSpecGroup::getProductId, productId)
                .list();
        List<String> existingSpecGroupIds = existingSpecGroups.stream()
                .map(ProductSpecGroup::getId)
                .collect(Collectors.toList());

        // 删除数据库中存在但提交数据中不存在的规格组
        List<String> specGroupIdsToDelete = existingSpecGroupIds.stream()
                .filter(id -> !submitSpecGroupIds.contains(id))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(specGroupIdsToDelete)) {
            productSpecGroupService.lambdaUpdate()
                    .in(ProductSpecGroup::getId, specGroupIdsToDelete)
                    .remove();
        }

        // 保存或更新规格组
        List<String> savedSpecGroupIds = new ArrayList<>();
        for (ProductTransform.ProductSpecGroupTransform groupReq : specGroupReqs) {
            ProductSpecGroup specGroup;
            if (StringUtils.isNotBlank(groupReq.getId())) {
                // 更新
                specGroup = productSpecGroupService.getById(groupReq.getId());
                if (specGroup != null) {
                    specGroup.setSpecName(groupReq.getName());
                    specGroup.setOperatorId(webContext.getUserId());
                    productSpecGroupService.updateById(specGroup);
                    savedSpecGroupIds.add(specGroup.getId());
                }
            } else {
                // 新增
                specGroup = toSepcGroup(productId, groupReq, webContext.getUserId(), sort.incrementAndGet());
                productSpecGroupService.save(specGroup);
                savedSpecGroupIds.add(specGroup.getId());
            }
        }

        // 处理规格值
        List<String> submitSpecValueIds = new ArrayList<>();
        for (ProductTransform.ProductSpecGroupTransform groupReq : specGroupReqs) {
            if (groupReq.getValues() != null) {
                submitSpecValueIds.addAll(groupReq.getValues().stream()
                        .map(ProductTransform.ProductSpecValueTransform::getId)
                        .filter(StringUtils::isNotBlank)
                        .collect(Collectors.toList()));
            }
        }

        // 查询数据库中现有的规格值
        List<ProductSpecValue> existingSpecValues = productSpecValueService.lambdaQuery()
                .eq(ProductSpecValue::getProductId, productId)
                .list();
        List<String> existingSpecValueIds = existingSpecValues.stream()
                .map(ProductSpecValue::getId)
                .collect(Collectors.toList());

        // 删除数据库中存在但提交数据中不存在的规格值
        List<String> specValueIdsToDelete = existingSpecValueIds.stream()
                .filter(id -> !submitSpecValueIds.contains(id))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(specValueIdsToDelete)) {
            productSpecValueService.lambdaUpdate()
                    .in(ProductSpecValue::getId, specValueIdsToDelete)
                    .remove();
        }

        // 保存或更新规格值
        Map<String, ProductSpecGroup> specGroupMap = productSpecGroupService.lambdaQuery()
                .eq(ProductSpecGroup::getProductId, productId)
                .list()
                .stream()
                .collect(Collectors.toMap(ProductSpecGroup::getSpecName, Function.identity(), (a, b) -> a));

        for (int i = 0; i < specGroupReqs.size(); i++) {
            ProductTransform.ProductSpecGroupTransform groupReq = specGroupReqs.get(i);
            String groupId = savedSpecGroupIds.get(i);
            groupReq.setId(groupId);

            if (groupReq.getValues() != null) {
                sort.set(0);
                for (ProductTransform.ProductSpecValueTransform valueReq : groupReq.getValues()) {
                    if (StringUtils.isNotBlank(valueReq.getId())) {
                        // 更新
                        ProductSpecValue specValue = productSpecValueService.getById(valueReq.getId());
                        if (specValue != null) {
                            specValue.setSpecValue(valueReq.getName());
                            specValue.setOperatorId(webContext.getUserId());
                            productSpecValueService.updateById(specValue);
                        }
                    } else {
                        // 新增
                        ProductSpecValue specValue = toSepcValue0(productId, groupId, valueReq, webContext.getUserId(),
                                sort.incrementAndGet());
                        productSpecValueService.save(specValue);
                    }
                }
            }
        }

        // 处理SKU
        List<String> submitSkuIds = productSpecAggrReq.getSkus().stream()
                .map(ProductTransform.SkuTransform::getId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        // 查询数据库中现有的SKU
        List<Sku> existingSkus = skuService.lambdaQuery()
                .eq(Sku::getProductId, productId)
                .list();
        List<String> existingSkuIds = existingSkus.stream()
                .map(Sku::getId)
                .collect(Collectors.toList());

        // 删除数据库中存在但提交数据中不存在的SKU
        List<String> skuIdsToDelete = existingSkuIds.stream()
                .filter(id -> !submitSkuIds.contains(id))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(skuIdsToDelete)) {
            skuService.lambdaUpdate()
                    .in(Sku::getId, skuIdsToDelete)
                    .remove();
        }

        // 保存或更新SKU
        for (ProductTransform.SkuTransform skuReq : productSpecAggrReq.getSkus()) {
            if (StringUtils.isNotBlank(skuReq.getId())) {
                // 更新
                Sku sku = skuService.getById(skuReq.getId());
                if (sku != null) {
                    sku.setSkuCode(skuReq.getSkuCode());
                    sku.setSpecValueIds(JSONArray.toJSONString(skuReq.getSpecValueIds()));
                    sku.setPrice(skuReq.getPrice());
                    sku.setTotalStock(skuReq.getStock());
                    sku.setFreezeStock(skuReq.getFreezeStock());
                    sku.setOperatorId(webContext.getUserId());
                    skuService.updateById(sku);
                }
            } else {
                // 新增
                Sku sku = toSku(productId, skuReq, webContext);
                skuService.save(sku);
            }
        }

        return Result.success();
    }

    private Sku toSku(String productId, ProductTransform.SkuTransform skuReq, WebContext webContext) {
        Sku sku = new Sku();
        sku.setId(Utils.getRandomUuid());
        sku.setProductId(productId);
        sku.setStatus(com.archer.admin.web.sku.entities.StatusEnum.PUBLISH.getValue());
        sku.setSkuCode(skuReq.getSkuCode());
        sku.setSpecValueIds(JSONArray.toJSONString(skuReq.getSpecValueIds()));
        sku.setPrice(skuReq.getPrice());
        sku.setTotalStock(skuReq.getStock());
        sku.setFreezeStock(skuReq.getFreezeStock());
        sku.setOperatorId(webContext.getUserId());
        return sku;
    }

    private ProductSpecValue toSepcValue0(String productId, String groupId,
            ProductTransform.ProductSpecValueTransform value, int userId, int sort) {
        ProductSpecValue productSpecValue = new ProductSpecValue();
        productSpecValue.setId(value.getId());
        productSpecValue.setProductId(productId);
        productSpecValue.setSpecGroupId(groupId);
        productSpecValue.setSpecValue(value.getName());
        productSpecValue.setSort(sort);
        productSpecValue.setOperatorId(userId);
        return productSpecValue;
    }

    private ProductSpecGroup toSepcGroup(String productId,
            ProductTransform.ProductSpecGroupTransform productSpecGroupReq, int userId, int sort) {
        ProductSpecGroup productSpecGroup = new ProductSpecGroup();
        productSpecGroup.setProductId(productId);
        productSpecGroup.setSpecName(productSpecGroupReq.getName());
        productSpecGroup.setSort(sort);
        productSpecGroup.setOperatorId(userId);
        return productSpecGroup;
    }

    public ProductSpecAttrRes getSpecAndStock(WebContext webContext, String productId) {

        List<ProductSpecGroup> groups = productSpecGroupService.lambdaQuery()
                .eq(ProductSpecGroup::getProductId, productId)
                .list();
        ;
        List<ProductSpecValue> values = productSpecValueService.lambdaQuery()
                .eq(ProductSpecValue::getProductId, productId)
                .list();

        // 获取规格数据
        List<ProductTransform.ProductSpecGroupTransform> gs = groups.stream().map(group -> {
            List<ProductSpecValue> valueList = values.stream()
                    .filter(value -> value.getSpecGroupId().equals(group.getId()))
                    .collect(Collectors.toList());
            ProductTransform.ProductSpecGroupTransform productSpecGroup = new ProductTransform.ProductSpecGroupTransform();
            productSpecGroup.setId(group.getId());
            productSpecGroup.setName(group.getSpecName());
            productSpecGroup.setValues(valueList.stream().map(value -> {
                ProductTransform.ProductSpecValueTransform productSpecValue = new ProductTransform.ProductSpecValueTransform();
                productSpecValue.setId(value.getId());
                productSpecValue.setName(value.getSpecValue());
                return productSpecValue;
            }).collect(Collectors.toList()));

            return productSpecGroup;
        }).collect(Collectors.toList());

        List<Sku> skus = skuService.lambdaQuery()
                .eq(Sku::getProductId, productId)
                .list();

        // sku信息
        List<ProductTransform.SkuTransform> skuTransforms = skus.stream().map(sku -> {
            ProductTransform.SkuTransform skuTransform = new ProductTransform.SkuTransform();
            skuTransform.setSkuCode(sku.getSkuCode());
            skuTransform.setId(sku.getId());
            skuTransform.setPrice(sku.getPrice());
            List<String> specValueIds = JSONArray.parseArray(sku.getSpecValueIds(), String.class);
            skuTransform.setSpecValueIds(specValueIds);
            skuTransform.setStock(sku.getTotalStock());
            skuTransform.setFreezeStock(sku.getFreezeStock());
            return skuTransform;
        }).collect(Collectors.toList());

        return ProductSpecAttrRes.builder()
                .specs(gs)
                .skus(skuTransforms)
                .build();
    }

    public List<ProductChannelConfigRes> queryProductChannelConfigList(WebContext webContext, String productId) {
        List<ProductChannel> productChannels = productChannelService.lambdaQuery()
                .eq(ProductChannel::getProductId, productId)
                .list();

        if (CollectionUtils.isEmpty(productChannels)) {
            return Collections.emptyList();
        }

        return productChannels.stream().map(productChannel -> {
            return ProductChannelConfigRes.builder()
                    .channelId(productChannel.getChannelId())
                    .channelName(channelService.getById(productChannel.getChannelId()).getChannelName())
                    .stockStrategy(StockStrategyEnum.of(productChannel.getStockStrategy()))
                    .platformProductId(productChannel.getPlatformProductId())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<ProductChannelSkuConfigRes> queryProductChannelSkuList(WebContext webContext, String productId,
            int channelId) {

        List<Sku> skus = skuService.lambdaQuery()
                .eq(Sku::getProductId, productId)
                .list();

        List<String> skuIds = skus.stream().map(Sku::getId).collect(Collectors.toList());

        List<ProductChannelSku> productChannelSkus = productChannelSkuService.lambdaQuery()
                .eq(ProductChannelSku::getProductId, productId)
                .eq(ProductChannelSku::getChannelId, channelId)
                .in(ProductChannelSku::getSkuId, skuIds)
                .list();

        if (CollectionUtils.isEmpty(productChannelSkus)) {
            return Collections.emptyList();
        }

        return productChannelSkus.stream().map(productChannelSku -> {
            return ProductChannelSkuConfigRes.builder()
                    .id(productChannelSku.getSkuId())
                    .shareNum(0)
                    .lockNum(productChannelSku.getLockNum())
                    .surplusNum(0)
                    .build();
        }).collect(Collectors.toList());
    }

    public Result saveProductChannelConfig(WebContext webContext, SaveProductChannelConfig saveProductChannelConfig) {

        List<ProductChannel> productChannels = saveProductChannelConfig.getPlatformProducts().stream().map(config -> {
            ProductChannel productChannel = new ProductChannel();
            productChannel.setProductId(saveProductChannelConfig.getProductId());
            productChannel.setChannelId(config.getChannelId());
            productChannel.setPlatformProductId(config.getPlatformProductId());
            productChannel.setStockStrategy(config.getStockStrategy());
            return productChannel;
        }).collect(Collectors.toList());

        productChannelService.saveBatch(productChannels);
        return Result.success();
    }

    public Result saveChannelSkuLock(WebContext webContext, SaveChannelSkuLockConfig saveChannelSkuLockConfig) {

        List<ProductChannelSku> productChannelSkus = saveChannelSkuLockConfig.getSku().stream().map(lock -> {
            ProductChannelSku productChannelSku = new ProductChannelSku();
            productChannelSku.setProductId(saveChannelSkuLockConfig.getProductId());
            productChannelSku.setChannelId(saveChannelSkuLockConfig.getChannel());
            productChannelSku.setSkuId(lock.getId());
            productChannelSku.setLockNum(lock.getLockNum());
            return productChannelSku;
        }).collect(Collectors.toList());

        productChannelSkuService.saveBatch(productChannelSkus);
        return Result.success();
    }

}
