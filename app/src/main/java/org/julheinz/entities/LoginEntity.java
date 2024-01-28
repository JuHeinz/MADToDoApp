package org.julheinz.entities;



/**
 * An (attempted) login
 */
public class LoginEntity {

    public enum EmailErrorState{
        INVALID_PATTERN, EMPTY, VALID, NOT_VALIDATED
    }

    public enum PwErrorState{
        NOT_SIX, EMPTY, NOT_NUM, VALID, NOT_VALIDATED
    }

    public enum AuthErrorState{
        BEFORE_ATTEMPT, SUCCESS, FAILURE, WAITING
    }
    @Override
    public String toString() {
        return "LoginEntity{" + "enteredPassword='" + enteredPassword + '\'' + ", enteredEmail='" + enteredEmail + '\'' + '}';
    }

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

    public EmailErrorState getEmailErrorState() {
        return emailErrorState;
    }

    public void setEmailErrorState(EmailErrorState emailErrorState) {
        this.emailErrorState = emailErrorState;
    }

    public PwErrorState getPwErrorState() {
        return pwErrorState;
    }

    public void setPwErrorState(PwErrorState pwErrorState) {
        this.pwErrorState = pwErrorState;
    }

    public AuthErrorState getAuthErrorState() {
        return authErrorState;
    }

    public void setAuthErrorState(AuthErrorState authErrorState) {
        this.authErrorState = authErrorState;
    }

    private EmailErrorState emailErrorState = EmailErrorState.NOT_VALIDATED;
    private PwErrorState pwErrorState = PwErrorState.NOT_VALIDATED;
    private AuthErrorState authErrorState = AuthErrorState.BEFORE_ATTEMPT;

}
