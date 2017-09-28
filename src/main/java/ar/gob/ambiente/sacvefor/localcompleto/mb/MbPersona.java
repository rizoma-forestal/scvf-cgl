
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.PersonaClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoEntidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoSociedadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.servicios.rue.Domicilio;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoEntidad;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoPersona;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoSociedad;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Bean de respaldo para las entidades Persona, cuyos roles podrán ser:
 * Proponente
 * Técino
 * Apoderado
 * Productor
 * Destinatario
 * etc.
 * @author rincostante
 */
public class MbPersona implements Serializable {

    // campos para gestionar
    private Persona persona;
    private List<Persona> lstProp;
    private List<Persona> listPropFilter;
    private List<Persona> lstTecnicos;
    private List<Persona> listTecFilter;
    private List<Persona> lstApod;
    private List<Persona> listApodFilter;
    private List<Persona> lstDestinatarios;
    private List<Persona> lstDestFilter;
    private List<Persona> lstTranpostistas;
    private List<Persona> lstTranspFilter;
    private boolean view;
    private boolean edit;
    private static final Logger logger = Logger.getLogger(MbPersona.class.getName());
    private List<Persona> lstRevisions; 
    private Long cuitBusqRue;
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona personaRue;
    private boolean subeMartillo;
    private MbSesion sesion;
    private Usuario usLogueado;
    
    // inyección de recursos
    @EJB
    private PersonaFacade perFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    @EJB
    private AutorizacionFacade autFacade;
    @EJB
    private GuiaFacade guiaFacade;
    
    // Clientes REST para la gestión del API de Personas
    private PersonaClient personaClient;  
    private TipoEntidadClient tipoEntClient;
    private TipoSociedadClient tipoSocClient;
    // Clientes REST para la gestión del API Territorial
    private ProvinciaClient provClient;    
    private DepartamentoClient deptoClient;
    private LocalidadClient locClient;
    
    /**
     * Campos para la gestión de las Entidades provenientes de la API
     * RUE en los combos del formulario.
     * Las Entidades de servicio se componen de un par {id | nombre}
     */   
    private List<EntidadServicio> listTipoEntidad;
    private EntidadServicio tipoEntidadSelected;
    private List<EntidadServicio> listTipoSoc;
    private EntidadServicio tipoSocSelected;  
    
    /**
     * Campos para la gestión de las Entidades provenientes de la API
     * Territorial en los combos del formulario.
     */  
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;  
    
    /**
     * Campos para el seteo de Personas
     */
    // Persona
    private TipoPersona rueTipoPers;
    private String rueNombreCompleto; 
    private String rueRazonSocial;
    private Long rueCuit;
    private String rueCorreoElectronico;
    // Domicilio
    private String rueCalle;
    private String rueNumero;
    private String ruePiso;
    private String rueDepto;
    private boolean rueEditable;
    
    /**
     * Listados por persona
     */
    // Autorizaciones
    private List<Autorizacion> lstAut;
    
    // Guías
    private List<Guia> lstGuias;
    
    /**
     * Constructor
     */        
    public MbPersona() {
    }
    
    /**********************
     * Métodos de acceso **
     **********************/      
    public List<Autorizacion> getLstAut() {
        return lstAut;
    }

    public void setLstAut(List<Autorizacion> lstAut) {
        this.lstAut = lstAut;
    }

    public List<Guia> getLstGuias() {
        return lstGuias;
    }
   
    public void setLstGuias(List<Guia> lstGuias) {
        this.lstGuias = lstGuias;
    }

    public List<Persona> getLstDestinatarios() {
        try{
            lstDestinatarios = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Destinatario")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Destinatarios registrados. " + ex.getMessage());
        }
        return lstDestinatarios;
    }

    public void setLstDestinatarios(List<Persona> lstDestinatarios) {
        this.lstDestinatarios = lstDestinatarios;
    }

    public List<Persona> getLstDestFilter() {
        return lstDestFilter;
    }

    public void setLstDestFilter(List<Persona> lstDestFilter) {
        this.lstDestFilter = lstDestFilter;
    }

    public List<Persona> getLstTranpostistas() {
        try{
            lstTranpostistas = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Transportista")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Transportistas registrados. " + ex.getMessage());
        }
        return lstTranpostistas;
    }

    public void setLstTranpostistas(List<Persona> lstTranpostistas) {
        this.lstTranpostistas = lstTranpostistas;
    }

    public List<Persona> getLstTranspFilter() {
        return lstTranspFilter;
    }

    public void setLstTranspFilter(List<Persona> lstTranspFilter) {
        this.lstTranspFilter = lstTranspFilter;
    }

    public LocalidadClient getLocClient() {
        return locClient;
    }
    
    public void setLocClient(LocalidadClient locClient) {
        this.locClient = locClient;
    }

    public boolean isSubeMartillo() {
        return subeMartillo;
    }
  
    public void setSubeMartillo(boolean subeMartillo) {
        this.subeMartillo = subeMartillo;
    }

    public boolean isRueEditable() {
        return rueEditable;
    }
 
    public void setRueEditable(boolean rueEditable) {
        this.rueEditable = rueEditable;
    }

    public String getRueNombreCompleto() {
        return rueNombreCompleto;
    }

    public void setRueNombreCompleto(String rueNombreCompleto) {
        this.rueNombreCompleto = rueNombreCompleto;
    }

    public String getRueRazonSocial() {
        return rueRazonSocial;
    }

    public void setRueRazonSocial(String rueRazonSocial) {
        this.rueRazonSocial = rueRazonSocial;
    }

    public Long getRueCuit() {
        return rueCuit;
    }

    public void setRueCuit(Long rueCuit) {
        this.rueCuit = rueCuit;
    }

    public String getRueCorreoElectronico() {
        return rueCorreoElectronico;
    }

    public void setRueCorreoElectronico(String rueCorreoElectronico) {
        this.rueCorreoElectronico = rueCorreoElectronico;
    }

    public String getRueCalle() {
        return rueCalle;
    }

    public void setRueCalle(String rueCalle) {
        this.rueCalle = rueCalle;
    }

    public String getRueNumero() {
        return rueNumero;
    }

    public void setRueNumero(String rueNumero) {
        this.rueNumero = rueNumero;
    }

    public String getRuePiso() {
        return ruePiso;
    }

    public void setRuePiso(String ruePiso) {
        this.ruePiso = ruePiso;
    }

    public String getRueDepto() {
        return rueDepto;
    }

    public void setRueDepto(String rueDepto) {
        this.rueDepto = rueDepto;
    }
    
    public TipoPersona getRueTipoPers() {
        return rueTipoPers;
    }
 
    public void setRueTipoPers(TipoPersona rueTipoPers) {
        this.rueTipoPers = rueTipoPers;
    }

    public List<EntidadServicio> getListProvincias() {
        if(listProvincias == null) listProvincias = new ArrayList<>();
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public List<EntidadServicio> getListDepartamentos() {
        if(listDepartamentos == null) listDepartamentos = new ArrayList<>();
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<EntidadServicio> getListLocalidades() {
        if(listLocalidades == null) listLocalidades = new ArrayList<>();
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }
       
    public List<Persona> getLstTecnicos() {
        try{
            lstTecnicos = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Tecnico")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Técnicos registrados. " + ex.getMessage());
        }
        return lstTecnicos;
    }

    public void setLstTecnicos(List<Persona> lstTecnicos) {
        this.lstTecnicos = lstTecnicos;
    }

    public List<Persona> getListTecFilter() {
        return listTecFilter;
    }

    public void setListTecFilter(List<Persona> listTecFilter) {
        this.listTecFilter = listTecFilter;
    }

    public List<Persona> getLstApod() {
        try{
            lstApod = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Apoderado")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Apoderados registrados. " + ex.getMessage());
        }
        return lstApod;
    }

    public void setLstApod(List<Persona> lstApod) {
        this.lstApod = lstApod;
    }

    public List<Persona> getListApodFilter() {
        return listApodFilter;
    }

    public void setListApodFilter(List<Persona> listApodFilter) {
        this.listApodFilter = listApodFilter;
    }

    public ar.gob.ambiente.sacvefor.servicios.rue.Persona getPersonaRue() {
        return personaRue;
    }

    public void setPersonaRue(ar.gob.ambiente.sacvefor.servicios.rue.Persona personaRue) {
        this.personaRue = personaRue;
    }

    public Long getCuitBusqRue() {
        return cuitBusqRue;
    }
      
    public void setCuitBusqRue(Long cuitBusqRue) {
        this.cuitBusqRue = cuitBusqRue;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<Persona> getLstProp() {
        try{
            lstProp = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Proponente")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Proponentes registrados. " + ex.getMessage());
        }
        return lstProp;
    }

    public void setLstProp(List<Persona> lstProp) {
        this.lstProp = lstProp;
    }

    public List<Persona> getListPropFilter() {
        return listPropFilter;
    }

    public void setListPropFilter(List<Persona> listPropFilter) {
        this.listPropFilter = listPropFilter;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public List<Persona> getLstRevisions() {
        return lstRevisions;
    }

    public void setLstRevisions(List<Persona> lstRevisions) {
        this.lstRevisions = lstRevisions;
    }

    public List<EntidadServicio> getListTipoEntidad() {
        if(listTipoEntidad == null) listTipoEntidad = new ArrayList<>();
        return listTipoEntidad;
    }

    public void setListTipoEntidad(List<EntidadServicio> listTipoEntidad) {
        this.listTipoEntidad = listTipoEntidad;
    }

    public EntidadServicio getTipoEntidadSelected() {
        return tipoEntidadSelected;
    }

    public void setTipoEntidadSelected(EntidadServicio tipoEntidadSelected) {
        this.tipoEntidadSelected = tipoEntidadSelected;
    }

    public List<EntidadServicio> getListTipoSoc() {
        if(listTipoSoc == null) listTipoSoc = new ArrayList<>();
        return listTipoSoc;
    }

    public void setListTipoSoc(List<EntidadServicio> listTipoSoc) {
        this.listTipoSoc = listTipoSoc;
    }

    public EntidadServicio getTipoSocSelected() {
        return tipoSocSelected;
    }

    public void setTipoSocSelected(EntidadServicio tipoSocSelected) {
        this.tipoSocSelected = tipoSocSelected;
    }
    
    public TipoPersona[] getTiposPersona() {
            return TipoPersona.values();
    }       
    
    /**********************
     * Métodos de inicio **
     * ********************/

    @PostConstruct
    public void init(){
        persona = new Persona();
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
    } 
    
    /**
     * Método para actualizar el listado de Departamentos al seleccionar una Provincia
     * para la inserción de Personas en el RUP
     */
    public void provinciaChangeListener(){
        localSelected = new EntidadServicio();
        if(provSelected != null){
            getDepartamentosSrv(provSelected.getId());
        }else{
            deptoSelected = new EntidadServicio();
            provSelected = new EntidadServicio();
        }
        
    }    
    
    /**
     * Método para actualizar el listado de Localidades al seleccionar un Departamento
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
    }     
    
    /**
     * Método para limpiar el formulario cuando cabia el tipo de persona
     */
    public void tipoPersChangeListener(){
        limpiarFormPerRue();
    }
    
    /***********************
     * Métodos operativos **
     ***********************/   
    
    /**
     * Método para habilitar la vista detalle del formulario
     */
    public void prepareView(){
        view = true;
        edit = false;
    }  
    
    /**
     * Método para preparar el listado de Autorizaciones por Persona según su rol
     * @param rol
     */
    public void prepareViewAut(String rol){
        lstAut = new ArrayList<>();
        Parametrica rolPersona = obtenerRol(rol);
        lstAut = autFacade.getByPersona(persona, rolPersona);
    }
    
    public void preparaViewGuias(String rol){
        lstGuias = new ArrayList<>();
        if(rol.equals(ResourceBundle.getBundle("/Config").getString("TegFuente"))){
            // obtengo las Guías de las cuales es titular
            lstGuias = guiaFacade.getByOrigen(persona.getCuit());
        }else{
            // obtengo las Guías que lo tienen como destinatario
            lstGuias = guiaFacade.getByDestino(persona.getCuit());
        }
    }

    /**
     * Método para habilitar el formulario de creación de una Persona
     */
    public void prepareNew(){
        persona = new Persona();
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar la vista edit del formulario.
     * En este caso, además se carga la personaRue con los datos obtenidos 
     * a partir del idRue de la Persona local
     * Para el resto crear y editar.
     */
    public void prepareEdit(){
        cargarPersona();
        view = false;
        edit = true;
    }
    
    /**
     * Método para mostrar los datos de la Persona registrada en el RUE
     * En la vista detalle
     */
    public void verDatosRue(){
        cargarPersona();
    }
    
    /**
     * Método para setear los datos obtenidos del RUE en la Persona
     * @param rol : Rol de la Persona a generar
     */
    public void setearPersonaByRue(String rol){
        if(personaRue != null){
            // seteo el rol de la Persona
            Parametrica rolPersona = obtenerRol(rol);
            persona.setRolPersona(rolPersona);
            // cacheo en la Persona del RUE
            persona.setIdRue(personaRue.getId());
            if(personaRue.getNombreCompleto() == null) persona.setNombreCompleto(personaRue.getRazonSocial());
            if(personaRue.getRazonSocial() == null) persona.setNombreCompleto(personaRue.getNombreCompleto());
            persona.setCuit(personaRue.getCuit());
            persona.setTipo(personaRue.getTipo().getNombre());
            if(rolPersona.getNombre().equals(ResourceBundle.getBundle("/Config").getString("Destinatario"))){
                // si es Destintatario seteo el correo electrónico
                persona.setEmail(personaRue.getCorreoElectronico());
            }
            personaRue = null;
            view = false;
            edit = true;
        }else{
            JsfUtil.addErrorMessage("Debe seleccionar una Persona del Registro Unico.");
        }
    }
    
    /**
     * Método para subir la imagen del martillo en el subdirectorio temporal
     * El subdirectorio temporal se llama "TMP"
     * Se configuran en el archivo de propiedades configurable "Config.properties"
     * @param event 
     */
    public void subirMartilloTmp(FileUploadEvent event){ 
        // subo el archivo al directorio temporal
        try{
            UploadedFile fileMartillo = event.getFile();
            String destino = ResourceBundle.getBundle("/Config").getString("SubdirTemp");
            // obtengo el nombre del archivo
            String nombreArchivo = getNombreArchivoASubir(fileMartillo);
            // si todo salió bien, procedo
            if(nombreArchivo != null){
                // si logré subir el archivo, guardo la ruta
                if(JsfUtil.copyFile(nombreArchivo, fileMartillo.getInputstream(), destino)){
                    JsfUtil.addSuccessMessage("El archivo " + fileMartillo.getFileName() + " se ha subido al servidor con el nombre " + nombreArchivo);
                    persona.setRutaArchivo(destino);
                    persona.setNombreArchivo(nombreArchivo);
                    persona.setRutaTemporal(true);
                }
                // actualizo el flag
                subeMartillo = true;
            }else{
                JsfUtil.addErrorMessage("No se pudo obtener el destino de la imagen del Martillo.");
            }
        }catch(IOException e){
            JsfUtil.addErrorMessage("Hubo un error subiendo la imagen del Martillo" + e.getLocalizedMessage());
        }
    }   

    /**
     * Método para guardar la Persona con el rol de Proponente, sea inserción o edición.
     * Previa validación
     */      
    public void saveProponente(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolProp = obtenerRol(ResourceBundle.getBundle("/Config").getString("Proponente"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolProp);
            // valido por el nombre
            if(perExitente != null){
                if(persona.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)) valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Proponente") + " con el CUIT que está registrando.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Proponente") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // procedo al guardado definitivo de la imagen del martillo
                if(saveMartillo()){
                    // si no hubo errores en el guardado definitivo del martillo, persisto la persona
                    if(persona.getId() != null){
                        perFacade.edit(persona);
                        JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Proponente") + " fue guardado con exito");
                    }else{
                        // seteo la fecha de alta, habilitado y Rols
                        Date fechaAlta = new Date(System.currentTimeMillis());
                        persona.setFechaAlta(fechaAlta);
                        persona.setHabilitado(true);
                        perFacade.create(persona);
                        JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Proponente") + " fue registrado con exito");
                    }
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Proponente") + " : " + ex.getMessage());
        }
    }  
    
    /**
     * Método para guardar la Persona con el rol de Técnico, sea inserción o edición.
     * Previa validación
     */      
    public void saveTecnico(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolTec = obtenerRol(ResourceBundle.getBundle("/Config").getString("Tecnico"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolTec);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)) valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " con el CUIT que está registrando.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona sea edición o inserción
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar la Persona con el rol de Apoderado, sea inserción o edición.
     * Previa validación
     */      
    public void saveApoderado(){
        boolean valida = true;
        String mensaje = "";
        Parametrica rolApod = obtenerRol(ResourceBundle.getBundle("/Config").getString("Apoderado"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolApod);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)) valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " con el CUIT que está registrando.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según se trata de una edición o inserción
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " : " + ex.getMessage());
        }
    }  
    
    /**
     * Método para guardar la Persona con el rol de Destinatario, sea inserción o edición.
     * Previa validación
     */      
    public void saveDestinatario(){
        boolean valida = true;
        String mensaje = "";
        Parametrica rolDest = obtenerRol(ResourceBundle.getBundle("/Config").getString("Destinatario"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolDest);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)) valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " con el CUIT que está registrando.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según sea edición o insercion
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " : " + ex.getMessage());
        }
    }   
    
    /**
     * Método para guardar la Persona con el rol de Transportista, sea inserción o edición.
     * Previa validación
     */      
    public void saveTransportista(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolDest = obtenerRol(ResourceBundle.getBundle("/Config").getString("Transportista"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolDest);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)) valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Transportista") + " con el CUIT que está registrando.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Transportista") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según sea edición o insercion
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Transportista") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Transportista") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Transportista") + " : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para persistir una Persona en la API RUE, validando los datos, tanto para edición como insert
     * @param rolPersona : Rol de la Persona que se va a persistir. Si bien no es necesario para el insert,
     * sí lo es para la actualización.
     */
    public void savePerRue(String rolPersona){
        boolean valida = true;
        String mensaje = "", valCuit, valNombre, valDom;
        
        if(personaRue == null){
            personaRue = new ar.gob.ambiente.sacvefor.servicios.rue.Persona();
        }
        
        // valido el domicilio si lo tiene
        valDom = validarDomicilio(rolPersona);
        if(!valDom.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el Domicilio ingresado. " + valDom;
        }
        
        // valido el nombre completo o razón social, según corresponda
        valNombre = validarNombre();
        if(!valNombre.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el Nombre ingresado. " + valNombre;
        }
        
        // valido el rueCuit
        valCuit = validarCuit();
        if(!valCuit.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el CUIT ingresado. " + valCuit;
        }
        
        // si validó persisto
        if(valida){
            try{
                if(personaRue.getId() == 0){
                    // seteo la Persona con los campos del formulario
                    personaRue.setCorreoElectronico(rueCorreoElectronico);
                    personaRue.setCuit(rueCuit);
                    // seteo nombre completo o razón social según corresponda
                    if(rueTipoPers.toString().equals("Persona Física")){
                        personaRue.setNombreCompleto(rueNombreCompleto.toUpperCase());
                    }else{
                        personaRue.setRazonSocial(rueRazonSocial.toUpperCase());
                        personaRue.setTipoSociedad(obtenerTipoSociedad());
                    }
                    // si inserto, asigno el Tipo de Persona y la fecha de alta
                    personaRue.setTipo(rueTipoPers);
                    personaRue.setFechaAlta(new Date(System.currentTimeMillis()));
                }else{
                    // pongo nombre o razón social en mayúsculas
                    String nom;
                    if(personaRue.getTipo().toString().equals("Persona Física")){
                        nom = personaRue.getNombreCompleto().toUpperCase();
                        personaRue.setNombreCompleto(nom);
                    }else{
                        nom = personaRue.getRazonSocial().toUpperCase();
                        personaRue.setRazonSocial(nom);
                        personaRue.setTipoSociedad(obtenerTipoSociedad());
                    }
                }
                 // continúo para cualquier caso con el Tipo de Entidad, la idProvincia y el nombre de la Provincia
                personaRue.setEntidad(obtenerTipoEntidad());
                personaRue.setIdProvinciaGt(Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")));
                personaRue.setProvinciaGestion(ResourceBundle.getBundle("/Config").getString("Provincia"));
                
                
                // utilizo el cliente rest secún corresponda
                personaClient = new PersonaClient();
                Response res;
                if(personaRue.getId() == 0){
                    res = personaClient.create_JSON(personaRue);
                }else{
                    res = personaClient.edit_JSON(personaRue, String.valueOf(personaRue.getId()));
                }
                
                personaClient.close();
                
                switch (res.getStatus()) {
                    case 201:
                        JsfUtil.addSuccessMessage("La Persona se registró exitosamente en el Registro Unico, "
                                + "para continuar, busquela por el CUIT ingresado para registrarla localmente.");
                        limpiarFormPerRue();
                        break;
                    case 200:
                        JsfUtil.addSuccessMessage("La Persona se actualizó exitosamente en el Registro Unico, "
                                + "para completar la actualización, guarde los datos del formulario de edición.");
                        limpiarFormPerRue();
                        setearPersonaByRue(rolPersona);
                        break;
                    default:
                        JsfUtil.addErrorMessage("No se pudo guardar la Persona en el Registro Unico: " + res.getStatus() + " Error del servidor RUE.");
                        break;
                }

            }catch(NumberFormatException ex){
                JsfUtil.addErrorMessage("Hubo al intentar guardar la Persona en el Registro Unico: " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("No se pudieron validar los datos ingresados: " + mensaje);
        }
    }
    
    /**
     * Método para buscar la Persona en el RUE a partir del CUIT ingresado
     */
    public void buscarPersonaRue(){
        if(cuitBusqRue != null){
            personaRue = obtenerPersonaRueByCuit();
            if(personaRue == null){
                JsfUtil.addErrorMessage("La Persona correspondiente al CUIT ingresado no está registrada en el Registro Unico.");
            }
        }else{
            JsfUtil.addErrorMessage("Debe ingresar un CUIT.");
        }
    }
    
    /**
     * Método para preparar la apertura del formulario de búsqueda
     */
    public void prepareBuscarCuit(){
        cuitBusqRue = null;
        personaRue = new ar.gob.ambiente.sacvefor.servicios.rue.Persona();
    }
    
    /**
     * Método para deshabilitar una Persona. Modificará su condición de habilitado a false.
     * Las Personas deshabilitadas no estarán disponibles para su selección.
     */
    public void deshabilitar(){ 
        try{
            persona.setHabilitado(false);
            perFacade.edit(persona);
            persona = new Persona();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Persona seleccionada: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a las Personas a su condición normal.
     */
    public void habilitar(){
        try{
            persona.setHabilitado(true);
            perFacade.edit(persona);
            persona = new Persona();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Persona seleccionada: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para limpiar el formulario de Proponentes
     */
    public void limpiarFormProp() {
        // si ya subió un martillo, si estoy creando una persona nueva, lo elimino
        if(persona.getNombreArchivo() != null && persona.getId() == null){
            File martillo = new File(persona.getRutaArchivo() + persona.getNombreArchivo());
            martillo.delete();
            JsfUtil.addSuccessMessage("El martillo cargado ha sido eliminado.");
        }
        persona = new Persona();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
    }  
    
    /**
     * Método para limpiar el formulario de Técnicos, apoderados, transportistas y destinatarios
     */
    public void limpiarForm() {   
        persona = new Persona();
    } 
    
    /**
     * Método para iniciar el registro de una Persona en el RUE
     */
    public void prepareNewInsertRue(){
        rueCuit = null;
        cargarTiposEntidad();
        cargarTiposSociedad();
        cargarProvincias();
    }
    
    /**
     * Método para preparar la edición de un Vehículo en el RUE
     * Si la Persona persistida en el RUE no tiene Domicilio,
     * Creo y le asigno un Domicilio temporal para su posible seteo
     * y cargo las entidades provincias como para la creación de un domicilio nuevo.
     * Si la Persona tiene Domicilio persisitido cargo las entidades territoriales
     * en función del idLocalidad que tenga asignado el Domicilio en el RUE.
     */
    public void preparaEditRue(){
        rueEditable = true;
        personaRue = buscarPersonaRueById();
        //si no tiene domicilio, asigno un domicilio temporal y le reseteo los datos String
        if(personaRue.getDomicilio() == null){
            Domicilio domTemp = new Domicilio();
            //dom.setId(Long.valueOf(-1));
            domTemp.setCalle("");
            domTemp.setNumero("");
            domTemp.setPiso("");
            domTemp.setDepto("");
            personaRue.setDomicilio(domTemp);
        }

        if(Objects.equals(personaRue.getIdProvinciaGt(), Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")))){
            if(personaRue.getDomicilio().getId() > 0){
                cargarEntidadesTerr(personaRue.getDomicilio().getIdLocalidadGt());
            }else{
                cargarProvincias();
            }
            if(!personaRue.getTipo().getNombre().equals("Persona Física")){
                cargarTiposSociedad();
                tipoSocSelected = new EntidadServicio();
                tipoSocSelected.setId(personaRue.getTipoSociedad().getId());
                tipoSocSelected.setNombre(personaRue.getTipoSociedad().getNombre());
            }
            cargarTiposEntidad();
            tipoEntidadSelected = new EntidadServicio();
            tipoEntidadSelected.setId(personaRue.getEntidad().getId());
            tipoEntidadSelected.setNombre(personaRue.getEntidad().getNombre());
        }else{
            rueEditable = false;
        }
    }
    
    /**
     * Método para limpiar el formulario de inserción de Persona en el RUE
     */
    public void limpiarFormPerRue() {
        rueNombreCompleto = "";
        rueRazonSocial = "";
        rueCuit = null;
        rueCorreoElectronico = "";
        rueCalle = "";
        rueNumero = "";
        ruePiso = "";
        rueDepto = "";
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
        tipoEntidadSelected = new EntidadServicio();
        tipoSocSelected = new EntidadServicio();
    } 
    
    /**
     * Método para obtener el rol de la persona según la cadena recibida
     * @param sRol : rol de la persona a buscar
     * @return 
     */
    public Parametrica obtenerRol(String sRol) {
        TipoParam tipoParam = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        return paramFacade.getExistente(sRol, tipoParam);
    }    
    
    /*********************
     * Métodos privados **
     *********************/ 
    /**
     * Método que obtiene una Persona del RUE mediante el uso de la API correspondiente
     * @return 
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona obtenerPersonaRueByCuit(){
        List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
        
        try{
            // instancio el cliente para la obtención de la Persona
            personaClient = new PersonaClient();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
            Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(cuitBusqRue), null);
            listPersonas = response.readEntity(gType); 
            // cierro el cliente
            personaClient.close();
            if(listPersonas.isEmpty()){
                return null;
            }else{
                return listPersonas.get(0);
            }

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por su cuit desde el "
                    + "servicio REST de RUE", ex.getMessage()});
            return null;
        }
    }    

    /**
     * Método que obtine una Persona determinada del RUE según su id
     */
    private void cargarPersona() {
        try{
            // instancio el cliente para la obtención de la Persona
            personaClient = new PersonaClient();
            personaRue = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(persona.getIdRue()));
            personaClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
                    + "servicio REST de RUE", ex.getMessage()});
        }
    }
    
    /**
     * Método para nombrear un archivo subido, en este caso, el Martillo del Proponente
     * @param file
     * @param prefijo
     * @return 
     */
    private String getNombreArchivoASubir(UploadedFile file){
        Date date = new Date();
        String extension = file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1);
        String sufijo = JsfUtil.getDateInString(date);
        String nombreArchivo = persona.getCuit() + "_" + sufijo + "." + extension;
        return nombreArchivo;
    }    

    /**
     * Método para guardar la imagen del martillo a un directorio definitivo.
     * Completado el renombrado y guardado, elimino el archivo del directorio temporal
     * El directorio es "martillos" y está seteado en el Config.properties
     */
    private boolean saveMartillo() {
        if(subeMartillo){
            // obtengo la imagen del martillo del directorio temporal
            File martARenombrar = new File(ResourceBundle.getBundle("/Config").getString("SubdirTemp") + persona.getNombreArchivo());
            // instancio un nuevo File para el renombrado con el path al directorio definitivo
            File martDefinitivo = new File(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + persona.getNombreArchivo());
            // si existe, lo elimino
            martDefinitivo.delete();
            // renombro y devuelvo el resultado: true si fue existoso, false, si no lo fue.
            if(martARenombrar.renameTo(martDefinitivo)){
                // si todo fue bien, actualizo la ruta en la persona y la condición de temporal de la ruta
                persona.setRutaArchivo(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos"));
                persona.setRutaTemporal(false);
                subeMartillo = false;
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }

    /**
     * Método para cargar el listado de Tipos de Entidad para su selección
     */
    private void cargarTiposEntidad() {
        EntidadServicio tipoEntidad;
        List<TipoEntidad> listSrv;
        
        try{
            // instancio el cliente para la selección de los Tipos de Entidad
            tipoEntClient = new TipoEntidadClient();
            // obtengo el listado de Tipos de Entidad 
            GenericType<List<TipoEntidad>> gType = new GenericType<List<TipoEntidad>>() {};
            Response response = tipoEntClient.findAll_JSON(Response.class);
            listSrv = response.readEntity(gType);
            // lleno el list con los Tipos de Entidad como un objeto Entidad Servicio
            listTipoEntidad = new ArrayList<>();
            for(TipoEntidad te : listSrv){
                tipoEntidad = new EntidadServicio(te.getId(), te.getNombre());
                listTipoEntidad.add(tipoEntidad);
            }
            tipoEntClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Tipos de Entidad para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando los Tipos de Entidad desde "
                    + "el servicio REST del RUE.", ex.getMessage()});
        }
    }

    /**
     * Método para cargar el listado de Tipos de Sociedad para su selección
     */
    private void cargarTiposSociedad() {
        EntidadServicio tipoSociedad;
        List<TipoSociedad> listSrv;
        
        try{
            // instancio el cliente para la selección de los Tipos de Sociedad
            tipoSocClient = new TipoSociedadClient();
            // obtengo el listado de Tipos de Sociedad 
            GenericType<List<TipoSociedad>> gType = new GenericType<List<TipoSociedad>>() {};
            Response response = tipoSocClient.findAll_JSON(Response.class);
            listSrv = response.readEntity(gType);
            // lleno el list con los Tipos de Sociedad como un objeto Entidad Servicio
            listTipoSoc = new ArrayList<>();
            for(TipoSociedad ts : listSrv){
                tipoSociedad = new EntidadServicio(ts.getId(), ts.getNombre());
                listTipoSoc.add(tipoSociedad);
            }
            tipoSocClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Tipos de Sociedad para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando los Tipos de Sociedad desde "
                    + "el servicio REST del RUE.", ex.getMessage()});
        }
    }

    /**
     * Método para cargar el listado de Provincias para su selección
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class);
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();
            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
                //provincia = null;
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Provincias para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Provincias desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        }
    }

    /**
     * Método que carga el listado de Departamentos según la Provincia seleccionada
     * @param id 
     */
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // instancio el cliente para la selección de los Departamentos
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv));
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listDepartamentos = new ArrayList<>();
            for(Departamento dpt : listSrv){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
            }
            
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Departamentos de la Provincia seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Departamentos por Provincia "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Método que carga el listado de Localidades según el Departamento seleccionado
     * @param id 
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // instancio el cliente para la selección de las Localidades
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto));
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listLocalidades = new ArrayList<>();
            for(CentroPoblado loc : listSrv){
                local = new EntidadServicio(loc.getId(), loc.getNombre() + " - " + loc.getCentroPobladoTipo().getNombre());
                listLocalidades.add(local);
            }
            
            deptoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Centros poblados del Departamento seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Centros poblados por Departamento "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }
    
    /**
     * Método para cargar entidades de servicio y los listados, para actualizar el Domicilio de la Persona
     */
    private void cargarEntidadesTerr(Long idLocalidad){
        CentroPoblado cp;
        
        try{
            // instancio el cliente para la selección de las provincias
            locClient = new LocalidadClient();
            cp = locClient.find_JSON(CentroPoblado.class, String.valueOf(idLocalidad));
            // cierro el cliente
            locClient.close();
            // instancio las Entidades servicio
            localSelected = new EntidadServicio(cp.getId(), cp.getNombre());
            deptoSelected = new EntidadServicio(cp.getDepartamento().getId(), cp.getDepartamento().getNombre());
            provSelected = new EntidadServicio(cp.getDepartamento().getProvincia().getId(), cp.getDepartamento().getProvincia().getNombre());
            // cargo el listado de provincias
            cargarProvincias();
            // cargo el listado de Departamentos
            getDepartamentosSrv(provSelected.getId());
            // cargo el listado de Localidades
            getLocalidadesSrv(deptoSelected.getId());

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos territoriales del Domicilio de la Persona. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Centro poblado por id desde el "
                    + "servicio REST de centros poblados", ex.getMessage()});
        }
    }     

    /**
     * Método para validar el CUIT ingresado para persistir la Persona en el RUE
     * @return 
     */
    private String validarCuit() {
        String result = "";
        Long c;
        
        if(personaRue.getId() != 0){
            c = personaRue.getCuit();
        }else{
            c = rueCuit;
        }
        
        String strCuit = String.valueOf(c);
        // dejo solo números
        strCuit = strCuit.replaceAll("[^\\d]", "");
        // valido la cantidad de números
        if(strCuit.length() == 11){
            // armo un array de caracteres
            char[] cuitArray = strCuit.toCharArray();
            // inicializo un array de enterios por cada uno de los cuales se multiplicará cada uno de los dígitos del rueCuit a validar
            Integer[] serie = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
            // instancio una variable auxiliar para guardar los resultados del calculo del número validador
            Integer aux = 0;
            // recorro el ambos arrays y opero
            for (int i=0; i<10; i++){
                aux += Character.getNumericValue(cuitArray[i]) * serie[i];
            }
            // ejecuto la especificación (Módulo 11): 11 menos el resto de de la división de la suma de productos anterior por 11
            aux = 11 - (aux % 11);
            //Si el resultado anterior es 11 el código es 0
            if (aux == 11){
                aux = 0;
            }
            //Si el resultado anterior es 10 el código no tiene que validar, cosa que de todas formas pasa
            //en la siguiente comparación.
            //Devuelve verdadero si son iguales, falso si no lo son
            //(Paso todo a Object para prevenir errores, se puede usar: Integer.valueOf(cuitArray[10]) == aux;)
            Object oUltimo = (Integer)Character.getNumericValue(cuitArray[10]);
            Object oAux = aux;
            
            if(!oUltimo.equals(oAux)){
                result = "El CUIT ingresado es inválido.";
            }
            
            // Si valida, verifico que no está registrado ya en el RUE
            if(result.equals("")){
                try{
                    // instancio el cliente para verificar la existencia de una Persona con el mismo CUIT
                    List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
                    personaClient = new PersonaClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
                    Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(String.valueOf(c)), null);
                    listPersonas = response.readEntity(gType);
                    // cierro el cliente
                    personaClient.close();
                    if(!listPersonas.isEmpty()){
                        // actúo según sea una actualización o un insert
                        if(personaRue.getId() == 0){
                            // si inserta devuelvo el error
                            result = "Ya existe una Persona en el Registro Unico con el CUIT ingresado, por favor, verifique los datos.";
                        }else{
                            // si edita, verifico que no sea la misma Persona
                            if(!Objects.equals(listPersonas.get(0).getId(), personaRue.getId())){
                                // si no son la misma Persona, devuelvo error
                                result = "Ya existe una Persona en el Registro Unico con el CUIT ingresado, por favor, verifique los datos.";
                            }
                        }
                    }
                }catch(ClientErrorException ex){
                    result = "Hubo error validando el CUIT en el Registro Unico. " + ex.getMessage();
                }
            }            
        }    
        return result;
    }

    /**
     * Método para validar el nombre completo o razón social de la Persona para ser persistida en el RUE, 
     * según sea una edición o una inserción.
     * En cualquier caso, devuelve "" si validó o un mensaje si no validó
     * @return 
     */
    private String validarNombre() {
        String result = "", tipoPer, nomCom, razSoc;
        
        if(personaRue.getId() != 0){
            tipoPer = personaRue.getTipo().toString();
            nomCom = personaRue.getNombreCompleto();
            razSoc = personaRue.getRazonSocial();
        }else{
            tipoPer = rueTipoPers.toString();
            nomCom = rueNombreCompleto;
            razSoc = rueRazonSocial;
        }
        
        if(tipoPer.equals("Persona Física")){
            if(nomCom.equals("")){
                result = result + "Debe ingresar un Nombre completo para la Persona";
            }
        }else{
            if(razSoc.equals("")){
                result = result + "Debe ingresar una Razón social para la Persona";
            }
            if(tipoSocSelected.getId() == null){
                result = result + "Debe ingresar un Tipo de Sociedad";
            }
        }
        
        return result;
    }

    /**
     * Método para validar el domicilio según sea una edición o una inserción.
     * Si se trata de una edición, verifico el rol de la Persona:
     * Si es Destinatario, el Domicilio es obligatorio.
     * Para los roles de Persona restantes valid los datos y opero según las tres alternativas posibles:
     * 1: Domicilio completo, 2: Domicilio vacío, 3: Domicilio incompleto.
     * caso 1 => seteo el Domicilio;
     * caso 2 => elimino el Domicilio de la Persona;
     * caso 3 => seteo el mensaje de error;
     * En cualquier caso, devuelve "" si validó o un mensaje si no validó.
     * @return 
     */
    private String validarDomicilio(String rolPersona) {
        String result = "", valid;

        // defino los campos a validar según sea edición o insert
        if(personaRue.getId() != 0){
            if(rolPersona.equals(ResourceBundle.getBundle("/Config").getString("Destinatario"))){
                // si se trata de un Destinatario, el Domicilio es obligatorio, valido que esté completo
                valid = setearErrorDomEdit();
                if(!valid.equals("")){
                    // si está incompleto termino de armar el mensaje.
                    result = valid + " No se ha podido validar el Domicilio";
                }else{
                    // si está completo, devuelvo el mensaje vacío.
                    setearDom();
                    result = "";
                }
            }else{
                /**
                 * si no es Destinatario, actúo según el Domicilio estuviera o no persisitido con anterioridad, 
                 * dado que al prepararlo para la edición, en cualquier caso, ya se le asignó un Domicilio, persistido o no.
                 */
                if(!personaRue.getDomicilio().getCalle().equals("") &&
                            !personaRue.getDomicilio().getNumero().equals("") &&
                            localSelected != null && deptoSelected != null && provSelected != null){
                    // si el Domicilio está completo lo seteo y devuelvo el mensaje vación
                    setearDom();
                    result = "";
                }else if(personaRue.getDomicilio().getCalle().equals("") &&
                            personaRue.getDomicilio().getNumero().equals("") &&
                            localSelected == null && deptoSelected == null && provSelected == null){
                    // si el domicilio está vacío, lo elimino de la Persona y devuelvo el mensaje vació
                    personaRue.setDomicilio(null);
                    result = "";
                }else{
                    // si el domicilio está incompleto, seteo el mensaje de error y lo devuelvo
                    valid = setearErrorDomEdit();
                    result = valid + " No se ha podido validar el Domicilio";
                }
            }
        }else if(!rueCalle.equals("") || !rueNumero.equals("") || rolPersona.equals(ResourceBundle.getBundle("/Config").getString("Destinatario"))){
            // se considera que hay domicilio seteado, valido los datos obligarorios
            valid = setearErrorDomNuevo();
            if(!valid.equals("")){
                // si está incompleto termino de armar el mensaje.
                result = valid + " No se ha podido validar el Domicilio";
            }else{
                result = "";
                // instancio el domicilio
                Domicilio dom = new Domicilio();
                dom.setCalle(rueCalle.toUpperCase());
                dom.setNumero(rueNumero);
                dom.setPiso(ruePiso);
                dom.setDepto(rueDepto);
                dom.setIdLocalidadGt(localSelected.getId());
                dom.setDepartamento(deptoSelected.getNombre());
                dom.setProvincia(provSelected.getNombre());
                dom.setLocalidad(localSelected.getNombre());
                // asigno el domicilio a la PersonaRue
                personaRue.setDomicilio(dom);
            }
        }else{
            // si no hay domicilio seteado lo quito
            personaRue.setDomicilio(null);
        }
        
        return result;
    }

    /**
     * Método para obtener el Tipo de Entidad seleccionado a partir de la EntidadServicio
     * @return 
     */
    private TipoEntidad obtenerTipoEntidad() {
        try{
            // instancio el cliente para la selección de los Tipos de Entidad
            tipoEntClient = new TipoEntidadClient();
            TipoEntidad response = tipoEntClient.find_JSON(TipoEntidad.class, String.valueOf(tipoEntidadSelected.getId()));
            tipoEntClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo el Tipo de Entidad de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Tipo de Entidad por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método para obteber el Tipo de Sociedad seleccionado a partir de la EntidadServicio
     * @return 
     */
    private TipoSociedad obtenerTipoSociedad() {
        try{
            // instancio el cliente para la selección de los Tipos de Sociedad
            tipoSocClient = new TipoSociedadClient();
            TipoSociedad response = tipoSocClient.find_JSON(TipoSociedad.class, String.valueOf(tipoSocSelected.getId()));
            tipoSocClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo el Tipo de Sociedad de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Tipo de Sociedad por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método que obtiene una Persona desde la API RUE, según su id
     * @return 
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona buscarPersonaRueById() {
        try{
            // instancio el cliente para la selección de la Persona RUE
            personaClient = new PersonaClient();
            ar.gob.ambiente.sacvefor.servicios.rue.Persona response = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(persona.getIdRue()));
            personaClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método para setear el mensaje de error de validación de Domicilio durante la edición.
     * Valida que los datos obligatorios estén completos, en cuyo caso arma un mensaje específico.
     * O devuelve una cadena vacía si el Domicilio está completo
     */
    private String setearErrorDomEdit() {
        String result = "Está ingresando un domicilio.";
        if(personaRue.getDomicilio().getCalle().equals("")) result = result + " Debe ingresar una Calle.";
        if(personaRue.getDomicilio().getNumero().equals("")) result = result + " Debe ingresar un Némero de puerta.";
        if(localSelected == null) result = result + " Debe seleccionar una Localidad.";
        if(deptoSelected == null) result = result + " Debe seleccionar un Departamento.";
        if(provSelected == null) result = result + " Debe seleccionar una Provincia.";
        if(!result.equals("Está ingresando un domicilio.")){
            result = result + " No se ha podido validar el Domicilio";
        }else{
            result = "";
        }
        return result;
    }

    /**
     * Método que setea el Domicilio para ser persistido en el RUE al editar la Persona
     */
    private void setearDom() {
        // pongo la calle en mayúsculas
        String calle = personaRue.getDomicilio().getCalle();
        personaRue.getDomicilio().setCalle(calle.toUpperCase());
        // seteo el resto de los elementos
        personaRue.getDomicilio().setIdLocalidadGt(localSelected.getId());
        personaRue.getDomicilio().setDepartamento(deptoSelected.getNombre());
        personaRue.getDomicilio().setProvincia(provSelected.getNombre());
        personaRue.getDomicilio().setLocalidad(localSelected.getNombre());
    }
    /**
     * Método para setear el mensaje de error de validación de Domicilio nuevo.
     * Valida que los datos obligatorios estén completos, en cuyo caso arma un mensaje específico.
     * O devuelve una cadena vacía si el Domicilio está completo    
     */
    private String setearErrorDomNuevo() {
        String result = "Está ingresando un domicilio.";
        if(rueCalle.equals("")) result = result + " Debe ingresar una Calle.";
        if(rueNumero.equals("")) result = result + " Debe ingresar un Némero de puerta.";
        if(localSelected == null) result = result + " Debe seleccionar una Localidad.";
        if(deptoSelected == null) result = result + " Debe seleccionar un Departamento.";
        if(provSelected == null) result = result + " Debe seleccionar una Provincia.";
        if(!result.equals("Está ingresando un domicilio.")){
            result = result + " No se ha podido validar el Domicilio";
        }else{
            result = "";
        }
        return result;
    }
}
