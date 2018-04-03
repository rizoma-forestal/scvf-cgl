
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.GuiaCtrlClient;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Transporte;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EntidadGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.PersonaClient;
import ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.TipoParamClient;
import ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.DetalleTasas;
import ar.gob.ambiente.sacvefor.localcompleto.util.DetalleTasas.TasaModel;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.LiqTotalTasas;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Bean de respaldo para la gestión de Guías.
 * Implica la creación, edición y lectura de los datos 
 * y la emisión de la Guía. Gestiona las vistas de /guia/gestion/
 * @author rincostante
 */
public class MbGuia {

    /**
     * Variable privada: setea la página inicial para mostrar en el frame
     */
    private String page = "general.xhtml";
    
    /**
     * Variable privada: objeto principal a gestionar
     */
    private Guia guia;
    
    /**
     * Variable privada: listado de las guías existentes
     */
    private List<Guia> listado;
    
    /**
     * Variable privada: listado para filtrar la tabla de guías existentes
     */
    private List<Guia> listadoFilter;
    
    /**
     * Variable privada: número de la guía para obtenerla para su gestión
     */
    private String guiaNumero;
    
    /**
     * Variable privada: flag que indica que la guía que se está gestionando es existente
     */
    private boolean edit;
    
    /**
     * Variable privada: flag que indica que la guía que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */
    private Usuario usLogueado;
    
    //////////////////////
    // Fuente y Titular //
    //////////////////////
    /**
     * Listado de Tipos de Guías para poblar el combo para su selección
     */
    private List<TipoGuia> lstTiposGuia;
    
    ///////////////////////////////////////
    // Listado de Fuentes para las Guías //
    ///////////////////////////////////////
    /**
     * Variable privada: listado de las Autorizaciones disponibles para tomar como fuente de productos para la guía
     */
    private List<Autorizacion> lstAutorizaciones;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de Autorizaciones disponibles
     */
    private List<Autorizacion> lstAutFilters;
    
    /**
     * Variable privada: listado de Guías de acopio disponibles para tomar como fuente de productos para la guía 
     */
    private List<Guia> lstGuiasMadre;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de Guías de acopio disponibles
     */
    private List<Guia> lstGuiasMadreFilters;
    
    /**
     * Variable privada: listado de las guías que tomaron productos de la que se está gestionando
     */
    private List<Guia> lstGuiasHijas;
    
    /**
     * Variable privada: autorización seleccionada para descontar productos
     */
    private Autorizacion autSelected;
    
    /**
     * Variable privada: guia seleccionada para descontar productos
     */
    private Guia guiaSelected;
    
    /**
     * Variable privada: cuit del productor a buscar para asignarlo como Entidad Guía titular
     */
    private Long cuitBuscar;
    
    /**
     * Variable privada: productor a asignar a la Entidad Guía como titular
     */
    private Persona productor;
    
    /**
     * Variable privada: Entidad Guía que oficiará de titular de la guía
     */
    private EntidadGuia entOrigen;
    
    /////////////
    // Destino //
    /////////////
    /**
     * Variable privada: Persona a seleccionada para conformar la Entidad Guía destino
     */
    private Persona destinatario;
    
    /**
     * Variable privada: Entidad Guía que hará las veces de destinatario de la Guía
     */
    private EntidadGuia entDestino;
    
    /**
     * Variable privada: Persona del paquete paqRue.jar que encapsulará los datos de la Persona en el
     * Registro Unico de entidades (RUE)
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona personaRue;
    
    /**
     * Variable privada: Cliente para la API Rest de Personas en el RUE
     */
    private PersonaClient personaClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Usuarios en el RUE
     */
    private ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient usClientRue;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del RUE
     */
    private Token tokenRue;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del RUE
     */
    private String strTokenRue;   
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    private static final Logger logger = Logger.getLogger(Persona.class.getName());    
    
    /**
     * Variable privada: flag que indica que se está editando el destino
     */
    private boolean editCuit;
    
    /**
     * Variable privada: flag que indica que se está buscando el destino
     */
    private boolean cuitProcesado;
    
    ////////////////
    // Transporte //
    ////////////////
    /**
     * Variable privada: Transporte a asignar a la Guía
     */
    private Transporte transporte;
    
    /**
     * Variable privada: Vehículo que compondrá el transporte para la Guía
     */    
    private Vehiculo vehiculo;
    
    /**
     * Variable privada: matrícula del vehículo a buscar
     */    
    private String matBuscar;
    
    /**
     * Variable privada: flag que indica si se está editando el transporte
     */    
    private boolean editTransporte;
    
    /**
     * Variable privada: flag que indica si se está buscando un nuevo vehículo
     */    
    private boolean buscarVehNuevo;
    
    ///////////////
    // Productos //
    /////////////// 
    /**
     * Variable privada: Flag que habilita la asignación de productos
     */
    private boolean habilitarProd;
    
    /**
     * Variable privada: Listado de items de los cuales se tomarán productos para la Guía
     */
    private List<ItemProductivo> lstItemsOrigen;
    
    /**
     * Variable privada: Listado de items autorizados para descontar desde la Guía ya registrada
     */
    private List<ItemProductivo> lstItemsAutorizados;
    
    /**
     * Variable privada: flag que indica si se está descontando cupo de un producto
     */
    private boolean descontandoProd;
    
    /**
     * Variable privada: Item productivo seleccionado para obtener productos
     */
    private ItemProductivo itemFuente;
    
    /**
     * Variable privada: Item productivo asignado a la Guía
     */
    private ItemProductivo itemAsignado;
    
    /**
     * Variable privada: flag que indica si se está editando un ítem ya asignado a la Guía
     */
    private boolean editandoItem;
    
    /**
     * Variable privada: Listado de Items para tomar productos, sean de Guías o Autorizaciones
     */
    private List<ItemProductivo> lstItemsAsignados;
    
    ///////////
    // Tasas //
    ///////////   
    /**
     * Variable privada: Listado con los nombres de las tasas a liquidar
     */
    private List<String> lstNombresTasas;
    
    /**
     * Variable privada: listado de las tasas para un item productivo
     */
    private List<DetalleTasas> lstDetallesTasas;
    
    /**
     * Variable privada: liquidación total de tasas para un ítem productivo
     */
    private LiqTotalTasas liquidacion;
    
    /**
     * Variable privada: listado de las liquidaciones totales correspondientes a los ítems de la guía
     */
    private List<LiqTotalTasas> liquidaciones;
    
    /**
     * Variable privada: objeto para gestionar el reporte para la emisión de la Guía
     */
    JasperPrint jasperPrint;
    
    /**
     * Variable estática privada y final: indica la ruta de los reportes 
     */
    private static final String RUTA_VOLANTE = "/resources/reportes/";
    
    /////////////////////////////////////////////////////////
    // Notificación al Destinatario de Guías de transporte //
    /////////////////////////////////////////////////////////
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba") 
    private Session mailSesion;
    
    /**
     * Variable privada: String mensaje a enviar por correo electrónico
     */ 
    private Message mensaje;   
    
    /**
     * Variable privada: cliente para el acceso a la API de usuarios de Trazabilidad
     */
    private UsuarioClient usuarioClientTraz;
    
    /**
     * Variable privada: cliente para el acceso a la API de Tipo de paramétrica de Trazabilidad
     */
    private TipoParamClient tipoParamClient;
    
    /**
     * Variable privada: cliente para el acceso a la API de Validación de usuarios para el acceso a Trazabilidad
     */
    private ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioApiClient usApiClientTraz;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Trazabilidad
     */ 
    private String strTokenTraz;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Trazabilidad
     */    
    private Token tokenTraz;
    
    /**
     * Variable privada: cliente para el acceso a la API de Guías de Control y verificación
     */
    private GuiaCtrlClient guiaCtrlClient;
    
    /**
     * Variable privada: cliente para el acceso a la API de Validación de usuarios para el acceso a Control y verificación
     */
    private ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.UsuarioApiClient usApiClientCtrl;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Control y verificación
     */ 
    private String strTokenCtrl;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Control y verificación
     */    
    private Token tokenCtrl;
    
    /**
     * Variable privada: mensaje devuelto al usuario en caso de éxito de la emisión de la Guía
     */
    private String msgExitoEmision;
    
    /**
     * Variable privada: mensaje devuelto al usuario en caso de error de la emisión de la Guía
     */
    private String msgErrorEmision;
    
    /**
     * Variable privada: Listado de inmuebles vinculados a la Autorización del predio
     * desde el cual provienen los productos vinculados a la Guía,
     * cualquier sea su tipo. A mostrar en el detalle de la Guía.
     */
    private List<Inmueble> lstInmueblesOrigen;

    /////////////////////
    // Accesos a datos mediante inyección de recursos //
    /////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */   
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoGuia
     */   
    @EJB
    private TipoGuiaFacade tipoGuiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Persona
     */   
    @EJB
    private PersonaFacade perFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */   
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica
     */   
    @EJB
    private ParametricaFacade paramFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */   
    @EJB
    private AutorizacionFacade autFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ItemProductivo
     */   
    @EJB
    private ItemProductivoFacade itemFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EntidadGuia
     */   
    @EJB
    private EntidadGuiaFacade entGuiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EstadoGuia
     */   
    @EJB
    private EstadoGuiaFacade estadoFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Producto
     */   
    @EJB
    private ProductoFacade prodFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Vehiculo
     */   
    @EJB
    private VehiculoFacade vehFacade;
    
    /**
     * Constructor
     */
    public MbGuia() {
    }

    ///////////////////////
    // métodos de acceso //
    ///////////////////////
    public List<Inmueble> getLstInmueblesOrigen() {
        return lstInmueblesOrigen;
    }

    public void setLstInmueblesOrigen(List<Inmueble> lstInmueblesOrigen) {
        this.lstInmueblesOrigen = lstInmueblesOrigen;
    }

    public List<Guia> getLstGuiasHijas() {
        return lstGuiasHijas;
    }

    public void setLstGuiasHijas(List<Guia> lstGuiasHijas) {
        this.lstGuiasHijas = lstGuiasHijas;
    }

    public String getMsgExitoEmision() {
        return msgExitoEmision;
    }

    public void setMsgExitoEmision(String msgExitoEmision) {
        this.msgExitoEmision = msgExitoEmision;
    }

    public String getMsgErrorEmision() {
        return msgErrorEmision;
    }

    public void setMsgErrorEmision(String msgErrorEmision) {
        this.msgErrorEmision = msgErrorEmision;
    }

    public boolean isBuscarVehNuevo() {
        return buscarVehNuevo;
    }

    public void setBuscarVehNuevo(boolean buscarVehNuevo) {
        this.buscarVehNuevo = buscarVehNuevo;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getMatBuscar() {
        return matBuscar;
    }

    public void setMatBuscar(String matBuscar) {
        this.matBuscar = matBuscar;
    }

    public boolean isEditTransporte() {
        return editTransporte;
    }

    public void setEditTransporte(boolean editTransporte) {
        this.editTransporte = editTransporte;
    }

    public boolean isCuitProcesado() {
        return cuitProcesado;
    }

    public void setCuitProcesado(boolean cuitProcesado) {
        this.cuitProcesado = cuitProcesado;
    }

    public boolean isEditCuit() {
        return editCuit;
    }

    public void setEditCuit(boolean editCuit) {
        this.editCuit = editCuit;
    }

    public EntidadGuia getEntDestino() {
        return entDestino;
    }

    public void setEntDestino(EntidadGuia entDestino) {
        this.entDestino = entDestino;
    }

    public Persona getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Persona destinatario) {
        this.destinatario = destinatario;
    }

    public List<Guia> getLstGuiasMadre() {
        return lstGuiasMadre;
    }

    public void setLstGuiasMadre(List<Guia> lstGuiasMadre) {
        this.lstGuiasMadre = lstGuiasMadre;
    }

    public List<Guia> getLstGuiasMadreFilters() {
        return lstGuiasMadreFilters;
    }

    public void setLstGuiasMadreFilters(List<Guia> lstGuiasMadreFilters) {
        this.lstGuiasMadreFilters = lstGuiasMadreFilters;
    }

    public Guia getGuiaSelected() {
        return guiaSelected;
    }

    public void setGuiaSelected(Guia guiaSelected) {
        this.guiaSelected = guiaSelected;
    }

    /**
     * Método para completar el listado con los ítems autorizados de la guía
     * @return 
     */
    public List<ItemProductivo> getLstItemsAutorizados() {
        if(guia.getTipoFuente().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            // tomo los productos de la Autorización
            lstItemsAutorizados = itemFacade.getByAutorizacion(autFacade.getExistente(guia.getNumFuente()));
        }else if(guia.getTipoFuente().getNombre().equals(ResourceBundle.getBundle("/Config").getString("GuiaMadre"))){
            // tomo los productos de la Guía madre
            lstItemsAutorizados = itemFacade.getByGuiaHabilitados(guiaFacade.getExistente(guia.getNumFuente()));
        }
        // actualizo el flag 'descontado' de cada uno
        for(ItemProductivo ipOrigen : lstItemsAutorizados){
            for(ItemProductivo ipAsignado : getLstItemsAsignados()){
                if(Objects.equals(ipOrigen.getId(), ipAsignado.getItemOrigen())){
                    ipOrigen.setDescontado(true);
                }
            }
        }
        return lstItemsAutorizados;
    }

    public void setLstItemsAutorizados(List<ItemProductivo> lstItemsAutorizados) {
        this.lstItemsAutorizados = lstItemsAutorizados;
    }

    public List<LiqTotalTasas> getLiquidaciones() {
        return liquidaciones;
    }

    public void setLiquidaciones(List<LiqTotalTasas> liquidaciones) {
        this.liquidaciones = liquidaciones;
    }

    public LiqTotalTasas getLiquidacion() {
        return liquidacion;
    }

    public void setLiquidacion(LiqTotalTasas liquidacion) {
        this.liquidacion = liquidacion;
    }

    public List<DetalleTasas> getLstDetallesTasas() {
        return lstDetallesTasas;
    }

    public void setLstDetallesTasas(List<DetalleTasas> lstDetallesTasas) {
        this.lstDetallesTasas = lstDetallesTasas;
    }

    public List<String> getLstNombresTasas() {
        return lstNombresTasas;
    }

    public void setLstNombresTasas(List<String> lstNombresTasas) {
        this.lstNombresTasas = lstNombresTasas;
    }

    public boolean isHabilitarProd() {
        return habilitarProd;
    }

    public void setHabilitarProd(boolean habilitarProd) {
        this.habilitarProd = habilitarProd;
    }

    public boolean isEditandoItem() {
        return editandoItem;
    }

    public void setEditandoItem(boolean editandoItem) {
        this.editandoItem = editandoItem;
    }

    public ItemProductivo getItemFuente() {
        return itemFuente;
    }

    public void setItemFuente(ItemProductivo itemFuente) {
        this.itemFuente = itemFuente;
    }

    public boolean isDescontandoProd() {
        return descontandoProd;
    }

    public void setDescontandoProd(boolean descontandoProd) {
        this.descontandoProd = descontandoProd;
    }

    public ItemProductivo getItemAsignado() {
        return itemAsignado;
    }

    public void setItemAsignado(ItemProductivo itemAsignado) {
        this.itemAsignado = itemAsignado;
    }

    public Persona getProductor() {
        return productor;
    }

    public void setProductor(Persona productor) {
        this.productor = productor;
    }

    public EntidadGuia getEntOrigen() {
        return entOrigen;
    }

    public void setEntOrigen(EntidadGuia entOrigen) {
        this.entOrigen = entOrigen;
    }

    public List<ItemProductivo> getLstItemsOrigen() {
        return lstItemsOrigen;
    }

    public void setLstItemsOrigen(List<ItemProductivo> lstItemsOrigen) {
        this.lstItemsOrigen = lstItemsOrigen;
    }

    public Autorizacion getAutSelected() {
        return autSelected;
    }

    public void setAutSelected(Autorizacion autSelected) {
        this.autSelected = autSelected;
    }

    public List<Autorizacion> getLstAutFilters() {
        return lstAutFilters;
    }

    public void setLstAutFilters(List<Autorizacion> lstAutFilters) {
        this.lstAutFilters = lstAutFilters;
    }

    public Long getCuitBuscar() {
        return cuitBuscar;
    }

    public void setCuitBuscar(Long cuitBuscar) {
        this.cuitBuscar = cuitBuscar;
    }

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    public List<Guia> getListado() {
        listado = guiaFacade.findAll();
        return listado;
    }

    public void setListado(List<Guia> listado) {
        this.listado = listado;
    }

    public List<Guia> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<Guia> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    public String getGuiaNumero() {
        return guiaNumero;
    }

    public void setGuiaNumero(String guiaNumero) {
        this.guiaNumero = guiaNumero;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public List<TipoGuia> getLstTiposGuia() {
        return lstTiposGuia;
    }

    public void setLstTiposGuia(List<TipoGuia> lstTiposGuia) {
        this.lstTiposGuia = lstTiposGuia;
    }

    public List<Autorizacion> getLstAutorizaciones() {
        return lstAutorizaciones;
    }

    public void setLstAutorizaciones(List<Autorizacion> lstAutorizaciones) {
        this.lstAutorizaciones = lstAutorizaciones;
    }

    public List<ItemProductivo> getLstItemsAsignados() {
        lstItemsAsignados = itemFacade.getByGuia(guia);
        return lstItemsAsignados;
    }

    public void setLstItemsAsignados(List<ItemProductivo> lstItemsAsignados) {
        this.lstItemsAsignados = lstItemsAsignados;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
    
    /******************************
     * Métodos de inicialización **
     ******************************/
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa el bean de sesión y el usuario
     */
    @PostConstruct
    public void init(){
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
    }    
    
    ////////////////////////
    // Métodos operativos //
    ////////////////////////  
    /**
     * Método que carga la vista que se mostrará en el iframe complementario.
     * Según la vista solicitada se instanciarán los objetos a gestionar.
     * Las vistas con tratamiento específico podrán ser:
     * tasas.xhtml, tasas.xhtml, transporte.xhtml o emision.xhtml
     * Por defecto será "general.xhtml"
     * @param strPage String vista a cargar recibida como parámetro
     */
    public void cargarFrame(String strPage){
        // para la vista de Tasas preparo la liquidación
        switch (strPage) {
            case "tasas.xhtml":
                // cargo el listado de las tasas configuradas para el tipo de Guía
                lstNombresTasas = new ArrayList<>();
                for(TipoGuiaTasa tgt : guia.getTipo().getTasas()){
                    lstNombresTasas.add(tgt.getTipo().getNombre());
                }

                // cargo los detalles por item
                lstDetallesTasas = new ArrayList<>();
                for(ItemProductivo item : guia.getItems()){
                    // obtengo el producto para sacar las tasas
                    Producto prod = prodFacade.find(item.getIdProd());
                    // instancio el DetalleTasa
                    DetalleTasas detTasa = new DetalleTasas();
                    // instancio el listado de TasasModel
                    List<TasaModel> lstTasaModel = new ArrayList<>();
                    detTasa.setNombreProd(prod.getEspecieLocal().getNombreVulgar());
                    detTasa.setClase(prod.getClase().getNombre());
                    detTasa.setCantidad(item.getTotal());
                    detTasa.setUnidad(item.getUnidad());
                    // obtengo las tasas del producto. En caso de no tener tasas asignadas irán todas en 0
                    if(prod.getTasas().isEmpty()){
                        for(String nomTasa : lstNombresTasas){
                            lstTasaModel.add(new TasaModel(nomTasa, 0, 0));
                        }
                    }else{
                        for(String nomTasa : lstNombresTasas){
                            // por cada tasa asigno su valor, en los casos que la tengan configurada
                            float subTotal;
                            for(ProductoTasa prodTasa : prod.getTasas()){
                                if(nomTasa.equals(prodTasa.getTipo().getNombre())){
                                    lstTasaModel.add(new TasaModel(nomTasa, prodTasa.getMonto(), prodTasa.getMonto() * detTasa.getCantidad()));
                                    subTotal = prodTasa.getMonto() * detTasa.getCantidad();
                                    detTasa.setTotal(detTasa.getTotal() + subTotal);
                                }
                            }
                        }
                        // si hay tasas no configuradas para el item, las seteo en 0
                        for(String nomTasa : lstNombresTasas){
                            boolean existe = false;
                            for(TasaModel tasa : lstTasaModel){
                                if(nomTasa.equals(tasa.getHeader())){
                                    existe = true;
                                }
                            }
                            if(!existe){
                                lstTasaModel.add(new TasaModel(nomTasa, 0, 0));
                            }
                        }
                    }
                    
                    // asigno el listado de tasas
                    detTasa.setTasas(lstTasaModel);
                    // agrego el DetalleTasa al listado
                    lstDetallesTasas.add(detTasa);
                }   
                // instancio la Liquidación y el array para el reporte
                liquidacion = new LiqTotalTasas();
                liquidaciones = new ArrayList<>();
                // seteo los datos para mostrar en el reporte
                //liquidacion.setProvincia(guia.getOrigen().getProvincia());
                liquidacion.setTipoGuia(guia.getTipo().getNombre());
                liquidacion.setCodGuia(guia.getCodigo());
                liquidacion.setDetalles(lstDetallesTasas);
                liquidaciones.add(liquidacion);
                break;
            case "destino.xhtml":
                cuitProcesado = false;
                if(guia.getDestino() != null){
                    // si estaba editando, bajo los flags
                    if(editCuit){
                        editCuit = false;
                    }
                    entDestino = guia.getDestino();
                }   break;
            case "transporte.xhtml":
                transporte = new Transporte();
                editTransporte = false;
                if(guia.getTransporte() != null){
                    vehiculo = new Vehiculo();
                }else{
                    vehiculo = null;
                }   if(buscarVehNuevo){
                    buscarVehNuevo = false;
                }   break;
            case "emision.xhtml":
                guia = guiaFacade.getExistente(guia.getCodigo());
                break;
            default:
                lstNombresTasas = null;
                lstDetallesTasas = null;
                liquidacion = null;
                liquidaciones = null;
                break;
        }
        page = strPage;
    }   
    
    /**
     * Método que busca una Guía según el número ingresado en el formulario de búsqueda.
     * guiaNumero será el parámetro.
     */
    public void buscar(){
        try{
            // busco la Guía
            guia = guiaFacade.getExistente(guiaNumero.toUpperCase());
            if(guia == null){
                JsfUtil.addErrorMessage("No se encontró ninguna Guía con el Número ingresado.");
            }else{
                // redirecciono a la vista
                setearInmueblesOrigen();
                page = "generalView.xhtml";
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error buscando la Guía. " + ex.getMessage());
        }
    } 
    
    /**
     * Método para buscar la/s fuente/s de productos que correspondan según el tipo de Guía.
     * También setea el Prductor (Persona) según el CUIT obtenido.
     */
    public void buscarFuentesProductos(){
        // obtengo el rol de Proponente
        TipoParam tipoParamRol = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        Parametrica rolProp = paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Proponente"), tipoParamRol);
        // obtengo el Producor
        productor = perFacade.findByCuitRol(cuitBuscar, rolProp);
        if(productor != null){
            // obtengo las fuentes según el tipo de Guía
            if(guia.getTipo().isDescuentaAutoriz()){
                // seteo el tipo de Fuente
                TipoParam tipoParamFuente = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoFuente"));
                guia.setTipoFuente(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Autorizacion"), tipoParamFuente));
                // busco Autorizaciones que tengan al productor como Proponente
                lstAutorizaciones = autFacade.getFuenteByProponente(productor);
                lstGuiasMadre = null;
            }else{
                // busco Guías que tengan al Productor como titular u origen
                TipoParam tipoParamFuente = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoFuente"));
                guia.setTipoFuente(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaMadre"), tipoParamFuente));
                lstGuiasMadre = guiaFacade.getFuenteByTitular(productor.getCuit());
                lstAutorizaciones = null;
            }
        }else{
            JsfUtil.addErrorMessage("No se encontró un Productor registrado con el CUIT ingresado.");
        }

    }
    
    /**
     * Método que instancia una Guía nueva, los listados de los objetos a seleccinar y campos.
     * Renderiza el fomulario de creación/edición.
     */
    public void prepareNew(){
        guia = new Guia();
        resetearCampos();
    } 
    
    /**
     * Método para inicializar la edición de los datos generales de la Guía 
     * y redireccionar al formlario de edición
     */
    public void prepareEdit(){
        edit = true;
        // cargo el listado de Tipos de Guía
        resetearCampos();
        // seteo el CUIT buscado
        cuitBuscar = guia.getOrigen().getCuit();
        // seteo la fuente de productos
        buscarFuentesProductos();
        // si la fuente es una Autorización, cargo el listado correspondiente, seteo la Autorización seleccionda y armo su detalle
        if(guia.getTipoFuente().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            autSelected = autFacade.getExistente(guia.getNumFuente());
            verDetalleFuente(ResourceBundle.getBundle("/Config").getString("Autorizacion"));
        }else{
            guiaSelected = guiaFacade.getExistente(guia.getNumFuente());
            verDetalleFuente(ResourceBundle.getBundle("/Config").getString("GuiaMadre"));
        }
        page = "general.xhtml";
    }    
    
    /**
     * Método para redireccionar a la vista detalle de la Guía
     */
    public void prepareView(){
        limpiarForm();
        setearInmueblesOrigen();
        page = "generalView.xhtml";
    }    
    
    /**
     * Método para limpiar el campo de CUIT a buscar
     */
    public void limpiarCuit(){
        view = false;
        autSelected = null;
        guiaSelected = null;
        cuitBuscar = null;
        productor = null;
        lstAutorizaciones = null;
        lstGuiasMadre = null;
        entOrigen = null;
        guia = new Guia();
    }

    /**
     * Método para limpiar el formulario de datos generales luego de la edición de la Guía
     */
    public void limpiarForm() {
        edit = false;
        view = false;
        resetearCampos();
        cuitBuscar = null;
        productor = null;
        entOrigen = null;
        lstAutorizaciones = null;
        lstGuiasMadre = null;
        autSelected = null;
        guiaSelected = null;
        lstItemsOrigen = null;
    }
    
    /**
     * Método para mostrar el detalle de la Autorización o Guía seleccionada como fuente de productos.
     * @param fuente : Tipo de Fuente cuyo detalle se solicita
     */
    public void verDetalleFuente(String fuente){
        if(fuente.equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            lstItemsOrigen = itemFacade.getByAutorizacion(autSelected);
        }else{
            lstItemsOrigen = itemFacade.getByGuia(guiaSelected);
        }
        view = true;
    }
    
    /**
     * Método para cancelar la Fuente seleccionada como origen de productos
     * Tanto sea una Autorización o una Guía
     * @param fuente : Tipo de Fuente cuya selección de desea cancelar
     */
    public void cancelarFuenteSelected(String fuente){
        if(fuente.equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            autSelected = null;
        }else{
            guiaSelected = null;
        }
        view = false;
    }
    
    /**
     * Método para resetear la Fuente de productos
     * Tanto sea una Autorización o una Guía
     * @param fuente String Tipo de Fuente que se desea resetear
     */
    public void deleteFuenteGuardada(String fuente){
        if(fuente.equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            autSelected = null;
        }else{
            guiaSelected = null;
        }
        view = false;
        guia.setNumFuente(null);
    }
    
    /**
     * Método para guardar la Fuente de productos para la Guía.
     * Sea Autorización o Guía
     * @param tipoFuente String tipo específico de fuente
     */
    public void guardarFuente(String tipoFuente){
        if(tipoFuente.equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            guia.setNumFuente(autSelected.getNumero());
            JsfUtil.addSuccessMessage("Se ha registrado la Fuente de Productos, puede guardar los Datos Generales de la Guía");
        }else{
            guia.setNumFuente(guiaSelected.getCodigo());
            JsfUtil.addSuccessMessage("Se ha registrado la Fuente de Productos, puede guardar los Datos Generales de la Guía");
        }
    }
    
    /**
     * Método para persistir una Guía con los datos de origen.
     * Sea inserción o edición
     */
    public void save(){
        Date fechaAlta = null;
        if(!edit){
            fechaAlta = new Date(System.currentTimeMillis());
            // seteo la fecha de alta
            guia.setFechaAlta(fechaAlta);
            // obtengo la EntidadGuia (Origen) y seteo los datos de alta
            entOrigen = obtenerEntidadOrigen(productor, guia.getTipoFuente());
        }
        try{
            guia.setUsuario(usLogueado);
            // edito o inserto, según sea el caso
            if(!edit){
                // Estado (carga inicial)
                EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaInicial"));
                if(estado != null){
                    // solo inserto si hay un estado configurado
                    guia.setEstado(estado);
                    // si no existe la EntidadGuia origen, la creo
                    if(entOrigen.getId() == null){
                        entOrigen.setUsuario(usLogueado);
                        entOrigen.setFechaAlta(fechaAlta);
                        entOrigen.setHabilitado(true);
                        entGuiaFacade.create(entOrigen);
                    }
                    // Seteo el código. En principio será un autonumérico
                    guia.setCodigo(setCodigoGuia());
                    // Seteo EntidadGuia origen
                    guia.setOrigen(entOrigen);
                    // Creo la Guía
                    guiaFacade.create(guia);
                    JsfUtil.addSuccessMessage("La Guía se creo correctamente, puede continuar con los pasos siguientes para su emisión.");
                }else{
                    JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Estado de Guía: 'GuiaInicial'.");
                }
            }else{
                // actualizo la EntidadGuia origen
                if(entOrigen != null){
                    guia.setOrigen(entOrigen);
                }
                guiaFacade.edit(guia);
                JsfUtil.addSuccessMessage("La Guía se actualizó correctamente, puede continuar con los pasos siguientes para su emisión.");
            }
            // limpio los objetos temporales del bean
            limpiarForm();
            // redirecciono a la vista
            setearInmueblesOrigen();
            page = "generalView.xhtml";
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error gestionando la Guía. " + ex.getMessage());
        }

    }
    
    /////////////
    // Destino //
    /////////////
    /**
     * Método para buscar la/s fuente/s de productos que correspondan según el tipo de Guía.
     * También setea el Prductor (Persona) según el CUIT obtenido.
     */
    public void buscarDestinatario(){
        Parametrica rolPersona = obtenerParametro(ResourceBundle.getBundle("/Config").getString("RolPersonas"), ResourceBundle.getBundle("/Config").getString("Destinatario"));
        destinatario = perFacade.findByCuitRol(cuitBuscar, rolPersona);
        if(destinatario != null){
            entDestino = obtenerEntidadDestino(destinatario);
            cuitProcesado = true;
        }else{
            JsfUtil.addErrorMessage("No hay ningún Destinatario registrado con el CUIT ingresado.");
        }
    }
    
    /**
     * Método para limpiar el cuit seleccionado para buscar la persona
     */
    public void limpiarCuitDest(){
        view = false;
        cuitBuscar = null;
        destinatario = null;
        entDestino = null;
    }
    
    /**
     * Método para restaurar el destino de la Guía
     */
    public void restaurarDestino(){
        entDestino = guia.getDestino();
       	editCuit = false;
        cuitProcesado = false;
    }
    
    /**
     * Método para persistir el Destino de la Guía, en caso que lo requiera,
     * dado que no todos los movimientos implican un destinatario distinto al titular.
     */
    public void saveDestino(){
        EstadoGuia estado = guia.getEstado();
        try{
            // continúo según el Destino asignado ya existiera previamente
            if(entDestino.getId() == null){
                Date fechaAlta = new Date(System.currentTimeMillis());
                // obtengo la EntidadGuia (Origen) y seteo los datos de alta
                entDestino.setFechaAlta(fechaAlta);
                entDestino.setHabilitado(true);
                // seteo los campos faltantes
                entDestino.setUsuario(usLogueado);
                String tmpDom = entDestino.getInmDomicilio().toUpperCase();
                entDestino.setInmDomicilio(tmpDom);
                TipoParam tipoParamEntGuia = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoEntidadGuia"));
                entDestino.setTipoEntidadGuia(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TegDestino"), tipoParamEntGuia));
                // creo el Destino
                entGuiaFacade.create(entDestino);
            }
            // chequeo los estados
            if(guia.getTipo().isAbonaTasa() && guia.getTransporte() != null && !guia.getItems().isEmpty()){
                // si abona tasas, tiene transporte habilito la liquidación y tiene productos
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
            }else if(guia.getTipo().isAbonaTasa() && guia.getTransporte() == null && guia.getTipo().isMovInterno() && !guia.getItems().isEmpty()){
                // si abona tasas, no tiene transporte, tiene productos, es movimiento interno habilito la liquidación y tiene productos
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
            }else if(!guia.getTipo().isAbonaTasa() && guia.getTransporte() != null && !guia.getItems().isEmpty()){
                // si no abona tasas, tiene transporte y productos, habilito el paso a emisión
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConTransporte"));
            }else if(!guia.getTipo().isAbonaTasa() && guia.getTransporte() == null && !guia.getItems().isEmpty() && guia.getTipo().isMovInterno()){
                // si no abona tasas, no tiene transporte, tiene productos y es movimiento interno
                estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConTransporte"));
            }
            if(estado != guia.getEstado()){
                guia.setEstado(estado);
            }
            
            guia.setDestino(entDestino);
            guia.setUsuario(usLogueado);
            guiaFacade.edit(guia);
            // si está la Guía ya tenía un Destino asignado, acutualizo el flag
            if(editCuit){
                editCuit = false;
            }
            view = false;
            cuitBuscar = null;
            destinatario = null;
            cuitProcesado = false;
            JsfUtil.addSuccessMessage("El Destino fue registrado con éxito.");
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error gestionando el Destino de la Guía. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para limpiar el formuario de Destino de la Guía
     */
    public void limpiarFormDestino(){
        view = false;
        cuitBuscar = null;
        destinatario = null;
        cuitProcesado = false;
        entDestino = null;
    }
    
    /**
     * Método para preparar el formulario para la selección de otro
     * Destinatario cuando la Guía ya tiene un destino configurado
     */
    public void prepareNuevoDestinatario(){
        entDestino = null;
        cuitProcesado = false;
        editCuit = true;
        cuitBuscar = null;
    }
    
    ////////////////
    // Transporte //
    ////////////////
    
    /**
     * Método para buscar un Vehículo a partir de su matrícula.
     * El Vehículo debe estar previamente registrado.
     */
    public void buscarVehiculo(){
        try{
            vehiculo = vehFacade.getExistente(matBuscar.toUpperCase());
            if(vehiculo == null){
                JsfUtil.addErrorMessage("No hay ningún Vehículo registrado con la Matrícula ingresada para su búsqueda.");
            }else{
                if(buscarVehNuevo){
                    buscarVehNuevo = false;
                }
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error buscando el Vehículo: " + ex.getMessage());
        }
    }
    
    /**
     * Método para resetear la búsqueda del Vehículo mediante su Matrícula
     */
    public void limpiarMatricula(){
        matBuscar = "";
        vehiculo = null;
    }
    
    /**
     * Método para limpiar los campos editables del Tranporte
     */
    public void limpiarTransporte(){
        transporte = new Transporte();
    }
    
    /**
     * Método para iniciar la búsqueda de un nuevo Vehículo
     */
    public void prepareBuscarNuevoTransp(){
        matBuscar = "";
        limpiarMatricula();
        limpiarTransporte();
        if(guia.getTransporte() != null){
            buscarVehNuevo = true;
        }
    }
    
    /**
     * Método para asignar un Transporte a la Guía que así lo requiera
     * Sea edición o inserción
     */
    public void saveTransporte(){
        EstadoGuia estado = guia.getEstado();
        // valido que tenga un Destino configurado
        if(guia.getDestino() == null){
            JsfUtil.addErrorMessage("Antes de guardar un Transporte, debería configurar un Destino para la Guía.");
        }else{
            // continúo según el Destino asignado ya existiera previamente
            try{
                transporte.setVehiculo(vehiculo);
                String acop = transporte.getAcoplado();
                transporte.setAcoplado(acop.toUpperCase());
                String nombre = transporte.getCondNombre();
                transporte.setCondNombre(nombre.toUpperCase());
                guia.setTransporte(transporte);
                guia.setUsuario(usLogueado);
                
                // chequeo los estados
                if(guia.getTipo().isAbonaTasa() && guia.getDestino() != null && !guia.getItems().isEmpty()){
                    // si abona tasas, tiene destino y tiene productos habilito la liquidación
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
                }else if(!guia.getTipo().isAbonaTasa() && guia.getDestino() != null && !guia.getItems().isEmpty()){
                    // si no abona tasas, tiene transporte y productos, habilito el paso a emisión
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConTransporte"));
                }
                if(estado != guia.getEstado()){
                    guia.setEstado(estado);
                }

                guiaFacade.edit(guia);
                matBuscar = "";
                vehiculo = new Vehiculo();
                transporte = new Transporte();
                if(editTransporte) {
                    editTransporte = false;
                }
                JsfUtil.addSuccessMessage("El Tranporte se registró correctamente.");
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error guardando el Transporte: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para editar los datos del Transporte
     */
    public void prepareEditTranporte(){
        vehiculo = guia.getTransporte().getVehiculo();
        transporte = guia.getTransporte();
        editTransporte = true;
    }
    
    ///////////////
    // Productos //
    ///////////////
    /**
     * Método para generar un ItemAsignado con el consecuente descuento del itemFuente correspondiente
     */
    public void descontarProducto(){
        // seteo el saldo temporal para la operatoria
        itemFuente.setSaldoTemp(itemFuente.getSaldo());
        // instancio el itemAsignado y seteo los datos correspondientes
        itemAsignado = new ItemProductivo();
        itemAsignado.setIdProd(itemFuente.getIdProd());
        itemAsignado.setItemOrigen(itemFuente.getId());
        itemAsignado.setClase(itemFuente.getClase());
        itemAsignado.setUnidad(itemFuente.getUnidad());
        itemAsignado.setNombreCientifico(itemFuente.getNombreCientifico());
        itemAsignado.setNombreVulgar(itemFuente.getNombreVulgar());
        itemAsignado.setIdEspecieTax(itemFuente.getIdEspecieTax());
        itemAsignado.setKilosXUnidad(itemFuente.getKilosXUnidad());
        itemAsignado.setCodigoProducto(itemFuente.getCodigoProducto());
        
        TipoParam tipoParamTipoItemActual = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoItem"));
        itemAsignado.setTipoActual(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Extraidos"), tipoParamTipoItemActual));
        
        TipoParam tipoParamTipoItemOrigen = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoItem"));
        itemAsignado.setTipoOrigen(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Autorizados"), tipoParamTipoItemOrigen));
        
        itemAsignado.setTotalOrigen(itemFuente.getTotal());
        itemAsignado.setSaldoOrigen(itemFuente.getSaldo());
        
        descontandoProd = true;
    }
    
    /**
     * Método para agregar Productos provenientes de un itemFuente a un itemAsignado, 
     * incluye la actualización del saldo del itemFuente
     */
    public void addProducto(){
        EstadoGuia estado = guia.getEstado();
        boolean validaCantidad = true;
        // obtengo el tipoActual (item Autorizado) (tipoOrigen e itemOrigen van nulos
        Parametrica tipoActual = obtenerParametro(ResourceBundle.getBundle("/Config").getString("TipoItem"), ResourceBundle.getBundle("/Config").getString("Extraidos"));
        // valido la cantidad con el saldo
        if(itemAsignado.getTotal() > itemAsignado.getSaldoOrigen() || itemAsignado.getTotal() == 0){
            if(!editandoItem) validaCantidad = false;
            else if(itemAsignado.getTotal() > itemAsignado.getSaldoTemp()) validaCantidad = false;
        }

        // solo continúo si los parámetros están configurados
        if(tipoActual != null && estado != null && validaCantidad){
            
            // actualizo el saldo del itemAsignado
            itemAsignado.setSaldo(itemAsignado.getTotal());            
            // obtengo el Estado a setear
            if(guia.getTipo().isHabilitaTransp()){
                // si es de transporte verifico si tiene Destino
                if(guia.getDestino() == null){
                    // si no tiene Destino solo habilito la edición de datos complementarios, no permito continuar más allá del seteo de productos
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaSinTransporte"));
                }else if(guia.getTipo().isAbonaTasa() && guia.getTransporte() != null){
                    // si tiene destino, transporte y abona tasas, habilito el pase a liquidación
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
                }else if(guia.getTipo().isAbonaTasa() && guia.getTransporte() == null){
                    // si tiene destino, no tiene transporte y paga tasas, verifico si es movimiento interno
                    if(guia.getTipo().isMovInterno()){
                        // si es movimiento interno, lo habilito el pase a liquidación
                        estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
                    }else{
                        // si no es movimiento interno, no permito continuar más allá del seteo de productos 
                        estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaSinTransporte"));
                    }
                }else if(!guia.getTipo().isAbonaTasa() && guia.getTransporte() != null){
                    // si tiene destino, no abona tasa y tiene transporte, lo habilito a pasar a la emisión
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConTransporte"));
                }else if(!guia.getTipo().isAbonaTasa() && guia.getTransporte() == null){
                    // si tiene destino, no abona tasa y no tiene transporte, verifico si es movimiento interno
                    if(guia.getTipo().isMovInterno()){
                        // si no en movimiento interno, la dejo como liquidada y habilito emisión
                        estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConDestino"));
                    }else{
                        // si no, no permito continuar más allá del seteo de productos 
                        estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
                    }
                }
            }else{
                // si no es de transporte verifico si abona tasa
                if(guia.getTipo().isAbonaTasa()){
                    // si abona tasas, lo habilito a pasar a la liquidación
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProductos"));
                }else{
                    // si no abona tasas, lo habilito para la emisión
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaLiquidada"));
                }
            }
            try{
                // actualizo el saldo del itemOrigen
                for(ItemProductivo ip : lstItemsAutorizados){
                    if(Objects.equals(ip.getId(), itemAsignado.getItemOrigen())){
                        if(editandoItem){
                            ip.setSaldo(itemAsignado.getSaldoTemp() - itemAsignado.getTotal());
                        }else{
                            ip.setSaldo(ip.getSaldo() - itemAsignado.getTotal());
                        }
                        ip.setSaldoKg(ip.getSaldo() * ip.getKilosXUnidad());
                        ip.setDescontado(true);
                        // actualizo el item fuente
                        itemFacade.edit(ip);
                    }
                }
                if(editandoItem){
                    // actualizo el equivalente total en Kg.
                    itemAsignado.setTotalKg(itemAsignado.getTotal() * itemAsignado.getKilosXUnidad());
                    // seteo el equivalente en Kg. del saldo
                    itemAsignado.setSaldoKg(itemAsignado.getTotalKg());
                    // actualizo el item asignado a la guía
                    itemFacade.edit(itemAsignado);
                    editandoItem = false;
                }else{
                    // creo el item asignado a la guía
                    itemAsignado.setTipoActual(tipoActual);
                    // seteo los datos de inserción
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    itemAsignado.setFechaAlta(fechaAlta);
                    // seteo el usuario
                    itemAsignado.setUsuario(usLogueado);
                    // se habilitará cuando se habilite la Guía (NO SE)
                    itemAsignado.setHabilitado(true);
                    // seteo el saldo igual al total
                    itemAsignado.setSaldo(itemAsignado.getTotal());
                    // seteo el equivalente total en Kg.
                    itemAsignado.setTotalKg(itemAsignado.getTotal() * itemAsignado.getKilosXUnidad());
                    // seteo el equivalente en Kg. del saldo
                    itemAsignado.setSaldoKg(itemAsignado.getTotalKg());
                    // seteo la guía
                    itemAsignado.setGuia(guia);
                    itemFacade.create(itemAsignado);
                    // asigno el estado
                    if(estado != guia.getEstado()){
                        guia.setEstado(estado);
                    }
                    guiaFacade.edit(guia);
                    guia.getItems().add(itemAsignado);
                    // actualizo el flag
                    descontandoProd = false;
                }
                itemAsignado = null;
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error agregadon el Producto a la Guía: " + ex.getMessage());
            }
        }else if(tipoActual == null){
            JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Tipo de Item: 'Extraidos'.");
        }
        if(!validaCantidad){
            JsfUtil.addErrorMessage("La cantidad a descontar debe ser menor o igual al saldo disponible y mayor a 0.");
        }
        if(estado == null){
            JsfUtil.addErrorMessage("No se pudo encontrar un Estado de Guía para: 'GuiaConProductos'.");
        }
    }
    
    /**
     * Método para eliminar un item productivo asociado a una Guía
     */
    public void deleteProducto(){
        try{
            // actualizo el itemOrigen, agregando al saldo lo descontado para el item a eliminar
            ItemProductivo itemOrigen = itemFacade.find(itemAsignado.getItemOrigen());
            if(itemOrigen != null){
                // actualizo el saldo del item origen
                float saldo = itemOrigen.getSaldo();
                float saldoActualizado = saldo + itemAsignado.getTotal();
                itemOrigen.setSaldo(saldoActualizado);
                // actualizo el saldo en Kg
                itemOrigen.setSaldoKg(itemOrigen.getSaldo() * itemOrigen.getKilosXUnidad());
                // actualizo el itemOrigen
                itemFacade.edit(itemOrigen);
                // elimino el itemAsignado de la Guía
                itemFacade.remove(itemAsignado);
                // si estoy eliminando el único producto de la Guía, actualizo su estado
                getLstItemsAsignados();
                if(lstItemsAsignados.isEmpty()){
                    // asumo que si el producto fue creado, había configurado un Estado incicial para la Guía
                    EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaInicial"));
                    guia.setEstado(estado);
                    List<ItemProductivo> items = new ArrayList<>();
                    guia.setItems(items);
                    guiaFacade.edit(guia);
                }
                // muestro mensaje
                JsfUtil.addSuccessMessage("El Producto ha sido eliminado de la Guía correctamente.");
                itemAsignado = null;
            }
            editandoItem = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error eliminando el Producto. " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el formulario para el descuento y volver al listado
     */
    public void limpiarDescuento(){
        descontandoProd = false;
    }
    
    /**
     * Método para habilitar el formulario para la edición del Item asignado
     */
    public void prepareEditItem(){
        // obtengo el total autorizado del Item origen y seteo cantProdDescuento y saldo con el total y el saldo del Item asignado
        for(ItemProductivo ip : lstItemsAutorizados){
            if(Objects.equals(ip.getId(), itemAsignado.getItemOrigen())){
                itemAsignado.setSaldoTemp(ip.getSaldo() + itemAsignado.getTotal());
                itemAsignado.setSaldoOrigen(ip.getSaldo());
                itemAsignado.setTotalOrigen(ip.getTotal());
            }
        }
        editandoItem = true;
    }
    
    /**
     * Método para limpiar el formulario de edición del producto descontado
     */
    public void limpiarEdicion(){
        // vuelvo a los datos originales
        editandoItem = false;
        itemAsignado = null;
    }
    
    //////////////////////////////////////////////////
    // Métodos para el detalle de Tasasa a liquidar //
    //////////////////////////////////////////////////
    /**
     * Método que muestra el valor unitario de tasa para cada Producto del Item.
     * Hace uso de la clase útil TasaModel
     * @param nombreProd String nombre del Producto del item
     * @param clase String clase del Producto del item
     * @param nombreTasa String nombre de la Tasa del Prducto
     * @return float valor unitario de la tasa
     */
    public float getLiqUnitarioByTasa(String nombreProd, String clase, String nombreTasa){
        float result = 0;
        for(DetalleTasas detTasa : lstDetallesTasas){
            if(detTasa.getNombreProd().equals(nombreProd) && detTasa.getClase().equals(clase)){
                for(TasaModel tm : detTasa.getTasas()){
                    if(tm.getHeader().equals(nombreTasa)){
                        result = tm.getProperty();
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Método que muestra el total a liquidar por cada tasa para cada item
     * Hace uso de la clase útil TasaModel
     * @param nombreProd String nombre del Producto del item
     * @param clase String clase del Producto del item
     * @param nombreTasa String nombre de la Tasa del Prducto
     * @return float valor correspondiente al total a liquidar por la tasa
     */
    public float getLiqTotalByTasa(String nombreProd, String clase, String nombreTasa){
        float result = 0;
        for(DetalleTasas detTasa : lstDetallesTasas){
            if(detTasa.getNombreProd().equals(nombreProd) && detTasa.getClase().equals(clase)){
                for(TasaModel tm : detTasa.getTasas()){
                    if(tm.getHeader().equals(nombreTasa)){
                        result = tm.getProperty() * detTasa.getCantidad();
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Método que muestra el total por cada tasa
     * @param nombreTasa String nombre de la Tasa liquidada
     * @return float total solicitado
     */
    public float getLiqTotales(String nombreTasa){
        return liquidaciones.get(0).getTotalByTasa(nombreTasa);
    }
    
    /**
     * Método que muestra el total liquidado para la Guía
     * @return float total liquidado
     */
    public float getTotalLiquidado(){
        return liquidaciones.get(0).getTotalALiquidar();
    }
    
    /**
     * Método para generar el volante de pago de las tasas inherentes a los
     * Productos forestales asociados a los Items de la Guía
     */
    public void generarVolante(){
        try{
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(liquidaciones);
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "volante.jasper");
            jasperPrint =  JasperFillManager.fillReport(reportPath, new HashMap(), beanCollectionDataSource);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=volante_pago_" + liquidaciones.get(0).getCodGuia() + ".pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
            
            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
            
            // seteo la fecha de emisión del volante de pago
            Date fechaEmision = new Date(System.currentTimeMillis());
            guia.setFechaEmisionVolante(fechaEmision);
            // obtengo el Estado a setear y lo actualizo
            EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaLiquidada"));
            guia.setEstado(estado);
            guiaFacade.edit(guia);
        }catch(JRException | IOException ex){
            JsfUtil.addErrorMessage("Hubo un error al generar el volante de pago. " + ex.getMessage());
        }
    }
    
    /**
     * Método para registrar un Código de recibo para la Guía.
     * Una vez efectuado el pago de las tasas
     */
    public void registrarCodRecibo(){
        try{
            // actualizo
            guiaFacade.edit(guia);
            JsfUtil.addSuccessMessage("El código del recibo fue agregado a la Guía. Ya puede emitirla.");
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error registrando el código del recibo. " + ex.getMessage());
        }
    }
    
    /**
     * Método para emitir una Guía, con o sin transporte. En el caso de que sea de transporte, se detalla el proceso:
     * Después de las validaciones se genera el pdf, luego se actualiza la Guía.e
     * Posteriormente, si la Guía no es de acopio interno (reporta Transporte) se registra en el Componente de Control y Verificación (CCV)
     * Finalmente se verifica la existencia de una cuenta de usuario para el Destinatario de la Guía
     * en el Componente de Gestión de Trazabilidad. Si no es así, la genera mediante la API-TRAZ, si ya existe la cuenta 
     * solo se envía un correo al Destinatario dando aviso de la remisión de la Guía.
     */    
    public void emitir(){
        List<Guia> guias = new ArrayList<>();
        // asigno la fecha de emisión
        guia.setFechaEmisionGuia(new Date(System.currentTimeMillis()));
        // asigno la fecha de vencimiento, si el tipo de Guía tiene vigencia asignada.
        if(guia.getTipo().getVigencia() > 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(guia.getFechaEmisionGuia());
            calendar.add(Calendar.DAY_OF_YEAR, guia.getTipo().getVigencia());
            guia.setFechaVencimiento(calendar.getTime());
        }
        // cambio el estado de la Guía
        EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaEmitida"));
        guia.setEstado(estado);        
        try{
            // genero las copias que correspondan, según el listado de Copias asignado al Tipo de Guía
            Guia g;
            for(CopiaGuia copia : guia.getTipo().getCopias()){
                // seteo la Guía en el listado
                g = new Guia();
                g.setCodigo(guia.getCodigo());
                g.setDestinoCopia(copia.getDestino());
                g.setFechaAlta(guia.getFechaAlta());
                g.setFechaEmisionGuia(guia.getFechaEmisionGuia());
                g.setFechaVencimiento(guia.getFechaVencimiento());
                g.setItems(guia.getItems());
                g.setOrigen(guia.getOrigen());
                g.setTipo(guia.getTipo());
                g.setProvincia(guia.getProvincia());
                // si la Guía habilita transporte, agrego los campos correspondientes
                if(guia.getTipo().isHabilitaTransp()){
                    g.setDestino(guia.getDestino());
                    g.setTransporte(guia.getTransporte());
                }
                guias.add(g);
            }

            // genero el reporte con todas las copias
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(guias);
            String reportPath;
            if(guia.getTipo().isHabilitaTransp() && !guia.getTipo().isMovInterno()){
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guiaTransp.jasper");
            }else if(guia.getTipo().isHabilitaTransp() && guia.getTipo().isMovInterno()){
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guiaAcopio.jasper");
            }else{
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guia.jasper");
            }
            
            jasperPrint =  JasperFillManager.fillReport(reportPath, new HashMap(), beanCollectionDataSource);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            if(guia.getTipo().isHabilitaTransp() && !guia.getTipo().isMovInterno()){
                httpServletResponse.addHeader("Content-disposition", "attachment; filename=guiaTransp_" + guias.get(0).getCodigo() + ".pdf");
            }else if(guia.getTipo().isHabilitaTransp() && guia.getTipo().isMovInterno()){
                httpServletResponse.addHeader("Content-disposition", "attachment; filename=guiaAcopio_" + guias.get(0).getCodigo() + ".pdf");
            }else{
                httpServletResponse.addHeader("Content-disposition", "attachment; filename=guia_" + guias.get(0).getCodigo() + ".pdf");
            }

            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete(); 
            // actualizo la Guía
            guiaFacade.edit(guia);
            
            // si la Guía habilita transporte y no es de acopio interno, se impacta en el Componente de Control y Verificación (CCV), 
            // y se prosigue con la verificación de cuenta de usuario en el componente de Gestión de Trazabilidad (CGT), 
            // si la Guía es de acopio interno, no se persiste en el CCV y pasa directamente a la verificación de la cuenta de usuario en el CGT 
            if(guia.getTipo().isHabilitaTransp()){
                boolean persistidaCcv = false;
                // si no es de acopio interno persisto la Guía en el CCV
                if(!guia.getTipo().isMovInterno()){
                    List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item> lstItemCtrl = new ArrayList<>();
                    ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item itemCtrl;
                    for(ItemProductivo item : guia.getItems()){
                        itemCtrl = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item();
                        itemCtrl.setCodigoOrigen(item.getCodigoProducto());
                        itemCtrl.setNombreCientifico(item.getNombreCientifico());
                        itemCtrl.setNombreVulgar(item.getNombreVulgar());
                        itemCtrl.setClase(item.getClase());
                        itemCtrl.setUnidad(item.getUnidad());
                        itemCtrl.setTotal(item.getTotal());
                        itemCtrl.setTotalKg(item.getTotalKg());
                        lstItemCtrl.add(itemCtrl);
                    }
                    // creo la Guia a enviar
                    ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCrtl = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                    guiaCrtl.setCodigo(guia.getCodigo());
                    guiaCrtl.setTipo(guia.getTipo().getNombre());
                    guiaCrtl.setTipoFuente(guia.getTipoFuente().getNombre());
                    guiaCrtl.setNumFuente(guia.getNumFuente());
                    guiaCrtl.setItems(lstItemCtrl);
                    guiaCrtl.setNombreOrigen(guia.getOrigen().getNombreCompleto());
                    guiaCrtl.setCuitOrigen(guia.getOrigen().getCuit());
                    guiaCrtl.setLocOrigen(guia.getOrigen().getLocalidad() + " - " + guia.getOrigen().getProvincia());
                    guiaCrtl.setNombreDestino(guia.getDestino().getNombreCompleto());
                    guiaCrtl.setCuitDestino(guia.getDestino().getCuit());
                    guiaCrtl.setLocDestino(guia.getDestino().getLocalidad() + " - " + guia.getDestino().getProvincia());
                    guiaCrtl.setMatVehiculo(guia.getTransporte().getVehiculo().getMatricula());
                    guiaCrtl.setMatAcoplado(guia.getTransporte().getAcoplado());
                    guiaCrtl.setNombreConductor(guia.getTransporte().getCondNombre());
                    guiaCrtl.setDniConductor(guia.getTransporte().getCondDni());
                    guiaCrtl.setFechaEmision(guia.getFechaEmisionGuia());
                    guiaCrtl.setFechaVencimiento(guia.getFechaVencimiento());
                    guiaCrtl.setProvincia(guia.getProvincia());
                    
                    // obtengo el token si no está seteado o está vencido
                    if(tokenCtrl == null){
                        getTokenCtrl();
                    }else try {
                        if(!tokenCtrl.isVigente()){
                            getTokenCtrl();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                    }
                    
                    // persisto una copia de la Guía para ser controlada y verificada por el CCV
                    guiaCtrlClient = new GuiaCtrlClient();
                    Response responseCcv = guiaCtrlClient.create_JSON(guiaCrtl, tokenCtrl.getStrToken());
                    guiaCtrlClient.close();
                    
                    if(responseCcv.getStatus() == 201){
                        persistidaCcv = true;
                    }
                }else{
                    persistidaCcv = true;
                }
                // solo continúo si no hubo error
                if(persistidaCcv){
                    // obtengo el token si no está seteado o está vencido
                    if(tokenTraz == null){
                        getTokenTraz();
                    }else try {
                        if(!tokenTraz.isVigente()){
                            getTokenTraz();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                    }
                    usuarioClientTraz = new UsuarioClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>> gTypeUs = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>>() {};
                    Response responseCgt = usuarioClientTraz.findByQuery_JSON(Response.class, String.valueOf(guia.getDestino().getCuit()), null, tokenTraz.getStrToken());
                    List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario> listUs = responseCgt.readEntity(gTypeUs);
                    if(!listUs.isEmpty()){
                        usuarioClientTraz.close();
                        // el Usuario ya está registrado, entonces solo se envía el correo de aviso
                        if(!enviarCorreo()){
                            if(guia.getTipo().isMovInterno()){
                                msgErrorEmision = "La Guía ha sido emitida pero hubo un error enviando el correo al Destinatario, que deberá ser notificado.";
                            }else{
                                msgErrorEmision = "La Guía ha sido emitida y se la reportó en el Componente de Control y Verificación para ser controlada, pero hubo un error enviando el correo al Destinatario, que deberá ser notificado.";
                            }
                        }else{
                            if(guia.getTipo().isMovInterno()){
                                msgExitoEmision = "La Guía se emitió correctamente y se notificó por correo electrónico al Destinatario.";
                            }else{
                                msgExitoEmision = "La Guía se emitió correctamente, se la reportó en el Componente de Control y Verificación para ser controlada y se notificó por correo electrónico al Destinatario.";
                            }
                        }
                    }else{
                        // seteo el usuario a crear en el componente de Gestión de Trazabilidad
                        // obtengo el token si no está seteado o está vencido
                        if(tokenTraz == null){
                            getTokenTraz();
                        }else try {
                            if(!tokenTraz.isVigente()){
                                getTokenTraz();
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                        }
                        // obtengo el TipoParam para el Rol
                        tipoParamClient = new TipoParamClient();
                        GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam>> gTypeTipo = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam>>() {};
                        responseCgt = tipoParamClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("RolUsuarios"), tokenTraz.getStrToken());
                        List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam> listTipos = responseCgt.readEntity(gTypeTipo);
                        ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica rol = null;
                        if(!listTipos.isEmpty()){
                            // obtengo el rol                    
                            GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica>> gTypeParam = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica>>() {};
                            responseCgt = tipoParamClient.findParametricasByTipo_JSON(Response.class, String.valueOf(listTipos.get(0).getId()), tokenTraz.getStrToken());
                            List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica> listParam = responseCgt.readEntity(gTypeParam);
                            tipoParamClient.close();
                            for(ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica param : listParam){
                                if(param.getNombre().equals(ResourceBundle.getBundle("/Config").getString("Transformador"))){
                                    rol = param;
                                }
                            }
                        }else{
                            tipoParamClient.close();
                        }
                        if(rol != null){
                            // si obtuve el Rol, registro el Usuario vía API-TRAZ
                            ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario usTraz;
                            usTraz = new ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario();
                            usTraz.setEmail(guia.getDestino().getEmail());
                            usTraz.setJurisdiccion(guia.getDestino().getProvincia());
                            usTraz.setLogin(guia.getDestino().getCuit());
                            usTraz.setNombreCompleto(guia.getDestino().getNombreCompleto());
                            usTraz.setRol(rol);

                            responseCgt = usuarioClientTraz.create_JSON(usTraz, tokenTraz.getStrToken());
                            usuarioClientTraz.close();

                            // solo continúo si no hubo error
                            if(responseCgt.getStatus() == 201){
                                if(guia.getTipo().isMovInterno()){
                                    msgExitoEmision = "Se creó la cuenta de Usuario para el Destinatario en el Componente de Trazabilidad del Sistema, desde ahí la Guía será cerrada por el Destinatario.";
                                }else{
                                    msgExitoEmision = "Se reportó la Guía en el Componente de Control y Verificación para ser controlada y se creó la cuenta de Usuario para el Destinatario en el Componente de Trazabilidad del Sistema, desde ahí la Guía será cerrada por el Destinatario.";
                                }
                            }else{
                                if(guia.getTipo().isMovInterno()){
                                    msgErrorEmision = "No se pudo generar el Usuario en el Componente de Trazabilidad del Sistema, deberá solicitar su registro al Administrador del mismo.";
                                }else{
                                    msgErrorEmision = "Se reportó la Guía en el Componente de Control y Verificación para ser controlada pero no se pudo generar el Usuario en el Componente de Trazabilidad del Sistema, deberá solicitar su registro al Administrador del mismo.";
                                }
                            }   
                        }else{
                            if(guia.getTipo().isMovInterno()){
                                msgErrorEmision = "No se pudo obtener el Rol del Usuario del Destinatario en el Componente de Trazabilidad del Sistema para crear la respectiva cuenta de Usuario.";
                            }else{
                                msgErrorEmision = "Se reportó la Guía en el Componente de Control y Verificación para ser controlada pero no se pudo obtener el Rol del Usuario del Destinatario en el Componente de Trazabilidad del Sistema para crear la respectiva cuenta de Usuario.";
                            }
                        }
                    }
                }else{
                    msgErrorEmision = "Hubo un error reportanto la Guía para su control en el Componente de Control y Verificación, por lo tanto tampoco se notificó al Destinatario. Deberá contactar al Administrador.";
                }
            }
        }catch(JRException | IOException ex){
            msgErrorEmision = "Hubo un error emitiendo la guía. " + ex.getMessage();
        }
    }
    
    //////////////////////////////////////
    // Métodos para el listado de Guias //
    //////////////////////////////////////
    /**
     * Método para inicializar el listado Autorizaciones
     */
    public void prepareList(){
        guia = null;
        view = false;
    }
    
    /**
     * Método para inicializar la vista detalle de la Guía.
     * Instancia las posibles guías que toman productos de la presente
     * y el o los inmueble/s de origen
     */
    public void prepareViewDetalle(){
        // Busco las Guías de las que pudiera ser fuente la Autorización
        lstGuiasHijas = guiaFacade.findByNumFuente(guia.getCodigo());
        // Busco los inmuebles 
        setearInmueblesOrigen();
        view = true;
    }    
    
    //////////////////////
    // Métodos privados //
    //////////////////////

    /**
     * Método para limpiar resetear el listado con los tipos.
     * utilizado en prepareNew(), prepareEdit() y limpiarForm()
     */
    private void resetearCampos() {
        lstTiposGuia = tipoGuiaFacade.getHabilitados();
    }

    /**
     * Método para obtener la EntidadGuia de origen, si no existe se crea.
     * Solo seteo los datos del Productor. Utilizado en save()
     * @return EntidadGuia entidad guía generada
     */
    private EntidadGuia obtenerEntidadOrigen(Persona per, Parametrica tipoFuente) {
        EntidadGuia ent;
        Inmueble inm = new Inmueble();
        if(tipoFuente.getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            inm = autSelected.getInmuebles().get(0);
            // verifico si existe previamente
            ent = entGuiaFacade.getOrigenExistente(per.getCuit(), inm.getNombre());
        }else{
            ent = guiaSelected.getOrigen();
        }

        if(ent != null){
            return ent;
        }else{
            ent = new EntidadGuia();
            // productor
            ent.setCuit(per.getCuit());
            ent.setIdRue(per.getIdRue());
            ent.setNombreCompleto(per.getNombreCompleto());
            ent.setTipoPersona(per.getTipo());
            ent.setEmail(per.getEmail());
            // entidad
            TipoParam tipoParamEntGuia = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoEntidadGuia"));
            ent.setTipoEntidadGuia(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TegFuente"), tipoParamEntGuia));
            // inmueble
            if(inm.getIdCatastral() != null){
                ent.setInmCatastro(inm.getIdCatastral());
            }
            ent.setInmNombre(inm.getNombre());
            ent.setDepartamento(inm.getDepartamento());
            ent.setIdLocGT(inm.getIdLocGt());
            ent.setLocalidad(inm.getLocalidad());
            ent.setProvincia(inm.getProvincia());
            // autorización
            ent.setNumAutorizacion(autSelected.getNumero());
            return ent;
        }
    }
    
    /**
     * Método para obtener la EntidadGuia de destino, si no existe se crea.
     * Solo seteo los datos del Destinatario. Utilizado en buscarDestinatario()
     * @return EntidadGuia entidad guía generada
     */
    private EntidadGuia obtenerEntidadDestino(Persona per) {
        EntidadGuia ent;
        // verifico si existe previamente
        TipoParam tipoParamEntGuia = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoEntidadGuia"));
        Parametrica tipoEntidad = paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TegDestino"), tipoParamEntGuia);
        ent = entGuiaFacade.getDestinoExistente(per.getCuit(), tipoEntidad);
        if(ent != null){
            return ent;
        }else{
            ent = new EntidadGuia();
            // destinatario
            ent.setCuit(per.getCuit());
            ent.setIdRue(per.getIdRue());
            ent.setNombreCompleto(per.getNombreCompleto());
            ent.setTipoPersona(per.getTipo());
            ent.setEmail(per.getEmail());
            // destino
            try{
                // obtengo el token si no está seteado o está vencido
                if(tokenRue == null){
                    getTokenRue();
                }else try {
                    if(!tokenRue.isVigente()){
                        getTokenRue();
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                }
                
                
                personaClient = new PersonaClient();
                personaRue = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(per.getIdRue()), tokenRue.getStrToken());
                personaClient.close();
                ent.setIdLocGT(personaRue.getDomicilio().getIdLocalidadGt());
                ent.setInmDomicilio(personaRue.getDomicilio().getCalle() + "-" + personaRue.getDomicilio().getNumero());
                ent.setLocalidad(personaRue.getDomicilio().getLocalidad());
                ent.setDepartamento(personaRue.getDomicilio().getDepartamento());
                ent.setProvincia(personaRue.getDomicilio().getProvincia());
            }catch(ClientErrorException ex){
                // muestro un mensaje al usuario
                JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
                        + "servicio REST de RUE", ex.getMessage()});
                return null;
            }
            
            // entidad
            ent.setTipoEntidadGuia(tipoEntidad);
            return ent;
        }
    }    

    /**
     * Método que crea el código de la Guía
     * De acuerdo a la configuración del Config.properties.
     * Utilizado en save()
     * @return String código generado
     */
    private String setCodigoGuia() {
        String codigo;
        Calendar fecha = Calendar.getInstance();
        String sAnio = String.valueOf(fecha.get(Calendar.YEAR));
        int id = guiaFacade.getUltimoId();
        
        // si la Guía es de transporte
        if(guia.getTipo().isHabilitaTransp()){
            if(guia.getTipoFuente().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
                // si surge de una Autorización => 'ET'
                codigo = "ET";
            }else{
                // si surge de otra Guía => 'TT'
                codigo = "TT";
            }
        }else{
            // si es de extracción => 'EE'
            codigo = "EE";
        }
        if(id == 0){
            // si inicia
            codigo = codigo + "-" + ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + "-00001";
        }else{
            String sId = String.valueOf(id + 1);
            int ceros = 5 - sId.length();
            // agrego los ceros a la izquierda
            int j = ceros;
            codigo = codigo + "-" + ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + "-";
            while(j > 0){
                codigo = codigo + "0";
                j -= 1;
            }
            codigo = codigo + sId;
        }
        return codigo + "-" + sAnio;
    }

    /**
     * Método para obtener una Paramétrica según su nombre y nombre del Tipo.
     * Utilizado en buscarDestinatario() y addProducto()
     * @param nomTipo String nombre del Tipo de Paramétrica
     * @param nomParam String nombre de la Paramétrica
     * @return Parametrica paramétrica obtenida
     */
    private Parametrica obtenerParametro(String nomTipo, String nomParam) {
        TipoParam tipo = tipoParamFacade.getExistente(nomTipo);
        return paramFacade.getExistente(nomParam, tipo);
    }
    
    /**
     * Método para enviar un correo electrónico al usuario.
     * Utilizado en emitir()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean enviarCorreo(){  
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>Se acaba de emitir la Guía " + guia.getCodigo() + " a su nombre, proveniente de la Provincia de " + ResourceBundle.getBundle("/Config").getString("Provincia") + ".</p>"
                + "<p>Dispone hasta el " + formateador.format(guia.getFechaVencimiento()) + " para aceptarla y completar el ciclo.</p>"
                + "<p>Podrá hacerlo ingresando a " + ResourceBundle.getBundle("/Config").getString("TrazServer") + ResourceBundle.getBundle("/Config").getString("TrazRutaAplicacion") + " "
                + "con sus credenciales de acceso.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guia.getDestino().getEmail()));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Aviso de emisión de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo de registro de usuario" + ex.getMessage());
        }
        
        return result;
    }       

    /**
     * Método para listar los inmuebles vinculados a la Autorización de la cual provienen los productos de la Guía.
     * Utilizado en buscar(), prepareView(), save() y prepareViewDetalle()
     */
    private void setearInmueblesOrigen() {
        lstInmueblesOrigen = new ArrayList<>();
        // obtengo la Autorización según el tipo de Guía
        Autorizacion aut;
        if(guia.getTipoFuente().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            // si la fuente de la Guía es una Autorización, obtengo sus inmuebles
            aut = autFacade.getExistente(guia.getNumFuente());
        }else{
            // si la fuente es una Guía obtengo su Autorización
            Guia g = guiaFacade.getExistente(guia.getNumFuente());
            aut = autFacade.getExistente(g.getNumFuente());
        }
        for(Inmueble inm : aut.getInmuebles()){
            lstInmueblesOrigen.add(inm);
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en obtenerEntidadDestino(Persona per)
     */
    private void getTokenRue(){
        try{
            usClientRue = new ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient();
            Response responseUs = usClientRue.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestRue"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenRue = (String)lstHeaders.get(0); 
            tokenRue = new Token(strTokenRue, System.currentTimeMillis());
            usClientRue.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }      
    
    /**
     * Método privado que obtiene y setea el tokenTraz para autentificarse ante la API rest de trazabilidad
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en emitir()
     */    
    private void getTokenTraz(){
        try{
            usApiClientTraz = new ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioApiClient();
            Response responseUs = usApiClientTraz.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestSvf"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenTraz = (String)lstHeaders.get(0); 
            tokenTraz = new Token(strTokenTraz, System.currentTimeMillis());
            usApiClientTraz.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenTraz para autentificarse ante la API rest de control y verificación
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en emitir()
     */    
    private void getTokenCtrl(){
        try{
            usApiClientCtrl = new ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.UsuarioApiClient();
            Response responseUs = usApiClientCtrl.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestSvf"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenCtrl = (String)lstHeaders.get(0); 
            tokenCtrl = new Token(strTokenCtrl, System.currentTimeMillis());
            usApiClientCtrl.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }    
}
