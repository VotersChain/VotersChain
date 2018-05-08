/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.util.Date;

/**
 *
 * @author jferr
 */
public class Block {
    private String hash;
    private String previousHash;
    private String data; //depois mudar para arraylist 
    private long timeStamp;
    private long nonce; //number for proof of work
    
    public Block(String data,String previousHash){
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }
    
    public String calculateHash() {
        String calculatedHash = HashUtils.hashFuncSHA256(previousHash+Long.toString(timeStamp)+data+Long.toString(nonce));
        return calculatedHash;
    } 

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "Block{" + "hash=" + hash + ", previousHash=" + previousHash + ", data=" + data + ", timeStamp=" + timeStamp + ", nonce=" + nonce + '}';
    }
    
    public void mineBlock(int difficulty){
        nonce = 0;
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(true){
            hash = calculateHash();
            if(hash.substring(0,difficulty).equals(target))
                break;
            nonce++;
        }
        System.out.println("Block Mined!!! : " + hash);
    }
    
}
