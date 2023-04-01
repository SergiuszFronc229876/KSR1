package pl.ksr.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);
    private final String dirPath;
    private final List<String> stopWords;

    public FileLoader(String dirPath, List<String> stopWords) {
        this.dirPath = dirPath;
        this.stopWords = stopWords;
    }

    public List<String> readFiles() {
        List<String> articleList = new ArrayList<>();

        for (File file : getArticlesFiles(new File(dirPath))) {
            try {
                articleList.add(readFile(file.getAbsolutePath()));
            } catch (IOException e) {
                LOGGER.error("Could not read files from given directory path: {}", dirPath);
                throw new RuntimeException(e);
            }
        }
        return articleList;
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return removeStopWords(sb.toString());
        } catch (IOException e) {
            LOGGER.error("Could not read content of file: {}", filePath);
            throw e;
        }
    }

    private String removeStopWords(String content) {
        ArrayList<String> stringList = Stream.of(content.split(" "))
                .collect(Collectors.toCollection(ArrayList<String>::new));
        stringList.removeAll(stopWords);
        return stringList.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    private File[] getArticlesFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            return files;
        } else {
            LOGGER.error("Files in given directory Path not found: {}", directory.getAbsolutePath());
            throw new IllegalArgumentException();
        }
    }
}
