package com.amazonaws.a1;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class DownloadAndUploadImages {

    private static final String BUCKET_NAME = "my-music-images-2025";
    private static final String FILE_PATH = "./2025a1.json";
    private static final String DOWNLOAD_DIRECTORY = "./downloaded_images/";

    public static void main(String[] args) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1).build();

            File dir = new File(DOWNLOAD_DIRECTORY);
            if (!dir.exists()) {
                dir.mkdir();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(FILE_PATH));

            if (rootNode.has("songs")) {
                List<Map<String, Object>> songsList = mapper.convertValue(
                       rootNode.get("songs"),
                       new TypeReference<List<Map<String, Object>>>() {}
                );
                for (Map<String, Object> song : songsList) {
                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    String imageUrl = (String) song.get("img_url");

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Download the image
                        String fileName = title.replaceAll("[^a-zA-Z0-9.-]", "_") + ".jpg";
                        String filePath = DOWNLOAD_DIRECTORY + fileName;
                        downloadImage(imageUrl, filePath);

                        uploadToS3(s3Client, BUCKET_NAME, fileName, filePath);
                    } else {
                        System.out.println("Skipping record with missing img_url");
                    }
                }
                System.out.println("All images downloaded and uploaded successfully");
            }else {
                System.out.println("Error: songs array not found");

            }
        }catch (Exception e) {
            System.err.println("Error processing images.");
            e.printStackTrace();
        }
    }

    // Method for downloading image
    public static void downloadImage(String imageUrl, String filePath) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream()){
            Files.copy(in, new File(filePath).toPath());
            System.out.println("Downloaded: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to download: " + imageUrl);
            throw e;
        }

    }
    public static void uploadToS3(AmazonS3 s3Client, String bucketName, String fileName, String filePath) {
        try {
            File file = new File(filePath);
            s3Client.putObject(bucketName, fileName, file);
            System.out.println("Uploaded to S3: " + fileName);
        }catch (AmazonServiceException e) {
            System.err.println("Failed to upload to S3: " + fileName);
            e.printStackTrace();
        }
    }
}
