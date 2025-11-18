package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.Produto;

public class ProdutoDAO extends EntidadeBaseDAO<Produto> {

    private Connection connection;

    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    // Fallback para pontos onde ainda não havia injeção da conexão
    public ProdutoDAO() {
        this.connection = Conexao.conectar();
    }

    @Override
    public void inserir(Produto produto) throws SQLException {
        produto.normalizar();
        produto.validar();

        try {
            Long idnovo = gerarIdUnico("produto", "ID_produto");
            produto.setId(idnovo);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sql = "INSERT INTO produto (ID_produto, Descricao, Quantidade, Tamanho, Preco, Categoria, sexo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, produto.getId());
            stmt.setString(2, produto.getDescricao());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setString(4, produto.getTamanho());
            stmt.setDouble(5, produto.getPreco());
            stmt.setString(6, produto.getCategoria());
            stmt.setString(7, produto.getSexo());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir produto, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Produto inserido com sucesso!");
        }
    }

    @Override
    public void deletar(Produto produto) {
        String sql = "DELETE FROM produto WHERE ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, produto.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Produto deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    @Override
    public void alterar(Produto produto) {
        String sql = "UPDATE produto SET Descricao = ?, Quantidade = ?, Tamanho = ?, Preco = ?, Categoria = ?, sexo = ? "
                   + "WHERE ID_produto = ?";

        try {
            produto.normalizar();
            produto.validar();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, produto.getDescricao());
                stmt.setInt(2, produto.getQuantidade());
                stmt.setString(3, produto.getTamanho());
                stmt.setDouble(4, produto.getPreco());
                stmt.setString(5, produto.getCategoria());
                stmt.setString(6, produto.getSexo());
                stmt.setLong(7, produto.getId());

                stmt.executeUpdate();
                System.out.println("[DAO] Produto alterado com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar produto: " + e.getMessage());
        }
    }

    private Produto montarProduto(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_produto");
        String nome = rs.getString("Nome");
        String descricao = rs.getString("Descricao");
        String tamanho = rs.getString("Tamanho");
        String categoria = rs.getString("Categoria");
        Double preco = rs.getDouble("Preco");
        Integer quantidade = rs.getInt("Quantidade");
        String sexo = rs.getString("sexo");

        return new Produto(id, nome, descricao, tamanho, categoria, preco, quantidade, sexo);
    }

    @Override
    public Produto buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarProduto(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();

        if (connection == null) {
            System.out.println("Conexao nula ao listar produtos.");
            return produtos;
        }

        String sql = "SELECT * FROM produto";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("ID_produto"));
                p.setNome(rs.getString("Nome"));
                p.setDescricao(rs.getString("Descricao"));
                int qtd = rs.getInt("Quantidade");
                p.setQuantidade(rs.wasNull() ? 0 : qtd);
                p.setTamanho(rs.getString("Tamanho"));
                p.setPreco(rs.getDouble("Preco"));
                p.setCategoria(rs.getString("Categoria"));
                p.setSexo(rs.getString("sexo"));
                produtos.add(p);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        return produtos;
    }

    /**
     * Retorna os produtos mais recentes cadastrados.
     * A ordenação utiliza o ID (auto-increment) como proxy da data de inserção.
     */
    public List<Produto> listarMaisRecentes(int limite) {
        List<Produto> produtos = new ArrayList<>();

        if (connection == null) {
            System.out.println("Conexao nula ao listar produtos recentes.");
            return produtos;
        }

        String sql = "SELECT * FROM produto ORDER BY ID_produto DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limite);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(montarProduto(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar produtos recentes: " + e.getMessage());
        }

        return produtos;
    }
}
