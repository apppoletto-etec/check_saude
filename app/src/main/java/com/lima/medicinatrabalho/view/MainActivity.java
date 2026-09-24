package com.lima.medicinatrabalho.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lima.medicinatrabalho.R;
import com.lima.medicinatrabalho.bd.ConfiguracaoFirebase;
import com.lima.medicinatrabalho.model.EntregaExames;
import com.lima.medicinatrabalho.model.Funcionarios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth autenticacao;

    private List<Funcionarios> funcionarioList = new ArrayList<>();
    private List<EntregaExames> entregaExameList = new ArrayList<>();
    private ArrayAdapter<Funcionarios> arrayAdapterFuncionarios;
    private ArrayAdapter<EntregaExames> arrayAdapterEntregaExames;

    private ListView listVdadosEntregaExames;
    private ListView listVdadosFuncionario;

    FirebaseDatabase firebaseDatabaseEntregaExames;
    FirebaseDatabase firebaseDatabaseFuncionario;

    private Funcionarios funcionario = new Funcionarios();
    private EntregaExames entregaExames = new EntregaExames();
    String nome1,funcao1,prazoEntrega1, nomeFuncEntrega1;

    private Button botaoEditarEntregaExames,botaoLimparEntregaExames,botaoExcluirEntregaExames, botaoSalvarEntregaEntregaExames;

    private EditText nomeExame, dataEntrega, dataProximaEntrega,editNomePesquisa;
    private CheckBox checkAdmissional,checkPeriodico,checkRetorno,checkMudanca,checkDemissional, checkApto, checkInapto;

    DatabaseReference funcionarioRef;
    DatabaseReference entregaExameRef;

    Funcionarios funcionarioSelecionado = new Funcionarios();

    Funcionarios funcionarioSelecionadoIntent = new Funcionarios();

    EntregaExames entregSelecionada = new EntregaExames();
    EntregaExames entregSelecionadaIntent = new EntregaExames();
    ImageView imageVoltar;
    Funcionarios funcId = new Funcionarios();

    int cont = 0;

    private boolean flagIntent =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Habilita o botão "Up" na ActionBar



        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        Toolbar toolbar = findViewById(R.id.toolbarJava);
        toolbar.setTitle("Entrega de Exames");
        toolbar.setTitleMarginStart (200);
        setSupportActionBar(toolbar);



       inicializarComponentes();
        inicializarFirebase();
       eventodatabaseFunc();
        eventoeditFunc();
        eventodatabaseExame();
        eventoeditExame();
        setupCheckBoxListeners();
        carregarEntrega();
        dataProximaTroca();
        dataEntrega();



        listVdadosFuncionario.setVisibility(View.INVISIBLE);
        botaoEditarEntregaExames.setVisibility(View.INVISIBLE);
        botaoExcluirEntregaExames.setVisibility(View.INVISIBLE);




        // Recuperar os dados da Intent (vindo do AdapterListagem)
        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("id");
            String idFuncionario = intent.getStringExtra("idFuncionario");


            // Verificar se os dados da entrega foram passados via Intent
            if (id != null && idFuncionario != null ) {
                // Se os dados vieram do Adapter, tornamos os campos invisíveis
                botaoLimparEntregaExames.setVisibility(View.INVISIBLE);
                botaoEditarEntregaExames.setVisibility(View.INVISIBLE);
                botaoSalvarEntregaEntregaExames.setVisibility(View.INVISIBLE);
                editNomePesquisa.setVisibility(View.INVISIBLE);
                botaoExcluirEntregaExames.setVisibility(View.VISIBLE);
                nomeExame.setEnabled(false);
                dataEntrega.setEnabled(false);
                dataProximaEntrega.setEnabled(false);
                listVdadosFuncionario.setVisibility(View.INVISIBLE);
                listVdadosEntregaExames.setVisibility(View.INVISIBLE);
                checkAdmissional.setEnabled(false);
                checkPeriodico.setEnabled(false);
                checkInapto.setEnabled(false);
                checkApto.setEnabled(false);
                checkMudanca.setEnabled(false);
               checkPeriodico.setEnabled(false);
               checkDemissional.setEnabled(false);
               checkRetorno.setEnabled(false);

                flagIntent =false;

                //  Intent i = new Intent(this, MainActivity.class);
                //  startActivity(i);

            } else {
                // Se a Activity foi aberta sem os dados do Adapter (ou seja, em modo de criação)
                botaoLimparEntregaExames.setVisibility(View.VISIBLE);
                botaoEditarEntregaExames.setVisibility(View.INVISIBLE);
                botaoSalvarEntregaEntregaExames.setVisibility(View.VISIBLE);
                botaoExcluirEntregaExames.setVisibility(View.INVISIBLE);
                editNomePesquisa.setVisibility(View.VISIBLE);
                nomeExame.setEnabled(true);
                dataEntrega.setEnabled(true);
                dataProximaEntrega.setEnabled(true);
                listVdadosFuncionario.setVisibility(View.INVISIBLE);
                listVdadosEntregaExames.setVisibility(View.INVISIBLE);
                checkAdmissional.setEnabled(true);
                checkPeriodico.setEnabled(true);
                checkInapto.setEnabled(true);
                checkApto.setEnabled(true);
                checkMudanca.setEnabled(true);
                checkPeriodico.setEnabled(true);
                checkDemissional.setEnabled(true);
                checkRetorno.setEnabled(true);
                flagIntent =true;
            }
        }











        //inicializando os lists
        //carregando funcionário  na lista
        listVdadosFuncionario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                funcionarioSelecionado =(Funcionarios)parent.getItemAtPosition(position);

                nomeExame.setText(funcionarioSelecionado.getNome().toString());
            //    campoFuncao.setText(funcionarioSelecionado.getFuncao().toString());
                System.out.println(funcionarioSelecionado.getId());



                nome1 =funcionarioSelecionado.getNome().toString();
                funcao1=funcionarioSelecionado.getFuncao().toString();
                  listVdadosFuncionario.setVisibility(View.INVISIBLE);

            }
        });


        listVdadosEntregaExames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                entregSelecionada =(EntregaExames) parent.getItemAtPosition(position);

                // Preenche os campos de texto com os dados selecionados
                nomeExame.setText(entregSelecionada.getNomeFuncExame());
                dataEntrega.setText(entregSelecionada.getDataEntrega());
                dataProximaEntrega.setText(entregSelecionada.getDataProximaEntrega());

                // Recupera os valores de tipoExame e statusExame
                String tipoExam = entregSelecionada.getTipoExame();
                String tipoStatus = entregSelecionada.getStatusExame();

                // Configura os CheckBoxes com base nos valores recuperados
                setTipoExameCheckBox(tipoExam);
                setStatusExameCheckBox(tipoStatus);

                 funcId = entregSelecionada.getFuncionario();

                // Exibe ou oculta os botões conforme necessário
                botaoEditarEntregaExames.setVisibility(View.VISIBLE);
                botaoExcluirEntregaExames.setVisibility(View.VISIBLE);
                botaoSalvarEntregaEntregaExames.setVisibility(View.INVISIBLE);
                listVdadosFuncionario.setVisibility(View.INVISIBLE);
            }
        });




    }//fim onCreate








    public void carregarEntrega() {
        // Recuperar dados da Intent
        Intent intent = getIntent();
        String id = null;
        String tipoExame = null;
        String status = null;
        String nome = null;
        String dataEntregaIntent = null;
        String proximaEntregaIntent = null;
        if (intent != null) {
            id = intent.getStringExtra("id");
            String idFuncionario = intent.getStringExtra("idFuncionario");
            nome = intent.getStringExtra("nome");
            String funcao = intent.getStringExtra("funcao");
            tipoExame = intent.getStringExtra("tipoExame");
            status = intent.getStringExtra("status");
            dataEntregaIntent = intent.getStringExtra("dataEntregaIntent");
             proximaEntregaIntent = intent.getStringExtra("proximaEntregaIntent");

            // Inicializar os objetos antes de setar os valores
            funcionarioSelecionadoIntent = new Funcionarios();


            // Preencher os campos da entrega
            entregSelecionada.setId(id);

            // Preencher o Funcionario
            if (idFuncionario != null) {
                funcionarioSelecionadoIntent.setId(idFuncionario);
                funcionarioSelecionadoIntent.setNome(nome);
                funcionarioSelecionadoIntent.setFuncao(funcao);
                // Preencha com o nome recuperado
            }


        }

        // Preencher os dados da entrega na entrega selecionada

        entregSelecionadaIntent = new EntregaExames();
        entregSelecionadaIntent.setId(id);
        entregSelecionadaIntent.setFuncionario(funcionarioSelecionadoIntent);

        entregSelecionadaIntent.setDataEntrega(dataEntrega.toString().trim());
        entregSelecionadaIntent.setDataProximaEntrega(dataProximaEntrega.toString().trim());
        entregSelecionadaIntent.setTipoExame(tipoExame);
        entregSelecionadaIntent.setStatusExame(status);

        // Preencher os campos da UI
        nomeExame.setText(nome);


        Map<String, String> checkBoxValues = checkBox();

        setTipoExameCheckBox(tipoExame);
        setStatusExameCheckBox(status);
        dataEntrega.setText(dataEntregaIntent);
       dataProximaEntrega.setText(proximaEntregaIntent);
    }



    //-----crud---------


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


    public void salvarEntregaExames(View v) {
        entregaExameRef = ConfiguracaoFirebase.getFireBaseDatabase();

        // Formata o texto de entrada
        String textoNome = formatarTexto(nomeExame.getText().toString());
        String dataEntreg = formatarTexto(dataEntrega.getText().toString());

        // Recupera os valores dos checkboxes e formata
        Map<String, String> checkBoxValues = checkBox();
        String tipoExam = formatarTexto(checkBoxValues.get("texto"));
        String tipoEstatu = formatarTexto(checkBoxValues.get("status"));

        if (!textoNome.isEmpty()) {
            if (!dataEntreg.isEmpty()) {
                if (!tipoExam.isEmpty()) {
                    if (!tipoEstatu.isEmpty()) {

                        funcionario = new Funcionarios();
                        funcionario.setNome(funcionarioSelecionado.getNome());
                        funcionario.setFuncao(funcionarioSelecionado.getFuncao());
                        funcionario.setId(funcionarioSelecionado.getId());

                        entregaExames = new EntregaExames();
                        entregaExames.setFuncionario(funcionario);
                        entregaExames.setNomeFuncExame(textoNome.trim());
                        entregaExames.setDataEntrega(dataEntreg.trim());
                        entregaExames.setTipoExame(tipoExam);
                        entregaExames.setStatusExame(tipoEstatu);
                        entregaExames.setDataProximaEntrega(dataProximaEntrega.getText().toString().trim());
                        entregaExames.setDataEntrega(dataEntrega.getText().toString().trim());
                        entregaExames.setId(UUID.randomUUID().toString());

                        entregaExameRef.child("EntregaExames").child(entregaExames.getId()).setValue(entregaExames);

                        Toast.makeText(this, "Salvo!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        nomeExame.requestFocus();

                    } else {
                        Toast.makeText(this, "Preencha o status, se apto ou inapto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Selecione o tipo de exame", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha a data de entrega", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }
    }

    //fim salvar
    public void atualizarEntregaExame(View v) {

        // Formata o texto de entrada
        String textoNome = formatarTexto(nomeExame.getText().toString());

        // Recupera os valores dos checkboxes e formata
        Map<String, String> checkBoxValues = checkBox();
        String tipoExam = formatarTexto(checkBoxValues.get("texto"));
        String tipoEstatu = formatarTexto(checkBoxValues.get("status"));

        if (!textoNome.isEmpty()) {

            funcionario = new Funcionarios();

            // Verificando nulidade do funcionario selecionado
            if (funcionarioSelecionado != null && funcionarioSelecionado.getId() != null && funcionarioSelecionado.getNome() != null) {
                funcionario.setNome(funcionarioSelecionado.getNome());
                funcionario.setFuncao(funcionarioSelecionado.getFuncao());
                funcionario.setId(funcionarioSelecionado.getId());
            } else {
                funcionario.setId(funcId.getId());
                funcionario.setNome(funcId.getNome());
                funcionario.setFuncao(funcId.getFuncao());
            }

            entregaExames = new EntregaExames();
            entregaExames.setFuncionario(funcionario);
            entregaExames.setId(entregSelecionada.getId());
            entregaExames.setNomeFuncExame(textoNome.trim());
            entregaExames.setDataEntrega(dataEntrega.getText().toString().trim());
            entregaExames.setTipoExame(tipoExam);
            entregaExames.setStatusExame(tipoEstatu);
            entregaExames.setDataProximaEntrega(dataProximaEntrega.getText().toString().trim());

            if (entregaExames.getId() != null) {
                entregaExameRef.child("EntregaExames").child(entregaExames.getId()).setValue(entregaExames)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Registro atualizado!", Toast.LENGTH_SHORT).show();
                            limparCampos();
                            nomeExame.requestFocus();
                            entregSelecionada.setId(null);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Erro ao atualizar registro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Erro: ID do exame é nulo.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Você precisa inserir os registros que deseja alterar", Toast.LENGTH_SHORT).show();
        }
    }


    public void deletar(View v){
        //instancia objeto dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //configura titulo da mensagem
        dialog.setTitle("Deletar registro");

        dialog.setIcon(getResources().getDrawable(R.drawable.baseline_close_24));
        dialog.setMessage("Tem certeza que deseja dar baixa neste registro ?");

        //configura ações
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(entregSelecionada!=null) {
                    EntregaExames e = new EntregaExames();
                    e.setId(entregSelecionada.getId());
                    entregaExameRef.child("EntregaExames").child(e.getId()).removeValue();

                }else{
                    EntregaExames e = new EntregaExames();
                    e.setId(entregSelecionadaIntent.getId());
                    entregaExameRef.child("EntregaExames").child(e.getId()).removeValue();

                    limparCampos();
                }
                Intent intent = new Intent(MainActivity.this, ListagemExameActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Registro excluido", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        //criar exibir dialog
        dialog.create();
        dialog.show();




    }



    //--fim crud--------


    ///métodos de evento de listagem

    private void eventoeditFunc() {


        nomeExame.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra= nomeExame.getText().toString().trim();
                pesquisaPalavraFunc(palavra);

            }
        });
    }

    private void eventodatabaseFunc() {
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
                arrayAdapterFuncionarios = new ArrayAdapter<Funcionarios>(MainActivity.this,
                        android.R.layout.simple_list_item_1,funcionarioList );
                     listVdadosFuncionario.setAdapter(arrayAdapterFuncionarios);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void pesquisaPalavraFunc(@NonNull String palavra) {

        listVdadosFuncionario.setVisibility(View.VISIBLE);

        String textoDigitado = formatarTexto(palavra);

        Query query;
        if (textoDigitado.equals("")) {
            query =funcionarioRef.child("Funcionario").orderByChild("nome");
        } else {



            query = funcionarioRef.child("Funcionario")
                    .orderByChild("nome")
                    .startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");

            nome1 = textoDigitado;
        }

        funcionarioList.clear();

        //------
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objsnapshot : dataSnapshot.getChildren()) {
                    Funcionarios s = objsnapshot.getValue(Funcionarios.class);
                    funcionarioList.add(s);



                }

                arrayAdapterFuncionarios = new ArrayAdapter<Funcionarios>(MainActivity.this,
                        android.R.layout.simple_list_item_1, funcionarioList);
                if (nome1 == null) {
                    arrayAdapterFuncionarios.clear();
                }
                listVdadosFuncionario.setAdapter(arrayAdapterFuncionarios);



            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void pesquisaPalavraExame(@NonNull String palavra) {

        listVdadosEntregaExames.setVisibility(View.VISIBLE);

        String textoDigitado = formatarTexto(palavra);

        Query query;
        if (textoDigitado.equals("")) {
            query =entregaExameRef.child("EntregaExames").orderByChild("nomeFuncExame");
        } else {



            query = entregaExameRef.child("EntregaExames")
                    .orderByChild("nomeFuncExame")
                    .startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");

            nome1 = textoDigitado;
        }

        entregaExameList.clear();

        //------
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objsnapshot : dataSnapshot.getChildren()) {
                    EntregaExames e = objsnapshot.getValue(EntregaExames.class);
                   entregaExameList.add(e);



                }

                arrayAdapterEntregaExames = new ArrayAdapter<EntregaExames>(MainActivity.this,
                        android.R.layout.simple_list_item_1, entregaExameList);
                if (nome1 == null) {
                    arrayAdapterEntregaExames.clear();
                }
                listVdadosEntregaExames.setAdapter(arrayAdapterEntregaExames);



            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void eventodatabaseExame() {
        //-----------------------------------

        entregaExameRef.child("EntregaExames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entregaExameList.clear();
                for(DataSnapshot objSnapshot: dataSnapshot.getChildren()){
                    EntregaExames s=objSnapshot.getValue(EntregaExames.class);
                    entregaExameList.add(s);
                    cont= entregaExameList.size();
                    //  totalFunc.setText(String.valueOf(cont));

                }
                arrayAdapterEntregaExames = new ArrayAdapter<EntregaExames>(MainActivity.this,
                        android.R.layout.simple_list_item_1,entregaExameList );
                listVdadosEntregaExames.setAdapter(arrayAdapterEntregaExames);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void eventoeditExame() {


       editNomePesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra= editNomePesquisa.getText().toString().trim();
                pesquisaPalavraExame(palavra);

            }
        });
    }

    //----------------------------------fim metodos evento de listagem




    //metodos checkBox -----------------------------------------
    private void setTipoExameCheckBox(String tipoExam) {
        // Primeiro, desmarca todos os CheckBoxes de tipo de exame
        checkAdmissional.setChecked(false);
        checkPeriodico.setChecked(false);
        checkRetorno.setChecked(false);
        checkDemissional.setChecked(false);
        checkMudanca.setChecked(false);

        // Marca o CheckBox correspondente ao tipo de exame selecionado
        if (tipoExam != null) {
            switch (tipoExam) {
                case "Admissional":
                    checkAdmissional.setChecked(true);
                    break;
                case "Periódico":
                    checkPeriodico.setChecked(true);
                    break;
                case "Retorno de trabalho":
                    checkRetorno.setChecked(true);
                    break;
                case "Demissional":
                    checkDemissional.setChecked(true);
                    break;
                case "Mudança de função":
                    checkMudanca.setChecked(true);
                    break;
                default:
                    // Caso o tipo de exame não corresponda a nenhum CheckBox
                    break;
            }
        }
    }

    private void setStatusExameCheckBox(String tipoStatus) {
        // Primeiro, desmarca ambos os CheckBoxes de status
        checkApto.setChecked(false);
        checkInapto.setChecked(false);

        // Marca o CheckBox correspondente ao status selecionado
        if (tipoStatus != null) {
            switch (tipoStatus) {
                case "Apto":
                    checkApto.setChecked(true);
                    break;
                case "Inapto":
                    checkInapto.setChecked(true);
                    break;
                default:
                    // Caso o status não corresponda a nenhum CheckBox
                    break;
            }
        }
    }

    public Map<String, String> checkBox() {
        Map<String, String> result = new HashMap<>();

        String texto = "";
        String status = "";

        if (checkAdmissional.isChecked()) {
            texto = "Admissional";
            uncheckOthers(checkAdmissional);
        } else if (checkPeriodico.isChecked()) {
            texto = "Periódico";
            uncheckOthers(checkPeriodico);
        } else if (checkRetorno.isChecked()) {
            texto = "Retorno de trabalho";
            uncheckOthers(checkRetorno);
        } else if (checkDemissional.isChecked()) {
            texto = "Demissional";
            uncheckOthers(checkDemissional);
        } else if (checkMudanca.isChecked()) {
            texto = "Mudança de função";
            uncheckOthers(checkMudanca);
        }

        if (checkApto.isChecked()) {
            status = "Apto";
            checkInapto.setChecked(false);
        } else if (checkInapto.isChecked()) {
            status = "Inapto";
            checkApto.setChecked(false);
        }

        result.put ("texto", texto);
        result.put("status", status);

        return result;
    }


    private void uncheckOthers(CheckBox selectedCheckBox) {
        if (selectedCheckBox != checkAdmissional) checkAdmissional.setChecked(false);
        if (selectedCheckBox != checkPeriodico) checkPeriodico.setChecked(false);
        if (selectedCheckBox != checkRetorno) checkRetorno.setChecked(false);
        if (selectedCheckBox != checkDemissional) checkDemissional.setChecked(false);
        if (selectedCheckBox != checkMudanca) checkMudanca.setChecked(false);
    }

    private void setupCheckBoxListeners() {
        // Listener para os exames
        CheckBox.OnCheckedChangeListener examCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                uncheckOthers((CheckBox) buttonView);
            }
        };

        // Configura o listener para cada checkbox de exame
        checkAdmissional.setOnCheckedChangeListener(examCheckedChangeListener);
        checkPeriodico.setOnCheckedChangeListener(examCheckedChangeListener);
        checkRetorno.setOnCheckedChangeListener(examCheckedChangeListener);
        checkDemissional.setOnCheckedChangeListener(examCheckedChangeListener);
        checkMudanca.setOnCheckedChangeListener(examCheckedChangeListener);

        // Listener para os checkboxes de status
        checkApto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkInapto.setChecked(false);
            }
        });

        checkInapto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkApto.setChecked(false);
            }
        });
    }


    //-----------fim metodos checkBox-----------------------------------------

     private void inicializarComponentes() {

        nomeExame = findViewById(R.id.editTextNomeEntrega);
        dataEntrega = findViewById(R.id.editDataEntrega);
        dataProximaEntrega = findViewById(R.id.editDataRetornoExame);
        checkAdmissional = findViewById(R.id.checkBoxAdmissional);
        checkPeriodico = findViewById(R.id.checkBoxPeriodico);
        checkRetorno = findViewById(R.id.checkBoxRetorno);
        checkMudanca = findViewById(R.id.checkBoxMudança);
        checkDemissional = findViewById(R.id.checkBoxDemissional);
        checkApto = findViewById(R.id.checkBoxApto);
        checkInapto = findViewById(R.id.checkBoxInapto);
        listVdadosEntregaExames= findViewById(R.id.listTotalExames);
        listVdadosFuncionario= findViewById(R.id.listFuncionarioExames);
        botaoSalvarEntregaEntregaExames= findViewById(R.id.buttonSalvarExame);
        botaoEditarEntregaExames = findViewById(R.id.buttonAtualizarExame);
        botaoExcluirEntregaExames = findViewById(R.id.buttonExcluirExame);
        botaoLimparEntregaExames = findViewById(R.id.buttonLimparExame);
         editNomePesquisa = findViewById(R.id.editNomePesquisarExame);
         imageVoltar = findViewById(R.id.imageBack);

    }

    public  void voltarMain(View v){
        Intent intent = new Intent(MainActivity.this, ListagemExameActivity.class);
        startActivity(intent);
    }


    public void inicializarFirebase(){

        firebaseDatabaseFuncionario    =FirebaseDatabase.getInstance();
        if (FirebaseApp.getApps(this).size() == 0)
            firebaseDatabaseFuncionario.setPersistenceEnabled(true);
        funcionarioRef =firebaseDatabaseFuncionario.getReference();

        firebaseDatabaseEntregaExames=FirebaseDatabase.getInstance();
        if (FirebaseApp.getApps(this).size() == 0)
            firebaseDatabaseEntregaExames.setPersistenceEnabled(true);
        entregaExameRef= firebaseDatabaseEntregaExames .getReference();
        FirebaseApp.initializeApp(MainActivity.this);




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sair) {
            deslogarUsuario();
            finish();
        } else if (id == R.id.cadastroFunc) {
            Intent intent = new Intent(MainActivity.this, CadastroFuncionarioActivity.class);

            startActivity(intent);
            finish();

        }else if(id ==R.id.relatorio){
            Intent intent = new Intent(MainActivity.this, ListagemExameActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    public void deslogarUsuario(){

try{
autenticacao.signOut();
}catch (Exception e){
    e.printStackTrace();
}


}




    private void limparCampos() {

        nomeExame.setText("");
        dataEntrega.setText("");
        dataProximaEntrega.setText("");
        checkAdmissional.setChecked(false);
       checkPeriodico.setChecked(false);
       checkRetorno.setChecked(false);
        checkDemissional.setChecked(false);
        checkMudanca.setChecked(false);
        checkApto.setChecked(false);
        checkInapto.setChecked(false);

        editNomePesquisa.setText("");
        listVdadosFuncionario.setVisibility(View.INVISIBLE);
        listVdadosEntregaExames.setVisibility(View.INVISIBLE);





    }

    public void limparCamposBotaoNovo(View v) {

        nomeExame.setText("");
        dataEntrega.setText("");
        dataProximaEntrega.setText("");
        checkAdmissional.setChecked(false);
        checkPeriodico.setChecked(false);
        checkRetorno.setChecked(false);
        checkDemissional.setChecked(false);
        checkMudanca.setChecked(false);
        checkApto.setChecked(false);
        checkInapto.setChecked(false);

        editNomePesquisa.setText("");
        listVdadosFuncionario.setVisibility(View.INVISIBLE);
        listVdadosEntregaExames.setVisibility(View.INVISIBLE);
        botaoSalvarEntregaEntregaExames.setVisibility(View.VISIBLE);
        botaoExcluirEntregaExames.setVisibility(View.INVISIBLE);
        botaoEditarEntregaExames.setVisibility(View.INVISIBLE);





    }


    public  void dataProximaTroca(){
        dataProximaEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                String format = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                                Date date;

                                try {
                                    date = sdf.parse(sdf.format(calendar.getTime()));
                                    String dayS = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                                    String monthS = new SimpleDateFormat("MM", Locale.ENGLISH).format(date);
                                    String yearS = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);

                                   dataProximaEntrega.setText((dayS + "/" + monthS + "/" + yearS));
                                } catch (ParseException ignored) {

                                }
                            }
                        }, year, month, day);
                datePickerDialog.show();
                datePickerDialog.getDatePicker();
            }


        });


        String dataProximaEntregas  = dataProximaEntrega.getText().toString();

    }


    public  void dataEntrega(){
       dataEntrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                String format = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                                Date date;

                                try {
                                    date = sdf.parse(sdf.format(calendar.getTime()));
                                    String dayS = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                                    String monthS = new SimpleDateFormat("MM", Locale.ENGLISH).format(date);
                                    String yearS = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);

                                    dataEntrega.setText((dayS + "/" + monthS + "/" + yearS));
                                } catch (ParseException ignored) {

                                }
                            }
                        }, year, month, day);
                datePickerDialog.show();
                datePickerDialog.getDatePicker();
            }


        });


        String dataEntreg  = dataEntrega.getText().toString();

    }


}