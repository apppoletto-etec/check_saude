package com.lima.medicinatrabalho.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lima.medicinatrabalho.R;
import com.lima.medicinatrabalho.adapter.AdapterListagem;
import com.lima.medicinatrabalho.bd.ConfiguracaoFirebase;
import com.lima.medicinatrabalho.model.EntregaExames;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListagemExameActivity extends AppCompatActivity {


    private EditText campoFuncionarioListagem;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference listagemRef;
    private FirebaseAuth autenticacao;
    private RecyclerView recyclerListagem;
    private List<EntregaExames> entregaList = new ArrayList<>();
    private AdapterListagem adapterListagem;
    private Button botaoAtualizarEntrega;

    private ProgressBar progressBar;

    // Listener do Firebase para ser removido quando a atividade for destruída
    private ValueEventListener listagemListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listagem_exame);
        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        listagemRef = ConfiguracaoFirebase.getFireBaseDatabase().child("EntregaExames");

        Toolbar toolbar = findViewById(R.id.toolbarJava);
        toolbar.setTitle("Relatório de entrega de Exames");
        setSupportActionBar(toolbar);


//        // Agendar a verificação diária
//        PeriodicWorkRequest verificacaoDiariaWork =
//                new PeriodicWorkRequest.Builder(VerificacaoDiariaWorker.class, 1, TimeUnit.DAYS)
//                        .build();
//
//        WorkManager.getInstance(this).enqueue(verificacaoDiariaWork);






        // Configurar RecyclerView
        recyclerListagem.setLayoutManager(new LinearLayoutManager(this));
        recyclerListagem.setHasFixedSize(true);

        // Inicializar o Adapter
        adapterListagem = new AdapterListagem(entregaList, this);
        recyclerListagem.setAdapter(adapterListagem);

        // Recupera a listagem de entregas de EPI
        recuperaListagem();

        // Configura o evento de edição para pesquisa
        eventoEditListagem();
    }


    //salvar texto formatado no firebase
    //
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


    private void pesquisaEntrega(@NonNull String palavra) {
        String textoDigitado = formatarTexto(palavra);
        Query query;

        if (textoDigitado.equals("")) {
            query = listagemRef.orderByChild("nomeFuncExame");
        } else {
            query = listagemRef
                    .orderByChild("nomeFuncExame")
                    .startAt(textoDigitado)
                    .endAt(textoDigitado + "\uf8ff");
        }

        entregaList.clear();

        listagemListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entregaList.clear(); // Limpa a lista antes de adicionar os itens filtrados

                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                    EntregaExames e = objSnapshot.getValue(EntregaExames.class);
                    entregaList.add(e);
                }

                adapterListagem.notifyDataSetChanged(); // Notifica o adaptador sobre as mudanças
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Trate o erro, se necessário
            }
        });
    }

    private void eventoEditListagem() {
        campoFuncionarioListagem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String palavra = campoFuncionarioListagem.getText().toString().trim();
                pesquisaEntrega(palavra);
            }
        });
    }

    private void recuperaListagem() {

        progressBar.setVisibility(View.VISIBLE);
        listagemListener = listagemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entregaList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    entregaList.add(ds.getValue(EntregaExames.class));
                }

                Collections.reverse(entregaList);
                adapterListagem.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (entregaList == null) {
                    Toast.makeText(ListagemExameActivity.this, "Não há itens", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void inicializarComponentes() {
        recyclerListagem = findViewById(R.id.recyclerListagem);
        botaoAtualizarEntrega = findViewById(R.id.btnAtualizarEntrega);
        campoFuncionarioListagem = findViewById(R.id.editPesquisarListagem);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tela_relatorio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sair) {
            deslogarUsuario();
            finish();
        } else if (id == R.id.cadastroFunc) {
            startActivity(new Intent(ListagemExameActivity.this, CadastroFuncionarioActivity.class));
            finish();
        } else if (id == R.id.main) {
            startActivity(new Intent(ListagemExameActivity.this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario() {
        try {
            autenticacao.signOut();
            Intent in = new Intent(ListagemExameActivity.this, LoginActivity.class);
            startActivity(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
