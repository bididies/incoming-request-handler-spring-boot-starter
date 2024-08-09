package yu.ya.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

public class EnvPostProcessor implements EnvironmentPostProcessor {

    private static final String STARTER_NAME = "incoming-request-handler-spring-boot-starter";
    private static final String CREATE_PROPERTY_FAILED =
            String.format("Unable to create property source for the Spring Boot Starter [%s]", STARTER_NAME);

    private final YamlPropertySourceLoader yamlPropertyLoader;

    public EnvPostProcessor() {
        this.yamlPropertyLoader = new YamlPropertySourceLoader();
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var resource = new ClassPathResource("rq-default.yaml");
        PropertySource<?> propertySource;

        try {
            propertySource = yamlPropertyLoader.load(STARTER_NAME, resource).stream()
                    .findFirst()
                    .orElseThrow(() -> new NullPointerException(CREATE_PROPERTY_FAILED));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        environment.getPropertySources().addLast(propertySource);
    }
}
