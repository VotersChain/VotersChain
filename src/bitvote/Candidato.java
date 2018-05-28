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
    private int total;
    
    public Candidato(int id, int total){
        this.id = id;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int x) {
        this.total += x;
    }

    @Override
    public String toString() {
        return "Candidato{" + "id=" + id + ", total=" + total + '}';
    }

    
    
    
}
