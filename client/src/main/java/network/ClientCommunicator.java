package network;

import request.LoginRequest;
import result.LogRegResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientCommunicator {



    public String doGet(String urlString, String header) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");

        //Write header if they're authorized
        if(header!=null) {
            connection.addRequestProperty("Authorization", header);
        }

        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return readInputStream(responseBody);
        } else {
            InputStream responseBody = connection.getErrorStream();
            return readInputStream(responseBody);
        }
    }

    public String doPost(String urlString, String jsonBody, String header) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        return getServerResult(jsonBody, header, connection);
    }

    private String getServerResult(String jsonBody, String header, HttpURLConnection connection) throws IOException {
        //Write header if they're authorized
        if(header !=null) {
            connection.addRequestProperty("Authorization", header);
        }

        //Write body
        try(OutputStream requestBody = connection.getOutputStream();) {
            requestBody.write(jsonBody.getBytes());
        }

        connection.connect();


        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream responseBody = connection.getInputStream();
            return readInputStream(responseBody);
        } else {
            InputStream responseBody = connection.getErrorStream();
            return readInputStream(responseBody);
        }
    }

    public String doPut(String urlString, String jsonBody, String header) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setReadTimeout(5000);
        connection.setRequestMethod("Put");
        connection.setDoOutput(true);

        //Write header if they're authorized
        return getServerResult(jsonBody, header, connection);
    }

    // Helper method to read InputStream and return it as a String
    private String readInputStream(InputStream stream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString().trim();
    }
}
