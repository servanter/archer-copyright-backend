package com.archer.admin.web.copyright.service;

import com.archer.admin.base.entities.Copyright;
import com.archer.admin.base.service.CopyrightService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryReq;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryRes;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizCopyrightService {

    @Resource
    private CopyrightService copyrightService;

    public CopyrightRes query(int copyrightId) {
        return CopyrightRes.parseRes(copyrightService.getById(copyrightId));
    }

    public Result modify(Copyright copyright) {
        return copyrightService.updateById(copyright) ? Result.success() : Result.error();
    }

    public CopyrightQueryRes list(CopyrightQueryReq copyrightQueryReq) {
        Page<Copyright> page = copyrightService.lambdaQuery()
                
.like(StringUtils.isNotBlank(copyrightQueryReq.getCopyrightName()), Copyright::getCopyrightName, copyrightQueryReq.getCopyrightName())

.like(StringUtils.isNotBlank(copyrightQueryReq.getCpName()), Copyright::getCpName, copyrightQueryReq.getCpName())




.eq(Copyright::getValid, 1)

                .page(new Page<>(copyrightQueryReq.getPageNo(), copyrightQueryReq.getPageSize()));

        List<CopyrightRes> list = page.getRecords().stream().map(CopyrightRes::parseRes).collect(Collectors.toList());
        return CopyrightQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int copyrightId) {
        Copyright copyright = new Copyright();
        copyright.setId(copyrightId);
        copyright.setValid(-1);
        return copyrightService.updateById(copyright) ? Result.success() : Result.error();
    }

    public Result save(Copyright copyright) {
        return copyrightService.save(copyright) ? Result.success() : Result.error();
    }
}
