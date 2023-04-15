package pl.ksr.extractor;

import pl.ksr.model.Article;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureExtractorUtils {
    public static String[] getWordsFromText(Article article) {
        return article.getText().trim().split("\\W+");
    }

    public static Matcher provideMatcher(String text, String keyWord) {
        Pattern pattern = Pattern.compile("\\b" + keyWord.toLowerCase() + "\\b");
        return pattern.matcher(text);
    }
}
