package br.feacp.nippo_agenda.utils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Patterns;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Classe que fornece métodos para validação de campos de entrada do usuário.
 */
public class Validador {

    /**
     * Valida se um campo obrigatório foi preenchido.
     *
     * @param textInputLayout O TextInputLayout associado ao campo.
     * @param campo           O campo EditText a ser validado.
     * @param nomeCampo       O nome do campo para exibir na mensagem de erro.
     * @return true se o campo estiver preenchido, false caso contrário.
     */
    public static boolean validarCampoObrigatorio(TextInputLayout textInputLayout, EditText campo, String nomeCampo) {
        String valor = campo.getText().toString().trim();
        if (valor.isEmpty()) {
            textInputLayout.setError(nomeCampo + " é obrigatório");
            campo.getBackground().mutate().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_ATOP);
            return false;
        } else {
            textInputLayout.setError(null);
            campo.getBackground().mutate().clearColorFilter(); // Restaura a cor padrão
        }
        return true;
    }

    /**
     * Valida o formato de um endereço de e-mail.
     *
     * @param textInputLayout O TextInputLayout associado ao campo de e-mail.
     * @param campoEmail      O campo EditText contendo o endereço de e-mail.
     * @return true se o formato de e-mail for válido, false caso contrário.
     */
    public static boolean validarFormatoEmail(TextInputLayout textInputLayout, EditText campoEmail) {
        String email = campoEmail.getText().toString().trim().toLowerCase();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayout.setError("Formato de email inválido");
            campoEmail.getBackground().mutate().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_ATOP); // Muda a cor da linha para vermelho
            return false;
        } else {
            textInputLayout.setError(null);
            campoEmail.getBackground().mutate().clearColorFilter(); // Restaura a cor padrão
        }
        return true;
    }

    /**
     * Valida um número de telefone.
     *
     * @param textInputLayout O TextInputLayout associado ao campo de telefone.
     * @param campoTelefone   O campo EditText contendo o número de telefone.
     * @return true se o número de telefone for válido, false caso contrário.
     */
    public static boolean validarTelefone(TextInputLayout textInputLayout, EditText campoTelefone) {
        String telefone = campoTelefone.getText().toString().trim().replaceAll("[^0-9]", "");
        if (telefone.length() < 10 || telefone.length() > 11) {
            textInputLayout.setError("Telefone inválido");
            campoTelefone.getBackground().mutate().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_ATOP); // Muda a cor da linha para vermelho
            return false;
        } else {
            textInputLayout.setError(null);
            campoTelefone.getBackground().mutate().clearColorFilter(); // Restaura a cor padrão
        }
        return true;
    }

    /**
     * Valida um número de CPF.
     *
     * @param textInputLayout O TextInputLayout associado ao campo de CPF.
     * @param campoCPF        O campo EditText contendo o número de CPF.
     * @return true se o CPF for válido, false caso contrário.
     */
    public static boolean validarCPF(TextInputLayout textInputLayout, EditText campoCPF) {
        String cpf = campoCPF.getText().toString().trim().replaceAll("[^0-9]", "");
        if (cpf.length() != 11 || !validarDigitosCPF(cpf)) {
            textInputLayout.setError("CPF inválido");
            campoCPF.getBackground().mutate().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_ATOP); // Muda a cor da linha para vermelho
            return false;
        } else {
            textInputLayout.setError(null);
            campoCPF.getBackground().mutate().clearColorFilter(); // Restaura a cor padrão
        }
        return true;
    }

    /**
     * Valida a força de uma senha.
     *
     * @param textInputLayout O TextInputLayout associado ao campo de senha.
     * @param campoSenha      O campo EditText contendo a senha.
     * @return true se a senha for forte, false caso contrário.
     */
    public static boolean validarSenha(TextInputLayout textInputLayout, EditText campoSenha) {
        String senha = campoSenha.getText().toString().trim();
        if (senha.length() < 8 || !senha.matches(".*[A-Z].*") || !senha.matches(".*[a-z].*") || !senha.matches(".*\\d.*") || !senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            textInputLayout.setError("A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial e ter pelo menos 8 caracteres");
            campoSenha.getBackground().mutate().setColorFilter(
                    Color.RED, PorterDuff.Mode.SRC_ATOP); // Muda a cor da linha para vermelho
            return false;
        } else {
            textInputLayout.setError(null);
            campoSenha.getBackground().mutate().clearColorFilter(); // Restaura a cor padrão
        }
        return true;
    }

    /**
     * Valida os dígitos de um número de CPF.
     *
     * @param cpf O número de CPF a ser validado.
     * @return true se os dígitos do CPF forem válidos, false caso contrário.
     */
    private static boolean validarDigitosCPF(String cpf) {
        int[] pesosCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        if (!cpf.matches("\\d{11}")) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * pesosCPF[i + 1];
        }
        int digito1 = soma % 11 < 2 ? 0 : 11 - soma % 11;
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * pesosCPF[i];
        }
        int digito2 = soma % 11 < 2 ? 0 : 11 - soma % 11;

        return cpf.endsWith(String.format("%d%d", digito1, digito2));
    }
}
