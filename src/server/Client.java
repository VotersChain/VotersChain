/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import bitvote.AES;
import bitvote.SignatureUtils;
import bitvote.StringUtils;
import bitvote.VoteChain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import p2p.Cliente;

/**
 *
 * @author jferr
 */
public class Client {

    private static final int PORT = 2019;
    private static String chavePublica;
    private static String chavePrivada;
    private static String Host = "localhost";
    // Pascoal - DESKTOP-C38TKIF
    // Édi - Asus-Pc

    public static int Registo(Server obj) {
        try {
            //Pedir o Nome e o ID ao Utilizador
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Nome: ");
            String sNome = in.readLine();
            System.out.print("Identificador: ");
            long lID = Long.parseLong(in.readLine());

            //(Cliente  -> Servidor) Nome - Id
            //(Servidor -> Cliente ) Chaves pk, sk
            String chaves = "blank";
            chaves = obj.regist(sNome, lID);

            //Se utilizador já se encontra registado
            if (chaves.equals("")) {
                System.out.println("Utilizador já se encontra registado! Por favor, efetue o Login!");
                return 1;
            }

            //Efetuar registo
            String[] keys = chaves.split(",");
            String pk, sk;
            pk = keys[0];
            sk = keys[1];
            System.out.println("SK: " + sk);
            chavePublica = pk;
            chavePrivada = sk;

            //Gravar num ficheiro
            String fpk = "pk.txt";

            BufferedWriter bw = null;
            FileWriter fw = null;

            //Gravar pk
            fw = new FileWriter(fpk);
            bw = new BufferedWriter(fw);
            bw.write(pk);
            bw.close();
            fw.close();

            //Gravar sk
            byte[] iv = AES.generateIV();;
            byte[] key = DatatypeConverter.parseHexBinary(obj.requestKey());

            FileOutputStream fout = new FileOutputStream("iv_sk.txt");
            String ivHex = DatatypeConverter.printHexBinary(iv);
            fout.write(ivHex.getBytes());

            byte[] cripto = AES.encrypt(sk, key, iv);
            fout = new FileOutputStream("sk.txt");
            byte[] encoded = Base64.getEncoder().encode(cripto);
            fout.write(encoded);
            fout.close();

            //(Servidor -> Cliente ) nonce
            String nonce = obj.loginStepOne(pk);

            //(Cliente  -> Servidor) Assinar o nonce encia
            byte[] signNonce = SignatureUtils.signString(nonce, StringUtils.getPrivateKeyFromString(sk));

            //(Servidor -> Cliente ) Valida a assinatura
            boolean isLoged = obj.loginStepTwo(signNonce);

            if (isLoged) {
                //Registado e evolui para menu 2
                return 2;
            } else {
                //Erro na assinutura do nonce
                System.out.println("Erro no registo! Por favor tente novamente!");
                return 3;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static String getSK(Server obj) {
        String pt = "";
        String outString = "";
        try {
            InputStream inputStream = new FileInputStream("sk.txt");
            Reader inputStreamReader = new InputStreamReader(inputStream, "ISO-8859-1");

            int data = inputStreamReader.read();
            while (data != -1) {
                char theChar = (char) data;
                outString += theChar;
                data = inputStreamReader.read();
            }
            inputStreamReader.close();
        } catch (Exception ex) {
            Logger.getLogger(SQLiteBD.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            String ivString = "";
            FileReader filer = new FileReader("iv_sk.txt");
            char[] buff = new char[1];
            while (filer.read(buff) > 0) {
                ivString += buff[0];
            }
            filer.close();

            byte[] iv = DatatypeConverter.parseHexBinary(ivString);
            byte[] key = DatatypeConverter.parseHexBinary(obj.requestKey());

            byte[] decoded = Base64.getDecoder().decode(outString.getBytes("ISO-8859-1"));
            pt = AES.decrypt(decoded, key, iv);
            FileOutputStream out = new FileOutputStream("sk.txt");
            out.write(pt.getBytes("ISO-8859-1"));
            out.close();

            //Voltar a encriptar o ficheiro
            FileOutputStream fout = new FileOutputStream("iv_sk.txt");
            String ivHex = DatatypeConverter.printHexBinary(iv);
            fout.write(ivHex.getBytes());

            byte[] cripto = AES.encrypt(pt, key, iv);
            fout = new FileOutputStream("sk.txt");
            byte[] encoded = Base64.getEncoder().encode(cripto);
            fout.write(encoded);
            fout.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pt;
    }

    public static int Login(Server obj) {
        try {
            //Ler a pk do ficheiro
            String fpk = "pk.txt";
            BufferedReader br = null;
            FileReader fr = null;
            fr = new FileReader(fpk);
            br = new BufferedReader(fr);
            String pk = br.readLine();
            br.close();
            fr.close();
            chavePublica = pk;

            //(Servidor -> Cliente ) nonce
            String nonce = obj.loginStepOne(pk);

            //Ler o sk e decifrar
            String sk = getSK(obj);
            chavePrivada = sk;

            //(Cliente  -> Servidor) Assinar o nonce encia
            byte[] signNonce = SignatureUtils.signString(nonce, StringUtils.getPrivateKeyFromString(sk));

            //(Servidor -> Cliente ) Valida a assinatura
            boolean isLoged = obj.loginStepTwo(signNonce);
            if (isLoged) {
                //Registado e evolui para menu 2
                return 2;
            } else {
                //Erro na assinutura do nonce
                System.out.println("Erro no registo! Por favor tente novamente!");
                return 3;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static void menu2(Server obj) throws NoSuchProviderException, InvalidKeySpecException, Exception {
        Cliente cliente = null;
        
        //Obter a BlockChian no server
        VoteChain BlockChain = obj.getBlockChain();
        
        try {
            //Iniciar conecção ao server e criação do cliente
            cliente = new Cliente(Host, BlockChain, obj);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        String menu2 = "***********************************\n1-Votar\n2-Status Eleição\n0-Sair\n-->";
        int sair = 0;
        while (sair == 0) {
            System.out.print(menu2);
            Scanner in = new Scanner(System.in);
            int op = in.nextInt();
            switch (op) {
                case 1: {
                    //Lista de candidatos a votação
                    //Servidor nonce
                    //value (nº de votos)
                    //id da eleição 
                    //Blockchain só com genesis vote
                    cliente.makeVote(chavePrivada, chavePublica);
                }
                break;
                case 2: {

                }
                break;
                case 0: {
                    sair = 1;
                    System.exit(0);
                }
                break;
                default: {
                    System.out.println("Opção inválida tente novamente!");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(Host, PORT, new RMISSLClientSocketFactory());
            Server obj = (Server) registry.lookup("Server");

            String menu1 = "***********************************\n1-Login\n2-Registar\n0-Sair\n-->";

            int sair = 0;
            while (sair == 0) {
                System.out.print(menu1);
                Scanner in = new Scanner(System.in);
                int op = in.nextInt();
                switch (op) {
                    case 1: {
                        int res = Login(obj);
                        if (res == 2) {
                            //Registado - Passar ao menu 2
                            System.out.println("Loginado!");
                            menu2(obj);
                            sair = 1;
                        }
                    }
                    break;
                    case 2: {
                        int res = Registo(obj);
                        if (res == 2) {
                            //Registado - Passar ao menu 2
                            System.out.println("Registado!");
                            menu2(obj);
                            sair = 1;
                        }
                    }
                    break;
                    case 0: {
                        sair = 1;
                        System.exit(0);
                    }
                    break;
                    default: {
                        System.out.println("Opção inválida tente novamente!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("HelloClient exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
