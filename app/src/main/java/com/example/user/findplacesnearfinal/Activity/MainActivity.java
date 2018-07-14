package com.example.user.findplacesnearfinal.Activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.user.findplacesnearfinal.SugarDataBase.FavorietsDB;
import com.example.user.findplacesnearfinal.Fragments.FavoritesFragment;
import com.example.user.findplacesnearfinal.Fragments.InfoFragment;
import com.example.user.findplacesnearfinal.Fragments.SearchFragment;
import com.example.user.findplacesnearfinal.Fragments.MyPrefsFragment;
import com.example.user.findplacesnearfinal.R;
import com.example.user.findplacesnearfinal.Service.MyFragmentChanger;
import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.orm.SugarContext;


public class MainActivity extends AppCompatActivity implements MyFragmentChanger{

    FavoritesFragment favoritesFragment;
    SearchFragment searchFragment;
    InfoFragment infoFragment;
    Toolbar toolbar;

    private AdView mAdView;



    
    // use for john bryce final project - power connected
//    BroadcastReceiver connectedBroadcast;
//    BroadcastReceiver disconnectedBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add Ads with Ad library
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        //refresh sugar database
        SugarContext.init(this);

        //connected to Broadcast to listen the mobile Power
//        connectedOrDisConnectedPowerChanging();

        //settings for UI
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //set up the custom tool bar
        setToolBar();

        //initialize the fragment
        favoritesFragment = new FavoritesFragment();

        //set up the screen
        screenPositionOrder();

    }

//*******************************************************************************************************

//    /**
//     * connected to broadcast to check the phone power on/off
//     */
//    private void connectedOrDisConnectedPowerChanging() {
//
//        connectedBroadcast = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                Toast.makeText(context, "Power Connected", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        registerReceiver(connectedBroadcast, new IntentFilter(Intent.ACTION_POWER_CONNECTED) );
//
//        disconnectedBroadcast = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                Toast.makeText(context, "Power Disconnected", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        registerReceiver(disconnectedBroadcast, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED) );
//    }


//-------------------------------------------------------------------------------------------------

    /**
     * method that Initializing the layouts by the device orientation and if its mobile or tablet.
     *
     * JUST PORTRAIT
     */

    public void screenPositionOrder() {

        searchFragment = new SearchFragment();
        infoFragment = new InfoFragment();

//        //if its mobile and portrait
//        if (!isTablet(this) && isPortrait()) {

            getFragmentManager().beginTransaction().replace(R.id.main_portrait_layout, searchFragment).commit();

//        }else if(!isPortrait()){

//            getFragmentManager().beginTransaction().replace(R.id.search_tablet_layout, searchFragment).commit();
//            getFragmentManager().beginTransaction().replace(R.id.tablet_map_layout, infoFragment)
//                    .addToBackStack(null).commit();
//
//        } else if (isTablet(this)) {
//
//            getFragmentManager().beginTransaction().replace(R.id.search_tablet_layout, searchFragment).commit();
//            getFragmentManager().beginTransaction().replace(R.id.tablet_map_layout, infoFragment)
//                    .addToBackStack(null).commit();
//
//        }
    }

    /**
     * Checks if the device is a tablet (7" or greater).
     */
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * checks the orientation of the screen.
     */
    private boolean isPortrait() {

        // landscape checker
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            return false;
        }
        // else is portrait
        return true;
    }

//-------------------------------------------------------------------------------------------------

    /**
     * menu && toolBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.favorite_popup:

                changeToFavoritesFragment();
                break;

            case R.id.closeApp:

                finish();
                break;

            case R.id.showSettings:
                getFragmentManager().beginTransaction().replace(R.id.main_portrait_layout, new MyPrefsFragment())
                                    .addToBackStack(null).commit();
                break;
            case R.id.delete_favorites:
                FavorietsDB.deleteAll(FavorietsDB.class);
                break;
        }
        return true;
    }

    private void setToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolBar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

//--------------------------------------------------------------------------------------------------------------

    /**
     *  the implementation of FragmentChanger interface
     */
    @Override
    public void changeFragments(final PlacesDB place) {

        infoFragment = new InfoFragment(place);

        if(isPortrait() && !isTablet(this)) {

            getFragmentManager().beginTransaction().replace(R.id.main_portrait_layout, infoFragment)
                    .addToBackStack(null).commit();

        }else if(isTablet(this) || !isPortrait()) {

            getFragmentManager().beginTransaction().replace(R.id.tablet_map_layout, infoFragment)
                    .addToBackStack(null).commit();

        }
    }

    @Override
    public void changeToFavoritesFragment() {

        getFragmentManager().beginTransaction().replace(R.id.main_portrait_layout, favoritesFragment)
                .addToBackStack(null).commit();
    }

//--------------------------------------------------------------------------------------------------

    /**
     * Settings os the screen onStop() && onBackPressed()
     */
    @Override
    protected void onStop() {
        super.onStop();

        // close receiver
        try{
//            unregisterReceiver(connectedBroadcast);
//            unregisterReceiver(disconnectedBroadcast);
        }catch (Exception ee){

            ee.printStackTrace();
        }

    }

    @Override
    public void onBackPressed(){

        if(getFragmentManager().getBackStackEntryCount() == 1){
            getFragmentManager().popBackStackImmediate();
        }
        else if(getFragmentManager().getBackStackEntryCount() == 2){

            getFragmentManager().beginTransaction().replace(R.id.main_portrait_layout, searchFragment).commit();

        }else {

            finish();
        }
    }
}