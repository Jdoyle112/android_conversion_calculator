package com.example.jdoyle112.currencyconverter.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Jdoyle112 on 1/3/2017.
 */

public class NetworkUtils {

    final static String API_BASE_URL = "http://api.fixer.io/latest";

    final static String BASE_PARAM = "base";

    final static String SYMBOLS_PARAM = "symbols";

    final static String BASE_QUERY_VALUE = "USD";


    // builds the url
    public static URL buildUrl(String baseSymbol, String foreignCurrencySymbol){
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendQueryParameter(BASE_PARAM, baseSymbol)
                .appendQueryParameter(SYMBOLS_PARAM, foreignCurrencySymbol)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return url;
    }

    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
           //scanner.useDelimiter("\\d");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
