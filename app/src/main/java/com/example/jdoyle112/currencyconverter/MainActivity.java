package com.example.jdoyle112.currencyconverter;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import static android.R.attr.key;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    EditText mDollarAmount;
    Spinner mCurrencies;
    TextView mOutputAmount;

    //Double convRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDollarAmount = (EditText) findViewById(R.id.enter_amount);
        mButton = (Button) findViewById(R.id.submit);
        mCurrencies = (Spinner) findViewById(R.id.spinner);
        mOutputAmount = (TextView) findViewById(R.id.output_amount);

        // populate spinner w/ array strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCurrencies.setAdapter(adapter);

        // fired when button clicked
        mButton.setOnClickListener(
                new View.OnClickListener(){

                    public void onClick(View view){
                        // get the selected currency
                        String selected = mCurrencies.getSelectedItem().toString();
                        if(selected != null){
                            // build the api url using selected value
                            makeApiUrl(selected);

                        } else {
                            Toast.makeText(getApplicationContext(), "Please select a currency to convert", Toast.LENGTH_LONG).show();
                            return;
                        }

                    }
                }
        );
    }

    public void displayToast(Double amount){
        if(amount != null) {
            Toast.makeText(getApplicationContext(), amount.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public void convert(Double convRate){
        String value = mDollarAmount.getText().toString();
        Double usdAmount = Double.parseDouble(value);

        Double convertedValue = usdAmount * convRate;
        mOutputAmount.setText(convertedValue.toString());
    }


    private void makeApiUrl(String selected){
        URL apiUrl = NetworkUtils.buildUrl(selected);
        new ApiQueryTask().execute(apiUrl);
    }

    public class ApiQueryTask extends AsyncTask<URL, Void, String> {

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
