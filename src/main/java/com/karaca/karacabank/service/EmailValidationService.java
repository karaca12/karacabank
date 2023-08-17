package com.karaca.karacabank.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.karaca.karacabank.exception.ApiException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailValidationService {
    private static final String API_HOST="mailcheck.p.rapidapi.com";
    private static final String API_KEY="32af8f522bmsh10ceece95504f47p1cdf35jsn006f6e968432";
    public boolean validateEmail(String eMail){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url("https://"+API_HOST+"/?domain="
                        +getEmailDomain(eMail))
                .get()
                .addHeader("x-rapidapi-host",API_HOST)
                .addHeader("x-rapidapi-key",API_KEY)
                .build();
        try(Response response=client.newCall(request).execute()){
            if(response.isSuccessful()){
                String responseBody=response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                boolean isValid = jsonObject.get("valid").getAsBoolean();
                boolean isBlock = jsonObject.get("block").getAsBoolean();
                boolean isDisposable = jsonObject.get("disposable").getAsBoolean();
                return isValid&&!isBlock&&!isDisposable;
            }else {
                throw new ApiException("API request was not successful.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getEmailDomain(String eMail) {
        int atIndex=eMail.indexOf("@");
        if(atIndex>=0&&atIndex<eMail.length()-1){
            return eMail.substring(atIndex+1);
        }else return "";
    }

}
