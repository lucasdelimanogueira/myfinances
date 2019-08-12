package com.example.myfinances.model;

import com.example.myfinances.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;
    private Double totalRevenue = 0.00;
    private Double totalExpenses = 0.00;

    public User() {
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void save(){
        DatabaseReference firebase = FirebaseConfig.getFirebaseDatabase();
        firebase.child("usuarios").child(this.userId).setValue(this);
    }
    @Exclude //exclui userId de ser salva no banco pelo método save()
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //exclui password de ser salva no banco pelo método save()
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
