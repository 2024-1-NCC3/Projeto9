package br.feacp.nippo_agenda.models;

public class Consulta {
    private String medicoNome;
    private String especialidadeNome;
    private String data;
    private boolean foiAtendido;

    // Construtor
    public Consulta(String medicoNome, String especialidadeNome, String data, boolean foiAtendido) {
        this.medicoNome = medicoNome;
        this.especialidadeNome = especialidadeNome;
        this.data = data;
        this.foiAtendido = foiAtendido;
    }
    public Consulta(String medicoNome, String especialidadeNome, String data) {
        this.medicoNome = medicoNome;
        this.especialidadeNome = especialidadeNome;
        this.data = data;
        this.foiAtendido = false; // ou qualquer valor padrão que você deseja definir
    }

    // Getters e Setters
    public String getMedicoNome() {
        return medicoNome;
    }

    public String getEspecialidadeNome() {
        return especialidadeNome;
    }

    public String getData() {
        return data;
    }

    public boolean isFoiAtendido() {
        return foiAtendido;
    }
}

