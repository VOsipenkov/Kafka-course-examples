package examples.kafka.course;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableKafka
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final String topic;

    public void send(Order order) {
        log.info("Отправка сообщения в кафку {}", order);
        kafkaTemplate.send(topic, "", order);
    }

    @KafkaListener(topics = "#{topic}")
    public void listener(Order order) {
        log.info("Получено сообщение из кафки {}", order);
    }
}
