package com.leon.counter_reading.base_items;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;


public abstract class BaseFragment extends Fragment {
    View view;
    Typeface typeface;
    Context context;

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = FragmentView(inflater, parent, savedInstanceState);
        context = getActivity();
        initialize();
        return view;
    }

    public abstract View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    public abstract void initialize();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
        typeface = null;
    }
}
