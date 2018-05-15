
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Delegacion;
import ar.gob.ambiente.sacvefor.localcompleto.facades.DelegacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Bean de respaldo para la gestión de Delegaciones.
 * Gestiona la vista de /param/deleg.xhtml
 * @author rincostante
 */
public class MbDelegacion {

    /**
     * Variable privada: entidad a gestionar
     */
    private Delegacion delegacion;
    
    /**
     * Variable privada: listado de las entidades a gestionar
     */
    private List<Delegacion> listado;
    
    /**
     * Variable privada: listado para el filtrado de la tabla
     */
    private List<Delegacion> listFilter;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando es existente
     */
    private boolean edit;    
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MbDelegacion.class);        
    
    ////////////////////////////////////////
    // campos para la gestion territorial //
    ////////////////////////////////////////
    
    /**
     * Variable privada: Entidad de servicio para setear la provincia seleccionada del combo
     */
    private EntidadServicio provSelected;
    
    /**
     * Variable privada: Entidad de servicio para setear el departamento seleccionado del combo
     */    
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: Entidad de servicio para setear la localidad seleccionada del combo
     */    
    private EntidadServicio localSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las localidades.
     */  
    private List<EntidadServicio> listLocalidades;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los departamentos.
     */      
    private List<EntidadServicio> listDepartamentos;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las provincias.
     */      
    private List<EntidadServicio> listProvincias;
    
    ///////////////////////////////////////////////////////
    // Clientes REST para la gestión del API Territorial //
    ///////////////////////////////////////////////////////
    
    /**
     * Variable privada: Cliente para la API Rest de Provincias en Organización territorial
     */
    private ProvinciaClient provClient;   
    
    /**
     * Variable privada: Cliente para la API Rest de Departamentos en Organización territorial
     */
    private DepartamentoClient deptoClient;

    /**
     * Variable privada: Cliente para la API Rest de Localidades en Organización territorial
     */
    private LocalidadClient locClient;   
    
    /**
     * Variable privada: Cliente para la API Rest de Usuarios en Organización territorial
     */
    private UsuarioClient usClientTerr;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Organización territorial
     */
    private Token tokenTerr;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Organización territorial
     */
    private String strTokenTerr;    
    
    ////////////////////
    // Acceso a datos //
    ////////////////////
    @EJB
    private DelegacionFacade delegFacade;

    /**
     * Constructor
     */
    public MbDelegacion() {
    }

    ///////////////////////
    // Métodos de acceso //
    ///////////////////////      
    public Delegacion getDelegacion() {
        return delegacion;
    }

    public void setDelegacion(Delegacion delegacion) {
        this.delegacion = delegacion;
    }

    public List<Delegacion> getListado() {
        listado = delegFacade.findAll();
        return listado;
    }

    public void setListado(List<Delegacion> listado) {
        this.listado = listado;
    }

    public List<Delegacion> getListFilter() {
        return listFilter;
    }

    public void setListFilter(List<Delegacion> listFilter) {
        this.listFilter = listFilter;
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

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

    public List<EntidadServicio> getListLocalidades() {
        if(listLocalidades == null) listLocalidades = new ArrayList<>();
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public List<EntidadServicio> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public List<EntidadServicio> getListProvincias() {
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar
     * y carga el combo con las provincias para seleccionar el departamento y la localidad.
     */  
    @PostConstruct
    public void init(){
        delegacion = new Delegacion();
        cargarProvincias();
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
     * Método para preparar la vista de registro de una nueva Delegación
     */
    public void prepareNew(){
        delegacion = new Delegacion();
        view = false;
        edit = true;   
    }
    
    /**
     * Método para preparar la edición de la Delegación.
     * setea los datos territoriales relacionados con la localidad
     */
    public void prepareEdit(){
        edit = true;
        cargarEntidadesTerr(delegacion.getIdLoc());
    }
    
    /**
     * Método para preparar la vista de los datos de la Delegación
     */
    public void prepareView(){
        view = true;
    }
    
    /**
     * Método para persistir una Delegación, sea nueva o actualización de una existente
     */
    public void save(){
        boolean valida = true;
        try{
            delegacion.setLocalidad(localSelected.getNombre());
            Delegacion delegExitente = delegFacade.getExistente(delegacion.getNombre().toUpperCase(), delegacion.getLocalidad().toUpperCase()); 
            if(delegExitente != null){
                if(delegExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!delegExitente.equals(delegacion)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = delegacion.getNombre();
                delegacion.setNombre(tempNombre.toUpperCase());
                String tempDomicilio = delegacion.getDomicilio().toUpperCase();
                delegacion.setDomicilio(tempDomicilio);
                delegacion.setIdLoc(localSelected.getId());
                delegacion.setHabilitada(true);
                if(delegacion.getId() != null){
                    delegFacade.edit(delegacion);
                    JsfUtil.addSuccessMessage("La Delegación fue guardada con exito");
                }else{
                    delegacion.setHabilitada(true);
                    delegFacade.create(delegacion);
                    JsfUtil.addSuccessMessage("La Delegación fue registrado con exito");
                }   
                limpiarForm();
            }else{
                JsfUtil.addErrorMessage("La Delegación que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error procesando la Delegación forestal : " + ex.getMessage());
        }
    }
    
    /**
     * Método para deshabilitar una Delegación
     * No estará disponible en los listados correspondientes
     */    
    public void deshabilitar(){
        try{
            delegacion.setHabilitada(false);
            delegFacade.edit(delegacion);
            delegacion = new Delegacion();
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al deshabilitar la Delegación forestal: " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar una Delegación
     * Volverá a estar disponible en los listados
     */    
    public void habilitar(){
        try{
            delegacion.setHabilitada(true);
            delegFacade.edit(delegacion);
            delegacion = new Delegacion();
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al habilitar la Delegación forestal: " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el formulario. Resetea los lisados y entidades seleccionadas
     */
    public void limpiarForm(){
        if(delegacion.getId() == null){
            delegacion= new Delegacion();
        }
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        localSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
        listProvincias = new ArrayList<>();
    }

    //////////////////////
    // Métodos privados //
    //////////////////////
    /**
     * Método para cargar el listado de Provincias para su selección.
     * Utilizado en el init()
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                LOG.fatal("Hubo un error obteniendo la vigencia del token TERR. " + ex.getMessage());
            }
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class, tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();
            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Provincias para su selección.");
            // lo escribo en el log del server
            LOG.fatal("Hubo un error cargando las Provincias desde el servicio REST de Centros poblados. " + ex.getMessage());
        }
    }
    
    /**
     * Método que carga el listado de Departamentos según la Provincia seleccionada.
     * Utilizado en cargarEntidadesTerr(Long idLocalidad) y provinciaChangeListener()
     * @param id Long identificador de la provincia en Organización Territorial
     */
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                LOG.fatal("Hubo un error obteniendo la vigencia del token TERR. " + ex.getMessage());
            }
            // instancio el cliente para la selección de los Departamentos
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv), tokenTerr.getStrToken());
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
            LOG.fatal("Hubo un error cargando los Departamentos por Provincia desde el servicio REST de Centros poblados. " + ex.getMessage());
        }
    }   
    
    /**
     * Método para cargar entidades de servicio y los listados, para actualizar el Domicilio de la Persona.
     * Utilizado en 
     */
    private void cargarEntidadesTerr(Long idLocalidad){
        CentroPoblado cp;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                LOG.fatal("Hubo un error obteniendo la vigencia del token TERR. " + ex.getMessage());
            }
            // instancio el cliente para la selección de las provincias
            locClient = new LocalidadClient();
            cp = locClient.find_JSON(CentroPoblado.class, String.valueOf(idLocalidad), tokenTerr.getStrToken());
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
            LOG.fatal("Hubo un error obteniendo el Centro poblado por id por Provincia desde el servicio REST de Centros poblados. " + ex.getMessage());
        }
    }       
    
    /**
     * Método que carga el listado de Localidades según el Departamento seleccionado
     * Utilizados en deptoChangeListener() y cargarEntidadesTerr(Long idLocalidad)
     * @param id Long identificación del departamento
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                LOG.fatal("Hubo un error obteniendo la vigencia del token TERR. " + ex.getMessage());
            }
            // instancio el cliente para la selección de las Localidades
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto), tokenTerr.getStrToken());
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
            LOG.fatal("Hubo un error obteniendo los Centros poblados por Departamento desde el servicio REST de Centros poblados. " + ex.getMessage());
        }
    }     
    
    /**
     * Método privado que obtiene y setea el tokenTerr para autentificarse ante la API rest de Territorial
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en cargarEntidadesTerr(Long idLocalidad), getDepartamentosSrv(Long idProv) y cargarProvincias()
     */
    private void getTokenTerr(){
        try{
            usClientTerr = new UsuarioClient();
            Response responseUs = usClientTerr.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenTerr = (String)lstHeaders.get(0); 
            tokenTerr = new Token(strTokenTerr, System.currentTimeMillis());
            usClientTerr.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API Territorial: " + ex.getMessage());
        }
    } 
}
