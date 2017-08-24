
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
 * Clase genérica que encapsula los datos personales.
 * Usada para:
 * Proponente
 * Tecnico
 * Apoderado
 * @author rincostante
 */
@Entity
@Audited
@XmlRootElement
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Referencia al id de la Personas en el Registro Unico de Entidades (RUE)
     */
    private Long idRue;
    
    /**
     * Nombre completo o razón social de la persona, cacheado del servicio del RUE
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCompleto;
    
    /**
     * CUIT cacheado el RUE
     */
    private Long cuit;
    
    /**
     * Guarda el correo electrónico de la Persona, registrado en el RUE
     * Solo para los de que tengan el rol de Destinatario
     */
    @Column (length=100)
    @Size(message = "El campo email no puede tener más de 100 caracteres", max = 100)     
    private String email;
    
    /**
     * Paramétrica que indica el rol que cumple la Persona en el modelo:
     * Proponente
     * Técnico
     * Apoderado
     * etc.
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="rol_id", nullable=false)
    @NotNull(message = "Debe existir un Rol de la Persona")    
    private Parametrica rolPersona;
    
    /**
     * Para los casos de Técnicos y Apoderados, fecha de inicio de la vigencia del rol
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicioVigencia;
    
    /**
     * Para los casos de Técnicos y Apoderados, fecha de fin de la vigencia del rol
     */    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date finVigencia;
    
    /**
     * Campo que cachea el tipo de Persona del que se trata:
     * Física o Jurídica
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo tipo no puede ser nulo")
    @Size(message = "El campo tipo no puede tener más de 20 caracteres", min = 1, max = 20) 
    private String tipo;
    
    /**
     * Para los Proponentes, ruta del martillo
     */
    private String rutaArchivo;
    
    /**
     * Para los Proponentes, nombre del archivo
     */
    private String nombreArchivo;
    
    /**
     * Fecha de alta de la Persona
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
    
    /**
     * Condición de habilitado de la Persona
     */
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */    
    @Transient
    private Date fechaRevision;    
    
    /**
     * Campo que indica si la ruta a la imagen del martillo es temporal o definitiva
     */
    @Transient
    private boolean rutaTemporal;

    @XmlTransient
    public boolean isRutaTemporal() {
        return rutaTemporal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRutaTemporal(boolean rutaTemporal) {
        this.rutaTemporal = rutaTemporal;
    }

    @XmlTransient
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Long getIdRue() {
        return idRue;
    }

    public void setIdRue(Long idRue) {
        this.idRue = idRue;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    @XmlTransient
    public Parametrica getRolPersona() {
        return rolPersona;
    }

    public void setRolPersona(Parametrica rolPersona) {
        this.rolPersona = rolPersona;
    }

    @XmlTransient
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @XmlTransient
    public Date getFinVigencia() {
        return finVigencia;
    }

    public void setFinVigencia(Date finVigencia) {
        this.finVigencia = finVigencia;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @XmlTransient
    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
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

    /**
     * Para los roles que impliquen período de vigencia
     */
    @XmlTransient
    public boolean getVigencia() {
        Date fechaActual = new Date();
        if(fechaActual.after(finVigencia)){
            habilitado = false;
        }
        return habilitado;
    }
    
    @XmlTransient
    public boolean getHabilitado() {
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
        if (!(object instanceof Persona)) {
            return false;
        }
        Persona other = (Persona) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.servicios.gestionterritorial.entidades.Persona[ id=" + id + " ]";
    }
    
}
