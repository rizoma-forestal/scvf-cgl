
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos correspondientes a los items
 * que conforman las cuentas de Autorización y de Extracción, 
 * las que constituirán el tipo de Item (Paramétrica)
 * El conjunto de items autorizados para una misma Autorización, constituye para ella una Cuenta de Autorizados. 
 * El conjunto de items extraídos para una misma Guía, constituye para ella una Cuenta de Extraídos. 
 * Cada ítem de la cuenta de Autorizados constituye un item trazable (producto)
 * @author rincostante
 */
@Entity
@Audited
@XmlRootElement
public class ItemProductivo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nombre Científico cacheado del Producto
     * Se incluye en el código del producto tazable
     */
    @Column (nullable=false, length=100)
    @NotNull(message = "El campo nombreCientifico no puede ser nulo")
    @Size(message = "El campo nombreCientifico no puede tener más de 100 caracteres", min = 1, max = 100)      
    private String nombreCientifico;
    
    /**
     * Nombre vulgar cacheado del Producto
     * Se incluye en el código del producto tazable
     */
    @Column (nullable=false, length=50)
    @NotNull(message = "El campo nombreVulgar no puede ser nulo")
    @Size(message = "El campo nombreVulgar no puede tener más de 50 caracteres", min = 1, max = 50)  
    private String nombreVulgar;
    
    /**
     * Clase cacheada del Producto
     * Se incluye en el código del producto tazable
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo clase no puede ser nulo")
    @Size(message = "El campo clase no puede tener más de 30 caracteres", min = 1, max = 30)  
    private String clase;
    
    /**
     * Unidad de medida cacheada del Producto
     * Se incluye en el código del producto tazable
     */    
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo unidad no puede ser nulo")
    @Size(message = "El campo unidad no puede tener más de 30 caracteres", min = 1, max = 30) 
    private String unidad;
    
    /**
     * Id de la Especie de la que se constituye el Producto, 
     * registrado en el Registro de Taxonomía
     */
    @Column
    private Long idEspecieTax;
    
    /**
     * Referencia al identificador del Producto en la base local,
     * cacheado al momento de registrar el item
     */
    @Column 
    private Long idProd;
    
    /**
     * Para los items de tipo Autorizado, el campo guarda la Autorización correspondiente
     */
    @ManyToOne
    @JoinColumn(name="autorizacion_id")
    private Autorizacion autorizacion;
    
    /**
     * Para los items de tipo Extraído, el campo guarda la Guía correspondiente
     */
    @ManyToOne
    @JoinColumn(name="guia_id")
    private Guia guia;    
    
    /**
     * Paramétrica cuyo tipo es "Tipos de ïtems" que indica el tipo de ítem actual
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_tipoActual_id", nullable=false)
    @NotNull(message = "Debe existir un tipoActual")
    private Parametrica tipoActual;
    
    /**
     * Referencia del ítem del cual se originó el actual
     * Para los items de tipo "Autorización" no habrá itemOrigen
     */
    @Column 
    private Long itemOrigen;
    
    /**
     * Paramétrica cuyo tipo es "Tipos de items" que indica el tipo de ítem que originó al acutal
     * Para los items de tipo "Autorización" no habrá tipoOrigen
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_tipoOrigen_id")  
    private Parametrica tipoOrigen;
    
    /**
     * Equivalencia en Kg por unidad de medida del Producto.
     * Cacheado del Producto
     */
    @Column
    private float kilosXUnidad;
    
    /**
     * Cantidad autorizada de Producto a extraer
     * Cupo
     */
    @Column
    private float total;
    
    /**
     * Equivalencia del total autorizado en Kg.
     */
    @Column
    private float totalKg;
    
    /**
     * Campo temporal que guarda el total asignado al item origen.
     * Empleado durante la operatoria, no se persiste
     */
    @Transient
    private float totalOrigen;     
    
    /**
     * Saldo dispobible del Producto para ser descontado
     * Cupo
     */
    @Column
    private float saldo;  
    
    /**
     * Equivalencia en Kg. del saldo disponible
     */
    @Column
    private float saldoKg;  
    
    /**
     * El código de producto es una cadena de elementos separados por '|' en este orden
     * nombreCientifico: nombre científico de la Especie constituido por 'Género/Especie'
     * nombreVulgar: nombre vulgar de la Especie definido de manera local
     * clase: clase en la que se comercializa el Producto definido de manera local
     * unidad: unidad de medida en la que se comercializa el Producto/Clase definido de manera local
     * resolución: numero de la resolución (campo numero de la entidad Autorización)
     * provincia: nombre de la Provincia dentro de la cual se extraerá el Producto
     * EJ.:"1|Prosopis caldenia|Caldén|Rollo|Unidad|123-DGB-2017|Santiago del Estero"
     * Todos los valores son cacheados al momento de registrar un item
     */
    @Column (length=200)
    @Size(message = "El campo unidad no puede tener más de 200 caracteres",  max = 200)     
    private String codigoProducto;
    
    /**
     * Campo temporal que guarda el saldo disponible del item origen.
     * Empleado durante la operatoria, no se persiste
     */
    @Transient
    private float saldoOrigen;      
    
    /**
     * Campo temporal que guarda el saldo del item mientras se va realizando la operatoria
     * No se persiste
     */
    @Transient
    private float saldoTemp; 

    /**
     * Observaciones que pudieran corresponder
     */
    @Column (length=500)
    @Size(message = "El campo obs no puede tener más de 500 caracteres", max = 500)     
    private String obs;
    
    /**
     * Fecha de registro de la Autorización
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Usuario que gestiona la Autorización
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un usuario")       
    private Usuario usuario;

    @Column 
    private boolean habilitado;
    
    /**
     * Campo que mostrará la fecha de las revisiones
     * No se persiste
     */    
    @Transient
    private Date fechaRevision;
    
    /**
     * Flag temporal que indica que el item ya ha sido descontado.
     * Para usar durante la operatoria, no se persiste
     */
    @Transient
    private boolean descontado;    
    
    /**********************
     * Métodos de acceso **
     **********************/  
    public String getCodigoProducto() {
        return codigoProducto;
    }
   
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Long getIdEspecieTax() {
        return idEspecieTax;
    }

    public void setIdEspecieTax(Long idEspecieTax) {
        this.idEspecieTax = idEspecieTax;
    }

    public float getTotalKg() {
        return totalKg;
    }

    public void setTotalKg(float totalKg) {
        this.totalKg = totalKg;
    }

    public float getSaldoKg() {
        return saldoKg;
    }
  
    public float getKilosXUnidad() {
        return kilosXUnidad;
    }
 
    public void setKilosXUnidad(float kilosXUnidad) {
        this.kilosXUnidad = kilosXUnidad;
    }

    public void setSaldoKg(float saldoKg) {
        this.saldoKg = saldoKg;
    }

    @XmlTransient
    public boolean isDescontado() {
        return descontado;
    }
   
    public void setDescontado(boolean descontado) {
        this.descontado = descontado;
    }

    @XmlTransient
    public float getTotalOrigen() {
        return totalOrigen;
    }

    public void setTotalOrigen(float totalOrigen) {
        this.totalOrigen = totalOrigen;
    }

    @XmlTransient
    public float getSaldoOrigen() {
        return saldoOrigen;
    }
 
    public void setSaldoOrigen(float saldoOrigen) {
        this.saldoOrigen = saldoOrigen;
    }

    @XmlTransient
    public float getSaldoTemp() {
        return saldoTemp;
    }
      
    public void setSaldoTemp(float saldoTemp) {
        this.saldoTemp = saldoTemp;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public String getNombreVulgar() {
        return nombreVulgar;
    }

    public void setNombreVulgar(String nombreVulgar) {
        this.nombreVulgar = nombreVulgar;
    }
    
    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    @XmlTransient
    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    @XmlTransient
    public Autorizacion getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(Autorizacion autorizacion) {
        this.autorizacion = autorizacion;
    }

    @XmlTransient
    public Long getIdProd() {
        return idProd;
    }

    public void setIdProd(Long idProd) {
        this.idProd = idProd;
    }

    /**

     * @return 
     */
//    public String getCodigoProducto() {
//        return String.valueOf(this.id) + "|"
//                + this.nombreCientifico + "|"
//                + this.nombreVulgar + "|"
//                + this.clase + "|"
//                + this.unidad + "|"
//                + this.autorizacion.getNumero() + "|"
//                + this.autorizacion.getProvincia() + "|";
//    }

    @XmlTransient
    public Parametrica getTipoActual() {
        return tipoActual;
    }

    public void setTipoActual(Parametrica tipoActual) {
        this.tipoActual = tipoActual;
    }

    @XmlTransient
    public Long getItemOrigen() {
        return itemOrigen;
    }

    public void setItemOrigen(Long itemOrigen) {
        this.itemOrigen = itemOrigen;
    }

    @XmlTransient
    public Parametrica getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(Parametrica tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @XmlTransient
    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @XmlTransient
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @XmlTransient
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    @XmlTransient
    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
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
        if (!(object instanceof ItemProductivo)) {
            return false;
        }
        ItemProductivo other = (ItemProductivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo[ id=" + id + " ]";
    }
    
}
