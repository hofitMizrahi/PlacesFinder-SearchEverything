package com.example.user.findplacesnearfinal.Service;

import com.example.user.findplacesnearfinal.SugarDataBase.PlacesDB;

/**
 * Created by user on 06/03/2018.
 */

public interface MyFragmentChanger {

    void changeFragments(PlacesDB place);

    void changeToFavoritesFragment();
}
