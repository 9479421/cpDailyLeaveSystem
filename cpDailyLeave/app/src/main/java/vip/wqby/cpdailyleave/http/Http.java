package vip.wqby.cpdailyleave.http;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class Http {
    private URL url = null;
    private HttpURLConnection httpURLConnection = null;
    private HashMap<String, String> httpHeaders = new HashMap<>();

    private int connectTimeout = 8000;
    private int readTimeout = 8000;

    private HashMap<String, String> cookieMap = new HashMap<>();
    private boolean isAutoCookie = false;


    public Http() {

    }

    public Http(String urlPath) {
        try {
            url = new URL(urlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(String urlPath) {
        httpURLConnection = null;
        try {
            url = new URL(urlPath);
            trustAllHosts();

            if (url.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                httpURLConnection = https;
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AutoCookie() {
        isAutoCookie = true;
    }

    public String getCookie() {
        return httpHeaders.get("Cookie");
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void setUserAgent(String userAgent) {
        setHeader("User-Agent", userAgent);
    }

    public void setHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public void removeHeader(String key) {
        httpHeaders.remove(key);
    }

    public void setHeaders(Map<String, String> header) {
        if (httpHeaders == null) httpHeaders = new HashMap<String, String>();
        Iterator<String> iterHeaders = header.keySet().iterator();
        while (iterHeaders.hasNext()) {
            String key = iterHeaders.next();
            String value = header.get(key);
            httpHeaders.put(key, value);
        }
    }

    public void setHeaders(String str) {
        String[] headerArr = str.split("\n");
        for (String header : headerArr) {
            String[] split = header.split(":");
            if (split.length == 2) {
                httpHeaders.put(split[0], split[1]);
            }
        }
    }

    public String getHeaders() {
        String str = "";
        Iterator<String> iterator = httpHeaders.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = httpHeaders.get(key) + "\n";
            str = str + key + ":" + value + "\n";
        }
        return str;
    }

    public void setTimeout(int connectTime, int readTime) {
        connectTimeout = connectTime;
        readTimeout = readTime;
    }

    public void setConnectTimeout(int time) {
        connectTimeout = time;
    }

    public void setReadTimeout(int time) {
        readTimeout = time;
    }

    public HttpResponse post() throws IOException {
//        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        return send("POST", "");
    }

    public HttpResponse post(String params) throws IOException {
//        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        return send("POST", params);

    }

    public HttpResponse get() throws IOException {
//        httpURLConnection = (HttpURLConnection) url.openConnection();
        return send("GET", "");

    }


    private HttpResponse send(String method, String params) throws IOException {
        //自动Auto协议头
//        setHeader("Referer", url.toString() + params);
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(readTimeout);
        httpURLConnection.setInstanceFollowRedirects(false);

        //置入协议头
        httpURLConnection.setRequestProperty("Content-Length", params.length() + "");
        if (httpHeaders != null) {
            Iterator<String> iterator = httpHeaders.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpURLConnection.setRequestProperty(key, httpHeaders.get(key));
            }
        }
        if (method.toLowerCase().equals("post")) {
            OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
            writer.write(params);
            writer.flush();
            writer.close();
        }

        //发完消息，开始封装response
        HttpResponse httpResponse = new HttpResponse();
        //存储Cookie
        if (isAutoCookie) {
            Map<String, List<String>> CookieMap = httpURLConnection.getHeaderFields();
            Iterator<String> iterHeader = CookieMap.keySet().iterator();
            while (iterHeader.hasNext()) {
                String key = iterHeader.next();
                //判断cookie
                if (key != null && key.equals("Set-Cookie")) {
                    List<String> cookieArr = CookieMap.get(key);
                    for (String cookieChild : cookieArr) {
                        String[] cookies = cookieChild.split(";");
                        for (String cookie : cookies) {
                            String[] split = cookie.split("=");
                            if (split.length == 2) {
                                cookieMap.put(split[0], split[1]);
                            }
                        }
                    }

                }
                if (key != null && key.equals("Location")) {
                    httpResponse.setLocation(httpURLConnection.getHeaderField(key));
                }
            }
            String cookie = "";
            Iterator<String> iterCookie = cookieMap.keySet().iterator();
            while (iterCookie.hasNext()) {
                String key = iterCookie.next();
                String value = cookieMap.get(key);
                cookie += key + "=" + value + ";";
            }
            httpHeaders.put("Cookie", cookie);
        }

        int responseCode = httpURLConnection.getResponseCode();
        httpResponse.setCode(responseCode);
        if (responseCode == 200) {
            //置入Base64
/*            if (allowBase64) {
                httpResponse.setBase64(toBase64(httpURLConnection.getInputStream()));
            }*/
            /*BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line = "";
            String httpResult = "";
            while ((line = bf.readLine()) != null) {
                httpResult += line + "\n";
            }
            //去除尾部空行
            if (httpResult.lastIndexOf("\n") == httpResult.length()-1) {
                httpResult = httpResult.substring(0, httpResult.length() - 2);
            }
            bf.close();
            httpResponse.setBody(httpResult);*/
           /* try {
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(IOUtils.toByteArray(httpURLConnection.getInputStream())));
                StringBuffer szBuffer = new StringBuffer();
                byte tByte[] = new byte[1024];
                while (true) {
                    int iLength = gzip.read(tByte, 0, 1024); // <-- Error comes here
                    if (iLength < 0) {
                        break;
                    }
                    szBuffer.append(new String(tByte, 0, iLength));
                }
                httpResponse.setBody(szBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
                httpResponse.setBody(new String(IOUtils.toByteArray(httpURLConnection.getInputStream())));
            }*/
            byte []bytes = IOUtils.toByteArray(httpURLConnection.getInputStream());
            httpResponse.setBytes(bytes);
            httpResponse.setBody(new String(bytes));


        } else {
            //置入Base64
            /*if (allowBase64) {
                httpResponse.setBase64(toBase64(httpURLConnection.getInputStream()));
            }*/
            /*BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8"));
            String line = "";
            String httpResult = "";
            while ((line = bf.readLine()) != null) {
                httpResult += line + "\n";
            }
            //去除尾部空行
            if (httpResult.lastIndexOf("\n") == httpResult.length()-1) {
                httpResult = httpResult.substring(0, httpResult.length() - 2);
            }
            bf.close();
            httpResponse.setBody(httpResult);*/
            /*try {
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(IOUtils.toByteArray(httpURLConnection.getInputStream())));
                StringBuffer szBuffer = new StringBuffer();
                byte tByte[] = new byte[1024];
                while (true) {
                    int iLength = gzip.read(tByte, 0, 1024); // <-- Error comes here
                    if (iLength < 0) {
                        break;
                    }
                    szBuffer.append(new String(tByte, 0, iLength));
                }
                httpResponse.setBody(szBuffer.toString());
            } catch (Exception e) {

                httpResponse.setBody(new String(IOUtils.toByteArray(httpURLConnection.getInputStream())));
            }*/
            byte []bytes = IOUtils.toByteArray(httpURLConnection.getInputStream());
            httpResponse.setBytes(bytes);
            httpResponse.setBody(new String(bytes));

        }
        return httpResponse;
    }

/*
    public static String toBase64(InputStream inputStream) {
        try {
            //转换为base64
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
*/


    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {

            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
