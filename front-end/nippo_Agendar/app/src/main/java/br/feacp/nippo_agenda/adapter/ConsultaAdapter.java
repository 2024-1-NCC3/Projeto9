package br.feacp.nippo_agenda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.feacp.nippo_agenda.R;
import br.feacp.nippo_agenda.models.Consulta;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder> {

    private List<Consulta> consultaList;
    private Context context;

    // Construtor
    public ConsultaAdapter(List<Consulta> consultaList, Context context) {
        this.consultaList = consultaList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consulta, parent, false);
        return new ConsultaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        Consulta consulta = consultaList.get(position);

        holder.medicoNomeTextView.setText(consulta.getMedicoNome());
        holder.especialidadeNomeTextView.setText(consulta.getEspecialidadeNome());
        holder.dataTextView.setText(consulta.getData());

        if (!consulta.isFoiAtendido()) {
            // Se a consulta não foi atendida, exibe os botões de confirmação e cancelamento
            holder.confirmaPresencaButton.setVisibility(View.VISIBLE);
            holder.cancelaConsultaButton.setVisibility(View.VISIBLE);
        } else {
            // Caso contrário, oculta os botões
            holder.confirmaPresencaButton.setVisibility(View.GONE);
            holder.cancelaConsultaButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return consultaList.size();
    }

    public class ConsultaViewHolder extends RecyclerView.ViewHolder {
        TextView medicoNomeTextView;
        TextView especialidadeNomeTextView;
        TextView dataTextView;
        Button confirmaPresencaButton;
        Button cancelaConsultaButton;

        public ConsultaViewHolder(@NonNull View itemView) {
            super(itemView);
            medicoNomeTextView = itemView.findViewById(R.id.medicoNomeTextView);
            especialidadeNomeTextView = itemView.findViewById(R.id.especialidadeNomeTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
            confirmaPresencaButton = itemView.findViewById(R.id.confirmaPresencaButton);
            cancelaConsultaButton = itemView.findViewById(R.id.cancelaConsultaButton);
        }
    }
}
