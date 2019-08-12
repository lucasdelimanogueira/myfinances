package com.example.myfinances.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinances.R;
import com.example.myfinances.model.Movimentacao;

import java.util.List;


public class AdapterMovimentacao extends RecyclerView.Adapter<AdapterMovimentacao.MyViewHolder> {

    List<Movimentacao> movimentacoes;
    Context context;

    public AdapterMovimentacao(List<Movimentacao> movimentacoes, Context context) {
        this.movimentacoes = movimentacoes;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_movimentacao, parent, false);
        return new MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movimentacao movimentacao = movimentacoes.get(position);

        holder.title.setText(movimentacao.getDescription());
        holder.value.setText(String.valueOf(movimentacao.getValue()));
        holder.category.setText(movimentacao.getCategory());
        holder.value.setTextColor(context.getResources().getColor(R.color.colorAccentReceita));

        if (movimentacao.getType().equals("d")) {
            holder.value.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.value.setText("-" + movimentacao.getValue());
        }
    }


    @Override
    public int getItemCount() {
        return movimentacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, value, category;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textAdapterTitle);
            value = itemView.findViewById(R.id.textAdapterValue);
            category = itemView.findViewById(R.id.textAdapterCategory);
        }

    }

}
