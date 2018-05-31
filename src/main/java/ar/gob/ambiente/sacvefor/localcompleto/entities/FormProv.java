
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad que encapsula los datos correspondientes a un formulario provisorio a partir del cual se genera una guía remito.
 * Solo para los casos en que así esté configurado en la variable "ImprimeFormularios" del config.properties
 * @author rincostante
 */
@Entity
public class FormProv implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: indica el código de guía de la cual tomó el formulario
     */
    private String codGuia;
    
    /**
     * Variable privada: indica el número de formulario que tomó
     */    
    private int numFormuario;

    public String getCodGuia() {
        return codGuia;
    }

    public void setCodGuia(String codGuia) {
        this.codGuia = codGuia;
    }

    public int getNumFormuario() {
        return numFormuario;
    }

    public void setNumFormuario(int numFormuario) {
        this.numFormuario = numFormuario;
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
        if (!(object instanceof FormProv)) {
            return false;
        }
        FormProv other = (FormProv) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.FormProv[ id=" + id + " ]";
    }
    
}
