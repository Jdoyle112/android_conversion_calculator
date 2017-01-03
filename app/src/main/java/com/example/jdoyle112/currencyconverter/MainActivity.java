package com.example.jdoyle112.currencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jdoyle112.currencyconverter.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import static android.R.attr.content;
import static android.R.attr.key;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    EditText mInputAmount;
    Spinner mBaseCurrency;
    Spinner mForeignCurrency;
    TextView mOutputAmount;
    ProgressBar mLoadingIndicator;
    public Double inputAmount;
    public String currencySymbol;


    //Double convRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputAmount = (EditText) findViewById(R.id.enter_amount);
        mButton = (Button) findViewById(R.id.submit);
        mBaseCurrency = (Spinner) findViewById(R.id.spinner);
        mForeignCurrency = (Spinner) findViewById(R.id.convert_to);
        mOutputAmount = (TextView) findViewById(R.id.output_amount);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // populate spinner w/ array strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBaseCurrency.setAdapter(adapter);
        mForeignCurrency.setAdapter(adapter);

        // fired when button clicked
        mButton.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View view){
                        // hide keyboard
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                        // get the selected amount
                        String inputValue = mInputAmount.getText().toString();
                        inputAmount = Double.parseDouble(inputValue);
                        // clear the input
                        mInputAmount.setText("");
                        if(inputAmount == null){
                            displayToast("You must enter an amount of currency to be converted.");
                            return;
                        }

                        // get the selected amount
                        String baseSelected = mBaseCurrency.getSelectedItem().toString();
                        String foreignSelected = mForeignCurrency.getSelectedItem().toString();
                        if(baseSelected != null && foreignSelected != null){
                            // check if currencies are equal
                            if(baseSelected == foreignSelected){
                                displayToast("You cannout select the same currencies. Please choose two different currencies to convert.");
                                return;
                            }
                            setCurrencySymbol(foreignSelected);
                            // build the api url using selected value
                            makeApiUrl(baseSelected, foreignSelected);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please select a currency to convert", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }
        );
    }

    public void displayToast(String message){
        if(message != "" && message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }


    public void convert(Double convRate){
        Double convertedValue = inputAmount * convRate;
        // limit to 2 decimal places
        String formattedValue = String.format("%.2f", convertedValue);
        formattedValue = currencySymbol.toString() + formattedValue;
        mOutputAmount.setText(formattedValue);
    }

    public void setCurrencySymbol(String country){
       switch (country){
            case "USD":
                currencySymbol = "\u0024";
                break;
            case "EUR":
                currencySymbol = "\u20AC";
                break;
            case "JPY":
                currencySymbol = "\u00A5";
                break;
            case "GBP":
                currencySymbol = "\u00A3";
                break;
            case "CAD":
                currencySymbol = "\u0024";
                break;
            case "CHF":
                currencySymbol = "\u20A3";
                break;
            case "AUD":
                currencySymbol = "\u0024";
                break;
            default:
                currencySymbol = "\u0024";
                break;
        }


    }

    private void makeApiUrl(String baseSelected, String foreignSelected){
        URL apiUrl = NetworkUtils.buildUrl(baseSelected, foreignSelected );
        new ApiQueryTask().execute(apiUrl);
    }

    public class ApiQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        // get api results in the background
        @Override
        protected String doInBackground(URL... params){
            URL apiUrl = params[0];
            String apiResults = null;
            try {
                // get response from http url
                apiResults = NetworkUtils.getHttpResponse(apiUrl);

            } catch(IOException e){
                e.printStackTrace();
            }
            return apiResults;
        }

        // set the text view with the results
        @Override
        protected void onPostExecute(String apiResults){
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(apiResults != null){
                // parse JSON object
                try {
                    JSONObject jsonResultsObj = new JSONObject(apiResults);
                    Object ratesObj = jsonResultsObj.get("rates");
                    JSONObject newResultsObj = new JSONObject(ratesObj.toString());
                    Iterator<String> keys = newResultsObj.keys();
                    while (keys.hasNext()){
                        String keyVal = keys.next();
                        Double convRate = newResultsObj.getDouble(keyVal);
                        if(convRate != null){
                            convert(convRate);
                        }
                    }

                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }


}
