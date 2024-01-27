package org.julheinz.entities;

import android.util.Log;


/**
 * An (attempted) login
 */
public class LoginEntity {

    private static final String LOG_TAG = LoginEntity.class.getSimpleName();

    public String getEnteredPassword() {
        if (enteredPassword == null) {
            return "";
        } else {
            return enteredPassword;
        }
    }

    public String getEnteredEmail() {
        if (enteredEmail == null) {
            return "";
        } else {
            return enteredEmail;
        }
    }

    public void setEnteredEmail(String enteredEmail) {
        Log.i(LOG_TAG, "Entered email:" + enteredEmail);
        this.enteredEmail = enteredEmail;
    }


    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }

    private String enteredPassword;
    private String enteredEmail;


}
