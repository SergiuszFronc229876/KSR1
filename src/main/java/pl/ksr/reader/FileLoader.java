package pl.ksr.reader;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);

    public List<String> readFiles(String dirPaths) {
        File filesDir = new File(dirPaths);
        List<File> files = Arrays.asList(getFilesList(filesDir));
        Collections.sort(files);
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
        try {
            FileUtils.cleanDirectory(filesDir);
            FileUtils.deleteDirectory(filesDir);
        } catch (IOException e) {
            throw new RuntimeException("Unable to clean temp articles directory structure: ", e);
        }
        return new LinkedList<>(mapWhichKeepsOrder.values());
    }

    public static Path getUnzipedArticlesPath(String zipArticles) {
        try {
            Path tmpZipDir = Files.createTempDirectory("articles");
            InputStream initialStream = FileLoader.class.getResourceAsStream(zipArticles);
            if (initialStream == null) {
                throw new RuntimeException();
            }
            File tmpZipFile = new File(tmpZipDir.toString() + "/zip_tmp.zip");

            FileUtils.copyInputStreamToFile(initialStream, tmpZipFile);
            ZipFile file = new ZipFile(tmpZipFile);
            file.extractAll(tmpZipDir.toAbsolutePath().toString());
            Files.deleteIfExists(tmpZipFile.toPath());
            return tmpZipDir;
        } catch (Exception e) {
            LOGGER.error("Could not unzip articles: {} ", zipArticles);
            throw new RuntimeException(e);
        }
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
