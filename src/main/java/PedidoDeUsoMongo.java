import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class PedidoDeUsoMongo extends Pedido{
    private String uri = "mongodb+srv://app_intelimed:projetointegrador4@clusterintelimed.wi4xnuk.mongodb.net/?retryWrites=true&w=majority&appName=clusterIntelimed";
    private String db;
    private String collection;
    private String tipoPedido;
    private Document filtro;
    private Document novosDados;
    private Document doc;

    public PedidoDeUsoMongo(String database, String collection, String tipoPedido, Document filtro, Document novosDados, Document documento) {
        this.db = database;
        this.collection = collection;
        this.tipoPedido = tipoPedido;
        this.filtro = filtro;
        this.novosDados = novosDados;
        this.doc = documento;
    }
    public MongoCollection<Document> getDbCollecion() throws Exception {
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase(this.db);
            return database.getCollection(this.collection);
        } catch (Exception erro) {
            throw new Exception("Não foi possivel conectar ao Banco de Dados.");
        }
    }

    public String getTipoPedido() {
        return this.tipoPedido;
    }
    public Document getFiltro() {
        return this.filtro;
    }
    public Document getNovosDados() {
        return this.novosDados;
    }
    public Document getDocumento() {
        return this.doc;
    }
}
