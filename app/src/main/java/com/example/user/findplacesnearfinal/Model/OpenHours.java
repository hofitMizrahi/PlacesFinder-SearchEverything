package com.example.user.findplacesnearfinal.Model;

/**
 * Created by user on 20/02/2018.
 */

public class OpenHours {

    private boolean open_now;

    public OpenHours(boolean open_now) {
        this.open_now = open_now;
    }

    public boolean isOpen_now() {
        return open_now;
    }

}
