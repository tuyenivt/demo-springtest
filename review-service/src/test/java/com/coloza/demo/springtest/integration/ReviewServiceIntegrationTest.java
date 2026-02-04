package com.coloza.demo.springtest.integration;

import com.coloza.demo.springtest.AbstractMongoIT;
import com.coloza.demo.springtest.model.Review;
import com.coloza.demo.springtest.model.ReviewEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class ReviewServiceIntegrationTest extends AbstractMongoIT {

    @Autowired
    private MockMvc mockMvc;

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void beforeEach() {
        loadData("/data/review/sample.json", "Reviews");
    }

    @Test
    @DisplayName("GET /review/1 - Found")
    void testGetReviewByIdFound() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", 1))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("user1")))
                .andExpect(jsonPath("$.entries[0].review", is("This is a review")));
    }

    @Test
    @DisplayName("GET /review/99 - Not Found")
    void testGetReviewByIdNotFound() throws Exception {

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", 99))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /review - Success")
    void testCreateReview() throws Exception {
        // Set up mocked service
        var postReviewEntry = ReviewEntry.builder().username("test-user").review("Great product").build();
        var postReview = Review.builder().productId(1).entries(List.of(postReviewEntry)).build();

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postReview)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().exists(HttpHeaders.LOCATION))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", any(String.class)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")))
                .andExpect(jsonPath("$.entries[0].date", any(String.class)));
    }

    @Test
    @DisplayName("POST /review/{productId}/entry")
    void testAddEntryToReview() throws Exception {
        // Set up mocked service
        var reviewEntry = ReviewEntry.builder().username("test-user").review("Great product").build();

        mockMvc.perform(post("/review/{productId}/entry", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(reviewEntry)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(2)))
                .andExpect(jsonPath("$.entries[0].username", is("user1")))
                .andExpect(jsonPath("$.entries[0].review", is("This is a review")))
                .andExpect(jsonPath("$.entries[1].username", is("test-user")))
                .andExpect(jsonPath("$.entries[1].review", is("Great product")))
                .andExpect(jsonPath("$.entries[1].date", any(String.class)));
    }

    @Test
    @DisplayName("DELETE /review/1 - Success")
    void deleteReview_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/review/{id}", 1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/review/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /review/99 - Not Found")
    void deleteReview_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/review/{id}", 99))
                .andExpect(status().isNotFound());
    }
}
