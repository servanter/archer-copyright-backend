package com.archer.admin.web.component;

import com.archer.admin.web.util.TokenUtils;
import com.archer.admin.web.util.TokenUtils.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class WebContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(WebContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            UserInfo userInfo = TokenUtils.getUserInfo(token);
            return WebContext.builder()
                    .userId(userInfo.getUserId())
                    .userName(userInfo.getLoginName())
                    .build();
        }

        // 具体实现
        return WebContext.builder()
                .userId(-1)
                .userName("")
                .build();
    }
}
