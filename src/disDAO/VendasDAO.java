/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disDAO;

import disCliente.Clientes;
import disConexao.ConnectionFactory;

import disModel.Vendas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Walan
 */
public class VendasDAO {

    private Connection con;

    public VendasDAO() {
        this.con = new ConnectionFactory().getConnection();

    }
    //Cadastrar Venda//

    public void cadastrarVenda(Vendas obj) {
        try {

            String sql = "insert into tb_vendas(cliente_id,data_venda,total_venda,lucro,observacoes,desconto)values(?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, obj.getCliente().getId());
            stmt.setString(2, obj.getData_venda());
            stmt.setDouble(3, obj.getTotal_venda());
            stmt.setDouble(4, obj.getLucro());
            stmt.setString(5, obj.getObs());
            stmt.setDouble(6, obj.getDesconto());

            stmt.execute();

            stmt.close();

        } catch (Exception erro) {
            JOptionPane.showMessageDialog(null, "ERRO" + erro);
        }

    }
    //Retorna a Ultima venda//

    public int retornaUltimaVenda() {

        try {
            int idvenda = 0;

            String sql = "select max(id)id from tb_vendas";
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Vendas p = new Vendas();
                p.setId(rs.getInt("id"));

                idvenda = p.getId();

            }
            return idvenda;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //Metodo que filtra venda por datas //
    public List<Vendas> listarVendasPorPeriodo(LocalDate data_inicio, LocalDate data_fim) {
        try {

            //1 passo criar lista de Vendas//
            List<Vendas> lista = new ArrayList<>();

            String sql = "select v.id,date_format(v.data_venda,'%d/%m/%Y')as data_formatada,c.nome,v.total_venda,v.observacoes,v.lucro,v.desconto from tb_vendas as v "
                    + "inner join tb_clientes as c on(v.cliente_id = c.id)where v.data_venda BETWEEN? AND?";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, data_inicio.toString());
            stmt.setString(2, data_fim.toString());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vendas obj = new Vendas();
                Clientes c = new Clientes();

                obj.setId(rs.getInt("v.id"));
                obj.setData_venda(rs.getString("data_formatada"));
                c.setNome(rs.getString("c.nome"));
                obj.setTotal_venda(rs.getDouble("v.total_venda"));
                obj.setObs(rs.getString("v.observacoes"));
                obj.setLucro(rs.getDouble("v.lucro"));
                obj.setDesconto(rs.getDouble("v.desconto"));

                obj.setCliente(c);

                lista.add(obj);

            }

            return lista;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "ERRO!" + e);
            return null;

        }

    }

    //Metodo que calcula total da vendaa por data//
    public double retornaTotalVendaPorData(LocalDate data_venda) {
        try {
            double totalvenda = 0;

            String sql = "select sum(total_venda)as total from tb_vendas where data_venda = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, data_venda.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                totalvenda = rs.getDouble("total");

            }
            return totalvenda;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int notaVenda() {
        try {
            
            int venda_id = 0;
            String sql = "select tb_clientes.nome"
                    + "AS Nome,tb_clientes.celular"
                    + "AS Celular,tb_clientes.endereco"
                    + "AS Endereço,tb_clientes.numero"
                    + "AS Numero, tb_clientes.bairro"
                    + "AS Bairro, tb_clientes.cidade"
                    + "AS Cidade ,tb_itensvendas.subtotal"
                    + "AS SubTatotal, tb_produtos.descricao"
                    + "AS Descrição"
                    + "from  tb_produtos"
                    + "INNER JOIN tb_itensvendas"
                    + "ON tb_produtos.id = tb_itensvendas.produto_id"
                    + "INNER JOIN tb_vendas"
                    + "ON tb_itensvendas.venda_id = tb_vendas.id "
                    + "INNER JOIN tb_clientes "
                    + "ON tb_vendas.cliente_id = tb_clientes.id"
                    + "where tb_vendas.id = '" + venda_id + "'";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.execute();
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Vendas p = new Vendas();
                p.setId(rs.getInt("id"));

                venda_id = p.getId();

            }
            
            JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);
            JasperReport cr = JasperCompileManager.compileReport("Relatorios2/LojaTeree.jrxml");
            JasperPrint jp = JasperFillManager.fillReport(cr, new HashMap(), jrRS);
            JasperViewer.viewReport(jp, false);
   
            return venda_id;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "NOTA NÃO EMITIDA");
        }
        return 0;
       
          

    }

}
