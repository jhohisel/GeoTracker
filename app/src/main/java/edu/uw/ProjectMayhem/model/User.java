/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto
 */
package edu.uw.ProjectMayhem.model;

import java.io.Serializable;

/**
 * Stores data about an app user.
 */
public final class User implements Serializable {

    /**
     * Unique ID for the user.
     */
    private String mUserID;

    /**
     * The user's email address.
     */
    private String mEmail;

    /**
     * The user's passsword.
     */
    private String mPassword;

    /**
     * The selected security question.
     */
    private String mSecurityQuestion;

    /**
     * The answer for the security question.
     */
    private String mSecurityAnswer;

    /** Salt for the new password (used for hashing). */
    //private String salt = BCrypt.gensalt();

    /** The bcrypt hash of the new password (for secure transmission). */
    //private String pwHash;

    /**
     * Constructs a user with the given id, email, password, security question, and security answer.
     *
     * @param id       the unique ID for the user
     * @param email    the user's email address
     * @param password the user's password
     * @param question the user's security question
     * @param answer   the answer to the security question
     */
    public User(String id, String email, String password, String question, String answer) {

        mUserID = id;
        mEmail = email;
        mPassword = password;
        mSecurityQuestion = question;
        mSecurityAnswer = answer;
        //pwHash = BCrypt.hashpw(password, salt);
    }

    /**
     * Retrieves User ID.
     *
     * @return the user's unique ID.
     */
    public String getUserID() {
        return mUserID;
    }

    /**
     * Retrieves user email address.
     *
     * @return the user's email address.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Retrieves user security question.
     *
     * @return the user's security question.
     */
    public String getSecurityQuestion() {
        return mSecurityQuestion;
    }

    /**
     * Retrieves user security answer.
     *
     * @return the user's answer to the security question.
     */
    public String getSecurityAnswer() {
        return mSecurityAnswer;
    }

    /**
     * Retrieves user's password hash.
     * @return the hash of the password.
     */
    //public String getPwHash() {
    //    return pwHash;
    //}

    /**
     * Retrieves user's salt
     * @return the salt used to hash the password.
    public String getSalt() {
    return salt;
    }
     */

    /**
     * Saves user information.
     * To DO
     */
    public void saveUserInfo() {

    }


    /**
     * Resets User info.
     * TO DO
     */
    public void clearUserInfo() {

    }

}
