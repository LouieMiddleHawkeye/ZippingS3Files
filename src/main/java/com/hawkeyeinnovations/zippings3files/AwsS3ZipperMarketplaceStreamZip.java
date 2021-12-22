package com.hawkeyeinnovations.zippings3files;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class AwsS3ZipperMarketplaceStreamZip {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("awsKey","-")
            .addFormDataPart("awsSecret","-")
            .addFormDataPart("awsBucket","louiem-test-zipper")
            .addFormDataPart("awsRegion","eu-west-1")
            .addFormDataPart("resultsEmail","louie.middle@hawkeyeinnovations.com")
            .addFormDataPart("filePaths","louiem-test-zipper/flinkData")
            .addFormDataPart("filePaths","louiem-test-zipper/images")
            .addFormDataPart("zipFileName","test_stream_zip.zip")
            .build();
        Request request = new Request.Builder()
            .url("http://ec2-18-203-68-6.eu-west-1.compute.amazonaws.com/v2/streamzip")
            .method("POST", body)
            .build();
        Response response = client.newCall(request).execute();
        System.out.println(Objects.requireNonNull(response.body()).string());
    }
}
