
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entidad que encapsula los datos relativos a las 
 * delegaciones forestales del interior de la provincia
 * @author rincostante
 */
@Entity
public class Delegacion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: nombre de la delegación forestal
     */    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 50 caracteres", min = 1, max = 50)       
    private String nombre;
    
    /**
     * Variable privada: identificador de la localidad en el servicio territorial
     */    
    private Long idLoc;
    
    /**
     * Variable privada: nombre de la localidad en la que se ubica la delegación forestal
     */  
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo localidad no puede ser nulo")
    @Size(message = "El campo localidad no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String localidad;
    
    /**
     * Variable privada: domicilio en el que se ubica la delegación forestal
     */    
    @Column (nullable=false, length=70)
    @NotNull(message = "El campo domicilio no puede ser nulo")
    @Size(message = "El campo domicilio no puede tener más de 70 caracteres", min = 1, max = 70)  
    private String domicilio;
    
    /**
     * Variable privada: flag que define la condición de habilitada de la delegación forestal
     */
    private boolean habilitada;
    
    ///////////////////////
    // métodos de acceso //
    ///////////////////////    
    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Long idLoc) {
        this.idLoc = idLoc;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
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
        if (!(object instanceof Delegacion)) {
            return false;
        }
        Delegacion other = (Delegacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.trazabilidad.entities.Delegacion[ id=" + id + " ]";
    }
    
}
