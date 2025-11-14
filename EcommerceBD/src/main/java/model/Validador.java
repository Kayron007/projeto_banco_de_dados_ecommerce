package model;

public final class Validador {
    private Validador() {}

    /*
     * Retorna um email completo
     * Ex: email@gmail.com;
     */
    public static String emailValido(String email) {
        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'Email' é obrigatório!");
        }

        String regex = "^[\\w.!#$%&’*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(\\.[A-Za-z]+)+$";
        if(!email.matches(regex)) {
           throw new IllegalArgumentException("ERRO: Email inválido!");
        }

        return email;
    }

    /*
     * Retorna Nome limpo;
     */
    public static String nomeValido(String nome) {
        if(nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'Nome' é obrigatório!");
        }

        nome = nome.trim().replaceAll("\\s+", " ");

        if(nome.trim().length() < 3) {
            throw new IllegalArgumentException("O nome deve ter um mínimo de três caracteres!");
        }

        if(!nome.matches("^[A-Za-zÀ-ÿ ](?:[A-Za-zÀ-ÿ'\\- ]*[A-Za-zÀ-ÿ])?$")) {
            throw new IllegalArgumentException("ERRO: O nome deve conter apenas letras!");
        }

        return nome;
    }

    /* 
     * Retorna apenas números;
     */
    public static String cepValido(String cep) {
        if(cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'CEP' é obrigatório!");
        }

        /*
         * Remove máscara;
         */
        cep = cep.replaceAll("\\D", "");

        /*
         * Tamanho obrigatório;
         */
        if(cep.length() != 8) {
            throw new IllegalArgumentException("ERRO: O CEP deve conter apenas oito caracteres!");
        }

        /*
         * Evita números repetidos (00... 11... 22... etc)
         */
        if(cep.matches("(\\d)\\1{7}")) {
            throw new IllegalArgumentException("ERRO: CEP inválido!");
        }

        return cep;
    }

    /*
     * Retorna o CNPJ limpo;
     */
    public static String cnpjValido(String cnpj) {
        if(cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'CNPJ' é obrigatório!");
        }

        /*
         * Remove máscara;
         */
        cnpj = cnpj.replaceAll("\\D", "");

        /*
         * Tamanho obrigatório;
         */
        if(cnpj.length() != 14) {
            throw new IllegalArgumentException("ERRO: O CNPJ deve conter 14 dígitos numéricos.");
        }

        /* 
         * Evita números repetidos (00... 11... 22... etc)
         */
        if(cnpj.matches("(\\d)\\1{13}")) {
            throw new IllegalArgumentException("ERRO: CNPJ inválido!");
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
        soma = 0;

        for(int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesoSegundoDigito[i];
        }

        int segundoDigito = (soma % 11 < 2) ? 0 : 11 - (soma % 11);

        /**
         * Verifica contra dígitos informados;
         */
        String cnpjCalculado = cnpj.substring(0, 12) + primeiroDigito + segundoDigito;
        if(!cnpj.equals(cnpjCalculado)) {
            throw new IllegalArgumentException("ERRO: CNPJ inválido!");
        }

        return cnpj;
    }

    /*
     * Formata o CEP e retorna com hífen se possível;
     */
    public static String utilCEP(String cep) {
        String numeros = cepValido(cep);

        return numeros.substring(0, 5) + "-" + cep.substring(5);
    }

    /*
     * Retorna só números sem máscara
     */
    public static String utilCPF(String cpf) {
        /*
         * Verifica se o CPF é nulo;
         */
        if(cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'CPF' é obrigatório!");
        }
        
        /*
         * Remove tudo que não for número;
         */
        cpf = cpf.replaceAll("\\D", "");

        /*
         * Tamanho obrigatório
         */
        if(cpf.length() != 11) {
            throw new IllegalArgumentException("ERRO: Tamanho inválido! Digite apenas 11 números.");
        }

        /*
         * Verifica sequências repetidas
         */
        if(cpf.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("ERRO: CPF inválido!");
        }

        /*
         * Cálculo do primeiro dígito
         */
        int soma = 0;
        for(int i = 0, peso = 10; i < 9; i++, peso--) {
            soma += (cpf.charAt(i) - '0') * peso;
        }
        
        int dv1 = 11 - (soma % 11);
        if(dv1 > 9) {
            dv1 = 0;
        }

        /*
         * Cálculo do segundo digito
         */
        soma = 0;
        for(int i = 0, peso = 11; i < 10; i++, peso--) {
            soma += (cpf.charAt(i) - '0') * peso;
        }

        int dv2 = 11 - (soma % 11);
        if(dv2 > 9) {
            dv2 = 0;
        }

        /*
         * Comparação
         */
        if(dv1 == (cpf.charAt(9) - '0') && dv2 == (cpf.charAt(10) - '0')) {
            return cpf;
        } else {
            throw new IllegalArgumentException("ERRO: CPF inválido!");
        }
    }

    /*
     * Retorna o RG limpo;
     */
    public static String utilRG(String rg) {
        if(rg == null || rg.trim().isEmpty()) {
            throw new IllegalArgumentException("ERRO: O campo 'RG' é obrigatório!");
        }

        /*
         * Remove tudo que não for dígito;
         */
        String limpo = rg.trim().replaceAll("[^0-9Xx]", "").toUpperCase();

        if(limpo.length() < 8 || limpo.length() > 9) {
            throw new IllegalArgumentException("ERRO: Tamanho do RG inválido!");
        }

        /*
         * Verifica sequências repetidas
         */
        if(limpo.matches("^(\\d)\\1+$")) {
            throw new IllegalArgumentException("ERRO: RG inválido!");
        }

        /* 
         * Se tiver X, só pode ser no final 
         */
        if (limpo.contains("X") && !limpo.endsWith("X")) {
            throw new IllegalArgumentException("ERRO: O RG só pode conter 'X' no último digito!");
        }

        return limpo;
    }
}