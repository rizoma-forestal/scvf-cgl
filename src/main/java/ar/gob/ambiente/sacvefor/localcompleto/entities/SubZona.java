
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entidad que encapsula las diferentes sub zonas correspondientes a cada Zona:
 * I, II, III, etc
 * @author rincostante
 */
@Entity
public class SubZona implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Zona correspondiente a la Sub Zona
     */
    @ManyToOne
    @JoinColumn(name="zona_id", nullable=false)
    @NotNull(message = "Debe existir una Zona")    
    private ZonaIntervencion zona;
    
    /**
     * Nombre de la Sub Zona
     */    
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 100 caracteres", min = 1, max = 100)       
    private String nombre;
    
    private boolean habilitado;
    
    /**
     * Campo que indica si la sub zona está o no seleccionada
     */
    @Transient
    private boolean selected;    

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ZonaIntervencion getZona() {
        return zona;
    }

    public void setZona(ZonaIntervencion zona) {
        this.zona = zona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof SubZona)) {
            return false;
        }
        SubZona other = (SubZona) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.SubZona[ id=" + id + " ]";
    }
    
}
