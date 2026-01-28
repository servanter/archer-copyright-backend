package com.archer.admin.base.service.impl;

import com.archer.admin.base.entities.Channel;
import com.archer.admin.base.repository.ChannelMapper;
import com.archer.admin.base.service.ChannelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {
}
