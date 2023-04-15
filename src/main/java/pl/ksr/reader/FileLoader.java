package pl.ksr.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);

    public List<String> readFiles(String dirPaths) {
        List<File> files = Arrays.asList(getFilesList(new File(dirPaths)));
        Map<File, String> mapWhichKeepsOrder = new LinkedHashMap<>();
        files.forEach(file -> mapWhichKeepsOrder.put(file, null));
        files.parallelStream().forEach(file -> {
            try {
                LOGGER.debug("Reading File: {}", file.getName());
                mapWhichKeepsOrder.put(file, readFile(file.getAbsolutePath()));

            } catch (IOException e) {
                LOGGER.error("Could not read files from given directory path: {}", dirPaths);
                throw new RuntimeException(e);
            }
        });
        return new LinkedList<>(mapWhichKeepsOrder.values());
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(" ");
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            LOGGER.error("Could not read content of file: {}", filePath);
            throw e;
        }
    }

    private File[] getFilesList(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return files;
        } else {
            LOGGER.error("Files in given directory Path not found: {}", directory.getAbsolutePath());
            throw new IllegalArgumentException();
        }
    }
}
