package com.murilocht.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.murilocht.whatsappclone.R;
import com.murilocht.whatsappclone.config.ConfiguracaoFirebase;
import com.murilocht.whatsappclone.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    public void validarUsuario(View view) {
        String textEmail = campoEmail.getText().toString();
        String textSenha = campoSenha.getText().toString();

        if (!textEmail.isEmpty()) {
            if (!textSenha.isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setEmail(textEmail);
                usuario.setSenha(textSenha);

                logarUsuario(usuario);
            } else {
                Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
        }
    }

    public void logarUsuario(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            abrirTelaPrincipal();
                        } else {
                            String excecao = "";

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                excecao = "Usuário não está cadastrado";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                excecao = "Email e senha não correspondem a um usuário cadastrado";
                            } catch (Exception e) {
                                excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void abrirTelaCadastro(View view) {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();

        if (usuarioAtual != null) {
            abrirTelaPrincipal();
        }
    }
}
