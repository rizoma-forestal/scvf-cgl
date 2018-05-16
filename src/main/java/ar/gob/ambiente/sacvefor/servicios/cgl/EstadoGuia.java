
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos del Estado de una Guía.
 * @author rincostante
 */
@XmlRootElement
public class EstadoGuia implements Serializable {
    private Long id;
    private String nombre;
    
    /******************
     * Constructores **
     ******************/
    public EstadoGuia(){
        this.id = Long.valueOf(0);
        this.nombre = "default";
    }
    
    public EstadoGuia(Long id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof EstadoGuia)) {
            return false;
        }
        EstadoGuia other = (EstadoGuia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuffer(" id: ").append(id).
                append(" nombre: ").append(nombre).toString();
    }       
}
