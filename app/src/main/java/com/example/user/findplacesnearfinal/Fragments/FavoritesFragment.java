package com.example.user.findplacesnearfinal.Fragments;


import android.graphics.Canvas;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.findplacesnearfinal.Adapters.FavoritesAdapter;
import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.R;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class FavoritesFragment extends Fragment {

    RecyclerView favoritesRecyclerView;
    FavoritesAdapter adapter;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesRecyclerView = myView.findViewById(R.id.favoriet_RV);

        //setting txt adapter
        adapter = new FavoritesAdapter(getActivity());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        favoritesRecyclerView.setHasFixedSize(true);

        favoritesRecyclerView.setLayoutManager(layoutManager);
        favoritesRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged ();

        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

    }
}
