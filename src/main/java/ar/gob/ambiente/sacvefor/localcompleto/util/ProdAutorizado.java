
package ar.gob.ambiente.sacvefor.localcompleto.util;

/**
 * Clase que encapsula los datos de un producto autorizado 
 * según productor, autorización, inmueble, departamento, especie, clasey unidad 
 * @author rincostante
 */
public class ProdAutorizado {
    
    /**
     * Variable privada: nombre completo del productor autorizado
     */    
    private String nombreCompleto;
    
    /**
     * Variable privada: cuit del productor autorizado
     */      
    private long cuit;
    
    /**
     * Variable privada: número de la Autorización
     */      
    private String numAut;
    
    /**
     * Variable privada: nombre del inmueble autorizado
     */      
    private String nombreInm;
    
    /**
     * Variable privada: identificación catastral del inmueble autorizado
     */      
    private String idCastastral;
    
    /**
     * Variable privada: departamento en el cual se ubica el inmueble autorizado
     */      
    private String depto;
    
    /**
     * Variable privada: nombre de la clase de comercialización (rollo, poste, leña, etc)
     */       
    private String clase;
    
    /**
     * Variable privada: nombre vulgar de la especie del producto autorizado
     */         
    private String especie;
    
    /**
     * Variable privada: cupo autorizado para el producto
     */          
    private float cupo;
    
    /**
     * Variable privada: saldo disponible actual para el producto
     */          
    private float saldo;
    
    /**
     * Variable privada: unidad de medida del producto autorizado
     */          
    private String unidad;
    
    /**
     * Variable privada: nombre de la cuenca dentro de la cual se encuentra el inmueble autorizado (si corresponde)
     */          
    private String cuenca;

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public long getCuit() {
        return cuit;
    }

    public void setCuit(long cuit) {
        this.cuit = cuit;
    }

    public String getNumAut() {
        return numAut;
    }

    public void setNumAut(String numAut) {
        this.numAut = numAut;
    }

    public String getNombreInm() {
        return nombreInm;
    }

    public void setNombreInm(String nombreInm) {
        this.nombreInm = nombreInm;
    }

    public String getIdCastastral() {
        return idCastastral;
    }

    public void setIdCastastral(String idCastastral) {
        this.idCastastral = idCastastral;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public float getCupo() {
        return cupo;
    }

    public void setCupo(float cupo) {
        this.cupo = cupo;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getCuenca() {
        return cuenca;
    }

    public void setCuenca(String cuenca) {
        this.cuenca = cuenca;
    }
    
    
}
