package br.feacp.nippo_agenda.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.adapter.ConsultaAdapter;
import br.feacp.nippo_agenda.models.Consulta;
import br.feacp.nippo_agenda.utils.SharedPreferencesManager;

public class HistoricoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConsultaAdapter consultaAdapter;
    private List<Consulta> consultaList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_consultas);

        recyclerView = findViewById(R.id.recyclerViewConsultas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        consultaList = new ArrayList<>();
        consultaAdapter = new ConsultaAdapter(consultaList, this);
        recyclerView.setAdapter(consultaAdapter);

        requestQueue = Volley.newRequestQueue(this);

        // Obtenha o ID do usuário do SharedPreferencesManager
        String usuarioId = SharedPreferencesManager.getUserId(this);

        // Verifique se o ID do usuário não está vazio
        if (usuarioId != null) {
            // URL do endpoint para buscar as consultas do usuário
            String url = "hhttps://xjhck8-3001.csb.app/agendamentos/" + usuarioId;

            // Crie uma solicitação GET para buscar as consultas do usuário
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                // Limpe a lista de consultas
                                consultaList.clear();
                                // Preencha a lista com as consultas recebidas da resposta JSON
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String medicoNome = jsonObject.getString("medico_nome");
                                    String especialidadeNome = jsonObject.getString("especialidade_nome");
                                    String data = jsonObject.getString("data");
                                    consultaList.add(new Consulta(medicoNome, especialidadeNome, data));
                                }
                                // Notifique o adapter sobre as mudanças nos dados
                                consultaAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Lide com erros de solicitação
                            Toast.makeText(HistoricoActivity.this, "Erro ao buscar consultas", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            // Adicione a solicitação à fila de solicitações
            requestQueue.add(jsonArrayRequest);
        } else {
            // Se o ID do usuário estiver vazio, exiba uma mensagem de erro
            Toast.makeText(this, "ID do usuário não encontrado", Toast.LENGTH_SHORT).show();
        }
    }
}
