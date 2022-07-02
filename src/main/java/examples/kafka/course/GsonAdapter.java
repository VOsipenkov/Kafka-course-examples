package examples.kafka.course;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class GsonAdapter<T> implements Serializer<T>, Deserializer<T> {

    private Gson gSon;
    private Class<T> clazz;

    private String VALUE_TYPE = "value-class";
    private String KEY_TYPE = "key-class";

    private static final String SERIALIZATION_MESSAGE_ERROR = "Serialization class not found";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        log.info("JsonAdapter configuration");
        GsonBuilder gSonBuilder = new GsonBuilder();
        gSon = gSonBuilder.create();
        configureClass(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, T t) {
        String result = gSon.toJson(t);
        return result.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        return gSon.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    private void configureClass(Map<String, ?> configs, boolean isKey) {
        String className = getClassName(configs, isKey);
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Can't to execute Class.forName");
            e.printStackTrace();
        }
    }

    private String getClassName(Map<String, ?> configs, boolean isKey) {
        String type = isKey ? KEY_TYPE : VALUE_TYPE;
        if (!configs.containsKey(type)) {
            log.error(SERIALIZATION_MESSAGE_ERROR);
            throw new SerializationException(SERIALIZATION_MESSAGE_ERROR);
        }
        return (String) configs.get(type);
    }
}
