package yu.ya.model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@Builder
public class MessageCreationContext {

    private static final long THOUSAND = 1_000;
    private static final long MILLION = 1_000_000;
    private static final double ONE_DOUBLE = 1.0;

    private final String PAYLOAD_TOO_LARGE = "Payload is too large and exceeds %s KByte";

    private String applicationName;
    private Integer payloadByteSize;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * Поступление запроса в милисекундах
     */
    private long timeReceiptOfRequest;

    /**
     * Начало обработки запроса в наносекундах
     */
    private long requestExecutionStart;

    /**
     * Окончание обработки запроса в наносекундах
     */
    private long requestExecutionEnd;

    /**
     * Время обработки запроса в микросекундах
     */
    public long getRequestExecutionMicros() {
        return (requestExecutionEnd - requestExecutionStart) / THOUSAND;
    }

    /**
     * Время обработки запроса в секундах с микросекундами
     */
    public double getRequestExecutionSecond() {
        return (getRequestExecutionMicros() * 1.0 / MILLION);
    }

    /**
     * Время поступления запроса в секундах
     */
    public int getRequestTime() {
        return (int) (timeReceiptOfRequest / THOUSAND);
    }

    /**
     * Время поступления запроса в секундах с микросекундами
     */
    public double getRequestTimeWithMicros() {
        return (timeReceiptOfRequest * ONE_DOUBLE / THOUSAND);
    }

    public RequestInput getRequestInput() {
        return RequestInput.builder()
                .parameterMap(request.getParameterMap())
                .body(getRequestBody())
                .build();
    }

    public String getRequestBody() {
        byte[] payload = ((ContentCachingRequestWrapper) request).getContentAsByteArray();
        return checkPayloadSize(payload);
    }

    public String getResponseBody() {
        byte[] payload = ((ContentCachingResponseWrapper) response).getContentAsByteArray();
        return checkPayloadSize(payload);
    }

    private String checkPayloadSize(byte[] payload) {
        return payload.length > payloadByteSize
                ? String.format(PAYLOAD_TOO_LARGE, payloadByteSize / 1024)
                : new String(payload, StandardCharsets.UTF_8);
    }
}
