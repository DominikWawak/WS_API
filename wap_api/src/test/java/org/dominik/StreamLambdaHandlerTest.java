package org.dominik;


import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class StreamLambdaHandlerTest {

    private static StreamLambdaHandler handler;
    private static Context lambdaContext;

    @BeforeAll
    public static void setUp() {
        handler = new StreamLambdaHandler();
        lambdaContext = new MockLambdaContext();
    }

    @Test
    public void query_weatherStation_returnsCorrectData() throws Exception {
        InputStream requestStream = new AwsProxyRequestBuilder("/weatherStation/query", HttpMethod.GET)
                .queryString("sensorIds","ws1,ws2")
                .queryString("metrics","temperature,humidity")
                .queryString("statistic","avg")
                .queryString("startDate","2025-01-14")
                .queryString("endDate","2025-01-24")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .buildStream();

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());  // Verify that the response status is OK (200)

        assertFalse(response.isBase64Encoded());

        assertTrue(response.getBody().contains("temperature"));
        assertTrue(response.getBody().contains("humidity"));

        assertTrue(response.getBody().contains("\"temperature\":16.0"));
        assertTrue(response.getBody().contains("\"humidity\":22.0"));

        assertTrue(response.getMultiValueHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getMultiValueHeaders().getFirst(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.APPLICATION_JSON));

    }

    @Test
    public void query_weatherStation_no_start_or_end_date() throws Exception {
        InputStream requestStream = new AwsProxyRequestBuilder("/weatherStation/query", HttpMethod.GET)
                .queryString("sensorIds","ws1,ws2")
                .queryString("metrics","temperature,humidity")
                .queryString("statistic","avg")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .buildStream();

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());  // Verify that the response status is OK (200)

        assertFalse(response.isBase64Encoded());

        assertTrue(response.getBody().contains("temperature"));
        assertTrue(response.getBody().contains("humidity"));

        assertTrue(response.getBody().contains("\"temperature\":16.0"));
        assertTrue(response.getBody().contains("\"humidity\":22.0"));

        assertTrue(response.getMultiValueHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getMultiValueHeaders().getFirst(HttpHeaders.CONTENT_TYPE).startsWith(MediaType.APPLICATION_JSON));

    }

    @Test
    public void query_weatherStation_missing_query() throws Exception {
        InputStream requestStream = new AwsProxyRequestBuilder("/weatherStation/query", HttpMethod.GET)
                .queryString("sensorIds","ws1,ws2")
                .queryString("statistic","avg")
                // no metrics
                .queryString("startDate","2025-01-14")
                .queryString("endDate","2025-01-24")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .buildStream();

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());  // Verify that the response status is OK (200)

        assertFalse(response.isBase64Encoded());
    }

    //TODO add more tests



    @Test
    public void invalidResource_streamRequest_responds404() {
        InputStream requestStream = new AwsProxyRequestBuilder("/pong", HttpMethod.GET)
                                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                                            .buildStream();
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

        handle(requestStream, responseStream);

        AwsProxyResponse response = readResponse(responseStream);
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatusCode());
    }

    private void handle(InputStream is, ByteArrayOutputStream os) {
        try {
            handler.handleRequest(is, os, lambdaContext);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private AwsProxyResponse readResponse(ByteArrayOutputStream responseStream) {
        try {
            return LambdaContainerHandler.getObjectMapper().readValue(responseStream.toByteArray(), AwsProxyResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error while parsing response: " + e.getMessage());
        }
        return null;
    }
}
