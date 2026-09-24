package com.lima.medicinatrabalho.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.lima.epimanager.helper.exibirMensagem
import com.lima.epimanager.model.Usuario
import com.lima.medicinatrabalho.databinding.ActivityCadastroUsuarioBinding


class CadastroUsuarioActivity : AppCompatActivity() {

    private val binding by lazy {
       ActivityCadastroUsuarioBinding.inflate(layoutInflater)
    }

    private  lateinit var nome: String
    private  lateinit var email: String
    private  lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        inicializarToolbar()
        inicializarEventosClique()

    }

    private fun inicializarEventosClique() {

        binding.btnCadastar.setOnClickListener {

            if(validarCampos()){
                cadastrarUsuario(nome,email,senha)
            }
        }




    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {

        firebaseAuth.createUserWithEmailAndPassword(
            email,senha
        ).addOnCompleteListener {resultado->

            if(resultado.isSuccessful){
                //salvando usuario no firestore
                val idusuario = resultado.result.user?.uid
                if(idusuario !=null){
                    val usuario = Usuario(
                        idusuario,nome,email,senha
                    )
                    salvarUsuarioFirestore(usuario)

                }


            }
        }.addOnFailureListener {erro->

            try {
                throw erro
            }catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                exibirMensagem("E-mail inválido")
            }catch (errousuarioExistente: FirebaseAuthUserCollisionException){
                exibirMensagem("Já existe uma conta com esse e-mail")
            }
            catch (errousuarioSenhaFraca: FirebaseAuthWeakPasswordException){
                exibirMensagem("Senha muito fraca")
            }
        }

    }

    private fun salvarUsuarioFirestore(usuario: Usuario) {

         firestore
             .collection("usuarios")
             .document(usuario.id)
             .set(usuario)
             .addOnSuccessListener {
                 exibirMensagem("Cadastro realizado!")
                 startActivity(
                     Intent(applicationContext,ListagemExameActivity::class.java)

                 )
                 finish()
             }.addOnFailureListener{
                 exibirMensagem("Ocorreu um erro ao realizar o cadastro")

             }

    }


    @SuppressLint("SuspiciousIndentation")
    private fun validarCampos(): Boolean {


        nome = binding.editNome.text.toString();
        email = binding.editEmail.text.toString();
        senha = binding.editSenha.text.toString();


        if(nome.isNotEmpty()){
            binding.textLayoutNome.error = null
              if(email.isNotEmpty()){
                  binding.textLayoutEmail.error = null
                  if(senha.isNotEmpty()){
                       binding.textILayoutSenha.error = null


                      return  true


                  }else{
                      binding.textILayoutSenha.error = "Preencha a sua senha"
                      return  false
                  }

              }else{
                  binding.textLayoutEmail.error = "Preencha o seu e-mail"
                  return  false
              }

        }else{
            binding.textLayoutNome.error = "Preencha o seu nome"
           return  false
        }

    }


    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onStart() {
        super.onStart()
        val usuarioAtual: FirebaseUser? = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            ListagemExameActivity()
        }
    }



}