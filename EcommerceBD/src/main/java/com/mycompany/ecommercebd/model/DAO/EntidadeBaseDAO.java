package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

import com.mycompany.ecommercebd.model.Conexao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public abstract class EntidadeBaseDAO<T> implements DAObase<T> {
    protected Long gerarIdUnico(String nomeTabela, String colunaID) throws SQLException {

    final int MIN_ID = 1;
    final int MAX_ID = 999_999;
    final int MAX_TENTATIVAS = 100;

    String sql = "SELECT COUNT(*) FROM " + nomeTabela + " WHERE " + colunaID + " = ?";
    for (int i = 0; i < MAX_TENTATIVAS; i++) {
        long idCandidato = ThreadLocalRandom.current().nextInt(MIN_ID, MAX_ID + 1);

        try (Connection connection = Conexao.conectar();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idCandidato);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()) {
                    int count = rs.getInt(1);
                    if(count == 0) {
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
}
