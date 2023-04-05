package pl.ksr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableArticle.class)
@JsonDeserialize(as = ImmutableArticle.class)
public interface Article {
    String getPlace();

    List<String> getText();
}