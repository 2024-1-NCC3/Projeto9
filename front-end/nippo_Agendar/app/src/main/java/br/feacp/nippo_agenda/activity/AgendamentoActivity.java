package br.feacp.nippo_agenda.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.utils.SharedPreferencesManager;

public class AgendamentoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteEspecialidade;
    private AutoCompleteTextView autoCompleteMedico;
    private AutoCompleteTextView autoCompleteData;
    private Button btnagendar;
    private Button btncancelar;
    private RequestQueue requestQueue;
    private String userId;
    private Context context;

    private Map<String, Integer> especialidadeMap = new HashMap<>();
    private Map<String, Integer> medicoMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        context = this;

        autoCompleteEspecialidade = findViewById(R.id.autoCompleteEspecialidade);
        autoCompleteMedico = findViewById(R.id.autoCompleteMedico);
        autoCompleteData = findViewById(R.id.autoCompleteData);
        btnagendar = findViewById(R.id.btnagendar);
        btncancelar = findViewById(R.id.btncancelar);

        requestQueue = Volley.newRequestQueue(this);

        userId = SharedPreferencesManager.getUserId(this);
        if (userId == null) {
            Toast.makeText(this, "ID do usuário não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEspecialidades();

        btncancelar.setOnClickListener(v -> {
            Intent intent = new Intent(AgendamentoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnagendar.setOnClickListener(v -> {
            if (camposPreenchidos()) {
                agendarConsulta();
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean camposPreenchidos() {
        return !autoCompleteEspecialidade.getText().toString().isEmpty() &&
                !autoCompleteMedico.getText().toString().isEmpty() &&
                !autoCompleteData.getText().toString().isEmpty();
    }

    private void loadEspecialidades() {
        String url = "https://xjhck8-3001.csb.app/especialidades";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> especialidades = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String nomeEspecialidade = jsonObject.getString("nome");
                                int idEspecialidade = jsonObject.getInt("id");
                                especialidades.add(nomeEspecialidade);
                                especialidadeMap.put(nomeEspecialidade, idEspecialidade);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AgendamentoActivity.this, android.R.layout.simple_dropdown_item_1line, especialidades);
                        autoCompleteEspecialidade.setAdapter(adapter);

                        autoCompleteEspecialidade.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedEspecialidade = adapter.getItem(position);
                            Integer especialidadeId = especialidadeMap.get(selectedEspecialidade);
                            if (especialidadeId != null) {
                                autoCompleteMedico.setText("");
                                autoCompleteData.setText("");

                                autoCompleteMedico.setAdapter(null);
                                autoCompleteData.setAdapter(null);

                                loadMedicos(especialidadeId);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(AgendamentoActivity.this, "Erro ao carregar especialidades", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void loadMedicos(int especialidadeId) {
        String url = "https://xjhck8-3001.csb.app/medicos/" + especialidadeId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> medicos = new ArrayList<>();
                        medicoMap.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String nomeMedico = jsonObject.getString("nome");
                                int idMedico = jsonObject.getInt("id");
                                medicos.add(nomeMedico);
                                medicoMap.put(nomeMedico, idMedico);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AgendamentoActivity.this, android.R.layout.simple_dropdown_item_1line, medicos);
                        autoCompleteMedico.setAdapter(adapter);

                        autoCompleteMedico.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedMedico = adapter.getItem(position);
                            Integer medicoId = medicoMap.get(selectedMedico);
                            if (medicoId != null) {
                                loadDatas(medicoId);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(AgendamentoActivity.this, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void loadDatas(int medicoId) {
        String url = "https://xjhck8-3001.csb.app/horarios/" + medicoId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    ArrayList<String> datas = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            if (jsonObject.has("data") && jsonObject.has("hora")) {
                                String dataHorario = jsonObject.getString("data") + " " + jsonObject.getString("hora");
                                datas.add(dataHorario);
                            } else {
                                Log.e("AgendamentoActivity", "Objeto JSON inválido encontrado: " + jsonObject.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("AgendamentoActivity", "Erro ao analisar horário JSON: " + e.getMessage());
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AgendamentoActivity.this, android.R.layout.simple_dropdown_item_1line, datas);
                    autoCompleteData.setAdapter(adapter);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(AgendamentoActivity.this, "Erro ao carregar datas", Toast.LENGTH_SHORT).show();
                    Log.e("AgendamentoActivity", "Erro ao carregar datas: " + error.getMessage());
                }
        );

        requestQueue.add(jsonArrayRequest);
    }


    private void agendarConsulta() {
        String url = "https://xjhck8-3001.csb.app/agendamentos";

        String selectedMedico = autoCompleteMedico.getText().toString();
        Integer medicoId = medicoMap.get(selectedMedico);
        String data = autoCompleteData.getText().toString();

        if (medicoId == null || data.isEmpty()) {
            Toast.makeText(context, "Erro ao obter dados do médico ou data", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject agendamento = new JSONObject();
        try {
            agendamento.put("usuario_id", userId);
            agendamento.put("medico_id", medicoId);
            agendamento.put("data", data);
            Log.d("AgendamentoActivity", "Dados do agendamento: " + agendamento.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                agendamento,
                response -> {
                    Toast.makeText(context, "Agendamento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    Log.d("AgendamentoActivity", "Resposta do servidor: " + response.toString());
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Erro ao salvar agendamento: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AgendamentoActivity", "Erro na solicitação POST: " + error.getMessage());
                    if (error.networkResponse != null) {
                        Log.e("AgendamentoActivity", "Código de resposta: " + error.networkResponse.statusCode);
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("AgendamentoActivity", "Resposta do servidor: " + responseBody);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        requestQueue.add(request);
    }

}

