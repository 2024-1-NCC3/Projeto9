package br.feacp.nippo_agenda.models;

/**
 * Classe que representa um usuário.
 */
public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;

    /**
     * Construtor vazio da classe Usuario.
     */
    public Usuario() {
    }

    /**
     * Construtor da classe Usuario.
     * @param nome O nome do usuário.
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @param telefone O telefone do usuário.
     * @param cpf O CPF do usuário.
     */
    public Usuario(String nome, String email, String senha, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf = cpf;
    }

    /**
     * Método para obter o nome do usuário.
     * @return O nome do usuário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Método para definir o nome do usuário.
     * @param nome O nome do usuário.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Método para obter o email do usuário.
     * @return O email do usuário.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Método para definir o email do usuário.
     * @param email O email do usuário.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Método para obter a senha do usuário.
     * @return A senha do usuário.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Método para definir a senha do usuário.
     * @param senha A senha do usuário.
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Método para obter o telefone do usuário.
     * @return O telefone do usuário.
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Método para definir o telefone do usuário.
     * @param telefone O telefone do usuário.
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Método para obter o CPF do usuário.
     * @return O CPF do usuário.
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Método para definir o CPF do usuário.
     * @param cpf O CPF do usuário.
     */
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
