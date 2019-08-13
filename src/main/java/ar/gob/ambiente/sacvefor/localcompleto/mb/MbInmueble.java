
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.InmuebleFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Bean de respaldo para la gestión de Inmuebles
 * Gestiona la vista aut/inm/inmueble.xhtml
 * @author rincostante
 */
public class MbInmueble {

    /**
     * Variable privada: objeto principal a gestionar
     */
    private Inmueble inmueble;
    
    /**
     * Variable privada: listado de los inmuebles existentes
     */
    private List<Inmueble> listado;
    
    /**
     * Variable privada: listado para filtrar la tabla de inmuebles existentes
     */
    private List<Inmueble> listFilters;
    
    /**
     * Variable privada: listado de los inmuebles existentes para poblar el combo de selección de inmueble de origen, si corresponde
     */
    private List<Inmueble> lstInmOrigen;
    
    /**
     * Variable privada: listado de las paramétricas que oficiarán de opciones para seleccionar el origen del inmueble.
     * (Privado, Fiscal, etc.) A pedido de la Provincia de Misiones.
     */
    private List<Parametrica> lstOrigenInm;

    /**
     * Variable privada: rodal a asignar al inmueble
     */
    private Rodal rodal;
    
    /**
     * Variable privada: listado de rodales en los que podrá estar subdividido un inmueble. 
     * Los mismos podrán ser incuidos como atributo en el inmueble a registrar.
     * A pedido de la Provincia de Misiones.
     */
    private List<Rodal> rodales;
    
    /**
     * Variable privada: flag que indica que el inmueble que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que el inmueble que se está gestionando es existente
     */
    private boolean edit;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    private static final Logger logger = Logger.getLogger(Inmueble.class.getName());  
    
    /**
     * Variable privada: flag que indica si se está subiendo una imagen de martillo
     */
    private boolean subeMartillo;    
    
    ///////////////////////////////////////////////////
    // acceso a datos mediante inyección de recursos //
    ///////////////////////////////////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Inmueble
     */ 
    @EJB
    private InmuebleFacade inmFacade;
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica.
     */
    @EJB
    private ParametricaFacade paramFacade;
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */  
    @EJB
    private TipoParamFacade tipoParamFacade;    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */
    @EJB
    private AutorizacionFacade autFacade;
    
    ////////////////////////////////////////////////////////////
    // Clientes REST para la selección de datos territoriales //
    ////////////////////////////////////////////////////////////
    
    /**
     * Variable privada: cliente para el acceso a la API de Provincias de Organización territorial
     */
    private ProvinciaClient provClient;    
    
    /**
     * Variable privada: cliente para el acceso a la API de Departamentos de Organización territorial
     */
    private DepartamentoClient deptoClient;
    
    /**
     * Variable privada: cliente para el acceso a la API de Localidades de Organización territorial
     */
    private LocalidadClient localidadClient;   
    
    /**
     * Variable privada: cliente para el acceso a la API de Validación de usuarios para el acceso a Organización territorial
     */
    private UsuarioClient usuarioClient;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Organización territorial
     */    
    private Token token;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Organización territorial
     */ 
    private String strToken;    

    /////////////////////////////////////////////////////////////////////////////////////////
    // Campos para la gestión de los elementos territoriales en los combos del formulario. //
    // Las Entidades de servicio se componen de un par {id | nombre} ////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Provincias
     */
    private List<EntidadServicio> listProvincias;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Provincia seleccionada del combo
     */
    private EntidadServicio provSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los Departamentos
     */
    private List<EntidadServicio> listDepartamentos;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos del Departamento seleccionado del combo
     */
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Localidades
     */
    private List<EntidadServicio> listLocalidades;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Localidad seleccionado del combo
     */
    private EntidadServicio localSelected;       
    
    /**
     * Constructor
     */
    public MbInmueble() {
    }

    ///////////////////////
    // Métodos de acceso //
    ///////////////////////  
    public Rodal getRodal() {    
        return rodal;
    }    
  
    public void setRodal(Rodal rodal) {
        this.rodal = rodal;
    }

    public List<Parametrica> getLstOrigenInm() {
        TipoParam tipoParam = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("OrigenPredio"));
        return paramFacade.getHabilitadas(tipoParam);
    }    

    public void setLstOrigenInm(List<Parametrica> lstOrigenInm) {
        this.lstOrigenInm = lstOrigenInm;
    }

    public List<Rodal> getRodales() {
        return rodales;
    }

    public void setRodales(List<Rodal> rodales) {        
        this.rodales = rodales;
    }

    public boolean isSubeMartillo() {
        return subeMartillo;
    }    
    
    public void setSubeMartillo(boolean subeMartillo) {
        this.subeMartillo = subeMartillo;
    }

    public List<Inmueble> getLstInmOrigen() {
        lstInmOrigen = inmFacade.getHabilitados();  
        return lstInmOrigen;
    }
     
    public void setLstInmOrigen(List<Inmueble> lstInmOrigen) {
        this.lstInmOrigen = lstInmOrigen;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public List<Inmueble> getListado() {
        listado = inmFacade.findAll();
        return listado;
    }

    public void setListado(List<Inmueble> listado) {
        this.listado = listado;
    }

    public List<Inmueble> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Inmueble> listFilters) {
        this.listFilters = listFilters;
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

    public List<EntidadServicio> getListProvincias() {
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
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
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

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    
    ////////////////////////
    // Mátodos operativos //
    ////////////////////////
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar, 
     * el inmueble y las provincias a seleccionar
     */  
    @PostConstruct
    public void init(){
        inmueble = new Inmueble();
        cargarProvincias();
    }   

    /**
     * Método para actualizar el listado de departamentos según la Provincia seleccionada
     */    
    public void provinciaChangeListener(){
        localSelected = new EntidadServicio();
        getDepartamentosSrv(provSelected.getId());
    }   
    
    /**
     * Método para actualizar el listado de localidades según el Departamento seleccionado
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
    }  
    
    /**
     * Método para guardar el Inmueble, sea inserción o edición.
     * Con la condición de haber guardado previamente el archivo de la imagen del martillo
     * Previa validación
     */      
    public void save(){
        boolean valida = true;
        String message = "";
        
        try{
            Inmueble inmExitente = inmFacade.getExistenteByCatastro(inmueble.getIdCatastral());
            // valido por el idCatastral
            if(inmExitente != null){
                if(inmueble.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!inmExitente.equals(inmueble)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    message += " El Inmueble que está tratando de persisitir ya existe, por favor verifique los datos ingresados.";
                }
            }
//            // valido el origen del predio si está configurado para discriminar tasas según él
//            if(ResourceBundle.getBundle("/Config").getString("DiscTasaOrigen").equals("si")){
//                if(inmueble.getOrigen() == null){
//                    // si no se asignó un origen al inmueble no valido
//                    valida = false;
//                    message += " Debe seleccionar el origen del " + ResourceBundle.getBundle("/Config").getString("Inmueble") + " que corresponda.";
//                }
//            }
            if(valida){
                // procedo al guardado definitivo de la imagen del martillo
                if(saveMartillo()){
                    inmueble.setIdCatastral(inmueble.getIdCatastral().toUpperCase());
                    String tempNombre = inmueble.getNombre();
                    inmueble.setNombre(tempNombre.toUpperCase());
                    String tempDomicilio = inmueble.getDomicilio();
                    inmueble.setDomicilio(tempDomicilio.toUpperCase());
                    // seteo los datos territoriales
                    inmueble.setIdLocGt(localSelected.getId());
                    inmueble.setLocalidad(localSelected.getNombre());
                    inmueble.setDepartamento(deptoSelected.getNombre());
                    inmueble.setProvincia(provSelected.getNombre());
                    if(inmueble.getId() != null){
                        inmFacade.edit(inmueble);
                        JsfUtil.addSuccessMessage("El Inmueble fue guardado con exito");
                    }else{
                        inmueble.setHabilitado(true);
                        inmFacade.create(inmueble);
                        JsfUtil.addSuccessMessage("El Inmueble fue registrado con exito");
                    }  
                    edit = false;
                }
            }else{
                JsfUtil.addErrorMessage(message);
            }
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Inmueble : " + ex.getMessage());
        }     
    }
    
    /**
     * Método que setea la provPersona según el id correspondiente que la Persona tenga seteado para ser editada
     */
    public void prepareEdit(){
        // cargo las entidades territoriales
        cargarEntidadesSrv(inmueble.getIdLocGt());
        // seteo los flags
        view = false;
        edit = true;
    } 
  
    /**
     * Método para habilitar la vista detalle del formulario
     */
    public void prepareView(){
        view = true;
        edit = false;
    } 
    
    /**
     * Método para habilitar la muestra nuevo del formulario
     */
    public void prepareNew(){
        view = false;
        edit = true;
    } 

    /**
     * Método para deshabilitar un Inmueble. Modificará su condición de habilitado a false.
     * Los Inmuebles deshabilitados no estarán disponibles para su selección
     */
    public void deshabilitar(){
        try{
            inmueble.setHabilitado(false);
            inmFacade.edit(inmueble);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Inmueble: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a las Clases a su condición normal.
     */
    public void habilitar(){
        try{
            inmueble.setHabilitado(true);
            inmFacade.edit(inmueble);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Inmueble: " + ex.getMessage());
        }
    }
    
    /**
     * Limpia el formulario de inserción/edición
     */
    public void limpiarForm() {
        // si ya subió un martillo, si estoy creando un inmueble nuevo, lo elimino
        if(inmueble.getNombreArchivo() != null && inmueble.getId() == null){
            File martillo = new File(inmueble.getRutaArchivo() + inmueble.getNombreArchivo());
            martillo.delete();
            JsfUtil.addSuccessMessage("El martillo cargado ha sido eliminado.");
        }        
        inmueble = new Inmueble();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
    }    
    
    ////////////////////////////////////////
    // Métodos para gestionar el martillo //
    ////////////////////////////////////////
    /**
     * Método para subir la imagen del martillo en el subdirectorio temporal
     * El subdirectorio temporal se llama "TMP"
     * Se configuran en el archivo de propiedades configurable "Config.properties"
     * @param event FileUploadEvent evento de subida de martillo
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
                    inmueble.setRutaArchivo(destino);
                    inmueble.setNombreArchivo(nombreArchivo);
                    inmueble.setRutaTemporal(true);
                }
                // actualizo el flag
                subeMartillo = true;
                edit = true;
            }else{
                JsfUtil.addErrorMessage("No se pudo obtener el destino de la imagen del Martillo.");
            }
        }catch(IOException e){
            JsfUtil.addErrorMessage("Hubo un error subiendo la imagen del Martillo" + e.getLocalizedMessage());
        }
    }
    
    /**
     * Método para preparar el formulario de gestión de rodales.
     * Agrega nuevos, quita existentes. 
     * En caso de tener Autorizaciones vinculadas al inmueble, 
     * de los rodales existentes, solo se podrán quitar los rodales 
     * no asingados a ninguna autorización
     */
    public void prepareAddRodal(){
        limpiarRodal();
        // si el inmueble tiene rodales, valido su asignación para alguna autorización
        if(!inmueble.getRodales().isEmpty()){
            try{
                for(Rodal r : inmueble.getRodales()){
                    // si el rodal está asignado a alguna autorización, seteo el flag correspondiente
                    if(!autFacade.getByRodal(r).isEmpty()){
                        r.setAsignado(true);
                    }
                }
            }catch(Exception ex){
                JsfUtil.addErrorMessage("Hubo un error seteando los rodales asignados a una Autorización" + ex.getMessage());
            }
        }
    }
    
    /**
     * Método para limpiar el objeto rodal del formulario
     */
    public void limpiarRodal(){
        rodal = new Rodal();
    }
    
    /**
     * Método para agregar un rodal al inmueble
     * Valida que no haya un rodal vinculado al inmueble con el mismo número
     */
    public void agregarRodal(){
        boolean valida = true;
        // si tiene rodales valido que el número no sea repetido
        if(!inmueble.getRodales().isEmpty()){
            for(Rodal r : inmueble.getRodales()){
                if(r.getNumOrden().equals(rodal.getNumOrden().toUpperCase())){
                    valida = false;
                }
            }
        }
        try{
            if(valida){
                // seteo mayúsculas en el número del rodal
                String num = rodal.getNumOrden();
                rodal.setNumOrden(num.toUpperCase());
                inmueble.getRodales().add(rodal);
                limpiarRodal();
            }else{
                JsfUtil.addErrorMessage("El número asignado al Rodal ya existe, por favor ingrese otro número.");
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al agergar el Rodal al " + ResourceBundle.getBundle("/Config").getString("Inmueble") + ": " + ex.getMessage());
        }   
    }
    
    /**
     * Método para desagregar un rodal ya asingado al inmueble
     */
    public void desagregarRodal(){
        int i = 0, j = 0;
        
        for(Rodal rod : inmueble.getRodales()){
            if(rodal.getNumOrden() == rod.getNumOrden()){
                j = i;
            }
            i = i+= 1;
        }
        inmueble.getRodales().remove(j);
        limpiarRodal();      
    }
    
    //////////////////////
    // Métodos privados //
    //////////////////////
    
    /**
     * Método para guardar la imagen del martillo a un directorio definitivo.
     * Completado el renombrado y guardado, elimino el archivo del directorio temporal
     * El directorio es "martillos" y está seteado en el Config.properties.
     * Utilizado en save()
     */
    private boolean saveMartillo() {
        if(subeMartillo){
            // obtengo la imagen del martillo del directorio temporal
            File martARenombrar = new File(ResourceBundle.getBundle("/Config").getString("SubdirTemp") + inmueble.getNombreArchivo());
            // instancio un nuevo File para el renombrado con el path al directorio definitivo
            File martDefinitivo = new File(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + inmueble.getNombreArchivo());
            // si existe, lo elimino
            martDefinitivo.delete();
            // renombro y devuelvo el resultado: true si fue existoso, false, si no lo fue.
            if(martARenombrar.renameTo(martDefinitivo)){
                // si todo fue bien, actualizo la ruta en la persona y la condición de temporal de la ruta
                inmueble.setRutaArchivo(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos"));
                inmueble.setRutaTemporal(false);
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
     * Método para nombrear un archivo subido, en este caso, el Martillo del Proponente.
     * Utilizado en subirMartilloTmp(FileUploadEvent event)
     * @param file UploadedFile archivo a subir
     * @return String nombre del archivo según la provincia, el cuit y la fecha
     */
    private String getNombreArchivoASubir(UploadedFile file){
        Date date = new Date(System.currentTimeMillis());
        String extension = file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1);
        Long sufijo = date.getTime();
        String nombreArchivo = ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + ""
                + "_" + sufijo.toString() + "." + extension;
        return nombreArchivo;
    }       
    
    /**
     * Método para obtener el inmueble según su clave identificatoria.
     * Utilizado en el converter
     * @param key Long identificación única del inmueble
     * @return Object Inmueble obtenido
     */
    private Object getInmueble(Long key) {
        return inmFacade.find(key);
    }

    /**
     * Método priviado para obtener las Provincias mediante el servicio REST de Organización territorial.
     * Obtiene las provincias y por cada una crea una EntidadServicio con el id y nombre.
     * Luego las incluye en el listado listProvincias.
     * Utilizado por init()
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTerr();
            }else try {
                if(!token.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class, token.getStrToken());
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
     * Método para poblar el listado de Departamentos según la Provincia seleccionada del servicio REST de Organización territorial.
     * Utilizado por cargarEntidadesSrv(Long idLocGt)
     */  
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTerr();
            }else try {
                if(!token.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv), token.getStrToken());
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
     * Método para poblar el listado de Localidades según el Departamento seleccionado del servicio REST de Organización territorial
     * Utilizado por deptoChangeListener()
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTerr();
            }else try {
                if(!token.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto), token.getStrToken());
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
            JsfUtil.addErrorMessage("Hubo un error obteniendo las Localidades del Departamento seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo las Localidades por Departamento "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Método para cargar entidades de servicio y los listados, para actualizar el Domicilio de la Persona.
     * Utilizado por prepareEdit()
     */    
    private void cargarEntidadesSrv(Long idLocGt) {
        CentroPoblado cp;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTerr();
            }else try {
                if(!token.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            localidadClient = new LocalidadClient();
            cp = localidadClient.find_JSON(CentroPoblado.class, String.valueOf(idLocGt), token.getStrToken());
            // cierro el cliente
            localidadClient.close();
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
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos territoriales del Inmueble. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Localidad por id desde el "
                    + "servicio REST de centros poblados", ex.getMessage()});
        }
    }
    
    /**
     * Método privado que obtiene y setea el token para autentificarse ante la API rest de Territorial
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado por cargarProvincias(), getDepartamentosSrv(Long idProv), getLocalidadesSrv(Long idDepto) y cargarEntidadesSrv(Long idLocGt)
     */
    private void getTokenTerr(){
        try{
            usuarioClient = new UsuarioClient();
            Response responseUs = usuarioClient.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strToken = (String)lstHeaders.get(0); 
            token = new Token(strToken, System.currentTimeMillis());
            usuarioClient.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }        
    
    /////////////////////////////
    // Converter para Inmueble //
    /////////////////////////////
    @FacesConverter(forClass = Inmueble.class)
    public static class InmuebleConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbInmueble controller = (MbInmueble) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbInmueble");
            return controller.getInmueble(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }
        
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Inmueble) {
                Inmueble o = (Inmueble) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Inmueble.class.getName());
            }
        }
    }       
}
