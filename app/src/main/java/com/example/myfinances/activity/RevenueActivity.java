package com.example.myfinances.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfinances.R;
import com.example.myfinances.config.FirebaseConfig;
import com.example.myfinances.helper.Base64Custom;
import com.example.myfinances.helper.DateCustom;
import com.example.myfinances.model.Movimentacao;
import com.example.myfinances.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RevenueActivity extends AppCompatActivity {

    private TextInputEditText fieldDate, fieldCategory, fieldDescription;
    private EditText fieldValue;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
    private FirebaseAuth authentication = FirebaseConfig.getFirebaseAuthentication();
    private Double totalRevenue = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        fieldDate = findViewById(R.id.editDate);
        fieldCategory = findViewById(R.id.editCategory);
        fieldDescription = findViewById(R.id.editDescription);
        fieldValue = findViewById(R.id.editValue);

        //preenche o campo data com a data atual
        fieldDate.setText(DateCustom.currentDate());

        returnTotalRevenue();
    }

    //salva receita (método chamado no onclick do float action button)
    public void saveRevenue(View view){

        if(validateRevenueField()) {

            movimentacao = new Movimentacao();
            String date = fieldDate.getText().toString();
            Double returnedValue = Double.parseDouble(fieldValue.getText().toString());

            movimentacao.setValue(returnedValue);
            movimentacao.setCategory(fieldCategory.getText().toString());
            movimentacao.setDescription(fieldDescription.getText().toString());
            movimentacao.setDate(date);
            movimentacao.setType("r");

            totalRevenue = totalRevenue + returnedValue;

            //atualiza despesa total do usuario no banco
            updateTotalExpenses();

            //salva movimentacao no banco
            movimentacao.save(date);
            finish();
        }
    }

    //validar se os campos estao preenchidos
    public Boolean validateRevenueField(){
        String textValue = fieldValue.getText().toString();
        String textDate = fieldDate.getText().toString();
        String textCategory = fieldCategory.getText().toString();

        if(!textValue.isEmpty()){
            if(!textDate.isEmpty()){
                if(!textCategory.isEmpty()){
                    return true;
                }
                else{
                    Toast.makeText(RevenueActivity.this,
                            "Preencha a categoria", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }else{
                Toast.makeText(RevenueActivity.this,
                        "Preencha a data", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(RevenueActivity.this,
                    "Preencha um valor", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //recupera total de receitas do usuario
    public void returnTotalRevenue(){
        String userEmail = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);
        //recupera usuario
        DatabaseReference userRef = firebaseRef.child("usuarios").child(userId);

        //ouvinte para retornar o valor do campo totalExpense do usuario, caso seja modificado
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                totalRevenue = user.getTotalRevenue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //atualiza receita total
    public void updateTotalExpenses(){

        String userEmail = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);
        //recupera usuario
        DatabaseReference userRef = firebaseRef.child("usuarios").child(userId);

        userRef.child("totalRevenue").setValue(totalRevenue);

    }

}
