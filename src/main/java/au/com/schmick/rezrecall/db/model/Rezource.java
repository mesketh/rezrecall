package au.com.schmick.rezrecall.db.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Builder
@ToString
public class Rezource {

  @Id
  private BigInteger id;

  @JsonProperty(required = true)
  private String title;

  @JsonProperty(required = true)
  @Singular
  private List<Author> authors;

  @JsonProperty(required = true)
  @Builder.Default
  private RezType type = RezType.BOOK;

  @JsonProperty(required = true)
  private String location;

}

