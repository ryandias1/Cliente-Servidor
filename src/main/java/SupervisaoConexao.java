import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisaoConexao extends Thread {
    private Parceiro            usuario;
    private Socket              conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisaoConexao (Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao==null) throw new Exception ("Conexao ausente");
        if (usuarios==null) throw new Exception ("Usuarios ausentes");
        this.conexao  = conexao;
        this.usuarios = usuarios;
    }

    public void run () {
        ObjectOutputStream transmissor;
        try {
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
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
                Pedido pedido = this.usuario.EnviarUmPedido();
                if (pedido==null) return;
                else if (pedido instanceof PedidoDeUsoMongo) {
                    PedidoDeUsoMongo pedidoDeUsoMongo = (PedidoDeUsoMongo) pedido;
                    UsarMongo dbAcess = new UsarMongo(pedidoDeUsoMongo.getDbCollecion());
                    switch (pedidoDeUsoMongo.getTipoPedido()) {
                        case "insert":
                            dbAcess.inserirNoBanco(pedidoDeUsoMongo.getDocumento());
                            break;
                        case "update":
                            dbAcess.alterarNoBanco(pedidoDeUsoMongo.getFiltro(), pedidoDeUsoMongo.getNovosDados());
                            break;
                        case "delete":
                            dbAcess.excluirNoBanco(pedidoDeUsoMongo.getFiltro());
                        case "find":
                            FindIterable<Document> dadosObtidos = dbAcess.obterDados(pedidoDeUsoMongo.getFiltro());
                            this.usuario.RecebeUmPedido(new RetornarDados(dadosObtidos));
                    }
                } else if (pedido instanceof PedidoDeSaida) {
                    synchronized (this.usuarios) {
                        this.usuarios.remove (this.usuario);
                    }
                    this.usuario.FecharConexao();
                }
            }
        } catch (Exception erro) {
            try {
                transmissor.close ();
                receptor   .close ();
            } catch (Exception falha) {} // so tentando fechar antes de acabar a thread
            return;
        }
    }
}
