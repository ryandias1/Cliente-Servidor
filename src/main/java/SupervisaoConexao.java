import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisaoConexao extends Thread {
    private Map<String, Parceiro> usuariosIdentificados;
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisaoConexao (Socket conexao, ArrayList<Parceiro> usuarios, Map<String, Parceiro> usuariosIdentificados) throws Exception {
        if (conexao==null) throw new Exception ("Conexao ausente");
        if (usuarios==null) throw new Exception ("Usuarios ausentes");
        if (usuariosIdentificados==null) throw new Exception ("Usuarios identificados ausentes");
        this.conexao  = conexao;
        this.usuarios = usuarios;
        this.usuariosIdentificados = usuariosIdentificados;
    }

    public void run () {
        ObjectOutputStream transmissor;
        try {
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
            transmissor.flush();
        } catch (Exception erro) {
            return;
        }
        ObjectInputStream receptor=null;
        try {
            receptor = new ObjectInputStream(this.conexao.getInputStream());
        } catch (Exception erro) {
            try {
                transmissor.close();
            } catch (Exception falha) {} // so tentando fechar antes de acabar a thread
            return;
        }

        try {
            this.usuario = new Parceiro (this.conexao, receptor, transmissor);
        } catch (Exception erro) {} // sei que passei os parametros corretos

        try {
            synchronized (this.usuarios) {
                this.usuarios.add (this.usuario);
            }

            for(;;) {
                Pedido pedido = this.usuario.enviarUmPedido();
                if (pedido==null) return;
                else if (pedido instanceof PedidoDeUsoMongo) {
                    PedidoDeUsoMongo pedidoDeUsoMongo = (PedidoDeUsoMongo) pedido;
                    UsarMongo mongo = new UsarMongo(pedidoDeUsoMongo.getDbCollecion());
                    switch (pedidoDeUsoMongo.getTipoPedido()) {
                        case "insert":
                            mongo.inserirNoBanco(pedidoDeUsoMongo.getDocumento());
                            break;
                        case "update":
                            mongo.alterarNoBanco(pedidoDeUsoMongo.getFiltro(), pedidoDeUsoMongo.getNovosDados());
                            break;
                        case "delete":
                            mongo.excluirNoBanco(pedidoDeUsoMongo.getFiltro());
                            break;
                        case "find":
                            List<Document> dadosObtidos = mongo.obterDados(pedidoDeUsoMongo.getFiltro());
                            this.usuario.recebeUmPedido(new RetornarDados(dadosObtidos));
                            break;
                    }
                } else if (pedido instanceof PedidoDeSaida) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.fecharConexao();
                }
            }
        } catch (Exception erro) {
            try {
                transmissor.close ();
                receptor   .close ();
            } catch (Exception falha) {} // so tentando fechar antes de acabar a thread
        }
    }
}
