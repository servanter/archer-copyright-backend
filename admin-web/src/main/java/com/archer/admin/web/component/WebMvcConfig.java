package com.archer.admin.web.component;

import com.archer.admin.web.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;
    @Value("${file.upload.path:public}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 1. 前端真实运行地址，按需修改！！核对你的Vue端口，这个必须对
                .allowedOriginPatterns("http://localhost:8081","http://127.0.0.1:8081","http://localhost:5173","http://127.0.0.1:5173","http://localhost:3000")
                .allowedHeaders("*")
                .allowedMethods("*") // 放行所有请求方法，替代枚举，兼容性更强
                .allowCredentials(true)
                .exposedHeaders("*") // 暴露所有响应头，解决浏览器响应头校验问题
                .maxAge(3600);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new WebContextResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 修复问题4：核心！放行 登录接口 + 文件上传接口 + 静态资源（上传的图片访问）
                .excludePathPatterns("/login/**", "/common/upload", "/" + uploadPath + "/**");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 public 文件夹设置为静态资源目录
        String projectRoot = System.getProperty("user.dir");
        String absolutePath = "file:" + projectRoot + "/" + uploadPath + "/";
        registry.addResourceHandler("/" + uploadPath + "/**")
                .addResourceLocations(absolutePath);
    }
}