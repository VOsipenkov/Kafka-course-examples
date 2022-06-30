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
public class SimpleMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        log.info("Отправка сообщения в кафку {}", message);
        kafkaTemplate.sendDefault(message);
    }

    @KafkaListener(topics = "#{topic}")
    public void listener(String message) {
        log.info("Получено сообщение из кафки {}", message);
    }
}
