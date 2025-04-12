package com.amazonaws.a1;

import java.util.Arrays;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class CreateMusicTable {
    public static void main(String[] args) throws Exception {
        // Create DynamoDB client

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "music";

        try {
            System.out.println("Creating table " + tableName + "...");
            // define table schema
            Table table = dynamoDB.createTable(tableName,
                        Arrays.asList(
                                // partition key
                                new KeySchemaElement("title", KeyType.HASH),
                                // Sort key
                                new KeySchemaElement("year", KeyType.RANGE)
                        ),
                        Arrays.asList(
                                // type as string
                                new AttributeDefinition("title", ScalarAttributeType.S),
                                // type as number
                                new AttributeDefinition("year", ScalarAttributeType.N)
                        ),
                        // capacity for read & write
                        new ProvisionedThroughput(5L, 5L)
                    );

            table.waitForActive();
            System.out.println("Success! Table status: " + table.getDescription().getTableStatus());
        } catch (Exception e) {
            System.err.println("Unable to create table: " + tableName);
            System.err.println(e.getMessage());
        }
    }
}
