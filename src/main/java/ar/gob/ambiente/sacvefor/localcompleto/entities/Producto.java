
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Gestiona los productos que se podrán seleccionar como autorizados para la extracción.
 * Será auditada.
 * @author rincostante
 */
@Entity
@Audited
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Especie local a la que pertenece el Producto
     * No será auditada
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="especie_id", nullable=false)
    @NotNull(message = "Debe existir una Especie Local")    
    private ProductoEspecieLocal especieLocal;
    
    /**
     * Clase en la que se comercializa el Producto
     * No será auditada
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="clase_id", nullable=false)
    @NotNull(message = "Debe existir una Especie Clase")     
    private ProductoClase clase;
    
    /**
     * Equivalencia en Kg. de la Unidad de medida de la Clase del Producto
     */
    private float equivalKg;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Usuario que registró el Producto, también registrado en las auditorías.
     * No será auditado
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")  
    private Usuario usuario;
    
    /**
     * Tasas que deberá pagar el producto por unidad de medida
     * Podrá ser una, varias o ninguna
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private List<ProductoTasa> tasas;     
    
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */
    @Transient
    private Date fechaRevision;      
    
    /**
     * Constructor
     */
    public Producto(){
        tasas = new ArrayList<>();
    }

    public float getEquivalKg() {
        return equivalKg;
    }

    public void setEquivalKg(float equivalKg) {
        this.equivalKg = equivalKg;
    }

    public List<ProductoTasa> getTasas() {
        return tasas;
    }

    public void setTasas(List<ProductoTasa> tasas) {
        this.tasas = tasas;
    }

    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public ProductoEspecieLocal getEspecieLocal() {
        return especieLocal;
    }

    public void setEspecieLocal(ProductoEspecieLocal especieLocal) {
        this.especieLocal = especieLocal;
    }

    public ProductoClase getClase() {
        return clase;
    }

    public void setClase(ProductoClase clase) {
        this.clase = clase;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Producto[ id=" + id + " ]";
    }
    
}
