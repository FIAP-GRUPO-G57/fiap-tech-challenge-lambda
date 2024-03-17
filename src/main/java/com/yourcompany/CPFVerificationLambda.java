package com.yourcompany;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Named;

@Named("cpfVerification")
@ApplicationScoped
public class CPFVerificationLambda implements RequestHandler<Map<String, String>, String> {

    DynamoDbClient dynamoDB = DynamoDbClient.create();

    @Override
    public String handleRequest(Map<String, String> input, Context context) {
        String cpf = input.get("cpf");

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("cpf", AttributeValue.builder().s(cpf).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("cliente")
                .key(key)
                .build();

        var result = dynamoDB.getItem(request);

        if (result.item() == null || result.item().isEmpty()) {
            return "CPF not found";
        } else {
            // Here you should integrate with Amazon Cognito to retrieve an access token
            // This is a placeholder for where you would add that logic
            return "Access token retrieved successfully";
        }
    }
}
