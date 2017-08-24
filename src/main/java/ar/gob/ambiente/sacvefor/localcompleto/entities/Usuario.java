
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad para gestionar los Usuarios de la aplicación
 * @author rincostante
 */
@Entity
@XmlRootElement
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Guarda el rol al que pertenece el usuario
     */
    @ManyToOne
    @JoinColumn(name="rol_id", nullable=false)
    @NotNull(message = "Debe existir un Rol")    
    private Parametrica rol;
    
    /**
     * Será el DNI del usuario que oficiará como nombre de usuario
     */
    private Long login;
    
    /**
     * Nombre y apellido del usuario
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombreCompleto no puede ser nulo")
    @Size(message = "El campo nombreCompleto no puede tener más de 50 caracteres", min = 1, max = 50)       
    private String nombreCompleto;
    
    /**
     * Clave encriptada que generará el sistema automáticamente la primera vez y 
     * solicitará al usuario su cambio cuando realice la primera sesión.
     */
    @Column (length=100)
    @Size(message = "el campo clave no puede tener más de 100 caracteres", max = 100)   
    private String clave;
    
    /**
     * Correo electrónico válido del usuario al que se le remitrirán las credenciales de acceso
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo email no puede ser nulo")
    @Size(message = "El campo email no puede tener más de 50 caracteres", min = 1, max = 50)    
    private String email;
    
    /**
     * Fecha de alta del usuario
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Fecha de la última modificación de los datos del usuario
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaModif;    
    
    /**
     * Fecha de la última vez que el usuario registra una sesión en la aplicación
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaUltimoLogin;
    
    /**
     * indica si el usuario solo se logueó una vez, en cuyo caso no cambió la contraseña
     */
    private boolean primeraVez;    
    
    /**
     * Estado de habilitado
     */
    private boolean habilitado;
    
    public Usuario(){
    }

    public boolean isPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(boolean primeraVez) {
        this.primeraVez = primeraVez;
    }

    public Parametrica getRol() {
        return rol;
    }

    public void setRol(Parametrica rol) {
        this.rol = rol;
    }

    public Long getLogin() {
        return login;
    }

    public void setLogin(Long login) {
        this.login = login;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaModif() {
        return fechaModif;
    }

    public void setFechaModif(Date fechaModif) {
        this.fechaModif = fechaModif;
    }

    public Date getFechaUltimoLogin() {
        return fechaUltimoLogin;
    }

    public void setFechaUltimoLogin(Date fechaUltimoLogin) {
        this.fechaUltimoLogin = fechaUltimoLogin;
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
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario[ id=" + id + " ]";
    }
    
}
