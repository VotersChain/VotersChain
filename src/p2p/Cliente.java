/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vitor
 */
public class Cliente {
    
    public void EnviaMensagem(Socket s) throws IOException {
        System.out.println("Function: Enviar Mensagem");
        ObjectOutputStream oos = null;
        oos = new ObjectOutputStream(s.getOutputStream());

        //Para teste - Ler do teclado
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String mensagem = "";
        while (true) {
            mensagem = in.readLine();
            System.out.println("Mensagem: " + mensagem);
            oos.writeObject(mensagem);
            System.out.println("Mensagem Enviada");
        }
    }
    
    public Cliente(String address) throws IOException, ClassNotFoundException {
        Socket s = new Socket(address, 2222);

        System.out.println("Conectado");
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    EnviaMensagem(s);
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        
        ObjectInputStream ois = null;
        ois = new ObjectInputStream(s.getInputStream());
        String data = null;
        int i = 0;
        while (true) {
            data = (String) ois.readObject();
            
            if(data==null){
                break;
            }
            else{
                System.out.println("Mensagem: " + data);
            }
        }
    }
}

//ReentrantLock lock = new ReentrantLock();