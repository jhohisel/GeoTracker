/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.model;

import junit.framework.TestCase;

import edu.uw.ProjectMayhem.model.User;

public class UserTest extends TestCase{
    private User mUser;

    public void setUp() {
        mUser = new User("TestDummy", "test@dummy.com", "test", "Where were you born?", "Aberdeen");
    }

    public void testConstructor() {
        User user = new User("id", "email@email.com", "password", "question", "answer");
        assertNotNull(user);
    }

    public void testGetUserID() {
        assertEquals("getUserID() failed!", "TestDummy", "TestDummy");
    }

    public void testGetEmail() {
        assertEquals("getEmail() failed!", "test@dummy.com", "test@dummy.com");
    }

    public void testGetPassword() {
        assertEquals("getPassword() failed!", "test", "test");
    }

    public void testGetSecurityQuestion() {
        assertEquals("getSecurityQuestion() failed!", "Where were you born?", "Where were you born?");
    }

    public void testGetSecurityAnswer() {
        assertEquals("getSecurityAnswer() failed!", "Aberdeen", "Aberdeen");
    }
}
