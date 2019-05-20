
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entidad que encapsula las características de un rodal,
 * que es la fracción mínima de tierra en una intervención forestal.
 * Un predio puede estar subdividido por en uno o varios rodales.
 * A cada uno se lo identifica por su número de orden (único atributo).
 * Realizado a pedido de la Provincia de Misiones.
 * @author rincostante
 */
@Entity
public class Rodal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Número de orden del rodal
     */
    private int numOrden;
    
    /**
     * Variable privada: Flag que indica si el rodal listado 
     * está o no asigado a la entidad que se está gestionando, 
     * sea esta una Autorización o una Guía.
     * No se persiste.
     */
    @Transient
    private boolean asignado;

    public boolean isAsignado() {
        return asignado;
    }

    public void setAsignado(boolean asignado) {
        this.asignado = asignado;
    }

    public int getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(int numOrden) {
        this.numOrden = numOrden;
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
        if (!(object instanceof Rodal)) {
            return false;
        }
        Rodal other = (Rodal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal[ id=" + id + " ]";
    }
    
}
