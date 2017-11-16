package com.myyg.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myyg.R;

/**
 *
 */
public class ConsumerFragment extends Fragment {
    private RecyclerView rv_consumer;

    public ConsumerFragment() {

    }

    public static ConsumerFragment newInstance() {
        ConsumerFragment fragment = new ConsumerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumer, container, false);
        return view;
    }
}
