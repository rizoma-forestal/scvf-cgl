
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
 * Entidad que encapsula los diferentes tipos de Copias que se emitirá de una Guía.
 * En principio serán:
 * Original,
 * Duplicado,
 * Triplicado y
 * Cuatruplicado
 * @author rincostante
 */
@Entity
public class CopiaGuia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre que llevará la copia, ej: Original, Duplicado, etc.
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 30 caracteres", min = 1, max = 30)       
    private String nombre;
    
    /**
     * Destino de la impresión, ej: Productor, Delegación Forestal, Destinatario, etc.
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo destino no puede ser nulo")
    @Size(message = "El campo destino no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String destino;
    
    /**
     * El tipo de Guía a la que corresponda la CopiaGuia
     */
    @ManyToOne
    @JoinColumn(name="tipoguia_id")
    private TipoGuia tipoGuia;    
    
    private boolean habilitado;

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public TipoGuia getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(TipoGuia tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
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
        if (!(object instanceof CopiaGuia)) {
            return false;
        }
        CopiaGuia other = (CopiaGuia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Copia[ id=" + id + " ]";
    }
    
}
