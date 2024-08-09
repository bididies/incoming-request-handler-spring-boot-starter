package yu.ya.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "incoming-request-handler")
public class RequestLogProperties {

    private boolean enabled;

    /**
     * Logged endpoints
     */
    private List<String> paths;

    /**
     * Топик для отправки данных с логами приложения в Click House
     */
    @NotBlank
    private String kafkaTopic;

    /**
     * Включение дефолтного фильтра, обрабатывающего входящие запросы
     */
    private boolean filterEnabled;

    /**
     * Кафка кластер для логирования
     */
    @NotBlank
    private String kafkaBootstrapServers;

    /**
     * Максимальный размер тела логируемого request/response в килобайтах
     */
    @NotNull
    private Integer maxPayloadSizeKBytes;
}
