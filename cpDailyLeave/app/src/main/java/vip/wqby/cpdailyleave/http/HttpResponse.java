package vip.wqby.cpdailyleave.http;


import java.util.Arrays;

public class HttpResponse {
    private int code;
    private String body;
    private byte[] bytes;
    private String location;

    public HttpResponse() {

    }

    public HttpResponse(int code, String body, byte[] bytes, String location) {
        this.code = code;
        this.body = body;
        this.bytes = bytes;
        this.location = location;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", body='" + body + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                ", location='" + location + '\'' +
                '}';
    }
}
