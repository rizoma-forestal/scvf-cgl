
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
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
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
 * de la Resolución autorizante
 * @author rincostante
 */
public class MbAutorizacion {

    private String page = "instrumento.xhtml";
    private Autorizacion autorizacion;
    private List<Autorizacion> listado;
    private List<Autorizacion> listadoFilter;
    private String autNumero;
    private boolean nueva;
    private boolean edit;
    private boolean view;
    private boolean editEstado;
    private MbSesion sesion;
    private Usuario usLogueado;
    
    /**
     * Listado de Tipos de Intervención autorizada
     */
    private List<Parametrica> lstIntervenciones;
    
    /**
     * Uso del suelo autorizado para el predio
     */
    private List<Parametrica> lstUsosSuelo;
    
    /**
     * Listado de Guías vinculadas a la Autorización
     */
    private List<Guia> lstGuias;
    
    /**
     * Zona seleccionada según ordenamiento ambiental
     */
    private ZonaIntervencion zonaSelected;
    private List<ZonaIntervencion> lstZonas;
    private boolean amarillaSelected;
    private boolean verdeSelected;
    
    /**
     * Sub Zonas vinculadas a la Autorización
     * En función de la Zona
     */
    private SubZona subZonaSelected;
    private List<SubZona> lstSubZonasAmarilla;
    private List<SubZona> lstSubZonasVerde;
    
    /*********************************
     * Gestión de objetos a agregar **
     *********************************/
    /**
     * Personas
     */
    private Long cuitBuscar;
    private Persona persona;
    private boolean viewPersona;
    /**
     * Inmuebles
     */
    private String catastroBuscar;
    private Inmueble inmueble;
    private boolean viewInmueble;
    /**
     * Productos
     */
    private List<ProductoEspecieLocal> lstEspecieLocal;
    private ProductoEspecieLocal especieSelected;
    private List<Producto> lstProductos;
    private Producto prodClaseSelected;
    private Producto producto;
    private boolean viewProducto;
    private ItemProductivo itemAutorizado;
    private List<ItemProductivo> lstItemsAut;
    /**
     * Estado
     */
    private List<EstadoAutorizacion> lstEstados;
    
    /********************
     * Accesos a datos **
     ********************/
    @EJB
    private AutorizacionFacade autFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    @EJB
    private ZonaIntervencionFacade zonaFacade;
    @EJB
    private SubZonaFacade subZonaFacade;
    @EJB
    private EstadoAutorizacionFacade estadoFacade;
    @EJB
    private PersonaFacade perFacade;
    @EJB
    private InmuebleFacade inmFacade;
    @EJB
    private ProductoEspecieLocalFacade espLocalFacade;
    @EJB
    private ProductoFacade prodFacade;
    @EJB
    private ItemProductivoFacade itemFacade;
    @EJB
    private GuiaFacade guiaFacade;
    
    public MbAutorizacion() {
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
    
    
    /******************************
     * Métodos de inicialización **
     ******************************/
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
    
    /***********************
     * Métodos operativos **
     ***********************/   
    /**
     * Método que carga la vista que se mostrará en el iframe complementario
     * Por defecto será "instrumento.xhtml"
     * @param strPage : vista a cargar recibida como parámetro
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
                if(!lstTemSubZonas.isEmpty()){
                    autorizacion.setSubZonas(lstTemSubZonas);
                }
                
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
     * Método para cargar las Zonas y SubZonas para su edición
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
    
    /**************************
     * Métodos para personas **
     **************************/
    /**
     * @param rolPersona : Rol de la persona a buscar.
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
     * Vávlido para Proponentes, Técnicos y Apoderados
     * @param rolPersona : Rol de la Persona a asociar
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
                autFacade.edit(autorizacion);
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
     * Vávlido para Proponentes, Técnicos y Apoderados
     * @param rolPersona : Rol de la Persona a desvincular
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
    
    /***************************
     * Métodos para Inmuebles **
     ***************************/    
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
     * Método para agregar un Inmueble a la Autorización
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
                autFacade.edit(autorizacion);
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
    
    /***************************
     * Métodos para Productos **
     ***************************/ 

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
    
    /********************************************************
     * Métodos para actualizar el Estado de la Autorización **
     ********************************************************/
    public void actualizarEstado(){
        boolean valida = true;
        String result;
        // si el estado seleccionado implica habilitación para la emisión de Guías
        // verifico que la Autorización esté completa
        if(autorizacion.getEstado().isHabilitaEmisionGuia()){
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
                autFacade.edit(autorizacion);
                editEstado = false;
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
    
    /**********************************************
     * Métodos para el listado de Autorizaciones **
     **********************************************/
    /**
     * Método para inicializar el listado Autorizaciones
     */
    public void prepareList(){
        autorizacion = null;
        view = false;
    }
    
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
    
    
    /*********************
     * Métodos privados **
     *********************/

    /**
     * Método para cargar el listado de las Sub Zonas correspondientes
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
     * Método para limpiar el listado de las Sub Zonas de la Zona que se eliminó de la selección
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
     * Método para setear una Sub Zona como seleccionada
     */    
    public void addSubZona(){
        subZonaSelected.setSelected(true);
    }    
    
    /**
     * Método para eliminar una Sub Zona de la selección
     */
    public void deleteSubZona(){
        subZonaSelected.setSelected(false);
    }    

    /**
     * Método que valida que la superficie solicitada no sea mayor a la total y la autorizada no sea mayor a la solicitada.
     * Si hay error devuelve un string con el mensaje correspondiente.
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
     * Método que valida que se haya seleccionado al menos una zona para el predio
     * Si hay error devuelve un string con el mensaje correspondiente.
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
     * Método que resetea los campos del formulario
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
     * Método para validar que una Autorización tenga los datos mínimos para poder ser habilitada
     * para emitir una Guía
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
     * Método para obtener una Paramétrica según su nombre y nombre del Tipo
     * @param nomTipo : nombre del Tipo de Paramétrica
     * @param nomParam : nombre de la Paramétrica
     * @return 
     */
    private Parametrica obtenerParametro(String nomTipo, String nomParam) {
        TipoParam tipo = tipoParamFacade.getExistente(nomTipo);
        return paramFacade.getExistente(nomParam, tipo);
    }    

    /**
     * Método para crear el código de origen del Producto que se compone de 
     * una cadena de elementos separados por '|' en este orden
     * nombreCientifico: nombre científico de la Especie constituido por 'Género/Especie'
     * nombreVulgar: nombre vulgar de la Especie definido de manera local
     * clase: clase en la que se comercializa el Producto definido de manera local
     * unidad: unidad de medida en la que se comercializa el Producto/Clase definido de manera local
     * resolución: numero de la resolución (campo numero de la entidad Autorización)
     * provincia: nombre de la Provincia dentro de la cual se extraerá el Producto
     * EJ.:"1|Prosopis caldenia|Caldén|Rollo|Unidad|123-DGB-2017|Santiago del Estero"
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
