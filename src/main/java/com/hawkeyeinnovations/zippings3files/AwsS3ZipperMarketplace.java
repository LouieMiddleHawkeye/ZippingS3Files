package com.hawkeyeinnovations.zippings3files;

import okhttp3.*;

import java.io.IOException;

public class AwsS3ZipperMarketplace {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("awsKey","AKIAQMVBC43AMBLBOGFX")
            .addFormDataPart("awsSecret","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")
            .addFormDataPart("awsBucket","louiem-test-zipper")
            .addFormDataPart("awsRegion","eu-west-1")
            .addFormDataPart("resultsEmail","louie.middle@hawkeyeinnovations.com")
            .addFormDataPart("filePaths","louiem-test-zipper")
            .addFormDataPart("zipTo","louiem-test-zipper/myzips")
            .addFormDataPart("expireLink","1")
            .addFormDataPart("bucketAsDir","true")
            .addFormDataPart("zipFileName","test.zip")
            .addFormDataPart("maxFileSize","10MB")
            .addFormDataPart("maxFileCount","10")
            .build();
        Request request = new Request.Builder()
            .url("http://ec2-18-203-68-6.eu-west-1.compute.amazonaws.com/v2/s3zip")
            .method("POST", body)
            .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body());
    }
}
