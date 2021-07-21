
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entidad que gestiona las subclases que se derivan de la producción de otra
 * como Costaneros y Despuntes.
 * Para transformación de productos
 * @author rincostante
 */
@Entity
public class ProductoSubClase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="clase_derivada_id", nullable=true)
    private ProductoClase claseDerivada;
    
    @Column(name = "factor_relativo")
    private float factorRelativo;
    
    @ManyToOne
    @JoinColumn(name="clase_principal_id")
    private ProductoClase clasePrincipal;

    public ProductoClase getClasePrincipal() {
        return clasePrincipal;
    }

    public void setClasePrincipal(ProductoClase clasePrincipal) {
        this.clasePrincipal = clasePrincipal;
    }

    public ProductoClase getClaseDerivada() {
        return claseDerivada;
    }

    public void setClaseDerivada(ProductoClase claseDerivada) {
        this.claseDerivada = claseDerivada;
    }

    public float getFactorRelativo() {
        return factorRelativo;
    }

    public void setFactorRelativo(float factorRelativo) {
        this.factorRelativo = factorRelativo;
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
        if (!(object instanceof ProductoSubClase)) {
            return false;
        }
        ProductoSubClase other = (ProductoSubClase) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.SubClase[ id=" + id + " ]";
    }
    
}
