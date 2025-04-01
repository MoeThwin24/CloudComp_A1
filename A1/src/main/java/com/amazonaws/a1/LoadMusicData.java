package com.amazonaws.a1;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LoadMusicData {
    public static void main(String[] args) {

        // Create DynamoDB client
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        DynamoDB dynamoDB = new DynamoDB(client);
        Table table = dynamoDB.getTable("music");

        // load the data form the JSON 2025a1 file
        String filepath = "./2025a1.json";

        try {
            // to read Json data, need to create objectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode rootNode = objectMapper.readTree(new File(filepath));

            if (rootNode.has("songs")){
                List<Map<String, Object>> musicData = objectMapper.convertValue(
                        rootNode.get("songs"),
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                insertDataToTable(musicData, table);
                System.out.println("Data successfully loaded form " + filepath);
            }else {
                System.err.println("Error: 'song' array not found");
            }
            // put the data to a list of Map objects

        } catch (Exception e) {
            System.err.println("Unable to load data into the music table.");
            System.err.println(e.getMessage());
        }
    }

    public static void insertDataToTable(List<Map<String, Object>> musicData, Table table) {
        // Insert each into DynamoDB
        for (Map<String, Object> music : musicData) {
            try {
                String title = (String) music.get("title");
                int year = Integer.parseInt(music.get("year").toString());
                String artist = (String) music.get("artist");
                String album = (String) music.get("album");
                //String imageUrl = (music.get("image")!=null) ? (String) music.get("image").toString() : null;
                String imageUrl = (String) music.get("img_url");
                // Create item to insert
                Item item = new Item()
                        .withPrimaryKey("title", title, "year", year)
                        .withString("artist", artist)
                        .withString("album", album)
                        .withString("image_url", imageUrl);

                // Insert the item into the music table
                table.putItem(item);
                System.out.println("Inserted: " + title + " (" + year + ")");
            }catch (Exception e) {
                System.err.println("Error inserting item");
                e.printStackTrace();
            }
        }
    }
}
