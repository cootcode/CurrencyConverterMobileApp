package org.me.gcu.cw_currency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CurrencyAdapter extends ArrayAdapter<CurrencyRate> {
    private final Context context;
    private final List<CurrencyRate> currencies;

    public CurrencyAdapter(@NonNull Context context, List<CurrencyRate> currencies) {
        super(context, 0, currencies);
        this.context = context;
        this.currencies = currencies;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.currency_list_item, parent, false);
        }

        CurrencyRate currency = currencies.get(position);

        ImageView flagIcon = convertView.findViewById(R.id.flagIcon);
        TextView currencyNameView = convertView.findViewById(R.id.currencyName);
        TextView currencyCodeView = convertView.findViewById(R.id.currencyCode);
        TextView rateView = convertView.findViewById(R.id.rate);

        // ----- Bind main texts -----
        currencyNameView.setText(currency.getCurrencyName());
        currencyCodeView.setText(currency.getCurrencyCode());
        rateView.setText(currency.getFormattedRate());

        convertView.setAlpha(currency.isRateAvailable() ? 1f : 0.6f);

        // ----- Flag icon + accessibility description -----
        String flagCode = CurrencyFlags.getFlagForCurrency(currency.getCurrencyCode());
        String country = currency.getCountryName();
        if (country == null || country.trim().isEmpty()) {
            // Fallback if country wasn’t parsed/available
            country = currency.getCurrencyName();
        }

        if (flagCode != null) {
            @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(
                    flagCode, "drawable", context.getPackageName());

            if (resId != 0) {
                flagIcon.setImageResource(resId);
                flagIcon.setVisibility(View.VISIBLE);
                flagIcon.setContentDescription(
                        context.getString(R.string.flag_content_desc, country));
            } else {
                // Drawable not found for mapping
                flagIcon.setVisibility(View.GONE);
                flagIcon.setContentDescription(
                        context.getString(R.string.flag_missing_desc, country));
            }
        } else {
            // No mapping exists
            flagIcon.setVisibility(View.GONE);
            flagIcon.setContentDescription(
                    context.getString(R.string.flag_missing_desc, country));
        }

        // ----- Row background colour by rate band -----
        convertView.setBackgroundColor(currency.getColorForRate());

        return convertView;
    }
}
