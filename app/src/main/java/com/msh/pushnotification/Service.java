package com.msh.pushnotification;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class Service
{
    static Service instance = new Service();

    public Service()
    {

    }

    public static Service getInstance()
    {
        return instance;
    }

    public String sendParamsInBody(String phoneNumber, String registrationToken)
    {
        HttpURLConnection urlConn = null;
        URL uri;
        String requestBody = "";

        try
        {
            uri = new URL("http://82.115.17.20:9092/users/register");
            urlConn = (HttpURLConnection) uri.openConnection();

            createURLConnection(urlConn);
            requestBody = buildPostParameters(phoneNumber, registrationToken);

            Log.d("pushNotif", "post request URL: " + uri);
            Log.d("pushNotif", "post request requestBody: " + requestBody);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return callWebService(urlConn, requestBody);
    }

    private void createURLConnection(HttpURLConnection urlConn) throws ProtocolException
    {
        urlConn.setConnectTimeout(40000);
        urlConn.setReadTimeout(40000);
        urlConn.setRequestMethod("POST");
        urlConn.setAllowUserInteraction(false);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.addRequestProperty("Content-Type", "application/json");
    }

    private String buildPostParameters(String phoneNumber, String registrationToken)
    {
        JSONObject mParams = new JSONObject();
        try
        {
            mParams.put("mobileNumber", phoneNumber);
            mParams.put("registrationToken", registrationToken);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return mParams.toString();
    }

    private String callWebService(HttpURLConnection urlConn, String requestBody)
    {
        InputStream inputStream = null;
        String receivedData = "";

        try
        {
            if (urlConn.getRequestMethod().equals("POST"))
            {
                OutputStream os = urlConn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bw.write(requestBody);
                bw.flush();
                bw.close();
                os.close();
            }

            int responseCode = urlConn.getResponseCode();
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
                inputStream = urlConn.getInputStream();
            else
                inputStream = urlConn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = br.readLine()) != null)
            {
                receivedData += line;
            }
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            Log.d("pushNotif", "restAPI exception: " + e.getMessage());
            return "-1";
        }
        return receivedData;
    }
}
