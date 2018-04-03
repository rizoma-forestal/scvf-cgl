
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
 * Entidad que encapsula los diferentes Estados que puede tomar una Autorización 
 * y la condición frente a determinadas acciones
 * Bloqueado
 * Carga Inicial
 * Habilitado
 * Suspendido
 * etc.
 * @author rincostante
 */
@Entity
public class EstadoAutorizacion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Nombre completo del Estado
     */
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String nombre;
    
    /**
     * Variable privada: Código o abreviatura del Estado
     * ej: HAB
     */
    @Column (nullable=false, length=10, unique=true)
    @NotNull(message = "El campo codigo no puede ser nulo")
    @Size(message = "El campo codigo no puede tener más de 50 caracteres", min = 1, max = 10)     
    private String codigo;
    
    /**
     * Variable privada: Condición frente a la funcionalidad de edición
     */
    private boolean habilitaEdicion;
    
    /**
     * Variable privada: Condición frente a la funcionalidad de emisión de Guías
     */
    private boolean habilitaEmisionGuia;
    
    /**
     * Variable privada: Condición de habilitado del estado
     */
    private boolean habilitado;    
    

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean isHabilitaEdicion() {
        return habilitaEdicion;
    }

    public void setHabilitaEdicion(boolean habilitaEdicion) {
        this.habilitaEdicion = habilitaEdicion;
    }

    public boolean isHabilitaEmisionGuia() {
        return habilitaEmisionGuia;
    }

    public void setHabilitaEmisionGuia(boolean habilitaEmisionGuia) {
        this.habilitaEmisionGuia = habilitaEmisionGuia;
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
        if (!(object instanceof EstadoAutorizacion)) {
            return false;
        }
        EstadoAutorizacion other = (EstadoAutorizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion[ id=" + id + " ]";
    }
    
}
