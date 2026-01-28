package com.archer.admin.web.productpic.service;

import com.archer.admin.base.entities.ProductPic;
import com.archer.admin.base.service.ProductPicService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicQueryReq;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicQueryRes;
import com.archer.admin.web.productpic.entities.ProductPicTransform.ProductPicRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizProductPicService {

    @Resource
    private ProductPicService productPicService;

    public ProductPicRes query(int product_picId) {
        return ProductPicRes.parseRes(productPicService.getById(product_picId));
    }

    public Result modify(ProductPic product_pic) {
        return productPicService.updateById(product_pic) ? Result.success() : Result.error();
    }

    public ProductPicQueryRes list(ProductPicQueryReq product_picQueryReq) {
        Page<ProductPic> page = productPicService.lambdaQuery()
                
.like(StringUtils.isNotBlank(product_picQueryReq.getProductId()), ProductPic::getProductId, product_picQueryReq.getProductId())
.eq(product_picQueryReq.getFileType() != 0, ProductPic::getFileType, product_picQueryReq.getFileType())
.like(StringUtils.isNotBlank(product_picQueryReq.getFileName()), ProductPic::getFileName, product_picQueryReq.getFileName())
.like(StringUtils.isNotBlank(product_picQueryReq.getFileUrl()), ProductPic::getFileUrl, product_picQueryReq.getFileUrl())
.eq(ProductPic::getValid, 1)

                .page(new Page<>(product_picQueryReq.getPageNo(), product_picQueryReq.getPageSize()));

        List<ProductPicRes> list = page.getRecords().stream().map(ProductPicRes::parseRes).collect(Collectors.toList());
        return ProductPicQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int product_picId) {
        ProductPic product_pic = new ProductPic();
        product_pic.setId(product_picId);
        product_pic.setValid(-1);
        return productPicService.updateById(product_pic) ? Result.success() : Result.error();
    }

    public Result save(ProductPic product_pic) {
        return productPicService.save(product_pic) ? Result.success() : Result.error();
    }
}
