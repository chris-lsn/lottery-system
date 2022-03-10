package com.gogox.codingtest.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpResponseData {
    private HttpResponseStatusData status;

    private Object data;

    @Data
    @Builder
    static class HttpResponseStatusData {
        private boolean success;

        private String message;
    }

    public static HttpResponseData createResponse(boolean success, String message, Object data)
    {
        HttpResponseStatusData responseStatusData = HttpResponseStatusData.builder().success(success).message(message).build();
        return HttpResponseData.builder().status(responseStatusData).data(data).build();
    }
}
