
package ar.gob.ambiente.sacvefor.localcompleto.util;

/**
 * Clase utilitaria para encapsular los datos estadísticos de superficies de autorizaciones:
 * @author rincostante
 */
public class AutSupConsulta {
    /**
     * Variable privada: 
     */
    private String nombreTitular;
    
    /**
     * Variable privada: 
     */
    private Long cuitTitular;
    
    /**
     * Variable privada: 
     */
    private float supTotal;
    
    /**
     * Variable privada: 
     */
    private float supSol;
    
    /**
     * Variable privada: 
     */
    private float supAut;
    
    /**
     * Variable privada: 
     */
    private String depto;
    
    /**
     * Variable privada: 
     */
    private String numOrden;

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public Long getCuitTitular() {
        return cuitTitular;
    }

    public void setCuitTitular(Long cuitTitular) {
        this.cuitTitular = cuitTitular;
    }

    public float getSupTotal() {
        return supTotal;
    }

    public void setSupTotal(float supTotal) {
        this.supTotal = supTotal;
    }

    public float getSupSol() {
        return supSol;
    }

    public void setSupSol(float supSol) {
        this.supSol = supSol;
    }

    public float getSupAut() {
        return supAut;
    }

    public void setSupAut(float supAut) {
        this.supAut = supAut;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(String numOrden) {
        this.numOrden = numOrden;
    }
}
