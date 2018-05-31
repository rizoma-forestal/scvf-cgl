
package ar.gob.ambiente.sacvefor.localcompleto.util;

import java.io.Serializable;
import java.util.Date;

/**
 * Clase utilitaria que encapsula los datos principales de una guía que adeuda el pago de tasas:
 * Código de guía, fecha de emisión, fecha de vencimiento, total adeudado
 * @author rincostante
 */
public class GuiaSinPago implements Serializable{
    /**
     * Variable privada: Identificador único
     */
    private Long id;
    
    /**
     * Variable privada: Código único de la guía
     */    
    private String codGuia;

    /**
     * Variable privada: Fecha de emisión de la guía
     */    
    private Date fechaEmision;
    
    /**
     * Variable privada: Fecha de vencimiento de la guía
     */    
    private Date fechaVenc;
    
    /**
     * Variable privada: Total en pesos adeudado en concepto de tasas
     */    
    private float totalAdeudado;
    
    ///////////////////////
    // métodos de acceso //
    ///////////////////////    
    public String getCodGuia() {
        return codGuia;
    }

    public void setCodGuia(String codGuia) {
        this.codGuia = codGuia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Date getFechaVenc() {
        return fechaVenc;
    }

    public void setFechaVenc(Date fechaVenc) {
        this.fechaVenc = fechaVenc;
    }

    public float getTotalAdeudado() {
        return totalAdeudado;
    }

    public void setTotalAdeudado(float totalAdeudado) {
        this.totalAdeudado = totalAdeudado;
    }
    
    //////////////////////////
    // métodos sobrescritos //
    //////////////////////////

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.GuiaSinPago[ id=" + id + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(obj instanceof GuiaSinPago)) {
            return false;
        }
        GuiaSinPago other = (GuiaSinPago) obj;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
}
