
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entidad que encapsula los diferentes tipos de Guías.
 * En principio serán:
 * Acopio,
 * Acopio y Transporte
 * Transporte
 * @author rincostante
 */
@Entity
@XmlRootElement
public class TipoGuia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre del Tipo
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 20 caracteres", min = 1, max = 20)   
    private String nombre;
    
    /**
     * Especifica si habilita el registro de transporte, para el caso de las Guías de transporte
     */
    private boolean habilitaTransp;
    
    /**
     * Especifica su descuenta los Items de una Autirización, para el caso de una Guía de acopio
     */
    private boolean descuentaAutoriz;
    
    /**
     * Especifica si permite descontar Items extraídos para otra guía
     */
    private boolean habilitaDesc;
    
    /**
     * Especifica si la guía debe abonar algún tipo de tasa como condición previa a su entrada en vigencia
     */
    private boolean abonaTasa;
    
    /**
     * Listado de las Copias a imprimir de la Guía
     */
    @OneToMany (mappedBy="tipoGuia")
    private List<CopiaGuia> copias;     
    
    private boolean habilitado;
    
    /**
     * Indica la vigencia del tipo de Guía en días.
     */
    private int vigencia;

    public int getVigencia() {
        return vigencia;
    }

    public void setVigencia(int vigencia) {
        this.vigencia = vigencia;
    }

    @XmlTransient
    public List<CopiaGuia> getCopias() {
        return copias;
    }

    public void setCopias(List<CopiaGuia> copias) {
        this.copias = copias;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAbonaTasa() {
        return abonaTasa;
    }

    public void setAbonaTasa(boolean abonaTasa) {
        this.abonaTasa = abonaTasa;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isHabilitaTransp() {
        return habilitaTransp;
    }

    public void setHabilitaTransp(boolean habilitaTransp) {
        this.habilitaTransp = habilitaTransp;
    }

    public boolean isDescuentaAutoriz() {
        return descuentaAutoriz;
    }

    public void setDescuentaAutoriz(boolean descuentaAutoriz) {
        this.descuentaAutoriz = descuentaAutoriz;
    }

    public boolean isHabilitaDesc() {
        return habilitaDesc;
    }

    public void setHabilitaDesc(boolean habilitaDesc) {
        this.habilitaDesc = habilitaDesc;
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
        if (!(object instanceof TipoGuia)) {
            return false;
        }
        TipoGuia other = (TipoGuia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia[ id=" + id + " ]";
    }
    
}
