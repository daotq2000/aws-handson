package org.example.awsserverlessnoteapp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class LambdaUpdateDataFunction implements RequestHandler<Map<String,Object>, String> {
    private static final String TABLE_NAME = System.getenv("AWS_DYNAMO_TABLE_NAME_VALUE");
    private AmazonDynamoDBClient ddb;

    public LambdaUpdateDataFunction() {
        ddb = (AmazonDynamoDBClient) AmazonDynamoDBClient.builder().withRegion(System.getenv("AWS_REGION_VALUE")).build();
    }
    @Override
    public String handleRequest(Map<String, Object> item, Context context) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(String.valueOf(item.get("id"))));

        Map<String, AttributeValueUpdate> itemUpdates = new HashMap<>();
        item.forEach((k, v) -> {
            if (!k.equals("id")) {
                itemUpdates.put(k, new AttributeValueUpdate().withValue(new AttributeValue().withS(String.valueOf(v))).withAction(AttributeAction.PUT));
            }
        });

        UpdateItemRequest request = new UpdateItemRequest()
                .withTableName(TABLE_NAME)
                .withKey(key)
                .withAttributeUpdates(itemUpdates);
        ddb.updateItem(request);
        return "Successfully Update";
    }
}
