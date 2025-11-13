package model;

public final class Validador {
    private Validador() {}

    public static void emailValido(String email) {
        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Email");
        }

        String regex = "^[a-zA-Z0-9._%+-]+@([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)[a-zA-Z]{2,}$";
        if(!email.matches(regex)) {
           throw new IllegalArgumentException("Email inválido!");
        }
    }

    public static void nomeValido(String nome) {
        if(nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: Nome");
        }

        if(nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve ter um mínimo de três caracteres!");
        }
    }

    public static void cepValido(String cep) {
        if(cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: CEP");
        }

        if(cep.length() != 8) {
            throw new IllegalArgumentException("O CEP deve conter apenas oito caracteres!");
        }

        if(cep.matches("(\\d)\\1{7}")) {
            throw new IllegalArgumentException("CEP inválido!");
        }
    }

    public static void cnpjValido(String cnpj) {
        if(cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório não preenchido: CNPJ");
        }

        // Tamanho obrigatório
        if(cnpj.length() != 14) {
            throw new IllegalArgumentException("O CNPJ deve conter 14 dígitos numéricos.");
        }

        // Evita números repetidos (00... 11... 22... etc)
        if(cnpj.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("CNPJ inválido!");
        }

        /* Algoritmo oficial dos dígitos verificadores */
        int[] pesoPrimeiroDigito  = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] pesoSegundoDigito   = {6,5,4,3,2,9,8,7,6,5,4,3,2};

        int soma = 0;

        /**
         * Calcula primerio dígito;
         */
        for(int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesoPrimeiroDigito[i];
        }

        int primeiroDigito = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        /**
         * Calcula segundo dígito;
         */
        for(int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesoSegundoDigito[i];
        }

        int segundoDigito = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        /**
         * Verifica contra dígitos informados;
         */
        String digitosCalculados = "" + primeiroDigito + segundoDigito;
        if(!cnpj.endsWith(digitosCalculados)) {
            throw new IllegalArgumentException("CNPJ inválido!");
        }
    }

    public static String utilCEP(String cep) {
        if(cep == null || cep.length() != 8) {
            return cep;
        }
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }
}