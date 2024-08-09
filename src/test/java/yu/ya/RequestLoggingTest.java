package yu.ya;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import yu.ya.controller.TestController;
import yu.ya.kafka.KafkaConsumer;
import yu.ya.model.Message;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "incoming-request-handler.kafka-bootstrap-servers", ports = 58361)
@SpringBootTest(classes = {ApplicationTestContext.class, TestController.class, KafkaConsumer.class})
@AutoConfigureMockMvc
public class RequestLoggingTest {

    private static final String PATH = "/api/test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaConsumer consumer;

    @Test
    void contextLoads() {}

    @Test
    void logKafkaWriting() throws Exception {
        final String responseBody = "get-test";

        mockMvc.perform(get(PATH))
                .andDo(print())
                .andExpect(content().string(responseBody));

        consumer.getLatch().await(5000, TimeUnit.MILLISECONDS);
        Message message = consumer.getMessage();

        assertThat(consumer.getLatch().getCount()).isEqualTo(0L);
        assertThat(message.getRequestMethod()).isEqualTo("GET");
        assertThat(message.getRequestPath()).isEqualTo(PATH);
        assertThat(message.getResponseBody()).isEqualTo(responseBody);
    }
}

