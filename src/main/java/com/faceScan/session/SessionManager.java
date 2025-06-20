package com.faceScan.session;

import com.faceScan.model.User;

public class SessionManager {
    private static User currentUser;

    public static void login(User user){
        currentUser = user;
        System.out.println("[SESSION] Logged in: " + user.getUsername());
    }

    public static void logout(){
        System.out.println("[SESSION] Logged out: " + (currentUser != null ? currentUser.getUsername() : "none"));
        currentUser = null;
    }

    public static boolean isLoggedIn(){ return currentUser != null; }

    public static void setCurrentUser(User user) {currentUser = user;}
    public static User getCurrentUser() {return currentUser;}


}