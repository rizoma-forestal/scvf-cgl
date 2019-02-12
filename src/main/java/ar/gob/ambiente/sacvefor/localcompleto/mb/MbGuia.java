
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.GuiaCtrlClient;
import ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.ParamCtrlClient;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Delegacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.FormProv;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Transporte;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.DelegacionFacade;
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
import ar.gob.ambiente.sacvefor.localcompleto.util.CriptPass;
import ar.gob.ambiente.sacvefor.localcompleto.util.DetalleTasas;
import ar.gob.ambiente.sacvefor.localcompleto.util.DetalleTasas.TasaModel;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.Formulario;
import ar.gob.ambiente.sacvefor.localcompleto.util.GuiaSinPago;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.LiqTotalTasas;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Variable privada: guarda las claves y nombres para la gestión de los mensajes de vigencia de las guías.
     * Se setea en el método init() para el caso de la vista formulario.xhtml
     */
    private static final Map<Integer, String> VIGENCIAS = iniciateMap();

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
    
    //////////////////////////////////////
    // campos para la búsqueda de guías //
    //////////////////////////////////////
    
    /**
     * Variable privada: listado de las opciones de búsqueda para obtener el listado de guías.
     * Según la opción seleccionada se abrirá mostrará el campo para el ingreso de la opción y el botón de búsqueda.
     */
    private List<EntidadServicio> lstOptBucarGuias;
    
    /**
     * Variable privada: opción seleccionada para realizar la búsqueda
     */
    private EntidadServicio optBusqSelected;
    
    /**
     * Variable privada: litado de tipos de guías para llenar el combo de selección del tipo a buscar las guías respectivas
     */
    private List<TipoGuia> lstBusqTipoGuias;
    
    /**
     * Variable privada: tipo de guía seleccionado para la búsqueda
     */    
    private TipoGuia busqTipoGuiaSelected;
    
    /**
     * Variable privada: listado de estados de guías para llenar el combo de selección del estado a buscar las guías respectivas
     */    
    private List<EstadoGuia> lstBusqEstadosGuias;
    
    /**
     * Variable privada: estado de guía seleccionado para la búsqueda
     */    
    private EstadoGuia busqEstadoGuiaSelected;
    
    /**
     * Variable privada: cadena para guardar el código de la guía a buscar
     */    
    private String busqCodGuia;
    
    /**
     * Variable privada: cuit del titular cuyas guías se quiere buscar
     */    
    private Long busqCuitTit;
    
    /**
     * Variable privada: cuit del destinatario cuyas guías remitidas se quiere buscar
     */    
    private Long busqCuitDest;
    
    /**
     * Variable privada: número de autorización de la cual las guías a buscar tomaron los productos
     */    
    private String busqNumFuente;
    
    /**
     * Variable privada: flag que indica que la búsqueda no arrojó resultados
     */    
    private boolean busqSinResultados;
    
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
     * Variable privada: flag que indica si se muestra el detalle de la Autorización seleccionada como Fuente
     * de productos para la guía
     */
    private boolean viewFuente;
    
    /**
     * Variable privada: flag que indica si se muestra el detalle de la Guía seleccionada para descontar
     * productos para asingar a la guía
     */
    private boolean viewGuia;
    
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
    
    /**
     * Variable privada: Rodal rodal asingado a la Autorización fuente 
     * que se seleccionó para asignar o desasignar a la Guía que descuenta de una Autorización.
     * Solo para CGL que trabajan con inmuebles subdivididos en rodales.
     */
    private Rodal rodalSelected;
    
    /**
     * Variable privada: Listado de rodales de una Autorización, disponibles para ser vinculados a una Guía.
     */
    private List<Rodal> rodalesDisponibles;    
    
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
     * Variable privada: guia seleccionada para su asignación como descuento de productos
     */
    private Guia guiaSelected;   
    
    /**
     * Variable privada: guia asignada para descontar productos.
     */
    private Guia guiaAsignada;
    
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
    
    ///////////////////////////////////////
    // Obrajeros a solicitud de Misiones //
    ///////////////////////////////////////
    /**
     * Variable privada: guarda el cuit para buscar una persona con el rol de obrajero y agregarla a la guía
     */
    private Long cuitObraj;
    
    /**
     * Variable privada: persona a vincular a la guía con el rol de obrajero
     */
    private Persona obrajero;
    
    /**
     * Variable privada: flag que indica que se está en una vista detalle de los datos del obrajero
     */
    private boolean viewObrajero;
    
    /////////////
    // Destino //
    /////////////
    /**
     * Variable privada: Persona a seleccionada para conformar la Entidad Guía destino
     */
    private Persona destinatario;
    
    /**
     * Variable privada: Objeto para setear el domDestino seleccionado del destinatario
     * para setear la EntidadGuia destino.
     */    
    private Domicilio domDestino;
    
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
     * Variable privada: flag que indica que se está buscando el destino o un obrajero
     */
    private boolean cuitProcesado;
    
    /**
     * Variable privada: flag que indica si la guía está vencida, 
     * para habilitar o no el botón de cambio de destinatario
     */
    private boolean guiaVencida;

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
     * Variable privada: Listado de items procedentes de la fuente de productos de la guía.
     */
    private List<ItemProductivo> lstItemsOrigen;
    
    /**
     * Variable privada: Listado de items autorizados para descontar desde la Guía ya registrada
     */
    private List<ItemProductivo> lstItemsAutorizados;
    
    /**
     * Variable privada: Listado de items a descontar productos durante el registro de la guía
     */
    private List<ItemProductivo> lstItemsADescontar;
    
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
    
    /**
     * Variable privada: falg que indica si la guía se esta emitiendo sin acreditar el pago de tasas
     */
    private boolean emiteSinPago;
    
    /////////////////
    // Cancelación //
    /////////////////
    
    /**
     * Variable privada: mensaje que indicará que la guía no se puede cancelar
     */    
    private String msgCancelVencida;
    
    /**
     * Variable privada: mensaje que indicará que la guía podrá cancelarse
     */       
    private String msgCancelVigente;
    
    /**
     * Variable privada: mensaje que indica el error al cancelar la guía
     */
    private String msgErrorCancelGuia;
    
    ///////////////////////////
    // Extensión de vigencia //
    ///////////////////////////
    
    /**
     * Variable privada: flag que indica que el vencimiento de la guía fue actualizado.
     * Se setea en false cuando se inicia el frme de extensión de vencimiento.
     */
    private boolean vencAtualizado;
    
    //////////////////////////////////////////
    // impresión de formularios provisorios //
    //////////////////////////////////////////
    
    /**
     * Variable privada: mensaje que indicará que la guía está vencida y no puede emitir formularios
     */
    private String msgImpFormVencida;
    
    /**
     * Variable privada: mensaje que indicará que la guía está vigente y podrá emitir formularios
     */
    private String msgImpFormVigente;
    
    /**
     * Variable privada: listado de Delegaciones forestales para 
     * llenar el combo de destinos para la impresión de formularios provisorios
     */
    private List<Delegacion> lstDelegaciones;
    
    /**
     * Variable privada: delegación forestal seleccionada
     */
    private Delegacion delegSelected;
    
    /**
     * Variable privada: objeto a imprimir la cantidad de copias especificada
     */
    private Formulario form;
    
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
     * Variable privada: ParamCtrlClient Cliente para la API REST de Control y Verificación
     */
    private ParamCtrlClient paramCtrlClient;
    
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
 
    /**
     * Variable privada: formulario provisorio que se agregará a la guía
     * en los casos en que esté configurada la emisión de formularios provisorios
     */
    private FormProv formProv;
    
    /**
     * Variable privada: listado de guías que adeudan el pago de tasas.
     * Para mostrar en caso en que esté configurada la opción de emitir
     * guías con pagos adeudados.
     */
    private List<GuiaSinPago> guiasSinPago;
    
    /**
     * Variable privada: listado de entidades de servicio (id, nombre) para poblar el combo de guías emisoras
     * de formularios provisorias disponibles para la guía.
     */
    private List<EntidadServicio> lstGuiasEmisorasFormProv;
    
    /**
     * Variable privada: entidad de servicio que contiene los datos de la guía seleccionada (id y código)
     */
    private EntidadServicio guiaEmisoraSelected;

    ////////////////////////////////////////////////////
    // Accesos a datos mediante inyección de recursos //
    ////////////////////////////////////////////////////
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
     * Variable privada: EJB inyectado para el acceso a datos de Delegaciones forestales
     */
    @EJB
    private DelegacionFacade delegFacade;
    
    /**
     * Constructor
     */
    public MbGuia() {
    }

    ///////////////////////
    // métodos de acceso //
    ///////////////////////    
    public List<Rodal> getRodalesDisponibles() {    
        return rodalesDisponibles;
    }

    public void setRodalesDisponibles(List<Rodal> rodalesDisponibles) {    
        this.rodalesDisponibles = rodalesDisponibles;
    }

    public Rodal getRodalSelected() {
        return rodalSelected;
    }

    public void setRodalSelected(Rodal rodalSelected) {    
        this.rodalSelected = rodalSelected;
    }

    public Long getCuitObraj() {
        return cuitObraj;
    }

    public void setCuitObraj(Long cuitObraj) {
        this.cuitObraj = cuitObraj;
    }

    public Persona getObrajero() {
        return obrajero;
    }

    public void setObrajero(Persona obrajero) {
        this.obrajero = obrajero;
    }

    public boolean isViewObrajero() {
        return viewObrajero;
    }

    public void setViewObrajero(boolean viewObrajero) {    
        this.viewObrajero = viewObrajero;
    }

    public boolean isBusqSinResultados() {
        return busqSinResultados;
    }

    public void setBusqSinResultados(boolean busqSinResultados) {    
        this.busqSinResultados = busqSinResultados;
    }

    public EntidadServicio getOptBusqSelected() {
        return optBusqSelected;
    }

    public void setOptBusqSelected(EntidadServicio optBusqSelected) {   
        this.optBusqSelected = optBusqSelected;
    }

    public List<EntidadServicio> getLstOptBucarGuias() {
        lstOptBucarGuias = new ArrayList<>();
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(1), "Tipo de Guía"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(2), "Número de Guía"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(3), "CUIT del Titular"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(4), "CUIT del Destinatario"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(5), "Autorización fuente"));
        lstOptBucarGuias.add(new EntidadServicio(Long.valueOf(6), "Estado de la Guía"));
        return lstOptBucarGuias;
    }

    public void setLstOptBucarGuias(List<EntidadServicio> lstOptBucarGuias) {
        this.lstOptBucarGuias = lstOptBucarGuias;
    }

    public List<TipoGuia> getLstBusqTipoGuias() {
        lstBusqTipoGuias = tipoGuiaFacade.getHabilitados();
        return lstBusqTipoGuias;
    }

    public void setLstBusqTipoGuias(List<TipoGuia> lstBusqTipoGuias) {
        this.lstBusqTipoGuias = lstBusqTipoGuias;
    }

    public TipoGuia getBusqTipoGuiaSelected() {
        return busqTipoGuiaSelected;
    }

    public void setBusqTipoGuiaSelected(TipoGuia busqTipoGuiaSelected) {
        this.busqTipoGuiaSelected = busqTipoGuiaSelected;
    }

    public List<EstadoGuia> getLstBusqEstadosGuias() {
        lstBusqEstadosGuias = estadoFacade.getHabilitados();
        return lstBusqEstadosGuias;
    }

    public void setLstBusqEstadosGuias(List<EstadoGuia> lstBusqEstadosGuias) {
        this.lstBusqEstadosGuias = lstBusqEstadosGuias;
    }

    public EstadoGuia getBusqEstadoGuiaSelected() {
        return busqEstadoGuiaSelected;
    }

    public void setBusqEstadoGuiaSelected(EstadoGuia busqEstadoGuiaSelected) {
        this.busqEstadoGuiaSelected = busqEstadoGuiaSelected;
    }

    public String getBusqCodGuia() {
        return busqCodGuia;
    }

    public void setBusqCodGuia(String busqCodGuia) {
        this.busqCodGuia = busqCodGuia;
    }

    public Long getBusqCuitTit() {
        return busqCuitTit;
    }

    public void setBusqCuitTit(Long busqCuitTit) {
        this.busqCuitTit = busqCuitTit;
    }

    public Long getBusqCuitDest() {
        return busqCuitDest;
    }

    public void setBusqCuitDest(Long busqCuitDest) {
        this.busqCuitDest = busqCuitDest;
    }

    public String getBusqNumFuente() {
        return busqNumFuente;
    }

    public void setBusqNumFuente(String busqNumFuente) {   
        this.busqNumFuente = busqNumFuente;
    }

    public boolean isVencAtualizado() {
        return vencAtualizado;
    }

    public void setVencAtualizado(boolean vencAtualizado) {   
        this.vencAtualizado = vencAtualizado;
    }

    public List<GuiaSinPago> getGuiasSinPago() {
        if(guiasSinPago == null){
            verGuiasAdeudadas();
        }
        return guiasSinPago;
    }

    public void setGuiasSinPago(List<GuiaSinPago> guiasSinPago) {    
        this.guiasSinPago = guiasSinPago;
    }

    public Domicilio getDomDestino() {
        return domDestino;
    }
   
    public void setDomDestino(Domicilio domDestino) {
        this.domDestino = domDestino;
    }

    public List<EntidadServicio> getLstGuiasEmisorasFormProv() {
        return lstGuiasEmisorasFormProv;
    }

    public void setLstGuiasEmisorasFormProv(List<EntidadServicio> lstGuiasEmisorasFormProv) {
        this.lstGuiasEmisorasFormProv = lstGuiasEmisorasFormProv;
    }

    public EntidadServicio getGuiaEmisoraSelected() {
        return guiaEmisoraSelected;
    }
   
    public void setGuiaEmisoraSelected(EntidadServicio guiaEmisoraSelected) {
        this.guiaEmisoraSelected = guiaEmisoraSelected;
    }

    public FormProv getFormProv() {
        return formProv;
    }

    public void setFormProv(FormProv formProv) {
        this.formProv = formProv;
    }

    public Formulario getForm() {
        return form;
    }
   
    public void setForm(Formulario form) {
        this.form = form;
    }

    public List<Delegacion> getLstDelegaciones() {
        return lstDelegaciones;
    }

    public void setLstDelegaciones(List<Delegacion> lstDelegaciones) {
        this.lstDelegaciones = lstDelegaciones;
    }

    public Delegacion getDelegSelected() {
        return delegSelected;
    }

    public void setDelegSelected(Delegacion delegSelected) {    
        this.delegSelected = delegSelected;
    }

    public String getMsgImpFormVencida() {
        return msgImpFormVencida;
    }

    public void setMsgImpFormVencida(String msgImpFormVencida) {
        this.msgImpFormVencida = msgImpFormVencida;
    }

    public String getMsgImpFormVigente() {
        return msgImpFormVigente;
    }

    public void setMsgImpFormVigente(String msgImpFormVigente) {    
        this.msgImpFormVigente = msgImpFormVigente;
    }

    public boolean isGuiaVencida() {
        return guiaVencida;
    }
 
    public void setGuiaVencida(boolean guiaVencida) {
        this.guiaVencida = guiaVencida;
    }

    public String getMsgErrorCancelGuia() {
        return msgErrorCancelGuia;
    }
    
    public void setMsgErrorCancelGuia(String msgErrorCancelGuia) {    
        this.msgErrorCancelGuia = msgErrorCancelGuia;
    }

    public String getMsgCancelVencida() {
        return msgCancelVencida;
    }

    public void setMsgCancelVencida(String msgCancelVencida) {
        this.msgCancelVencida = msgCancelVencida;
    }

    public String getMsgCancelVigente() {
        return msgCancelVigente;
    }

    public void setMsgCancelVigente(String msgCancelVigente) {
        this.msgCancelVigente = msgCancelVigente;
    }

    public boolean isViewFuente() {
        return viewFuente;
    }

    public void setViewFuente(boolean viewFuente) {
        this.viewFuente = viewFuente;
    }

    public boolean isViewGuia() {
        return viewGuia;
    }

    public void setViewGuia(boolean viewGuia) {
        this.viewGuia = viewGuia;
    }

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
     * Método para completar el listado con los ítems autorizados de la guía.
     * Si descuenta de una autorización, traigo los ítems autorizados por resolución.
     * Si descuenta de otras guías, las obtengo y, por cada una, levanto los ítems respectivos
     * @return List<ItemProductivo> listado de items a descontar productos.
     */
    public List<ItemProductivo> getLstItemsAutorizados() {
        if(guia.getTipo().isDescuentaAutoriz()){
            // si descuenta de una autorización
            lstItemsAutorizados = itemFacade.getByAutorizacion(autFacade.getExistente(guia.getNumFuente()));
        }else{
            // si descuenta de guías, recorro el listado y por cada una voy agregando sus ítems
            if(lstItemsAutorizados == null){
                lstItemsAutorizados = new ArrayList<>();
            }else{
                lstItemsAutorizados.clear();
            }
            // recargo los items de las guías seleccionadas
            for(Guia g : guia.getGuiasfuentes()){
                for (ItemProductivo item : itemFacade.getByGuia(g)){
                    lstItemsAutorizados.add(item);
                }
            }
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

    public Guia getGuiaAsignada() {
        return guiaAsignada;
    }

    public void setGuiaAsignada(Guia guiaAsignada) {
        this.guiaAsignada = guiaAsignada;
    }

    public List<ItemProductivo> getLstItemsADescontar() {
        return lstItemsADescontar;
    }

    public void setLstItemsADescontar(List<ItemProductivo> lstItemsADescontar) {
        this.lstItemsADescontar = lstItemsADescontar;
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
        optBusqSelected = null;
        busqSinResultados = false;
        limpiarFormBusqGuias();
    }    
    
    ////////////////////////
    // Métodos operativos //
    ////////////////////////  
    
    /**
     * Método para listar las guías según los parámetros de búsqueda
     * seteados en en getLstBusqTipoGuias()
     * Si no hubo resultados setea el flag correspondiente para mostrar un mensaje al usuario.
     * Al final limpia el formulario de búsqueda.
     */
    public void poblarListado(){
        // reseteo el resultado
        busqSinResultados = false;
        // busco y pueblo el listado luego de validar
        switch (optBusqSelected.getId().intValue()) {
            case 1:
                // Si busca por tipo de guía
                listado = guiaFacade.getByTipo(busqTipoGuiaSelected);
                break;
            case 2:
                // Si busca por número de guía
                listado = new ArrayList<>();
                listado.add(guiaFacade.getExistente(busqCodGuia));
                break;
            case 3:
                // Si busca por el cuit del titular
                listado = guiaFacade.getByOrigen(busqCuitTit);
                break;
            case 4:
                // Si busca por el cuit del destinatario
                listado = guiaFacade.getByDestino(busqCuitDest);
                break;
            case 5:
                // Si busca por el número de la Autorización fuente
                listado = guiaFacade.getByNumFuente(busqNumFuente);
                break;
            case 6:
                // Si busca por estado de guía
                listado = guiaFacade.getByEstado(busqEstadoGuiaSelected);
                break;
        }
        // verifico los resultados
        if(listado.isEmpty()) busqSinResultados = true;
        // limpio el formulario
        limpiarFormBusqGuias();
    }
    
    /**
     * Método que carga la vista que se mostrará en el iframe complementario.
     * Según la vista solicitada se instanciarán los objetos a gestionar.
     * Las vistas con tratamiento específico podrán ser:
     * tasas.xhtml, destino.xhtml, transporte.xhtml, emision.xhtml, cancelar.xhtml o formularios.xhtml
     * Para tasas.xhtml carga el listado de tasas para el tipo de guía, por cada item toma el monto de cada tasa a pagar por la guía
     * y genera el detalle que lo va agregando al listado.
     * Para destino.xhtml verifica vencimiento.
     * Para transporte.xhtml verifica si va a modificar uno existente o lo va a vincular por primera vez.
     * Para emision.xhtml instancia el listado de guías registradas como fuentes de productos para poblar el combo de guías para consignar
     * formularios provisorios, si así está configurada la aplicación.
     * Para cancelar.xhtml y formularios.xhtml valida vigencia y setea el mensaje que corresponda.
     * Por defecto será "general.xhtml"
     * @param strPage String vista a cargar recibida como parámetro
     */
    public void cargarFrame(String strPage){
        obrajero = null;
        rodalSelected = null;
        // renuevo el objeto guía
        Guia guiaAct = guiaFacade.find(guia.getId());
        // para la vista de Tasas preparo la liquidación
        switch (strPage) {
            case "tasas.xhtml":
                // cargo el listado de las tasas configuradas para el tipo de Guía
                lstNombresTasas = new ArrayList<>();
                liquidarTasas(guiaAct);
                break;
            case "destino.xhtml":
                cuitProcesado = false;
                // verifico si la guía está en estado emitida
                if(guiaAct.getEstado().isHabilitaFuenteProductos()){
                    // si está emitida verifico si está vencida
                    Date hoy = new Date(System.currentTimeMillis());
                    if(!hoy.before(guiaAct.getFechaVencimiento())){
                        guiaVencida = true;
                    }
                }

                if(guiaAct.getDestino() != null){
                    // si estaba editando, bajo los flags
                    if(editCuit){
                        editCuit = false;
                    }
                    entDestino = guiaAct.getDestino();
                }   
                break;
            case "transporte.xhtml":
                transporte = new Transporte();
                editTransporte = false;
                if(guiaAct.getTransporte() != null){
                    vehiculo = new Vehiculo();
                }else{
                    vehiculo = null;
                }   if(buscarVehNuevo){
                    buscarVehNuevo = false;
                }   break;
            case "emision.xhtml":
                guia = guiaAct;
                // si emite sin acreditar pagos seteo un código de recibo temporal
                if(emiteSinPago){
                    guia.setCodRecibo("TEMP");
                    emiteSinPago = false;
                }
                formProv = new FormProv();
                // instancio el listado de guías disponibles para asingar formularios provisorios
                lstGuiasEmisorasFormProv = new ArrayList<>();
                for (Guia g : guia.getGuiasfuentes()){
                    EntidadServicio ent = new EntidadServicio();
                    ent.setId(g.getId());
                    ent.setNombre(g.getCodigo());
                    lstGuiasEmisorasFormProv.add(ent);
                }
                break;
            case "cancelar.xhtml":
                guia = guiaAct;
                msgCancelVencida = null;
                msgCancelVigente = null;
                validarVigencia(1);
                break;
            case "extender.xhtml":
                vencAtualizado = false;
                break;
            case "formularios.xhtml":
                guia = guiaAct;
                msgImpFormVencida = null;
                msgImpFormVigente = null;
                validarVigencia(2);
                lstDelegaciones = delegFacade.getHabilitadas();
                form = new Formulario();
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
     *** Si estoy editando una guía limpio todo lo establecido previamente como fuente de produtos.
     * También setea el Prductor (Persona) según el CUIT obtenido.
     */
    public void buscarFuentesProductos(){
//        // si estoy editando limpio todo
//        if(guia.getId() != null && viewGuia){
//            autSelected = null;
//            guiaSelected = null;
//            guiaAsignada = null;
//            viewFuente = false;
//            guia.setNumFuente(null);
//            if(!guia.getGuiasfuentes().isEmpty()){
//                guia.getGuiasfuentes().clear();
//            }
//            lstGuiasMadre = null;
//            lstItemsOrigen = null;
//            lstItemsADescontar = null;
//        }
        // obtengo el rol de Proponente
        TipoParam tipoParamRol = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        Parametrica rolProp = paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Proponente"), tipoParamRol);
        // obtengo el Producor
        productor = perFacade.findByCuitRol(cuitBuscar, rolProp);
        if(productor != null){
            // busco Autorizaciones que tengan al productor como Proponente
            lstAutorizaciones = autFacade.getFuenteByProponente(productor);
        }else{
            JsfUtil.addErrorMessage("No se encontró un Productor registrado con el CUIT ingresado.");
        }
    }
    
    /**
     * Metodo que busca las guías disponibles para descontarles productos pertencencientes al titular
     * de la que se está gestionando y a la fuente seleccionada.
     */
    public void buscarGuiasDisponibles(){
        // busco Guías que tengan al Productor como titular u origen y a la Autorización como fuente
        lstGuiasMadre = guiaFacade.getDispByTitularYFuente(productor.getCuit(), guia.getNumFuente());
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
     * Método para inicializar la edición de los datos generales de la Guía.
     * Seteo todos los datos para su gestión: fuente, guías de descuento, etc.
     * Si tomó productos de guías de movimiento interno, tomo como cuit del productor el del origen de las guías fuente, 
     * dado que el origen de la guía puede ser un destinatario.
     * Si descuenta de una autorización, solo seteo su detalle, 
     * si descuenta de guías, seteo las guías, madre de la autorización para ese titular,
     * seteo de ellas las que fueron seleccionadas y agrego sus respectivos ítems al listado
     * de los disponibles par adescontar. Pongo como guía asignada a la primera de las guías fuentes.
     * Finalmente, redirecciono al formlario de edición
     */
    public void prepareEdit(){
        boolean cuitOrigenFuentes = false;
        edit = true;
        // cargo el listado de Tipos de Guía
        resetearCampos();
        // verifico si la guía tomó productos de una de movimiento interno con la primera del listado es suficiente
        if(!guia.getGuiasfuentes().isEmpty()){
            if(guia.getGuiasfuentes().get(0).getTipo().isMovInterno()){
                // si tomó productos de una guía de movimiento interno, tomo el cuit del origen de dicha guía para buscar las fuentes disponible
                cuitBuscar = guia.getGuiasfuentes().get(0).getOrigen().getCuit();
                cuitOrigenFuentes = true;
            }else{
                // si no, sigo normalmente
                cuitBuscar = guia.getOrigen().getCuit();
            }
        }else{
            // es una guía de extracción
            cuitBuscar = guia.getOrigen().getCuit();
        }
        // seteo la fuente de productos
        buscarFuentesProductos();
        // en cualquier caso, seteo el flag de la autorización seleccionada
        for (Autorizacion aut : lstAutorizaciones){
            if(aut.getNumero().equals(guia.getNumFuente())){
                aut.setAsignadaDesc(true);
            }
        }   
        // si la guia descuenta productos de una Autorización, cargo el listado correspondiente, seteo la Autorización seleccionda y armo su detalle
        if(guia.getTipo().isDescuentaAutoriz()){
            autSelected = autFacade.getExistente(guia.getNumFuente());
            verDetalleFuente();
            page = "general.xhtml";
        }else{
            // si la guía descuenta de otras guías seteo el listado de guías madre vinculadas, 
            // además de setear el detalle de la fuente, seteo lo correspondiente a las guías de descuento.
            verDetalleFuente();
            // obtengo las guías madre según haya tomado productos de una de movimiento interno
            if(cuitOrigenFuentes){
                // si tomó de guías de movimiento interno, tomo el cuit del origen de las guías fuente
                lstGuiasMadre = guiaFacade.getDispByTitularYFuente(guia.getGuiasfuentes().get(0).getOrigen().getCuit(), guia.getNumFuente());
            }else{
                // si tomó de guías sin movimiento interno, tomo el cuit del origen de la guía gestionada.
                lstGuiasMadre = guiaFacade.getDispByTitularYFuente(guia.getOrigen().getCuit(), guia.getNumFuente());
            }
            
            // seteo su contenido con el flag de seleccionadas y seteo el listado de items a descontar
            lstItemsADescontar = new ArrayList<>();
            // verifico si obtuve guías madre para descontar
            if(!lstGuiasMadre.isEmpty()){
                for (Guia g : lstGuiasMadre){
                    // si la guía fue seteada como a descontar, actualizo el flag correspondiente 
                    // y agrego sus ítems al listado lstItemsADescontar
                    for (Guia gDesc : guia.getGuiasfuentes()){
                        if(gDesc.equals(g)){
                            g.setAsignadaDesc(true);
                            for (ItemProductivo item : g.getItems()){
                                lstItemsADescontar.add(item);
                            }
                        }
                    }
                }
                // seteo a la primera guía a descontar como guía asignada
                guiaAsignada = lstGuiasMadre.get(0);
                viewGuia = true;
                page = "general.xhtml";
            }else{
                // las guías disponibles de la autorización seleccionada como fuente están vencidas.
                JsfUtil.addErrorMessage("No se puede editar esta guía, es probable que la fuente de productos se encuentre vencida.");
            }
        }
    }    
    
    /**
     * Método para redireccionar a la vista detalle de la Guía.
     * Actualizo la guía para deshacer los cambios que hubiera podido hacer
     */
    public void prepareView(){
        guia = guiaFacade.find(guia.getId());
        limpiarForm();
        setearInmueblesOrigen();
        page = "generalView.xhtml";
    }    
    
    /**
     * Método para limpiar el campo de CUIT a buscar
     */
    public void limpiarCuit(){
        viewFuente = false;
        viewGuia = false;
        autSelected = null;
        guiaSelected = null;
        guiaAsignada = null;
        lstItemsADescontar = null;
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
        viewFuente = false;
        viewGuia = false;
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
     * Método para mostrar el detalle de la Autorización seleccionada como fuente de productos.
     * Solo guardo los ítems si la guía descuenta de una Autorización
     */
    public void verDetalleFuente(){
        lstItemsOrigen = itemFacade.getByAutorizacion(autSelected);
        viewFuente = true;
    }
    
    /**
     * Método para mostrar el detalle de la Guía seleccionada para el descuento de productos.
     */
    public void verDetalleGuiaADesc(){
        viewGuia = true;
    }    
    
    /**
     * Método para cancelar la Fuente seleccionada (Autorización) como origen de productos
     * Resteteo el flag de asignación y los listados y objetos seleccionados.
     */
    public void cancelarFuenteSelected(){
        // reseteo el flag de asignación de la guía
        for (Autorizacion aut : lstAutorizaciones){
            if(Objects.equals(aut.getId(), autSelected.getId())){
                aut.setAsignadaDesc(false);
            }
        }        
        autSelected = null;
        lstGuiasMadre = null;
        guiaSelected = null;
        lstItemsADescontar = null;
        viewFuente = false;
        guia.getRodales().clear();
    }
    
    /**
     * Método para cancelar la Guía seleccionada para su asiganación para el descuento de productos.
     * Si ya hay ítems seleccionados, se quitan del listado de items seleccionados agregados en verDetalleGuiaADesc(), 
     * los que correspondan a la guía.
     */
    public void cancelarGuiaSelected(){
        // si ya hay items seleccionados los migro a un listado temporal
        if(lstItemsADescontar != null){
            List<ItemProductivo> lstItADesTemp = new ArrayList<>();
            lstItADesTemp.addAll(lstItemsADescontar);
            lstItemsADescontar.clear();
            // levanto los items de la guía a desasociar
            List<ItemProductivo> lstItGuia = itemFacade.getByGuia(guiaSelected);
            // recorro el listado temporal y valido cada item. Solo agrego al listado aquellos items que no forman parte de la guía a desasociar
            for(ItemProductivo ipTemp : lstItADesTemp){
                boolean aAgregar = true;
                for(ItemProductivo iGuia : lstItGuia){
                    if(Objects.equals(iGuia.getId(), ipTemp.getId())){
                        aAgregar = false;
                    }
                }
                if(aAgregar){
                    lstItemsADescontar.add(ipTemp);
                }
            }   
        }
        viewGuia = false;
        guiaSelected = null;
    }
    
    /**
     * Método para resetear la Fuente de productos.
     * Si hay guías seleccionadas para descontar, las quito
     * y elimino la que pudiera estar para asignar.
     */
    public void deleteFuenteGuardada(){
        // reseteo el flag de asignación de la guía
        for (Autorizacion aut : lstAutorizaciones){
            if(Objects.equals(aut.getId(), autSelected.getId())){
                aut.setAsignadaDesc(false);
            }
        }    
        autSelected = null;
        guiaSelected = null;
        guiaAsignada = null;
        viewFuente = false;
        guia.setNumFuente(null);
        if(!guia.getGuiasfuentes().isEmpty()){
            guia.getGuiasfuentes().clear();
        }
        lstGuiasMadre = null;
        lstItemsOrigen = null;
        lstItemsADescontar = null;
        guia.getRodales().clear();
    }
    
    /**
     * Método para quitar la guía seleccionada para descontar.
     * También se quitan los items respectivos del listado mostrado al usuario
     * mediante el método cancelarGuiaSelected()
     */
    public void deleteGuiaSeleccionada(){
        // migro el listado a uno temporal
        List<Guia> lstGf = new ArrayList<>();
        lstGf.addAll(guia.getGuiasfuentes());
        guia.getGuiasfuentes().clear();
        // recorro el listado y comparo con la guia a quitar, solo agrego las restantes 
        for (Guia g : lstGf){
            boolean aAgregar = true;
            if(Objects.equals(g.getId(), guiaSelected.getId())){
                aAgregar = false;
            }
            if(aAgregar){
                guia.getGuiasfuentes().add(g);
            }
        }
        // reseteo el flag de asignación de la guía
        for (Guia g : lstGuiasMadre){
            if(Objects.equals(g.getId(), guiaSelected.getId())){
                g.setAsignadaDesc(false);
            }
        }        
        // quito los items
        cancelarGuiaSelected();     
        // reseteo la guía asignada
        guiaAsignada = null;
        
        JsfUtil.addSuccessMessage("La guía a descontar productos ha sido desagregada.");
    }
    
    /**
     * Método para listar los rodales disponibles de la Autorización para ser asignados a la Guía. 
     * Se actualiza el flag "asignado" para los rodales según esté o no asignado a la Guía.
     * Solo para los casos en los que el CGL esté configurado para trabajar con rodales de inmuebles.
     */
    public void prepareViewRodalesDisponibles(){
        // seteo los rodales disponibles
        rodalesDisponibles = new ArrayList<>();
        rodalesDisponibles.addAll(autSelected.getRodales());
        // si algún rodal ya está asignado a la Guía seteo el flag correspondiente
        for(Rodal rodDisp : rodalesDisponibles){
            if(guia.getRodales().contains(rodDisp)){
                rodDisp.setAsignado(true);
            }else{
                rodDisp.setAsignado(false);
            }
        }
    }
    
    /**
     * Método para listar los rodales asignados a una guía.
     * En el caso de una Guía que descuente de una Autorización, no hace nada.
     * En el caso que descuente de otra guía, setea los rodales de forma temporal 
     * en la guía que se está viendo, dado que el paso de los rodales de una a la otra es automático
     */
    public void prepareViewRodalesAsignados(){
        if(!guia.getTipo().isDescuentaAutoriz()){
            guia.getRodales().clear();
            guia.getRodales().addAll(guia.getGuiasfuentes().get(0).getRodales());
        }
    }
    
    /**
     * Método para asignar un rodal de una Autorización a una Guía.
     */
    public void asignarRodal(){
        rodalSelected.setAsignado(true);
        guia.getRodales().add(rodalSelected);
    }
    
    /**
     * Método para desvincular el rodal de una Autorización a una Guía
     */
    public void desvincularRodal(){
        int i = 0, iRod = 0;
        if(guia.getRodales().size() > 1){
            for(Rodal r : guia.getRodales()){
                if(r.equals(rodalSelected)){
                    iRod = i;
                }
                i += 1;
            }
            guia.getRodales().remove(iRod);
            rodalSelected.setAsignado(false);
        }else{
            JsfUtil.addErrorMessage("La Guía debe tener al menos un rodal vinculado.");
        }
    }    
    
    /**
     * Método para guardar la Fuente de productos para la Guía.
     * Se define como fuente de productos al instrumento que autoriza su extracción
     * de uno o más inmuebles especificados.
     * Si el origen de los productos es una autorización, se setea a este tipo como tipoFuente,
     * si, en cambio es una o más guías madres, se setea al tipo como guía madre.
     */
    public void guardarFuente(){
        boolean valida = true;
        // seteo la validación según gestione o no rodales
        if(ResourceBundle.getBundle("/Config").getString("GestionaRodales").equals("si") && guia.getTipo().isDescuentaAutoriz()){
            if(!autSelected.getRodales().isEmpty() && guia.getRodales().isEmpty()){
                valida = false;
            }
        }
        // si valida seteo el número de fuente de los productos
        if(valida){
            guia.setNumFuente(autSelected.getNumero());
            // seteo el flag de seleccionada a la autorización correspondiente
            for (Autorizacion aut : lstAutorizaciones){
                if(Objects.equals(aut.getId(), autSelected.getId())){
                    aut.setAsignadaDesc(true);
                }
            }  
            // seteo el tipo de paramétrica para obtener el tipo de fuente de productos (origen del descuento)
            TipoParam tipoParamFuente = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoFuente"));
            if(guia.getTipo().isDescuentaAutoriz()){
                // seteo la autorización como el tipo de origen de productos
                guia.setTipoFuente(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("Autorizacion"), tipoParamFuente));
                JsfUtil.addSuccessMessage("Se ha registrado la Fuente de Productos, puede guardar los Datos Generales de la Guía");
            }else{
                // si no descuenta de Autorización, cargo las guías disponibles
                buscarGuiasDisponibles();
                // seteo la guía como el tipo de origen de productos
                guia.setTipoFuente(paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaMadre"), tipoParamFuente));
                JsfUtil.addSuccessMessage("Se ha registrado la Fuente de Productos, puede continuar seleccionado la/s guía/s para descontar");
            }
        }else{
            JsfUtil.addErrorMessage("La Autorización seleccionada tiene rodales asignados, debe vincular a la Guía al menos uno.");
        }
    }
    
    /**
     * Método para guardar la/s guía/s madre de las que la guía descontará los productos.
     * Se agrega la guía al listado de guías fuentes de la guía gestionada y los items como seleccionados para descontar
     * La guía se desasignará mediante deleteGuiaSeleccionada()
     */
    public void guardarGuiaADesc(){
        boolean valida = true;
        // verifico si la guía ya tiene una fuente seleccionada, para que la actual tenga el mismo destino de productos de la anterior
        if(!guia.getGuiasfuentes().isEmpty()){
            if(guia.getGuiasfuentes().get(0).isDestinoExterno() != guiaSelected.isDestinoExterno()){
                valida = false;
            }
        }
        // solo continúo si valida
        if(valida){
            // guardo los items
            if(lstItemsADescontar == null){
                lstItemsADescontar = itemFacade.getByGuia(guiaSelected);
            }else{
                List<ItemProductivo> lstIt = itemFacade.getByGuia(guiaSelected);
                for(ItemProductivo ip : lstIt){
                    lstItemsADescontar.add(ip);
                }
            }
            // asigno la guía
            guia.getGuiasfuentes().add(guiaSelected);
            // si discrimina tasas por el destino de los productos, seteo la condición de externo o interno de los productos
            if(ResourceBundle.getBundle("/Config").getString("DiscTasaDestExt").equals("si")){
                guia.setDestinoExterno(guiaSelected.isDestinoExterno());
            }
            guiaAsignada = guiaSelected;
            // seteo el flag de la guía seleccionada
            for (Guia g : lstGuiasMadre){
                if(Objects.equals(g.getId(), guiaAsignada.getId())){
                    g.setAsignadaDesc(true);
                }
            }
            guiaSelected = null;
            JsfUtil.addSuccessMessage("Se ha agregado la guía para descontar sus productos. Podrá agregar más o guardar los datos generales de la guía que está gestionando.");
        }else{
            JsfUtil.addErrorMessage("El destino de los productos de la guía seleccionada no coincide con el de guías fuente existentes.");
        }
    }
    
    /**
     * Método para persistir una Guía con los datos de origen.
     * Crea o seleccina la entidad Origen y asigna el inmueble de origen
     * Sea inserción o edición
     */
    public void save(){
        Date fechaAlta = new Date(System.currentTimeMillis());
        // obtengo la EntidadGuia (Origen)
        entOrigen = obtenerEntidadOrigen(productor, guia.getTipoFuente());
        if(!edit){
            // seteo la fecha de alta
            guia.setFechaAlta(fechaAlta);
        }
        try{
            guia.setUsuario(usLogueado);
            // si no existe la EntidadGuia origen, la creo
            if(entOrigen.getId() == null){
                entOrigen.setUsuario(usLogueado);
                entOrigen.setFechaAlta(fechaAlta);
                entOrigen.setHabilitado(true);
                entGuiaFacade.create(entOrigen);
            }
            // seteo o actualizo la EntidadGuia origen
            if(entOrigen != null){
                guia.setOrigen(entOrigen);
            }
            // edito o inserto, según sea el caso
            if(!edit){
                // Estado (carga inicial)
                EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaInicial"));
                if(estado != null){
                    // solo inserto si hay un estado configurado
                    guia.setEstado(estado);
                    // Seteo el código. En principio será un autonumérico
                    guia.setCodigo(setCodigoGuia());
                    // Creo la Guía
                    guiaFacade.create(guia);
                    JsfUtil.addSuccessMessage("La Guía se creo correctamente, puede continuar con los pasos siguientes para su emisión.");
                }else{
                    JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Estado de Guía: 'GuiaInicial'.");
                }
            }else{
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
     * Método para asignar un domicilio al destino de la guía.
     * Ya se seleccionó un destinatario que puede tener varios domicilios.
     * Si hay un condicionamiento para el destino de los productos, lo valido.
     * Con el domicilio asignado se obtiene la entidad destino
     */
    public void addDomDestino(){
        boolean valida = true;
        // si la guía descontó productos de guías de extracción valido la condición del destino de los productos
        if(!guia.getGuiasfuentes().isEmpty() && ResourceBundle.getBundle("/Config").getString("DiscTasaDestExt").equals("si")){
            valida = validarDomDestino();
        }
        if(valida){
            entDestino = obtenerEntidadDestino(destinatario, domDestino);
        }
        
    }
    
    /**
     * Método para quitar el domicilio seleccionado y dar nuevamente la opción de selecconar otro al usuario.
     * Solo pone la entidad destino en null.
     */
    public void deleteDom(){
        entDestino = null;
    }
    
    /**
     * Método para buscar la/s fuente/s de productos que correspondan según el tipo de Guía.
     * También setea el Prductor (Persona) según el CUIT obtenido.
     */
    public void buscarDestinatario(){
        Parametrica rolPersona = obtenerParametro(ResourceBundle.getBundle("/Config").getString("RolPersonas"), ResourceBundle.getBundle("/Config").getString("Destinatario"));
        destinatario = perFacade.findByCuitRol(cuitBuscar, rolPersona);
        if(destinatario != null){
            cuitProcesado = true;
        }else{
            JsfUtil.addErrorMessage("No hay ningún Destinatario registrado con el CUIT ingresado.");
        }
    }
    
    /**
     * Método para buscar un obrajero para asignarlo a la guía
     * También setea el Obrajero (Persona) según el CUIT obtenido.
     */
    public void buscarObrajero(){
        Parametrica rolPersona = obtenerParametro(ResourceBundle.getBundle("/Config").getString("RolPersonas"), ResourceBundle.getBundle("/Config").getString("Obrajero"));
        obrajero = perFacade.findByCuitRol(cuitObraj, rolPersona);
        if(obrajero != null){
            cuitProcesado = true;
        }else{
            JsfUtil.addErrorMessage("No hay ningún Obrajero registrado con el CUIT ingresado.");
        }
    }
    
    /**
     * Método para limpiar el cuit seleccionado para buscar el destinatario
     */
    public void limpiarCuitDest(){
        viewFuente = false;
        viewGuia = false;
        cuitBuscar = null;
        destinatario = null;
        entDestino = null;
    }
    
    /**
     * Método para limpiar el cuit seleccionado para buscar el obrajero
     */
    public void limpiarCuitObraj(){
        viewFuente = false;
        viewGuia = false;
        cuitObraj = null;
        obrajero = null;
    }
    
    /**
     * Método para agregar un obrajero a la guía. Aunque en principio solo se permitirá un obrajero por guía,
     * se lo trata como si pudiera incluir más.
     * Valida que el obrajero no esté ya vinculado a la Guía.
     * Si no está vinculado, lo agrega al listado de la guía y actualiza la guía.
     */
    public void addObrajero(){
        boolean valida = true;
        
        // valido que el obrajero no esté vinculado ya a la guía
        for(Persona obrj : guia.getObrajeros()){
            if(Objects.equals(obrj.getCuit(), obrajero.getCuit())){
                valida = false;
                JsfUtil.addErrorMessage("El " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " que está tratando de asociar, ya está vinculado a la Guía.");
            }
        }
        try{
            if(valida){
                // agrego el obrajero al listado
                guia.getObrajeros().add(obrajero);
                // guardo temporalmente el listado de obrajeros
                List<Persona> obrjTemp = new ArrayList<>();
                for(Persona per : guia.getObrajeros()){
                    obrjTemp.add(per);
                }
                // limpio el listado y actualizo
                guia.setObrajeros(null);
                guiaFacade.edit(guia);
                // seteo y guardo los obrajeros definitivos y actualizo
                guia.setObrajeros(obrjTemp);
                guiaFacade.edit(guia);
                limpiarViewObrajero();
                JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " se agregó a la Guía");
            }
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error asignando al " + ResourceBundle.getBundle("/Config").getString("Obrajero") + ". " + ex.getMessage());
        }
    }
    
    /**
     * Método para desvincular un obrajero de una Guía.
     * Quita el obrajero a eliminar del listado de la guía y
     * la actualiza.
     */
    public void deleteObrajero(){
        int i = 0, j = 0;
        try{
            for(Persona obrj : guia.getObrajeros()){
                if(Objects.equals(obrj.getCuit(), obrajero.getCuit())){
                    j = i;
                }
                i = i+= 1;
            }
            guia.getObrajeros().remove(j);
            guiaFacade.edit(guia);
            viewObrajero = false;
            limpiarViewObrajero();
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error desvinculando al " + ResourceBundle.getBundle("/Config").getString("Obrajero") + ". " + ex.getMessage());
        }
    }
 
    
    /**
     * Método para habilitar la vista detalle del obrajero
     */
    public void prepareViewObrajero(){
        viewObrajero = true;
    }
    
    /**
     * Método para limpiar la vista detalle del Obrajero
     */
    public void limpiarViewObrajero(){
        obrajero = null;
        cuitObraj = null;
    }
    
    /**
     * Método para restaurar el destino de la Guía
     */
    public void restaurarDestino(){
        destinatario = null;
        entDestino = guia.getDestino();
       	editCuit = false;
        cuitProcesado = false;
    }
    
    /**
     * Método para persistir el Destino de la Guía, en caso que lo requiera,
     * dado que no todos los movimientos implican un destinatario distinto al titular.
     * Registra un destino por primera vez y permite actualizar uno seleccionado previamente.
     * En los casos en que la guía ya esté emitida, la modificación se completa
     * con la actualización del destino de la guía en el componente de control y verificación, 
     * la notificación al nuevo destinatario para que reciba la guía mediante el componente de trazabilidad,
     * y la notificación al destinatario desafectado.
     */
    public void saveDestino(){
        String msgExito = "", msgError = "";
        boolean continuar = false;
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
            // chequeo los estados solo si no está emitida
            if(!guia.getEstado().getNombre().equals(ResourceBundle.getBundle("/Config").getString("GuiaEmitida"))){
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
            }
            // agrego el usuario
            guia.setUsuario(usLogueado);
            // si la Guía ya tenía un Destino asignado, verifico si está emitida
            if(editCuit){
                if(guia.getEstado().isHabilitaFuenteProductos()){
                    // si está emitida (habilita descuento) coninúo si realmente hubo cambio de destino
                    if(!Objects.equals(entDestino.getId(), guia.getDestino().getId())){
                        // guardo el correo del destinatario anterior
                        String mailAnterior = guia.getDestino().getEmail();
                        // seteo el nuevo destino
                        guia.setDestino(entDestino);
                        // actualizo la guía
                        guiaFacade.edit(guia);
                        
                        // actualizo el nuevo destino en el el CCV. Obtengo el token si no está seteado o está vencido
                        if(tokenCtrl == null){
                            getTokenCtrl();
                        }else try {
                            if(!tokenCtrl.isVigente()){
                                getTokenCtrl();
                            }
                        } catch (IOException ex) {
                            msgError = msgError + "Hubo un error accediendo al componente de Control y Verificación. ";
                            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token de CCV", ex.getMessage()});
                        }
                        // busco la Guía en el CCV
                        guiaCtrlClient = new GuiaCtrlClient();
                        List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
                        GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
                        Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guia.getCodigo(), null, null, tokenCtrl.getStrToken());
                        lstGuias = response.readEntity(gTypeG);
                        // obtengo el id de la guía en el ccv
                        Long idGuiaCtrl = lstGuias.get(0).getId(); 
                        // instancio la guía ccv
                        ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                        // seteo la Guía solo con los valores que necesito para editarla (el id y los datos del destino)
                        guiaCtrol.setEstado(lstGuias.get(0).getEstado());
                        guiaCtrol.setId(idGuiaCtrl);
                        guiaCtrol.setNombreDestino(guia.getDestino().getNombreCompleto());
                        guiaCtrol.setCuitDestino(guia.getDestino().getCuit());
                        guiaCtrol.setLocDestino(guia.getDestino().getLocalidad());
                        guiaCtrol.setFechaVencimiento(guia.getFechaVencimiento());

                        // chequeo el vencimiento del token
                        if(tokenCtrl == null){
                            getTokenCtrl();
                        }else try {
                            if(!tokenCtrl.isVigente()){
                                getTokenCtrl();
                            }
                        } catch (IOException ex) {
                            msgError = msgError + "Hubo un error accediendo al componente de Control y Verificación. ";
                            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
                        }

                        response = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                        if(response.getStatus() == 200){
                            // reseteo el flag
                            continuar = true;
                            // armo el mensaje exitoso
                            msgExito = msgExito + "Se actualizó el destinatario en el componente de Control y Verificación. ";
                        }else{
                            // se cerró en CGL pero no actualizó en CCV
                            msgError = msgError + "No se pudo actualizar el destinatario en el componente de Control y Verificación, por favor, contacte al Administrador. ";
                        }
                        
                        // si todo salió bien verifico si el destinatario tiene una cuenta de usuario creada en TRAZ
                        if(continuar){
                            // obtengo el token si no está seteado o está vencido
                            if(tokenTraz == null){
                                getTokenTraz();
                            }else try {
                                if(!tokenTraz.isVigente()){
                                    getTokenTraz();
                                }
                            } catch (IOException ex) {
                                msgError = msgError + "Hubo un error accediendo al componente de Trazabilidad. ";
                                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token en TRAZ", ex.getMessage()});
                            }
                            // busco el usuario
                            usuarioClientTraz = new UsuarioClient();
                            GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>> gTypeUs = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>>() {};
                            Response responseCgt = usuarioClientTraz.findByQuery_JSON(Response.class, String.valueOf(guia.getDestino().getCuit()), null, tokenTraz.getStrToken());
                            List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario> listUs = responseCgt.readEntity(gTypeUs);
                            if(!listUs.isEmpty()){
                                // el cliente está registrado, notifico
                                if(notificarNuevoDestino()){
                                    // reseteo el flag
                                    continuar = true;
                                    // armo el mensaje exitoso
                                    msgExito = msgExito + "Se notificó el cambio al nuevo destinatario. ";
                                }else{
                                    // reseteo el flag
                                    continuar = false;
                                    // armo el mensaje de error
                                    msgError = msgError + "No se pudo notificar al nuevo destinatario, por favor, contacte al Administrador. ";
                                }
                            }else{
                                // si no existe la cuenta, lo creo
                                // valido el token
                                if(tokenTraz == null){
                                    getTokenTraz();
                                }else try {
                                    if(!tokenTraz.isVigente()){
                                        getTokenTraz();
                                    }
                                } catch (IOException ex) {
                                    msgError = msgError + "Hubo un error accediendo al componente de Trazabilidad. ";
                                    logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token en TRAZ", ex.getMessage()});
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
                                // con el rol obtenido registro el Usuario vía API-TRAZ
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
                                    // reseteo el flag
                                    continuar = true;
                                    if(guia.getTipo().isMovInterno()){
                                        msgExito = msgExito + "Se creó la cuenta de Usuario para el nuevo Destinatario en el Componente de Trazabilidad del Sistema, desde ahí la Guía será cerrada por el Destinatario. ";
                                    }else{
                                        msgExito = msgExito + "Se actualizó la Guía en el Componente de Control y Verificación para ser controlada y se creó la cuenta de Usuario para el nuevo Destinatario en el Componente de Trazabilidad del Sistema, desde ahí la Guía será cerrada por el Destinatario. ";
                                    }
                                }else{
                                    // reseteo el flag
                                    continuar = false;
                                    if(guia.getTipo().isMovInterno()){
                                        msgError = msgError + "No se pudo generar el Usuario en el Componente de Trazabilidad del Sistema, deberá solicitar su registro al Administrador del mismo. ";
                                    }else{
                                        msgError = msgError + "Se actualizó la Guía en el Componente de Control y Verificación para ser controlada pero no se pudo generar el Usuario en el Componente de Trazabilidad del Sistema, deberá solicitar su registro al Administrador del mismo. ";
                                    }
                                }   
                            }
                            // si todo anduvo bien envío un correo al destinatario substituído
                            if(continuar){
                                if(cancelarDestino(mailAnterior)){
                                    // reseteo el flag
                                    continuar = true;
                                    // armo el mensaje exitoso
                                    msgExito = msgExito + "Se notificó la novedad al destinatario anterior. ";
                                }else{
                                    // reseteo el flag
                                    continuar = false;
                                    msgError = msgError + "No se pudo notificar al destinatario, por favor, contacte al Administrador. ";
                                }
                            }
                        }else{
                            // solo se pudo actualizar localmente, no se pudo actualizar en CTRL
                            msgError = msgError + "Se actualizó la guía localmente ";
                        }
                    }   
                }else{
                    // si no está emitida solo agrego el destino y actualizo la guía
                    guia.setDestino(entDestino);
                    guiaFacade.edit(guia);
                    continuar = true;
                }
                // en cualquier caso reseteo todo
                editCuit = false;
                view = false;
                cuitBuscar = null;
                destinatario = null;
                cuitProcesado = false;
            }else{
                // no tenía destino asignado, seteo el nuevo destino
                guia.setDestino(entDestino);
                // actualizo la guía
                guiaFacade.edit(guia);
                // seteo el flag
                continuar = true;
                // reseteo todo
                view = false;
                cuitBuscar = null;
                destinatario = null;
                cuitProcesado = false;
            }
            if(!continuar){
                JsfUtil.addErrorMessage(msgError + "Se actualizó el destino localmente pero surgieron errores: " + msgError);
            }else{
                if(msgExito.equals("")){
                    // si la guía no está emitida
                    JsfUtil.addSuccessMessage("El destinatario se agregó existosamente.");
                }else{
                    // si la guía está emitida
                    JsfUtil.addSuccessMessage(msgExito);
                }
            }
        }catch(ClientErrorException ex){
            JsfUtil.addErrorMessage(msgError + "Hubo un error gestionando el Destino de la Guía. " + ex.getMessage());
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
                    estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaConProdAEmitir"));
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
     * Productos forestales asociados a los Items de la Guía.
     * Invoca al método privado imprimirVolante()
     */
    public void generarVolante(){
        try{
            // imprimo el pdf con el volante
            imprimirVolante();
            // seteo la fecha de emisión del volante de pago
            Date fechaEmision = new Date(System.currentTimeMillis());
            guia.setFechaEmisionVolante(fechaEmision);
            // obtengo el Estado a setear y lo actualizo
            EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaLiquidada"));
            guia.setEstado(estado);
            guiaFacade.edit(guia);
        }catch(Exception ex){
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
            guia = guiaFacade.find(guia.getId());
            JsfUtil.addSuccessMessage("El código del recibo fue agregado a la Guía. Ya puede emitirla.");
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error registrando el código del recibo. " + ex.getMessage());
        }
    }
    
    /**
     * Método para generar un volante de pago de una guía ya emitida.
     * Invoca al método imprimirVolante()
     */
    public void emitirVolanteAdeudado(){
        imprimirVolante();
    }
    
    /**
     * Método para generar un código de recibo temporal.
     * Setea el flag para que en el método cargarFrame()
     * se genere un código temporal para el recibo.
     */
    public void generarCodReciboTemp(){
        emiteSinPago = true;
        cargarFrame("emision.xhtml");
    }
    
    /**
     * Método para emitir una Guía, con o sin transporte. En el caso de que sea de transporte, se detalla el proceso:
     * Después de las validaciones se genera el pdf con tantas copias de la guía como estén configuradas, enviando el martillo
     * del inmueble y, si tiene configurado el uso de obrajeros para las guías, el martillo del obrajero también, todo mediante generarCopias().
     * Genero el reporte llamando al método imprimirGuia().
     * Posteriormente, si la Guía no es de acopio interno (reporta Transporte) se registra en el Componente de Control y Verificación (CCV)
     * Finalmente se verifica la existencia de una cuenta de usuario para el Destinatario de la Guía
     * en el Componente de Gestión de Trazabilidad. Si no es así, la genera mediante la API-TRAZ, si ya existe la cuenta 
     * solo se envía un correo al Destinatario dando aviso de la remisión de la Guía.
     * Si es una guía madre (que descuenta de una autorización) se setea el campo "formEmitidos" en 0.
     * Si no tomó de formulario se seteará en 0
     * Se llaman a los métodos privados generarCopias() e imprimirGuia() 
     */       
    public void emitir(){
        List<Guia> guias = new ArrayList<>();
        // encripto el código para generar el qr en el reporte y lo asigno a la guía
        Date fechaEmision = new Date(System.currentTimeMillis());
        String codQr = "guía:" + guia.getCodigo() + "|fuente:" + guia.getNumFuente();
        guia.setCodQr(codQr);
        // asigno la fecha de emisión
        guia.setFechaEmisionGuia(fechaEmision);
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
        // si la Guía descuenta de una autorización, seteo "formEmitidos" en 0
        if(guia.getTipo().isDescuentaAutoriz()){
            guia.setFormEmitidos(0);
        }        
        // obtengo la autorización vinculada a la Guías
        Autorizacion aut = autFacade.getExistente(guia.getNumFuente());
        // si la guía tomó productos de una u otras guías, agrupo los items
        List<ItemProductivo> itemsAgr;
        if(!guia.getTipo().isDescuentaAutoriz()){
            itemsAgr = agruparItems();
        }else{
            itemsAgr = guia.getItems();
        }
        // genero las copias
        generarCopias(aut, itemsAgr, guias);
        // imprimo las copias en pdf
        imprimirGuia(guias, aut);
        // actualizo la Guía. Si tiene un código de recibo temporal lo pongo nulo
        if(guia.getCodRecibo() != null){
            if(guia.getCodRecibo().equals("TEMP")){
                guia.setCodRecibo(null);
            }
        }
        guiaFacade.edit(guia);
        // si la Guía habilita transporte y no es de acopio interno, se impacta en el Componente de Control y Verificación (CCV),
        // y se prosigue con la verificación de cuenta de usuario en el componente de Gestión de Trazabilidad (CGT),
        // si la Guía es de acopio interno, no se persiste en el CCV y pasa directamente a la verificación de la cuenta de usuario en el CGT
        if(guia.getTipo().isHabilitaTransp() && !guia.getTipo().isMovInterno()){
            boolean persistidaCcv = false;
            List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item> lstItemCtrl = new ArrayList<>();
            ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item itemCtrl;
            for(ItemProductivo item : itemsAgr){
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
                    if(!enviarCorreoEmision()){
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
    }    
    
    /**
     * Método para asignar un formulario provisorio a una guía.
     * Valida que el código de la guía emisora y el número de formulario estén seteados
     * y valida también que la guía emisora seleccionada tenga formularios emitidos y
     * que el número ingresado no sea mayor a la cantidad de formularios emitidos.
     */
    public void asignarFormProv(){
        boolean valida = true;
        String msgError = "";
        // valido que haya seleccionado una guía
        if(guiaEmisoraSelected == null){
            valida = false;
            msgError = msgError + " Debe consignar una guía emisora del formulario provisorio.";
        }
        // valido que haya ingresado un número de formulario provisorio
        if(formProv.getNumFormuario() == 0){
            valida = false;
            msgError = msgError + " Debe consignar un número de formulario.";
        }
        // valido que la guía seleccionada tenga formularios emitidos y que el número ingresado no sea mayor
        int cantFormGuiaSelected = guiaFacade.getCantFormProv(guiaEmisoraSelected.getId());
        if(cantFormGuiaSelected == 0){
            valida = false;
            msgError = msgError + " La guía emisora seleccionada no tiene formularios emitidos.";
        }else if(cantFormGuiaSelected < formProv.getNumFormuario()){
            valida = false;
            msgError = msgError + " El número de formulario ingresado es menor a la cantidada de formularios emitidos por la guía emisora seleccionada: " + cantFormGuiaSelected;
        }
        
        // sigo si validó todo
        if(valida){
            // seteo el código de guía
            formProv.setCodGuia(guiaEmisoraSelected.getNombre());
            guia.getFormProvisorios().add(formProv);
            try{
                guiaFacade.edit(guia);
                // actualizo la guía en memoria
                guia = guiaFacade.find(guia.getId());
                // limpio el formulario
                limpiarFormEmision();
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error asignando el formulario provisorio. " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage(msgError);
        }
    }
    
    /**
     * Método para limpiar el formulario de asiganción de formularios provisorios a una guía.
     */
    public void limpiarFormEmision(){
        formProv = new FormProv();
        guiaEmisoraSelected = new EntidadServicio();
    }
    
    /**
     * Método para desasignar un formulario ya asignado. Recorre el listado de formularios y desasigna el que corresponda
     */
    public void desasignarFormProv(){
        int i = 0, iDes = 0;
        for(FormProv f : guia.getFormProvisorios()){
            if(Objects.equals(f.getId(), formProv.getId())){
                iDes = i;
            }
            i += 1;
        }
        guia.getFormProvisorios().remove(iDes);
        try{
            guiaFacade.edit(guia);
            formProv = new FormProv();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error actualzando la guía. " + ex.getMessage());
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
     * Instancia las posibles guías que toman productos de la presente.
     * Si las guías no se emiten de forma remota instanciará los inmuebles a partir de la Autorización
     */
    public void prepareViewDetalle(){
        if(guia.getTipo().isHabilitaDesc()){
            lstGuiasHijas = guiaFacade.findHijas(guia);
        }
        // Busco los inmuebles si la guía no se emite de forma remota
        if(!ResourceBundle.getBundle("/Config").getString("emiteRemota").equals("si")){
            setearInmueblesOrigen();
        }
        view = true;
    }    
    
    //////////////////////////////////////////
    // Métodos para la cancelación de Guias //
    //////////////////////////////////////////    
    
    /**
     * Método para limpiar el formulario para la cancelación de guías
     * Resetea el campo "obs" de la guía.
     */
    public void limpiarFormCancel(){
        guia.setObs("");
    }
    
    /**
     * Método para cancelar una guía vigente.
     * Se actualiza el estado localmente, se actualiza la réplica de la guía
     * en el componente de control y verificación,
     * se reintegran los productos a su orgien y se notifica la novedad al titular de la guía
     */
    public void cancelarGuia(){
        boolean actualizadaCcv = false, actualizadaLocal = false, actualizadosItems = false, 
                guiaCcvNoEncontrada = false, estadoCcvNoEncontrado = false, titularNotificado = false;
        msgErrorCancelGuia = "";
        
        try{
            //////////////////////////////////
            // actualizo la guía localmente //
            //////////////////////////////////
            EstadoGuia estado = estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaCancelada"));
            guia.setEstado(estado); 
            guiaFacade.edit(guia);
            actualizadaLocal = true;
            
            ////////////////////////////////////////////////////////////////////////
            // actualizo la guía en el componente de control y verificación (CCV) //
            ////////////////////////////////////////////////////////////////////////
            // obtengo el token si no está seteado o está vencido
            if(tokenCtrl == null){
                getTokenCtrl();
            }else try {
                if(!tokenCtrl.isVigente()){
                    getTokenCtrl();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token de CTRL", ex.getMessage()});
            }
            // busco la Guía
            guiaCtrlClient = new GuiaCtrlClient();
            List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
            Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guia.getCodigo(), null, null, tokenCtrl.getStrToken());
            lstGuias = response.readEntity(gTypeG);
            // valido que tuve respuesta
            if(lstGuias.get(0) != null){
                // obtengo el id de la guía en CCV
                Long idGuiaCtrl = lstGuias.get(0).getId();
                // instancio la guía a editar
                ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                // obtengo el estado "CANCELADA" del CCV
                List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica> lstParmEstados;
                paramCtrlClient = new ParamCtrlClient();
                GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>> gTypeParam = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>>() {};
                Response responseParam = paramCtrlClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("CtrlTipoParamEstGuia"), ResourceBundle.getBundle("/Config").getString("CtrlGuiaCancelada"), tokenCtrl.getStrToken());
                lstParmEstados = responseParam.readEntity(gTypeParam);
                // solo continúo si encontré el Estado correspondiente
                if(lstParmEstados.get(0) != null){
                    // seteo la Guía solo con los valores que necesito para editarla
                    guiaCtrol.setId(idGuiaCtrl);
                    guiaCtrol.setEstado(lstParmEstados.get(0));
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
                    response = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                    if(response.getStatus() == 200){
                        // se completaron todas las operaciones
                        actualizadaCcv = true;
                    }
                }else{
                    estadoCcvNoEncontrado = true;
                }
                // cierro los clientes
                paramCtrlClient.close();
                guiaCtrlClient.close();
            }else{
                // no se encontró la guía a editar en el CCV
                guiaCcvNoEncontrada = true;
            }
            
            ////////////////////////////////////////////////////////////////
            // reintegro los productos y deshabilito los ítems de la guía //
            ////////////////////////////////////////////////////////////////
            // obtengo los items
            List<ItemProductivo> lstItems = guia.getItems();
            // recorro el listado y por cada uno retorno los productos a su origen original y deshabilito
            for (ItemProductivo item : lstItems){
                // obtengo el id del item origen
                Long idItemOrigen = item.getItemOrigen();
                // obtengo el item origen
                ItemProductivo itemOrigen = itemFacade.find(idItemOrigen);
                // seteo los saldos actuales
                float saldoActual = itemOrigen.getSaldo();
                float saldoKgActual = itemOrigen.getSaldoKg();
                // actualizo los saldos
                itemOrigen.setSaldo(saldoActual + item.getTotal());
                itemOrigen.setSaldoKg(saldoKgActual + item.getTotalKg());
                // actualizo el origen
                itemFacade.edit(itemOrigen);
                // deshabilito el item actual
                item.setHabilitado(false);
                itemFacade.edit(item);
            }
            actualizadosItems = true;
            
            /////////////////////////
            // notifico al titular //
            /////////////////////////       
            if(notificarCancelacion()){
                titularNotificado = true; 
            }else{
                titularNotificado = false;
            }
            // armo el mensaje
            if(!actualizadaCcv || !actualizadaLocal || !actualizadosItems || guiaCcvNoEncontrada || estadoCcvNoEncontrado || !titularNotificado){
                msgErrorCancelGuia = "Hubo un error cancelando la guía. ";
                if(!actualizadaCcv) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizó la guía en el CCV. ";
                if(!actualizadaLocal ) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizó la guía localmente. ";
                if(!actualizadosItems ) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizaron los items. ";
                if(guiaCcvNoEncontrada ) msgErrorCancelGuia = msgErrorCancelGuia + "No se encontró la guía para actualizar en el CCV. ";
                if(estadoCcvNoEncontrado ) msgErrorCancelGuia = msgErrorCancelGuia + "No se encontró el estado CANCELADA en el CCV. ";
                if(!titularNotificado ) msgErrorCancelGuia = msgErrorCancelGuia + "No se pudo notificar al titular.";
                JsfUtil.addErrorMessage(msgErrorCancelGuia);
            }else{
                JsfUtil.addSuccessMessage("La Guía se canceló exitosamente, "
                        + "se reintegraron sus productos al origen, "
                        + "se actualizó el estado en el componente de Control y se notificó al titular de la guía");
            }
            
        }catch(ClientErrorException ex){
            msgErrorCancelGuia = "Hubo un error cancelando la guía. ";
            if(!actualizadaCcv) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizó la guía en el CCV. ";
            if(!actualizadaLocal ) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizó la guía localmente. ";
            if(!actualizadosItems ) msgErrorCancelGuia = msgErrorCancelGuia + "No se actualizaron los items. ";
            if(guiaCcvNoEncontrada ) msgErrorCancelGuia = msgErrorCancelGuia + "No se encontró la guía para actualizar en el CCV. ";
            if(estadoCcvNoEncontrado ) msgErrorCancelGuia = msgErrorCancelGuia + "No se encontró el estado CANCELADA en el CCV. ";
            if(!titularNotificado ) msgErrorCancelGuia = msgErrorCancelGuia + "No se pudo notificar al titular.";
            JsfUtil.addErrorMessage(msgErrorCancelGuia + ex.getMessage());
        }
    }
    
    /**
     * Método para extender la vigencia de una guía. Aplicable para todo tipo de guías.
     * Extiende su fecha de vencimiento, guarda los motivos del cambio, 
     * genera un nueo código qr y si es de trnsporte y no de movimiento interno 
     * actualiza los datos registrados en el componente de control (CTRL)
     */
    public void extenderVenc(){
        boolean actualizoCtrl = true, error = false;
        String msg = "";
        // valido que la fecha de vencimiento sea mayor a la original
        Guia g = guiaFacade.find(guia.getId());
        if(guia.getFechaVencimiento().after(g.getFechaVencimiento())){
            // encripto el código para generar el qr en el reporte y lo asigno a la guía
            Date fechaExtend = new Date(System.currentTimeMillis());
            String codLiso = guia.getCodigo() + "_" + fechaExtend.toString();
            guia.setCodQr(CriptPass.encriptar(codLiso));        
            // actualiza la guía localmente
            guiaFacade.edit(guia);
            // solo actualizo en CTRL si la guía es de transporte y no de movimiento interno
            if(guia.getTipo().isHabilitaTransp() && !guia.getTipo().isMovInterno()){
                try{
                    ///////////////////////////////
                    // actualiza la guía en CTRL //
                    ///////////////////////////////
                    // obtengo el token si no está seteado o está vencido
                    if(tokenCtrl == null){
                        getTokenCtrl();
                    }else try {
                        if(!tokenCtrl.isVigente()){
                            getTokenCtrl();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token de CTRL", ex.getMessage()});
                    }
                    // busco la Guía
                    guiaCtrlClient = new GuiaCtrlClient();
                    List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
                    Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guia.getCodigo(), null, null, tokenCtrl.getStrToken());
                    lstGuias = response.readEntity(gTypeG);
                    // valido que tuve respuesta
                    if(lstGuias.get(0) != null){
                        // obtengo el id de la guía en CCV
                        Long idGuiaCtrl = lstGuias.get(0).getId();
                        // instancio la guía a editar
                        ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                        // seteo la nueva fecha de venicimiento y el nuevo qr
                        guiaCtrol.setId(idGuiaCtrl);
                        guiaCtrol.setFechaVencimiento(guia.getFechaVencimiento());
                        // seteo el estado y el cuit destino porque los chequea la API
                        guiaCtrol.setEstado(lstGuias.get(0).getEstado());
                        guiaCtrol.setCuitDestino(lstGuias.get(0).getCuitDestino());
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
                        response = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                        if(response.getStatus() == 200){
                            // seteo mensaje y flag
                            msg = "Se actualizó la guía localmente y se actualizaron los datos en el componente de Control y Verificación";
                        }else{
                            // seteo mensaje y flag
                            msg = "Se actualizó la guía localmente pero no se pudo actualizar en el componente de Control y Verificación.";
                            actualizoCtrl = false;
                            error = true;
                        }
                        // cierro el cliente
                        guiaCtrlClient.close();
                        // actualizo el flag para emitir
                        vencAtualizado = true;
                    }else{
                        error = true;
                        msg = "Se actualizo la guía localmente pero no se encontraron sus datos en el componente de Control y Verificación.";
                    }
                }catch(ClientErrorException ex){
                    error = true;
                    msg += " Hubo un error al actualizar los datos en el componente de Control y Verificación. " + ex.getMessage();
                }
            }else{
                actualizoCtrl = true;
            }
            // notifico al titular que se extendió el período de vigencia de la guía si no hubo errores.
            if(actualizoCtrl){
                if(guia.getOrigen().getEmail() != null){
                    if(notificarExtensionVig(guia.getOrigen().getEmail())){
                        msg += " Se notificó al titular de la guía la extensión de la vigencia de la misma.";
                    }else{
                        error = true;
                        msg += " No se pudo notificar al titular de la guía la extensión de la vigencia de la misma.";
                    }
                }else{
                    error = true;
                    msg += " No se pudo notificar al titular de la guía la extensión de la vigencia de la misma dado que no tiene registrada una dirección de correo.";
                }
            }
            
        }else{
            error = true;
            msg = "La nueva fecha de vencimiento debe ser mayor a la existente.";
        }
        
        if(error){
            JsfUtil.addErrorMessage(msg + " Por favor, contacte al administrador.");
        }else{
            JsfUtil.addSuccessMessage(msg);
        }
    }
    
    /**
     * Método para limpiar los datos del formulario de extensión de vencimiento.
     * Vacía el campo observaciones de la Guía.
     */
    public void limpiarFormExtend(){
        guia = guiaFacade.find(guia.getId());
        guia.setObs("");
    }
    
    /**
     * Método para emitir el pdf con los datos actualizados de la guía.
     * Obtengo la autorización de la cual surgen los productos.
     * Mando a generar el listado de copias mediante generarCopias()
     * y luego imprimo el pdf con las copias generadas mediante imprimirCopias().
     * Y si todo salió bien mando a imprimir las copias
     */
    public void emitirExtend(){
        List<Guia> copias = new ArrayList<>(); 
        // obtengo la autorización vinculada a la Guías
        Autorizacion aut = autFacade.getExistente(guia.getNumFuente()); 
        // si la guía tomó productos de una u otras guías, agrupo los items
        List<ItemProductivo> itemsAgr;
        if(!guia.getTipo().isDescuentaAutoriz()){
            itemsAgr = agruparItems();
        }else{
            itemsAgr = guia.getItems();
        }       
        // genero las copias
        generarCopias(aut, itemsAgr, copias);
        // imprimo el pdf
        imprimirGuia(copias, aut);
    }
    
    /**
     * Método para generar el pdf con los formularios provisorios a imprimir.
     * Valida que estén seteados la cantidad y las horas de vigencia.
     * Crea un listado de formularios y los manda como parámetro al reporte.
     * Actualiza la cantidad de formularios emitidos de la guía y la edita en la base.
     */
    public void imprimirFormularios() {
        List<Formulario> formularios = new ArrayList<>();
        Date hoy = new Date(System.currentTimeMillis());
        boolean valida = true;
        String msgError = "";
        // valido cantidades
        if(form.getCantidad() == 0){
            msgError = msgError + "Debe especificar la cantidad de copias.";
            valida = false;
        }
        if(form.getHorasVigencia() == 0){
            msgError = msgError + "Debe especificar la cantidad de horas de vigencia.";
            valida = false;
        }
        if(valida){
            // solo continúo si valida
            // seteo el número de formulario inicial
            int inicio = guia.getFormEmitidos() + 1;
            int numForm = guia.getFormEmitidos();
            try{
                // genero las copias de formulario
                Formulario f;
                for(int i = 0; i < form.getCantidad(); i = i + 1){
                    // seteo la Guía en el listado
                    f = new Formulario();
                    f.setCantidad(form.getCantidad());
                    f.setCodAut(guia.getNumFuente());
                    f.setCodGuia(guia.getCodigo());
                    f.setCuitTitular(guia.getOrigen().getCuit());
                    f.setDepto(guia.getOrigen().getDepartamento());
                    f.setDestino(delegSelected);
                    f.setFechaEmision(hoy);
                    f.setHorasVigencia(form.getHorasVigencia());
                    f.setId(Long.valueOf(numForm + 1));
                    f.setIdCatastral(guia.getOrigen().getInmCatastro());
                    f.setNomInmueble(guia.getOrigen().getInmNombre());
                    f.setNomTitular(guia.getOrigen().getNombreCompleto());
                    // agrego la copia
                    formularios.add(f);
                    
                    numForm += 1;
                }
                // genero el reporte con todas las copias
                JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(formularios);
                String reportPath;
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "form.jasper");

                jasperPrint =  JasperFillManager.fillReport(reportPath, new HashMap(), beanCollectionDataSource);
                HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                httpServletResponse.addHeader("Content-disposition", "attachment; filename=formProv_" + "_" + guia.getFormEmitidos() + inicio + "_" + numForm + ".pdf");

                ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

                JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
                FacesContext.getCurrentInstance().responseComplete(); 

                // actualizo la guía con la cantidad de copias de formulario
                guia.setFormEmitidos(guia.getFormEmitidos() + formularios.size());
                guiaFacade.edit(guia);
                // limpio todo
                limpiarFormImpForm();
            }catch(JRException | IOException ex){
            msgErrorEmision = "Hubo un error generando los formularios provisorios para la guía. " + ex.getMessage();
        }
        }else{
            JsfUtil.addErrorMessage(msgError);
        }
            
    }
    
    /**
     * Método para limpiar el formulario de emisión de formularios provisorios
     */
    public void limpiarFormImpForm(){
        form = new Formulario();
        delegSelected = new Delegacion();
    }
    
    
    //////////////////////
    // Métodos privados //
    //////////////////////

    /**
     * Método privado y estático que inicializa el map con los tipos de consulta de cancelación
     * @return Map<Integer, String> map con las claves y tipos
     */
    private static Map<Integer, String> iniciateMap() {
        Map<Integer, String> result = new HashMap<>();
        result.put(1, "Cancel");
        result.put(2, "PrintForm");
        return Collections.unmodifiableMap(result);
    }
    /**
     * Método para limpiar resetear el listado con los tipos.
     * utilizado en prepareNew(), prepareEdit() y limpiarForm()
     */
    private void resetearCampos() {
        lstTiposGuia = tipoGuiaFacade.getHabilitados();
    }

    /**
     * Método para obtener la EntidadGuia de origen, si no existe se crea.
     * Si la creo y la guía de la cual descuenta no es de movimiento interno, se setea el origen con los datos del titular de la guía origen. 
     * En es movimiento interno se setean los datos del destino de la guía de la que descontó, porque puede no ser el mismo que el titular.
     * Si se está editando la guía valida que tenga la autorización fuente.
     * Utilizado en save()
     * @return EntidadGuia entidad guía generada
     */
    private EntidadGuia obtenerEntidadOrigen(Persona per, Parametrica tipoFuente) {
        EntidadGuia ent;
        boolean fuenteMovInterno = false;
        if(autSelected == null){
            autSelected = autFacade.getExistente(guia.getNumFuente());
        }
        Inmueble inm = autSelected.getInmuebles().get(0);
        if(tipoFuente.getNombre().equals(ResourceBundle.getBundle("/Config").getString("Autorizacion"))){
            // si descontó de una autorización verifico si la entidad origen está registrada.
            ent = entGuiaFacade.getOrigenExistente(per.getCuit(), inm.getNombre());
        }else if(guiaAsignada.getTipo().isMovInterno()){
            // si descontó de otra guía y es de movimiento interno seteo el flag y
            // verifico si está registrado el orígen para el inmueble y el destinatario de la guía de movimiento interno
            fuenteMovInterno = true;
            ent = entGuiaFacade.getOrigenExistente(guiaAsignada.getDestino().getCuit(), inm.getNombre());
        }else{
            // seteo el origen de la guía de la que descontó
            ent = guiaAsignada.getOrigen();
        }

        if(ent != null){
            return ent;
        }else{
            ent = new EntidadGuia();
            // seteo la persona según el origen de los productos sea o no una guía de movimiento interno
            if(fuenteMovInterno){
                ent.setCuit(guiaAsignada.getDestino().getCuit());
                ent.setIdRue(guiaAsignada.getDestino().getIdRue());
                ent.setNombreCompleto(guiaAsignada.getDestino().getNombreCompleto());
                ent.setTipoPersona(guiaAsignada.getDestino().getTipoPersona());
                ent.setEmail(guiaAsignada.getDestino().getEmail());
            }else{
                ent.setCuit(per.getCuit());
                ent.setIdRue(per.getIdRue());
                ent.setNombreCompleto(per.getNombreCompleto());
                ent.setTipoPersona(per.getTipo());
                ent.setEmail(per.getEmail());
            }
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
     * Método para obtener la EntidadGuia de destino, si no existe se crea
     * seteando sus datos con los del destinatario y su domicilio seleccionado
     * @return EntidadGuia entidad guía generada
     */
    private EntidadGuia obtenerEntidadDestino(Persona per, Domicilio domDestino) {
        EntidadGuia ent;
        // verifico si existe previamente
        TipoParam tipoParamEntGuia = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoEntidadGuia"));
        Parametrica tipoEntidad = paramFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TegDestino"), tipoParamEntGuia);
        ent = entGuiaFacade.getDestinoExistente(per.getCuit(), tipoEntidad, domDestino);
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
            ent.setIdLocGT(domDestino.getIdLoc());
            ent.setInmDomicilio(domDestino.getCalle() + "-" + domDestino.getNumero());
            ent.setLocalidad(domDestino.getLocalidad());
            ent.setDepartamento(domDestino.getDepartamento());
            ent.setProvincia(domDestino.getProvincia());
//            try{
//                // obtengo el token si no está seteado o está vencido
//                if(tokenRue == null){
//                    getTokenRue();
//                }else try {
//                    if(!tokenRue.isVigente()){
//                        getTokenRue();
//                    }
//                } catch (IOException ex) {
//                    logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
//                }
//                
//                
//                personaClient = new PersonaClient();
//                personaRue = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(per.getIdRue()), tokenRue.getStrToken());
//                personaClient.close();
//                ent.setIdLocGT(personaRue.getDomicilio().getIdLocalidadGt());
//                ent.setInmDomicilio(personaRue.getDomicilio().getCalle() + "-" + personaRue.getDomicilio().getNumero());
//                ent.setLocalidad(personaRue.getDomicilio().getLocalidad());
//                ent.setDepartamento(personaRue.getDomicilio().getDepartamento());
//                ent.setProvincia(personaRue.getDomicilio().getProvincia());
//            }catch(ClientErrorException ex){
//                // muestro un mensaje al usuario
//                JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
//                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
//                        + "servicio REST de RUE", ex.getMessage()});
//                return null;
//            }
            
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
    private boolean enviarCorreoEmision(){  
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
     * Método para notificar a titular de una guía que la misma fue cancelada.
     * Utilizado en cancelarGuia()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarCancelacion(){
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>La Guía " + guia.getCodigo() + " de la cual es titular, acaba de ser cancelada. Todo lo documentado en la misma queda sin efecto. "
                + "Los productos descontados han sido retornados a sus fuentes de origen. Por cualquier consulta podrá dirigirse a la Autoridad local.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";        
        
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guia.getOrigen().getEmail()));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Notificación de cancelación de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo de cancelación al titular de la Guía" + ex.getMessage());
        }        
        
        return result;
    }

    /**
     * Método para enviar un correo electrónico al nuevo destinatario de la Guía.
     * Utilizado en saveDestino() en caso de haber sido modificado el destino de 
     * una guía ya emitida.
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarNuevoDestino() {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>Ha sido consignado como destinatario de la Guía " + guia.getCodigo() + " proveniente de la Provincia de " + ResourceBundle.getBundle("/Config").getString("Provincia") + ".</p>"
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
            System.out.println("Hubo un error enviando el corre al usuario" + ex.getMessage());
        }
        
        return result;        
    }

    /**
     * Método para enviar un correo electrónico al destinatario desafectado de la Guía.
     * Utilizado en saveDestino() en caso de haber sido modificado el destino de 
     * una guía ya emitida.
     * @param mail String dirección de correo electrónico del destinatario desafectado
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean cancelarDestino(String mail) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date hoy = new Date(System.currentTimeMillis());
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        
        bodyMessage = "<p>Estimado/a</p> "
                + "Por decisión del titular de la Guía y con la aprobación del trámite por parte de la Autoridad local de la Provincia, "
                + "con fecha " + formateador.format(hoy) + " ha quedado sin efecto su designación como destinatario de la Guía " + guia.getCodigo() + " proveniente de la Provincia de " + ResourceBundle.getBundle("/Config").getString("Provincia") + ".</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";

        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Aviso de desafectación como destinatario de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo al usuario" + ex.getMessage());
        }
        
        return result;               
    }    
    
    /**
     * Método para listar los inmuebles vinculados a la Autorización de la cual provienen los productos de la Guía.
     * Si descuenta de autorización busco la misma a partir de su número de fuente, 
     * si descuenta de guías, busco la autorización desde en número de fuente de cualquiera de las guías de las que
     * se seleccionaron para el descuento de productos
     * Utilizado en buscar(), prepareView(), save() y prepareViewDetalle()
     */
    private void setearInmueblesOrigen() {
        lstInmueblesOrigen = new ArrayList<>();
        // obtengo la Autorización según el tipo de Guía
        Autorizacion aut;
        if(guia.getTipo().isDescuentaAutoriz()){
            // si la guía descuenta productos directamente de una Autorización, la obtengo
            aut = autFacade.getExistente(guia.getNumFuente());
        }else{
            // si la guía descuenta productos de otras guías, primero las busco y después busco la Autorización
            // fuente de cualquiera de ellas
            aut = autFacade.getExistente(guia.getGuiasfuentes().get(0).getNumFuente());
        }
        for(Inmueble inm : aut.getInmuebles()){
            lstInmueblesOrigen.add(inm);
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

    /**
     * Método para validar la vigencia de la guía en el caso que se quiera cancelar.
     * Según esté vigente se setea el mensaje correspondiente para mostrar en la vista /guia/gestion/cancel.xhtml
     */
    private void validarVigencia(Integer tipo) {
        Date hoy = new Date(System.currentTimeMillis());
        if(tipo == 1){
            if(guia.getFechaVencimiento().after(hoy)){
                msgCancelVigente = ResourceBundle.getBundle("/Config").getString("MsgCancelVigente");
            }else{
                msgCancelVencida = ResourceBundle.getBundle("/Config").getString("MsgCancelVencida");
            }
        }else if(tipo == 2){
            if(guia.getFechaVencimiento().after(hoy)){
                msgImpFormVigente = ResourceBundle.getBundle("/Config").getString("MsgImpFormVigente");
            }else{
                msgImpFormVencida = ResourceBundle.getBundle("/Config").getString("MsgImpFormVencida");
            }
        }
    }

    /**
     * Método privado para obtener los ítems agrupados de una guía que tome los productos de una o más guías
     * dado que pueden tener más de un ítem con el mismo producto, surgidos de guías diferentes.
     * Utilizado en emitir()
     * @return List<ItemProductivo> Listado de los ítems agrupados por producto.
     */
    private List<ItemProductivo> agruparItems() {
        // actualizo la guia
        Guia g = guiaFacade.find(guia.getId());
        List<ItemProductivo> itemsAgrupados = new ArrayList<>();
        // recorro los ítems vinculados a la guía para poblar el listado de agrupados (sin repetir productos)
        for (ItemProductivo item : g.getItems()){
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
        if(itemsAgrupados.size() != g.getItems().size()){
            // si hay diferencia vuelvo a recorrer el listado original para hacer la agrupación
            for (ItemProductivo item : g.getItems()){
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
            return guia.getItems();
        }
    }

    /**
     * Método para liquidar todas las tasas de los productos de una guía para luego generar el volante de pago.
     * Primero recorre las tasas configuradas para la guía. Por cada una verifica si discrimina valores según la configuración.
     * Si discrimina, verifica por cada tasa con discriminación registrada que, además de estar en la base, deberán estar también en el properties.
     * Al momento son 5:
     *  1. Según el origen del predio: CondAforoFiscal=FISCAL CON AFORO. El valor de la variable configurada deberá ser el del origen del predio a validar, de las fuentes de productos.
     *  2. Según el destino de los productos: CondDerInspExt=DESTINO EXTERNO y CondDerInspInt=DESTINO INTERNO
     *  3. Según el tipo de intervención autorizado para la fuente de productos: IntervPCUS=CAMBIO USO DEL SUELO y IntervPM=PLAN MANEJO SOSTENIBLE.
     *  El valor de cada variable configurada deberá ser el tipo de intervención que corresponda para cada caso.
     * Según corresponda en cada caso se agregará o no al listado de tasas a liquidar.
     * Si no discrimina, se agrega directamente.
     * Luego, se recorren los ítems productivos y por cada uno se calcula el detalle correpondiente a cada tasa que integra el listado, 
     * siempre que el ítem la tenga configurada.
     * Finalmente se genera el listado de las liquidaciones que se mostrarán en pantalla y será luego mandado como datasource para el reporte.
     * Invocado por cargarFrame() al llamar a la vista tasas.xhtml
     * @param guiaAct Guia guía de la cual se liquidarán las tasas.
     */
    private void liquidarTasas(Guia guiaAct) {
        for(TipoGuiaTasa tgt : guiaAct.getTipo().getTasas()){
            // por cada tasa que tenga configurada la guía a liquidar, verifico si tiene discriminaciones
            if(tgt.getTipo().isLeeConf()){
                // si las tiene, respondo en cada caso
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondAforoFiscal"))){
                    // si discrimina por origen del predio, verifico si el predio de la fuente de productos tiene un origen que corresponda a la discriminación
                    Autorizacion aut = autFacade.getExistente(guia.getNumFuente());
                    if(aut.getInmuebles().get(0).getOrigen().getNombre().equals(ResourceBundle.getBundle("/Config").getString("CondAforoFiscal"))){
                        // si el origen del predio amerita el pago, guardo la tasa en el listado
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondDerInspExt"))){
                    // si discrimina por el destino externo de los productos se la agrega si el destino de la guía es externo
                    if(guiaAct.isDestinoExterno()){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("CondDerInspInt"))){
                    // si discrimina por el destino interno de los productos se la agrega si el destino de la guía es interno
                    if(!guiaAct.isDestinoExterno()){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("IntervPCUS"))){
                    // si discrimina por intervención PCUS de la fuente de productos, se la agrega si ese es el tipo de intervención
                    Autorizacion aut = autFacade.getExistente(guia.getNumFuente());
                    if(aut.getIntervencion().getNombre().equals(ResourceBundle.getBundle("/Config").getString("IntervPCUS"))){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
                if(tgt.getTipo().getConf().equals(ResourceBundle.getBundle("/Config").getString("IntervPM"))){
                    // si discrimina por intervención PM de la fuente de productos, se la agrega si ese es el tipo de intervención
                    Autorizacion aut = autFacade.getExistente(guia.getNumFuente());
                    if(aut.getIntervencion().getNombre().equals(ResourceBundle.getBundle("/Config").getString("IntervPM"))){
                        lstNombresTasas.add(tgt.getTipo().getNombre());
                    }
                }
            }else{
                // si no las tiene, directamente agrego la tasa al listado
                lstNombresTasas.add(tgt.getTipo().getNombre());
            }
        }

        // cargo los detalles por item
        lstDetallesTasas = new ArrayList<>();
        for(ItemProductivo item : guiaAct.getItemsAgrupados()){
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
        liquidacion.setTipoGuia(guiaAct.getTipo().getNombre());
        liquidacion.setCodGuia(guiaAct.getCodigo());
        liquidacion.setDetalles(lstDetallesTasas);
        liquidaciones.add(liquidacion);
    }
    
    /**
     * Método para generar un pdf con las copias de una guía.
     * Agrega los parámetros que correspondan según la configuración del CGL
     * Llamado por emitir() y emitirExtend()
     * @param guias List<Guia> listado con las copias de las guías a imprimir
     * @param aut Autorizacion autorización fuente de los productos de la guía para tomar de allí el martillo del inmueble
     */
    public void imprimirGuia(List<Guia> guias, Autorizacion aut){
        try{
            final Map<String,Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(guias);
            String reportPath;
            if(guia.getTipo().isHabilitaTransp() && !guia.getTipo().isMovInterno()){
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guiaTransp.jasper");
            }else if(guia.getTipo().isHabilitaTransp() && guia.getTipo().isMovInterno()){
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guiaAcopio.jasper");
            }else{     
                reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "guia.jasper");
            }
            // muestro guardo el expediente como parámetro
            parameters.put("expediente", aut.getNumExp());
            // si discrimina tasa según el origen del predio guardo dicho origen como parámetro
            if(ResourceBundle.getBundle("/Config").getString("DiscTasaOrigen").equals("si")){
                parameters.put("origenPredio", aut.getInmuebles().get(0).getOrigen().getNombre());
            }
            // si muestra la autorización en la guía, agrego su número como parámetro
            if(ResourceBundle.getBundle("/Config").getString("MuestraAutGuias").equals("si")){
                parameters.put("numAut", aut.getNumero());
            }
            // si muestra los datos catastrales del predio, lo agrego como parámetro
            if(ResourceBundle.getBundle("/Config").getString("MuestraPredioGuias").equals("si")){
                parameters.put("inmCatastro", aut.getInmuebles().get(0).getIdCatastral());
            }
            // si muestra el destino de los productos lo agrego como parámetro
            if(ResourceBundle.getBundle("/Config").getString("MuestraDestinoGuias").equals("si")){
                String destino;
                boolean externo;
                if(guia.getTipo().isDescuentaAutoriz()){
                    // tomo el dato directamente de la guía
                    externo = guia.isDestinoExterno();
                }else{
                    // tomo el dato de la guía fuente de productos
                    externo = guia.getGuiasfuentes().get(0).isDestinoExterno();
                }
                if(externo){
                    destino = "Extra Provincial";
                }else{
                    destino = "Provincial";
                }
                parameters.put("destinoProd", destino);
            } 
            // si gestiona rodales, genero el parámetro (String) para mostrarlos en el reporte. GestionaRodales
            if(ResourceBundle.getBundle("/Config").getString("GestionaRodales").equals("si")){
                String stRodales = "";
                for(Rodal r : guia.getRodales()){
                    if(stRodales.equals("")){
                        stRodales = Integer.toString(r.getNumOrden());
                    }else{
                        stRodales = stRodales + "; " + Integer.toString(r.getNumOrden());
                    }
                }
                parameters.put("rodales", stRodales);
            }            
                
            jasperPrint =  JasperFillManager.fillReport(reportPath, parameters, beanCollectionDataSource);
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
        }catch(JRException | IOException ex){
            msgErrorEmision = "Hubo un error emitiendo la guía. " + ex.getMessage();
        }
    }

    /**
     * Método que genera el pdf con el volante a partir de un listado de LiqTotalTasas con las tasas de una guía
     * Llamado por generarVolante()
     */
    private void imprimirVolante() {
        try{
            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(liquidaciones);
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(RUTA_VOLANTE + "volante.jasper");
            jasperPrint =  JasperFillManager.fillReport(reportPath, new HashMap(), beanCollectionDataSource);
            HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            httpServletResponse.addHeader("Content-disposition", "attachment; filename=volante_pago_" + liquidaciones.get(0).getCodGuia() + ".pdf");
            ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();

            JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
            FacesContext.getCurrentInstance().responseComplete();
        }catch(JRException | IOException ex){
            JsfUtil.addErrorMessage("Hubo un error al imprimir el volante de pago. " + ex.getMessage());
        }
    }
    
    /**
     * Método privado para listar las guías que se emitieron sin registrar el comprobante de pago de tasas.
     * Invocado por getGuiasSinPago(), solo se ejecuta si la aplicación está configurada para emitir guías
     * sin registrar los pagos de tasas.
     */
    private void verGuiasAdeudadas(){
        guiasSinPago = new ArrayList<>();
        lstNombresTasas = new ArrayList<>();
        List<Guia> adeudadas = guiaFacade.getSinPago(guia.getOrigen().getCuit());
        for (Guia g : adeudadas){
            // limpio el listado de liquidaciones
            liquidaciones = new ArrayList<>();
            // obtengo el total adeudado
            liquidarTasas(g);
            GuiaSinPago gsp = new GuiaSinPago();
            gsp.setId(g.getId());
            gsp.setCodGuia(g.getCodigo());
            gsp.setFechaEmision(g.getFechaEmisionGuia());
            gsp.setFechaVenc(g.getFechaVencimiento());
            gsp.setTotalAdeudado(liquidaciones.get(0).getTotalALiquidar());
            guiasSinPago.add(gsp);
        }
    }    
    
    /**
     * Método para enviar un correo electrónico al titular de una guía
     * para notificarlo de la extensión del período de vigencia.
     * @param mail String dirección de correo electrónico del titular
     * @return boolean verdadero o false según el resultado del envío.
     */
    private boolean notificarExtensionVig(String mail) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date hoy = new Date(System.currentTimeMillis());
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        
        bodyMessage = "<p>Estimado/a</p> "
                + "Hoy: " + formateador.format(hoy) + " se ha extendido la vigencia de la "
                + "guía " + guia.getCodigo() + " emitida el " + formateador.format(guia.getFechaEmisionGuia()) + ", "
                + "que ahora vencerá el " + formateador.format(guia.getFechaVencimiento()) + ".</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";

        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Aviso de extensión del período de vigencia de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo al usuario" + ex.getMessage());
        }
        
        return result;               
    }  

    /**
     * Método para limpiar el formulario de búsqueda de guías.
     * Resetea todas las variables y listados.
     */
    private void limpiarFormBusqGuias() {
        optBusqSelected = null;
        lstBusqTipoGuias = new ArrayList<>();
        busqTipoGuiaSelected = null;
        lstBusqEstadosGuias = new ArrayList<>();
        busqEstadoGuiaSelected = null;
        busqCodGuia = null;
        busqCuitTit = null;
        busqCuitDest = null;
    }

    /**
     * Método privado para validar el domicilio seleccionado para el destino de la guía de transporte.
     * Recorre cada guía fuente de productos y valida que la condición de externo o interno a la provincia del domicilio
     * coicida con lo definido para la guía fuente (guia.destinoExterno)
     * @return 
     */
    private boolean validarDomDestino() {
        boolean result = true;
        // recorro las guías fuentes de la guía y valido la correspondencia del domicilio a asignar como destino con el flag de cada una de ellas
        for(Guia gf : guia.getGuiasfuentes()){
            if(domDestino.getProvincia().toUpperCase().equals(ResourceBundle.getBundle("/Config").getString("Provincia").toUpperCase()) 
                && gf.isDestinoExterno()){
                // si el domicilio es interno y el flag indica destino externo no valida
                JsfUtil.addErrorMessage("El domicilio destino seleccionado se encuentra en la provincia "
                        + "y la/s guía/s fuente de productos está/n seteada/s para generar guías de transporte con destino externo.");
               result = false;
            }else if(!domDestino.getProvincia().toUpperCase().equals(ResourceBundle.getBundle("/Config").getString("Provincia").toUpperCase()) 
                && !gf.isDestinoExterno()){
                // si el domicilio es externo y el flag indica destino interno no valida
                JsfUtil.addErrorMessage("El domicilio destino seleccionado se encuentra fuera de la provincia "
                        + "y la/s guía/s fuente de productos está/n seteada/s para generar guías de transporte con destino interno.");
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Método privado para generar el listado con las copias de la guía a imprimir.
     * Recibe la Autorización, el listado de items productivos y el listado de guías a seteas, tantas como copias estén configuradas
     * Genera el código QR. Agrupo los itmes productivos si la guía toma productos de otra guía.
     * Setea los martillos (el de obrajero solo si está configurado así)
     * Valida la existencia de obrajero y, de tenerlo, del correspondiente martillo.
     * En caso de no tener obrajero o que este no tenga martillo, se setean las rutas de archivos suplentes
     * Recorre la cantidad de copias a imprimir según el tipo de guía y va generando una por una.
     * Llamado por emitir() y emitirExtend()
     * @param aut Autorización desde la cual se toman los productos
     * @param itemsAgr Listado de Items agrupados
     * @param guias Listado de las guías a poblar como paso previo a la generación del pdf
     */
    private void generarCopias(Autorizacion aut, List<ItemProductivo> itemsAgr, List<Guia> guias){
        Guia g;
        // obtengo la ruta al martillo (del primer inmueble asociado)
        String rutaMartillo = aut.getInmuebles().get(0).getRutaArchivo() + aut.getInmuebles().get(0).getNombreArchivo();
        // obtengo la ruta al martillo del obrajero si corresponde
        String rutaMartilloObrj = null;
        if(ResourceBundle.getBundle("/Config").getString("TieneObrajeros").equals("si")){
            // seteo la ruta al archivo "sinObrajero.jpg"
            String rutaSinObrj = ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                            ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + 
                            ResourceBundle.getBundle("/Config").getString("MrtSinObrajero");
            // seteo la ruta al archivo "sinMartillo.jpg"
            String rutaSinMart = ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                            ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + 
                            ResourceBundle.getBundle("/Config").getString("MrtSinMartillo");
            // obro según el tipo de guía
            if(guia.getTipo().isDescuentaAutoriz()){
                if(guia.getObrajeros().isEmpty()){
                    // sin obrajero
                    rutaMartilloObrj = rutaSinObrj;
                }else if(guia.getObrajeros().get(0).getNombreArchivo() == null){
                    // sin martillo
                    rutaMartilloObrj = rutaSinMart;
                }else{
                    // tiene obrajero y martillo
                    rutaMartilloObrj = guia.getObrajeros().get(0).getRutaArchivo() + guia.getObrajeros().get(0).getNombreArchivo();
                }
            }else{
                Guia gFuente = guia.getGuiasfuentes().get(0);
                if(gFuente.getObrajeros().isEmpty()){
                    // sin obrajero
                    rutaMartilloObrj = rutaSinObrj;
                }else if(gFuente.getObrajeros().get(0).getNombreArchivo() == null){
                    // sin martillo
                    rutaMartilloObrj = rutaSinMart;
                }else{
                    // tiene obrajero y martillo
                    rutaMartilloObrj = gFuente.getObrajeros().get(0).getRutaArchivo() + gFuente.getObrajeros().get(0).getNombreArchivo();
                }
            }
        }
        try{
            for(CopiaGuia copia : guia.getTipo().getCopias()){
                // creo el inputStream con el martillo
                FileInputStream fisMartillo = new FileInputStream(rutaMartillo);
                // creo el inputStream con el martillo del obrajero si corresponde
                FileInputStream fisMartilloObrj = null;
                if(ResourceBundle.getBundle("/Config").getString("TieneObrajeros").equals("si")){
                    fisMartilloObrj = new FileInputStream(rutaMartilloObrj);
                }
                
                // seteo la Guía en el listado
                g = new Guia();
                g.setCodigo(guia.getCodigo());
                g.setDestinoCopia(copia.getDestino());
                g.setFechaAlta(guia.getFechaAlta());
                g.setFechaEmisionGuia(guia.getFechaEmisionGuia());
                g.setFechaVencimiento(guia.getFechaVencimiento());
                g.setItems(itemsAgr);
                g.setOrigen(guia.getOrigen());
                g.setTipo(guia.getTipo());
                g.setProvincia(guia.getProvincia());
                g.setCodQr(guia.getCodQr());
                g.setMartillo(fisMartillo);
                if(ResourceBundle.getBundle("/Config").getString("TieneObrajeros").equals("si")){
                    g.setMartilloObrajero(fisMartilloObrj);
                }
                // si la Guía habilita transporte, agrego los campos correspondientes
                if(guia.getTipo().isHabilitaTransp()){
                    g.setDestino(guia.getDestino());
                    g.setTransporte(guia.getTransporte());
                }
                // si gestiona rodales y es una guía de tránsito, seteo los rodales de la guía madre
                if(ResourceBundle.getBundle("/Config").getString("GestionaRodales").equals("si") && !guia.getTipo().isDescuentaAutoriz()){
                    guia.getRodales().clear();
                    guia.getRodales().addAll(guia.getGuiasfuentes().get(0).getRodales());
                }
                guias.add(g);
            }  
        }catch(FileNotFoundException ex){
            JsfUtil.addErrorMessage("Hubo un error generando el martillo para la guía. " + ex.getMessage());
        } 
    }
}