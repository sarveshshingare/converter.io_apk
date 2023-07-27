package com.example.converterio;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean isConnected = false;
    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToconvert;
    Button convertButton;
    ConnectivityManager connectivityManager;

    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog ToDialog;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {"EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF", "PLN", "RON", "SEK", "ISK", "NOK", "HRK", "RUB", "TRY", "AUD", "BRL", "CAD", "CNY", "HKD", "IDR", "ILS", "INR", "KRW", "MXN", "MYR", "NZD", "PHP", "SGD", "THB", "ZAR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropDown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_To_dropDown_menu);
        convertButton = findViewById(R.id.btn_convert);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToconvert = findViewById(R.id.amountToConvertValueEditText);

        amountToconvert.requestFocus();

        arrayList = new ArrayList<>();
        for (String i : country) {
            arrayList.add(i);
        }
        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDialog = new Dialog(MainActivity.this);
                fromDialog.setContentView(R.layout.from_spinner);

                fromDialog.show();
                EditText editText = fromDialog.findViewById(R.id.edit_text);
                ListView listView = fromDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertFromDropdownTextView.setText(adapter.getItem(position));
                        fromDialog.dismiss();
                        convertFromValue = adapter.getItem(position);
                    }
                });
            }
        });

        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDialog = new Dialog(MainActivity.this);
                ToDialog.setContentView(R.layout.to_spinner);

                ToDialog.show();
                EditText editText = ToDialog.findViewById(R.id.edit_text);
                ListView listView = ToDialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convertToDropdownTextView.setText(adapter.getItem(position));
                        ToDialog.dismiss();
                        convertToValue = adapter.getItem(position);
                    }
                });
            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    if (TextUtils.isEmpty(convertFromValue)) {
                        convertFromDropdownTextView.setError("select");
                        convertFromDropdownTextView.requestFocus();
                    } else {
                        convertFromDropdownTextView.setError(null);
                    }
                    if (TextUtils.isEmpty(convertToValue)) {
                        convertToDropdownTextView.setError("select");
                        convertToDropdownTextView.requestFocus();
                    } else {
                        convertToDropdownTextView.setError(null);
                    }
                    try {


                        Double amountToConvert = Double.valueOf(MainActivity.this.amountToconvert.getText().toString());
                        getConverionRate(convertFromValue, convertToValue, amountToConvert);


                    } catch (Exception e) {

                    }
                } else {
                    AlertDialogBox();
                }

            }
        });
    }

    private void AlertDialogBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Connection Failed");
        builder.setMessage("Please Check Your Internet Connection");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               dialog.cancel();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void registerNetworkCallBack() {

        try {

            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected = false;
                }
            });
        } catch (Exception e) {
            isConnected = false;
            System.out.println(e);

        }

    }


    private String getConverionRate(String convertFromValue, String convertToValue, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.freecurrencyapi.com/v1/latest?apikey=fca_live_6tYVI92HDbwZplO3yu8pJfKzwW1NhvOJLAvpohbq&currencies=" + convertToValue + "&base_currency=" + convertFromValue;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);

                    Double temp = jsonObject.getJSONObject("data").getDouble(convertToValue);
                    System.out.println(temp);
                    conversionValue = "" + round((temp * amountToConvert), 4);
                    conversionRateText.setText(conversionValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        return null;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkCallBack();
    }


}
//done