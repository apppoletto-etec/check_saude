package com.lima.medicinatrabalho.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.lima.medicinatrabalho.R;
import com.lima.medicinatrabalho.model.Funcionarios;

import java.util.List;

public class AdapterFuncionarios extends RecyclerView.Adapter<AdapterFuncionarios.MyViewHolder> {


    private List<Funcionarios> funcionarios;
    private Context context;


    public AdapterFuncionarios(List<Funcionarios> funcionarios, Context context) {
        this.funcionarios = funcionarios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_funcionario,parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Funcionarios funcionario  = funcionarios.get(position);
        holder.nome.setText(funcionario.getNome());
        holder.funcao.setText(funcionario.getFuncao());



    }

    @Override
    public int getItemCount() {
        return funcionarios.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView funcao;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome=  itemView.findViewById(R.id.textViewNomeAdapetr);
            funcao = itemView.findViewById(R.id.textViewFuncao);


        }
    }


}