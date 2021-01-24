package exceptions;

import java.net.URL;

public class HttpException extends Exception{

    public int statusCode;
    public String url;

    public HttpException(int statusCode, URL url) {
        super(String.format("The Status code was %d", statusCode));
        this.statusCode = statusCode;
    }
}
