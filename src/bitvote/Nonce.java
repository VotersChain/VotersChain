/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

import java.security.PublicKey;

/**
 *
 * @author paulo
 */
public class Nonce {
    private long nonce_id;
    private PublicKey pk;
    private int candidate_id;

    public Nonce(long nonce_id, PublicKey pk, int candidate_id) {
        this.nonce_id = nonce_id;
        this.pk = pk;
        this.candidate_id = candidate_id;
    }

    public long getNonce_id() {
        return nonce_id;
    }

    public PublicKey getPk() {
        return pk;
    }

    public int getCandidate_id() {
        return candidate_id;
    }

    public void setNonce_id(long nonce_id) {
        this.nonce_id = nonce_id;
    }

    public void setPk(PublicKey pk) {
        this.pk = pk;
    }

    public void setCandidate_id(int candidate_id) {
        this.candidate_id = candidate_id;
    }
    
    
    
}
