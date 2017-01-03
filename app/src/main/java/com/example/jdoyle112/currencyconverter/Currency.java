package com.example.jdoyle112.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import static android.R.attr.id;

public class Currency extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner mCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        mCurrencies = (Spinner) findViewById(R.id.spinner);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        String selected = mCurrencies.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
