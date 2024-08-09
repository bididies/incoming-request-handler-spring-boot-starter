package yu.ya.config;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yu.ya.config.properties.RequestLogProperties;
import yu.ya.filter.RequestLogFilter;
import yu.ya.kafka.RequestHandlerKafkaSender;
import yu.ya.model.Message;

@Configuration
@EnableConfigurationProperties(RequestLogProperties.class)
@ConditionalOnProperty(value = "incoming-request-handler.enabled", havingValue = "true")
public class RequestLogFilterConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    @ConditionalOnProperty(value = "incoming-request-handler.filter-enabled", havingValue = "true")
    public FilterRegistrationBean<RequestLogFilter> registrationBean(
            final Tracer tracer,
            final RequestHandlerKafkaSender<String, Message> kafkaSender,
            final RequestLogProperties properties) {
        FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestLogFilter(appName, properties.getMaxPayloadSizeKBytes(),
                                                        tracer, kafkaSender));
        registrationBean.addUrlPatterns(properties.getPaths().toArray(new String[0]));

        return registrationBean;
    }
}
