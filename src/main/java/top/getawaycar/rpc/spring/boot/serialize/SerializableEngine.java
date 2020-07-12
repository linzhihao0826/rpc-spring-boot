package top.getawaycar.rpc.spring.boot.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SerializableEngine {

    ObjectMapper objectMapper;

    public SerializableEngine() {
        objectMapper = new ObjectMapper();
    }

    public byte[] translateToSerializable(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T deserializable(byte[] data, Class<T> t) {
        try {
            return objectMapper.readValue(data, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
