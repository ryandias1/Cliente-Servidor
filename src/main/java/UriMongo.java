import java.nio.file.Files;
import java.nio.file.Paths;

public class UriMongo {
    private static String uri;

    static {
        try {
            uri = Files.readString(Paths.get("src/main/java/config/uri_mongo.txt")).trim();
        } catch (Exception e) {
            System.err.println("⚠️ Erro: Arquivo de URI do MongoDB não encontrado em 'config/uri_mongo.txt'");
            uri = ""; // ou pode lançar exceção, se quiser obrigar a configuração
        }
    }

    public static String getUri() {
        return uri;
    }
}
