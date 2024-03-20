
package com.yourcompany;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import jakarta.inject.Inject;
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
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setQueryStringParameters(Collections.singletonMap("cpf", "10345678900"));



        String result = String.valueOf(lambda.handleRequest(event, null)); // null for Context as we usually don't need it for simple tests
        assertEquals("{statusCode: 401,body: CPF not found: 10345678900}", result); // Replace "Expected result" with the actual expected result
    }


}