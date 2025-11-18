package com.mycompany.ecommercebd.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.ecommercebd.model.Cliente;
import com.mycompany.ecommercebd.model.Conexao;
import com.mycompany.ecommercebd.model.NotaFiscal;
import com.mycompany.ecommercebd.model.Pagamento;
import com.mycompany.ecommercebd.model.Pedido;

public class NotaFiscalDAO extends EntidadeBaseDAO<NotaFiscal> {

    private Connection connection;

        // Construtor com conexão (para usar nos controllers)
    public NotaFiscalDAO(Connection connection) {
        this.connection = connection;
    }

    // Construtor padrão (para uso em outros DAOs)
    public NotaFiscalDAO() {
        this.connection = Conexao.conectar();
    }

    @Override
    public void inserir(NotaFiscal notaFiscal) throws SQLException {
        notaFiscal.normalizar();
        notaFiscal.verificar();
        
        try {
            Long novoId = gerarIdUnico("NotaFiscal", "id");
            notaFiscal.setId(novoId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO notafiscal (Valor_total, Imposto, Chave_de_acesso, fk_Pedido_ID_pedido) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setFloat(1, notaFiscal.getValorTotal());
            stmt.setFloat(2, notaFiscal.getImposto());
            stmt.setString(3, notaFiscal.getChaveDeAcesso());
            stmt.setLong(4, notaFiscal.getId_pedido().getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir nota fiscal, nenhuma linha afetada.");
            }

            System.out.println("[DAO] Nota fiscal inserida com sucesso!");
        }
    }

    @Override
    public void deletar(NotaFiscal notaFiscal) {
        String sql = "DELETE FROM notafiscal WHERE ID_nota_fiscal = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, notaFiscal.getId());
            stmt.executeUpdate();
            System.out.println("[DAO] Nota fiscal deletada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao deletar nota fiscal: " + e.getMessage());
        }
    }

    @Override
    public void alterar(NotaFiscal notaFiscal) {
        String sql = "UPDATE notafiscal SET Data_de_emissao = ?, Valor_total = ?, Imposto = ?, "
                + "Chave_de_acesso = ?, fk_Pedido_ID_pedido = ? WHERE ID_nota_fiscal = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(notaFiscal.getDataDeEmissao()));
            stmt.setInt(2, notaFiscal.getValorTotal());
            stmt.setInt(3, notaFiscal.getImposto());
            stmt.setString(4, notaFiscal.getChaveDeAcesso());
            stmt.setLong(5, notaFiscal.getId_pedido().getId());
            stmt.setLong(6, notaFiscal.getId());

            stmt.executeUpdate();
            System.out.println("[DAO] Nota fiscal alterada com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao alterar nota fiscal: " + e.getMessage());
        }
    }

    @Override
    public NotaFiscal buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM notafiscal WHERE ID_nota_fiscal = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {

                    id = rs.getLong("ID_nota_fiscal");
                    LocalDateTime dataEmissao = rs.getTimestamp("Data_de_emissao").toLocalDateTime();
                    int valorTotal = rs.getInt("Valor_total");
                    int imposto = rs.getInt("Imposto");
                    String chaveDeAcesso = rs.getString("Chave_de_acesso");

                    Long Id_pedido = rs.getLong("fk_Pedido_ID_pedido");
                    PedidoDAO Pd = new PedidoDAO();
                    Pedido p = Pd.buscarPorId(Id_pedido);

                    return new NotaFiscal(id, dataEmissao, valorTotal, imposto, chaveDeAcesso, p);
                }
            }
        }
        

        return null;
    }
    

    private NotaFiscal montarNotaFiscal(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID_nota_fiscal");
        LocalDateTime Data_de_emissao = rs.getTimestamp("Data_do_pedido").toLocalDateTime();
        int ValorTotal = rs.getInt("Valor_total");
        int Imposto = rs.getInt("Imposto");
        String chaveDeAcesso = rs.getString("Chave_de_acesso");

        Long Id_pedido = rs.getLong("fk_Pedido_ID_pedido");
        PedidoDAO Pd = new PedidoDAO();
        Pedido p = Pd.buscarPorId(Id_pedido);

        return new NotaFiscal(id, Data_de_emissao, ValorTotal, Imposto, chaveDeAcesso, p);

    }

    @Override
    public List<NotaFiscal> listar() {
        List<NotaFiscal> notas = new ArrayList<>();
        String sql = "SELECT * FROM notafiscal";

        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                NotaFiscal nf = montarNotaFiscal(rs);
                notas.add(nf);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar notas fiscais: " + e.getMessage());
        }

        return notas;
    }
    public NotaFiscal buscarPorPedido(Long idPedido) {
    String sql = "SELECT * FROM notafiscal WHERE fk_Pedido_ID_pedido = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setLong(1, idPedido);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return montarNotaFiscal(rs);
            }
        }
    } catch (Exception e) {
        System.out.println("Erro ao buscar nota fiscal do pedido: " + e.getMessage());
    }

    return null;
}

    // Lista todos os pedidos de um cliente específico
    public List<Pedido> listarPorCliente(Long idCliente) {
        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pedido WHERE fk_Cliente_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getLong("ID_pedido"));
                    p.setData(rs.getTimestamp("Data_do_pedido").toLocalDateTime());
                    p.setStatus(rs.getString("Status"));
                    p.setValorTotal(rs.getBigDecimal("Valor_total"));

                    Long Idcliente = rs.getLong("fk_Cliente_id");
                    ClienteDAO cd = new ClienteDAO(connection);
                    Cliente c = cd.buscarPorId(Idcliente);
                    p.setId_cliente(c);

                    Long Idpagamento = rs.getLong("fk_Pagamento_ID_pagamento");
                    PagamentoDAO pd = new PagamentoDAO();
                    Pagamento Pa = pd.buscarPorId(Idpagamento);
                    p.setId_pagamento(Pa);

                    pedidos.add(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar pedidos do cliente: " + e.getMessage());
        }

        return pedidos;
    }


}
