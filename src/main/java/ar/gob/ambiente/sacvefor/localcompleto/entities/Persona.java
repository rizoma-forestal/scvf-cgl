
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
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Referencia al id de la Personas en el Registro Unico de Entidades (RUE)
     */
    private Long idRue;
    
    /**
     * Variable privada: Nombre completo o razón social de la persona, cacheado del servicio del RUE
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCompleto;
    
    /**
     * Variable privada: CUIT cacheado el RUE
     */
    private Long cuit;
    
    /**
     * Variable privada: Guarda el correo electrónico de la Persona, registrado en el RUE
     * Solo para los de que tengan el rol de Destinatario
     */
    @Column (length=100)
    @Size(message = "El campo email no puede tener más de 100 caracteres", max = 100)     
    private String email;
    
    /**
     * Variable privada: Paramétrica que indica el rol que cumple la Persona en el modelo
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
     * Variable privada: Para los casos de Técnicos y Apoderados, fecha de inicio de la vigencia del rol
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicioVigencia;
    
    /**
     * Variable privada: Para los casos de Técnicos y Apoderados, fecha de fin de la vigencia del rol
     */    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date finVigencia;
    
    /**
     * Variable privada: Campo que cachea el tipo de Persona del que se trata:
     * Física o Jurídica
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo tipo no puede ser nulo")
    @Size(message = "El campo tipo no puede tener más de 20 caracteres", min = 1, max = 20) 
    private String tipo;
    
    /**
     * Variable privada: Fecha de alta de la Persona
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
     * Variable privada: Condición de habilitado de la Persona
     */
    private boolean habilitado;
    
    /**
     * Variable privada no persistida: Campo que mostrará la fecha de las revisiones
     */    
    @Transient
    private Date fechaRevision;    
    
    // métodos de acceso
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    /**
     * Método que retorna la Parametrica correspondiente al rol de la Persona
     * no incluido en la entidad de para la API Rest
     * @return Parametrica rol de la persona
     */        
    @XmlTransient
    public Parametrica getRolPersona() {
        return rolPersona;
    }

    public void setRolPersona(Parametrica rolPersona) {
        this.rolPersona = rolPersona;
    }

    /**
     * Método que retorna la condición de temporal de la ruta de la imagen del martillo
     * no incluido en la entidad de para la API Rest
     * @return Date inicio de la vigencia de la persona
     */        
    @XmlTransient
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    /**
     * Método que retorna la fecha de fin de vigencia de la Guía
     * no incluido en la entidad de para la API Rest
     * @return Date fin de la vigencia de la persona
     */      
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

    /**
     * Método que retorna la fecha de alta de la persona
     * no incluido en la entidad de para la API Rest
     * @return Date fecha de alta de la persona
     */      
    @XmlTransient
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    /**
     * Método que retorna el usuario que registró o modificó la Persona
     * no incluido en la entidad de para la API Rest
     * @return Usuairio usuario correspondiente
     */      
    @XmlTransient
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Método que retorna el estado de vigencia de la persona, para los roles que impliquen período de vigencia
     * @return boolean verdadero o falso según la persona esté o no vigente
     */
    @XmlTransient
    public boolean getVigencia() {
        Date fechaActual = new Date();
        if(fechaActual.after(finVigencia)){
            habilitado = false;
        }
        return habilitado;
    }
    
    /**
     * Método que retorna la condición de habilitada de la persona
     * no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */      
    @XmlTransient
    public boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    /**
     * Método que retorna la fecha de revisión de la persona para su auditoría
     * no incluido en la entidad de para la API Rest
     * @return Date fecha de revisión
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
