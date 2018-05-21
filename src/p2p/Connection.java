/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author Vitor
 */
public class Connection extends Thread {

    private Socket S;
    private Ligacao lig;

    public Connection(Socket s, Ligacao Lig) {
        super();
        S = s;
        lig = Lig;
        start();
    }

    public void Share(String mensagem) throws IOException {
        for (Ligacao con : Servidor.Ligacoes) {
            con.getOos().writeObject(mensagem);
        }
    }

    public void run() {
        try {
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(S.getInputStream());

            while (true) {
                String data = null;
                data = (String) ois.readObject();
                System.out.println("Mensagem Recebida: " + data);
                Share(data);
            }

        } catch (SocketException ex) {
            System.out.println("Desliguei.me caraças");
            Servidor.connections.remove(S);
            
            //remover oos
            Servidor.Ligacoes.remove(lig);
            
            System.out.println("Lista de Sockets: " + Servidor.connections.toString());
        } catch (Exception ex) {
            System.out.println("Detetei");
        }
    }
}
