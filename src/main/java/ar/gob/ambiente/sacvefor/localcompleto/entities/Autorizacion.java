
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;
import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Entidad que encapsula todo lo correspondiente a una Autorización para la intervención forestal
 * Entidad auditada
 * @author rincostante
 */
@Entity
@Audited
public class Autorizacion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * Variable privada: Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Variable privada: Paramétrica cuyo tipo es "Tipos de Autorización"
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_tipo_id", nullable=false)
    @NotNull(message = "Debe existir un Tipo")
    private Parametrica tipo;
    
    /**
     * Variable privada: Número identificatorio del Intrumento administrativo que autorice la extracción
     * El formato será validado en la lógica de la aplicación
     * en función de lo configurado en el Config.properties
     */
    @Column (nullable=false, length=30, unique=true)
    @NotNull(message = "El campo numero no puede ser nulo")
    @Size(message = "El campo numero no puede tener más de 30 caracteres", min = 1, max = 30)   
    private String numero;
    
    /**
     * Variable privada: Fecha de la Resolución o instrumento autorizante
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInstrumento;
    
    /**
     * Variable privada: Paramétrica cuyo tipo es "Tipos de Intervención"
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_intervencion_id", nullable=false)
    @NotNull(message = "Debe existir una Intervencion")
    private Parametrica intervencion;
    
    /**
     * Variable privada: Paramétrica cuyo tipo es "Uso de Suelo"
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="param_usosuelo_id", nullable=false)
    @NotNull(message = "Debe existir una usoSuelo")
    private Parametrica usoSuelo;

    /**
     * Variable privada: Listado de Zonas asinganadas al predio
     * Verde
     * Roja
     * Amarilla
     * Otra
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToMany
    @JoinTable(
            name = "zonasXAutorizaciones",
            joinColumns = @JoinColumn(name = "autorizacion_fk"),
            inverseJoinColumns = @JoinColumn(name = "zona_fk")
    )
    private List<ZonaIntervencion> zonas;  
    
    /**
     * Variable privada: Listado de SubZonas vinculadas a la Autorización en el caso de que las tuviera
     * Solo tendrán Sub Zonas las Zonas Verde y Amarilla
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToMany
    @JoinTable(
            name = "subzonasXAutorizaciones",
            joinColumns = @JoinColumn(name = "autorizacion_fk"),
            inverseJoinColumns = @JoinColumn(name = "subzona_fk")
    )    
    private List<SubZona> subZonas;

    /**
     * Variable privada: Listado de Personas vinculadas con la Autorización, estas podrán ser:
     * Técnicos,
     * Proponentes,
     * Apoderados
     * La obtención de cada uno de los grupos se realizará por el método correspondiente:
     * getTecnicos()
     * getProponentes()
     * getApoderados()
     */
    @ManyToMany
    @JoinTable(
            name = "personasXAutorizaciones",
            joinColumns = @JoinColumn(name = "autorizacion_fk"),
            inverseJoinColumns = @JoinColumn(name = "persona_fk")
    )
    private List<Persona> personas;     
    
    /**
     * Variable privada: Listado de Inuebles afectados a la Autrización
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToMany
    @JoinTable(
            name = "inmueblesXAutorizaciones",
            joinColumns = @JoinColumn(name = "autorizacion_fk"),
            inverseJoinColumns = @JoinColumn(name = "inmueble_fk")
    )
    private List<Inmueble> inmuebles;   
    
    /**
     * Variable privada: Listado de los items autorizados correspondientes a la Autorización
     */
    @OneToMany (mappedBy="autorizacion")
    private List<ItemProductivo> items;  
    
    /**
     * Variable privada: Estado de la autorización
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="estado_id", nullable=false)
    @NotNull(message = "Debe existir un estado")    
    private EstadoAutorizacion estado;
    
    /**
     * Variable privada: Número identificatorio del Expediente mediante el cual tramitó la Autorización
     * El formato será validado en la lógica de la aplicación
     * en función de lo configurado en el Config.properties
     */
    @Column (nullable=false, length=30)
    @NotNull(message = "El campo numExp no puede ser nulo")
    @Size(message = "El campo numExp no puede tener más de 30 caracteres", min = 1, max = 30)   
    private String numExp;
    
    /**
     * Variable privada: Fecha de ingreso del Expediente a la Autiridad local
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaIngExpte;
    
    /**
     * Variable privada: Superficie solicitada para autorizar la extracción
     */
    private float supSolicitada;
    
    /**
     * Variable privada: Superficie autorizada por la Autoridad local para realizar la extracción
     */
    private float supAutorizada;
    
    /**
     * Variable privada: Superficie total obtenida de la sumatoria de las superficies
     * de los inmuebles vinculados a la Autorización
     */
    private float supTotal;
    
    /**
     * Variable privada: Fecha de vencimiento de la autorización
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaVencAutoriz;
    
    /**
     * Variable privada: Observaciones que pudieran corresponder
     */
    @Column (length=500)
    @Size(message = "El campo obs no puede tener más de 500 caracteres", max = 500)     
    private String obs;
    
    /**
     * Variable privada: Provincia que la emitió. Surge de la configurada en cada aplciación
     */
    private String provincia;
    
    /**
     * Variable privada: Fecha de registro de la Autorización
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaAlta;
    
    /**
     * Variable privada: Usuario que gestiona la Autorización
     */
    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name="usuario_id", nullable=false)
    @NotNull(message = "Debe existir un usuario")       
    private Usuario usuario;
    
    /**
     * Variable privada no persistida: Campo que mostrará la fecha de las revisiones
     */    
    @Transient
    private Date fechaRevision;
    
    /**
     * Variable privada no persistida: Listado de Técnicos vinculados a la Autorización
     */
    @Transient
    private List<Persona> lstTecnicos;
    
    /**
     * Variable privada no persistida: Listado de Proponentes vinculados a la Autorización
     */
    @Transient
    private List<Persona> lstProponentes;
    
    /**
     * Variable privada no persistida: Listado de Apoderados vinculados a la Autorización
     */
    @Transient
    private List<Persona> lstApoderados;   
    
    /**
     * Variable privada no persistida: flag temporal que indica si la autorización fue 
     * seleccionada para descontar productos para una guía, durante su registro
     */
    @Transient
    private boolean asignadaDesc;    
    
    /**
     * Constructor que inicializa los listados
     */
    public Autorizacion(){
        personas = new ArrayList<>();
        inmuebles = new ArrayList<>();
        items = new ArrayList<>();
        subZonas = new ArrayList<>();
        zonas = new ArrayList<>();
    }
    
    /**********************
     * Métodos de acceso **
     **********************/
    public boolean isAsignadaDesc() {
        return asignadaDesc;
    }

    public void setAsignadaDesc(boolean asignadaDesc) {
        this.asignadaDesc = asignadaDesc;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public List<ZonaIntervencion> getZonas() {
        return zonas;
    }

    public void setZonas(List<ZonaIntervencion> zonas) {
        this.zonas = zonas;
    }

    public Date getFechaInstrumento() {
        return fechaInstrumento;
    }

    public void setFechaInstrumento(Date fechaInstrumento) {
        this.fechaInstrumento = fechaInstrumento;
    }

    public List<SubZona> getSubZonas() {
        return subZonas;
    }

    public void setSubZonas(List<SubZona> subZonas) {
        this.subZonas = subZonas;
    }

    public List<ItemProductivo> getItems() {
        return items;
    }

    public void setItems(List<ItemProductivo> items) {    
        this.items = items;
    }

    /**
     * Método que devuelve las Personas vinculadas a la Autorización con el rol de Técnicos
     * @return List<Persona> listado de técnicos
     */
    public List<Persona> getLstTecnicos() {
        lstTecnicos = new ArrayList<>();
        for(Persona per : this.personas){
            if(per.getRolPersona().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Tecnico"))){
                lstTecnicos.add(per);
            }
        }
        return lstTecnicos;
    }

    public void setLstTecnicos(List<Persona> lstTecnicos) {
        this.lstTecnicos = lstTecnicos;
    }

    /**
     * Método que devuelve las Personas vinculadas a la Autorización con el rol de Proponentes
     * @return List<Persona> listado de proponentes
     */    
    public List<Persona> getLstProponentes() {
        lstProponentes = new ArrayList<>();
        for(Persona per : this.personas){
            if(per.getRolPersona().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Proponente"))){
                lstProponentes.add(per);
            }
        }
        return lstProponentes;
    }

    public void setLstProponentes(List<Persona> lstProponentes) {
        this.lstProponentes = lstProponentes;
    }

    /**
     * Método que devuelve las Personas vinculadas a la Autorización con el rol de Apoderados
     * @return List<Persona> listado de Apoderados
     */    
    public List<Persona> getLstApoderados() {
        lstApoderados = new ArrayList<>();
        for(Persona per : this.personas){
            if(per.getRolPersona().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Apoderado"))){
                lstApoderados.add(per);
            }
        }
        return lstApoderados;

    }

    public void setLstApoderados(List<Persona> lstApoderados) {
        this.lstApoderados = lstApoderados;
    }

    public Parametrica getTipo() {
        return tipo;
    }

    public void setTipo(Parametrica tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Parametrica getIntervencion() {
        return intervencion;
    }

    public void setIntervencion(Parametrica intervencion) {
        this.intervencion = intervencion;
    }

    public Parametrica getUsoSuelo() {
        return usoSuelo;
    }

    public void setUsoSuelo(Parametrica usoSuelo) {
        this.usoSuelo = usoSuelo;
    }

    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }

    public List<Inmueble> getInmuebles() {
        return inmuebles;
    }

    public void setInmuebles(List<Inmueble> inmuebles) {
        this.inmuebles = inmuebles;
    }

    public EstadoAutorizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAutorizacion estado) {
        this.estado = estado;
    }

    public String getNumExp() {
        return numExp;
    }

    public void setNumExp(String numExp) {
        this.numExp = numExp;
    }

    public Date getFechaIngExpte() {
        return fechaIngExpte;
    }

    public void setFechaIngExpte(Date fechaIngExpte) {
        this.fechaIngExpte = fechaIngExpte;
    }

    public float getSupSolicitada() {
        return supSolicitada;
    }

    public void setSupSolicitada(float supSolicitada) {
        this.supSolicitada = supSolicitada;
    }

    public float getSupAutorizada() {
        return supAutorizada;
    }

    public void setSupAutorizada(float supAutorizada) {
        this.supAutorizada = supAutorizada;
    }

    public float getSupTotal() {
        return supTotal;
    }

    public void setSupTotal(float supTotal) {
        this.supTotal = supTotal;
    }

    public Date getFechaVencAutoriz() {
        return fechaVencAutoriz;
    }

    public void setFechaVencAutoriz(Date fechaVencAutoriz) {
        this.fechaVencAutoriz = fechaVencAutoriz;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
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
        if (!(object instanceof Autorizacion)) {
            return false;
        }
        Autorizacion other = (Autorizacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion[ id=" + id + " ]";
    }
    
}
