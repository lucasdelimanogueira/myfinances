package com.example.myfinances.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfinances.R;
import com.example.myfinances.config.FirebaseConfig;
import com.example.myfinances.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private Button buttonEnter;
    private User user;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fieldEmail = findViewById(R.id.editEmail);
        fieldPassword = findViewById(R.id.editPassword);
        buttonEnter = findViewById(R.id.buttonEnter);

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = fieldEmail.getText().toString();
                String textPassword = fieldPassword.getText().toString();

                //validar preenchimento dos campos
                if(!textEmail.isEmpty()){
                    if(!textPassword.isEmpty()){
                        //caso todos campos preenchidos
                        user = new User();
                        user.setEmail(textEmail);
                        user.setPassword(textPassword);
                        validateLogin();
                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateLogin(){
        authentication = FirebaseConfig.getFirebaseAuthentication();
        authentication.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //abre tela principal
                    openPrincipalActivity();
                }else{
                    String exceptionMsg = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionMsg = "Usuário ou senha inválida";
                    }catch (FirebaseAuthInvalidUserException e) {
                        exceptionMsg = "Usuário ainda não cadastrado";
                    }catch (Exception e){
                        exceptionMsg = "Erro ao fazer login: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            exceptionMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //ir para tela principal
    void openPrincipalActivity(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}
