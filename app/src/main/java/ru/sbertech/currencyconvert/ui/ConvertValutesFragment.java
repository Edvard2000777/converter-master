package ru.sbertech.currencyconvert.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ru.sbertech.currencyconvert.R;
import ru.sbertech.currencyconvert.repository.ValuteInfo;

public class ConvertValutesFragment extends TabFragment {

    private CustomSpinnerAdapter adapter;
    private EditText editText;
    private TextView textView;
    private Spinner sourceSpinner, finalSpinner;
    private List<ValuteInfo> valutes;


    private ValutesActivityViewModel viewModel;

    public static ConvertValutesFragment getInstance(Context context){
        Bundle bundle = new Bundle();
        ConvertValutesFragment fragment = new ConvertValutesFragment();
        fragment.setArguments(bundle);
        fragment.setTitle(context.getString(R.string.text_converter));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_convert_currency, container, false);
        adapter = new CustomSpinnerAdapter(getActivity());
        editText = view.findViewById(R.id.source_edit);
        textView = view.findViewById(R.id.final_edit);

        initSpinner();

        Button button = view.findViewById(R.id.btn_ok);
        button.setOnClickListener(v -> performConversion());
        return view;
    }

    private void initSpinner() {
        sourceSpinner = view.findViewById(R.id.source_spinner);
        finalSpinner = view.findViewById(R.id.final_spinner);
        sourceSpinner.setAdapter(adapter);
        finalSpinner.setAdapter(adapter);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(ValutesActivityViewModel.class);
        loadData();
    }

    private void loadData() {
        viewModel.getListValutes().observe(this, data -> setData(data));
    }
    private void setData(@NonNull List<ValuteInfo> data) {
        valutes = data;
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    private void performConversion() {
        int sourcePosition = sourceSpinner.getSelectedItemPosition();
        int finalPosition = finalSpinner.getSelectedItemPosition();
        double amount;
        try {
            amount = Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            textView.setText("Введите корректное число");
            return;
        }

        ValuteInfo fromValute = valutes.get(sourcePosition);
        ValuteInfo toValute = valutes.get(finalPosition);

        double result = convertCurrency(amount, fromValute, toValute);
        textView.setText(String.format("%.2f", result));
    }

    private double convertCurrency(double amount, ValuteInfo fromValute, ValuteInfo toValute) {
        double fromValueInBase = fromValute.getValue() / fromValute.getNominal();
        double toValueInBase = toValute.getValue() / toValute.getNominal();
        return amount * (fromValueInBase / toValueInBase);
    }

    @Override
    protected void setupFragmentComponent() {

    }
}