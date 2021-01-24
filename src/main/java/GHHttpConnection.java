import exceptions.HttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GHHttpConnection {

    final HttpURLConnection connection;

    GHHttpConnection(URL url) throws IOException {
        this.connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
    }

    public String getResponse() throws IOException, HttpException {
        try {
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                //System.out.println(connection.getHeaderField("Retry-After"));
                throw new HttpException(statusCode, connection.getURL());
            }
        } catch (HttpException e) {
            connection.disconnect();
            throw e;
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return content.toString();

    }

}
