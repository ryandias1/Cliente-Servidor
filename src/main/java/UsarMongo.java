import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

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

    public List<Document> obterDados(Document filtro) throws Exception{
        try {
            FindIterable<Document> list = this.collection.find(filtro);
            List<Document> lista = new ArrayList<Document>();
            for (Document doc : list) {
                lista.add(doc);
            }
            return lista;
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
