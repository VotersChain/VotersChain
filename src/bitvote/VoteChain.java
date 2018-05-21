/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;


public class VoteChain {
    
	private static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 3;	
	public static Vote genesisVote;

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        
        for(Block item : blockchain){
            if(item.getPreviousHash().equals("0")){
                continue;
            }
            
            currentBlock = item;
            previousBlock = blockchain.get(blockchain.indexOf(item)-1);
			
	if (!(currentBlock.getMerkleRoot().equals(StringUtils.getMerkleRoot(currentBlock.getData())))) {
		System.out.println("Current MerkeRoot not equal (list of votes not valid)");
		return false;
	}
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
	
	public static void addBlock(Block newBlock) 
	{
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
	public static Boolean blockchainHadVoted(PublicKey pk, int id_eleicao) 
	{
		//verificação nos blocos minados
		for (Block item : blockchain) {
			for (Vote v : item.getData()) {
				if (v.sender.equals(pk) && v.id_eleicao==id_eleicao) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Boolean nonMinedBlockhadVoted(PublicKey pk, ArrayList<Vote> votes, int id_eleicao)
	{
            //verificação nos votos que nao foram minados
            if(votes.isEmpty())
                return false;
            for (Vote vote : votes) {
                if(vote.sender.equals(pk) && vote.id_eleicao==id_eleicao)
                    return true;
            }
		return false;
	}

	public static void main(String[] args) throws Exception 
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//Create wallets
		VotersWallet walletA = new VotersWallet(1);
		VotersWallet walletB= new VotersWallet(1);
		VotersWallet walletC = new VotersWallet(1);
		VotersWallet walletD= new VotersWallet(1);
		VotersWallet voteBase = new VotersWallet(1);
		
		//create genesis Vote, which sends 1 vote to all wallets:
	
		genesisVote = new Vote(voteBase.publicKey, 000000, 1, 0);
		genesisVote.generateSignature(voteBase.privateKey);
		genesisVote.voteId = "0";
		
		//Create and minning genesis block
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addVote(genesisVote);
		addBlock(genesis);
		
		//testes
		long candidateX = StringUtils.generateNonce();
		System.out.println(candidateX);
		long candidateY= StringUtils.generateNonce();
		System.out.println(candidateY);
		//System.out.println(StringUtils.getStringFromKey(walletA.publicKey));
                
                ArrayList <Vote> nonMinedVotes = new ArrayList<Vote>();

		Block block1 = new Block(genesis.getHash());
		System.out.println("\nWalletA is Attempting to vote on candidate X...");
                
                if(blockchainHadVoted(walletA.publicKey,1) || nonMinedBlockhadVoted(walletA.publicKey, nonMinedVotes,1)){
			System.out.println("TRUE");
			 System.out.println("votante A  ja votou");
		}
		else{
			System.out.println("Pode Votar");
			block1.addVote(walletA.sendVote(candidateX,walletA.n_votes,1 ));
                        nonMinedVotes.add(walletA.sendVote(candidateX,walletA.n_votes,1 ));
		}
                
                System.out.println("\nWalletA is Attempting to vote on candidate AGAIN...");
                
                if(blockchainHadVoted(walletA.publicKey,1) || nonMinedBlockhadVoted(walletA.publicKey, nonMinedVotes,1)){
			System.out.println("TRUE");
			 System.out.println("votante A  ja votou");
		}
		else{
			System.out.println("Pode Votar");
			block1.addVote(walletA.sendVote(candidateX,walletA.n_votes,1 ));
                        nonMinedVotes.add(walletA.sendVote(candidateX,walletA.n_votes,1 ));
		}
                
                System.out.println("\nWalletB is Attempting to vote on candidate X...");
		 if(blockchainHadVoted(walletB.publicKey,2) || nonMinedBlockhadVoted(walletB.publicKey, nonMinedVotes,2)){
			 System.out.println("TRUE");
			 System.out.println("votante B ja votou");
		}
		else{
			 System.out.println("Pode Votar");
			 block1.addVote(walletB.sendVote(candidateX,walletB.n_votes,2 ));
                         nonMinedVotes.add(walletB.sendVote(candidateX,walletB.n_votes,2 ));
		 }
                 
                 addBlock(block1);
                 nonMinedVotes.clear();
		
		Block block2 = new Block(block1.getHash());
                System.out.println("\nWalletC is Attempting to vote on candidate Y...");
                if(blockchainHadVoted(walletC.publicKey,2) || nonMinedBlockhadVoted(walletC.publicKey, nonMinedVotes,2)){
			System.out.println("TRUE");
			System.out.println("Votante C ja votou");
		}
		else{
			System.out.println("Pode Votar");
			block2.addVote(walletC.sendVote(candidateY,walletC.n_votes, 2));
                        nonMinedVotes.add(walletC.sendVote(candidateY,walletC.n_votes, 2));
		}
                System.out.println("\nWalletC is Attempting to vote on candidate Y AGAIN");
                if(blockchainHadVoted(walletC.publicKey,1) || nonMinedBlockhadVoted(walletC.publicKey, nonMinedVotes,1)){
			System.out.println("TRUE");
			System.out.println("Votante C ja votou");
		}
		else{
			System.out.println("Pode Votar");
			block2.addVote(walletC.sendVote(candidateY,walletC.n_votes, 1));
                        nonMinedVotes.add(walletC.sendVote(candidateY,walletC.n_votes, 1));
		}
                
                System.out.println("\nWalletD is Attempting to vote on candidate Y...");
		if(blockchainHadVoted(walletD.publicKey,2) || nonMinedBlockhadVoted(walletD.publicKey, nonMinedVotes,2)){
			System.out.println("TRUE");
			System.out.println("Votante D ja votou");
		}
		else{
			System.out.println("Pode Votar");
			block2.addVote(walletD.sendVote(candidateY,walletD.n_votes, 2 ));
                        nonMinedVotes.add(walletD.sendVote(candidateY,walletD.n_votes, 2 ));
		 }
                addBlock(block2);
                nonMinedVotes.clear();
		
		System.out.println("\n");
		System.out.println("\n");
		//block1.ImprimeBlock(1);
		//block2.ImprimeBlock(2);
		
		System.out.println(isChainValid());
		
		
		
	}
    
}
