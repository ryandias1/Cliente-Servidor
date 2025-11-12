import java.util.*;

public class Servidor {
    public static final String portaPadrao = "3000";

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            throw new Exception("Numero de argumentos invalido");
        }
        String porta = Servidor.portaPadrao;
        if (args.length == 1) {
            porta = args[0];
        }
        ArrayList<Parceiro> usuarios = new ArrayList<Parceiro>();
        Map<String, Parceiro> usuariosIdentificados = new HashMap<>();
        AceitarConexao aceitarConexao=null;
        try {
            aceitarConexao = new AceitarConexao (porta, usuarios, usuariosIdentificados);
            aceitarConexao.start();
        } catch (Exception erro) {
            throw new Exception("Escolha uma porta apropriada e liberada para uso!\n");
        }

        for (;;) {
            System.out.println("O servidor esta rodando na porta: " + porta);
            System.out.println("Use o comando \"des\" para desativar\n");
            System.out.print("> ");

            String comando = null;
            try {
                comando = Teclado.getUmString();
            } catch (Exception erro) {
                continue;
            }

            if (comando == null)
                continue;

            if (comando.toLowerCase().equals("des")) {
                synchronized (usuarios) {
                    PedidoDeDesligamento pedidoDeDesligamento = new PedidoDeDesligamento();

                    for (Parceiro usuario : usuarios) {
                        try {
                            usuario.recebeUmPedido(pedidoDeDesligamento);
                            usuario.fecharConexao();
                        } catch (Exception erro) {}
                    }
                }

                System.out.println("O servidor foi desativado!\n");
                System.exit(0);
            } else {
                System.err.println("Comando inválido!\n");
            }
        }
    }
}
