import org.bson.Document;
import java.util.List;

public class RetornarDados extends Pedido{
    private List<Document> Dados;

    public RetornarDados(List<Document> lista) {
        this.Dados = lista;
    }

    public List<Document> getDados() {
        return Dados;
    }
}
