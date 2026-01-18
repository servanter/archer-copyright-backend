package com.archer.admin.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.archer.admin.web.component.Result;
import com.archer.admin.web.util.TokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if(request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String token = request.getHeader("token");
        if (StringUtils.isNotEmpty(token)) {
            boolean verity = TokenUtils.verity(token);
            if (verity) {
                return true;
            }
        }

        // 拦截请求，并返回响应
        //设置编码格式
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.write(JSONObject.toJSONString(Result.TOKEN_EXPIRED));
        pw.flush();
        pw.close();
        return false;
    }
}
