package com.example.user.findplacesnearfinal.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.helpClasses.CallToRetrofit;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class SearchFragment extends Fragment implements LocationListener {

    private final int PERMISSION_REQUEST_CODE = 9;

    private LocationManager locationManager;
    public static Location lastKnownLocation;
    private String lastKnowStringLoc;
    private boolean searchWithLocationAPI = false;

    View myView;
    RecyclerView recyclerView;
    Button locationBtn;
    EditText searchTXT;
    SeekBar seekBar;
    int progressBarValue;
    TextView seekBarProgressText;

    private Boolean isPrefUseKm;

    LinearLayout cityLayout;

    private ArrayList<PlacesDB> allPlaces;
    private MyRecyclerAdapter adapter;

    // Required empty public constructor
    public SearchFragment() {
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        //inflate the layout
        myView = inflater.inflate(R.layout.search_fragment, container, false);

        SugarContext.init(getActivity());

        //initialization the LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        randomBackgroundPhoto();

        //get the user miles or Km pref
        getKmOrMilesFromPreference();

        //initialization the RecyclerView, the location button, seekBar
        recyclerView = myView.findViewById(R.id.myList_RV);
        locationBtn = myView.findViewById(R.id.locationChangeBtn);
        seekBar = myView.findViewById(R.id.mySeekBar_id);
        seekBarProgressText = myView.findViewById(R.id.progress_forseekbar_TV);
        seekBarProgressText.setTextSize(14f);

        //city or country layout
        cityLayout = myView.findViewById(R.id.city_layout);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Log.i("Progress", String.valueOf(progress));
                progressBarValue = progress+5 ; // 5 - 30

                seekBarProgressText.setTextSize(18f);

                seekBarProgressText.setText(String.valueOf(progressBarValue));


            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                seekBarProgressText.setTextSize(14f);
                seekBarProgressText.setText(String.valueOf(progressBarValue)+ " km");
            }
        });


//-------------------------------------------------------------------------------------------------------------------

        /**
         * check if i have permission to the location && GPS is on, the gps image will be green in the beginning
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            getLocation();
            seekBar.setVisibility(View.VISIBLE);
            cityLayout.setVisibility(View.INVISIBLE);
            seekBarProgressText.setVisibility(View.VISIBLE);
            searchWithLocationAPI = true;
            locationBtn.setBackgroundResource(R.drawable.location_green);

        } else {

            isGpsEnable();
            checkLocationPermission();
        }

//--------------------------------------------------------------------------------------------------------

        try{

            getLocation();

        }catch (Exception ee){

            ee.printStackTrace();
        }

        setRecyclerFromDB();

//--------------------------------------------------------------------------------------------------------

        /**
         *  user click on locationBtn to change it
         */
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if gps image is ON (R.drawable.location_green) --> the locationButton change to OFF
                if (locationBtn.getBackground().getConstantState()
                        == getResources().getDrawable(R.drawable.location_green)
                        .getConstantState()) {

                    locationBtn.setBackgroundResource(R.drawable.not_location);
                    seekBar.setVisibility(View.INVISIBLE);
                    cityLayout.setVisibility(View.VISIBLE);
                    seekBarProgressText.setVisibility(View.INVISIBLE);

                    searchWithLocationAPI = false;

                    // if user wont to set the Gps btn to ON - gps need to be Enable and also the permission check are true
                } else if (locationBtn.getBackground().getConstantState()
                        == getResources().getDrawable(R.drawable.not_location)
                        .getConstantState()
                        && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    getLocation();
                    locationBtn.setBackgroundResource(R.drawable.location_green);
                    seekBar.setVisibility(View.VISIBLE);
                    cityLayout.setVisibility(View.INVISIBLE);
                    seekBarProgressText.setVisibility(View.VISIBLE);
                    searchWithLocationAPI = true;

                } else {

                    checkLocationPermission();
                    isGpsEnable();
                }
            }
        });

//--------------------------------------------------------------------------------------------------------------------

        /**
         * when the user click on the search button, start retrofit connection
         * Depends if the location service is on or off
         */
        ImageView searchImage = myView.findViewById(R.id.search_image);
        searchImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(getActivity().getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }

                // take the text that the user enter to EditText
                String userText = getUserText();

                if(lastKnowStringLoc != null){

                    //call retrofit and update recyclerView
                    CallToRetrofit retrofitCall = new CallToRetrofit(getActivity(), userText, progressBarValue,
                            lastKnowStringLoc, searchWithLocationAPI,
                            lastKnownLocation, recyclerView, adapter);
                    retrofitCall.startCallRetrofitApi();

                }else {

                    checkLocationPermission();
                    CallToRetrofit retrofitCall = new CallToRetrofit(getActivity(), userText, progressBarValue,
                            lastKnowStringLoc, searchWithLocationAPI,
                            lastKnownLocation, recyclerView, adapter);
                    retrofitCall.startCallRetrofitApi();
                }
            }

        });

        return myView;
    }

//*************************************************************************************************************

    //background image on random

    private void randomBackgroundPhoto() {

        int[] photoRes = new int[4];
        photoRes[0] = R.drawable.hamburger;
        photoRes[1] = R.drawable.fish;
        photoRes[2] = R.drawable.pasta;
        photoRes[3] = R.drawable.drinktoapp;

        Random random = new Random();
        int x = random.nextInt(4);

        RelativeLayout myImgView = myView.findViewById(R.id.backgroundImage);
        myImgView.setBackgroundResource(photoRes[x]);
    }

//*******************************************************************************************************************

    /**
     * set the RecyclerView From DataBase
     */
    private void setRecyclerFromDB() {

        LatLng latLng = null;

        if(lastKnownLocation != null) {

            latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        }else {

            // no location found
            latLng = new LatLng(0, 0);

        }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            //setting adapter
            adapter = new MyRecyclerAdapter(getActivity(), latLng);
            recyclerView.setAdapter(adapter);
    }

//------------------------------------------------------------------------------------------------------------------

    //get user enter text to search
    private String getUserText() {

        searchTXT = myView.findViewById(R.id.searchtext_ET);
        EditText cityName = myView.findViewById(R.id.cityName);

        String name = "";

        if(searchWithLocationAPI){

            return searchTXT.getText().toString();

        }else if(cityName.getText().toString().equals("")){

            Toast.makeText(getActivity(), "Enter City or Country", Toast.LENGTH_SHORT).show();

            return name;

        }else {

            name = searchTXT.getText().toString() + " " + cityName.getText().toString();
        }

        return name;
    }

    /**
     * OnResume && OnPause
      */

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            getLocation();
            locationBtn.setBackgroundResource(R.drawable.location_green);
            seekBar.setVisibility(View.VISIBLE);
            seekBarProgressText.setVisibility(View.VISIBLE);
            cityLayout.setVisibility(View.INVISIBLE);
            searchWithLocationAPI = true;

        } else {
            locationBtn.setBackgroundResource(R.drawable.not_location);
            seekBar.setVisibility(View.INVISIBLE);
            seekBarProgressText.setVisibility(View.INVISIBLE);
            searchWithLocationAPI = false;
            cityLayout.setVisibility(View.VISIBLE);
        }

//        setRecyclerFromDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }

//------------------------------------------------------------------------------------------------------------

    /**
     * implement methods From LocationListener Object
     */
    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {

        } else {
            lastKnowStringLoc = location.getLatitude() + "," + location.getLongitude();
            Log.i("LOC", "lat: " + location.getLatitude() + " lon:" + location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            getLocation();
            locationBtn.setBackgroundResource(R.drawable.location_green);
            seekBar.setVisibility(View.VISIBLE);
            seekBarProgressText.setVisibility(View.VISIBLE);
            cityLayout.setVisibility(View.INVISIBLE);
            searchWithLocationAPI = true;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        locationBtn.setBackgroundResource(R.drawable.not_location);
        seekBar.setVisibility(View.INVISIBLE);
        seekBarProgressText.setVisibility(View.INVISIBLE);
        cityLayout.setVisibility(View.VISIBLE);
        searchWithLocationAPI = false;
    }

//---------------------------------------------------------------------------------------------------------------------------------

    /**
     * get current location method from GPS || Network
     */
    @SuppressLint("MissingPermission")
    private void getLocation() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation == null) {
            lastKnownLocation = ((LocationManager) getActivity().getSystemService(LOCATION_SERVICE)).getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }

        if(lastKnownLocation != null){
            lastKnowStringLoc = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();

        }

        if(lastKnownLocation == null){
            Toast.makeText(getActivity(), "Enable your GPS if you wont to use location service", Toast.LENGTH_SHORT).show();
        }
    }

//-------------------------------------------------------------------------------------------------------------------------

    /**
     * location Permission
     */
    public boolean checkLocationPermission() {

        // if i don't have permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);

            return false;

        } else {

            getLocation();
            return true;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST_CODE: {

                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    {
                        // if user have GPS provider Enable
                        if(isGpsEnable()){

                            getLocation();
                        }
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

//-----------------------------------------------------------------------------------------------------------------

    /**
     * check if gps enable, if not show alert dialog and intent to open GPS
     */
    private boolean isGpsEnable() {

        // if i don't have gps
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // ask for gps and sent to it by intent
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. you need to enable it if you wont to search something near to your current location")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    getActivity().startActivity(callGPSSettingIntent);
                                    dialog.cancel();
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            return false;
        }
        return true;
    }

//---------------------------------------------------------------------------------------------------------------------

    //get the value from shearPreference
    public void  getKmOrMilesFromPreference(){

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        //get value from SharedPrefs
        String showInList = sharedPreferences.getString("list_preference", "list");

        if(showInList.equals("Km")) {

            isPrefUseKm = true;
        }
        else if(showInList.equals("Miles"))
        {
            isPrefUseKm = false;

        }else {

            isPrefUseKm = true;
        }

    }

}
