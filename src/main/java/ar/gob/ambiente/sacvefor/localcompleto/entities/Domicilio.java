
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
 * Enitidad para gestionar los domicilios de las personas con el rol de destinatarios.
 * Cada uno deberá tener al menos un domicilio vinculado.
 * Al momento de seleccionar el destinatario de una guía se deberá seleccionar un domicilio
 * que quedará seteado en la EntidadGuia destino.
 * @author rincostante
 */
@Entity
public class Domicilio implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: nombre de la calle del domicilio
     */    
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo calle no puede ser nulo")
    @Size(message = "El campo calle no puede tener más de 20 caracteres", min = 1, max = 50)  
    private String calle;
    
    /**
     * Variable privada: número de puerta del domicilio
     */        
    @Column (nullable=false, length=10)
    @NotNull(message = "El numero calle no puede ser nulo")
    @Size(message = "El numero calle no puede tener más de 20 caracteres", min = 1, max = 10)  
    private String numero; 
    
    /**
     * Variable privada: piso del domicilio si corresponde
     */        
    @Column (nullable=true, length=10)
    @Size(message = "El numero calle no puede tener más de 10 caracteres", max = 10)  
    private String piso;   
    
    /**
     * Variable privada: departamento del domicilio si corresponde
     */        
    @Column (nullable=true, length=10)
    @Size(message = "El numero depto no puede tener más de 10 caracteres", max = 10)  
    private String depto;   
    
    /**
     * Variable privada: identificador de la localidad en la API Territorial
     */        
    private Long idLoc;
    
    /**
     * Variable privada: nombre de la localidad en la que se encuentra el domicilio
     */        
    @Column (nullable=false, length=100)
    @NotNull(message = "El localidad calle no puede ser nulo")
    @Size(message = "El localidad calle no puede tener más de 100 caracteres", min = 1, max = 100)  
    private String localidad; 
    
    /**
     * Variable privada: nombre del departamento que contiene la localidad
     */        
    @Column (nullable=false, length=50)
    @NotNull(message = "El departamento calle no puede ser nulo")
    @Size(message = "El departamento calle no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String departamento; 
    
    /**
     * Variable privada: nombre de la provincia correpondiente a la localidad y departamento en el que se encuentra el domicilio
     */        
    @Column (nullable=false, length=50)
    @NotNull(message = "El provincia calle no puede ser nulo")
    @Size(message = "El provincia calle no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String provincia; 
    
    /**
     * Variable privada: código postal correspondiente al domicilio
     */        
    @Column (nullable=true, length=10)
    @Size(message = "El codPostal depto no puede tener más de 10 caracteres", max = 10)  
    private String codPostal;       

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public Long getIdLoc() {
        return idLoc;
    }

    public void setIdLoc(Long idLoc) {
        this.idLoc = idLoc;
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

    public String getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(String codPostal) {
        this.codPostal = codPostal;
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
        if (!(object instanceof Domicilio)) {
            return false;
        }
        Domicilio other = (Domicilio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio[ id=" + id + " ]";
    }
    
}
