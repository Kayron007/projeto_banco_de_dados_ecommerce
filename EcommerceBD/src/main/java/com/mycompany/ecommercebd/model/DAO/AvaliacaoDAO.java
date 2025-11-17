/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.ecommercebd.model.Avaliacao;
import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Produto;
import com.mycompany.ecommercebd.model.Conexao;

/**
 *
 * @author gustavo
 */
public class AvaliacaoDAO extends EntidadeBaseDAO<Avaliacao> {

    private Connection connection;

    public AvaliacaoDAO(Connection connection) {
        this.connection = connection;
    }

    // Fallback para pontos onde ainda não havia injeção da conexão
    public AvaliacaoDAO() {
        this.connection = Conexao.conectar();
    }

    @Override
    public void inserir(Avaliacao avaliacao) throws SQLException {
        avaliacao.normalizar();
        avaliacao.validar();

        try {
            Long idnovo = gerarIdUnico("avaliacao", "ID_avaliacao");
            avaliacao.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO avaliacao (ID_avaliacao, fk_ID_cliente, fk_ID_produto, comentario, nota) VALUES (?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.setLong(2, avaliacao.getId_cliente().getId());
            stmt.setLong(3, avaliacao.getId_produto().getId());
            stmt.setString(4, avaliacao.getComentario());
            stmt.setShort(5, avaliacao.getNota());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Falha ao inserir avaliação, nenhuma linha afetada");
        }
    }

    @Override
    public void deletar(Avaliacao avaliacao) {
        String sql = "DELETE FROM avaliacao WHERE ID_avaliacao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliacao.getId());
            stmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Erro ao deletar avaliação " + e.getMessage());
        }
    }

    @Override
    public void alterar(Avaliacao avaliacao) {
        String sql = "UPDATE avaliacao SET comentario = ?, nota = ? WHERE ID_avaliacao = ?";

        try {
            avaliacao.normalizar();
            avaliacao.validar();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, avaliacao.getComentario());
                stmt.setShort(2, avaliacao.getNota());
                stmt.setLong(3, avaliacao.getId());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Erro ao alterar avaliação " + e.getMessage());
        }
    }

    @Override
    public List<Avaliacao> listar() {
        List<Avaliacao> notas = new ArrayList<>();
        String sql = "SELECT * FROM avaliacao";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Avaliacao avaliacao = montarFornecedor(rs);
                notas.add(avaliacao);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar avalia\u00e7\u00f5es: " + e.getMessage());
        }

        return notas;
    }

    private Avaliacao montarFornecedor(ResultSet rs) throws SQLException {
        // Extrai os valores das colunas do ResultSet
        Long id = rs.getLong("ID_avaliacao");

        Long id_cliente = rs.getLong("fk_ID_cliente");
        ClienteDAO cd = new ClienteDAO(connection);
        Cliente c = cd.buscarPorId(id_cliente);

        Long id_produto = rs.getLong("fk_ID_produto");
        ProdutoDAO pd = new ProdutoDAO(connection);
        Produto p = pd.buscarPorId(id_produto);

        String comentario = rs.getString("comentario");
        Short nota = rs.getShort("nota");

        // Retorna um novo objeto Fornecedor
        return new Avaliacao(id, nota, comentario, p, c);
    }

    @Override
    public Avaliacao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM avaliacao WHERE ID_avaliacao = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarFornecedor(rs);
                }
            }
        }

        return null;
    }

    public List<Avaliacao> listarPorProduto(Long idProduto) throws SQLException {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacao WHERE fk_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    avaliacoes.add(montarFornecedor(rs));
                }
            }
        }

        return avaliacoes;
    }
}
