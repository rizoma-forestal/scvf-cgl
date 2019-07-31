
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
     * Variable privada: Número de orden del rodal.
     * Originalmente int, a pedido de Misiones se lo llevó a un String
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo numOrden no puede ser nulo")
    @Size(message = "El campo numOrden no puede tener más de 30 caracteres", min = 1, max = 30)     
    private String numOrden;
    
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

    public String getNumOrden() {
        return numOrden;
    }

    public void setNumOrden(String numOrden) {
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
