package br.feacp.nippo_agenda.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Map;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.utils.BottomNavigationUtil;


/**
 * Activity para exibir e atualizar o perfil do usuário.
 */
public class PerfilActivity extends AppCompatActivity {

    private EditText inputNome, inputEmail, inputTelefone, inputCPF;
    private Button btnAtualizarPerfil;
    private String idUsuario;

    private Map<Integer, Class<?>> navigationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volta para MainActivity
                finish();
            }
        });


        // Inicializa os componentes da interface do usuário
        inputNome = findViewById(R.id.inputNomePerfil);
        inputEmail = findViewById(R.id.inputEmailPerfil);
        inputTelefone = findViewById(R.id.inputTelefonePerfil);
        inputCPF = findViewById(R.id.inputCPFPerfil);
        btnAtualizarPerfil = findViewById(R.id.btnAtualizarPerfil);

        // Obtém o ID do usuário logado
        idUsuario = getIdUsuarioLogado();

        // Busca os dados do perfil do usuário e os exibe na interface
        buscarDadosDoPerfil(idUsuario);

        // Configura o botão de atualizar perfil para chamar a função de confirmação
        btnAtualizarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarAtualizacao();
            }
        });

        // Configure o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationUtil.setupBottomNavigationView(this, bottomNavigationView);

    }

    /**
     * Obtém o ID do usuário logado a partir das preferências compartilhadas.
     *
     * @return O ID do usuário logado.
     */
    private String getIdUsuarioLogado() {
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        return preferences.getString("id", "");
    }

    /**
     * Busca os dados do perfil do usuário no servidor.
     *
     * @param idUsuario O ID do usuário.
     */
    private void buscarDadosDoPerfil(String idUsuario) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://xjhck8-3001.csb.app/perfil/" + idUsuario;

        // Faz uma requisição GET para obter os dados do perfil do usuário
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Atualiza a interface com os dados do perfil recebidos
                        atualizarInterfaceComDadosDoPerfil(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }

    /**
     * Atualiza a interface com os dados do perfil do usuário.
     *
     * @param perfil O objeto JSON contendo os dados do perfil.
     */
    private void atualizarInterfaceComDadosDoPerfil(JSONObject perfil) {
        try {
            // Preenche os campos da interface com os dados do perfil
            inputNome.setText(perfil.getString("nome"));
            inputEmail.setText(perfil.getString("email"));
            inputTelefone.setText(perfil.getString("telefone"));
            inputCPF.setText(perfil.getString("cpf"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exibe um diálogo de confirmação antes de atualizar o perfil do usuário.
     */
    private void confirmarAtualizacao() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Atualização")
                .setMessage("Deseja salvar as alterações?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Se o usuário confirmar, chama a função para atualizar o perfil no servidor
                        atualizarPerfilNoServidor();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    /**
     * Envia uma requisição PUT para atualizar o perfil do usuário no servidor.
     */
    private void atualizarPerfilNoServidor() {
        // Obtém os novos dados do perfil da interface
        String nome = inputNome.getText().toString();
        String email = inputEmail.getText().toString();
        String telefone = inputTelefone.getText().toString();
        String cpf = inputCPF.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://xjhck8-3001.csb.app/usuarios/" + idUsuario;

        // Cria o objeto JSON com os novos dados do perfil
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nome", nome);
            jsonBody.put("email", email);
            jsonBody.put("telefone", telefone);
            jsonBody.put("cpf", cpf);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Cria a requisição PUT para atualizar o perfil
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Exibe uma mensagem de sucesso ao usuário
                        Toast.makeText(PerfilActivity.this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Exibe uma mensagem de erro ao usuário em caso de falha
                        Toast.makeText(PerfilActivity.this, "Erro ao atualizar perfil: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                // Converte o objeto JSON em bytes para ser enviado no corpo da requisição
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                // Define o tipo de conteúdo do corpo da requisição como JSON
                return "application/json; charset=utf-8";
            }
        };

        queue.add(putRequest);
    }
}
