package edu.cuhk.csci3310.travelspot.ui.home;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import java.util.List;

import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.manager.GeofenceManager;
import edu.cuhk.csci3310.travelspot.model.Spot;
import edu.cuhk.csci3310.travelspot.R;

public class HomeFragment extends Fragment {
    private DatabaseManager databaseManager;
    private GeofenceManager geofenceManager;

    private RecyclerView mRecyclerView;
    private SpotListAdapter mAdapter;

    private Activity activity;
    private Context context;
    private View view;


    public HomeFragment() {
        // Required empty public constructor
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
        context = this.getContext();
        databaseManager = new DatabaseManager(this.getContext());
        geofenceManager = new GeofenceManager(this.getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mRecyclerView == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            search(view);
            mRecyclerView = view.findViewById(R.id.recyclerview);
            mAdapter = new SpotListAdapter(context, databaseManager, geofenceManager);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

            ItemTouchHelper itemTouchHelper = new
                    ItemTouchHelper(new RecyclerItemTouchHelper(mAdapter));
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }
        else {
            search(view);

        }
        return view;
    }

    public void search(View view){
        // Add a SearchView to the layout
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do nothing
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateData(databaseManager.getAllSpots());
        mAdapter.notifyDataSetChanged();
    }

}