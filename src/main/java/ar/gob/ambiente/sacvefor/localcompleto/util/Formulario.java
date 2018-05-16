
package ar.gob.ambiente.sacvefor.localcompleto.util;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Delegacion;
import java.io.Serializable;
import java.util.Date;

/**
 * Clase útil que encapusla los datos relativos a los formularios provisorios emitidos junto con las guías madre.
 * Cada formulario avala el movimiento de productos desde el predio a la Delegación forestal más cercana.
 * Cada uno será el respaldo para la creación de una guía de transporte correspondiente, conteniendo los mismos items y cantidades.
 * Al emitir los formularios se consigna en la guía madre la cantidad de formularios emitidos.
 * Al generar la guía de transporte se actualiza la cantidad de formularios emitidos para la guía restándole uno
 * La emisión de formularios será opcional para cada provincia.
 * @author rincostante
 */
public class Formulario implements Serializable {

    /**
     * Variable privada: Identificador único
     */
    private Long id;
    
    /**
     * Variable privada: fecha de emisión de los formularios
     */    
    private Date fechaEmision;
    
    /**
     * Variable privada: cantidad de horas de vigencia que sumados a la fecha de emisión dan el vencimiento
     */    
    private int horasVigencia;
    
    /**
     * Variable privada: delegación forestal en la que se generará la guía correspondiente al formulario
     */    
    private Delegacion destino;
    
    /**
     * Variable privada: cantidad de formularios a imprimir
     */
    private int cantidad;
    
    ///////////////////////////////////////////
    // atributos cacheados ////////////////////
    // completados en el proceso de emisión ///
    ///////////////////////////////////////////
    
    /**
     * Variable privada: nombre completo del titular de la guía madre
     */  
    private String nomTitular;
    
    /**
     * Variable privada: cuit del titular de la guía madre
     */      
    private Long cuitTitular;
    
    /**
     * Variable privada: nombre del inmueble vinculado a la autorización
     */      
    private String nomInmueble;
    
    /**
     * Variable privada: identificación catastral del inmueble
     */      
    private String idCatastral;
    
    /**
     * Variable privada: nombre del departamento en el cual está ubicado el inmueble
     */      
    private String depto;
    
    /**
     * Variable privada: código de la guía madre
     */      
    private String codGuia;
    
    /**
     * Variable privada: código de la autorización fuente de productos
     */      
    private String codAut;

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNomTitular() {
        return nomTitular;
    }

    public void setNomTitular(String nomTitular) {
        this.nomTitular = nomTitular;
    }

    public Long getCuitTitular() {
        return cuitTitular;
    }

    public void setCuitTitular(Long cuitTitular) {
        this.cuitTitular = cuitTitular;
    }

    public String getNomInmueble() {
        return nomInmueble;
    }

    public void setNomInmueble(String nomInmueble) {
        this.nomInmueble = nomInmueble;
    }

    public String getIdCatastral() {
        return idCatastral;
    }

    public void setIdCatastral(String idCatastral) {
        this.idCatastral = idCatastral;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public String getCodGuia() {
        return codGuia;
    }

    public void setCodGuia(String codGuia) {
        this.codGuia = codGuia;
    }

    public String getCodAut() {
        return codAut;
    }

    public void setCodAut(String codAut) {
        this.codAut = codAut;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public int getHorasVigencia() {
        return horasVigencia;
    }

    public void setHorasVigencia(int horasVigencia) {
        this.horasVigencia = horasVigencia;
    }

    public Delegacion getDestino() {
        return destino;
    }

    public void setDestino(Delegacion destino) {
        this.destino = destino;
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
        if (!(object instanceof Formulario)) {
            return false;
        }
        Formulario other = (Formulario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Formulario[ id=" + id + " ]";
    }
    
}
