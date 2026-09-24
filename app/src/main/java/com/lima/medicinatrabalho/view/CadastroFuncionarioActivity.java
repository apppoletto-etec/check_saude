package com.lima.medicinatrabalho.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.lima.medicinatrabalho.R;
import com.lima.medicinatrabalho.bd.ConfiguracaoFirebase;
import com.lima.medicinatrabalho.model.Funcionarios;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CadastroFuncionarioActivity extends AppCompatActivity {



    private List<Funcionarios>funcionarioList = new ArrayList<>();
    private ArrayAdapter<Funcionarios>arrayAdapterFuncionario;

    private ListView listVdados;

    private TextView totalFunc;

    private EditText campoNomePesquisado, campoNome,campoFuncao;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference funcionarioRef;

    Funcionarios funcionario = new Funcionarios();

    private ImageView voltarFunc;

    Funcionarios funcionarioSelecionado = new Funcionarios();

    private Button botaoSalvarFunc, botaoEditarFunc,botaoExcluirFunc, botaoNovoFunc;

    String nome1,funcao1;
    int cont =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_funcionario);

        Toolbar toolbar = findViewById(R.id.toolbarJava);
        toolbar.setTitle("Cadastro de Funcionários");
        toolbar.setTitleMarginStart(200);
        setSupportActionBar(toolbar);






        //Area de inicialização-------------------
        inicializarFirebase();
        inicializarComponentes();
        eventodatabase();
        eventoedit();
        botaoExcluirFunc.setVisibility(View.INVISIBLE);
        botaoEditarFunc.setVisibility(View.INVISIBLE);
        botaoSalvarFunc.setVisibility(View.VISIBLE);


        //fim Area de inicialização-------------------


        listVdados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                funcionarioSelecionado =(Funcionarios)parent.getItemAtPosition(position);

                campoNome.setText(funcionarioSelecionado.getNome().toString());
               campoFuncao.setText(funcionarioSelecionado.getFuncao().toString());
                System.out.println(funcionarioSelecionado.getId());



                nome1 =funcionarioSelecionado.getNome().toString();
                funcao1=funcionarioSelecionado.getFuncao().toString();

                botaoEditarFunc.setVisibility(View.VISIBLE);
                botaoExcluirFunc.setVisibility(View.VISIBLE);
                botaoSalvarFunc.setVisibility(View.INVISIBLE);


            }
        });







    }//fim onCreate


    public  void voltarFunc(View v){
        Intent intent = new Intent(CadastroFuncionarioActivity.this, ListagemExameActivity.class);
        startActivity(intent);
    }

    //---------------------------------- CRUD  ------------------------------------------
    private String formatarTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }

        // Divide o texto em palavras
        String[] palavras = texto.split("\\s+");
        StringBuilder textoFormatado = new StringBuilder();

        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                // Converte a primeira letra para maiúscula e o restante para minúsculo
                String palavraFormatada = palavra.substring(0, 1).toUpperCase() + palavra.substring(1).toLowerCase();
                textoFormatado.append(palavraFormatada).append(" ");
            }
        }

        // Remove o último espaço em branco
        return textoFormatado.toString().trim();
    }

    public void salvarFuncionario(View v){

        funcionarioRef = ConfiguracaoFirebase.getFireBaseDatabase();

        String textoNome = formatarTexto(campoNome.getText().toString());


        String textoFuncao = campoFuncao.getText().toString();




        if(!textoNome.isEmpty()){
            if(!textoFuncao.isEmpty()){


              funcionario = new Funcionarios();
                funcionario.setNome(textoNome.trim());
                  //  sepultado.setCemiterio("Santa Faustina");
                funcionario.setFuncao(campoFuncao.getText().toString().trim());

                funcionario.setId(UUID.randomUUID().toString());

                   funcionarioRef.child("Funcionario").child(funcionario.getId()).setValue(funcionario);




                    Toast.makeText(this, "Salvo!", Toast.LENGTH_SHORT).show();
                    limparCampos();
                    campoNome.requestFocus();



                }else{
                    Toast.makeText(this, "Preencha a função", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
            }






    }//fim salvar

    public void atualizar(View v){

        String textoNome = formatarTexto(campoNome.getText().toString());


        if(!textoNome.isEmpty()){
            Funcionarios s= new Funcionarios();
            s.setId(funcionarioSelecionado.getId());

            s.setNome(textoNome.trim());
            //   s.setCemiterio(campoCemiterio.getText().toString().trim());
           // s.setNome(campoNome.getText().toString().trim());
            s.setFuncao(campoFuncao.getText().toString().trim());


            funcionarioRef.child("Funcionario").child(s.getId()).setValue(s);
            limparCampos();



        }else{
            Toast.makeText(this, "Voce precisa iserir os registro que deseja alterar", Toast.LENGTH_SHORT).show();
        }




    }

    public void deletar(View v){
        Funcionarios s= new Funcionarios();
        s.setId(funcionarioSelecionado.getId());
        funcionarioRef.child("Funcionario").child(s.getId()).removeValue();
        limparCampos();
    }
    public  void btLimparCamppos(View v){
        limparCampos();
    }



    private void limparCampos() {

        campoNome.setText("");
        campoFuncao.setText("");

        botaoEditarFunc.setVisibility(View.INVISIBLE);
        botaoExcluirFunc.setVisibility(View.INVISIBLE);
        botaoSalvarFunc.setVisibility(View.VISIBLE);





    }


// // /--------------------fim crud -------------------------------------------------------------------


    public void inicializarFirebase(){

        firebaseDatabase=FirebaseDatabase.getInstance();
        if (FirebaseApp.getApps(this).size() == 0)
            firebaseDatabase.setPersistenceEnabled(true);
       funcionarioRef =firebaseDatabase.getReference();
        FirebaseApp.initializeApp(CadastroFuncionarioActivity.this);

    }

//    public void telaPesquisar(View v){
//        Intent intent = new Intent(CadastroFuncionarioActivity.this,PesquisarActivity.class);
//        startActivity(intent);
//        finish();
//    }




    private void pesquisaPalavra(@NonNull String palavra) {
        // Formata a palavra de entrada
        String textoDigitado = formatarTexto(palavra);

        Query query;
        if (textoDigitado.isEmpty()) {
            query = funcionarioRef.child("Funcionario").orderByChild("nome");
        } else {
            query = funcionarioRef.child("Funcionario")
                    .orderByChild("nome")
                    .startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");

            nome1 = textoDigitado;
        }

        funcionarioList.clear();

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objsnapshot : dataSnapshot.getChildren()) {
                    Funcionarios s = objsnapshot.getValue(Funcionarios.class);
                    funcionarioList.add(s);
                }

                arrayAdapterFuncionario = new ArrayAdapter<Funcionarios>(CadastroFuncionarioActivity.this,
                        android.R.layout.simple_list_item_1, funcionarioList);
                if (nome1 == null) {
                    arrayAdapterFuncionario.clear();
                }
                listVdados.setAdapter(arrayAdapterFuncionario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Lidar com o erro aqui, se necessário
            }
        });
    }


    private void eventoedit() {


        campoNomePesquisado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra= campoNomePesquisado.getText().toString().trim();
                pesquisaPalavra(palavra);

            }
        });
    }


    public void inicializarComponentes(){
        campoNomePesquisado=findViewById(R.id.editNomePesquisar2) ;
        campoFuncao = findViewById(R.id.editFuncao);
        campoNome = findViewById(R.id.editNome);
        listVdados=findViewById(R.id.list);
        voltarFunc = findViewById(R.id.imageViewVoltarFunc);
        botaoEditarFunc= findViewById(R.id.buttonAtualizarFuncionario);
        botaoExcluirFunc= findViewById(R.id.buttonExcluirFuncioanrio);
        botaoSalvarFunc= findViewById(R.id.buttonSalvarFuncionario);
        botaoNovoFunc= findViewById(R.id.buttonLimparFuncionario);


    }


    private void eventodatabase() {
        //-----------------------------------

       funcionarioRef.child("Funcionario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                funcionarioList.clear();
                for(DataSnapshot objSnapshot: dataSnapshot.getChildren()){
                    Funcionarios s=objSnapshot.getValue(Funcionarios.class);
                    funcionarioList.add(s);
                    cont= funcionarioList.size();
                  //  totalFunc.setText(String.valueOf(cont));

                }
                arrayAdapterFuncionario = new ArrayAdapter<Funcionarios>(CadastroFuncionarioActivity.this,
                        android.R.layout.simple_list_item_1,funcionarioList );
                listVdados.setAdapter(arrayAdapterFuncionario);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }







    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ListagemExameActivity.class);
                NavUtils.navigateUpTo(this, intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }










}