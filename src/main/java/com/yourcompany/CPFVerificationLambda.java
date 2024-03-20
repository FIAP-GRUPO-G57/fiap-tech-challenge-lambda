package com.yourcompany;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.enterprise.context.ApplicationScoped;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import jakarta.inject.Named;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Named("cpfVerification")
@ApplicationScoped
public class CPFVerificationLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    DynamoDbClient dynamoDB = DynamoDbClient.create();


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        System.out.println("Received event: " + event);
        String cpf = event.getQueryStringParameters().get("cpf");

        System.out.println("cpf: " + cpf);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("cpf", AttributeValue.builder().s(cpf).build());

        System.out.println("key: " + key);
        GetItemRequest request = GetItemRequest.builder()
                .tableName("cliente")
                .key(key)
                .build();

        System.out.println("request: " + request);

        var result = dynamoDB.getItem(request);

        System.out.println("result: " + result);

        if (result.item() == null || result.item().isEmpty()) {

            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(401);
            response.setBody("Unauthorized");
            return response;

        } else {
            // Here you should integrate with Amazon Cognito to retrieve an access token
            // This is a placeholder for where you would add that logic

            String clientId = "4daih53dr47miaualue9p8e14k";

            String username = "usuario2"; // Replace with a valid username
            String password = "PlaPla3030"; 



             CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                    .region(Region.US_EAST_1) // Substitua pela região do seu User Pool
                    .httpClient(UrlConnectionHttpClient.create())
                    .build();





                  

            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .clientId(clientId)
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .userPoolId("us-east-1_Ny9FfbR8y")
                    .authParameters(Map.of("USERNAME", username, "PASSWORD", password))
                    .build();

                   

            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);



            String accessToken = authResponse.authenticationResult().accessToken();
      

            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(200);
            response.setBody("CPF verification result: " + cpf);
            response.setHeaders(Map.of("Authorization", accessToken));
            return response;

        }


        
    }

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            //String hash = Base64.getEncoder().encodeToString(mac.doFinal((userName + userPoolClientId).getBytes()));
            
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
