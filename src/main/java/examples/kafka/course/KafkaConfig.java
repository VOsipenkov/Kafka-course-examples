package examples.kafka.course;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.template.default-topic}")
    private String topicValue;

    @Bean
    public String topic(){
        return topicValue;
    }
}
