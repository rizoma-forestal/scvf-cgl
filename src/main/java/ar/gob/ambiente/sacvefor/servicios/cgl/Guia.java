
package ar.gob.ambiente.sacvefor.servicios.cgl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entidad que encapsula los datos de las Guías del CGL
 * @author rincostante
 */
@XmlRootElement
public class Guia implements Serializable {
    private Long id;
    /**
     * Código de la Guía
     */
    private String codigo;
    /**
     * Número del Instrumento fuente de productos (Autorización y Guía)
     */
    private String numFuente;
    /**
     * Listado de los Items que componen la Guía
     */
    private List<ItemProductivo> items; 
    /**
     * Entidad destino de la Guía
     */
    private EntidadGuia destino;
    /**
     * Transporte de los Productos
     */
    private Transporte transporte;
    /**
     * Enitdad origen de la Guía
     */
    private EntidadGuia origen;
    private Date fechaAlta;
    private Date fechaEmisionGuia;
    private Date fechaVencimiento;
    private Date fechaCierre; 
    private EstadoGuia estado;
    
    /******************
     * Constructores **
     ******************/
    public Guia(){
        this.id = Long.valueOf(0);
        this.codigo = "default";
        this.numFuente = "default";
        this.items = new ArrayList<>();
        this.destino = new EntidadGuia();
        this.transporte = new Transporte();
        this.origen = new EntidadGuia();
        this.fechaAlta = new Date();
        this.fechaEmisionGuia = new Date();
        this.fechaVencimiento = new Date();
        this.fechaCierre = new Date();
        this.estado = new EstadoGuia();
    }
    
    public Guia(Long id, String codigo, String numFuente, List<ItemProductivo> items, EntidadGuia destino, Transporte transporte,
            EntidadGuia origen, Date fechaAlta, Date fechaEmisionGuia, Date fechaVencimiento, Date fechaCierre, EstadoGuia estado){
        this.id = id;
        this.codigo = codigo;
        this.numFuente = numFuente;
        this.items = items;
        this.destino = destino;
        this.transporte = transporte;
        this.origen = origen;
        this.fechaAlta = fechaAlta;
        this.fechaEmisionGuia = fechaEmisionGuia;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaCierre = fechaCierre;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNumFuente() {
        return numFuente;
    }

    public void setNumFuente(String numFuente) {
        this.numFuente = numFuente;
    }

    public List<ItemProductivo> getItems() {
        return items;
    }

    public void setItems(List<ItemProductivo> items) {
        this.items = items;
    }

    public EntidadGuia getDestino() {
        return destino;
    }

    public void setDestino(EntidadGuia destino) {
        this.destino = destino;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public EntidadGuia getOrigen() {
        return origen;
    }

    public void setOrigen(EntidadGuia origen) {
        this.origen = origen;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Date getFechaEmisionGuia() {
        return fechaEmisionGuia;
    }

    public void setFechaEmisionGuia(Date fechaEmisionGuia) {
        this.fechaEmisionGuia = fechaEmisionGuia;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public EstadoGuia getEstado() {
        return estado;
    }

    public void setEstado(EstadoGuia estado) {
        this.estado = estado;
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
        if (!(object instanceof Guia)) {
            return false;
        }
        Guia other = (Guia) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return new StringBuffer(" id: ").append(id).
                append(" codigo: ").append(codigo).
                append(" numFuente: ").append(numFuente).
                append(" destino: ").append(destino).
                append(" transporte: ").append(transporte).
                append(" origen: ").append(origen).
                append(" fechaAlta: ").append(fechaAlta).
                append(" fechaEmisionGuia: ").append(fechaEmisionGuia).
                append(" fechaVencimiento: ").append(fechaVencimiento).
                append(" fechaCierre: ").append(fechaCierre).
                append(" estado: ").append(estado).toString();
    }       
}
