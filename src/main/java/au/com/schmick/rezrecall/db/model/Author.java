package au.com.schmick.rezrecall.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Author {

  @JsonProperty
  private String firstName;

  @JsonProperty
  private String familyName;

}
