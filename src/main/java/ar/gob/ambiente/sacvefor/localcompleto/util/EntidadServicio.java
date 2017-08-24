

package ar.gob.ambiente.sacvefor.localcompleto.util;

import java.io.Serializable;

/**
 * Este objeto sencillo se crea con el fin de gestionar los listados
 * devueltos por un servicio.
 * @author rincostante
 */
public class EntidadServicio implements Serializable{
    
    private Long id;
    private String nombre;
    
    public EntidadServicio(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }    
    
    public EntidadServicio() {
        
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
    public boolean equals(Object other) {
        return (other instanceof EntidadServicio) && (id != null)
            ? id.equals(((EntidadServicio) other).id)
            : (other == this);
    }

    @Override
    public int hashCode() {
        return (id != null)
            ? (this.getClass().hashCode() + id.hashCode())
            : super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("EntidadServicio[%d, %s]", id, nombre);
    }
}
