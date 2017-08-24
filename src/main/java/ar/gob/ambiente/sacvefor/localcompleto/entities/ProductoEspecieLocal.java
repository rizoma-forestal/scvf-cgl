
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
 * Entidad que guarda una referencia a la Especie obtenida del servicio de Taxonomía y
 * cachea el resto de sus datos, para ser utilizada como una réplica de la Especie obtenida
 * pero persistida localmente. Solo se le agregará el nombre vulgar local
 * @author rincostante
 */
@Entity
public class ProductoEspecieLocal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Id de referenica a la Especie en el servicio de Taxonomía (especiesVegetales)
     */
    private Long idTax;
    
    /**
     * Nombre científico cacheado del servicio, compuesto por género/especie
     */
    @Column (nullable=false, length=100, unique=true)
    @NotNull(message = "El campo nombreCientifico no puede ser nulo")
    @Size(message = "El campo nombreCientifico no puede tener más de 100 caracteres", min = 1, max = 100)   
    private String nombreCientifico;
    
    /**
     * Nombre vulgar de la especie en el ámbito local
     * ingresado al momento de registrar. Editable
     */
    @Column (nullable=false, length=50, unique=true)
    @NotNull(message = "El campo nombreVulgar no puede ser nulo")
    @Size(message = "El campo nombreVulgar no puede tener más de 50 caracteres", min = 1, max = 50) 
    private String nombreVulgar;
    
    /**
     * En este campo se cacheará el sinónimo del servicio, se podrá agregar más datos al momento del registro o edición
     */
    @Column (nullable=true, length=500)
    @Size(message = "El campo obs no puede tener más de 3500 caracteres", max = 500)    
    private String obs;
    
    private boolean habilitado; 

    public Long getIdTax() {
        return idTax;
    }

    public void setIdTax(Long idTax) {
        this.idTax = idTax;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
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
        if (!(object instanceof ProductoEspecieLocal)) {
            return false;
        }
        ProductoEspecieLocal other = (ProductoEspecieLocal) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal[ id=" + id + " ]";
    }
    
}
