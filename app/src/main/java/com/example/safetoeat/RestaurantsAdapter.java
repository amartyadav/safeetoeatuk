package com.example.safetoeat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    Context context;
    String[] name;
    String[] address;
    String[] rating;
    String[] localAuthorityName;
    String[] localAuthorityEmail;
    String[] localAuthorityWebsite;

    public RestaurantsAdapter(Context context, String[] name, String[] address, String[] rating, String[] localAuthorityName, String[] localAuthorityEmail, String[] localAuthorityWebsite) {
        this.context = context;

        this.name = name;
        this.address = address;
        this.rating = rating;
        this.localAuthorityName = localAuthorityName;
        this.localAuthorityEmail = localAuthorityEmail;
        this.localAuthorityWebsite = localAuthorityWebsite;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflating the custom layout
        View restaurantItemView = inflater.inflate(R.layout.rv_restaurants_item, parent, false);

        // returning a new holder instance
        ViewHolder viewHolder = new ViewHolder(restaurantItemView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        holder.tv_restaurantName.setText(name[position]);
        holder.tv_restaurantRating.setText(rating[position]);
        holder.tv_restaurantAddress.setText(address[position]);
        holder.tv_localAuthorityName.setText(localAuthorityName[position]);
        holder.tv_localAuthorityEmail.setText(localAuthorityEmail[position]);
        holder.tv_localAuthorityWebsite.setText(localAuthorityWebsite[position]);
    }



    @Override
    public int getItemCount() {
        return name.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_restaurantName;
        public TextView tv_restaurantAddress;
        public TextView tv_restaurantRating;
        public TextView tv_localAuthorityName;
        public TextView tv_localAuthorityWebsite;
        public TextView tv_localAuthorityEmail;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_restaurantName = (TextView) itemView.findViewById(R.id.tv_restaurantNameItem);
            tv_restaurantAddress = (TextView) itemView.findViewById(R.id.tv_restaurantAddressItem);
            tv_restaurantRating = (TextView) itemView.findViewById(R.id.tv_restaurantRatingItem);
            tv_localAuthorityName = (TextView) itemView.findViewById(R.id.tv_restaurantLocalAuthorityNameItem);
            tv_localAuthorityWebsite = (TextView) itemView.findViewById(R.id.tv_restaurantLocalAuthorityWebsiteItem);
            tv_localAuthorityEmail = (TextView) itemView.findViewById(R.id.tv_restaurantLocalAuthorityEmailItem);
        }
    }
}
