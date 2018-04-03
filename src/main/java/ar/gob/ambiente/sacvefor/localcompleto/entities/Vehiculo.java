
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
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Identificación de la Localidad en el servicio de registro único de entidades (RUE)
     */    
    private Long idRue;
    
    /**
     * Variable privada: Matrícula del vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo matricula no puede ser nulo")
    @Size(message = "El campo matricula no puede tener más de 20 caracteres", min = 1, max = 20)
    private String matricula;
    
    /**
     * Variable privada: Marca del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo marca no puede ser nulo")
    @Size(message = "El campo marca no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String marca;
    
    /**
     * Variable privada: Modelo del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo modelo no puede ser nulo")
    @Size(message = "El campo modelo no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String modelo;
    
    /**
     * Variable privada: Año del modelo del Vehículo, obtenida del servicio de registro único de entidades (RUE)
     */
    private int anio;
    
    /**
     * Variable privada: Persona física o jurídica que hace las veces de titular del Vehículo
     */
    @ManyToOne
    @JoinColumn(name="titular_id")
    private Persona titular;    
    
    /**
     * Variable privada: Fecha de registro del Vehículo
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;    
    
    /**
     * Variable privada: Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario; 
    
    /**
     * Variable privada: condición de habilitado
     */
    private boolean habilitado;
    
    /**
     * Variable privada no persistida: Campo que mostrará la fecha de las revisiones
     */    
    @Transient
    private Date fechaRevision;     

    // métodos de acceso
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

    /**
     * Método que retorna la fecha de alta del usuario 
     * no incluido en la entidad de para la API Rest
     * @return Date fecha de alta
     */
    @XmlTransient
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    /**
     * Método que retorna el usuario que registró o editó el vehículo
     * no incluido en la entidad de para la API Rest
     * @return Usuario usuario correspondiente
     */
    @XmlTransient
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método que retorna la condición de habilitado del vehículo
     * no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según corresponda
     */    
    @XmlTransient
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    /**
     * Método que retorna la fecha de revisión del vehículo para su auditoría
     * no incluido en la entidad de para la API Rest
     * @return Date fecha de revisión del vehículo
     */    
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
