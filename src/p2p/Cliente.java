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
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author Vitor
 */
public class Cliente {

    private ObjectOutputStream oos;
    private VoteChain BlockChain;
    private int FLAG_MINING = 2;

    public void makeVote(String sk, String pk, long candidato_nonce, int id_eleicao) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, Exception {
        // Cria uma carteira
        VotersWallet wallet = new VotersWallet(StringUtils.getPrivateKeyFromString(sk), StringUtils.getPublicKeyFromString(pk));

        //Votar no candidato x, com a carteira respetiva, id
        Vote v = wallet.sendVote(candidato_nonce, wallet.n_votes, id_eleicao); //testes

        //indica ao servidor que vai mandar um objeto do tipo voto
        oos.writeObject("send-Voto");
        oos.flush();

        //Enviar o voto para broadcast
        oos.writeObject(v);
        oos.flush();
        System.out.println("Voto enviado");
    }
    
    public void sendBlockChain(VoteChain vt) throws IOException{
        oos.writeObject("send-BlockChain");
        oos.flush();
        
        oos.writeObject(vt);
        oos.flush();
        
        System.out.println("BlockChain enviada");
    }

    public Cliente(String address, Server obj) throws IOException, ClassNotFoundException {
        Socket s = new Socket(address, 2222);
        
        BlockChain = obj.getBlockChain();  
        BlockChain.getBlockchain().get(0).ImprimeBlock(0);

        System.out.println("Conectado");
        
        oos = new ObjectOutputStream(s.getOutputStream());

        //Ficar à escuta
        new Thread(()
                -> {
            try {
                ObjectInputStream ois = null;
                ois = new ObjectInputStream(s.getInputStream());
                ArrayList<Vote> lista_votos = new ArrayList<Vote>();
                Vote voto = null;
                VoteChain getBLOCKCHAIN = null;

                while (true) {
                    //Identificar qual o obejcto recebido
                    String ObjectType = (String) ois.readObject();

                    //Se voto recebido
                    if (ObjectType.equals("send-Voto")) {
                        System.out.println("Cliente: Recebe Voto");
                        voto = (Vote) ois.readObject();
                        
                        //Verificar se o votante já votou
                        if(BlockChain.nonMinedBlockhadVoted(voto.getSender(), lista_votos, voto.getId_eleicao()) == true){
                            System.out.println("Já votou");
                            break;
                        }
                        System.out.println("Ainda não votou");
                        lista_votos.add(voto);
                        
                        if(lista_votos.size() == FLAG_MINING){
                            //minar
                            System.out.println("Minar: ");
                            System.out.println("BlockChain: "+ BlockChain.getBlockchain().size());
                            Block bloco = new Block(BlockChain.lastBlock().getHash());
                            
                            for (Vote objteto_voto : lista_votos) {
                                bloco.addVote(objteto_voto);
                            }
                            //Limpar flag mining
                            lista_votos.clear();
                            
                            BlockChain.addBlock(bloco);
                            
                            //Autoridade de confiança
                            //obj.atualizaBlockChain(BlockChain);
                            
                            //Broadcast pelos votantes
                            sendBlockChain(BlockChain);                        
                        }
                        
                    } //Se BlockChain recebida
                    else if (ObjectType.equals("send-BlockChain")) {
                        System.out.println("Cliente: Recebe BlockChain");
                        
                        //Recebo a BlockChain 
                        getBLOCKCHAIN = (VoteChain) ois.readObject();
                        
                        //Valida a BlockChain
                        if(getBLOCKCHAIN.isChainValid()==true){
                            //Se a blockchain recebida for válida substituir a atual
                            BlockChain = getBLOCKCHAIN;
                            
                            System.out.println("Mostra BlockChian");
                            for (int i=0; i<BlockChain.getBlockchain().size(); i++) {
                                BlockChain.getBlockchain().get(i).ImprimeBlock(i);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                System.out.println("Server Desligou");
                System.exit(0);
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Server off");
                //Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ).start();
    }
}

//ReentrantLock lock = new ReentrantLock();
