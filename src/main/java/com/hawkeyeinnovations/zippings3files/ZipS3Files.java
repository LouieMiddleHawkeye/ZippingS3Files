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
    private static final String srcBucketName = "fdp-feed-data-stage";
    private static final String outputBucketName = "louiem-test";
    private static final Region region = Region.EU_WEST_1;
    private static final S3Client s3Client = S3Client.builder()
        .region(region)
        .build();
    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private static final ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
    private static final String key = "test.zip";

    private static int partNumber = 1;
    private static String uploadId;
    private static String eTag;
    private static List<CompletedPart> completedParts = new ArrayList<>();
    private static List<Long>  timeTakenPerPart = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();

        CreateMultipartUploadRequest multipartUploadRequest = CreateMultipartUploadRequest.builder()
            .bucket(outputBucketName)
            .key(key)
            .contentType("application/zip")
            .build();
        CreateMultipartUploadResponse multipartUploadResponse = s3Client.createMultipartUpload(multipartUploadRequest);
        uploadId = multipartUploadResponse.uploadId();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(
            ListObjectsV2Request.builder()
                .bucket(srcBucketName)
                .prefix("messages/2022/1_UEFA Champions League/89590_Stadion Wankdorf/2032651_Young Boys_Villarreal/2021-10-20/1800/delayed.samples.people.joints/")
                .build()
        );
        // Will only get 1000 objects each time
        List<S3Object> s3Objects = listObjectsResponse.contents();
        uploadPart(s3Objects);
        while (listObjectsResponse.isTruncated()) {
            String continuationToken = listObjectsResponse.nextContinuationToken();
            listObjectsResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                    .bucket(srcBucketName)
                    .continuationToken(continuationToken)
                    .build()
            );
            s3Objects = listObjectsResponse.contents();
            uploadPart(s3Objects);
        }

        zipOutputStream.close();

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build();

        s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
            .bucket(outputBucketName)
            .key(key)
            .uploadId(uploadId)
            .multipartUpload(completedMultipartUpload)
            .build()
        );

        long endTime = System.nanoTime();
        long timeTaken = (endTime - startTime) / 1000000000;
        System.out.println("Time taken " + timeTaken + " seconds");
        System.out.println("Average time per part " + getAverage(timeTakenPerPart));
    }

    private static void uploadPart(List<S3Object> s3Objects) throws IOException {
        long timeStart = System.nanoTime();
        for (S3Object s3Object : s3Objects) {
            String objKey = s3Object.key();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(srcBucketName)
                .key(objKey)
                .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes());
            byte[] objectBytes = response.asByteArray();

            ZipEntry entry = new ZipEntry(objKey);
            zipOutputStream.putNextEntry(entry);
            zipOutputStream.write(objectBytes, 0, objectBytes.length);
            zipOutputStream.closeEntry();
        }

        byte[] zipPart = byteArrayOutputStream.toByteArray();

        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
            .bucket(outputBucketName)
            .key(key)
            .uploadId(uploadId)
            .partNumber(partNumber)
            .contentLength((long) zipPart.length)
            .build();

        eTag = s3Client.uploadPart(
            uploadPartRequest,
            RequestBody.fromBytes(zipPart)
        ).eTag();

        CompletedPart completedPart = CompletedPart.builder()
            .partNumber(partNumber)
            .eTag(eTag)
            .build();
        completedParts.add(completedPart);

        System.out.println("part number " + partNumber);
        partNumber++;
        zipOutputStream.flush();
        long timeEnd = System.nanoTime();
        long timeTaken = (timeEnd - timeStart) / 1000000000;
        timeTakenPerPart.add(timeTaken);
        System.out.println("Time taken to upload part " + timeTaken);
    }

    private static long getAverage(List<Long> numbers) {
        long total = numbers.stream().mapToLong(number -> number).sum();
        return total / numbers.size();
    }
}
