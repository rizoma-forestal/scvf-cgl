
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
 * Entidad para gestionar los datos de los clientes de la API
 * (CTRL y TRAZ). Estos usuarios serán los que inserten o editen entidades locales desde la API
 * @author rincostante
 */
@Entity
public class UsuarioApi implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: indica el nombre que identificará al cliente de la API
     * Ej: scvf-ctrl, scvf-traz, etc.
     */
    @Column(nullable=false, length=20, unique=true)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String nombre;
    
    /**
     * Variable privada: indica el rol (de tipo Parametrica) que tiene el usuario,
     * es decir, a qué cliente pertenece Ej: CLIENTE_CTRL, CLIENTE_TRAZ, etc.
     */
    @ManyToOne
    @JoinColumn(name="rol_id", nullable=false)
    @NotNull(message = "Debe existir un Rol")
    private Parametrica rol;
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Parametrica getRol() {
        return rol;
    }

    public void setRol(Parametrica rol) {
        this.rol = rol;
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
        if (!(object instanceof UsuarioApi)) {
            return false;
        }
        UsuarioApi other = (UsuarioApi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.UsuarioApi[ id=" + id + " ]";
    }
    
}
