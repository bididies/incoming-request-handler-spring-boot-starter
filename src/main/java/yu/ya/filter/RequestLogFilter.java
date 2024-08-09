package yu.ya.filter;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import yu.ya.kafka.RequestHandlerKafkaSender;
import yu.ya.model.Message;
import yu.ya.model.MessageCreationContext;

import java.io.IOException;

import static yu.ya.util.RequestLogHelper.createMessage;

@Slf4j
@RequiredArgsConstructor
public class RequestLogFilter implements Filter {

    public static final String LOG_TRACE = "[{}] TraceId: {}, SpanId: {}";

    private final String appName;
    private final Integer payloadKByteSize;

    private final Tracer tracer;
    private final RequestHandlerKafkaSender<String, Message> kafkaSender;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        MessageCreationContext.MessageCreationContextBuilder builder = MessageCreationContext.builder()
                .timeReceiptOfRequest(System.currentTimeMillis())
                .payloadByteSize(payloadKByteSize * 1024)
                .applicationName(appName);

        ContentCachingRequestWrapper cachingRequestWrapper =
                new ContentCachingRequestWrapper((HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper cachingResponseWrapper =
                new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);

        builder.request(cachingRequestWrapper)
                .response(cachingResponseWrapper)
                .requestExecutionStart(System.nanoTime());

        filterChain.doFilter(cachingRequestWrapper, cachingResponseWrapper);

        builder.requestExecutionEnd(System.nanoTime());
        Message message = createMessage(builder.build());
        kafkaSender.send(message);

        cachingResponseWrapper.copyBodyToResponse();

        TraceContext context = tracer.currentSpan().context();
        log.info(LOG_TRACE, message.getApplication(), context.traceId(), context.spanId());
        log.info("{}", tracer.getClass());
    }
}
