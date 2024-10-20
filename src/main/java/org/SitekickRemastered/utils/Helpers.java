package org.SitekickRemastered.utils;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

public class Helpers {

    /**
     * Sends a post request to a link. Since the post request has no parameters / arguments, we just need to get the response.
     *
     * @param link         - The link that the POST request will be sent to.
     * @param params       - The parameters to send to the HTTP request
     * @param errorMessage - The error message to print if it fails.
     */
    public static JSONObject postRequest(String link, List<NameValuePair> params, String errorMessage) throws IOException, ParseException {

        // Setup the post request and send it.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(link);
        httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        CloseableHttpResponse response = httpclient.execute(httppost);
        JSONObject json = null;

        // If we get the code 200 back, then populate the json variable with the information
        if (response.getCode() == 200) {
            HttpEntity entity = response.getEntity();
            json = new JSONObject(EntityUtils.toString(entity, StandardCharsets.UTF_8));
            entity.close();
        }

        // Otherwise, print to the screen the code / why and where the request failed.
        else
            System.out.println(Instant.now() + " ERROR " + response.getCode() + ": " + errorMessage);

        httpclient.close();
        response.close();

        return json;
    }
}
