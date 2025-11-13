package model.DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import model.Fornecedor;
import model.FornecedorProduto;
import model.Produto;

public class FornecedorProdutoDAO extends EntidadeBaseDAO<FornecedorProduto> {

    private Connection connection;

    @Override
    public void inserir(FornecedorProduto fp) throws SQLException {
        try {
            Long novoId = gerarIdUnico("cliente", "id");
            fp.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO fornecedor_produto (fk_Fornecedor_ID_fornecedor, fk_Produto_ID_produto, preco_fornecedor, prazo_entrega) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, fp.getIdFornecedor().getId());
            stmt.setLong(2, fp.getIdProduto().getId());
            stmt.setBigDecimal(3, fp.getPrecoFornecedor());
            stmt.setLong(1, fp.getIdFornecedor().getId());
            stmt.setLong(2, fp.getIdProduto().getId());
            stmt.setBigDecimal(3, fp.getPrecoFornecedor());
            stmt.setInt(4, fp.getPrazo());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir fornecedor_produto, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Registro fornecedor_produto inserido com sucesso!");
        }
    }

    @Override
    public void deletar(FornecedorProduto fp) {
        String sql = "DELETE FROM fornecedor_produto WHERE fk_Fornecedor_ID_fornecedor = ? AND fk_Produto_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, fp.getIdFornecedor().getId());
            stmt.setLong(2, fp.getIdProduto().getId());
            stmt.setLong(1, fp.getIdFornecedor().getId());
            stmt.setLong(2, fp.getIdProduto().getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Registro fornecedor_produto deletado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar fornecedor_produto: " + e.getMessage());
        }
    }

    @Override
    public void alterar(FornecedorProduto fp) {
        String sql = "UPDATE fornecedor_produto SET preco_fornecedor = ?, prazo_entrega = ? "
                + "WHERE fk_Fornecedor_ID_fornecedor = ? AND fk_Produto_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, fp.getPrecoFornecedor());
            stmt.setBigDecimal(2, fp.getPrecoFornecedor());
            stmt.setLong(3, fp.getIdFornecedor().getId());
            stmt.setLong(4, fp.getIdProduto().getId());
            stmt.setBigDecimal(1, fp.getPrecoFornecedor());
            stmt.setBigDecimal(2, fp.getPrecoFornecedor());
            stmt.setLong(3, fp.getIdFornecedor().getId());
            stmt.setLong(4, fp.getIdProduto().getId());

            stmt.executeUpdate();
            System.out.println("[DAO] Registro fornecedor_produto alterado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar fornecedor_produto: " + e.getMessage());
        }
    }

    private FornecedorProduto montarFornecedorProduto(ResultSet rs) throws SQLException {
        Long FornecedorId = rs.getLong("fk_Fornecedor_ID_fornecedor");
        FornecedorDAO fpd = new FornecedorDAO();
        Fornecedor f = fpd.buscarPorId(FornecedorId);

        Long fkProdutoId = rs.getLong("fk_Produto_ID_produto");
        ProdutoDAO pd = new ProdutoDAO();
        Produto p = pd.buscarPorId(fkProdutoId);

        BigDecimal precoFornecedor = rs.getBigDecimal("preco_fornecedor");
        Integer prazoEntrega = rs.getInt("prazo_entrega");

        return new FornecedorProduto(f, p, precoFornecedor, prazoEntrega);
    }

    @Override
    public FornecedorProduto buscarPorId(Long id) throws SQLException {
        throw new UnsupportedOperationException("Use buscarPorChaves(fornecedorId, produtoId) para buscar nessa tabela.");
    }

    public FornecedorProduto buscarPorChaves(Long fornecedorId, Long produtoId) throws SQLException {
        String sql = "SELECT * FROM fornecedor_produto WHERE fk_Fornecedor_ID_fornecedor = ? AND fk_Produto_ID_produto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, fornecedorId);
            stmt.setLong(2, produtoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return montarFornecedorProduto(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<FornecedorProduto> listar() {
        List<FornecedorProduto> lista = new ArrayList<>();

        String sql = "SELECT * FROM fornecedor_produto";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                FornecedorProduto fp = new FornecedorProduto();

                Long FornecedorId = rs.getLong("fk_Fornecedor_ID_fornecedor");
                FornecedorDAO fpd = new FornecedorDAO();
                Fornecedor f = fpd.buscarPorId(FornecedorId);
                fp.setIdFornecedor(f);
                fp.setIdFornecedor(f);

                Long fkProdutoId = rs.getLong("fk_Produto_ID_produto");
                ProdutoDAO pd = new ProdutoDAO();
                Produto p = pd.buscarPorId(fkProdutoId);
                fp.setIdProduto(p);
                fp.setIdProduto(p);
                
                fp.setPrecoFornecedor(rs.getBigDecimal("preco_fornecedor"));
                fp.setPrecoFornecedor(rs.getBigDecimal("preco_fornecedor"));
                fp.setPrazo(rs.getInt("prazo_entrega"));
                lista.add(fp);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar fornecedor_produto: " + e.getMessage());
        }
        return lista;
    }
}
