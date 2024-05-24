package br.feacp.nippo_agenda.service;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.adapter.CustomAutoCompleteAdapter;

public class AgendamentoService {

    private final Context context;
    private final RequestQueue requestQueue;

    public AgendamentoService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void carregarEspecialidades(AutoCompleteTextView autoCompleteEspecialidade) {
        String url = "https://xjhck8-3001.csb.app/especialidades";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> especialidades = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject especialidade = response.getJSONObject(i);
                                especialidades.add(especialidade.getString("nome"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(
                                context,
                                android.R.layout.simple_spinner_item,
                                especialidades
                        );
                        autoCompleteEspecialidade.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Erro ao carregar especialidades", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(request);
    }

    public void getMedicosPorEspecialidade(String especialidade, AutoCompleteTextView autoCompleteMedico) {
        String url = "https://xjhck8-3001.csb.app/medicos?especialidade=" + especialidade;
        List<String> medicos = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject medico = response.getJSONObject(i);
                            medicos.add(medico.getString("nome"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, medicos);
                    autoCompleteMedico.setAdapter(adapterMedicos);
                },
                error -> Toast.makeText(context, "Erro ao carregar médicos", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    public void getHorariosPorMedico(String medico, AutoCompleteTextView autoCompleteData) {
        String url = "https://xjhck8-3001.csb.app/horarios?medico=" + medico;
        List<String> horarios = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject horario = response.getJSONObject(i);
                            horarios.add(horario.getString("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapterHorarios = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, horarios);
                    autoCompleteData.setAdapter(adapterHorarios);
                },
                error -> Toast.makeText(context, "Erro ao carregar horários", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    public void salvarAgendamento(String userId, String medicoId, String data) {
        String url = "https://xjhck8-3001.csb.app/agendamentos";

        JSONObject agendamento = new JSONObject();
        try {
            agendamento.put("usuario_id", userId); // Certifique-se de que os nomes dos campos estão corretos
            agendamento.put("medico_id", medicoId); // Certifique-se de que os nomes dos campos estão corretos
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
