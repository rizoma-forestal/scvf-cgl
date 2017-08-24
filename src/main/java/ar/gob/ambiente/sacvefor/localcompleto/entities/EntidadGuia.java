
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
 * Entidad que encapsula los datos del EntidadGuia.
 * Si bien la Autorización podrá tener uno o más Proponentes, las Guías solo tendrán un EntidadGuia 
 * que será uno de los Proponentes del Proyecto.
 * Lo mismo ocurre con el inmueble de procedencia de los Productos, se obtendrá el primero y se cachearán los datos
 * Los datos son cacheados del Proponente al generar la guía.
 * No podrá haber más de una EntidadGuia para el mismo par Productor - Inmueble
 * @author rincostante
 */
@Entity
@Audited
@XmlRootElement
public class EntidadGuia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Identificación de la Persona en el servicio de registro único de entidades (RUE)
     */
    private Long idRue;
    
    /**
     * Paramétrica que indica el tipo de Entidad Guía:
     * Origen
     * Destino
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="tipoentidad_id", nullable=false)
    @NotNull(message = "Debe existir un tipo de EntidadGuia")    
    private Parametrica tipoEntidadGuia;
    
    /**
     * Nombre completo del Productor si es física o razón social si es jurídica
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCompleto;
    
    /**
     * Tipo de persona:
     * Física
     * Jurídica
     */
    @Column (nullable=false, length=20)
    @NotNull(message = "El campo tipoPersona no puede ser nulo")
    @Size(message = "El campo tipoPersona no puede tener más de 20 caracteres", min = 1, max = 20)   
    private String tipoPersona;
    
    /**
     * Cuit del Productor
     */
    private Long cuit;
    
    /**
     * Guarda el correo electrónico cacheado de la Persona
     * Solo para las EntidadGuia destino
     */
    @Column (length=100)
    @Size(message = "El campo email no puede tener más de 100 caracteres", max = 100)     
    private String email;    
    
    /**
     * Identificación de la Localidad en el servicio de gestión territorial
     */
    private Long idLocGT;
    
    /**
     * Identificación catastral del Inmueble productor (cacheado del Inmueble)
     * Solo para Origen
     */
    @Column (length=20)
    @Size(message = "El campo inmCatastro no puede tener más de 20 caracteres", max = 20) 
    private String inmCatastro;
    
    /**
     * Nombre por el cual se lo puede reconocer al Inmueble (cacheado del Inmueble)
     * Solo para Origen
     */
    @Column (length=100)
    @Size(message = "El campo inmNombre no puede tener más de 100 caracteres", max = 100)    
    private String inmNombre;  
    
    /**
     * Domicilio del destino
     * Solo para Destino
     */
    @Column (length=100)
    @Size(message = "El campo inmDomicilio no puede tener más de 100 caracteres", max = 100)    
    private String inmDomicilio;
    
    /**
     * Número de Autorización, solo para Guías que toman productos de una Autorización
     * Solo para Origen
     */
    @Column (length=30)
    @Size(message = "El campo numAutorizacion no puede tener más de 30 caracteres", max = 30)    
    private String numAutorizacion;     
    
    /**
     * Nombre de la Localidad del Inmueble desde el cual se realiza la extracción
     */
    private String localidad;

    /**
     * Nombre del Departamento del Inmueble desde el cual se realiza la extracción
     */    
    private String departamento;
    
    /**
     * Nombre de la Provincia del Inmueble desde el cual se realiza la extracción
     */  
    private String provincia;
    
    /**
     * Fecha de registro del EntidadGuia
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlTransient
    public String getInmDomicilio() {
        return inmDomicilio;
    }

    public void setInmDomicilio(String inmDomicilio) {
        this.inmDomicilio = inmDomicilio;
    }

    @XmlTransient
    public String getNumAutorizacion() {
        return numAutorizacion;
    }

    public void setNumAutorizacion(String numAutorizacion) {
        this.numAutorizacion = numAutorizacion;
    }

    @XmlTransient
    public String getInmCatastro() {
        return inmCatastro;
    }

    public void setInmCatastro(String inmCatastro) {
        this.inmCatastro = inmCatastro;
    }

    @XmlTransient
    public String getInmNombre() {
        return inmNombre;
    }

    public void setInmNombre(String inmNombre) {
        this.inmNombre = inmNombre;
    }
    
    @XmlTransient
    public Parametrica getTipoEntidadGuia() {
        return tipoEntidadGuia;
    }

    public void setTipoEntidadGuia(Parametrica tipoEntidadGuia) {
        this.tipoEntidadGuia = tipoEntidadGuia;
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
    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
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

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public Long getIdLocGT() {
        return idLocGT;
    }

    public void setIdLocGT(Long idLocGT) {
        this.idLocGT = idLocGT;
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

    @XmlTransient
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
        if (!(object instanceof EntidadGuia)) {
            return false;
        }
        EntidadGuia other = (EntidadGuia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Productor[ id=" + id + " ]";
    }
    
}
