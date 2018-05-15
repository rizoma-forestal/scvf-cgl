
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos del Transporte de una Guía.
 * @author rincostante
 */
@XmlRootElement
public class Transporte implements Serializable {
    private Long id;
    /**
     * Vehículo de transporte
     */
    private Vehiculo vehiculo;
    /**
     * Si lo hubiera, la matrícula del acoplado
     */
    private String acoplado;
    /**
     * Nombre del conductor
     */
    private String condNombre;
    /**
     * DNI del conductor
     */    
    private Long condDni;
    
    /******************
     * Constructores **
     ******************/
    public Transporte(){
        this.id = Long.valueOf(0);
        this.vehiculo = new Vehiculo();
        this.acoplado = "default";
        this.condNombre = "default";
        this.condDni = Long.valueOf(0);
    }
    
    public Transporte(Long id, Vehiculo vehiculo, String acoplado, String condNombre, Long condDni){
        this.id = id;
        this.vehiculo = vehiculo;
        this.acoplado = acoplado;
        this.condNombre = condNombre;
        this.condDni = condDni;
    }

    /**********************
     * Métodos de acceso **
     **********************/    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getAcoplado() {
        return acoplado;
    }

    public void setAcoplado(String acoplado) {
        this.acoplado = acoplado;
    }

    public String getCondNombre() {
        return condNombre;
    }

    public void setCondNombre(String condNombre) {
        this.condNombre = condNombre;
    }

    public Long getCondDni() {
        return condDni;
    }

    public void setCondDni(Long condDni) {
        this.condDni = condDni;
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
        if (!(object instanceof Transporte)) {
            return false;
        }
        Transporte other = (Transporte) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuffer(" id: ").append(id).
                append(" vehiculo: ").append(vehiculo.getMatricula()).
                append(" acoplado: ").append(acoplado).
                append(" condNombre: ").append(condNombre).
                append(" condDni: ").append(acoplado).toString();
    }    
}
