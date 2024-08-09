package yu.ya.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * Идентификатор ноды (hostname)
     */
    private String node;

    /**
     * Имя приложения, логируемого запроса
     */
    private String application;

    /**
     * HTTP метод запроса
     */
    @JsonProperty("request_method")
    private String requestMethod;

    /**
     * Path запроса
     */
    @JsonProperty("request_path")
    private String requestPath;

    /**
     * Заголовки запроса
     */
    @JsonProperty("request_headers")
    private String requestHeaders;

    /**
     * Параметры и тело запроса
     */
    @JsonProperty("request_input")
    private String requestInput;

    /**
     * Идентификатор пользователя, вызвавшего запрос
     */
    @JsonProperty("request_user_id")
    private Integer requestUserId;

    /**
     * Время обработки запроса в микросекундах
     */
    @JsonProperty("request_execution_time")
    private double requestExecutionTime;

    /**
     * Время поступления запроса в секундах
     */
    @JsonProperty("request_timestamp")
    private int requestTimestamp;

    /**
     * Время поступления запроса в секундах с микросекундами
     */
    @JsonProperty("request_timestamp_origin")
    private double requestTimestampOrigin;

    /**
     * HTTP код ответа
     */
    @JsonProperty("response_code")
    private int responseCode;

    /**
     * Заголовки ответа
     */
    @JsonProperty("response_headers")
    private String responseHeaders;

    /**
     * Тело ответа
     */
    @JsonProperty("response_body")
    private String responseBody;
}
