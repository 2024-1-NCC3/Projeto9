package br.feacp.nippo_agenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import br.feacp.nippo_agenda.R;

import br.feacp.nippo_agenda.interfaces.CadastroCallback;
import br.feacp.nippo_agenda.models.Usuario;
import br.feacp.nippo_agenda.service.CadastroService;

import br.feacp.nippo_agenda.utils.Validador;


/**
 * Activity responsável pelo processo de cadastro de novos usuários.
 */
public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editTextNome;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextSenha;
    private TextInputEditText editTextTelefone;
    private TextInputEditText editTextCpf;
    private TextInputLayout inputLayoutNome;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutSenha;
    private TextInputLayout inputLayoutTelefone;
    private TextInputLayout inputLayoutCpf;
    private Button buttonCadastrar;
    private Button buttonLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Inicialização dos TextInputLayouts e TextInputEditTexts
        inputLayoutNome = findViewById(R.id.nomeCadastro);
        inputLayoutEmail = findViewById(R.id.emailCadastro);
        inputLayoutSenha = findViewById(R.id.senhaCadastro);
        inputLayoutTelefone = findViewById(R.id.telefoneCadastro);
        inputLayoutCpf = findViewById(R.id.cpfCadastro);
        editTextNome = findViewById(R.id.inputNomeCadastro);
        editTextEmail = findViewById(R.id.inputEmailCadastro);
        editTextSenha = findViewById(R.id.inputSenhaCadastro);
        editTextTelefone = findViewById(R.id.inputTelefoneCadastro);
        editTextCpf = findViewById(R.id.inputCpfCadastro);

        buttonCadastrar = findViewById(R.id.btnCadastro);
        buttonLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBarCadastro);

        // Define o OnClickListener para o botão de cadastro
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario();
            }
        });

        // Define o OnClickListener para o botão de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Método para realizar o cadastro do usuário.
     */
    private void cadastrarUsuario() {
        String nome = editTextNome.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();
        String telefone = editTextTelefone.getText().toString().trim();
        String cpf = editTextCpf.getText().toString().trim();

        // Validação dos campos com os TextInputLayouts
        if (!Validador.validarCampoObrigatorio(inputLayoutNome, editTextNome, "Nome") ||
                !Validador.validarCampoObrigatorio(inputLayoutCpf, editTextCpf, "CPF") ||
                !Validador.validarCampoObrigatorio(inputLayoutEmail, editTextEmail, "E-mail") ||
                !Validador.validarCampoObrigatorio(inputLayoutTelefone, editTextTelefone, "Telefone") ||
                !Validador.validarCampoObrigatorio(inputLayoutSenha, editTextSenha, "Senha") ||
                !Validador.validarFormatoEmail(inputLayoutEmail, editTextEmail) ||
                !Validador.validarTelefone(inputLayoutTelefone, editTextTelefone) ||
                !Validador.validarSenha(inputLayoutSenha, editTextSenha)) {
            return;
        }

        // Exibe a ProgressBar para indicar o processo de cadastro em andamento
        progressBar.setVisibility(View.VISIBLE);

        // Criar um novo usuário
        Usuario usuario = new Usuario(nome, email, senha, telefone, cpf);

        // Serviço de cadastro de usuário
        CadastroService cadastroService = new CadastroService(this);

        // Cadastro do usuário
        cadastroService.cadastrarUsuario(usuario, new CadastroCallback() {
            @Override
            public void onSuccess(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CadastroActivity.this, message, Toast.LENGTH_SHORT).show();

                // Após o cadastro bem-sucedido, ir para a tela de login
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CadastroActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
