
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase para manipular las Personas provenientes del servicio de Gestion Local
 * Ej.: Proponentes, Técnicos, Transportistas, Destinatarios, etc.
 * @author rincostante
 */
@XmlRootElement
public class Persona implements Serializable {

    private Long id;
    /**
     * Id de la Persona en el RUE
     */
    private Long idRue;  
    /**
     * Nombre completo o razón social de la persona, cacheado del servicio del RUE
     */
    private String nombreCompleto;
    private Long cuit;
    private String email;
    /**
     * Tipo de Persona
     * Ej.: Fïsica, Jurídica
     */
    private String tipo;

    /******************
     * Constructores **
     ******************/    
    public Persona() {
        this.id = Long.valueOf(0);
        this.idRue = Long.valueOf(0);
        this.nombreCompleto = "default";
        this.cuit = Long.valueOf(0);
        this.email = "default";
        this.tipo = "default";
    }    
    
    public Persona(Long id, Long idRue, String nombreCompleto, Long cuit, String email, String tipo) {
        this.id = id;
        this.idRue = idRue;
        this.nombreCompleto = nombreCompleto;
        this.cuit = cuit;
        this.email = email;
        this.tipo = tipo;
    }      

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
        return new StringBuffer(" id: ").append(id).
                append(" idRue: ").append(idRue).
                append(" tipo: ").append(tipo).
                append(" nombreCompleto: ").append(nombreCompleto).
                append(" cuit: ").append(cuit).
                append(" email: ").append(email).
                append(" tipo: ").append(tipo).toString();
    }
    
}
