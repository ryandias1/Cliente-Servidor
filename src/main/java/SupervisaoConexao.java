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
            Pedido pedido = null;
            do
            {
               pedido = (Pedido) this.usuario.espionarPedido ();
            }
            while (!(pedido instanceof PedidoIdentificacao));
        } catch (Exception erro) {
            erro.printStackTrace();
            return;
        }

        PedidoIdentificacao identificacao;
        try {
            identificacao = (PedidoIdentificacao) this.usuario.enviarUmPedido();

        } catch (Exception erro) {
            return;
        }

        try {
            synchronized (this.usuarios) {
                this.usuarios.add (this.usuario);
            }
            synchronized (this.usuariosIdentificados) {
                this.usuariosIdentificados.put(identificacao.getUid(), this.usuario);
            }
            this.usuario.setUid(identificacao.getUid());
            try {
                UsarMongo mongoMsg = new UsarMongo("IntelimedDB", "MensagensPendentes");

                // Buscar mensagens pendentes do usuário conectado
                Document filtro = new Document("uidDestinatario", this.usuario.getUid());
                List<Document> pendentes = mongoMsg.obterDados(filtro);

                // Enviar todas as mensagens
                for (Document msg : pendentes) {
                    String idRemetente = msg.getString("uidRemetente");
                    String conteudo = msg.getString("Conteudo");
                    String chaveBase64 = msg.getString("chave");

                    PedidoMensagem pedidoMensagem = new PedidoMensagem(idRemetente, conteudo, this.usuario.getUid(), chaveBase64);
                    this.usuario.recebeUmPedido(pedidoMensagem);
                }

                if (!pendentes.isEmpty()) {
                    mongoMsg.excluirNoBanco(filtro);
                }


            } catch (Exception erro) {
                throw new Exception("Erro ao enviar mensagens pendentes: " + erro);
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
                } else if (pedido instanceof PedidoMensagem) {
                    PedidoMensagem pedidoMensagem = (PedidoMensagem) pedido;
                    Parceiro destinatario = this.usuariosIdentificados.get(pedidoMensagem.getUidDestinatario());
                    if (destinatario==null) {
                        Document msgPendente = new Document()
                                .append("uidRemetente", pedidoMensagem.getUidRemetente())
                                .append("uidDestinatario", pedidoMensagem.getUidDestinatario())
                                .append("Conteudo", pedidoMensagem.getConteudoCriptografado())
                                .append("chave", pedidoMensagem.getChaveBase64());
                        UsarMongo mongo = new UsarMongo("IntelimedDB", "MensagensPendentes");
                        mongo.inserirNoBanco(msgPendente);
                    } else {
                        destinatario.recebeUmPedido(pedidoMensagem);
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
