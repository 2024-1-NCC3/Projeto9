package br.feacp.nippo_agenda.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

import br.feacp.nippo_agenda.interfaces.CadastroCallback;
import br.feacp.nippo_agenda.models.Usuario;

/**
 * Classe que gerencia o cadastro de usuários.
 */
public class CadastroService {
    private RequestQueue requestQueue;
    private Context context;

    /**
     * Construtor da classe CadastroService.
     * @param context O contexto da aplicação.
     */
    public CadastroService(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Método para cadastrar um usuário no servidor.
     * @param usuario O objeto de usuário a ser cadastrado.
     * @param callback O objeto de callback para lidar com o resultado do cadastro.
     */
    public void cadastrarUsuario(Usuario usuario, CadastroCallback callback) {
        String url = "https://xjhck8-3001.csb.app/cadastro";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", usuario.getNome());
                params.put("email", usuario.getEmail());
                params.put("senha", usuario.getSenha());
                params.put("telefone", usuario.getTelefone());
                params.put("cpf", usuario.getCpf());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
