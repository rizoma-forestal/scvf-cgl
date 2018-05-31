
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad que encapsula los datos estadísticos de superficies de autorizaciones:
 * total, solicitada y aprobada
 * @author rincostante
 */
@Entity
public class AutSupConsulta implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    /**
     * Variable privada: identificador del tipo de intervención
     */    
    private Long idtipoint;
    
    /**
     * Variable privada: suma de superficies totales de los predios en Has.
     */    
    private float suptotal;
    
    /**
     * Variable privada: suma de superficies solicitadas en Has.
     */    
    private float supsol;
    
    /**
     * Variable privada: suma de superficies autorizadas en Has.
     */    
    private float supaut;
    
    /**
     * Variable privada: nombre del tipo de intervención
     */    
    private String tipoint;

    ///////////////////////
    // métodos de acceso //
    ///////////////////////
    public Long getIdtipoint() {
        return idtipoint;
    }

    public void setIdtipoint(Long idtipoint) {
        this.idtipoint = idtipoint;
    }

    public float getSuptotal() {
        return suptotal;
    }

    public void setSuptotal(float suptotal) {
        this.suptotal = suptotal;
    }

    public float getSupsol() {
        return supsol;
    }

    public void setSupsol(float supsol) {
        this.supsol = supsol;
    }

    public float getSupaut() {
        return supaut;
    }

    public void setSupaut(float supaut) {
        this.supaut = supaut;
    }

    public String getTipoint() {
        return tipoint;
    }

    public void setTipoint(String tipoint) {
        this.tipoint = tipoint;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtipoint != null ? idtipoint.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AutSupConsulta)) {
            return false;
        }
        AutSupConsulta other = (AutSupConsulta) object;
        if ((this.idtipoint == null && other.idtipoint != null) || (this.idtipoint != null && !this.idtipoint.equals(other.idtipoint))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.AutSupConsulta[ id=" + idtipoint + " ]";
    }
    
}
