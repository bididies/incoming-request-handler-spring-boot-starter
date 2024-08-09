package yu.ya.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import yu.ya.model.Message;

import java.util.concurrent.CountDownLatch;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.springframework.kafka.support.serializer.JsonDeserializer.VALUE_DEFAULT_TYPE;

@Getter
@Setter
@Component
public class KafkaConsumer {

    private static final String JSON_DESERIALIZER = "=org.springframework.kafka.support.serializer.JsonDeserializer";

    private CountDownLatch latch = new CountDownLatch(1);
    private Message message;

    @KafkaListener(topics = "${incoming-request-handler.kafka-topic}",
            properties = {
                    KEY_DESERIALIZER_CLASS_CONFIG + JSON_DESERIALIZER,
                    VALUE_DESERIALIZER_CLASS_CONFIG + JSON_DESERIALIZER,
                    VALUE_DEFAULT_TYPE + "=yu.ya.model.Message"
            })
    public void listener(Message message) {
        this.message = message;
        latch.countDown();
    }
}
