import br.com.ibm.intelimed.network.Parceiro;
import br.com.ibm.intelimed.network.SupervisaoConexao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SupervisaoConexaoTest {

    Socket socket = new Socket() {};

    private ArrayList<Parceiro> usuarios;
    private Map<String, Parceiro> identificados;

    @BeforeEach
    void setUp() {
        usuarios = new ArrayList<>();
        identificados = new HashMap<>();
    }

    @Test
    void deveConstruirSupervisorComSucesso() throws Exception {
        SupervisaoConexao supervisao =
                new SupervisaoConexao(socket, usuarios, identificados);

        assertNotNull(supervisao);
    }

    @Test
    void deveFalharSemSocket() {
        assertThrows(Exception.class,
                () -> new SupervisaoConexao(null, usuarios, identificados));
    }

    @Test
    void deveFalharSemUsuarios() {
        assertThrows(Exception.class,
                () -> new SupervisaoConexao(socket, null, identificados));
    }

    @Test
    void deveFalharSemIdentificados() {
        assertThrows(Exception.class,
                () -> new SupervisaoConexao(socket, usuarios, null));
    }
}
