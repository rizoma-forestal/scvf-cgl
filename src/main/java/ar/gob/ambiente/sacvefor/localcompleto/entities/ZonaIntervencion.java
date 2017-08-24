
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entidad que encapsula las diferentes zonas según el ordenamiento ambiental:
 * Roja
 * Verde
 * Amarilla
 * etc.
 * @author rincostante
 */
@Entity
public class ZonaIntervencion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Código o abreviatura de la Zona (I, II, III, etc)
     */
    @Column (nullable=false, length=10, unique=true)
    @NotNull(message = "El campo codigo no puede ser nulo")
    @Size(message = "El campo codigo no puede tener más de 10 caracteres", min = 1, max = 10)
    private String codigo;
    
    /**
     * Nombre de la Zona (Verde, Roja, Amarilla, etc)
     */
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String nombre;
    
    private boolean habilitado;
    
    /**
     * Campo que indica si la zona está o no seleccionada
     */
    @Transient
    private boolean selected;
    
    /**
     * Listado de los subtipos correspondientes a la Zona
     */
    @OneToMany (mappedBy="zona", orphanRemoval = true)
    @Basic(fetch = FetchType.LAZY)
    private List<SubZona> zonas;  

    public ZonaIntervencion(){
        zonas = new ArrayList<>();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public List<SubZona> getZonas() {
        return zonas;
    }

    public void setZonas(List<SubZona> zonas) {
        this.zonas = zonas;
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
        if (!(object instanceof ZonaIntervencion)) {
            return false;
        }
        ZonaIntervencion other = (ZonaIntervencion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion[ id=" + id + " ]";
    }
    
}
