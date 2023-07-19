package edu.cuhk.csci3310.travelspot.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.List;

import edu.cuhk.csci3310.travelspot.R;
import edu.cuhk.csci3310.travelspot.manager.GeofenceManager;
import edu.cuhk.csci3310.travelspot.ui.spotdetail.SpotDetailActivity;
import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.model.Spot;
import edu.cuhk.csci3310.travelspot.ults.CommonUlts;

public class SpotListAdapter extends Adapter<SpotListAdapter.SpotViewHolder>{
    private Context context;
    private LayoutInflater mInflater;

    private List<Spot> mSpotList;
    private DatabaseManager DatabaseManager;
    private GeofenceManager geofenceManager;



    class SpotViewHolder extends RecyclerView.ViewHolder {
        ImageView SpotImageView;
        TextView SpotTitleView;
        TextView SpotLocationTextView;
        CheckBox SpotCheckBox;

        final SpotListAdapter mAdapter;

        public SpotViewHolder(View itemView, SpotListAdapter adapter) {
            super(itemView);
            SpotImageView = itemView.findViewById(R.id.image);
            SpotTitleView = itemView.findViewById(R.id.spotTitle);
            SpotLocationTextView = itemView.findViewById(R.id.spotLocation);
            SpotCheckBox = itemView.findViewById(R.id.spotCheckBox);

            this.mAdapter = adapter;

        }

    }


    public SpotListAdapter(Context context, DatabaseManager databaseManager, GeofenceManager geofenceManager){
        this.context = context;
        this.DatabaseManager = databaseManager;
        this.geofenceManager = geofenceManager;
        this.mInflater = LayoutInflater.from(context);
        this.mSpotList = DatabaseManager.getAllSpots();
    }

    public void filter(String query) {
        mSpotList.clear();
        if (query.isEmpty()) {
            mSpotList = DatabaseManager.getAllSpots();
        } else {
            List<Spot> tempSpotList = DatabaseManager.getAllSpots();
            for(Spot spot:tempSpotList) {
                if(spot.getTitle().toLowerCase().contains(query.toLowerCase())){
                    mSpotList.add(spot);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.spotlist_item, parent, false);
        return new SpotViewHolder(mItemView, this);
    }


    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        //int id = mPinIDList.get(position);
        final Spot spot = mSpotList.get(position);
        //switch (pins_color[id]) {
        switch (spot.getColor()) {
            case "red":
                holder.SpotImageView.setImageResource(R.drawable.pinred);
                break;
            case "blue":
                holder.SpotImageView.setImageResource(R.drawable.pinblue);
                break;
            case "yellow":
                holder.SpotImageView.setImageResource(R.drawable.pinyellow);
                break;
            default:
                holder.SpotImageView.setImageResource(R.drawable.pin);
                break;
        }

        holder.SpotTitleView.setText(spot.getTitle());
        holder.SpotLocationTextView.setText(spot.getLocation());

        if(spot.getFinish() == 0)
            holder.SpotCheckBox.setChecked(false);
        if(spot.getFinish() == 1)
            holder.SpotCheckBox.setChecked(true);
        holder.SpotCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spot.setFinish(1);
                    DatabaseManager.updateSpot(spot);
                    geofenceManager.removeGeofence(String.valueOf(spot.getId()));
                } else {
                    spot.setFinish(0);
                    DatabaseManager.updateSpot(spot);
                    geofenceManager.addGeofence(String.valueOf(spot.getId()), spot.getLatitude(), spot.getLongitude(), CommonUlts.GEOFENCE_RADIUS);
                }
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return mSpotList.size();
    }

    public Context getContext() {
        return context;
    }

    public void updateData(List<Spot> spotList) {
        mSpotList.clear();
        mSpotList.addAll(spotList);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        Spot spot = mSpotList.get(position);
        DatabaseManager.deleteSpot(spot.getId());
        geofenceManager.removeGeofence(String.valueOf(spot.getId()));
        mSpotList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position){
        Spot spot = mSpotList.get(position);
        Intent intent = new Intent(getContext(), SpotDetailActivity.class);
        intent.putExtra("markerLocation", spot.getLocation());
        intent.putExtra("markerLat", spot.getLatitude());
        intent.putExtra("markerLng", spot.getLongitude());
        intent.putExtra("title", spot.getTitle());
        intent.putExtra("color", spot.getColor());
        intent.putExtra("note", spot.getNote());
        intent.putExtra("spotID",spot.getId());
        intent.putExtra("finish",spot.getFinish());

        getContext().startActivity(intent);
    }
}
