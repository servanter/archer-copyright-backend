package com.archer.admin.web.channel.controller;

import com.archer.admin.base.entities.Channel;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryReq;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelQueryRes;
import com.archer.admin.web.channel.entities.ChannelTransform.ChannelRes;
import com.archer.admin.web.channel.service.BizChannelService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.archer.admin.web.component.ResponseResultBody;
import com.archer.admin.web.component.WebContext;

@RequestMapping("/channel")
@RestController
@ResponseResultBody
public class ChannelController {

    @Resource
    private BizChannelService bizChannelService;

    @RequestMapping("/detail/{channelId}")
    public ChannelRes detail(WebContext webContext, @PathVariable("channelId") int channelId) {
        return bizChannelService.query(channelId);
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result modify(WebContext webContext, @RequestBody Channel channel) {
        return bizChannelService.modify(channel);
    }

    @RequestMapping("/list")
    public ChannelQueryRes list(WebContext webContext, ChannelQueryReq channelQueryReq) {
        return bizChannelService.list(channelQueryReq);
    }

    @RequestMapping("/remove")
    public Result remove(WebContext webContext, int id) {
        return bizChannelService.remove(id);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result add(WebContext webContext, @RequestBody Channel channel) {
        return bizChannelService.save(channel);
    }
}
