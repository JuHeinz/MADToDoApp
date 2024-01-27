package org.julheinz.entities;



/**
 * An (attempted) login
 */
public class LoginEntity {


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
        this.enteredEmail = enteredEmail;
    }


    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }

    private String enteredPassword;
    private String enteredEmail;


}
