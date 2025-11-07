package model;

import java.util.concurrent.ThreadLocalRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class EntidadeBase {
    protected Long id;

    //Construtor sem arg
    public EntidadeBase() {}

    //Construtor principal
    public EntidadeBase(Long id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Long gerarIdUnico(Connection conectar) throws SQLException {
        
        String nomeTabela = getTabela();

        final int MIN_ID = 1;
        final int MAX_ID = 999_999;

        final int MAX_TENTATIVAS = 100;

        String sql = "SELECT COUNT(*) FROM " + nomeTabela + " WHERE ID_" + nomeTabela + " = ?";
        for (int i = 0; i < MAX_TENTATIVAS; i++) {
            long idCandidato = ThreadLocalRandom.current().nextInt(MIN_ID, MAX_ID + 1);

            try (PreparedStatement stmt = conectar.prepareStatement(sql)) {
                stmt.setLong(1, idCandidato);

                try (ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        int count = rs.getInt(1);
                        if(count == 0) {
                            this.id = idCandidato;
                            return idCandidato;
                        }
                    }
                }
            }
        } throw new IllegalStateException (
        "Não foi possível gerar ID único após " + MAX_TENTATIVAS +
        "tentativas. Considere aumentar o range de IDs ou limpar registros antigos."
        );
    }

    protected abstract String getTabela();
    public abstract String toString();
}