package com.coloza.demo.springtest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class AbstractMongoIT {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    protected MongoTemplate mongoTemplate;

    protected void loadData(String classpathJson, String collection) {
        mongoTemplate.dropCollection(collection);

        try (var is = getClass().getResourceAsStream(classpathJson)) {
            if (is == null) {
                throw new IllegalStateException("Cannot find " + classpathJson);
            }

            var docs = mapper.readValue(is, new TypeReference<List<Document>>() {
            });

            mongoTemplate.getCollection(collection).insertMany(docs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
