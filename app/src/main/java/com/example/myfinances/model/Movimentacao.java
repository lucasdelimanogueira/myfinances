package com.example.myfinances.model;

import com.example.myfinances.config.FirebaseConfig;
import com.example.myfinances.helper.Base64Custom;
import com.example.myfinances.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String date;
    private String category;
    private String description;
    private String type;
    private Double value;
    private String key;

    public Movimentacao() {
    }

    public void save(String selectedDate){
        //recuperar usuario que está autenticado
        FirebaseAuth authentication = FirebaseConfig.getFirebaseAuthentication();
        String userId = Base64Custom.codeBase64(authentication.getCurrentUser().getEmail());

        //transformar data para formato utilizado no nó do banco 01/02/2000 -> 022000
        String monthYear = DateCustom.transformSelectedDate(selectedDate);

        //salva movimentacao
        DatabaseReference firebase = FirebaseConfig.getFirebaseDatabase();
        firebase.child("movimentacao").child(userId).child(monthYear).push().setValue(this);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
