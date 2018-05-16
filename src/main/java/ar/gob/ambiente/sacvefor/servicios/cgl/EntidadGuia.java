
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos del EntidadGuia. Servirá tanto para orígenes como destino de las Guías
 * @author rincostante
 */
@XmlRootElement
public class EntidadGuia implements Serializable {
    private Long id;
    /**
     * Id de registro de la Persona en el RUE
     */
    private Long idRue;
    private String nombreCompleto;
    private String tipoPersona;
    private Long cuit;
    private String email;
    /**
     * Id de registro de la Localidad en Gestión Territorial
     */
    private Long idLocGT;
    private String localidad;
    private String departamento;
    private String provincia;
    
    /******************
     * Constructores **
     ******************/
    
    public EntidadGuia(){
        this.id = Long.valueOf(0);
        this.idRue = Long.valueOf(0);
        this.nombreCompleto = "default";
        this.tipoPersona = "default";
        this.cuit = Long.valueOf(0);
        this.email = "default";
        this.idLocGT = Long.valueOf(0);
        this.localidad = "default";
        this.departamento = "default";
        this.provincia = "default";
    }
    
    public EntidadGuia(Long id, Long idRue, String nombreCompleto, String tipoPersona, Long cuit, String email, Long idLocGT, String localidad, String departamento, String provincia){
        this.id = id;
        this.idRue = idRue;
        this.nombreCompleto = nombreCompleto;
        this.tipoPersona = tipoPersona;
        this.cuit = cuit;
        this.email = email;
        this.idLocGT = idLocGT;
        this.localidad = localidad;
        this.departamento = departamento;
        this.provincia = provincia;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return new StringBuffer(" id: ").append(id).
                append(" idRue: ").append(idRue).
                append(" nombreCompleto: ").append(nombreCompleto).
                append(" tipoPersona: ").append(tipoPersona).
                append(" cuit: ").append(cuit).
                append(" email: ").append(email).toString();
    }      
}
