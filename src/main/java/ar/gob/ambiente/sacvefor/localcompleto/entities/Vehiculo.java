
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos del Vehículo mediante el cual se transportan productos (items) de una Guía
 * Los datos provinen del servicio del registro único de entidades (RUE)
 * @author rincostante
 */
@Entity
@Audited
@XmlRootElement
public class Vehiculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Identificación de la Localidad en el servicio de registro único de entidades (RUE)
     */    
    private Long idRue;
    
    /**
     * Matrícula del vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo matricula no puede ser nulo")
    @Size(message = "El campo matricula no puede tener más de 20 caracteres", min = 1, max = 20)
    private String matricula;
    
    /**
     * Marca del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo marca no puede ser nulo")
    @Size(message = "El campo marca no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String marca;
    
    /**
     * Modelo del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo modelo no puede ser nulo")
    @Size(message = "El campo modelo no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String modelo;
    
    /**
     * Año del modelo del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    private int anio;
    
    /**
     * Persona física o jurídica que hace las veces de titular del Vehículo
     */
    @ManyToOne
    @JoinColumn(name="titular_id")
    private Persona titular;    
    
    /**
     * Fecha de registro del Vehículo
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;    
    
    /**
     * Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario; 
    
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */    
    @Transient
    private Date fechaRevision;     

    public Persona getTitular() {
        return titular;
    }

    public void setTitular(Persona titular) {
        this.titular = titular;
    }
    
    public Long getIdRue() {
        return idRue;
    }

    public void setIdRue(Long idRue) {
        this.idRue = idRue;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    @XmlTransient
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @XmlTransient
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    @XmlTransient
    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
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
        if (!(object instanceof Vehiculo)) {
            return false;
        }
        Vehiculo other = (Vehiculo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo[ id=" + id + " ]";
    }
    
}
