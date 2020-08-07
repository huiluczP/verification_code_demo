package demo.verification_code_demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    // 为了前端能访问生成图片，进行本地映射
    @Value("${afterImage.resourceHandler}")
    private String resourceHandler;

    @Value("${afterImage.location}")
    private String location;

    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler(resourceHandler).addResourceLocations("file:///" + location);
    }
}
