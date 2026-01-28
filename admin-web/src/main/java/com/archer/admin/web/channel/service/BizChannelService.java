package com.archer.admin.web.channel.service;

import com.archer.admin.base.entities.Channel;
import com.archer.admin.base.service.ChannelService;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryReq;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryRes;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelRes;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BizChannelService {

    @Resource
    private ChannelService channelService;

    public ChannelRes query(int channelId) {
        return ChannelRes.parseRes(channelService.getById(channelId));
    }

    public Result modify(Channel channel) {
        return channelService.updateById(channel) ? Result.success() : Result.error();
    }

    public ChannelQueryRes list(ChannelQueryReq channelQueryReq) {
        Page<Channel> page = channelService.lambdaQuery()
                
.like(StringUtils.isNotBlank(channelQueryReq.getChannelName()), Channel::getChannelName, channelQueryReq.getChannelName())
.eq(Channel::getValid, 1)

                .page(new Page<>(channelQueryReq.getPageNo(), channelQueryReq.getPageSize()));

        List<ChannelRes> list = page.getRecords().stream().map(ChannelRes::parseRes).collect(Collectors.toList());
        return ChannelQueryRes.builder()
                .total(page.getTotal())
                .totalPage(page.getPages())
                .list(list)
                .build();
    }

    public Result remove(int channelId) {
        Channel channel = new Channel();
        channel.setId(channelId);
        channel.setValid(-1);
        return channelService.updateById(channel) ? Result.success() : Result.error();
    }

    public Result save(Channel channel) {
        return channelService.save(channel) ? Result.success() : Result.error();
    }
}
