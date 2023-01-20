package au.com.schmick.rezrecall.db.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "rezources")
@Getter
@Builder
@ToString
public class Rezource {

  @Id
  private BigInteger id;

  @JsonProperty(required = true)
  private String title;

  @JsonProperty(required = true)
  private Author primaryAuthor;

  @JsonProperty
  private Author secondaryAuthor;

  @JsonProperty(required = true)
  @Builder.Default
  private RezType type = RezType.BOOK;

  @JsonProperty(required = true)
  private String location;

}

