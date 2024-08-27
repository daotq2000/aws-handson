package org.aws.resize;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

public class LambdaResizeImage implements RequestHandler<Map<String, String>, URL> {
    private AmazonS3Client s3Client;
    private String sourceBucket = System.getenv("AWS_S3_BUCKET");
    private String destinationBucket = System.getenv("AWS_S3_DESTINATION");;
    public LambdaResizeImage(AmazonS3Client s3Client) {
        this.s3Client = (AmazonS3Client) AmazonS3Client.builder().withRegion("us-east-1").build();
    }

    @Override
    public URL handleRequest(Map<String, String> input, Context context) {
        String key = input.get("key");

        try {
            InputStream s3InputStream = s3Client.getObject(new GetObjectRequest(sourceBucket, key)).getObjectContent();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(s3InputStream)
                    .size(100, 100)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            byte[] thumbnailBytes = outputStream.toByteArray();
            InputStream thumbnailStream = new ByteArrayInputStream(thumbnailBytes);

            s3Client.putObject(new PutObjectRequest(destinationBucket, key, thumbnailStream, null));
            return s3Client.generatePresignedUrl(sourceBucket,key, Date.from(LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC)));
        } catch (IOException e) {
            context.getLogger().log("Error processing file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
