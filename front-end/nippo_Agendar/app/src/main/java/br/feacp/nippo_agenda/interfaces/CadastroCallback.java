package br.feacp.nippo_agenda.interfaces;

/**
 * Interface para lidar com o resultado de uma operação de cadastro.
 */
public interface CadastroCallback {

    /**
     * Método chamado quando a operação de cadastro é bem-sucedida.
     * @param message A mensagem indicando o sucesso da operação.
     */
    void onSuccess(String message);

    /**
     * Método chamado quando ocorre um erro durante a operação de cadastro.
     * @param error O erro ocorrido durante a operação de cadastro.
     */
    void onError(String error);
}