package com.example.user.findplacesnearfinal.helpClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.Adapters.MyRecyclerAdapter;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.example.user.findplacesnearfinal.Model.Place;
import com.example.user.findplacesnearfinal.Model.allResults;
import com.example.user.findplacesnearfinal.Service.GitHubService;
import com.example.user.findplacesnearfinal.remote.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *  objectClass that do the RetrofitCall and insert to Database (PlacesDB by SugarORM)
 */

public class CallToRetrofit {

    private String API_KEY = "AIzaSyBwpg6a0MQuMKzVTHlwzCmhTksktUCqHf8";

    allResults callResultsArray;

    Context context;
    String userTextToSearch;
    int progressBarDistance;
    String lastLocation;
    boolean ifLocationApi;

    Location lastKnowLocation;
    RecyclerView recyclerView;
    MyRecyclerAdapter Adapter;

    public CallToRetrofit(Context context, String userTextToSearch, int progressBarDistance, String lastLoc, boolean ifLocationApi,
                          Location Location, RecyclerView recyclerView, MyRecyclerAdapter adapter) {
        this.context = context;
        this.userTextToSearch = userTextToSearch;
        this.progressBarDistance = progressBarDistance;
        this.lastLocation = lastLoc;
        this.ifLocationApi = ifLocationApi;
        this.lastKnowLocation = Location;
        this.recyclerView = recyclerView;
        Adapter = adapter;
    }

    //----------------------------------------------------------------------------------------------------------------------------

    public boolean startCallRetrofitApi(){


        // Initialize a new instance of progress dialog
        ProgressDialog pd = ProgressDialog.show( context, null, null, false, true );
        // Set the progress dialog background color transparent
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.setContentView(R.layout.progressdialog_layout);


        // Finally, show the progress dialog
        pd.show();

        //connected to api service
        GitHubService apiService = RetrofitClient.getClient().create(GitHubService.class);

        Call<allResults> repos = null;

        //if user don't write nothing
        if (userTextToSearch.equals("")) {

            Toast.makeText(context, "oops... You forgot to enter something", Toast.LENGTH_SHORT).show();
            pd.dismiss();
            return false;

        }else {

            //if user wont to search by text || lastLocation = "" (don't have any location)
            if (!ifLocationApi || lastKnowLocation.equals("")) {

                repos = apiService.getTextSearchResults(userTextToSearch, API_KEY);

            // if user wont to search by near location && lastLocation != ""
            }else {

                String radius = String.valueOf(progressBarDistance * 1000);
                repos = apiService.getNearbyResults(lastLocation, radius, userTextToSearch, API_KEY);
            }

            // retrofit response
            repos.enqueue(new Callback<allResults>() {

                @Override
                public void onResponse(Call<allResults> call, Response<allResults> response) {

                    callResultsArray = response.body();

                    if (callResultsArray.getResults().isEmpty()) {

                        if(ifLocationApi){

                            //if i have zero results
                            Toast.makeText(context, "What you are looking for is not close to you, try to increase the distance", Toast.LENGTH_LONG).show();

                        }else {

                            //if i have zero results
                            Toast.makeText(context, "No Results", Toast.LENGTH_LONG).show();
                        }


                        //i have results in the array
                    } else {

                        insertTheResultsToPlacesDB();
                        setRecyclerAdapterFromDB();
                    }

                    //close progressBarRetrofit
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<allResults> call, Throwable t) {

                    Toast.makeText(context, "fail call, no open Internet connection", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                }

            });

            return true;
        }

    }

    // inserting the data to PlacesDB and checks Valid data
    private void insertTheResultsToPlacesDB(){

        PlacesDB.deleteAll(PlacesDB.class);

        for (int i = 0; i < callResultsArray.getResults().size(); i++) {

            Place place = callResultsArray.getResults().get(i);

            String photo = "";

            if (place.getPhotos() == null) {
                photo = "";
            } else {

                photo = place.getPhotos().get(0).getPhoto_reference();
            }

            String isOpen = null;

            if (place.getOpening_hours() == null) {

                isOpen = null;

            } else if (place.getOpening_hours() != null && place.getOpening_hours().isOpen_now() == true) {

                isOpen = "true";
            } else {
                isOpen = "false";
            }

            String placeAddress = "";
            if (place.getFormatted_address() != null) {

                placeAddress = place.getFormatted_address();
            } else if (place.getVicinity() != null) {

                placeAddress = place.getVicinity();
            }

            PlacesDB sugarPlaceTable = new PlacesDB(place.getGeometry().getLocation().getLat(),
                    place.getGeometry().getLocation().getLng(),
                    place.getIcon(), place.getName(), isOpen,
                    photo, place.getRating(),
                    placeAddress, false
            );

            sugarPlaceTable.save();
        }
    }

    public void setRecyclerAdapterFromDB(){

        LatLng latLng = null;

        if(lastKnowLocation != null){

            latLng = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());

        }else {

            //If don't have location
            latLng = new LatLng(0, 0);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //setting txt adapter
        Adapter = new MyRecyclerAdapter(context, latLng);
        recyclerView.setAdapter(Adapter);

    }
}
