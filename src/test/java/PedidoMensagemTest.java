import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PedidoMensagemTest {
    private static PedidoMensagem mensagem;

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        mensagem = new PedidoMensagem("uid123","Ola, Como esta?","uid456");
    }

    // - TESTES POR ESTADOS

    @Test
    void deveGerarConteudoCriptografadoAoCriarMensagem() {
        assertNotEquals("Ola, Como esta?",mensagem.getConteudoCriptografado());
    }

    @Test
    void deveGerarChaveBase64Valida() {
        assertNotNull(mensagem.getChaveBase64());
    }

    @Test
    void deveDescriptografarMensagemCorretamente() {
        assertEquals("Ola, Como esta?", mensagem.getConteudo());
    }

    @Test
    void deveManterMesmoConteudoAoRecriarComChave() throws Exception {
        PedidoMensagem mensagemCriptografada = new PedidoMensagem(mensagem.getUidRemetente(),mensagem.getConteudoCriptografado(),
                mensagem.getUidDestinatario(), mensagem.getChaveBase64());

        assertEquals("Ola, Como esta?",mensagemCriptografada.getConteudo());
    }

    @Test
    void deveRetornarMensagemDeErroQuandoChaveInvalida() throws Exception {
        PedidoMensagem mensagemCriptografada = new PedidoMensagem(mensagem.getUidRemetente(),mensagem.getConteudoCriptografado(),
                mensagem.getUidDestinatario(), "98298hw9nh9wn");

        assertEquals("[Erro ao descriptografar mensagem]",mensagemCriptografada.getConteudo());
    }

    // - TESTES POR ATRIBUTOS


}
