/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p2p;

import bitvote.Block;
import bitvote.StringUtils;
import bitvote.Vote;
import bitvote.VoteChain;
import bitvote.VotersWallet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import server.Server;

/**
 *
 * @author Vitor
 */
public class Cliente {

    private ObjectOutputStream oos;
    private VoteChain BlockChain;
    private int FLAG_MINING = 3;

    public void makeVote(String sk, String pk) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, Exception {
        // Cria uma carteira
        VotersWallet wallet = new VotersWallet(StringUtils.getPrivateKeyFromString(sk), StringUtils.getPublicKeyFromString(pk));

        //Exemplo com um candidato
        long candidateX = StringUtils.generateNonce();
        System.out.println(candidateX);

        //Votar no candidato x, com a carteira respetiva, id
        Vote v = wallet.sendVote(candidateX, wallet.n_votes, 1); //testes

        //indica ao servidor que vai mandar um objeto do tipo voto
        oos.writeObject("send-Voto");
        oos.flush();

        //Enviar o voto para broadcast
        oos.writeObject(v);
        oos.flush();
        System.out.println("Mensagem enviada");
    }

    public Cliente(String address, VoteChain BlockChainAtual, Server obj) throws IOException, ClassNotFoundException {
        Socket s = new Socket(address, 2222);
        BlockChain = BlockChainAtual;

        System.out.println("Conectado");
        oos = new ObjectOutputStream(s.getOutputStream());

        //Ficar à escuta
        new Thread(()
                -> {
            try {
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(s.getInputStream());
                
                while (true) {
                    //Identificar qual o obejcto recebido
                    String ObjectType = (String) ois.readObject();

                    //Se voto recebido
                    if (ObjectType.equals("send-Voto")) {
                        System.out.println("Cliente: Recebe Voto");                    
                        
                    } //Se BlockChain recebida
                    else if (ObjectType.equals("send-BlockChain")) {
                        System.out.println("Cliente: Recebe BlockChain");
                        
                        //Atualizar a BlockChain do Server
                        //obj.
                        
                        //Fazer Broadcast da BlockChain pelos restantes clientes
                        
                    }
                }
            } catch (IOException ex) {
                System.out.println("Server Desligou");
                System.exit(0);
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Server off");
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ).start();
    }
}

//ReentrantLock lock = new ReentrantLock();
