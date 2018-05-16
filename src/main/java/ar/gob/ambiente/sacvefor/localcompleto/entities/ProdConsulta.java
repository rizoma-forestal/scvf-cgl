
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad que encapsula los datos del movimiento de un producto determinado
 * @author rincostante
 */
@Entity
public class ProdConsulta implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * Variable privada: identificador del Producto
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProd;
    
    /**
     * Variable privada: nombre de la especie local registrada
     */    
    private String nombreVulgar;
    
    /**
     * Variable privada: nombre de la clase de comercialización (rollo, poste, leña, etc)
     */    
    private String clase;
    
    /**
     * Variable privada: unindad de medida mediante la que se comercializa el producto (M3, Tn, Me, etc.)
     */    
    private String unidad;
    
    /**
     * Variable privada: total del producto para el departamento consultado
     */
    private float total;
    
    
    /**
     * Constructor que instancia el listado de productos por departamento
     */
    public ProdConsulta(){
    }
    
    ///////////////////////
    // Métodos de acceso //
    ///////////////////////  
    public float getTotal() {
        return total;
    }
     
    public void setTotal(float total) {
        this.total = total;
    }

    public Long getIdProd() {
        return idProd;
    }

    public void setIdProd(Long idProd) {
        this.idProd = idProd;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
}
