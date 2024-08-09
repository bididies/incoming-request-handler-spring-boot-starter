package yu.ya.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import yu.ya.config.properties.RequestLogProperties;

@RequiredArgsConstructor
public class RequestHandlerKafkaSender<K, V> {

    private final RequestLogProperties properties;
    private final KafkaTemplate<K, V> kafkaTemplate;

    public void send(final V message) {
        String topic = properties.getKafkaTopic();
        kafkaTemplate.send(topic, message);
    }
}
