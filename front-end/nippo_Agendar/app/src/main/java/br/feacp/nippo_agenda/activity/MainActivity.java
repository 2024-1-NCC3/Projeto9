package br.feacp.nippo_agenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.utils.BottomNavigationUtil;

public class MainActivity extends AppCompatActivity {
    private ImageView tipsImageView;
    private int[] imageArray = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private int currentImageIndex = 0;
    private Handler handler = new Handler();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tipsImageView = findViewById(R.id.tipsImageView);

        // Inicia a troca de imagem automaticamente
        startImageChange();

        // Configure o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavigationUtil.setupBottomNavigationView(this, bottomNavigationView);

        // Inicializa o requestQueue para requisições HTTP
        requestQueue = Volley.newRequestQueue(this);

        // Carrega a última consulta agendada
        loadLastAppointment();

        Button agendarButton = findViewById(R.id.scheduleButton);
        agendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia AgendamentoActivity
                Intent intent = new Intent(MainActivity.this, AgendamentoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startImageChange() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Troca para a próxima imagem
                currentImageIndex = (currentImageIndex + 1) % imageArray.length;
                tipsImageView.setImageResource(imageArray[currentImageIndex]);

                // Agendamento para a próxima troca após 3 segundos
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private void loadLastAppointment() {
        String url = "https://xjhck8-3001.csb.app/ultimaconsulta/userId";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("data")) {
                                String dataConsultaString = response.getString("data");
                                // Formatar a data
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                try {
                                    Date dataConsulta = inputFormat.parse(dataConsultaString);
                                    String dataConsultaFormatted = outputFormat.format(dataConsulta);

                                    TextView nextAppointmentDateTimeTextView = findViewById(R.id.nextAppointmentDateTimeTextView);
                                    nextAppointmentDateTimeTextView.setText(dataConsultaFormatted);
                                } catch (ParseException e) {
                                    Log.e("MainActivity", "Erro ao parsear data: " + e.getMessage());
                                    Toast.makeText(MainActivity.this, "Erro ao processar a data.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Nenhuma consulta encontrada.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("MainActivity", "Erro ao processar resposta JSON: " + e.getMessage());
                            Toast.makeText(MainActivity.this, "Erro ao processar a resposta do servidor.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MainActivity", "Erro na solicitação GET: " + error.toString());
                        Toast.makeText(MainActivity.this, "Erro ao conectar ao servidor. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
