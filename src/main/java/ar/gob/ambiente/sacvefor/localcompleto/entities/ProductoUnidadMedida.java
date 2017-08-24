
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entidad que gestiona las unidades de medida de los diferentes productos:
 * Unidad
 * Metro cúbico
 * Tonelada
 * etc
 * @author rincostante
 */
@Entity
public class ProductoUnidadMedida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable=false, length=30, unique=true)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 30 caracteres", min = 1, max = 30)       
    private String nombre;
    
    @Column (nullable=false, length=10, unique=true)
    @NotNull(message = "El campo abreviatura no puede ser nulo")
    @Size(message = "El campo abreviatura no puede tener más de 10 caracteres", min = 1, max = 10)       
    private String abreviatura;
    
    /**
     * Guarda el rol al que pertenece el usuario
     */
    @ManyToOne
    @JoinColumn(name="tipoNum_id", nullable=false)
    @NotNull(message = "Debe existir un Tipo numérico")    
    private Parametrica tipoNum;    
    
    private boolean habilitado;

    public Parametrica getTipoNum() {
        return tipoNum;
    }

    public void setTipoNum(Parametrica tipoNum) {
        this.tipoNum = tipoNum;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
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
        if (!(object instanceof ProductoUnidadMedida)) {
            return false;
        }
        ProductoUnidadMedida other = (ProductoUnidadMedida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida[ id=" + id + " ]";
    }
    
}
