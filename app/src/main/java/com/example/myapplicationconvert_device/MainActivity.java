package com.example.myapplicationconvert_device;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity
{
    private Spinner from_conversions;
    private Spinner to_conversions;
    private EditText target;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        from_conversions = findViewById(R.id.convert_from);
        to_conversions = findViewById(R.id.convert_to);
        target = findViewById(R.id.currency_to_be_converted);
        tv = findViewById(R.id.currency_converted);

        target.setOnEditorActionListener((textview, id, event) -> {
            if(id == 1 || id == EditorInfo.IME_ACTION_GO)
            {
                if(!target.getText().toString().isEmpty())
                    convert(to_conversions.getSelectedItem().toString(), from_conversions.getSelectedItem().toString());
                return true;
            }

            return false;
        });

        String[] currencies = {"EUR", "MAD", "USD","AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN", "BHD",
                "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF", "CHF", "CLP", "CNY", "COP", "CRC",
                "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "FJD", "FKP", "FOK", "GBP", "GEL", "GGP", "GHS",
                "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS", "IMP", "INR", "UYU",
                "UZS", "VES", "VND", "VUV", "WST", "XAF", "XCD", "XDR", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWL"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, currencies);

        to_conversions.setAdapter(adapter);
        to_conversions.setSelection(1);

        from_conversions.setAdapter(adapter);
        from_conversions.setSelection(1);
    }

    private void convert(final String to, final String from)
    {
        CurrencyAPI api = getRetrofit().create(CurrencyAPI.class);
        Call<JsonObject> call = api.getExchangeCurrency(from);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull  Call<JsonObject> call, @NonNull Response<JsonObject> response)
            {
                assert response.body() != null;
                Log.e(MainActivity.class.getSimpleName(), response.body().toString());

                JsonObject json = response.body();
                JsonObject rates = json.getAsJsonObject("conversion_rates");

                String str_conversionValue = rates.get(to).toString();
                String str_numberToConvert = target.getText().toString();

                DecimalFormat formatter = new DecimalFormat("#.##");
                double conversionValue = Double.parseDouble(str_conversionValue); // multiplier variable on your code
                double numberToConvert = Double.parseDouble(str_numberToConvert); // currency variable on your code
                double result = numberToConvert * conversionValue;
                tv.setText("Result: " + formatter.format(result) + " " + to);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t)
            {}
        });
    }

    private Retrofit getRetrofit()
    {
        return (new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://v6.exchangerate-api.com/")
                .build());
    }
    private interface CurrencyAPI {
        @GET("v6/634026404c11d76206224073/latest/{currency}")
        Call<JsonObject> getExchangeCurrency(@Path("currency") String currency);
    }
}