package com.example.user.findplacesnearfinal.Fragments;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Activity.MainActivity;
import com.example.user.findplacesnearfinal.Model.Location;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.example.user.findplacesnearfinal.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import static com.example.user.findplacesnearfinal.Fragments.SearchFragment.lastKnownLocation;

/**
 * information fragment after user click on recyclerView item
 */
public class InfoFragment extends Fragment {

    View v;
    PlacesDB place;
    GoogleMap myGoogleMap;
    android.location.Location myLocation;

    public InfoFragment(){

        place = null;
    }

    @SuppressLint("ValidFragment")
    public InfoFragment(PlacesDB place) {
        // Required empty public constructor

        this.place = place;
        this.myLocation = lastKnownLocation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.map_and_info_layout, container, false);

        setMap();

//--------------------------------------------------------------------------------------------------

        if(place == null){

            Log.i("PlaceInfo", "place == null on info fragment");

        }else {

            //set title
            TextView name_info = v.findViewById(R.id.name_info);
            name_info.setText(place.getName());

//--------------------------------------------------------------------------------------------------

            // user click return to go back the main fragment
            ImageView returnIV = v.findViewById(R.id.returm_info_IV);
            returnIV.setOnClickListener((View view) -> {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            });

//--------------------------------------------------------------------------------------------------

            // open or close - String
            TextView openOrCloseText = v.findViewById(R.id.openorclose_info);
            ImageView dotOpenOrClose = v.findViewById(R.id.openorclose_dot_info);

            //if don't have open hours data
            if (place.getOpening_hours() == null) {

                openOrCloseText.setVisibility(View.INVISIBLE);
                dotOpenOrClose.setVisibility(View.INVISIBLE);

            } else if (place.getOpening_hours().equals("true")) {

                dotOpenOrClose.setBackgroundResource(R.drawable.open_small);
                openOrCloseText.setText(" open");

            } else {
                openOrCloseText.setText(" closed");
                dotOpenOrClose.setBackgroundResource(R.drawable.close_small);
            }

//*********************************************************************************************************

            // click on car image --> go to waze and start navigation
            ImageView carImage = v.findViewById(R.id.carImage_info);
            carImage.setOnClickListener((View view) -> {
                Toast.makeText(getActivity(), "car click", Toast.LENGTH_SHORT).show();

                try {
                    // Launch Waze to look for Hawaii:
                    String url = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll= " + place.getLat() + "," + place.getLng() + "&navigate=yes";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }
            });

//--------------------------------------------------------------------------------------------------

            ImageView walkModeToGoogle = v.findViewById(R.id.walk_info_IV);
            walkModeToGoogle.setOnClickListener((View view) -> {

                String uri = "http://maps.google.com/maps?saddr=" + myLocation.getLatitude() + "," + myLocation.getLongitude() + "&daddr="
                        + place.getLat() + "," + place.getLng() + "&mode=walking";

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            });

//--------------------------------------------------------------------------------------------------

            //set image resource for the place
            ImageView imageRes = v.findViewById(R.id.image_info);

            //check if there is any image resource in the Photo list array
            if (!place.getPhoto_reference().equals("")) {

                String reference = place.getPhoto_reference();

                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                        + reference
                        + "&key=AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";

                Picasso.with(getActivity())
                        .load(url)
                        .resize(90, 90)
                        .centerCrop()
                        .into(imageRes);
            }

//--------------------------------------------------------------------------------------------------

            ImageView shareImage = v.findViewById(R.id.share_info_IV);
            shareImage.setOnClickListener((View view) -> {

                String sendingText = "Want to go TO " + place.getName() + " with my?(:\n" + "If so, click the following link to use Waze: ";
                String url = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll= " + place.getLat() + "," + place.getLng() + "&navigate=yes";


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, sendingText);
                startActivity(Intent.createChooser(intent, "Share"));

            });

            ImageView locationImage = v.findViewById(R.id.location_info_IV);
            locationImage.setOnClickListener((View view) -> {

                LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                //update location and zoom 0 is the most far
                myGoogleMap.addMarker(new MarkerOptions().position(latLng).title("my location"));
                myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

            });

            //set address
            TextView addressInfo = v.findViewById(R.id.address_info);
            addressInfo.setText(place.getAddress());

            final SlidingUpPanelLayout slidingUpPanelLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
            slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            });

        }

        return v;
    }

//--------------------------------------------------------------------------------------------------

    private void setMap() {

        MapFragment mapFragment = new MapFragment();

        if(place == null){

        }else {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    myGoogleMap = googleMap;

                    myGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    LatLng latLng = new LatLng(place.getLat(), place.getLng());
                    //update location and zoom 0 is the most far
                    myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(place.getAddress()));
                    myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                }
            });
        }
        getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.map_layout, mapFragment).commit();
    }



    @Override
    public void onPause() {
        super.onPause();

    }
}
