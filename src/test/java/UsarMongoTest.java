import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsarMongoTest {
    private static UsarMongo usarMongo;
    private static Document usuario1;
    private static Document usuario2;
    private static Document usuario3;
    private static Document usuario4;
    private static Document usuario5;

    @BeforeAll
    static void setUp() throws Exception {
        usarMongo = new UsarMongo("meuBancoDeTeste","usuarios_teste");
        usarMongo.excluirNoBanco(new Document());

        usuario1 = new Document("nome", "Ryan")
                .append("idade", 25)
                .append("cidade", "São Paulo");

        usuario2 = new Document("nome", "Yasmin")
                .append("idade", 22)
                .append("cidade", "Rio de Janeiro");

        usuario3 = new Document("nome", "Carlos")
                .append("idade", 30)
                .append("cidade", "Curitiba");

        usuario4 = new Document("nome", "Bianca")
                .append("idade", 28)
                .append("cidade", "Salvador");

        usuario5 = new Document("nome", "Letícia")
                .append("idade", 19)
                .append("cidade", "Recife");
    }

    @Test
    @Order(1)
    void deveInserirNoBanco() throws Exception {
        usarMongo.inserirNoBanco(usuario1);
        usarMongo.inserirNoBanco(usuario2);
        usarMongo.inserirNoBanco(usuario3);
        usarMongo.inserirNoBanco(usuario4);
        usarMongo.inserirNoBanco(usuario5);

        List<Document> resultado = usarMongo.ObterTodosDados();

        assertEquals(5, resultado.size());
        assertEquals("Ryan", resultado.get(0).get("nome"));
        assertEquals("Bianca", resultado.get(3).get("nome"));
    }

    @Test
    @Order(2)
    void deveAtualizarNoBanco() throws Exception {
        Document filtro = new Document("nome", "Ryan");
        Document novosDados = new Document("idade", 26);

        usarMongo.alterarNoBanco(filtro, novosDados);

        List<Document> resultado = usarMongo.obterDados(filtro);
        assertEquals(26, resultado.get(0).getInteger("idade"));
    }

    @Test
    @Order(3)
    void deveExcluirNoBanco() throws Exception {
        Document filtro = new Document("nome", "Ryan");
        usarMongo.excluirNoBanco(filtro);

        List<Document> resultado = usarMongo.obterDados(filtro);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(4)
    void deveLancarExcecaoAoInserirNulo() {
        assertThrows(Exception.class, () -> usarMongo.inserirNoBanco(null));
    }
}
