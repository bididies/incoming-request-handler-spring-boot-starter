package yu.ya.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Deprecated
@Slf4j
public class RequestHandleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("coming_time_rq", System.currentTimeMillis());
        request.setAttribute("start_handle_rq", System.nanoTime());

        log.info("Request IP: {}", getRequestIp(request));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long requestHandleStart = (long) request.getAttribute("start_handle_rq");
        long requestHandleEnd = System.nanoTime();
        long requestExecutionMicros = (requestHandleEnd - requestHandleStart) / 1000;

        response.addHeader("Header", "value");

        log.info("Время обработки запроса: {} мкс", requestExecutionMicros);
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }

    private String getRequestIp(final HttpServletRequest request) {
        String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
        return ipFromHeader != null && !ipFromHeader.isEmpty()
                ? ipFromHeader
                : request.getRemoteAddr();
    }
}
