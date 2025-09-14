package br.csi.projeto_calculo_racao.util;

public class CpfUtils {

    //remove caracteres
    public static String limpar(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("\\D", "");
    }


    public static String formatar(String cpf) {
        cpf = limpar(cpf);
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }


//    public static boolean isFormatoValido(String cpf) {
//        cpf = limpar(cpf);
//        return cpf != null && cpf.matches("\\d{11}");
//    }
}
