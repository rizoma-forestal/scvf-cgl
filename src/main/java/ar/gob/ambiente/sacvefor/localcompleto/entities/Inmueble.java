
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Encapsula los datos correspondientes a los inmuebles que solicitan 
 * Autorizaciones para la extracción de Productos
 * @author rincostante
 */
@Entity
public class Inmueble implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Nombre por el cual se lo puede reconocer al Inmueble
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 100 caracteres", min = 1, max = 100)    
    private String nombre;
    
    /**
     * Variable privada: Referencia al id de la Localidad en el Servicio Gestión Territorial
     */
    private Long idLocGt;
    
    /**
     * Variable privada: Nombre de la Localidad, cacheado del Servicio
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo localidad no puede ser nulo")
    @Size(message = "El campo localidad no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String localidad;
    
    /**
     * Variable privada: Nombre del Departamente, cacheado del Servicio
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo departamento no puede ser nulo")
    @Size(message = "El campo departamento no puede tener más de 50 caracteres", min = 1, max = 50)       
    private String departamento;
    
    /**
     * Variable privada: Nombre de la Provincia, cacheado del Servicio
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo provincia no puede ser nulo")
    @Size(message = "El campo provincia no puede tener más de 50 caracteres", min = 1, max = 50)      
    private String provincia;
    
    /**
     * Variable privada: Si el Inmueble hubiera surgido a partir de la subdivisión de otro Inmueble,
     * se guarda el inmueble origina.
     */
    @ManyToOne
    @JoinColumn(name="inmueble_id")   
    private Inmueble inmOrigen;
    
    /**
     * Variable privada: Si existiera, la identificación catastral
     */
    @Column (length=20)
    @Size(message = "El campo idCatastral no puede tener más de 20 caracteres", max = 20) 
    private String idCatastral;
    
    /**
     * Variable privada: Cadena con calle, número, y lo que corresponda, si corresponde
     */
    @Column (length=50)
    @Size(message = "El campo domicilio no puede tener más de 50 caracteres", max = 50)       
    private String domicilio;
    
    /**
     * Variable privada: Superficie total del inmueble
     */
    private double superficie;
    
    /**
     * Variable privada: Si existiera, coordenada de latitud para la ubicación puntual del inmueble
     */
    private double latitud;
    
    /**
     * Variable privada: Si existiera, coordenada de longitud para la ubicación puntual del inmueble
     */
    private double longitud;
    
    /**
     * Variable privada: Condición de habilitado del inmueble
     */
    private boolean habilitado;
    
    /**
     * Variable privada: Si el inmueble hubiera sido subdividido, muestra a los hijos
     */
    @OneToMany (mappedBy="inmOrigen")
    private List<Inmueble> inmHijos;
    
    /**
     * Constructor, instancia el listado de subdivisiones que el inmueble pudiera tener
     */
    public Inmueble(){
        inmHijos = new ArrayList<>();
    }

    // métodos de acceso
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdLocGt() {
        return idLocGt;
    }

    public void setIdLocGt(Long idLocGt) {
        this.idLocGt = idLocGt;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Inmueble getInmOrigen() {
        return inmOrigen;
    }

    public void setInmOrigen(Inmueble inmOrigen) {
        this.inmOrigen = inmOrigen;
    }

    public String getIdCatastral() {
        return idCatastral;
    }

    public void setIdCatastral(String idCatastral) {
        this.idCatastral = idCatastral;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public double getSuperficie() {
        return superficie;
    }

    public void setSuperficie(double superficie) {
        this.superficie = superficie;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public List<Inmueble> getInmHijos() {
        return inmHijos;
    }

    public void setInmHijos(List<Inmueble> inmHijos) {
        this.inmHijos = inmHijos;
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
        if (!(object instanceof Inmueble)) {
            return false;
        }
        Inmueble other = (Inmueble) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble[ id=" + id + " ]";
    }
    
}
