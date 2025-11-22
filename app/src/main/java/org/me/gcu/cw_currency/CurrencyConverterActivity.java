package org.me.gcu.cw_currency;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

public class CurrencyConverterActivity extends AppCompatActivity {
    private TextView titleText;
    private TextView rateText;
    private EditText gbpInput;
    private EditText foreignInput;
    private TextView gbpLabel;
    private TextView foreignLabel;
    private Button swapButton;
    private Button backButton;

    private String currencyName;
    private String currencyCode;
    private double exchangeRate;        // 1 GBP -> X FOREIGN
    private boolean isGbpToForeign = true;
    private boolean isUpdating = false;

    private static final String KEY_GBP_TEXT = "gbp_text";
    private static final String KEY_FOREIGN_TEXT = "foreign_text";
    private static final String KEY_DIR = "gbp_to_foreign";
    private static final String KEY_CODE = "currency_code";
    private static final String KEY_NAME = "currency_name";
    private static final String KEY_RATE = "exchange_rate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        // --- Get data from intent (or restored below) ---
        currencyName = getIntent().getStringExtra("CURRENCY_NAME");
        currencyCode = getIntent().getStringExtra("CURRENCY_CODE");
        exchangeRate = getIntent().getDoubleExtra("EXCHANGE_RATE", 0.0);  // default 0.0 (not 1.0)
        android.util.Log.d("ConverterLaunch",
                "Received -> " + currencyCode + " rate=" + exchangeRate);

        // --- Initialize views ---
        titleText   = findViewById(R.id.converterTitle);
        rateText    = findViewById(R.id.rateDisplay);
        gbpInput    = findViewById(R.id.gbpInput);
        foreignInput= findViewById(R.id.foreignInput);
        gbpLabel    = findViewById(R.id.gbpLabel);
        foreignLabel= findViewById(R.id.foreignLabel);
        swapButton  = findViewById(R.id.swapButton);
        backButton  = findViewById(R.id.backButton);

        // If we have saved state (rotation), restore first so UI shows correct values immediately
        if (savedInstanceState != null) {
            currencyName   = savedInstanceState.getString(KEY_NAME, currencyName);
            currencyCode   = savedInstanceState.getString(KEY_CODE, currencyCode);
            exchangeRate   = savedInstanceState.getDouble(KEY_RATE, exchangeRate);
            isGbpToForeign = savedInstanceState.getBoolean(KEY_DIR, true);
        }

        // === Guard against unusable rate (prevents Infinity/NaN) ===
        if (exchangeRate <= 0.0 || Double.isNaN(exchangeRate) || Double.isInfinite(exchangeRate)) {
            Toast.makeText(this, "Exchange rate unavailable for this currency.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Set up UI ---
        titleText.setText("Currency Converter");
        updateRateDisplay();
        updateLabels();

        if (savedInstanceState != null) {
            gbpInput.setText(savedInstanceState.getString(KEY_GBP_TEXT, ""));
            foreignInput.setText(savedInstanceState.getString(KEY_FOREIGN_TEXT, ""));
        }

        // --- Text watchers ---
        gbpInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating && s.length() > 0) convertCurrency(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        foreignInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating && s.length() > 0) convertCurrency(false);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // --- Buttons ---
        swapButton.setOnClickListener(v -> swapConversionDirection());
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_GBP_TEXT, gbpInput.getText().toString());
        outState.putString(KEY_FOREIGN_TEXT, foreignInput.getText().toString());
        outState.putBoolean(KEY_DIR, isGbpToForeign);
        outState.putString(KEY_CODE, currencyCode);
        outState.putString(KEY_NAME, currencyName);
        outState.putDouble(KEY_RATE, exchangeRate);
    }

    private void updateRateDisplay() {
        if (isGbpToForeign) {
            rateText.setText(String.format(Locale.US, "1 GBP = %.4f %s", exchangeRate, currencyCode));
        } else {
            rateText.setText(String.format(Locale.US, "1 %s = %.4f GBP", currencyCode, 1.0 / exchangeRate));
        }
    }

    private void updateLabels() {
        if (isGbpToForeign) {
            gbpLabel.setText("GBP (British Pound):");
            foreignLabel.setText(currencyCode + " (" + currencyName + "):");
        } else {
            gbpLabel.setText(currencyCode + " (" + currencyName + "):");
            foreignLabel.setText("GBP (British Pound):");
        }
    }

    private void convertCurrency(boolean fromGbpField) {
        isUpdating = true;
        try {
            if (fromGbpField) {
                String gbpText = gbpInput.getText().toString();
                if (!gbpText.isEmpty()) {
                    double gbpAmount = Double.parseDouble(gbpText);
                    double foreignAmount = isGbpToForeign
                            ? gbpAmount * exchangeRate
                            : gbpAmount / exchangeRate;
                    foreignInput.setText(String.format(Locale.US, "%.2f", foreignAmount));
                }
            } else {
                String foreignText = foreignInput.getText().toString();
                if (!foreignText.isEmpty()) {
                    double foreignAmount = Double.parseDouble(foreignText);
                    double gbpAmount = isGbpToForeign
                            ? foreignAmount / exchangeRate
                            : foreignAmount * exchangeRate;
                    gbpInput.setText(String.format(Locale.US, "%.2f", gbpAmount));
                }
            }
        } catch (NumberFormatException ignored) {
            // do nothing on invalid input
        } finally {
            isUpdating = false;
        }
    }

    private void swapConversionDirection() {
        isGbpToForeign = !isGbpToForeign;
        updateRateDisplay();
        updateLabels();

        // Swap the current text values to match new direction
        String temp = gbpInput.getText().toString();
        gbpInput.setText(foreignInput.getText().toString());
        foreignInput.setText(temp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            MainActivity.showAbout(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
