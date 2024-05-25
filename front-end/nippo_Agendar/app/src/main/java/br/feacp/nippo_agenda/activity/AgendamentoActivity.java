package br.feacp.nippo_agenda.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.utils.BottomNavigationUtil;
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

        // Configure o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationUtil.setupBottomNavigationView(this, bottomNavigationView);

        loadEspecialidades();
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volta para MainActivity
                finish();
            }
        });

        btncancelar.setOnClickListener(v -> {
            Intent intent = new Intent(AgendamentoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnagendar.setOnClickListener(v -> {
            if (camposPreenchidos()) {
                showConfirmationDialog(); // Chamar o método para mostrar o dialog de confirmação
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

    private void showConfirmationDialog() {
        // Capturar os valores selecionados
        String especialidade = autoCompleteEspecialidade.getText().toString();
        String medico = autoCompleteMedico.getText().toString();
        String data = autoCompleteData.getText().toString();

        // Construir a mensagem de confirmação
        String mensagem = "Tem certeza de que deseja agendar esta consulta?\n\n" +
                "Especialidade: " + especialidade + "\n" +
                "Médico: " + medico + "\n" +
                "Data: " + data;

        new AlertDialog.Builder(this)
                .setTitle("Confirmar Agendamento")
                .setMessage(mensagem)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Integer medicoId = medicoMap.get(medico);

                        if (medicoId != null) {
                            agendarConsulta(userId, String.valueOf(medicoId), data);
                        } else {
                            Toast.makeText(context, "Médico inválido.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Não", null)
                .show();
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
                                // Formatar a data
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                try {
                                    Date data = inputFormat.parse(dataHorario);
                                    String dataFormatada = outputFormat.format(data);
                                    datas.add(dataFormatada);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.e("AgendamentoActivity", "Erro ao formatar data: " + e.getMessage());
                                }
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

    private void agendarConsulta(String usuarioId, String medicoId, String data) {
        String url = "https://xjhck8-3001.csb.app/agendamentos";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario_id", usuarioId);
            jsonBody.put("medico_id", medicoId);
            jsonBody.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Log.i("AgendamentoActivity", "Resposta do servidor: " + message);
                            Toast.makeText(AgendamentoActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Log.e("AgendamentoActivity", "Erro ao processar resposta JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof ServerError && error.networkResponse.statusCode == 409) {
                            // Consulta já agendada para o mesmo usuário, médico e data
                            Toast.makeText(AgendamentoActivity.this, "Você já possui uma consulta agendada para essa data e médico.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("AgendamentoActivity", "Erro na solicitação POST: " + error.toString());
                        }
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
