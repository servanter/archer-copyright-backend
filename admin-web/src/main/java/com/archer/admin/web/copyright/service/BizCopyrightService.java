package com.archer.admin.web.copyright.service;

import com.archer.admin.base.entities.Copyright;
import com.archer.admin.base.service.CopyrightService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.copyright.entities.CopyrightTransform;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryReq;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightQueryRes;
import com.archer.admin.web.copyright.entities.CopyrightTransform.CopyrightRes;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;
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
        LambdaQueryChainWrapper<Copyright> wrapper = copyrightService.lambdaQuery()
                .like(StringUtils.isNotBlank(copyrightQueryReq.getCopyrightName()), Copyright::getCopyrightName, copyrightQueryReq.getCopyrightName())
                .like(StringUtils.isNotBlank(copyrightQueryReq.getCpName()), Copyright::getCpName, copyrightQueryReq.getCpName())
                .eq(Copyright::getValid, 1);

        if(copyrightQueryReq.getExpireStatus() != 0) {
            LocalDate now = LocalDate.now();
            switch (CopyrightTransform.ExpireStatusEnum.of(copyrightQueryReq.getExpireStatus())) {
                case EXPIRED: // EXPIRED
                    wrapper.lt(Copyright::getAuthExpireDate, now);
                    break;
                case EXPIRING_SOON: // EXPIRING_SOON
                    wrapper.ge(Copyright::getAuthExpireDate, now)
                        .lt(Copyright::getAuthExpireDate, now.plusDays(3));
                    break;
                case NORMAL: // NORMAL
                    wrapper.ge(Copyright::getAuthExpireDate, now.plusDays(3));
                    break;
}

        }

        Page<Copyright> page = wrapper.page(new Page<>(copyrightQueryReq.getPageNo(), copyrightQueryReq.getPageSize()));

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
