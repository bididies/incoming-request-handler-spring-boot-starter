package yu.ya.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Deprecated
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public HandlerInterceptor requestHandleInterceptor() {
        return new RequestHandleInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHandleInterceptor());
    }
}
