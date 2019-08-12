package com.example.myfinances.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {
    private static FirebaseAuth authentication;
    private static DatabaseReference firebase;

    //retorna a instancia do firebasedatabase
    public static DatabaseReference getFirebaseDatabase(){
        if(firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }

    //retorna a instancia do firebaseauth
    public static FirebaseAuth getFirebaseAuthentication(){
        if(authentication == null) {
            authentication = FirebaseAuth.getInstance();
        }
        return authentication;
    }
}
