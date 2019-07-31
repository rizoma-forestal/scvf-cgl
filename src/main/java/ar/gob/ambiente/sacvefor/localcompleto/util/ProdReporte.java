
package ar.gob.ambiente.sacvefor.localcompleto.util;

/**
 * Clase que encapsula los datos del cupo autorizado y saldo disponible de los productos
 * @author rincostante
 */
public class ProdReporte {
    
    /**
     * Variable privada: número de la autorización
     */
    private String numero;
    
    /**
     * Variable privada: nombre vulgar de la especie
     */
    private String nombreVulgar;
    
    /**
     * Variable privada: nombre de la clase de comercialización (rollo, poste, leña, etc)
     */
    private String clase;
    
    /**
     * Variable privada: cantidad autorizada para la extración
     */
    private float cupo;
    
    /**
     * Variable privada: saldo disponible del producto
     */
    private float saldo;

    /**
     * Variable privada: unindad de medida mediante la que se comercializa el producto (M3, Tn, Me, etc.)
     */
    private String unidad;
    
    /**
     * Variable privada: nombre de la cuenca que podría estar asociada
     */
    public String cuenca;
    
    /**
     * Variable privada: nombre del departamento del inmueble autorizado
     */
    public String departamento;

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
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

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    
}
