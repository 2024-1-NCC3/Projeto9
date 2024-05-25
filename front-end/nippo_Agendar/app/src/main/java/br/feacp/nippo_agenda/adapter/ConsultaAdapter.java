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

        // Verifica se a consulta foi confirmada
        if (consulta.isFoiAtendido()) {
            // Define o status da consulta confirmada
            holder.statusTextView.setText("Consulta Confirmada");
            holder.statusTextView.setVisibility(View.VISIBLE);

            // Oculta os botões de confirmação e cancelamento
            holder.confirmaPresencaButton.setVisibility(View.GONE);
            holder.cancelaConsultaButton.setVisibility(View.GONE);
        } else {
            holder.statusTextView.setVisibility(View.GONE);

            // Configura o clique do botão para confirmar a consulta
            holder.confirmaPresencaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Atualiza o status da consulta
                    consulta.setFoiAtendido(true);
                    // Define o status da consulta confirmada
                    holder.statusTextView.setText("Consulta Confirmada");
                    holder.statusTextView.setVisibility(View.VISIBLE);
                    // Oculta os botões de confirmação e cancelamento
                    holder.confirmaPresencaButton.setVisibility(View.GONE);
                    holder.cancelaConsultaButton.setVisibility(View.GONE);
                }
            });

            // Configura o clique do botão para cancelar a consulta
            holder.cancelaConsultaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Atualiza o status da consulta
                    consulta.setFoiAtendido(true);
                    // Define o status da consulta cancelada
                    holder.statusTextView.setText("Consulta Cancelada");
                    holder.statusTextView.setVisibility(View.VISIBLE);
                    // Oculta os botões de confirmação e cancelamento
                    holder.confirmaPresencaButton.setVisibility(View.GONE);
                    holder.cancelaConsultaButton.setVisibility(View.GONE);
                }
            });

            // Exibe os botões de confirmação e cancelamento
            holder.confirmaPresencaButton.setVisibility(View.VISIBLE);
            holder.cancelaConsultaButton.setVisibility(View.VISIBLE);
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
        TextView statusTextView;
        Button confirmaPresencaButton;
        Button cancelaConsultaButton;

        public ConsultaViewHolder(@NonNull View itemView) {
            super(itemView);
            medicoNomeTextView = itemView.findViewById(R.id.medicoNomeTextView);
            especialidadeNomeTextView = itemView.findViewById(R.id.especialidadeNomeTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            confirmaPresencaButton = itemView.findViewById(R.id.confirmaPresencaButton);
            cancelaConsultaButton = itemView.findViewById(R.id.cancelaConsultaButton);
        }
    }
}
