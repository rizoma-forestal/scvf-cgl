
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
 * Entidad que gestiona las tasas a depositar por según el tipo de Guía:
 * @author rincostante
 */
@Entity
public class TipoGuiaTasa implements Serializable {

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

    // métodos de acceso     
    public Parametrica getTipo() {
        return tipo;
    }

    public void setTipo(Parametrica tipo) {
        this.tipo = tipo;
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
        if (!(object instanceof TipoGuiaTasa)) {
            return false;
        }
        TipoGuiaTasa other = (TipoGuiaTasa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa[ id=" + id + " ]";
    }
    
}
