
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entidad que encapsula los datos correspondientes a los distintos estados que puede tener una Guía. Ej.: 
 * Carga inicial
 * Cerrada
 * En tránsito
 * Intervenida
 * Extraviada
 * etc.
 * @author rincostante
 */
@Entity
@XmlRootElement
public class EstadoGuia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Nombre del Estado
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo nombre no puede ser nulo")
    @Size(message = "El campo nombre no puede tener más de 20 caracteres", min = 1, max = 20)  
    private String nombre;
    
    /**
     * Variable privada: Especifica si el estado permite la edición de los datos complementarios de la Guía.
     * Productos
     * Transporte
     * Destino
     */
    private boolean habilitaEdicionComp;
    
    /**
     * Variable privada: Especifica si el estado permite la edición de la Fuente de productos y Titular de la Guía
     */
    private boolean habilitaEdicionFuente;
    
    /**
     * Variable privada: Especifica si el estado permite el tránsito de la Guía en el caso en que se trate de una Guía de tránsito
     */
    private boolean habilitaTransito;
    
    /**
     * Variable privada: Especifica si el estado permite que la Guía sea fuente de Productos para otra Guía
     */
    private boolean habilitaFuenteProductos;
    
    /**
     * Variable privada: Especifica si el estado permite que la Guía inicie el proceso de emisión
     */
    private boolean habilitaEmision;  
    
    /**
     * Variable privada: Especifica si el estado permite liquidar tasas
     */
    private boolean habilitaLiquidacion;    
    
    /**
     * Variable privada: Especifica si el estado implica que la Guía completó su ciclo de vida
     */
    private boolean completaCiclo;
    
    /**
     * Variable privada: muestra la condición de habilitado del estado
     */
    private boolean habilitado;

    /**
     * Método que retorna la condición de habilitación de liquidación de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */
    @XmlTransient
    public boolean isHabilitaLiquidacion() {
        return habilitaLiquidacion;
    }
    
    public void setHabilitaLiquidacion(boolean habilitaLiquidacion) {
        this.habilitaLiquidacion = habilitaLiquidacion;
    }

    /**
     * Método que retorna la condición de habilitación de la emisión de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */   
    @XmlTransient
    public boolean isHabilitaEmision() {
        return habilitaEmision;
    }

    public void setHabilitaEmision(boolean habilitaEmision) {
        this.habilitaEmision = habilitaEmision;
    }

    /**
     * Método que retorna la condición de habilitación de la edición de la fuente de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
    @XmlTransient
    public boolean isHabilitaEdicionFuente() {
        return habilitaEdicionFuente;
    }

    public void setHabilitaEdicionFuente(boolean habilitaEdicionFuente) {
        this.habilitaEdicionFuente = habilitaEdicionFuente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método que retorna la condición de habilitación de la edición de los datos complementarios de la Guía, 
     * no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
    @XmlTransient
    public boolean isHabilitaEdicionComp() {
        return habilitaEdicionComp;
    }

    public void setHabilitaEdicionComp(boolean habilitaEdicionComp) {
        this.habilitaEdicionComp = habilitaEdicionComp;
    }

    /**
     * Método que retorna la condición de habilitación de tránsito de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
    @XmlTransient
    public boolean isHabilitaTransito() {
        return habilitaTransito;
    }

    public void setHabilitaTransito(boolean habilitaTransito) {
        this.habilitaTransito = habilitaTransito;
    }

    /**
     * Método que retorna la condición de habilitación de la emisión de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
    @XmlTransient
    public boolean isHabilitaFuenteProductos() {
        return habilitaFuenteProductos;
    }

    public void setHabilitaFuenteProductos(boolean habilitaFuenteProductos) {
        this.habilitaFuenteProductos = habilitaFuenteProductos;
    }

    /**
     * Método que retorna la condición de completar el ciclo de vida de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
    @XmlTransient
    public boolean isCompletaCiclo() {
        return completaCiclo;
    }

    public void setCompletaCiclo(boolean completaCiclo) {
        this.completaCiclo = completaCiclo;
    }

    /**
     * Método que retorna la condición de habilitación de la Guía, no incluido en la entidad de para la API Rest
     * @return boolean verdadero o falso según el caso
     */       
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
        if (!(object instanceof EstadoGuia)) {
            return false;
        }
        EstadoGuia other = (EstadoGuia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia[ id=" + id + " ]";
    }
    
}
