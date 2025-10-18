import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UsarMongo {
    private MongoCollection<Document> collection;

    public UsarMongo(MongoCollection<Document> col) {
        this.collection = col;
    }

    public void inserirNoBanco (Document doc) throws Exception{
        try {
            this.collection.insertOne(doc);
        } catch (Exception erro) {
            throw new Exception("Não foi possivel inserir no banco");
        }
    }

    public FindIterable<Document> obterDados(Document filtro) throws Exception{
        try {
            return this.collection.find(filtro);
        } catch (Exception erro) {
            throw new Exception("Não foi possivel obter os dados do banco");
        }
    }

    public void alterarNoBanco (Document filtro, Document novosDados) throws Exception{
        try{
            this.collection.updateOne(filtro, new Document("$set", novosDados));
        } catch (Exception erro) {
            throw new Exception("Não foi possivel alterar os dados do banco");
        }
    }

    public void excluirNoBanco (Document filtro) throws Exception{
        try {
            this.collection.deleteOne(filtro);
        } catch (Exception erro) {
            throw new Exception("Não foi possivel excluir dados do banco");
        }
    }
}
