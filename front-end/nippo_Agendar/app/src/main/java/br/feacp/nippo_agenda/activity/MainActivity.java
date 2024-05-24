package br.feacp.nippo_agenda.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.feacp.nippo_agenda.R;

import br.feacp.nippo_agenda.utils.BottomNavigationUtil;


public class MainActivity extends AppCompatActivity {
    private ImageView tipsImageView;
    private int[] imageArray = {R.drawable.image1, R.drawable.image2, R.drawable.image3};
    private int currentImageIndex = 0;
    private Handler handler = new Handler();

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
}
