import br.com.ibm.intelimed.network.Parceiro;
import br.com.ibm.intelimed.network.PedidoDeSaida;
import br.com.ibm.intelimed.network.PedidoIdentificacao;
import br.com.ibm.intelimed.network.SupervisaoConexao;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SupervisaoConexaoTest {

    private PipedInputStream servidorLeInput;   // input stream que a SupervisaoConexao vai ler
    private PipedOutputStream clienteOutParaServidor; // o que o "cliente" escreve para o servidor

    private PipedInputStream clienteLeInput;    // input stream que o "cliente" vai ler (respostas do servidor)
    private PipedOutputStream servidorOutParaCliente; // o que o servidor escreve (ObjectOutputStream do servidor)

    private Socket socketFake;
    private ArrayList<Parceiro> usuarios;
    private Map<String, Parceiro> identificados;

    @BeforeEach
    void setup() throws Exception {
        // cria pares piped: cliente -> servidor
        servidorLeInput = new PipedInputStream();
        clienteOutParaServidor = new PipedOutputStream(servidorLeInput);

        // servidor -> cliente (para permitir ObjetcOutputStream do servidor sem travar)
        clienteLeInput = new PipedInputStream();
        servidorOutParaCliente = new PipedOutputStream(clienteLeInput);

        socketFake = new Socket() {
            @Override public InputStream getInputStream() { return servidorLeInput; }       // o servidor (Supervisao) lê daqui
            @Override public OutputStream getOutputStream() { return servidorOutParaCliente; } // o servidor escreve aqui
        };

        usuarios = new ArrayList<>();
        identificados = new HashMap<>();
    }

    @AfterEach
    void tearDown() throws Exception {
        try { clienteOutParaServidor.close(); } catch(Exception e){}
        try { servidorOutParaCliente.close(); } catch(Exception e){}
        try { servidorLeInput.close(); } catch(Exception e){}
        try { clienteLeInput.close(); } catch(Exception e){}
    }

    // ---------------------------
    // 1) CENÁRIO NORMAL
    // Enviamos um PedidoIdentificacao e depois nada: espera-se que o usuário seja adicionado e identificado.
    // ---------------------------
    @Test
    void fluxoNormal_identificacao() throws Exception {
        // Prepara o stream do "cliente" para enviar pedidos ao servidor
        ObjectOutputStream clienteWriter = new ObjectOutputStream(clienteOutParaServidor);
        clienteWriter.flush();

        // Escreve um PedidoIdentificacao (será lido pelo Parceiro do lado do servidor)
        PedidoIdentificacao id = new PedidoIdentificacao("userA", "contatoB");
        clienteWriter.writeObject(id);
        clienteWriter.flush();

        // Agora inicia a SupervisaoConexao (ela vai criar os ObjectStreams do seu lado)
        SupervisaoConexao sc = new SupervisaoConexao(socketFake, usuarios, identificados);
        Thread thread = new Thread(sc::run);
        thread.start();

        // Dá um tempo curto para a thread processar (Piped streams são síncronos, então não precisa muito)
        Thread.sleep(200);

        // Verificações básicas
        assertEquals(1, usuarios.size(), "Deveria ter adicionado 1 usuário");
        assertTrue(identificados.containsKey("userA"), "Mapa de identificados deveria conter userA");

        // encerra thread: enviar PedidoDeSaida para que feche corretamente
        ObjectOutputStream oos = new ObjectOutputStream(clienteOutParaServidor);
        oos.writeObject(new PedidoDeSaida());
        oos.flush();

        thread.join(500);
    }

    // ---------------------------
    // 2) VARIAÇÃO: ERRO NA LEITURA (simulado fechando o stream antes de enviar identificação)
    // Espera-se que nada seja adicionado e que a thread termine sem lançar exceção para o teste.
    // ---------------------------
    @Test
    void variacao_erroDeIdentificacao_fecharStream() throws Exception {
        // Não envia nada: fecha o stream do cliente imediatamente para simular erro de leitura
        clienteOutParaServidor.close();

        SupervisaoConexao sc = new SupervisaoConexao(socketFake, usuarios, identificados);
        Thread thread = new Thread(sc::run);
        thread.start();

        Thread.sleep(200);

        assertTrue(usuarios.isEmpty(), "Sem comunicação, não deve adicionar usuários");
        assertTrue(identificados.isEmpty(), "Sem comunicação, não deve identificar");

        thread.join(500);
    }

    // ---------------------------
    // 3) REMOÇÃO APÓS PEDIDO DE SAÍDA
    // Envia identificação, confirma que entrou nos mapas,
    // depois envia PedidoDeSaida e valida que foi removido.
    // ---------------------------
    @Test
    void removerUsuarioAoSair() throws Exception {
        Map<String, Parceiro> identificados = Collections.synchronizedMap(new HashMap<>());
        ArrayList<Parceiro> usuarios = new ArrayList<>();

        Socket socketFake = criarSocketFake(
                new PedidoIdentificacao("USERTESTE", "USERTESTE2"),
                new PedidoDeSaida()
        );

        SupervisaoConexao supervisao =
                new SupervisaoConexao(socketFake, usuarios, identificados);

        Thread t = new Thread(supervisao);
        t.start();

        t.join(); // 👈 GARANTE QUE O PEDIDO DE SAÍDA FOI PROCESSADO

        assertFalse(identificados.containsKey("USERTESTE"),
                "Após PedidoDeSaida, usuário deveria ter sido removido do mapa");
    }

    //A função criarSocketFake(...) serve para simular a comunicação entre cliente e servidor no teste, sem precisar de um servidor real rodando.
    private Socket criarSocketFake(Object... pedidos) throws IOException {
        PipedInputStream servidorLeInput = new PipedInputStream();
        PipedOutputStream clienteOutParaServidor = new PipedOutputStream(servidorLeInput);

        PipedInputStream clienteLeInput = new PipedInputStream();
        PipedOutputStream servidorOutParaCliente = new PipedOutputStream(clienteLeInput);

        Socket socketFake = new Socket() {
            @Override
            public InputStream getInputStream() { return servidorLeInput; }
            @Override
            public OutputStream getOutputStream() { return servidorOutParaCliente; }
        };

        // Cria ObjectOutputStream do "cliente" e escreve os pedidos
        ObjectOutputStream oos = new ObjectOutputStream(clienteOutParaServidor);
        oos.flush();
        for (Object pedido : pedidos) {
            try {
                oos.writeObject(pedido);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return socketFake;
    }
}