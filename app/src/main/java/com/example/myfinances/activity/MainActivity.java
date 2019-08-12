package com.example.myfinances.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myfinances.R;
import com.example.myfinances.activity.LoginActivity;
import com.example.myfinances.activity.RegisterActivity;
import com.example.myfinances.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //apagar botoes de navegação
        setButtonBackVisible(false);
        setButtonNextVisible(false);

        //slide 1
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build());

        //slide 2
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build());

        //slide 3
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build());

        //slide 4
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .canGoBackward(true)
                .build());

        //slide 5 cadastro
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoBackward(true)
                .canGoForward(false)
                .build());

    }

    @Override
    protected void onStart() {
        super.onStart();
        //se usuario autenticado ir para tela principal
        verifyLoggedUser();
    }

    //abre tela de login
    public void btEnter(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }
    //abre tela de registrar
    public  void btRegister(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void verifyLoggedUser(){
        authentication = FirebaseConfig.getFirebaseAuthentication();
        if(authentication.getCurrentUser() != null){
            openPrincipalActivity();
        }
    }

    //ir para tela principal
    void openPrincipalActivity(){
        startActivity(new Intent(this, PrincipalActivity.class));
    }
}
