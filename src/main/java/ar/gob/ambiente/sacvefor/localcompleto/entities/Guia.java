
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula los datos correspondientes a una Guía.
 * Según el tipo (Acopio, Acopio y transporte, Transporte) tendrá campos obligatorios 
 * y opcionales.
 * @author rincostante
 */
@Entity
@Audited
@XmlRootElement
public class Guia implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Cadena que constituye el código único de la Guía,
     * el formato se configurará en el archivo de propiedades Config.
     */
    @Column (nullable=false, length=20, unique=true)
    @NotNull(message = "El campo codigo no puede ser nulo")
    @Size(message = "El campo codigo no puede tener más de 20 caracteres", min = 1, max = 20)    
    private String codigo;
    
    /**
     * Variable privada: Tipo de Guía
     * Acopio,
     * Acopio y Transporte
     * Transporte
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="tipo_id", nullable=false)
    @NotNull(message = "Debe existir una tipo de guía")    
    private TipoGuia tipo;
    
    /**
     * Variable privada: Paramétrica que indica el tipo de origen de los Productos que toma la guía.
     * Según el tipo de guía, dicho origen será de una autorización o de otra guía.
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="tipofuente_id", nullable=false)
    @NotNull(message = "Debe existir un tipoFuente")    
    private Parametrica tipoFuente;
    
    /**
     * Variable privada: Cadena que constituye el número identificatorio 
     * de la autorización que oficia como fuente de productos para las guías 
     * gestionadas por el componente local, es decir guías primarias.
     * Se trata del número de resolución que autoriza la extracción de los productos
     */
    @Column (nullable=true, length=30)
    @Size(message = "El campo numFuente no puede tener más de 30 caracteres", max = 30)  
    private String numFuente;
    
    /**
     * Variable privada: Guarda las guías que sean fuentes de productos
     * de la presente guía
     */
    @ManyToMany
    @JoinTable(
            name = "guiasFuenteXGuias",
            joinColumns = @JoinColumn(name = "guia_fk"),
            inverseJoinColumns = @JoinColumn(name = "guiafuente_fk")
    )
    private List<Guia> guiasfuentes;     
    
    /**
     * Variable privada: Listado de los items que constituyen el detalle de la Guía
     */
    @OneToMany (mappedBy="guia")
    @Basic(fetch = FetchType.LAZY)
    private List<ItemProductivo> items;     
    
    /**
     * Variable privada: Para los casos de las Guías de Transporte, guarda el destino de la misma
     */
    @ManyToOne
    @JoinColumn(name="destino_id")
    private EntidadGuia destino;
    
    /**
     * Variable privada: Para los casos de las Guías de Transporte, guarda el transporte que llevará los productos a destino
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name="transporte_id")
    private Transporte transporte;
    
    /**
     * Variable privada: Origen de la guía, instrumento desde el cuál descuenta cupos de productos
     */
    @ManyToOne
    @JoinColumn(name="origen_id", nullable=false)
    @NotNull(message = "Debe existir una origen")
    private EntidadGuia origen;

    /**
     * Variable privada: Fecha de registro del Vehículo
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;  
    
    /**
     * Variable privada: Fecha de emisión del voltante de pago de liquidación de tasas.
     * Si correspondiera
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaEmisionVolante;   
    
    /**
     * Variable privada: Código del recibo comprobante de pago de liquidación de Tasas
     */
    private String codRecibo;
    
    /**
     * Variable privada: Fecha de emisión de la Guía.
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaEmisionGuia;    
    
    /**
     * Variable privada: Fecha de emisión de vencimiento de la Guía.
     * Si correspondiera
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaVencimiento; 
    
    /**
     * Variable privada: Fecha de cierre de la Guía, para los casos de Guías de transporte,
     * cuando el destinatario la acepta mediante la interface de intermediación
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaCierre;    

    /**
     * Variable privada: Usuario que gestiona la inserciones o ediciones
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un Usuario")   
    private Usuario usuario;    
    
    /**
     * Variable privada: Estado que puede tomar una Guía
     * Carga inicial
     * Cerrada
     * En tránsito
     * Intervenida
     * Extraviada
     * etc.
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="estado_id", nullable=false)
    @NotNull(message = "Debe existir una estado de guía")        
    private EstadoGuia estado;
    
    /**
     * Variable privada: Indica si la guía tiene un destino fuera de la provincia.
     * Si la guía es madre, indica si los remitos serán con destino externo.
     * Si la guía es un remito, indica si el destino es externo.
     * A solicitud de Misiones.
     */
    @Column (nullable=true)
    private boolean destinoExterno;
    
    /**
     * Variable privada: listado de personas con el rol de obrajero que podrán estar vinculados
     * a una guía. A pedido de Misiones.
     */
    @ManyToMany
    @JoinTable(
            name = "obrajerosXGuias",
            joinColumns = @JoinColumn(name = "guia_fk"),
            inverseJoinColumns = @JoinColumn(name = "obrajero_fk")
    )
    private List<Persona> obrajeros;    
    
    /**
     * Variable privada no persistida: Campo que mostrará la fecha de las revisiones
     */    
    @Transient
    private Date fechaRevision;    
    
    /**
     * Campo temporal que indicará el destino de la copia, al generar el pdf.
     * Dicho valor sera´obtenido del listado de Copias vinculadas al Tipo de Guía
     */
    @Transient
    private String destinoCopia;
 
    /**
     * Variable privada no persistida: Campo temporal que contiene la Provincia que emite la Guía
     * obtenida de la EntidadGuia origen
     */
    @Transient
    private String provincia;
    
    /**
     * Variable privada no persistida: Campos para leer archivo de configuracion
     */
    @Transient
    Properties properties;
    
    /**
     * Variable privada no persistida: flujo para obtener el archivo de propiedades
     */
    @Transient
    InputStream inputStream;  
    
    /**
     * Variable privada no persistida: flag temporal que indica si la guía fue 
     * seleccionada para descontar productos para otra guía, durante su registro
     */
    @Transient
    private boolean asignadaDesc;
    
    /**
     * Variable privada: Observaciones que pudieran corresponder
     */
    @Column (length=250)
    @Size(message = "El campo obs no puede tener más de 500 caracteres", max = 250)     
    private String obs;  
    
    /**
     * Variable privada: para las guías remito generadas a partir de formularios provisorios
     * guarda un listado con el o los formularios provisorios que de los que se proveyó, en 
     * el caso en que asé esté dispuesto en la configuración.
     * No se publicará en la API
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "guia_id", referencedColumnName = "id")
    private List<FormProv> formProvisorios;
    
    /**
     * Variable privada: para las guías madre que hayan emitido formularios provisorios,
     * indica la cantidad de formularios emitidos. Actualizable cuando se emiten más formularios (suma)
     * y cuando se toman formularios para emitir guías remitos (resta). Puede ser nulo
     * No se publicará en la API
     */
    @Column (nullable=true)
    private int formEmitidos;
    
    /**
     * Variable privada no persistida: cadena compuesta por el
     * número de guía y el de la fuente separados por "|"
     * para generar el código QR de la guía en papel.
     */
    @Transient
    private String codQr;
    
    /**
     * Variable privada no persistida: destinada a guardar la imagen del martillo al setearse el listado
     * de Guías para emitir el reporte.
     * No está disponible para la API
     */
    @Transient
    private FileInputStream martillo;    

    /**
     * Constructor, inicializa los ítems y las guías fuentes si corresponde
     */
    public Guia(){
        items = new ArrayList<>();
        guiasfuentes = new ArrayList<>();
        formProvisorios = new ArrayList<>();
    }

    @XmlTransient
    public boolean isDestinoExterno() {
        return destinoExterno;
    }

    public void setDestinoExterno(boolean destinoExterno) {
        this.destinoExterno = destinoExterno;
    }

    @XmlTransient
    public List<Persona> getObrajeros() {
        return obrajeros;
    }

    public void setObrajeros(List<Persona> obrajeros) {
        this.obrajeros = obrajeros;
    }

    @XmlTransient
    public FileInputStream getMartillo() {
        return martillo;
    }

    public void setMartillo(FileInputStream martillo) {
        this.martillo = martillo;
    }

    @XmlTransient
    public String getCodQr() {
        return codQr;
    }

    public void setCodQr(String codQr) {
        this.codQr = codQr;
    }    
    
    @XmlTransient
    public List<FormProv> getFormProvisorios() {
        return formProvisorios;
    }

    public void setFormProvisorios(List<FormProv> formProvisorios) {
        this.formProvisorios = formProvisorios;
    }
    
    /**
     * El método que devuelve las observaciones, por ahora no está disponible para la API
     * @return String observaciones que la guía pudiera tener
     */
    @XmlTransient
    public String getObs() {
        return obs;
    }

    @XmlTransient
    public int getFormEmitidos() {
        return formEmitidos;
    }

    public void setFormEmitidos(int formEmitidos) {
        this.formEmitidos = formEmitidos;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    /**
     * Método que devuelve el flag para uso interno, no está disponible para la API
     * @return flag correspondiente
     */
    @XmlTransient
    public boolean isAsignadaDesc() {
        return asignadaDesc;
    }

    public void setAsignadaDesc(boolean asignadaDesc) {
        this.asignadaDesc = asignadaDesc;
    }
    
    @XmlTransient
    public void setGuiasfuentes(List<Guia> guiasfuentes) {    
        this.guiasfuentes = guiasfuentes;
    }

    public List<Guia> getGuiasfuentes() {
        return guiasfuentes;
    }     
    
    /**
     * Método que agrupa los items de una guía en el caso que se repitan los productos
     * descontado de guías madre diferentes. En ese caso, suma los totales pero mantiene un solo ítem.
     * De lo contrario, retorna los items
     * @return List<ItemProductivo> listado de los items agrupados por producto.
     */
    @XmlTransient
    public List<ItemProductivo> getItemsAgrupados(){
        // instancio el listado de agrupados
        List<ItemProductivo> itemsAgrupados = new ArrayList<>();
        // recorro los ítems vinculados a la guía para poblar el listado de agrupados (sin repetir productos)
        for (ItemProductivo item : items){
            boolean existe = false;
            // recorro el listado de agrupados
            for (ItemProductivo itemAgr : itemsAgrupados){
                if(Objects.equals(itemAgr.getIdProd(), item.getIdProd())){
                    existe = true;
                }
            }
            if(!existe){
                itemsAgrupados.add(item);
            }
        }
        if(itemsAgrupados.size() != items.size()){
            // si hay diferencia vuelvo a recorrer el listado original para hacer la agrupación
            for (ItemProductivo item : items){
                // por cada item verifico si ya está registrado entre los agrupados
                for (ItemProductivo itemAgr : itemsAgrupados){
                    // si ya está registrado el producto entre los agrupados sumo los totales (total, totalKg)
                    if(Objects.equals(itemAgr.getIdProd(), item.getIdProd()) && !Objects.equals(itemAgr.getId(), item.getId())){
                        float total = itemAgr.getTotal();
                        float totalKg = itemAgr.getTotalKg();
                        // actualizo total
                        itemAgr.setTotal(total + item.getTotal());
                        // actualizo el totalKg
                        itemAgr.setTotalKg(totalKg + item.getTotalKg());
                    }
                }
            } 
            return itemsAgrupados;
        }else{
            // si no es necesario agrupar retorno los ítems
            return items;
        }

        
    }

    /**
     * Método que retorna el nombre de la provincia emisora de la Guía, no incluido en la entidad de para la API Rest
     * @return String nombre de la provincia
     */
    @XmlTransient
    public String getProvincia() {
        // instancio el stream
        inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("Config.properties");
        properties = new Properties();
        try{
            // leo las propiedades
            properties.load(inputStream);
            provincia = properties.getProperty("Provincia");
            
            // cierro el stream
            inputStream.close(); 
            
        }catch(IOException ex){
            Logger.getLogger(Guia.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    /**
     * Método que retorna una cadena indicando el destino de la copia, no incluido en la entidad de para la API Rest
     * @return String cadena con el destino de la copia
     */
    @XmlTransient
    public String getDestinoCopia() {
        return destinoCopia;
    }

    public void setDestinoCopia(String destinoCopia) {
        this.destinoCopia = destinoCopia;
    }

    /**
     * Método que retorna una cadena con el código de recibo de los pagos de tasas de la guía, si corresponde, 
     * no incluido en la entidad de para la API Rest
     * @return String código de recibo
     */
    @XmlTransient
    public String getCodRecibo() {
        return codRecibo;
    }

    public void setCodRecibo(String codRecibo) {
        this.codRecibo = codRecibo;
    }

    /**
     * Método que retorna una cadena indicando la fecha de emisión del volante de pago, no incluido en la entidad de para la API Rest
     * @return Date fecha de emisión del volante de pago
     */
    @XmlTransient
    public Date getFechaEmisionVolante() {
        return fechaEmisionVolante;
    }

    public void setFechaEmisionVolante(Date fechaEmisionVolante) {
        this.fechaEmisionVolante = fechaEmisionVolante;
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

    /**
     * Método que retorna la Paramétrica correspondiente al tipo de fuente de la Guía, no incluido en la entidad de para la API Rest
     * @return Parametrica tipo de fuente
     */
    @XmlTransient
    public Parametrica getTipoFuente() {
        return tipoFuente;
    }

    public void setTipoFuente(Parametrica tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

    public String getNumFuente() {
        return numFuente;
    }

    public void setNumFuente(String numFuente) {
        this.numFuente = numFuente;
    }

    public EstadoGuia getEstado() {
        return estado;
    }

    public void setEstado(EstadoGuia estado) {
        this.estado = estado;
    }

    /**
     * Método que retorna la fecha de revisión de la Guía para su auditoría, no incluido en la entidad de para la API Rest
     * @return Date fecha de revisión
     */
    @XmlTransient
    public Date getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(Date fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Método que retorna una Parametrica correspondiente al tipo de guía, no incluido en la entidad de para la API Rest
     * @return Parametrica tipo de guía
     */
    @XmlTransient
    public TipoGuia getTipo() {
        return tipo;
    }

    public void setTipo(TipoGuia tipo) {
        this.tipo = tipo;
    }

    /**
     * Método que retorna el listado con los ítems de productos de la guía, no incluido en la entidad de para la API Rest
     * @return List<ItemProductivo> listado de ítems
     */
    @XmlTransient
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

    /**
     * Método que retorna el usuario que registró o modificó la Guía no incluido en la entidad de para la API Rest
     * @return Usuario usuario correspondiente
     */
    @XmlTransient
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Guia[ id=" + id + " ]";
    }
    
}
