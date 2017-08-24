
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos del Transporte, para los casos de las Guías que así lo requieran.
 * De ser así, la relación será de uno a uno y de composición, es decir que el Transporte solo tendrá
 * existencia mediante su vinculación con la Guía
 * @author rincostante
 */
@Entity
@XmlRootElement
public class Transporte implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Vehículo que compone el transporte
     */
    @ManyToOne
    @JoinColumn(name="vehiculo_id", nullable=false)
    @NotNull(message = "Debe existir una vehiculo")
    private Vehiculo vehiculo;
    
    /**
     * En los casos que corresponda, se agregará la matrícula del acoplado
     */
    @Column (length=20)
    @Size(message = "El campo acoplado no puede tener más de 20 caracteres", max = 20)
    private String acoplado;
    
    /**
     * Nombre completo del Conductor del Vehículo que realizará el transporte
     */
    @Column (length=100, nullable=false)
    @NotNull(message = "Debe existir una condNombre")
    @Size(message = "El campo condNombre no puede tener más de 100 caracteres", max = 100)
    private String condNombre;
    
    private Long condDni;

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
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Transporte[ id=" + id + " ]";
    }
    
}
