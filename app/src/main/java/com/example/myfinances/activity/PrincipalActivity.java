package com.example.myfinances.activity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.myfinances.adapter.AdapterMovimentacao;
import com.example.myfinances.config.FirebaseConfig;
import com.example.myfinances.helper.Base64Custom;
import com.example.myfinances.model.Movimentacao;
import com.example.myfinances.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfinances.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView textHello, textBalance;
    private Double totalExpenses = 0.00;
    private Double totalRevenue = 0.00;
    private Double balance = 0.00;

    private FirebaseAuth authentication = FirebaseConfig.getFirebaseAuthentication();
    private DatabaseReference firebaseRef = FirebaseConfig.getFirebaseDatabase();
    private DatabaseReference userRef;
    private ValueEventListener valueEventListenerUser;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef;
    private String selectedMonthYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Finances");
        setSupportActionBar(toolbar);

        textHello = findViewById(R.id.textHello);
        textBalance = findViewById(R.id.textBalance);

        calendarView = findViewById(R.id.calendarView);
        calendarViewConfig();

         recyclerView = findViewById(R.id.recyclerMovimentacoes);
        //configurar adapter para recyclerview
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        //configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);

        //deslizar movimentacoes pro lado para deletar
        swipe();
    }

    //mensagem de deletar movimentacao quando arrastar pro lado
    public void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteMovimentacao(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    //exclui movimentacao quando chamado
    public void deleteMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //configuração do alert dialog
        alertDialog.setTitle("Excluir movimentação");
        alertDialog.setMessage("Você tem certeza que deseja excluir esta movimentação?");
        alertDialog.setCancelable(false);

        //botao de confirmar
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(position);

                //id do usuario
                String userEmail = authentication.getCurrentUser().getEmail();
                String userId = Base64Custom.codeBase64(userEmail);

                //acessa o no da movimentação e deleta
                movimentacaoRef = firebaseRef.child("movimentacao").child(userId).child(selectedMonthYear);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);

                //atualiza saldo
                updateBalance();
            }
        });
        //botao de cancelar
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //recupera de volta a movimentação que sumiu da tela
                adapterMovimentacao.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    //atualizar saldo
    public void updateBalance(){

        String userEmail = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);
        //recupera usuario
        userRef = firebaseRef.child("usuarios").child(userId);

        if(movimentacao.getType().equals("r")){
            totalRevenue = totalRevenue - movimentacao.getValue();
            userRef.child("totalRevenue").setValue(totalRevenue);
        }

        if(movimentacao.getType().equals("d")){
            totalExpenses = totalExpenses - movimentacao.getValue();
            userRef.child("totalExpenses").setValue(totalExpenses);
        }
    }

    public void returnMovimentacoes(){
        //id do usuario
        String userEmail = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);

        movimentacaoRef = firebaseRef.child("movimentacao").child(userId).child(selectedMonthYear);
        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacoes.clear();

                for(DataSnapshot data: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = data.getValue(Movimentacao.class);
                    movimentacao.setKey(data.getKey());
                    movimentacoes.add(movimentacao);
                }

                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //recuperar resumo sobre o usuario (nome, saldo)
    public void returnUserResume(){
        String userEmail = authentication.getCurrentUser().getEmail();
        String userId = Base64Custom.codeBase64(userEmail);
        //recupera usuario
        userRef = firebaseRef.child("usuarios").child(userId);

        valueEventListenerUser = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //recupera dados do usuario
                User user = dataSnapshot.getValue(User.class);

                //exibe nome do usuario na tela
                textHello.setText("Olá, " + user.getName());

                //recupera total de receitas e despesas
                totalExpenses = user.getTotalExpenses();
                totalRevenue = user.getTotalRevenue();

                //calcula saldo, formata e exibe na tela
                balance = totalRevenue - totalExpenses;
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String formattedBalance = decimalFormat.format(balance);
                textBalance.setText("R$ " + formattedBalance);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //exibir menu (sair)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //tratar clique no menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                //deslogar usuário
                authentication.signOut();
                //ir para activity do começo
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addExpense(View view){
        startActivity(new Intent(this, ExpensesActivity.class));
    }
    public void addRevenue(View view){
        startActivity(new Intent(this, RevenueActivity.class));
    }

    //configurar calendario
    public void calendarViewConfig(){
        //substituir meses de ingles para portugues
        CharSequence months[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(months);

        CalendarDay currentDate = calendarView.getCurrentDate();

        String selectedMonth = String.format("%02d", currentDate.getMonth()+1);

         /*variavel selectedMonthYear recebe os valores de mes e ano MMyyyy para
           utilizar no nó no banco e identificar as movimetacoes do mes para o adapter*/

        selectedMonthYear = String.valueOf(selectedMonth + "" + currentDate.getYear());

        //listener para identificar mudança nos meses para navegar entre os meses
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String selectedMonth = String.format("%02d", date.getMonth()+1);

                /*variavel selectedMonthYear recebe os valores de mes e ano MMyyyy para
                utilizar no nó no banco e identificar as movimetacoes do mes para o adapter*/
                selectedMonthYear =  String.valueOf(selectedMonth + "" + date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
                returnMovimentacoes();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        //recuperar resumo sobre o usuario (nome, saldo)
        returnUserResume();

        //recupera movimentacoes
       returnMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //parar de utilizar o listener  de usuário quando o usuario fechar o app
        userRef.removeEventListener(valueEventListenerUser);

        //para de utilizar o listener de movimentacao quando usuario fechar o app
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacoes);
    }
}
