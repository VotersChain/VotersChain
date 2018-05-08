/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.util.ArrayList;

/**
 *
 * @author jferr
 */
public class VoteChain {
    
    private static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 6;

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        
        for(Block item : blockchain){
            if(item.getPreviousHash().equals("0")){
                continue;
            }
            
            currentBlock = item;
            previousBlock = blockchain.get(blockchain.indexOf(item)-1);
            if(!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("Current Hash not equal");
                return false;
            }
            
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash())){
                System.out.println("Current Hash not equal");
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        blockchain.add(new Block("Hi im the frist block","0"));
        System.out.println("Trying to Mine block 1... ");
        blockchain.get(0).mineBlock(difficulty);
        
        blockchain.add(new Block("Hi im the second block",blockchain.get(blockchain.size()-1).getHash()));
        System.out.println("Trying to Mine block 2... ");
        blockchain.get(1).mineBlock(difficulty);
        
        blockchain.add(new Block("Hi im the third block",blockchain.get(blockchain.size()-1).getHash()));
        System.out.println("Trying to Mine block 3... ");
        blockchain.get(2).mineBlock(difficulty); 
        
        System.out.println("\nBlockchain is Valid: " + isChainValid());
        
        System.out.println();
        blockchain.forEach((item) -> {
            System.out.println(item.toString());
        });
    }
    
}
