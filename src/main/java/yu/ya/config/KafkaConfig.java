package yu.ya.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import yu.ya.config.properties.RequestLogProperties;
import yu.ya.kafka.RequestHandlerKafkaSender;
import yu.ya.model.Message;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
@ConditionalOnProperty(value = "incoming-request-handler.enabled", havingValue = "true")
public class KafkaConfig {

    @Bean
    public RequestHandlerKafkaSender<String, Message> clickHouseLoggingKafkaSender(final KafkaProperties kafkaProps,
                                                                                   final RequestLogProperties applicationProps) {
        ProducerFactory<String, Message> producerFactory = producerFactory(kafkaProps, applicationProps);
        KafkaTemplate<String, Message> kafkaTemplate = kafkaTemplate(producerFactory);
        return new RequestHandlerKafkaSender<>(applicationProps, kafkaTemplate);
    }

    private KafkaTemplate<String, Message> kafkaTemplate(final ProducerFactory<String, Message> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    private ProducerFactory<String, Message> producerFactory(final KafkaProperties kafkaProperties,
                                                             final RequestLogProperties applicationProps) {
        var props = kafkaProperties.buildProducerProperties();

        props.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(BOOTSTRAP_SERVERS_CONFIG, applicationProps.getKafkaBootstrapServers());

        return new DefaultKafkaProducerFactory<>(props);
    }
}
