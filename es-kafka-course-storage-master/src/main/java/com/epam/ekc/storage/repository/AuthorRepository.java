package com.epam.ekc.storage.repository;

import com.epam.ekc.storage.model.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
