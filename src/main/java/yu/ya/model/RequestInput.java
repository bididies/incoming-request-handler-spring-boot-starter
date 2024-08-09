package yu.ya.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
@Builder
public class RequestInput {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Параметры входящего запроса
     */
    private Map<String, String[]> parameterMap;

    /**
     * Тело входящего запроса
     */
    private String body;

    @Override
    public String toString() {
        String params = parameterMap.isEmpty() ? null : getValueAsString(parameterMap);
        String body = StringUtils.isEmpty(this.body) ? null : this.body;
        return "{\"params\": " + params + ", \"body\": " + body + "}";
    }

    @NonNull
    private String getValueAsString(final Object value) {
        String vs = StringUtils.EMPTY;
        try {
            vs = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {}

        return vs;
    }
}
