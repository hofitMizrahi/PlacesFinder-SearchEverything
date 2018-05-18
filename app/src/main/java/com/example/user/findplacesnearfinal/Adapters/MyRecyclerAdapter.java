package com.example.user.findplacesnearfinal.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.findplacesnearfinal.helpClasses.CalculateDistance;
import com.example.user.findplacesnearfinal.SugarDataBase.FavorietsDB;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.MyFragmentChanger;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarContext;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter <MyRecyclerAdapter.myViewHolder> {

    ArrayList<PlacesDB> placeArrayList;
    Context context;
    LatLng latLng;

    public MyRecyclerAdapter(Context context, LatLng latLng) {

        placeArrayList = (ArrayList<PlacesDB>) PlacesDB.listAll(PlacesDB.class);
        this.context = context;
        this.latLng = latLng;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_item_model, null);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myViewHolder singleItem, int position) {

        PlacesDB place = placeArrayList.get(position);
        singleItem.bindMyCityData(place);
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        View holderView;

        public myViewHolder(View itemView) {
            super(itemView);

            this.holderView = itemView;
        }

        @SuppressLint("ResourceType")
        public void bindMyCityData(final PlacesDB place) {

            // title
            TextView title = holderView.findViewById(R.id.title_TV);
            title.setText(place.getName());

//--------------------------------------------------------------------------------------------------

            // address
            TextView address = holderView.findViewById(R.id.address_TV);

            String placeAddress = place.getAddress();

            if (placeAddress == null) {

                placeAddress = "";
            }

            if (!placeAddress.contains(",")) {

                address.setText(placeAddress);

            } else {

                String[] parts = placeAddress.split(",", 2);
                String part1 = parts[0]; // address
                String part2 = parts[1]; // country

                address.setText(part1 + "\n" + part2.substring(1));
            }


//--------------------------------------------------------------------------------------------------

            // open or close - String
            TextView openOrCloseSing = holderView.findViewById(R.id.openOrCloseSing);

            //if don't have open hours data
            if (place.getOpening_hours() == null) {

                openOrCloseSing.setVisibility(View.INVISIBLE);

            } else if (place.getOpening_hours().equals("true")) {

                openOrCloseSing.setBackgroundResource(R.drawable.open_shape);
                openOrCloseSing.setText(R.string.open);

            } else {
                openOrCloseSing.setText(R.string.closed);
                openOrCloseSing.setBackgroundResource(R.drawable.close_shape);
            }


            //image resource
            ImageView imageView = holderView.findViewById(R.id.imageView);

            //check if there is any image resource in the Photo list array
            if (!place.getPhoto_reference().equals("")) {

                String reference = place.getPhoto_reference();


                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + reference + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
                Picasso.with(context)
                        .load(url)
                        .resize(90, 90)
                        .centerCrop()
                        .into(imageView);
            }

//-----------------------------------------------------------------------------------------------------------------------------------------------------------

            // km || miles
            TextView meters = holderView.findViewById(R.id.KM_TV);

            if(latLng.longitude == 0){

                meters.setVisibility(View.INVISIBLE);

            }else {

                LatLng endP = new LatLng(place.getLat(), place.getLng());
                CalculateDistance calculateDistance = new CalculateDistance();
                double distance = calculateDistance.getDistance(latLng, endP);

                String myDistance = String.valueOf(round(distance, 1));

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                //get value from SharedPrefs
                String showInList = sharedPreferences.getString("list_preference", "list");

                if (showInList.equals("Miles")) {

                    //miles calculator
                    distance = distance * 0.621371;
                    myDistance = String.valueOf(round(distance, 1));
                }

                meters.setVisibility(View.VISIBLE);
                meters.setText(myDistance);

            }



//-------------------------------------------------------------------------------------------------------------------------------------------

            holderView.setOnClickListener((View v) -> {

                //Replaces fragment to mapFragment and displays the location by the name of the place you clicked
                MyFragmentChanger cityChanger = (MyFragmentChanger) context;
                cityChanger.changeFragments(place);
            });

            holderView.setOnLongClickListener((View v) -> {


//                //creating a popup menu
//                PopupMenu popup = new PopupMenu(context, holderView);
//                //inflating menu from xml resource
//                popup.inflate(R.menu.item_popup_menu);
//                //adding click listener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.favorit_save:
//                                //
//                                FavorietsDB favorietsTableDB = new FavorietsDB(place.getLat(), place.getLng(), place.getIcon(),
//                                        place.getName(), place.isOpen(), place.getPhoto_reference(), place.getRating(), place.getAddress());
//                                favorietsTableDB.save();
//                                break;
//
//                            case R.id.share:
//
//                                String sendingText = "Want to go here? " + place.getName() + "\n" + "If so, click the following link to use Waze: ";
//                                String url = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll= " + place.getLat() + "," + place.getLng() + "&navigate=yes";
//
//
//                                Intent intent = new Intent(Intent.ACTION_SEND);
//                                intent.setType("text/plain");
//                                intent.putExtra(Intent.EXTRA_TEXT, url);
//                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, sendingText);
//                                context.startActivity(Intent.createChooser(intent, "Share"));
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                //displaying the popup
//                popup.show();

                Dialog customDialog
                        = new Dialog(context);

                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = layoutInflater.inflate(R.layout.dialog_recycleritem_clicked, null);
                customDialog.setContentView(dialogView);
                customDialog.show();

                TextView addFavoriets = dialogView.findViewById(R.id.add_favorites_dialog);
                addFavoriets.setOnClickListener((View view)->{

                    FavorietsDB favorietsTableDB = new FavorietsDB(place.getLat(), place.getLng(), place.getIcon(),
                            place.getName(), place.isOpen(), place.getPhoto_reference(), place.getRating(), place.getAddress());
                    favorietsTableDB.save();

                    customDialog.dismiss();
                });

                TextView share = dialogView.findViewById(R.id.share_dialog);
                share.setOnClickListener((View view)->{

                    String sendingText = "Want to go here? " + place.getName() + "\n" + "If so, click the following link to use Waze: ";
                    String url = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll= " + place.getLat() + "," + place.getLng() + "&navigate=yes";

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, url);
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, sendingText);
                    context.startActivity(Intent.createChooser(intent, "Share"));

                    customDialog.dismiss();
                });

                return true;
            });
        }

    }


    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
