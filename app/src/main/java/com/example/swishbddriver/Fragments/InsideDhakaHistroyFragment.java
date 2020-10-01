package com.example.swishbddriver.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.swishbddriver.R;


public class InsideDhakaHistroyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inside_dhaka_histroy, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
    }
}