package com.yourcompany;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CPFVerificationLambdaTest {

    @Inject
    CPFVerificationLambda lambda;

    @InjectMocks
    DynamoDbClient dynamoDB; // Mock DynamoDB if your function interacts with it

    @Test
    public void testCpfVerificationNotFound() {
        Map<String, String> input = new HashMap<>();
        input.put("cpf", "12345678900");

        // Mock the DynamoDB interactions if necessary

        String result = lambda.handleRequest(input, null); // null for Context as we usually don't need it for simple tests
        assertEquals("CPF not found", result); // Replace "Expected result" with the actual expected result
    }



}
