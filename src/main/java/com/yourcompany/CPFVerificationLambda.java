package com.yourcompany;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Named;

@Named("cpfVerification")
@ApplicationScoped
public class CPFVerificationLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    DynamoDbClient dynamoDB = DynamoDbClient.create();


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String cpf = event.getQueryStringParameters().get("cpf");

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("cpf", AttributeValue.builder().s(cpf).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName("cliente")
                .key(key)
                .build();

        var result = dynamoDB.getItem(request);

        if (result.item() == null || result.item().isEmpty()) {

            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(404);
            response.setBody("CPF not found: " + cpf);
            return response;

        } else {
            // Here you should integrate with Amazon Cognito to retrieve an access token
            // This is a placeholder for where you would add that logic

            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody("CPF verification result: " + cpf);
            return response;

        }


        
    }
}
