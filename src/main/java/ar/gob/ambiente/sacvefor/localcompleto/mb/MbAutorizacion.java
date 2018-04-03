
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.SubZona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoAutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.InmuebleFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoEspecieLocalFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.SubZonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ZonaIntervencionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Bean de respaldo para la gestión de Autorizaciones.
 * Implica la creación, edición y lectura de los datos 
 * de la Resolución autorizante.
 * Gestiona las vistas de /aut/gestion/
 * @author rincostante
 */
public class MbAutorizacion {

    /**
     * Variable privada: setea la página inicial para mostrar en el frame
     */
    private String page = "instrumento.xhtml";
    
    /**
     * Variable privada: objeto principal a gestionar
     */
    private Autorizacion autorizacion;
    
    /**
     * Variable privada: listado de las Autorizaciones existente
     */
    private List<Autorizacion> listado;
    
    /**
     * Variable privada: listado para el filtrado de la tabla
     */
    private List<Autorizacion> listadoFilter;
    
    /**
     * Variable privada: número de la Autorización para obtenerla para su gestión
     */
    private String autNumero;
    
    /**
     * Variable privada: flag que indica que la autorización que se está gestionando es nueva
     */
    private boolean nueva;
    
    /**
     * Variable privada: flag que indica que la autorización que se está gestionando es existente
     */
    private boolean edit;
    
    /**
     * Variable privada: flag que indica que la autorización que se muestra no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que se está editando el estado de la autorización
     */
    private boolean editEstado;
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */
    private Usuario usLogueado;
    
    /**
     * Variable privada: Listado de Tipos de Intervención autorizada
     */
    private List<Parametrica> lstIntervenciones;
    
    /**
     * Variable privada: Uso del suelo autorizado para el predio
     */
    private List<Parametrica> lstUsosSuelo;
    
    /**
     * Variable privada: Listado de Guías vinculadas a la Autorización
     */
    private List<Guia> lstGuias;
    
    /**
     * Variable privada: Zona seleccionada según ordenamiento ambiental
     */
    private ZonaIntervencion zonaSelected;
    
    /**
     * Variable privada: listado de las zonas de intervención que completa el combo para su selección
     */
    private List<ZonaIntervencion> lstZonas;
    
    /**
     * Variable privada: flag que indica que la zona seleccionada es la amarilla, para la selección de las subzonas correspondientes
     */
    private boolean amarillaSelected;
    
    /**
     * Variable privada: flag que indica que la zona seleccionada es la verde, para la selección de las subzonas correspondientes
     */
    private boolean verdeSelected;

    /**
     * Variable privada: Sub Zona vinculada a la Autorización
     * En función de la Zona
     */
    private SubZona subZonaSelected;
    
    /**
     * Variable privada: listado de las subzonas correspondientes a la zona amarilla
     */
    private List<SubZona> lstSubZonasAmarilla;
    
    /**
     * Variable privada: listado de las subzonas correspondientes a la zona verde
     */
    private List<SubZona> lstSubZonasVerde;
    
    ///////////////////////////////////
    // Gestión de objetos a agregar ///
    ///////////////////////////////////
    ///////////////
    // Personas ///
    ///////////////
    /**
     * Variable privada: guarda el cuit para buscar una persona con el rol que corres y agregarla a la Autorización
     */
    private Long cuitBuscar;
    
    /**
     * Variable privada: persona a vincular a vincular a la Autorización
     */
    private Persona persona;
    
    /**
     * Variable privada: flag que indica que se está en una vista detalle de los datos de la persona
     */
    private boolean viewPersona;
    
    ///////////////
    // Inmuebles //
    ///////////////
    /**
     * Variable privada: identificación catastral para buscar el inmueble a vincular a la Autorización
     */
    private String catastroBuscar;
    
    /**
     * Variable privada: inmueble a vincular a la Autorización
     */
    private Inmueble inmueble;
    
    /**
     * Variable privada: flag que indica que se está en la vista detalle de los datos del inmueble
     */
    private boolean viewInmueble;
    
    ///////////////
    // Productos //
    ///////////////
    /**
     * Variable privada: listado de Especies locales para componer el combo para la selección de productos
     */
    private List<ProductoEspecieLocal> lstEspecieLocal;
    
    /**
     * Variable privada: especie local seleccionada para obtener los productos correspondientes
     */
    private ProductoEspecieLocal especieSelected;
    
    /**
     * Variable privada: listado de productos disponibles para autorizar su extracción
     */
    private List<Producto> lstProductos;
    
    /**
     * Variable privada: clase de producto seleccionada para generar el ítem productivo a asignar a la Autorización
     */
    private Producto prodClaseSelected;
    
    /**
     * Variable privada: producto seleccionado para generar el ítem productivo a asignar a la Autorización
     */
    private Producto producto;
    
    /**
     * Variable privada: flag que indica que se está ante una vista detalle de las propiedades del producto
     */
    private boolean viewProducto;
    
    /**
     * Variable privada: Item a asignar a la Autorización para otorgar el cupo de extracción correspondiente
     */
    private ItemProductivo itemAutorizado;
    
    /**
     * Variable privada: listado de los ítems productivos vincuados a la Autorización
     */
    private List<ItemProductivo> lstItemsAut;
    
    ////////////
    // Estado //
    ////////////
    /**
     * Variable privada: listado de los estados posibles para asiganar a la autorización
     */
    private List<EstadoAutorizacion> lstEstados;
    
    /**
     * Variable privada: estado seleccionado
     */
    private EstadoAutorizacion estadoSelected;
    
    //////////////////////////////////////////
    // Accesos a datos, recursos inyectados //
    //////////////////////////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */  
    @EJB
    private AutorizacionFacade autFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica
     */
    @EJB
    private ParametricaFacade paramFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ZonaIntervencion
     */
    @EJB
    private ZonaIntervencionFacade zonaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de SubZona
     */
    @EJB
    private SubZonaFacade subZonaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EstadoAutorizacion
     */
    @EJB
    private EstadoAutorizacionFacade estadoFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Persona
     */
    @EJB
    private PersonaFacade perFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Inmueble
     */
    @EJB
    private InmuebleFacade inmFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ProductoEspecieLocal
     */
    @EJB
    private ProductoEspecieLocalFacade espLocalFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Producto
     */
    @EJB
    private ProductoFacade prodFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ItemProductivo
     */
    @EJB
    private ItemProductivoFacade itemFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Cosntructor
     */
    public MbAutorizacion() {
    }

    ///////////////////////
    // Métodos de acceso //
    ///////////////////////    
    public EstadoAutorizacion getEstadoSelected() {
        return estadoSelected;
    }

    public void setEstadoSelected(EstadoAutorizacion estadoSelected) {
        this.estadoSelected = estadoSelected;
    }

    public List<Guia> getLstGuias() {
        return lstGuias;
    }

    public void setLstGuias(List<Guia> lstGuias) {
        this.lstGuias = lstGuias;
    }

    public boolean isEditEstado() {
        return editEstado;
    }

    public void setEditEstado(boolean editEstado) {
        this.editEstado = editEstado;
    }

    /**
     * Método para completar el listado de estados de Autorización
     * Incluye a todos menos el actual
     * @return List<EstadoAutorizacion> listado de los estados obtenidos
     */
    public List<EstadoAutorizacion> getLstEstados() {
        int i = 0, e = 0;
        lstEstados = estadoFacade.getHabilitadosSinUno(autorizacion.getEstado().getCodigo());
        if(!guiaFacade.findByNumFuente(autNumero).isEmpty()){
            for(EstadoAutorizacion est : lstEstados){
                if(est.getNombre().equals(ResourceBundle.getBundle("/Config").getString("GuiaInicial"))){
                   e = i;
                }
                i += 1;
            }
            lstEstados.remove(e);
        }
        return lstEstados;
    }

    public void setLstEstados(List<EstadoAutorizacion> lstEstados) {
        this.lstEstados = lstEstados;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    /**
     * Método para completar el listado con las Autorizaciones existentes
     * @return List<Autorizacion> listado de las Autorizaciones
     */
    public List<Autorizacion> getListado() {
        listado = autFacade.findAll();
        return listado;
    }

    public void setListado(List<Autorizacion> listado) {
        this.listado = listado;
    }

    public List<Autorizacion> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<Autorizacion> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    /**
     * Método para obtener los ítems productivos vinculados a la autorización
     * @return List<ItemProductivo> listado de los ítems correspondientes
     */
    public List<ItemProductivo> getLstItemsAut() {
        lstItemsAut = itemFacade.getByAutorizacion(autorizacion);
        return lstItemsAut;
    }

    public void setLstItemsAut(List<ItemProductivo> lstItemsAut) {
        this.lstItemsAut = lstItemsAut;
    }

    public Producto getProdClaseSelected() {
        return prodClaseSelected;
    }

    public void setProdClaseSelected(Producto prodClaseSelected) {
        this.prodClaseSelected = prodClaseSelected;
    }

    public List<Producto> getLstProductos() {
        return lstProductos;
    }

    public void setLstProductos(List<Producto> lstProductos) {
        this.lstProductos = lstProductos;
    }

    public ProductoEspecieLocal getEspecieSelected() {
        return especieSelected;
    }

    public void setEspecieSelected(ProductoEspecieLocal especieSelected) {
        this.especieSelected = especieSelected;
    }

    public List<ProductoEspecieLocal> getLstEspecieLocal() {
        return lstEspecieLocal;
    }

    public void setLstEspecieLocal(List<ProductoEspecieLocal> lstEspecieLocal) {
        this.lstEspecieLocal = lstEspecieLocal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public boolean isViewProducto() {
        return viewProducto;
    }

    public void setViewProducto(boolean viewProducto) {
        this.viewProducto = viewProducto;
    }

    public ItemProductivo getItemAutorizado() {
        return itemAutorizado;
    }

    public void setItemAutorizado(ItemProductivo itemAutorizado) {
        this.itemAutorizado = itemAutorizado;
    }

    public String getCatastroBuscar() {
        return catastroBuscar;
    }

    public void setCatastroBuscar(String catastroBuscar) {
        this.catastroBuscar = catastroBuscar;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public boolean isViewInmueble() {
        return viewInmueble;
    }

    public void setViewInmueble(boolean viewInmueble) {
        this.viewInmueble = viewInmueble;
    }

    public boolean isViewPersona() {
        return viewPersona;
    }

    public void setViewPersona(boolean viewPersona) {
        this.viewPersona = viewPersona;
    }

    public Long getCuitBuscar() {
        return cuitBuscar;
    }

    public void setCuitBuscar(Long cuitBuscar) {
        this.cuitBuscar = cuitBuscar;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isNueva() {
        return nueva;
    }

    public void setNueva(boolean nueva) {
        this.nueva = nueva;
    }

    public SubZona getSubZonaSelected() {
        return subZonaSelected;
    }

    public void setSubZonaSelected(SubZona subZonaSelected) {
        this.subZonaSelected = subZonaSelected;
    }

    public List<SubZona> getLstSubZonasVerde() {
        return lstSubZonasVerde;
    }

    public void setLstSubZonasVerde(List<SubZona> lstSubZonasVerde) {
        this.lstSubZonasVerde = lstSubZonasVerde;
    }

    public boolean isVerdeSelected() {
        return verdeSelected;
    }

    public void setVerdeSelected(boolean verdeSelected) {
        this.verdeSelected = verdeSelected;
    }

    public boolean isAmarillaSelected() {
        return amarillaSelected;
    }

    public void setAmarillaSelected(boolean amarillaSelected) {
        this.amarillaSelected = amarillaSelected;
    }

    public List<Parametrica> getLstIntervenciones() {
        return lstIntervenciones;
    }

    public void setLstIntervenciones(List<Parametrica> lstIntervenciones) {
        this.lstIntervenciones = lstIntervenciones;
    }

    public List<Parametrica> getLstUsosSuelo() {
        return lstUsosSuelo;
    }

    public void setLstUsosSuelo(List<Parametrica> lstUsosSuelo) {
        this.lstUsosSuelo = lstUsosSuelo;
    }

    public ZonaIntervencion getZonaSelected() {
        return zonaSelected;
    }

    public void setZonaSelected(ZonaIntervencion zonaSelected) {
        this.zonaSelected = zonaSelected;
    }

    public List<ZonaIntervencion> getLstZonas() {
        return lstZonas;
    }

    public void setLstZonas(List<ZonaIntervencion> lstZonas) {
        this.lstZonas = lstZonas;
    }

    public List<SubZona> getLstSubZonasAmarilla() {
        return lstSubZonasAmarilla;
    }

    public void setLstSubZonasAmarilla(List<SubZona> lstSubZonasAmarilla) {
        this.lstSubZonasAmarilla = lstSubZonasAmarilla;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Autorizacion getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(Autorizacion autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getAutNumero() {
        return autNumero;
    }

    public void setAutNumero(String autNumero) {
        this.autNumero = autNumero;
    }
    
    
    ///////////////////////////////
    // Métodos de inicialización //
    ///////////////////////////////
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa algunas entidades y listados a gestionar al inicio, 
     * además del bean de sesión y el usuario
     */
    @PostConstruct
    public void init(){
        TipoParam tipo = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoInterv"));
        lstIntervenciones = paramFacade.getHabilitadas(tipo);
        tipo = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("UsoSuelo"));
        lstUsosSuelo = paramFacade.getHabilitadas(tipo);
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
     * productos.xhtml o estado.xhtml
     * Por defecto será "instrumento.xhtml"
     * @param strPage String vista a cargar recibida como parámetro
     */
    public void cargarFrame(String strPage){
        persona = null;
        producto = null;
        inmueble = null;
        page = strPage;
        if(strPage.equals("productos.xhtml")){
            // si se carga productos, cargo los listados
            lstEspecieLocal = espLocalFacade.getHabilitadas();
        }
        if(strPage.equals("estado.xhtml")){
            // seteo el estado seleccionado
            estadoSelected = autorizacion.getEstado();
        }
    }  
    
    /**
     * Método que busca una Autorización según el número ingresado en el formulario de búsqueda.
     * autNumero será el parámetro.
     */
    public void buscar(){
        try{
            // busco la Autorización
            autorizacion = autFacade.getExistente(autNumero.toUpperCase());
            if(autorizacion == null){
                JsfUtil.addErrorMessage("No se encontró ninguna Autorización con el Número ingresado.");
            }else{
                // redirecciono a la vista
                page = "instrumentoView.xhtml";
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error buscando la Autorización. " + ex.getMessage());
        }
    }
    
    /**
     * Método que instancia una Autorización nueva, los listados de los objetos a seleccinar y campos.
     * Renderiza el fomulario de creación/edición.
     */
    public void prepareNew(){
        nueva = true;
        limpiarForm();
    }
    
    /**
     * Método para inicializar la edición y redireccionar al formlario de edición de Autorización
     */
    public void prepareEdit(){
        edit = true;
        cargarZonas();
        page = "instrumento.xhtml";
    }
    
    /**
     * Método para redireccionar a la vista detalle de la Autorización
     */
    public void prepareView(){
        //reseteo todo antes de redireccionar
        edit = false;
        resetearCampos();
        page = "instrumentoView.xhtml";
    }
    
    /**
     * Método para setear una Zona como seleccionada
     * Para los casos Verde y Amarilla, carga los listados
     * de Sub Zonas respectivas para mostrarlos en la vista 
     * para su selección
     */
    public void addZona(){
        if(zonaSelected.getCodigo().equals("II")){
            amarillaSelected = true;
            cargarSubzonas("II");
        }else if(zonaSelected.getCodigo().equals("I")){
            verdeSelected = true;
            cargarSubzonas("I");
        }
        zonaSelected.setSelected(true);
    }
    
    /**
     * Método para quitar una Zona de la selección.
     * Para los casos Verde y Amarilla, limpia los listados correspondientes
     * y los quita de la vista
     */
    public void deleteZona(){
        if(zonaSelected.getCodigo().equals("II")){
            amarillaSelected = true;
            limpiarSubzonas("II");
        }else if(zonaSelected.getCodigo().equals("I")){
            verdeSelected = true;
            limpiarSubzonas("I");
        }
        zonaSelected.setSelected(false);
    }
    
    /**
     * Método para limpiar el formulario
     */
    public void limpiarForm(){
        autorizacion = new Autorizacion();
        resetearCampos();
    }
    
    /**
     * Metodo para guardar la Autorización con los datos del instrumento
     * Primero se realizan las validaciones correspondientes.
     */
    public void save(){
        boolean valida = true;
        String msgSup = "", msgZonas = "", msgTipo = "", msgRepetido = "", msgError = "";
        
        // valido superficies
        msgSup = validarSuperficies();
        if(!msgSup.equals("")){
            valida = false;
        }
        // valido que haya una zona
        msgZonas = validarZonas();
        if(!msgZonas.equals("")){
            valida = false;
        }
        
        try{
            if(!edit){
                // valido que esté configurado el tipo de autorización (se tomará la que esté registrá, deberá ser una sola)
                // solo si no edita
                TipoParam tipo = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoAut"));
                List<Parametrica> lstTipo = paramFacade.getHabilitadas(tipo);
                if(!lstTipo.isEmpty()){
                    autorizacion.setTipo(lstTipo.get(0));
                }else{
                    msgTipo = "Debe haber configurado un Tipo de Autorización habilitado.";
                    valida = false;
                }
            }
            
            // valido que no se repita el número
            Autorizacion autExitente = autFacade.getExistente(autorizacion.getNumero());
            // valido por el nombre
            if(autExitente != null){
                if(edit){
                    // si edita, no habilito si no es el mismo
                    if(!autExitente.equals(autorizacion)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    msgRepetido = "Ya existe una Autorización con el número que está ingresando";
                    valida = false;
                }
            }
            
            // si valida continúo
            if(valida){
                // seteo la Provincia
                autorizacion.setProvincia(ResourceBundle.getBundle("/Config").getString("Provincia"));
                // pongo los números de Expediente e Instrumento en mayúsculas
                String numExp = autorizacion.getNumExp();
                autorizacion.setNumExp(numExp.toUpperCase());
                String numInst = autorizacion.getNumero();
                autorizacion.setNumero(numInst.toUpperCase());
                // cargo las zonas y subzonas
                List<ZonaIntervencion> lstTemZonas = new ArrayList<>();
                for(ZonaIntervencion z : lstZonas){
                    if(z.isSelected()){
                        lstTemZonas.add(z);
                    }
                }
                autorizacion.setZonas(lstTemZonas);
                
                List<SubZona> lstTemSubZonas = new ArrayList<>();
                if(!lstSubZonasAmarilla.isEmpty()){
                    for(SubZona sz : lstSubZonasAmarilla){
                        if(sz.isSelected()){
                            lstTemSubZonas.add(sz);
                        }
                    }
                }
                if(!lstSubZonasVerde.isEmpty()){
                    for(SubZona sz : lstSubZonasVerde){
                        if(sz.isSelected()){
                            lstTemSubZonas.add(sz);
                        }
                    }
                }
                autorizacion.setSubZonas(lstTemSubZonas);
                
                // seteo los datos faltantes
                autorizacion.setUsuario(usLogueado);
                if(edit){
                    // actualizo
                    autFacade.edit(autorizacion);
                    edit = false;
                    // envío mensaje de exito
                    JsfUtil.addSuccessMessage("La " + ResourceBundle.getBundle("/Config").getString("Instrumento") + " se ha actualizado con exito.");
                }else{
                    // estado (se tomará "Carga inicial")
                    EstadoAutorizacion estado = estadoFacade.getExistenteByCodigo(ResourceBundle.getBundle("/Config").getString("AutInicial"));
                    autorizacion.setEstado(estado);
                    // fecha de alta
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    autorizacion.setFechaAlta(fechaAlta);
                    // inserto
                    autFacade.create(autorizacion);
                    nueva = false;
                    // envío mensaje de exito
                    JsfUtil.addSuccessMessage("La " + ResourceBundle.getBundle("/Config").getString("Instrumento") + " se ha registrado con exito.");
                }

                // reseteo los campos del formulario
                resetearCampos();
                
                // redirecciono a la vista
                page = "instrumentoView.xhtml";
            }else{
                // armo el mensaje de error
                if(!msgSup.equals("")){
                    msgError = msgSup;
                }
                
                if(!msgRepetido.equals("") && msgError.equals("")){
                    msgError = msgRepetido;
                }else{
                    msgError = msgError + " " + msgRepetido;
                }

                if(!msgZonas.equals("") && msgError.equals("")){
                    msgError = msgZonas;
                }else{
                    msgError = msgError + " " + msgZonas;
                }

                if(!msgTipo.equals("") && msgError.equals("")){
                    msgError = msgTipo;
                }else{
                    msgError = msgError + " " + msgTipo;
                }
                JsfUtil.addErrorMessage("No se registró la " + ResourceBundle.getBundle("/Config").getString("Instrumento") + " Error: " + msgError);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error guardando la Autorización. " + ex.getMessage());
        }
    }
    
    /**
     * Método para cargar las Zonas y SubZonas respectivas para su edición
     */
    public void cargarZonas() {
        //actualizo el listado de zonas
        lstZonas = zonaFacade.getHabilitadas();
        for(ZonaIntervencion zl : lstZonas){
            for(ZonaIntervencion za : autorizacion.getZonas()){
                if(Objects.equals(zl.getId(), za.getId())){
                    zl.setSelected(true);
                }
            }
        }
        // actualizo los listados de subzonas
        try{
            // amarillas
            ZonaIntervencion zonaAmarilla = zonaFacade.getExistenteByCodigo("II");
            lstSubZonasAmarilla = subZonaFacade.getHabilitadasByZona(zonaAmarilla);
            for(SubZona subAmList : lstSubZonasAmarilla){
                for(SubZona subAmAut : autorizacion.getSubZonas()){
                    if(Objects.equals(subAmList.getId(), subAmAut.getId())){
                        subAmList.setSelected(true);
                        amarillaSelected = true;
                    }
                }
            }
            // verdes
            ZonaIntervencion zonaVerde = zonaFacade.getExistenteByCodigo("I");
            lstSubZonasVerde = subZonaFacade.getHabilitadasByZona(zonaVerde);
            for(SubZona subVerList : lstSubZonasVerde){
                for(SubZona subVerAut : autorizacion.getSubZonas()){
                    if(Objects.equals(subVerList.getId(), subVerAut.getId())){
                        subVerList.setSelected(true);
                        verdeSelected = true;
                    }
                }
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error cargando las Sub zonas. " + ex.getMessage());
        }
    }    
    
    ///////////////////////////
    // Métodos para personas //
    ///////////////////////////
    /**
     * Método para buscar una persona registrada localmente según un rol determinado
     * @param rolPersona String Rol de la persona a buscar.
     */
    public void buscarPersona(String rolPersona){
        TipoParam tipoParam = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        Parametrica rolProp = paramFacade.getExistente(rolPersona, tipoParam);
        persona = perFacade.getExistente(cuitBuscar, rolProp);
        if(persona == null){
            JsfUtil.addErrorMessage("No se registra un " + rolPersona + " con el CUIT ingresado.");
        }        
    }
    
    /**
     * Método para agregar una Persona a la Autorización
     * Válido para Proponentes, Técnicos y Apoderados
     * @param rolPersona String Rol de la Persona a asociar
     */
    public void addPersona(String rolPersona){
        boolean valida = true;

        // valido que la persona no esté vinculada ya con el mismo rol
        for(Persona per : autorizacion.getPersonas()){
            if(Objects.equals(per.getCuit(), persona.getCuit()) && Objects.equals(per.getRolPersona(), persona.getRolPersona())){
                valida = false;
                JsfUtil.addErrorMessage("El " + rolPersona + " que está tratando de asociar, ya está vinculada a la Autorización con el mismo rol.");
            }
        }
        try{
            if(valida){
                autorizacion.getPersonas().add(persona);
                // guardo temporalmente las personas
                List<Persona> perTemp = new ArrayList<>();
                for(Persona per : autorizacion.getPersonas()){
                    perTemp.add(per);
                }
                // limpio lo guardado
                autorizacion.setPersonas(null);
                autFacade.edit(autorizacion);
                // seteo y guardo las personas definitivas
                autorizacion.setPersonas(perTemp);
                autFacade.edit(autorizacion);
                // limpio todo
                persona = null;
                cuitBuscar = null;
                JsfUtil.addSuccessMessage("El " + rolPersona + " se agregó a la Autorización");
            }
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error asignando al " + rolPersona + ". " + ex.getMessage());
        }
    }
    
    /**
     * Método para desvincular una Persona a la Autorización
     * Válido para Proponentes, Técnicos y Apoderados
     * @param rolPersona String Rol de la Persona a desvincular
     */
    public void deletePersona(String rolPersona){
        int i = 0, j = 0;
        try{
            for(Persona per: autorizacion.getPersonas()){
                if(Objects.equals(per.getCuit(), persona.getCuit()) && Objects.equals(per.getRolPersona(), persona.getRolPersona())){
                    j = i;
                }
                i = i+= 1;
            }
            autorizacion.getPersonas().remove(j);
            autFacade.edit(autorizacion);
            persona = null;
            viewPersona = false;
            cuitBuscar = null;
            JsfUtil.addSuccessMessage("El " + rolPersona + " se desvinculó a la Autorización");
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error desvinculando al " + rolPersona + ". " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar la vista detalle de la Persona
     * Para todos los roles
     */
    public void prepareViewPersona(){
        viewPersona = true;
    }
    
    
    /**
     * Método para limpiar la vista detalle de la Persona
     * Para todos los roles
     */
    public void limpiarViewPersona(){
        cuitBuscar = null;
        persona = null;
    }
    
    ////////////////////////////
    // Métodos para Inmuebles //
    ////////////////////////////
    /**
     * Método para buscar un Inmueble según el id Catastral ingresado.
     */
    public void buscarInmueble(){
        inmueble = inmFacade.getExistenteByCatastro(catastroBuscar.toUpperCase());
        if(inmueble == null){
            JsfUtil.addErrorMessage("No se registra un " + ResourceBundle.getBundle("/Config").getString("Inmueble") + " con el id Catastral ingresado.");
        }   
    }
    
    /**
     * Método para agregar un Inmueble a la Autorización previa validación de un posible vínculo anterior
     */
    public void addInmueble(){
        boolean valida = true;

        // valido que la persona no esté vinculada ya con el mismo rol
        for(Inmueble inm : autorizacion.getInmuebles()){
            if(Objects.equals(inm.getIdCatastral(), inmueble.getIdCatastral())){
                valida = false;
                JsfUtil.addErrorMessage("El " + ResourceBundle.getBundle("/Config").getString("Inmueble") + " que está tratando de asociar, ya está vinculado a la Autorización con el mismo rol.");
            }
        }
        try{
            if(valida){
                autorizacion.getInmuebles().add(inmueble);
                // guardo temporalmente los inmuebles
                List<Inmueble> inmTemp = new ArrayList<>();
                for(Inmueble inm : autorizacion.getInmuebles()){
                    inmTemp.add(inm);
                }
                // limpio lo guardado
                autorizacion.setInmuebles(null);
                autFacade.edit(autorizacion);
                // seteo y guardo los inmuebles definitivos
                autorizacion.setInmuebles(inmTemp);
                autFacade.edit(autorizacion);
                // limpio todo
                inmueble = null;
                catastroBuscar = null;
                JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Inmueble") + " se agregó a la Autorización");
            }
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error asignando al " + ResourceBundle.getBundle("/Config").getString("Inmueble") + ". " + ex.getMessage());
        }
    }    
    
    /**
     * Método para desvincular un Inmueble a la Autorización
     */
    public void deleteInmueble(){
        int i = 0, j = 0;
        try{
            for(Inmueble inm: autorizacion.getInmuebles()){
                if(Objects.equals(inm.getIdCatastral(), inmueble.getIdCatastral())){
                    j = i;
                }
                i = i+= 1;
            }
            autorizacion.getInmuebles().remove(j);
            autFacade.edit(autorizacion);
            inmueble = null;
            viewInmueble = false;
            catastroBuscar = null;
            JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Inmueble") + " se desvinculó a la Autorización");
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error desvinculando al " + ResourceBundle.getBundle("/Config").getString("Inmueble") + ". " + ex.getMessage());
        }
    } 
    
    /**
     * Método para habilitar la vista detalle del Inmueble
     */
    public void prepareViewInmueble(){
        viewInmueble = true;
    }  
    
    /**
     * Método para limpiar la vista detalle del Inmueble
     * Para todos los roles
     */
    public void limpiarViewInmueble(){
        catastroBuscar = null;
        inmueble = null;
    } 
    
    ////////////////////////////
    // Métodos para Productos //
    //////////////////////////// 

    /**
     * Método que se dispara al seleccinoar una especie local y obtiene los productos vinculados a ella
     */
    public void especieChangeListener(){
        lstProductos = prodFacade.getByEspecieLocal(especieSelected);
    }
    
    /**
     * Método para buscar un Producto según la Especie local y clase seleccionadas.
     */
    public void buscarProducto(){  
        producto = prodFacade.getExistente(prodClaseSelected.getClase(), especieSelected);
        itemAutorizado = new ItemProductivo();
        // seteo el itemAutorizado
        itemAutorizado.setAutorizacion(autorizacion);
        itemAutorizado.setClase(producto.getClase().getNombre());
        itemAutorizado.setIdProd(producto.getId());
        itemAutorizado.setNombreCientifico(producto.getEspecieLocal().getNombreCientifico());
        itemAutorizado.setNombreVulgar(producto.getEspecieLocal().getNombreVulgar());
        itemAutorizado.setUnidad(producto.getClase().getUnidad().getNombre());
        itemAutorizado.setIdEspecieTax(producto.getEspecieLocal().getIdTax());
        itemAutorizado.setKilosXUnidad(producto.getEquivalKg());
        if(producto == null){
            JsfUtil.addErrorMessage("No se registra un " + ResourceBundle.getBundle("/Config").getString("Producto") + " para la Especie local y Clase seleccionadas.");
        }   
    }
    
    /**
     * Método para agregar un Producto a la Autorización
     * Lo que constituye un Item autorizado (ItemProductivo)
     */
    public void addProducto(){
        boolean valida = true;

        // valido que el producto no esté asociado ya a la Autorización
        List<ItemProductivo> items = itemFacade.getByAutorizacion(autorizacion);
        for(ItemProductivo itm : items){
            if(Objects.equals(itm.getNombreVulgar(), itemAutorizado.getNombreVulgar()) && Objects.equals(itm.getClase(), itemAutorizado.getClase())){
                valida = false;
                JsfUtil.addErrorMessage("El " + ResourceBundle.getBundle("/Config").getString("Producto") + " que está tratando de asociar, ya está vinculado a la Autorización.");
            }
        }
        
        try{
            if(valida){
                // seteo el tipoActual (item Autorizado) (tipoOrigen e itemOrigen van nulos
                Parametrica tipoActual = obtenerParametro(ResourceBundle.getBundle("/Config").getString("TipoItem"), ResourceBundle.getBundle("/Config").getString("Autorizados"));
                if(tipoActual != null){
                    itemAutorizado.setTipoActual(tipoActual);
                    // seteo los datos de inserción
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    itemAutorizado.setFechaAlta(fechaAlta);
                    // seteo el Usuario de registro
                    itemAutorizado.setUsuario(usLogueado);
                    // se habilitará cuando se habilite la Autorización (NO SE)
                    itemAutorizado.setHabilitado(true);
                    // seteo el saldo igual al total
                    itemAutorizado.setSaldo(itemAutorizado.getTotal());
                    // seteo el equivalente total en Kg.
                    itemAutorizado.setTotalKg(itemAutorizado.getTotal() * itemAutorizado.getKilosXUnidad());
                    // seteo el equivalente en Kg. del saldo
                    itemAutorizado.setSaldoKg(itemAutorizado.getTotalKg());
                    // seteo el código de origen del Producto
                    itemAutorizado.setCodigoProducto(crearCodigoProducto());

                    // inserto
                    itemFacade.create(itemAutorizado);

                    // reseteo los campos
                    itemAutorizado = null;
                    producto = null;
                    especieSelected = new ProductoEspecieLocal();
                    prodClaseSelected = new Producto();

                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Producto") + " se agregó a la Autorización");
                }else{
                    JsfUtil.addErrorMessage("No se pudo encontrar un Parámetro para el Tipo de Item: 'Autorizados'.");
                }
            }
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error asignando al " + ResourceBundle.getBundle("/Config").getString("Producto") + ". " + ex.getMessage());
        }
    }      
    
    /**
     * Método para desvincular un Producto a la Autorización
     * Elimina el Item Autorizado
     */
    public void deleteProducto(){
        try{
            itemFacade.remove(itemAutorizado);
            itemAutorizado = null;
            producto = null;
            viewProducto = false;
            especieSelected = new ProductoEspecieLocal();
            prodClaseSelected = new Producto();
            JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Producto") + " se desvinculó a la Autorización");
        }catch(NumberFormatException ex){
            JsfUtil.addErrorMessage("Hubo un error desvinculando al " + ResourceBundle.getBundle("/Config").getString("Producto") + ". " + ex.getMessage());
        }
    }   
    
    /**
     * Método para habilitar la vista detalle del Producto
     */
    public void prepareViewProducto(){
        viewProducto = true;
    }  
    
    /**
     * Método para limpiar la vista detalle del Producto
     * Para todos los roles
     */
    public void limpiarViewProducto(){
        especieSelected = new ProductoEspecieLocal();
        prodClaseSelected = new Producto();
        itemAutorizado = null;
        producto = null;
    }   
    
    /**
     * Método para editar un item
     */
    public void editProducto(){
        // seteo el Usuario de registro
        itemAutorizado.setUsuario(usLogueado);
        // seteo el saldo igual al total
        itemAutorizado.setSaldo(itemAutorizado.getTotal());
        // seteo el equivalente total en Kg.
        itemAutorizado.setTotalKg(itemAutorizado.getTotal() * itemAutorizado.getKilosXUnidad());
        // seteo el equivalente en Kg. del saldo
        itemAutorizado.setSaldoKg(itemAutorizado.getTotalKg());
        try{
            // actualizo
            itemFacade.edit(itemAutorizado);

            // reseteo los campos
            itemAutorizado = null;
            producto = null;
            especieSelected = new ProductoEspecieLocal();
            prodClaseSelected = new Producto();
            viewProducto = false;

            JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Producto") + " se actualizó correctamente.");
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error actualizando el " + ResourceBundle.getBundle("/Config").getString("Producto") + ". " + ex.getMessage());
        }
    }
    
    //////////////////////////////////////////////////////////
    // Métodos para actualizar el Estado de la Autorización //
    //////////////////////////////////////////////////////////
    /**
     * Método que actualiza el estado, solo se ejecutará si la Autorización está completa
     */
    public void actualizarEstado(){
        boolean valida = true;
        String result;
        // si el estado seleccionado implica habilitación para la emisión de Guías
        // verifico que la Autorización esté completa
        if(estadoSelected.isHabilitaEmisionGuia()){
            result = validarAutCompleta();
            if(!result.equals("")){
                valida = false;
                JsfUtil.addErrorMessage("No es posible aplicar este Estado. " + result);
            }
        }
        if(valida){
            try{
                // actualizo la Autorización
                autorizacion.setUsuario(usLogueado);
                autorizacion.setEstado(estadoSelected);
                autFacade.edit(autorizacion);
                editEstado = false;
                estadoSelected = null;
                JsfUtil.addSuccessMessage("Se actualizó el estado de la Autorización correctamente.");
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error actualizando el " + ResourceBundle.getBundle("/Config").getString("Producto") + ". " + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para habilitar la edición del Estado
     */
    public void prepareEditEstado(){
        editEstado = true;
    }
    
    /**
     * Método para cancelar la edición del estado
     */
    public void cancelarEditEstado(){
        editEstado = false;
    }
    
    ///////////////////////////////////////////////
    // Métodos para el listado de Autorizaciones //
    ///////////////////////////////////////////////
    /**
     * Método para inicializar el listado Autorizaciones
     */
    public void prepareList(){
        autorizacion = null;
        view = false;
    }
    
    /**
     * Método que prepara la vista detalle de la Autorización
     * Incluye las Guías para las que sirvió de fuente y, si correspondiera,
     * las Guías que tuvueron como fuente una Guía de las anteriores.
     */
    public void prepareViewDetalle(){
        // Busco las Guías de las que pudiera ser fuente la Autorización
        lstGuias = guiaFacade.findByNumFuente(autorizacion.getNumero());
        // Busco Guías que hayan tenido como fuente a las Guías obtenidas de la Autorización
        List<Guia> lstHijas = new ArrayList<>();
        for(Guia gm : lstGuias){
            for(Guia gh : guiaFacade.findByNumFuente(gm.getCodigo())){
                lstHijas.add(gh);
            }
        }
        // si hubo más Guías, las agrego
        if(!lstHijas.isEmpty()){
            for(Guia g : lstHijas){
                lstGuias.add(g);
            }
        }
        view = true;
    }
    
    
    //////////////////////
    // Métodos privados //
    //////////////////////

    /**
     * Método privado para cargar el listado de las Sub Zonas correspondientes
     * a la Zona seleccionada
     */
    private void cargarSubzonas(String codZona) {
        try{
            if(codZona.equals("I")){
                lstSubZonasVerde = subZonaFacade.getHabilitadasByZona(zonaSelected);
            }else{
                lstSubZonasAmarilla = subZonaFacade.getHabilitadasByZona(zonaSelected);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error cargando las Sub zonas. " + ex.getMessage());
        }

    }

    /**
     * Método privado para limpiar el listado de las Sub Zonas de la Zona que se eliminó de la selección
     */
    private void limpiarSubzonas(String codZona) {
        if(codZona.equals("I")){
            lstSubZonasVerde.clear();
            verdeSelected = false;
        }else{
            lstSubZonasAmarilla.clear();
            amarillaSelected = false;
        }
    }
    
    /**
     * Método privado para setear una Sub Zona como seleccionada
     */    
    public void addSubZona(){
        subZonaSelected.setSelected(true);
    }    
    
    /**
     * Método privado para eliminar una Sub Zona de la selección
     */
    public void deleteSubZona(){
        subZonaSelected.setSelected(false);
    }    

    /**
     * Método privado que valida que la superficie solicitada no sea mayor a la total y la autorizada no sea mayor a la solicitada.
     * Si hay error devuelve un string con el mensaje correspondiente. Utilizado en save()
     * @return 
     */
    private String validarSuperficies() {
        String result = "";
        if(autorizacion.getSupTotal() == 0 || autorizacion.getSupSolicitada() == 0 || autorizacion.getSupAutorizada() == 0){
            result = "Las tres superficies deben tener un valo mayor a 0.";
        }
        if(autorizacion.getSupAutorizada() > autorizacion.getSupSolicitada()){
            if(result.equals("")) result = "La superficie autorizada no puede ser mayor que la solicitada.";
            else result = result + " La superficie autorizada no puede ser mayor que la solicitada.";
        }
        if(autorizacion.getSupSolicitada() > autorizacion.getSupTotal()){
            if(result.equals("")) result = "La superficie solicitada no puede ser mayor que la total.";
            else result = result + " La superficie solicitada no puede ser mayor que la total.";
        }
        return result;
    }

    /**
     * Método privado que valida que se haya seleccionado al menos una zona para el predio
     * Si hay error devuelve un string con el mensaje correspondiente. Utilizado en save()
     * @return 
     */
    private String validarZonas() {
        int zonas = 0;
        for(ZonaIntervencion z : lstZonas){
            if(z.isSelected()) zonas += 1;
        }
        if(zonas == 0) return "Debe seleccionar al menos una Zona para el predio.";
        else return "";
    }

    /**
     * Método privado que resetea los campos del formulario.
     * Utilizado en prepareView(), limpiarForm() y save()
     */
    private void resetearCampos() {
        lstZonas = zonaFacade.getHabilitadas();
        zonaSelected = new ZonaIntervencion();
        amarillaSelected = false;
        verdeSelected = false;
        subZonaSelected = new SubZona();
        lstSubZonasAmarilla = new ArrayList<>();
        lstSubZonasVerde = new ArrayList<>();
    }

    /**
     * Método privado para validar que una Autorización tenga los datos mínimos para poder ser habilitada
     * para emitir una Guía. Utilizado en actualizarEstado()
     * @return 
     */
    private String validarAutCompleta() {
        String result = "";
        if(autorizacion.getLstProponentes().isEmpty()){
            if(result.equals("")){
                result = "Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Proponente") + ".";
            }else{
                result = result + " Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Proponente");
            }
        }
        if(autorizacion.getLstTecnicos().isEmpty()){
            if(result.equals("")){
                result = "Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Tecnico") + ".";
            }else{
                result = result + " Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Tecnico");
            }
        }
        if(autorizacion.getInmuebles().isEmpty()){
            if(result.equals("")){
                result = "Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Inmueble") + ".";
            }else{
                result = result + " Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Inmueble");
            }
        }
        if(itemFacade.getByAutorizacion(autorizacion).isEmpty()){
            if(result.equals("")){
                result = "Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Producto") + ".";
            }else{
                result = result + " Debe registrar al menos un " + ResourceBundle.getBundle("/Config").getString("Producto");
            }
        }
        return result;
    }
    
    /**
     * Método privado para obtener una Paramétrica según su nombre y nombre del Tipo.
     * Utilizado en addProducto()
     * @param nomTipo String nombre del Tipo de Paramétrica
     * @param nomParam String nombre de la Paramétrica
     * @return Parametrica paramétrica correspondiente a los parámetros remitidos
     */
    private Parametrica obtenerParametro(String nomTipo, String nomParam) {
        TipoParam tipo = tipoParamFacade.getExistente(nomTipo);
        return paramFacade.getExistente(nomParam, tipo);
    }    

    /**
     * Método privado para crear el código de origen del Producto que se compone de 
     * una cadena de elementos separados por '|' en este orden
     * nombreCientifico: nombre científico de la Especie constituido por 'Género/Especie'
     * nombreVulgar: nombre vulgar de la Especie definido de manera local
     * clase: clase en la que se comercializa el Producto definido de manera local
     * unidad: unidad de medida en la que se comercializa el Producto/Clase definido de manera local
     * resolución: numero de la resolución (campo numero de la entidad Autorización)
     * provincia: nombre de la Provincia dentro de la cual se extraerá el Producto
     * EJ.:"1|Prosopis caldenia|Caldén|Rollo|Unidad|123-DGB-2017|Santiago del Estero"
     * Utilizado en addProducto()
     * @return String código del producto generado
     */
    private String crearCodigoProducto() {
        String codigo;
        codigo = String.valueOf(itemAutorizado.getId()) + "|"
                + itemAutorizado.getNombreCientifico() + "|"
                + itemAutorizado.getNombreVulgar() + "|"
                + itemAutorizado.getClase() + "|"
                + itemAutorizado.getUnidad() + "|"
                + itemAutorizado.getAutorizacion().getNumero() + "|"
                + itemAutorizado.getAutorizacion().getProvincia();
        return codigo;
    }
}
