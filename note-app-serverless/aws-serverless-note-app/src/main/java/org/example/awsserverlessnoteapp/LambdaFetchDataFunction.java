package org.example.awsserverlessnoteapp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaFetchDataFunction implements RequestHandler<Map<String,String>, List<Map<String, String>>> {
    private static final String TABLE_NAME = System.getenv("AWS_DYNAMO_TABLE_NAME_VALUE");
    private AmazonDynamoDBClient ddb;

    public LambdaFetchDataFunction() {
        ddb = (AmazonDynamoDBClient) AmazonDynamoDBClient.builder().withRegion(System.getenv("AWS_REGION_VALUE")).build();
    }
    @Override
    public List<Map<String,String>> handleRequest(Map<String, String> itemValues, Context context) {
        ScanRequest request = new ScanRequest()
                .withTableName(TABLE_NAME);

        ScanResult response = ddb.scan(request);

        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.getItems()) {
            Map<String, String> newItem = new HashMap<>();
            for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
                newItem.put(entry.getKey(), entry.getValue().getS());
            }
            result.add(newItem);
        }
        return result;
    }
}
