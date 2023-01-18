package au.com.schmick.rezrecall.db.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Author {

  @JsonProperty
  private String firstName;

  @JsonProperty
  private String familyName;

}
