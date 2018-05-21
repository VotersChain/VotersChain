/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitvote;

/**
 *
 * @author paulo
 */
public class Candidato {
    private int id;
    private String candidateName;
    
    public Candidato(int id, String candidateName){
        this.id = id;
        this.candidateName = candidateName;
    }

    public int getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    
}
