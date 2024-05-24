package br.feacp.nippo_agenda.interfaces;


/**
 * Interface para lidar com o resultado de uma autenticação.
 */
public interface AuthenticationCallback {

    /**
     * Método chamado quando a autenticação é bem-sucedida.
     * @param response A resposta da autenticação.
     * @param idUsuario O ID do usuário autenticado.
     */
    void onSuccess(String response, String idUsuario);

    /**
     * Método chamado quando ocorre um erro durante a autenticação.
     * @param error O erro ocorrido durante a autenticação.
     */
    void onError(String error);
}
