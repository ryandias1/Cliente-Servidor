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
        AceitarConexao aceitarConexao=null;
        try {
            aceitarConexao = new AceitarConexao (porta, usuarios);
            aceitarConexao.start();
        } catch (Exception erro) {
            throw new Exception("Escolha uma porta apropriada e liberada para uso!\n");
        }

        //TODO: Completar Implementação(2)
    }
}