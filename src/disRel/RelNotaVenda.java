package disRel;

import disConexao.ConnectionFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class RelNotaVenda {

    private Connection con;

    public RelNotaVenda() {
         this.con = new ConnectionFactory().getConnection();
    }

    public void gerar(String layout, int vendaID) throws JRException, SQLException, ClassNotFoundException {
//gerando o jasper design
        JasperDesign desenho = JRXmlLoader.load(layout);

//compila o relatório
        JasperReport relatorio = JasperCompileManager.compileReport(desenho);

//estabelece conexão
       
        Statement stm = con.createStatement();

        String sql = " select tb_vendas.id codigo,tb_clientes.nome "
                + " AS Nome,tb_clientes.celular "
                + " AS Celular,tb_clientes.endereco "
                + " AS Endereço,tb_clientes.numero "
                + " AS Numero, tb_clientes.bairro "
                + " AS Bairro, tb_clientes.cidade "
                + " AS Cidade ,tb_itensvendas.subtotal "
                + " AS SubTatotal, tb_produtos.descricao "
                + " AS Descrição, tb_vendas.total_venda"
                + " AS Total_Da_Venda "
                + " from  tb_produtos "
                + " INNER JOIN tb_itensvendas "
                + " ON tb_produtos.id = tb_itensvendas.produto_id "
                + " INNER JOIN tb_vendas "
                + " ON tb_itensvendas.venda_id = tb_vendas.id "
                + " INNER JOIN tb_clientes "
                + " ON tb_vendas.cliente_id = tb_clientes.id "
                + " where tb_vendas.id = '" + vendaID + "' ";

        ResultSet rs = stm.executeQuery(sql);

//implementação da interface JRDataSource para DataSource ResultSet
        JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);

//executa o relatório
        Map parametros = new HashMap();
        parametros.put("venda", new Integer(10));
        JasperPrint impressao = JasperFillManager.fillReport(relatorio, parametros, jrRS);

//exibe o resultado
        JasperViewer viewer = new JasperViewer(impressao, true);
        viewer.show();
    }

}
