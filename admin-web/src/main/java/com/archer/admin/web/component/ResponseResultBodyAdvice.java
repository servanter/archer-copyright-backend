package com.archer.admin.web.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @Author: zhanghongyan p_hyanzhang
 * @Date: 2023/11/13 10:57 上午
 */
@Slf4j
@RestControllerAdvice
@Component
public class ResponseResultBodyAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ObjectMapper objectMapper;

    private static final Class<? extends Annotation> TYPE = ResponseResultBody.class;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        boolean b = AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), TYPE) || returnType
                .hasMethodAnnotation(TYPE);
        return b;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        try {
            Class returnClass = returnType.getMethod().getReturnType();
            if (body instanceof String || Objects.equals(returnClass, String.class)) {
                return objectMapper.writeValueAsString(Result.success(body));
            }

            if (body instanceof Result) {
                return body;
            }

            return Result.success(body);
        } catch (Exception e) {
            log.warn("error " + body, e);
        }
        return body;
    }
}
