package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Pagamento;

public class PagamentoDAO extends EntidadeBaseDAO<Pagamento> {

    private Connection connection;

    @Override
    public void inserir(Pagamento pagamento) throws SQLException {
        pagamento.validar();
        
        try {
            Long idnovo = gerarIdUnico("pagamento", "ID_pagamento");
            pagamento.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO pagamento (ID_pagamento, Forma_de_pagamento, Status) "
                   + "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pagamento.getId());
            stmt.setString(2, pagamento.getFormaDePagamento());
            stmt.setString(3, pagamento.getStatus());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir pagamento, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Pagamento inserido com sucesso!");
        }
    }

    @Override
    public void deletar(Pagamento pagamento) {
        String sql = "DELETE FROM pagamento WHERE ID_pagamento = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pagamento.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pagamento deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar pagamento: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Pagamento pagamento) {
        String sql = "UPDATE pagamento SET Forma_de_pagamento = ?, Status = ? "
                   + "WHERE ID_pagamento = ?";

        try {
            pagamento.validar();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, pagamento.getFormaDePagamento());
                stmt.setString(2, pagamento.getStatus());
                stmt.setLong(3, pagamento.getId());

                stmt.executeUpdate();
                System.out.println("[DAO] Pagamento alterado com sucesso!");
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar pagamento: " + e.getMessage());
        }
    }

    private Pagamento montarPagamento(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_pagamento");
        String formaDePagamento = rs.getString("Forma_de_pagamento");
        String status = rs.getString("Status");

        return new Pagamento(id, formaDePagamento, status);
    }

    @Override
    public Pagamento buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pagamento WHERE ID_pagamento = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPagamento(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Pagamento> listar() {
        List<Pagamento> pagamentos = new ArrayList<>();

        String sql = "SELECT * FROM pagamento";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Pagamento p = new Pagamento();
                p.setId(rs.getLong("ID_pagamento"));
                p.setFormaDePagamento(rs.getString("Forma_de_pagamento"));
                p.setStatus(rs.getString("Status"));
                pagamentos.add(p);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar pagamentos: " + e.getMessage());
        }

        return pagamentos;
    }
}
