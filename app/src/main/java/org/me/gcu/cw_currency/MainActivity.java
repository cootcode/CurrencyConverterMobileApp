package org.me.gcu.cw_currency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView statusText;
    private TextView lastUpdateText;
    private EditText searchField;
    private TextWatcher searchTextWatcher;

    private List<CurrencyRate> allCurrencies;
    private List<CurrencyRate> filteredCurrencies;
    private CurrencyAdapter adapter;

    private String lastBuildDate = "";

    private ExecutorService executorService;
    private Handler mainHandler;
    private Handler autoUpdateHandler;
    private Runnable autoUpdateRunnable;

    private static final long AUTO_UPDATE_INTERVAL = 3600000; // AUTOUPDATE EVERY 1 hour (can reduce for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize executor and handlers
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        autoUpdateHandler = new Handler(Looper.getMainLooper());

        // Initialize views
        statusText = findViewById(R.id.statusText);
        lastUpdateText = findViewById(R.id.lastUpdateText);
        Button refreshButton = findViewById(R.id.refreshButton);
        Button showMainButton = findViewById(R.id.showMainButton);
        Button showAllButton = findViewById(R.id.showAllButton);
        searchField = findViewById(R.id.searchField);
        ListView currencyListView = findViewById(R.id.currencyListView);

        // Initialize data structures
        allCurrencies = new ArrayList<>();
        filteredCurrencies = new ArrayList<>();
        adapter = new CurrencyAdapter(this, filteredCurrencies);
        currencyListView.setAdapter(adapter);

        // Set up button listeners
        refreshButton.setOnClickListener(v -> fetchData());
        showMainButton.setOnClickListener(v -> showMainCurrencies());
        showAllButton.setOnClickListener(v -> showAllCurrencies());

        // Set up search functionality
        searchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCurrencies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        searchField.addTextChangedListener(searchTextWatcher);

        // Set up list item click listener
        currencyListView.setOnItemClickListener((parent, view, position, id) -> {
            CurrencyRate selectedCurrency = filteredCurrencies.get(position);
            if (!selectedCurrency.isRateAvailable()) {
                Toast.makeText(this,
                        "Exchange rate unavailable for " + selectedCurrency.getCurrencyCode(),
                        Toast.LENGTH_LONG).show();
                return;
            }
            openCurrencyConverter(selectedCurrency);
        });

        // Set up auto-update
        setupAutoUpdate();

        // Fetch data on startup
        fetchData();
    }

    private void setupAutoUpdate() {
        autoUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                fetchData();
                autoUpdateHandler.postDelayed(this, AUTO_UPDATE_INTERVAL);
            }
        };
        autoUpdateHandler.postDelayed(autoUpdateRunnable, AUTO_UPDATE_INTERVAL);
    }

    private void fetchData() {
        statusText.setText(R.string.status_fetching);  // Instead of "Fetching data..."
        executorService.execute(new DataFetchTask());
    }

    private class DataFetchTask implements Runnable {
        @Override
        public void run() {
            try {
                // Fetch data from URL
                String urlSource = "https://www.fx-exchange.com/gbp/rss.xml";
                URL url = new URL(urlSource);
                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Clean up XML
                String xmlData = result.toString();
                int startIndex = xmlData.indexOf("<?");
                int endIndex = xmlData.indexOf("</rss>") + 6;
                xmlData = xmlData.substring(startIndex, endIndex);

                // Parse XML
                List<CurrencyRate> parsedCurrencies = parseXML(xmlData);

                // Update UI on main thread
                mainHandler.post(() -> {
                    allCurrencies.clear();
                    allCurrencies.addAll(parsedCurrencies);
                    showAllCurrencies();
                    statusText.setText(R.string.status_loaded);
                    lastUpdateText.setText(getString(R.string.last_update, lastBuildDate));
                    Toast.makeText(MainActivity.this,
                            getString(R.string.currencies_loaded, allCurrencies.size()),
                            Toast.LENGTH_SHORT).show();
                });

            } catch (IOException e) {
                Log.e("FetchData", "Error fetching data", e);
                mainHandler.post(() -> {
                    statusText.setText(R.string.status_error);
                    Toast.makeText(MainActivity.this,
                            getString(R.string.network_error, e.getMessage()),
                            Toast.LENGTH_LONG).show();
                });
            }
        }
    }

    private List<CurrencyRate> parseXML(String xmlData) {
        List<CurrencyRate> currencies = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlData));

            int eventType = parser.getEventType();
            String currentTag = "";
            String title = "";
            String description = "";
            String link = "";
            String pubDate = "";
            boolean inItem = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTag = parser.getName();
                        if (currentTag.equals("item")) {
                            inItem = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText().trim();
                        if (!text.isEmpty()) {
                            if (currentTag.equals("lastBuildDate") && !inItem) {
                                lastBuildDate = text;
                            } else if (inItem) {
                                switch (currentTag) {
                                    case "title":
                                        title = text;
                                        break;
                                    case "description":
                                        description = text;
                                        break;
                                    case "link":
                                        link = text;
                                        break;
                                    case "pubDate":
                                        pubDate = text;
                                        break;
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item") && inItem) {
                            // Parse the item data
                            CurrencyRate currency = parseCurrencyItem(
                                    title, description, link, pubDate);
                            if (currency != null) {
                                currencies.add(currency);
                            }
                            // Reset for next item
                            title = "";
                            description = "";
                            link = "";
                            pubDate = "";
                            inItem = false;
                        }
                        currentTag = "";
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            Log.e("ParseXML", "Error parsing XML", e);
        }

        return currencies;
    }

    private CurrencyRate parseCurrencyItem(String title, String description,
                                           String link, String pubDate) {
        try {
            // Parse title: "British Pound Sterling(GBP)/United States Dollar(USD)"
            String[] titleParts = title.split("/");
            if (titleParts.length < 2) return null;

            String secondCurrency = titleParts[1];

            // Extract currency name and code
            int openParen = secondCurrency.indexOf("(");
            int closeParen = secondCurrency.indexOf(")");

            if (openParen == -1 || closeParen == -1) return null;

            String currencyName = secondCurrency.substring(0, openParen).trim();
            String currencyCode = secondCurrency.substring(openParen + 1, closeParen);

            // Extract country name (handle cases like "United States" or "Chinese")
            String countryName = extractCountryName(currencyName);

            // Parse description: "1 British Pound Sterling = 1.3456 United States Dollar"
            String[] descParts = description.split("=");
            if (descParts.length < 2) return null;

            // Grab the right-hand side (e.g., " 0.00003125 Bitcoin")
            String rhs = descParts[1];

            // Sanitize and extract the first numeric token
            String numeric = extractFirstNumber(rhs);
            if (numeric == null) {
                Log.e("ParseItem", "Could not extract numeric rate from: " + description);
                return null;
            }

            // Use BigDecimal to avoid precision loss on tiny values
            BigDecimal bd = new BigDecimal(numeric);
            double rate = bd.doubleValue();

            return new CurrencyRate(currencyName, currencyCode, countryName,
                    rate, link, pubDate);

        } catch (Exception e) {
            Log.e("ParseItem", "Error parsing currency item", e);
            return null;
        }
    }

    private String extractCountryName(String currencyName) {
        String lower = currencyName.toLowerCase(Locale.ROOT);

        if (lower.contains("british")) return "United Kingdom";
        if (lower.contains("english")) return "United Kingdom";
        if (lower.contains("scottish")) return "United Kingdom";
        if (lower.contains("welsh")) return "United Kingdom";
        if (lower.contains("american") || lower.contains("united states")) return "United States";
        if (lower.contains("us dollar")) return "United States";
        if (lower.contains("european") || lower.equals("euro")) return "European Union";
        if (lower.contains("chinese")) return "China";
        if (lower.contains("japanese")) return "Japan";
        if (lower.contains("australian")) return "Australia";
        if (lower.contains("canadian")) return "Canada";
        if (lower.contains("swiss")) return "Switzerland";
        if (lower.contains("indian")) return "India";
        if (lower.contains("russian")) return "Russia";
        if (lower.contains("saudi")) return "Saudi Arabia";
        if (lower.contains("south african")) return "South Africa";
        if (lower.contains("singapore")) return "Singapore";
        if (lower.contains("hong kong")) return "Hong Kong";
        if (lower.contains("new zealand")) return "New Zealand";
        if (lower.contains("mexican")) return "Mexico";
        if (lower.contains("brazilian")) return "Brazil";
        if (lower.contains("turkish")) return "Turkey";
        if (lower.contains("thai")) return "Thailand";
        if (lower.contains("norwegian")) return "Norway";
        if (lower.contains("swedish")) return "Sweden";
        if (lower.contains("danish")) return "Denmark";
        if (lower.contains("korean")) return "South Korea";
        if (lower.contains("emirati") || lower.contains("emirates") || lower.contains("dirham"))
            return "United Arab Emirates";
        if (lower.contains("qatari")) return "Qatar";
        if (lower.contains("kuwaiti")) return "Kuwait";
        if (lower.contains("egyptian")) return "Egypt";
        if (lower.contains("pakistani")) return "Pakistan";
        if (lower.contains("bangladeshi")) return "Bangladesh";
        if (lower.contains("sri lanka")) return "Sri Lanka";
        if (lower.contains("nepalese") || lower.contains("nepali")) return "Nepal";
        if (lower.contains("indonesian")) return "Indonesia";
        if (lower.contains("philippine")) return "Philippines";
        if (lower.contains("malaysian")) return "Malaysia";
        if (lower.contains("vietnamese")) return "Vietnam";
        if (lower.contains("argentine")) return "Argentina";
        if (lower.contains("chilean")) return "Chile";
        if (lower.contains("colombian")) return "Colombia";
        if (lower.contains("peruvian")) return "Peru";
        if (lower.contains("uruguayan")) return "Uruguay";
        if (lower.contains("paraguayan")) return "Paraguay";
        if (lower.contains("bolivian")) return "Bolivia";
        if (lower.contains("polish")) return "Poland";
        if (lower.contains("czech")) return "Czech Republic";
        if (lower.contains("slovak")) return "Slovakia";
        if (lower.contains("hungarian")) return "Hungary";
        if (lower.contains("romanian")) return "Romania";
        if (lower.contains("bulgarian")) return "Bulgaria";
        if (lower.contains("croatian")) return "Croatia";
        if (lower.contains("serbian")) return "Serbia";
        if (lower.contains("icelandic")) return "Iceland";

        // Fallback: first token capitalized (better than nothing)
        String[] words = currencyName.split("\\s+");
        if (words.length > 0) {
            return capitalize(words[0]);
        }
        return currencyName;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT);
    }

    private void showMainCurrencies() {
        // Check if data is loaded
        if (allCurrencies.isEmpty()) {
            Toast.makeText(this,
                    R.string.please_wait_loading,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        filteredCurrencies.clear();

        for (CurrencyRate currency : allCurrencies) {
            String code = currency.getCurrencyCode();
            if (code.equals("USD") || code.equals("EUR") || code.equals("JPY")) {
                filteredCurrencies.add(currency);
                Log.d("MainActivity", "Added main currency: " + code);
            }
        }

        // Notify adapter FIRST, before clearing search
        adapter.notifyDataSetChanged();

        // Clear search field WITHOUT triggering the TextWatcher
        searchField.removeTextChangedListener(searchTextWatcher);
        searchField.setText("");
        searchField.addTextChangedListener(searchTextWatcher);

        // Show feedback to user
        Toast.makeText(this,
                getString(R.string.showing_main_currencies, filteredCurrencies.size()),
                Toast.LENGTH_SHORT).show();

        Log.d("MainActivity", "Main currencies filter applied. Showing: " + filteredCurrencies.size());
    }

    private void showAllCurrencies() {
        filteredCurrencies.clear();
        filteredCurrencies.addAll(allCurrencies);

        // Notify adapter FIRST
        adapter.notifyDataSetChanged();

        // Clear search field WITHOUT triggering the TextWatcher
        searchField.removeTextChangedListener(searchTextWatcher);
        searchField.setText("");
        searchField.addTextChangedListener(searchTextWatcher);
    }

    private void filterCurrencies(String query) {
        filteredCurrencies.clear();

        if (query.isEmpty()) {
            filteredCurrencies.addAll(allCurrencies);
        } else {
            String lowerQuery = query.toLowerCase();
            for (CurrencyRate currency : allCurrencies) {
                if (currency.getCurrencyName().toLowerCase().contains(lowerQuery) ||
                        currency.getCurrencyCode().toLowerCase().contains(lowerQuery) ||
                        currency.getCountryName().toLowerCase().contains(lowerQuery)) {
                    filteredCurrencies.add(currency);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void openCurrencyConverter(CurrencyRate currency) {
        Intent intent = new Intent(this, CurrencyConverterActivity.class);
        intent.putExtra("CURRENCY_NAME", currency.getCurrencyName());
        intent.putExtra("CURRENCY_CODE", currency.getCurrencyCode());
        intent.putExtra("EXCHANGE_RATE", currency.getRate());
        Log.d("ConverterLaunch", "Sending -> " + currency.getCurrencyCode() + " rate=" + currency.getRate());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (autoUpdateHandler != null && autoUpdateRunnable != null) {
            autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
        }
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

    public static void showAbout(Context ctx) {
        TextView msg = new TextView(ctx);
        msg.setText(Html.fromHtml(ctx.getString(R.string.about_message)));
        msg.setMovementMethod(LinkMovementMethod.getInstance());
        int pad = (int) (16 * ctx.getResources().getDisplayMetrics().density);
        msg.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(ctx)
                .setTitle(R.string.about_title)
                .setView(msg)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private static String extractFirstNumber(String text) {
        if (text == null) return null;

        // Normalize: remove NBSP, thin spaces, commas as thousand separators
        String cleaned = text
                .replace('\u00A0', ' ')  // NBSP
                .replace('\u2009', ' ')  // thin space
                .replace(",", " ")
                .trim();

        // Find first number like 123 or 123.456
        Matcher m = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)").matcher(cleaned);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
