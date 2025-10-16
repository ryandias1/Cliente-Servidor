import com.mongodb.client.FindIterable;
import org.bson.Document;

public class RetornarDados extends Pedido{
    private FindIterable<Document> Dados;

    public RetornarDados(FindIterable<Document> Dados) {
        this.Dados = Dados;
    }

    public FindIterable<Document> getDados() {
        return Dados;
    }
}
