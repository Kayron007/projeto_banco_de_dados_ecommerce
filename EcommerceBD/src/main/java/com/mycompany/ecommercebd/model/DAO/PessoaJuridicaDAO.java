package com.mycompany.ecommercebd.model.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.PessoaJuridica;

public class PessoaJuridicaDAO extends EntidadeBaseDAO<PessoaJuridica> {

    private Connection connection;

    public PessoaJuridicaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void inserir(PessoaJuridica pj) throws SQLException {
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            pj.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO pessoa_juridica (fk_ID_cliente, cnpj, IE) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pj.getId_cliente().getId());
            stmt.setString(2, pj.getCnpj());
            stmt.setString(3, pj.getIE());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Jurídica inserida com sucesso!");
        }
    }

    @Override
    public void alterar(PessoaJuridica pj) {
        String sql = "UPDATE pessoa_juridica SET cnpj = ?, IE = ? WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pj.getCnpj());
            stmt.setString(2, pj.getIE());
            stmt.setLong(3, pj.getId_cliente().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Jurídica alterada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar pessoa jurídica: " + e.getMessage());
        }
    }

    @Override
    public void deletar(PessoaJuridica pj) {
        String sql = "DELETE FROM pessoa_juridica WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, pj.getId_cliente().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Pessoa Jurídica deletada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar pessoa jurídica: " + e.getMessage());
        }
    }

    private PessoaJuridica montarPessoaJuridica(ResultSet rs) throws SQLException {
        Long idCliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente cliente = cd.buscarPorId(idCliente);

        String cnpj = rs.getString("cnpj");
        String ie = rs.getString("IE");

        return new PessoaJuridica(idCliente, cliente, cnpj, ie);
    }

    @Override
    public PessoaJuridica buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM pessoa_juridica WHERE fk_ID_cliente = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarPessoaJuridica(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<PessoaJuridica> listar() {
        List<PessoaJuridica> lista = new ArrayList<>();
        String sql = "SELECT * FROM pessoa_juridica";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(montarPessoaJuridica(rs));
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pessoas jurídicas: " + e.getMessage());
        }

        return lista;
    }
}
