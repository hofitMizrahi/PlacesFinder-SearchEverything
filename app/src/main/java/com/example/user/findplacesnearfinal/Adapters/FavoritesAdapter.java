package com.example.user.findplacesnearfinal.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.findplacesnearfinal.Fragments.FavoritesFragment;
import com.example.user.findplacesnearfinal.Model.Geometry;
import com.example.user.findplacesnearfinal.Model.Location;
import com.example.user.findplacesnearfinal.Model.OpenHours;
import com.example.user.findplacesnearfinal.Model.Photo;
import com.example.user.findplacesnearfinal.Model.Place;
import com.example.user.findplacesnearfinal.Service.MyFragmentChanger;
import com.example.user.findplacesnearfinal.SugarDataBase.FavorietsDB;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.myViewHolder> {

    Context context;
    ArrayList<FavorietsDB> allFavoriets;

    public FavoritesAdapter(Context context) {
        this.context = context;
        allFavoriets = (ArrayList<FavorietsDB>) FavorietsDB.listAll(FavorietsDB.class);

    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.single_favoriet_item, null);
        myViewHolder viewHolder = new myViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        FavorietsDB myFavorites = allFavoriets.get(position);
        holder.bindMyCityData(myFavorites);
    }

    @Override
    public int getItemCount() {
        return allFavoriets.size();
    }


    public class  myViewHolder extends RecyclerView.ViewHolder{

        View v;

        public myViewHolder(View itemView) {
            super(itemView);

            v = itemView;
        }


        public void bindMyCityData(FavorietsDB favorite) {

            ImageView favoriteImage = v.findViewById(R.id.favorite_IV);

            //check if there is any image resource in the Photo list array
            if (!favorite.getPhoto_reference().equals("")) {

                String reference = favorite.getPhoto_reference();

                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + reference + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";
                Picasso.with(context)
                        .load(url)
                        .resize(90, 90)
                        .centerCrop()
                        .into(favoriteImage);
            }

            ((TextView) v.findViewById(R.id.title_favorites_TV)).setText(favorite.getName());

//************************************************************************************************************

            // delete the favorite that clicked

            v.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Do You Wont To Delete This Item?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                            FavorietsDB favor = FavorietsDB.findById(FavorietsDB.class, favorite.getId());
                            favor.delete();
                            int position = allFavoriets.indexOf(favorite);
                            notifyItemRemoved(position);
                            allFavoriets.remove(favorite);

                        }
                    });

                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();

                    return true;
                }
            });


            v.setOnClickListener((View v)->{

                List<Photo> myList = new ArrayList<Photo>();
                myList.add(new Photo(favorite.getPhoto_reference()));

                PlacesDB place = new PlacesDB(favorite.getLat(), favorite.getLng(), favorite.getIcon(), favorite.getName(),
                                                favorite.getOpening_hours(), favorite.getPhoto_reference(),
                                                favorite.getRating(), favorite.getAddress(), true);

                //Replaces fragment to mapFragment and displays the location by the name of the place you clicked
                MyFragmentChanger cityChanger = (MyFragmentChanger) context;
                cityChanger.changeFragments(place);

            });
        }
    }
}
