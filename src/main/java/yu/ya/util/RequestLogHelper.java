package yu.ya.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import yu.ya.model.Message;
import yu.ya.model.MessageCreationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@UtilityClass
public final class RequestLogHelper {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_ID = "user_id";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Message createMessage(final MessageCreationContext context) {
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        return Message.builder()
                .node(request.getServerName())
                .application(context.getApplicationName())
                .requestMethod(request.getMethod())
                .requestPath(request.getRequestURI())
                .requestHeaders(getRequestHeaders(request).toString())
                .requestInput(context.getRequestInput().toString())
                .requestUserId(getUserId(request))
                .requestExecutionTime(context.getRequestExecutionSecond())
                .requestTimestamp(context.getRequestTime())
                .requestTimestampOrigin(context.getRequestTimeWithMicros())
                .responseCode(response.getStatus())
                .responseHeaders(getResponseHeaders(response).toString())
                .responseBody(context.getResponseBody())
                .build();
    }

    private static Map<String, String> getRequestHeaders(final HttpServletRequest request) {
        Iterator<String> iterator = request.getHeaderNames().asIterator();
        Map<String, String> headers = new HashMap<>();
        iterator.forEachRemaining(it -> headers.put(it, request.getHeader(it)));
        return headers;
    }

    private static Map<String, String> getResponseHeaders(final HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().forEach(it -> headers.put(it, response.getHeader(it)));
        return headers;
    }

    /**
     * Получение идентификатора пользователя из токена авторизации
     */
    @Nullable
    private static Integer getUserId(final HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        String jwt = header.substring(TOKEN_PREFIX.length());
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = Optional.of(jwt.split("\\."))
                .map(it -> it.length > 1 ? it[1] : EMPTY)
                .map(it -> new String(decoder.decode(it), StandardCharsets.UTF_8))
                .orElse(EMPTY);

        JsonNode node;

        try {
            node = mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            return null;
        }

        return Optional.ofNullable(node.get(USER_ID))
                .map(JsonNode::asText)
                .map(Integer::parseInt)
                .orElse(null);
    }
}
