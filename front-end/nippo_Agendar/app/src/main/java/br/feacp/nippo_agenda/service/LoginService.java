package br.feacp.nippo_agenda.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import br.feacp.nippo_agenda.interfaces.AuthenticationCallback;


/**
 * Classe que gerencia a autenticação de usuários.
 */
public class LoginService {
    private Context context;

    /**
     * Construtor da classe LoginService.
     * @param context O contexto da aplicação.
     */
    public LoginService(Context context) {
        this.context = context;
    }

    /**
     * Método para realizar o login do usuário.
     * @param email O e-mail do usuário.
     * @param senha A senha do usuário.
     * @param callback O objeto de callback para lidar com o resultado do login.
     */
    public void loginUser(String email, String senha, AuthenticationCallback callback) {
        String url = "https://xjhck8-3001.csb.app/login";

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if (message.equals("Login bem-sucedido")) {
                                // Obter o ID do usuário da resposta
                                String idUsuario = jsonObject.getString("idUsuario");
                                callback.onSuccess(message, idUsuario);
                            } else if (message.equals("Conta não encontrada")) {
                                callback.onError("Nenhuma conta encontrada com este e-mail. Por favor, crie uma conta.");
                            } else if (message.equals("Senha incorreta")) {
                                callback.onError("Senha incorreta. Por favor, tente novamente.");
                            } else {
                                callback.onError("Erro desconhecido: " + message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError("Erro ao processar a resposta do servidor");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String responseBody = new String(error.networkResponse.data, Charset.forName("UTF-8"));
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String errorMessage = jsonObject.getString("error");
                        if (errorMessage.equals("Conta não encontrada")) {
                            callback.onError("Nenhuma conta encontrada com este e-mail. Por favor, crie uma conta.");
                        } else if (errorMessage.equals("Senha incorreta")) {
                            callback.onError("Senha incorreta. Por favor, tente novamente.");
                        } else {
                            callback.onError("Erro ao fazer login. Por favor, tente novamente mais tarde.");
                        }
                    } catch (JSONException e) {
                        callback.onError("Erro ao processar a resposta do servidor.");
                    }
                } else {
                    callback.onError("Erro ao fazer login. Por favor, verifique sua conexão e tente novamente.");
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("senha", senha);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}
