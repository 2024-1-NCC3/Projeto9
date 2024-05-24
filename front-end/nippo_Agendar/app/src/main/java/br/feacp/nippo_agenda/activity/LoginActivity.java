package br.feacp.nippo_agenda.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.interfaces.AuthenticationCallback;
import br.feacp.nippo_agenda.service.LoginService;
import br.feacp.nippo_agenda.utils.Validador;


/**
 * Activity responsável pelo processo de login do usuário.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText senhaEditText;
    private Button buttonLogin;
    private Button buttonCadastro;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Inicializa as visualizações e define os listeners dos botões.
     */
    private void initializeViews() {
        emailEditText = findViewById(R.id.inputEmailLogin);
        senhaEditText = findViewById(R.id.inputSenhaLogin);
        buttonLogin = findViewById(R.id.btnEntrar);
        buttonCadastro = findViewById(R.id.buttonCadastro);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        buttonCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSignUp();
            }
        });
    }

    /**
     * Realiza o processo de login do usuário.
     */
    private void realizarLogin() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        // Valida os campos de email e senha
        if (!validarCampos(email, senha)) {
            return;
        }

        // Exibe a ProgressBar para indicar o processo de login em andamento
        progressBar.setVisibility(View.VISIBLE);

        // Serviço de login do usuário
        LoginService loginService = new LoginService(this);
        loginService.loginUser(email, senha, new AuthenticationCallback() {

            @Override
            public void onSuccess(String response, String idUsuario) {
                progressBar.setVisibility(View.INVISIBLE);
                if (response.equals("Login bem-sucedido")) {
                    // Salvar o ID do usuário localmente
                    salvarIdUsuarioLocalmente(idUsuario);
                    // Abrir a tela de perfil
                    abrirPerfil();
                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Valida os campos de email e senha.
     * @param email O email inserido pelo usuário.
     * @param senha A senha inserida pelo usuário.
     * @return true se os campos são válidos, false caso contrário.
     */
    private boolean validarCampos(String email, String senha) {
        boolean isValid = true;

        if (!Validador.validarCampoObrigatorio(findViewById(R.id.emailLogin), emailEditText, "E-mail") ||
                !Validador.validarCampoObrigatorio(findViewById(R.id.senhaLogin), senhaEditText, "Senha") ||
                !Validador.validarFormatoEmail(findViewById(R.id.emailLogin), emailEditText)) {
            isValid = false;
        }

        return isValid;
    }

    /**
     * Abre a tela de cadastro de novo usuário.
     */
    private void abrirSignUp() {
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    /**
     * Abre a tela de home  após o login bem-sucedido.
     */
    private void abrirPerfil() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Método para salvar o ID do usuário localmente.
     * @param idUsuario O ID do usuário.
     */
    private void salvarIdUsuarioLocalmente(String idUsuario) {
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", idUsuario);
        editor.apply();
    }
}
