
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
 * Entidad que gestiona las tasas a depositar por la extracción de los diferentes productos:
 * @author rincostante
 */
@Entity
public class ProductoTasa implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Paramétrica cuyo tipo es "Tipos de Tasa"
     */
    @ManyToOne
    @JoinColumn(name="tipoTasa_id", nullable=false)
    @NotNull(message = "Debe existir un Tipo")
    private Parametrica tipo;
    
    /**
     * Variable privada: Monto que deberá abonarse por la tasa
     * Los montos son por unidad
     */
    private float monto;

    /**********************
     * Métodos de acceso **
     **********************/
    public Parametrica getTipo() {
        return tipo;
    }

    public void setTipo(Parametrica tipo) {
        this.tipo = tipo;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
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
        if (!(object instanceof ProductoTasa)) {
            return false;
        }
        ProductoTasa other = (ProductoTasa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoTasa[ id=" + id + " ]";
    }
    
}
