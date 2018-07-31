
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Entidad que gestiona las clases de los productos:
 * Rollo
 * Carbón
 * Poste
 * etc.
 * @author rincostante
 */
@Entity
public class ProductoClase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    
    /**
     * Variable privada: Guarda la Unidad de medida correspondiente a la Clase
     */
    @ManyToOne
    @JoinColumn(name="unidad_id", nullable=false)
    @NotNull(message = "Debe existir una unidad")
    private ProductoUnidadMedida unidad;
    
    /**
     * Variable privada: condición de habilitado de la clase de producto
     */
    private boolean habilitado;

    // métodos de acceso
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ProductoUnidadMedida getUnidad() {
        return unidad;
    }

    public void setUnidad(ProductoUnidadMedida unidad) {
        this.unidad = unidad;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoClase)) {
            return false;
        }
        ProductoClase other = (ProductoClase) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase[ id=" + id + " ]";
    }
    
}
