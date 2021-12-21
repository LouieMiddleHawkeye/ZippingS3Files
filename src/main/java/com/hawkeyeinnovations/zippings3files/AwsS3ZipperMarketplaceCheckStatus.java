package com.hawkeyeinnovations.zippings3files;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class AwsS3ZipperMarketplaceCheckStatus {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"zipID\": \"c6776c2d-045f-402e-91e2-b7402cbc7cf0\"\n}");
        Request request = new Request.Builder()
            .url("http://ec2-18-203-68-6.eu-west-1.compute.amazonaws.com/v2/status")
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build();
        Response response = client.newCall(request).execute();
        System.out.println(Objects.requireNonNull(response.body()).string());
    }
}
