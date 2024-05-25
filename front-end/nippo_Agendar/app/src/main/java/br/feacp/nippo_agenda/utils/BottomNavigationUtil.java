package br.feacp.nippo_agenda.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.activity.AgendamentoActivity;
import br.feacp.nippo_agenda.activity.HistoricoActivity;
import br.feacp.nippo_agenda.activity.MainActivity;
import br.feacp.nippo_agenda.activity.PerfilActivity;

public class BottomNavigationUtil {
    private static final Map<Integer, Class<?>> navigationMap = new HashMap<>();

    static {
        navigationMap.put(R.id.nav_home, MainActivity.class);
        navigationMap.put(R.id.nav_historic, HistoricoActivity.class); // Adicionando a tela de histórico
        navigationMap.put(R.id.nav_agenda, AgendamentoActivity.class);
        navigationMap.put(R.id.nav_user, PerfilActivity.class);
    }

    public static void setupBottomNavigationView(Activity activity, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Class<?> activityClass = navigationMap.get(item.getItemId());
                if (activityClass != null) {
                    if (!activityClass.equals(activity.getClass())) {
                        Intent intent = new Intent(activity, activityClass);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                    return true;
                }
                return false;
            }
        });

        // Marcar o item correto como selecionado
        Integer selectedItemId = null;
        for (Map.Entry<Integer, Class<?>> entry : navigationMap.entrySet()) {
            if (entry.getValue().equals(activity.getClass())) {
                selectedItemId = entry.getKey();
                break;
            }
        }

        if (selectedItemId != null) {
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }
    }
}
