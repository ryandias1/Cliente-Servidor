package br.com.ibm.intelimed.network;

import java.util.*;
import java.net.*;

public class AceitarConexao extends Thread {
    private ServerSocket pedido;
    private ArrayList<Parceiro> usuarios;
    private Map<String, Parceiro> usuariosIdentificados;

    public AceitarConexao (String porta, ArrayList<Parceiro> usuarios, Map<String, Parceiro> usuariosIdentificados) throws Exception {
        if (porta==null) throw new Exception ("Porta ausente");
        try {
            this.pedido = new ServerSocket (Integer.parseInt(porta));
        } catch (Exception  erro) {
            throw new Exception ("Porta invalida");
        }
        if (usuarios==null) throw new Exception ("Usuarios ausentes");
        if (usuariosIdentificados==null) throw new Exception ("Usuarios identificados ausentes");
        this.usuarios = usuarios;
        this.usuariosIdentificados = usuariosIdentificados;
    }

    public void run () {
        for(;;) {
            Socket conexao=null;
            try {
                conexao = this.pedido.accept();
            } catch (Exception erro) {
                continue;
            }
            SupervisaoConexao supervisaoConexao = null;
            try {
                supervisaoConexao = new SupervisaoConexao (conexao, usuarios, usuariosIdentificados);
            } catch (Exception erro) {} // sei que passei parametros corretos para o construtor
            supervisaoConexao.start();
        }
    }
}
