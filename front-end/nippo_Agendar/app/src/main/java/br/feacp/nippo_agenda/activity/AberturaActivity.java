package br.feacp.nippo_agenda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import br.feacp.nippo_agenda.R;



/**
 * Activity para exibir a tela de abertura do aplicativo.
 */
public class AberturaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abertura);

        // Cria um novo Handler para agendar uma tarefa para ser executada após um intervalo de tempo
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Cria uma Intent para iniciar a LoginActivity
                Intent intent = new Intent(AberturaActivity.this, LoginActivity.class);
                // Inicia a LoginActivity
                startActivity(intent);
                // Finaliza a AberturaActivity para que ela não possa ser acessada pressionando o botão de voltar
                finish();
            }
        }, 3000); // Delay de 3000 milissegundos (3 segundos)
    }
}
