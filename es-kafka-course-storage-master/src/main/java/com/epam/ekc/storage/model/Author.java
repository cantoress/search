package com.epam.ekc.storage.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(collection = "author")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author implements Serializable, Identifiable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Field("first_name")
  private String firstName;

  @Field("last_name")
  private String lastName;

  @DBRef
  @Field("book")
  @JsonIgnoreProperties("authors")
  private Book book;
}
