package org.example.awsserverlessnoteapp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

public class LambdaAddDataFunction implements RequestHandler<Map<String,String>,String> {
    private static final String TABLE_NAME = System.getenv("AWS_DYNAMO_TABLE_NAME_VALUE");
    private AmazonDynamoDBClient ddb;

    public LambdaAddDataFunction() {
        ddb = (AmazonDynamoDBClient) AmazonDynamoDBClient.builder().withRegion(System.getenv("AWS_REGION_VALUE")).build();
    }
    @Override
    public String handleRequest(Map<String, String> itemValues, Context context) {
        Map<String, AttributeValue> item = new HashMap<>();
        itemValues.forEach((k, v) -> item.put(k, new AttributeValue().withS(v.toString())));
        PutItemRequest request = new PutItemRequest();
        request.setTableName(TABLE_NAME);
        request.setItem(item);
        ddb.putItem(request);
        return item.toString();
    }
}
