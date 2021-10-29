package com.hawkeyeinnovations.zippings3files;


import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipS3Files {

    public static void main(String[] args) throws IOException {
        String srcBucketName = "fdp-feed-data-stage";
        String outputBucketName = "louiem-test";
        Region region = Region.EU_WEST_1;

        long startTime = System.nanoTime();

        S3Client s3Client = S3Client.builder()
            .region(region)
            .build();

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(
            ListObjectsRequest.builder()
                .bucket(srcBucketName)
                .prefix("messages/2022/1_UEFA Champions League/89590_Stadion Wankdorf/2032651_Young Boys_Villarreal/2021-10-20/1800/delayed.samples.people.joints/")
                .build()
        );
        List<S3Object> s3Objects = new ArrayList<>(listObjectsResponse.contents());

        // TODO can't just zip everything in memory

        while (listObjectsResponse.isTruncated()) {
            List<S3Object> moreS3Objects = listObjectsResponse.contents();
            s3Objects.addAll(moreS3Objects);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        for (S3Object s3Object : s3Objects) {
            String key = s3Object.key();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(srcBucketName)
                .key(key)
                .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
            byte[] objectBytes = response.asByteArray();

            ZipEntry entry = new ZipEntry(key);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(objectBytes, 0, objectBytes.length);
            zipOutputStream.closeEntry();
        }

        String key = "test.zip";
        byte[] zip = byteArrayOutputStream.toByteArray();
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(outputBucketName)
                .key(key)
                .contentLength((long) zip.length)
                .contentType("application/zip")
                .build(),
            RequestBody.fromBytes(zip)
        );

        zipOutputStream.close();

        long endTime = System.nanoTime();
        long timeTaken = (endTime - startTime) / 1000000000;
        System.out.println("Time taken " + timeTaken + " seconds");
    }
}
