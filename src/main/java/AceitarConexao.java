import java.util.*;
import java.net.*;

public class AceitarConexao extends Thread {
    private ServerSocket pedido;
    private ArrayList<Parceiro> usuarios;

    public AceitarConexao (String porta, ArrayList<Parceiro> usuarios) throws Exception {
        if (porta==null) throw new Exception ("Porta ausente");
        try {
            this.pedido = new ServerSocket (Integer.parseInt(porta));
        } catch (Exception  erro) {
            throw new Exception ("Porta invalida");
        }
        if (usuarios==null) throw new Exception ("Usuarios ausentes");
        this.usuarios = usuarios;
    }

    public void run () {
        //TODO: Implementar lógica (1)
    }
}