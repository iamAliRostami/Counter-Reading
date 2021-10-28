package com.leon.counter_reading.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    private ArrayList<MultiSelectionItem> multiSelectionItems = null;
    private boolean[] selection = null;
    private final ArrayAdapter adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);

//        adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
        adapter = new ArrayAdapter(context, R.layout.item_dropdown_menu);
        super.setAdapter(adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

//        adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
        adapter = new ArrayAdapter(context, R.layout.item_dropdown_menu);
        super.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;

            adapter.clear();
            adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

//    @Override
//    public void setPrompt(CharSequence prompt) {
//        super.setPrompt(prompt);
//
//    }

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] itemNames = new String[multiSelectionItems.size()];

        for (int i = 0; i < multiSelectionItems.size(); i++) {
            itemNames[i] = multiSelectionItems.get(i).getName();
            selection[i] = multiSelectionItems.get(i).getValue();
        }

        builder.setMultiChoiceItems(itemNames, selection, this);
        builder.setPositiveButton(MyApplication.getContext().getString(R.string.ok), (dialog, which) -> {

        });

        builder.show();

        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(ArrayList<MultiSelectionItem> multiSelectionItems) {
        this.multiSelectionItems = multiSelectionItems;
        selection = new boolean[this.multiSelectionItems.size()];
        adapter.clear();
        adapter.add("");
    }

    public void setSelection(ArrayList<MultiSelectionItem> selection) {
        Arrays.fill(this.selection, false);

        for (MultiSelectionItem sel : selection) {
            for (int j = 0; j < multiSelectionItems.size(); ++j) {
                if (multiSelectionItems.get(j).getValue().equals(sel.getValue())) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < multiSelectionItems.size(); ++i) {
            if (selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }

                foundOne = true;

                sb.append(multiSelectionItems.get(i).getName());
            }
        }

        return sb.toString();
    }

    public ArrayList<MultiSelectionItem> getSelectedItems() {
        ArrayList<MultiSelectionItem> selectedMultiSelectionItems = new ArrayList<>();

        for (int i = 0; i < multiSelectionItems.size(); ++i) {
            if (selection[i]) {
                selectedMultiSelectionItems.add(multiSelectionItems.get(i));
            }
        }

        return selectedMultiSelectionItems;
    }
}