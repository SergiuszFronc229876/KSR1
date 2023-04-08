package pl.ksr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableArticle.class)
@JsonDeserialize(as = ImmutableArticle.class)
public interface Article {
    String getPlace();

    String getText();
}
