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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class Helpers {

    public static HashMap<String, String> getRole = new HashMap<>() {{
        put(":sitekick:1024144049868898334", "Events");
        put(":mecharm:644016015377956874", "Collections");
        put(":excited:756931821794754691", "Updates");
        put(":frantic:644015153523851264", "Polls");
    }};


    /**
     * Saves a string to a file.
     *
     * @param path - The path of the txt file where we will save the string n to.
     * @param n    - The string that we want to write to the file
     */
    public static void save(String path, String n) throws IOException {
        File fp = new File(path);
        BufferedWriter log = new BufferedWriter(new FileWriter(fp));
        log.write(n);
        log.close();
    }


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

        // If we get the code 200 back (everything went OK), then populate the json variable with the information
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
