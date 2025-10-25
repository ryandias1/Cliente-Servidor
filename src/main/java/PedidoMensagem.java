public class PedidoMensagem extends Pedido {
    private String uidRemetente;
    private String conteudo;
    private String uidDestinatario;

    public PedidoMensagem(String uidRemetente, String conteudo, String uidDestinatario) {
        this.uidRemetente = uidRemetente;
        this.conteudo = conteudo;
        this.uidDestinatario = uidDestinatario;
    }

    public String getUidRemetente() {
        return uidRemetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getUidDestinatario() {
        return uidDestinatario;
    }
}