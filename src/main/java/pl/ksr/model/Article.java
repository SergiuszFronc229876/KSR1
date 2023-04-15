package pl.ksr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static org.immutables.value.Value.Default;
import static org.immutables.value.Value.Immutable;
import static org.immutables.value.internal.$processor$.meta.$CriteriaMirrors.CriteriaId;

@Immutable
@JsonSerialize(as = ImmutableArticle.class)
@JsonDeserialize(as = ImmutableArticle.class)
public interface Article {

    @CriteriaId
    @Default
    default String getId() {
        return java.util.UUID.randomUUID().toString();
    }

    String getPlace();

    String getText();
}
