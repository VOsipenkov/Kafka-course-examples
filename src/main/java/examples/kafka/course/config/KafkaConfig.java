package examples.kafka.course.config;

import examples.kafka.course.Order;
import examples.kafka.course.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final OrderService service;

    @Value("${spring.kafka.template.default-topic}")
    private String topicValue;

    @Bean
    public String topic() {
        return topicValue;
    }

    //    PRODUCERS

    @Bean
    @ConfigurationProperties("spring.kafka.producer")
    public Properties orderProducerProperties() {
        return new Properties();
    }

    @Bean
    public ProducerFactory<String, Order> orderProducerFactory(Properties orderProducerProperties) {
        Map<String, Object> config = new HashMap<>();
        orderProducerProperties.stringPropertyNames().forEach(p -> config.put(p, orderProducerProperties.get(p)));
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Order> orderKafkaTemplate(ProducerFactory<String, Order> orderProducerFactory) {
        return new KafkaTemplate<>(orderProducerFactory);
    }

    //    CONSUMERS

    @Bean
    @ConfigurationProperties("spring.kafka.consumer.order")
    public Properties orderConsumerProps() {
        return new Properties();
    }

    @Bean
    public ConsumerFactory<String, Order> orderConsumerFactory(Properties orderConsumerProps) {
        Map<String, Object> config = new HashMap<>();
        orderConsumerProps.stringPropertyNames().forEach(p -> config.put(p, orderConsumerProps.get(p)));
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public MessageListener<String, Order> orderListener() {
        return service::listener;
    }

    @Bean
    public ConcurrentMessageListenerContainer orderMessageListenerContainer(
        Properties orderConsumerProps, ConsumerFactory<String, Order> orderConsumerFactory) {
        var containerProperties = new ContainerProperties(new String[]{(String) orderConsumerProps.get("topic")});
        containerProperties.setMessageListener(orderListener());
        containerProperties.setGroupId("first");
        return new ConcurrentMessageListenerContainer(orderConsumerFactory, containerProperties);
    }
}
