package com.coloza.demo.springtest.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
class InventoryServiceTest {

    @Autowired
    private InventoryService service;

    private WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        // Start the WireMock Server
        wireMockServer = new WireMockServer(10000);
        wireMockServer.start();

        // Configure our requests
        wireMockServer.stubFor(get(urlEqualTo("/inventory/1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/inventory-response.json")));
        wireMockServer.stubFor(get(urlEqualTo("/inventory/2"))
                .willReturn(aResponse().withStatus(404)));
        wireMockServer.stubFor(post("/inventory/1/purchaseRecord")
                // Actual Header sent by the RestTemplate is: application/json;charset=UTF-8
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"productId\":1"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBodyFile("json/inventory-response-after-post.json")));
    }

    @AfterEach
    void afterEach() {
        wireMockServer.stop();
    }

    @Test
    void testGetInventoryRecordSuccess() {
        var inventoryRecord = service.getInventoryRecord(1);
        Assertions.assertTrue(inventoryRecord.isPresent(), "InventoryRecord should be present");

        // Validate the contents of the response
        Assertions.assertEquals(500, inventoryRecord.get().getQuantity().intValue(),
                "The quantity should be 500");
    }

    @Test
    void testGetInventoryRecordNotFound() {
        var inventoryRecord = service.getInventoryRecord(2);
        Assertions.assertFalse(inventoryRecord.isPresent(), "InventoryRecord should not be present");
    }

    @Test
    void testPurchaseProductSuccess() {
        var inventoryRecord = service.purchaseProduct(1, 5);
        Assertions.assertTrue(inventoryRecord.isPresent(), "InventoryRecord should be present");

        // Validate the contents of the response
        Assertions.assertEquals(495, inventoryRecord.get().getQuantity().intValue(),
                "The quantity should be 495");
    }

    @Test
    void testGetInventoryRecord_ServiceUnavailable_ReturnsEmpty() {
        wireMockServer.stubFor(get(urlEqualTo("/inventory/503"))
                .willReturn(aResponse().withStatus(503)));

        var inventoryRecord = service.getInventoryRecord(503);
        Assertions.assertFalse(inventoryRecord.isPresent(), "InventoryRecord should not be present on 503");
    }

    @Test
    void testGetInventoryRecord_Timeout_ReturnsEmpty() {
        wireMockServer.stubFor(get(urlEqualTo("/inventory/timeout"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("json/inventory-response.json")
                        .withFixedDelay(5000)));

        var inventoryRecord = service.getInventoryRecord(999);
        Assertions.assertFalse(inventoryRecord.isPresent(), "InventoryRecord should not be present on timeout");
    }
}
