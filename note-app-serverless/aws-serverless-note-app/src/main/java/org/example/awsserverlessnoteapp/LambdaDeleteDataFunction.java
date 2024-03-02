package org.example.awsserverlessnoteapp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class LambdaDeleteDataFunction implements RequestHandler<Map<String,String>,String> {
    private static final String TABLE_NAME = System.getenv("AWS_DYNAMO_TABLE_NAME_VALUE");
    private AmazonDynamoDBClient ddb;

    public LambdaDeleteDataFunction() {
        ddb = (AmazonDynamoDBClient) AmazonDynamoDBClient.builder().withRegion(System.getenv("AWS_REGION_VALUE")).build();
    }
   @Override
    public String handleRequest(Map<String, String> itemValues, Context context) {
       Map<String, AttributeValue> key = new HashMap<>();
       key.put("id", new AttributeValue().withS(itemValues.get("id")));
       DeleteItemRequest request = new DeleteItemRequest()
               .withTableName(TABLE_NAME)
               .withKey(key);
       ddb.deleteItem(request);
        return null;
    }
}
