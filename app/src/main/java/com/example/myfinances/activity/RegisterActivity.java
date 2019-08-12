package com.example.myfinances.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfinances.R;
import com.example.myfinances.config.FirebaseConfig;
import com.example.myfinances.helper.Base64Custom;
import com.example.myfinances.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {
    private EditText fieldName, fieldEmail, fieldPassword;
    private Button buttonRegister;
    private FirebaseAuth authentication;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //mudar titulo na activity (action bar)
        getSupportActionBar().setTitle("Cadastro");

        fieldName = findViewById(R.id.editName);
        fieldEmail = findViewById(R.id.editEmail);
        fieldPassword = findViewById(R.id.editPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textName = fieldName.getText().toString();
                String textEmail = fieldEmail.getText().toString();
                String textPassword = fieldPassword.getText().toString();

                //validar campos preenchidos
                if(!textName.isEmpty()){
                    if(!textEmail.isEmpty()){
                        if(!textPassword.isEmpty()){
                            //caso todos campos preenchidos
                            user = new User();
                            user.setName(textName);
                            user.setEmail(textEmail);
                            user.setPassword(textPassword);
                            registerUser();

                        }else{
                            Toast.makeText(RegisterActivity.this,
                                    "Preencha a senha", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(RegisterActivity.this,
                                "Preencha o e-mail", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this,
                            "Preencha o nome", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerUser(){
        authentication = FirebaseConfig.getFirebaseAuthentication();
        authentication.createUserWithEmailAndPassword(
            user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //transforamar email em base64 pra servir de id
                    String userId = Base64Custom.codeBase64(user.getEmail());
                    user.setUserId(userId);
                    user.save();
                    finish();
                }else{

                    String exceptionMsg = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e) {
                        exceptionMsg = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exceptionMsg = "Por favor, digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        exceptionMsg = "Essa conta já foi cadastrado";
                    }catch (Exception e){
                        exceptionMsg = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this,
                            exceptionMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
