package examples.kafka.course;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonAdapter<T> implements Serializer<T>, Deserializer<T> {

    private Gson gSon;
    private Class<T> clazz;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        GsonBuilder gSonBuilder = new GsonBuilder();
        gSon = gSonBuilder.create();
        configureClass();
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        return gSon.fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public byte[] serialize(String s, T t) {
        String result = gSon.toJson(t);
        return result.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    private void configureClass() {
        String className = "examples.kafka.course.Order";
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
