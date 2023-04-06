package pl.ksr.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class JsonReader {

    public static HashMap<String, List<String>> readJsonIntoMap(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);
        try {
            return objectMapper.readValue(file, new TypeReference<HashMap<String, List<String>>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}