package pl.ksr.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonReader {

    public static HashMap<String, List<String>> readJsonIntoMap(String filePath) {
        InputStream resourceAsStream = JsonReader.class.getResourceAsStream(filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resourceAsStream, new TypeReference<HashMap<String, List<String>>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Integer> readStringToArray(String json) {
        try {
            return new ObjectMapper().readValue(json, ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}