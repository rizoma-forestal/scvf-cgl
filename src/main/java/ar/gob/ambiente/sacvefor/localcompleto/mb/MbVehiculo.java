
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.MarcaClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.ModeloClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.VehiculoClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.servicios.rue.Marca;
import ar.gob.ambiente.sacvefor.servicios.rue.Modelo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 *
 * @author rincostante
 */
public class MbVehiculo {

    // campos para gestionar local
    private Vehiculo vehiculo;
    private List<Vehiculo> lstVehiculos;
    private List<Vehiculo> lstVehiculosFilter;
    private boolean view;
    private boolean edit;
    // campos para gestionar rue
    private static final Logger logger = Logger.getLogger(Vehiculo.class.getName());
    private List<Vehiculo> lstRevisions; 
    private String matBusqRue; 
    private ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo vehiculoRue;
    // campos para gestionar Marcas en el rue
    private Marca marcaRue;
    private String nombreMarcaRue;
    private List<Marca> lstMarcas;
    private List<Marca> lstMarcasFilters;
    private Modelo modeloRue;
    private String nombreModeloRue;
    private List<Modelo> lstModelos;
    private List<Modelo> lstModelosFilters;
    private boolean viewMarca;
    private boolean editMarca;
    private boolean viewModelo;
    private boolean editModelo;
    private List<EntidadServicio> listMarcasRue;
    private EntidadServicio marcaRueSelected;
    private MbSesion sesion;
    private Usuario usLogueado;    
    
    
    // inyección de recursos
    @EJB
    private VehiculoFacade vehiculoFacade;
    @EJB
    private PersonaFacade perFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;    
    @EJB
    private UsuarioFacade usuarioFacade;    
    
    // Clientes REST para la gestión del API de Vehículos, Marcas y Modelos
    private VehiculoClient vehiculoClient;  
    private ModeloClient modeloClient;
    private MarcaClient marcaClient;
    
    /**
     * Campos para la gestión de las Entidades provenientes de la API
     * RUE en los combos del formulario.
     * Las Entidades de servicio se componen de un par {id | nombre}
     */   
    private List<EntidadServicio> listModelos;
    private EntidadServicio modeloSelected;    
    private List<EntidadServicio> listMarcas;
    private EntidadServicio marcaSelected;
    // agrego otra para el titularSelected del Vehículo
    private List<EntidadServicio> listTitulares;
    private EntidadServicio titularSelected;
    
    /**
     * Campos para el seteo de las Entidades RUE
     */    
    private String rueVehiculoMatricula;
    private int rueVehiculoAnio;
    private boolean rueEditable;
    
    public MbVehiculo() {
    }
       
    /**********************
     * Métodos de acceso **
     **********************/       
    public String getNombreMarcaRue() {
        return nombreMarcaRue;
    }

    public void setNombreMarcaRue(String nombreMarcaRue) {
        this.nombreMarcaRue = nombreMarcaRue;
    }

    public String getNombreModeloRue() {
        return nombreModeloRue;
    }
    
    public void setNombreModeloRue(String nombreModeloRue) {
        this.nombreModeloRue = nombreModeloRue;
    }

    public Modelo getModeloRue() {
        return modeloRue;
    }

    public void setModeloRue(Modelo modeloRue) {
        this.modeloRue = modeloRue;
    }
   
    public List<Modelo> getLstModelos() {
        // seteo el listado
        try{
            // instancio el cliente para la selección de las Marcas de Vehículos
            modeloClient = new ModeloClient();
            // obtengo el listado de modelos
            GenericType<List<Modelo>> gType = new GenericType<List<Modelo>>() {};
            Response response = modeloClient.findAll_JSON(Response.class);
            lstModelos = response.readEntity(gType);
            // cierro el cliente
            modeloClient.close();
            // seteo el listado de Marcas como EntidadServicio
            List<EntidadServicio> lMarcas = new ArrayList<>();
            listMarcasRue = cargarMarcas(lMarcas);
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Marcas.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Marcas desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        }         
        return lstModelos;
    }

    public void setLstModelos(List<Modelo> lstModelos) {
        this.lstModelos = lstModelos;
    }

    public List<Modelo> getLstModelosFilters() {
        return lstModelosFilters;
    }

    public void setLstModelosFilters(List<Modelo> lstModelosFilters) {
        this.lstModelosFilters = lstModelosFilters;
    }

    public boolean isViewModelo() {
        return viewModelo;
    }

    public void setViewModelo(boolean viewModelo) {
        this.viewModelo = viewModelo;
    }

    public boolean isEditModelo() {
        return editModelo;
    }
  
    public void setEditModelo(boolean editModelo) {
        this.editModelo = editModelo;
    }

    public Marca getMarcaRue() {
        return marcaRue;
    }

    public void setMarcaRue(Marca marcaRue) {
        this.marcaRue = marcaRue;
    }

    public List<Marca> getLstMarcas() {
        // seteo el listado
        try{
            // instancio el cliente para la selección de las Marcas de Vehículos
            marcaClient = new MarcaClient();
            // obtengo el listado de marcas
            GenericType<List<Marca>> gType = new GenericType<List<Marca>>() {};
            Response response = marcaClient.findAll_JSON(Response.class);
            lstMarcas = response.readEntity(gType);
            // cierro el cliente
            marcaClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Marcas.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Marcas desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        } 
        return lstMarcas;
    }

    public void setLstMarcas(List<Marca> lstMarcas) {
        this.lstMarcas = lstMarcas;
    }

    public List<Marca> getLstMarcasFilters() {
        return lstMarcasFilters;
    }

    public void setLstMarcasFilters(List<Marca> lstMarcasFilters) {
        this.lstMarcasFilters = lstMarcasFilters;
    }

    public boolean isViewMarca() {
        return viewMarca;
    }

    public void setViewMarca(boolean viewMarca) {
        this.viewMarca = viewMarca;
    }

    public boolean isEditMarca() {
        return editMarca;
    }

    public void setEditMarca(boolean editMarca) {
        this.editMarca = editMarca;
    }

    public List<EntidadServicio> getListMarcasRue() {
        return listMarcasRue;
    }

    public void setListMarcasRue(List<EntidadServicio> listMarcasRue) {
        this.listMarcasRue = listMarcasRue;
    }

    public EntidadServicio getMarcaRueSelected() {
        return marcaRueSelected;
    }
  
    public void setMarcaRueSelected(EntidadServicio marcaRueSelected) {
        this.marcaRueSelected = marcaRueSelected;
    }

    public boolean isRueEditable() {
        return rueEditable;
    }

    public void setRueEditable(boolean rueEditable) {
        this.rueEditable = rueEditable;
    }

    public List<EntidadServicio> getListTitulares() {
        if(listTitulares == null) listTitulares = new ArrayList<>();
        return listTitulares;
    }

    public void setListTitulares(List<EntidadServicio> listTitulares) {
        this.listTitulares = listTitulares;
    }

    public EntidadServicio getTitularSelected() {
        return titularSelected;
    }

    public void setTitularSelected(EntidadServicio titularSelected) {
        this.titularSelected = titularSelected;
    }
    
    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public List<Vehiculo> getLstVehiculos() {
        try{
            lstVehiculos = vehiculoFacade.findAll();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Vehículos regstrados. " + ex.getMessage());
        }
        return lstVehiculos;
    }

    public void setLstVehiculos(List<Vehiculo> lstVehiculos) {
        this.lstVehiculos = lstVehiculos;
    }

    public List<Vehiculo> getLstVehiculosFilter() {
        return lstVehiculosFilter;
    }

    public void setLstVehiculosFilter(List<Vehiculo> lstVehiculosFilter) {
        this.lstVehiculosFilter = lstVehiculosFilter;
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

    public List<Vehiculo> getLstRevisions() {
        return lstRevisions;
    }

    public void setLstRevisions(List<Vehiculo> lstRevisions) {
        this.lstRevisions = lstRevisions;
    }

    public String getMatBusqRue() {
        return matBusqRue;
    }

    public void setMatBusqRue(String matBusqRue) {
        this.matBusqRue = matBusqRue;
    }

    public ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo getVehiculoRue() {
        return vehiculoRue;
    }

    public void setVehiculoRue(ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo vehiculoRue) {
        this.vehiculoRue = vehiculoRue;
    }

    public List<EntidadServicio> getListModelos() {
        if(listModelos == null) listModelos = new ArrayList<>();
        return listModelos;
    }

    public void setListModelos(List<EntidadServicio> listModelos) {
        this.listModelos = listModelos;
    }

    public EntidadServicio getModeloSelected() {
        return modeloSelected;
    }

    public void setModeloSelected(EntidadServicio modeloSelected) {
        this.modeloSelected = modeloSelected;
    }

    public List<EntidadServicio> getListMarcas() {
        if(listMarcas == null) listMarcas = new ArrayList<>();
        return listMarcas;
    }

    public void setListMarcas(List<EntidadServicio> listMarcas) {
        this.listMarcas = listMarcas;
    }

    public EntidadServicio getMarcaSelected() {
        return marcaSelected;
    }

    public void setMarcaSelected(EntidadServicio marcaSelected) {
        this.marcaSelected = marcaSelected;
    }

    public String getRueVehiculoMatricula() {
        return rueVehiculoMatricula;
    }

    public void setRueVehiculoMatricula(String rueVehiculoMatricula) {
        this.rueVehiculoMatricula = rueVehiculoMatricula;
    }

    public int getRueVehiculoAnio() {
        return rueVehiculoAnio;
    }

    public void setRueVehiculoAnio(int rueVehiculoAnio) {
        this.rueVehiculoAnio = rueVehiculoAnio;
    }
    
    
    /**********************
     * Métodos de inicio **
     * ********************/

    @PostConstruct
    public void init(){
        vehiculo = new Vehiculo();
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
    } 
    
    /**
     * Método para buscar el Vehículo en el RUE a partir de la matrícula ingresada
     */
    public void buscarVehiculoRue(){
        vehiculoRue = obtenerVehiculoRueByMat();
        if(vehiculoRue == null){
            JsfUtil.addErrorMessage("El Vehículo correspondiente a la matrícula ingresada no está registrado en el Registro Unico.");
        }
    }   
    
    /**
     * Método para preparar la apertura del formulario de búsqueda
     */
    public void prepareBuscarMat(){
        matBusqRue = "";
        vehiculoRue = new ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo();
    }    
    
    /**
     * Método para setear los datos obtenidos del RUE en el Vehículo
     */
    public void setearVehiculoByRue(){
        if(vehiculoRue != null){
            // cacheo en el Vehículo del RUE
            vehiculo.setIdRue(vehiculoRue.getId());
            vehiculo.setMatricula(vehiculoRue.getMatricula());
            vehiculo.setMarca(vehiculoRue.getModelo().getMarca().getNombre());
            vehiculo.setModelo(vehiculoRue.getModelo().getNombre());
            vehiculo.setAnio(vehiculoRue.getAnio());
            // obtengo el Titular a partir de la Persona del RUE
            Persona tit;
            if(vehiculo.getId() == null){
                tit = perFacade.getByIdRue(vehiculoRue.getEmpresa().getId());
            }else{
                tit = perFacade.getByIdRue(titularSelected.getId());
            }
            
            vehiculo.setTitular(tit);
            
            view = false;
            edit = true;
        }else{
            JsfUtil.addErrorMessage("Debe seleccionar un Vehículo del Registro Unico.");
        }
    }    
    
    /**
     * Método para actualizar el listado de Modelos al seleccionar una Marca
     * para la inserción de Vehículos en el RUP
     */
    public void marcaChangeListener(){
        getModelosSrv(marcaSelected.getId());
    }    
    
    /**
     * Método para limpiar el formulario de inserción de Vehículo en el RUE
     */
    public void limpiarFormVehiculoRue() {
        rueVehiculoMatricula = "";
        rueVehiculoAnio = 0;
        modeloSelected = new EntidadServicio();
        marcaSelected = new EntidadServicio();
        titularSelected = new EntidadServicio();
        listModelos = new ArrayList<>();
        listMarcas = new ArrayList<>();
        listTitulares = new ArrayList<>();
        prepareNewInsertRue();
    }    
    
    /**
     * Método para habilitar el formulario de creación de un Vehículo
     */
    public void prepareNew(){
        vehiculo = new Vehiculo();
        view = false;
        edit = true;
    }    
    
    /**
     * Método para habilitar la vista edit del formulario.
     * En este caso, además se carga el vehiculoRue con los datos obtenidos 
     * a partir del idRue del Vehículo local
     * Para el resto crear y editar.
     */
    public void prepareEdit(){
        cargarVehiculo();
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
     * Método para iniciar el registro de un Vehículo en el RUE
     */
    public void prepareNewInsertRue(){
        rueVehiculoMatricula = "";
        cargarTitulares();
        List<EntidadServicio> lMarcas = new ArrayList();
        listMarcas = cargarMarcas(lMarcas);
    } 
    
    /**
     * Método para preparar la edición de un Vehículo en el RUE
     */
    public void preparaEditRue(){
        rueEditable = true;
        vehiculoRue = buscarVehiculoRueById();
        if(Objects.equals(vehiculoRue.getIdProvinciaGt(), Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")))){
            cargarTitulares();
            List<EntidadServicio> lMarcas = new ArrayList();
            listMarcas = cargarMarcas(lMarcas);
            
            // seteo el año y la matrícula
            rueVehiculoMatricula = vehiculoRue.getMatricula();
            rueVehiculoAnio = vehiculoRue.getAnio();
            
            marcaSelected = new EntidadServicio();
            marcaSelected.setId(vehiculoRue.getModelo().getMarca().getId());
            marcaSelected.setNombre(vehiculoRue.getModelo().getMarca().getNombre());
            
            // cargo los modelos correspondientes a la marca seleccionada
            getModelosSrv(marcaSelected.getId());
            
            modeloSelected = new EntidadServicio();
            modeloSelected.setId(vehiculoRue.getModelo().getId());
            modeloSelected.setNombre(vehiculoRue.getModelo().getNombre());
            
            // si tiene titular asignado seteo su nombre completo y su id
            if(vehiculoRue.getEmpresa() != null){
                titularSelected = new EntidadServicio();
                titularSelected.setId(vehiculoRue.getEmpresa().getId());
                // según sea física o jurídica seteo el valor correspondiente
                if(vehiculoRue.getEmpresa().getNombreCompleto() != null){
                    titularSelected.setNombre(vehiculoRue.getEmpresa().getNombreCompleto());
                }else{
                    titularSelected.setNombre(vehiculoRue.getEmpresa().getRazonSocial());
                }
            }
        }else{
            rueEditable = false;
        }
    }    
    
    /**
     * Método para preparar el acceso al diálogo de gestión de Marcas de Vehículos en el RUE
     */
    public void prepareMarcasRue(){
        marcaRue = new Marca();
    }    
    
    /**
     * Método para preparar el acceso al diálogo de gestión de Modelos de Vehículos en el RUE
     */
    public void prepareModelosRue(){
        modeloRue = new Modelo();
    }
    
    /**
     * Método para preparar la edición de una Marca en el RUE
     */
    public void prepareEditMarca(){
        nombreMarcaRue = marcaRue.getNombre();
        viewMarca = false;
        editMarca = true;      
    }
    
    /**
     * Método para preparar la edición de un Modelo en el RUE
     */
    public void prepareEditModelo(){
        nombreModeloRue = modeloRue.getNombre();
        marcaRueSelected = new EntidadServicio();
        marcaRueSelected.setId(modeloRue.getMarca().getId());
        marcaRueSelected.setNombre(modeloRue.getMarca().getNombre());
        viewModelo = false;
        editModelo = true;   
    }
    
    /**
     * Método para limpiar el formulario de Técnicos y apoderados
     */
    public void limpiarForm() {   
        vehiculo = new Vehiculo();
    } 
    
    /**
     * Método para limpiar el formulario de registro/edición de Marcas de Vehículos
     */
    public void limpiarFormMarcaRue(){
        marcaRue = new Marca();
        nombreMarcaRue = "";
    }
    
    /**
     * Método para limpiar el formulario de registro/edición de Modelos de Vehículos
     */
    public void limpiarFormModeloRue(){
        marcaRueSelected = new EntidadServicio();
        nombreModeloRue = "";
    }

    /**
     * Método para mostrar los datos del Vehículo registrado en el RUE
     * En la vista detalle
     */
    public void verDatosRue(){
        cargarVehiculo();
    }    
    
    
    /***********************
     * Métodos operativos **
     ***********************/
    /**
     * Método para deshabilitar un Vehículo. Modificará su condición de habilitado a false.
     * Los Vehículos deshabilitados no estarán disponibles para su selección.
     */
    public void deshabilitar(){ 
        try{
            vehiculo.setHabilitado(false);
            vehiculoFacade.edit(vehiculo);
            vehiculo = new Vehiculo();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Vehículo seleccionada: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a los Vehículos a su condición normal.
     */
    public void habilitar(){
        try{
            vehiculo.setHabilitado(true);
            vehiculoFacade.edit(vehiculo);
            vehiculo = new Vehiculo();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Vehículo seleccionado: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para persistir un Vehículo en la API RUE, validando los datos, tanto para edición como insert
     */
    public void saveVehiculoRue(){
        boolean valida = true;
        String mensaje = "", valMat, valMod, valAnio;
        
        if(vehiculoRue == null){
            vehiculoRue = new ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo();
        }
        
        // valido el Modelo
        valMod = validarModelo();
        if(!valMod.equals("")){
            valida = false;
            if(mensaje.equals("")){
                mensaje = valMod;
            }else{
                mensaje = mensaje + " " + valMod;
            }
        }
        
        // valido el año
        valAnio = validarAnio();
        if(!valAnio.equals("")){
            valida = false;
            if(mensaje.equals("")){
                mensaje = valAnio;
            }else{
                mensaje = mensaje + " " + valAnio;
            }
        }
        
        // valido la Matrícula
        if(!rueVehiculoMatricula.equals("")){
            valMat = validarMatricula();
            if(!valMat.equals("")){
                valida = false;
                if(mensaje.equals("")){
                    mensaje = valMat;
                }else{
                    mensaje = mensaje + " " + valMat;
                }
            }
        }else{
            valida = false;
            if(mensaje.equals("")){
                mensaje = "Debe ingresar una matrícula.";
            }else{
                mensaje = mensaje + " Debe ingresar una matrícula.";
            }
        }

        
        // si validó persisto
        if(valida){
            try{
                // seteo el Vehículo con los campos del formulario
                // obtengo el modelo desde el API RUE
                Modelo modelo;
                modeloClient = new ModeloClient();
                modelo = modeloClient.find_JSON(Modelo.class, String.valueOf(modeloSelected.getId()));
                // cierro el cliente
                modeloClient.close();
                // seteo el Modelo
                vehiculoRue.setModelo(modelo);
                // seteo el año
                vehiculoRue.setAnio(rueVehiculoAnio);
                // el idRue ya está asignado como id de titularSelected
                //Persona per = perFacade.find(titularSelected.getId());
                vehiculoRue.setIdTitular(titularSelected.getId());
                
                if(vehiculoRue.getId() == 0){
                    // seteo la matrícula solo si estoy insertando
                    vehiculoRue.setMatricula(rueVehiculoMatricula.toUpperCase());
                }
                
                // seteo la provincia origen
                vehiculoRue.setIdProvinciaGt(Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")));
                vehiculoRue.setProvinciaGestion(ResourceBundle.getBundle("/Config").getString("Provincia"));
                // seteo la fecha de alta
                vehiculoRue.setFechaAlta(new Date(System.currentTimeMillis()));
                
                
                // utilizo el cliente rest según corresponda
                vehiculoClient = new VehiculoClient();
                Response res;
                if(vehiculoRue.getId() == 0){
                    res = vehiculoClient.create_JSON(vehiculoRue);
                }else{
                    res = vehiculoClient.edit_JSON(vehiculoRue, String.valueOf(vehiculoRue.getId()));
                }
                
                vehiculoClient.close();
                
                switch (res.getStatus()) {
                    case 201:
                        JsfUtil.addSuccessMessage("El Vehículo se registró exitosamente en el Registro Unico, "
                                + "para continuar, búsquelo por la matrícula ingresada para registrarla localmente.");
                        limpiarFormVehiculoRue();
                        break;
                    case 200:
                        JsfUtil.addSuccessMessage("El Vehículo se actualizó exitosamente en el Registro Unico, "
                                + "para completar la actualización, guarde los datos del formulario de edición.");
                        
                        // obtengo el Titular a partir de la Persona del RUE
                        setearVehiculoByRue();
                        // limpio el formulario
                        limpiarFormVehiculoRue();
                        break;
                    default:
                        JsfUtil.addErrorMessage("No se pudo guardar el Vehículo en el Registro Unico: " + res.getStatus() + " Error del servidor RUE.");
                        break;
                }

            }catch(NumberFormatException ex){
                JsfUtil.addErrorMessage("Hubo un error al intentar guardar el Vehículo en el Registro Unico: " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("No se pudieron validar los datos ingresados: " + mensaje);
        }
    }
    
    /**
     * Método para guardar el Vehículo de forma local, sea inserción o edición.
     * Previa validación
     */      
    public void saveVehiculo(){
        boolean valida = true;
        try{
            Vehiculo vehExitente = vehiculoFacade.getExistente(vehiculo.getMatricula());
            // valido por el nombre
            if(vehExitente != null){
                if(vehiculo.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!vehExitente.equals(vehiculo)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            // si validó, continúo
            if(valida){
                vehiculo.setUsuario(usLogueado);
                // persisto la persona según sea edición o insercion
                if(vehiculo.getId() != null){
                    vehiculoFacade.edit(vehiculo);
                    JsfUtil.addSuccessMessage("El Vehículo fue guardado con exito");
                }else{
//                    // si tiene titular lo seteo
//                    if(titularSelected != null){    
//                        // obtengo el titular
//                        Persona per = perFacade.find(titularSelected.getId());
//                        vehiculo.setTitular(per);
//                    }
                    // seteo la fecha de alta y habilitado
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    vehiculo.setFechaAlta(fechaAlta);
                    vehiculo.setHabilitado(true);
                    vehiculoFacade.create(vehiculo);
                    JsfUtil.addSuccessMessage("El Vehículo fue registrado con exito");
                }
                vehiculo = new Vehiculo();
                edit = false;
            }else{
                JsfUtil.addErrorMessage("Ya existe un Vehículo registrado con la matrícula que se pretende ingresar.");
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Vehículo : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar la Marca de Vehículos en el RUE de forma local, sea inserción o edición.
     * Previa validación
     */      
    public void saveMarcaRue(){
        boolean valida = true;
        if(!nombreMarcaRue.equals("")){
            try{
                // instancio el cliente para la consulta sobre existencia del nombre
                marcaClient = new MarcaClient();
                Marca mrcRueExistente = marcaClient.findByName_JSON(Marca.class, nombreMarcaRue.toUpperCase());
                // valido por el nombre
                if(mrcRueExistente != null){
                    if(!marcaRue.getId().equals(Long.valueOf(0))){
                        // si edita, no habilito si no es el mismo
                        if(!mrcRueExistente.equals(marcaRue)) valida = false;
                    }else{
                        // si no edita no habilito de ninguna manera
                        valida = false;
                    }
                }
                // si validó, continúo
                if(valida){
                    // seteo el nombre
                    marcaRue.setNombre(nombreMarcaRue.toUpperCase());
                    // persisto la Marca según sea edición o insercion
                    Response res;
                    if(marcaRue.getId().equals(Long.valueOf(0))){
                        res = marcaClient.create_JSON(marcaRue);
                    }else{
                        res = marcaClient.edit_JSON(marcaRue, String.valueOf(marcaRue.getId()));
                    }
                    marcaClient.close();

                    switch (res.getStatus()) {
                        case 201:
                            JsfUtil.addSuccessMessage("La Marca se registró exitosamente en el Registro Unico.");
                            limpiarFormMarcaRue();
                            break;
                        case 200:
                            JsfUtil.addSuccessMessage("La Marca se actualizó exitosamente en el Registro Unico.");
                            limpiarFormMarcaRue();
                            break;
                        default:
                            JsfUtil.addErrorMessage("No se pudo guardar la Marca en el Registro Unico: " + res.getStatus() + " Error del servidor RUE.");
                            break;
                    }
                }
                marcaRue = new Marca();
                edit = false;
            }catch(ClientErrorException ex){
                JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Marca : " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("Debe ingresar un nombre para la Marca.");
        }
    }    
    
    /**
     * Método para guardar el Modelo de Vehículos en el RUE de forma local, sea inserción o edición.
     * Previa validación
     */      
    public void saveModeloRue(){
        boolean valida = true;
        if(!nombreModeloRue.equals("") && marcaRueSelected.getId() != null){
            try{
                // instancio el cliente para la consulta sobre existencia del nombre
                modeloClient = new ModeloClient();
                Modelo modRueExistente = modeloClient.findByName_JSON(Modelo.class, nombreModeloRue.toUpperCase());
                // valido por el nombre
                if(modRueExistente != null){
                    if(!modeloRue.getId().equals(Long.valueOf(0))){
                        // si edita, no habilito si no es el mismo
                        if(!modRueExistente.equals(modeloRue)) valida = false;
                    }else{
                        // si no edita no habilito de ninguna manera
                        valida = false;
                    }
                }
                // si validó, continúo
                if(valida){
                    // seteo el nombre
                    modeloRue.setNombre(nombreModeloRue.toUpperCase());
                    // obtengo la Marca seleccionada
                    Marca marca;
                    marcaClient = new MarcaClient();
                    marca = marcaClient.find_JSON(Marca.class, String.valueOf(marcaRueSelected.getId()));
                    // cierro el cliente
                    marcaClient.close();
                    // seteo la Marca
                    modeloRue.setMarca(marca);
                    // persisto la Marca según sea edición o insercion
                    Response res;
                    if(modeloRue.getId().equals(Long.valueOf(0))){
                        res = modeloClient.create_JSON(modeloRue);
                    }else{
                        res = modeloClient.edit_JSON(modeloRue, String.valueOf(modeloRue.getId()));
                    }
                    modeloClient.close();

                    switch (res.getStatus()) {
                        case 201:
                            JsfUtil.addSuccessMessage("El Modelo se registró exitosamente en el Registro Unico.");
                            limpiarFormModeloRue();
                            break;
                        case 200:
                            JsfUtil.addSuccessMessage("El Modelo se actualizó exitosamente en el Registro Unico.");
                            limpiarFormModeloRue();
                            break;
                        default:
                            JsfUtil.addErrorMessage("No se pudo guardar el Modelo en el Registro Unico: " + res.getStatus() + " Error del servidor RUE.");
                            break;
                    }
                }
                modeloRue = new Modelo();
                edit = false;
            }catch(ClientErrorException ex){
                JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Modelo: " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("Debe ingresar un Nombre y una Marca para el Modelo.");
        }
    }    
    
    
    /*********************
     * Métodos privados **
     *********************/ 
    /**
     * Método que carga el listado de Modelos según la Marca seleccionada
     * @param id : de la Marca a obtener los modelos
     */    
    private void getModelosSrv(Long idMarca) {
        EntidadServicio modelo;
        List<Modelo> listSrv;
        
        try{
            // instancio el cliente para la selección de los Modelos
            marcaClient = new MarcaClient();
            // obtngo el listado
            GenericType<List<Modelo>> gType = new GenericType<List<Modelo>>() {};
            Response response = marcaClient.findModelosByMarca_JSON(Response.class, String.valueOf(idMarca));
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listModelos = new ArrayList<>();
            for(Modelo mod : listSrv){
                modelo = new EntidadServicio(mod.getId(), mod.getNombre());
                listModelos.add(modelo);
            }
            
            marcaClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Modelos de los Vehículos de la Marca seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Modelos de Vehículos por Marca "
                    + "del servicio REST del RUE", ex.getMessage()});
        }
    }

    /**
     * Método que obtiene un Vehículo del RUE mediante el uso de la API correspondiente
     * @return 
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo obtenerVehiculoRueByMat() {
        List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo> listVehiculos = new ArrayList<>();
        
        try{
            // instancio el cliente para la obtención de la Persona
            vehiculoClient = new VehiculoClient();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>>() {};
            Response response = vehiculoClient.findByQuery_JSON(Response.class, null, matBusqRue.toUpperCase(), null);
            listVehiculos = response.readEntity(gType); 
            // cierro el cliente
            vehiculoClient.close();
            if(listVehiculos.isEmpty()){
                return null;
            }else{
                return listVehiculos.get(0);
            }

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos del Vehículo del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Vehículo por su matrícula desde el "
                    + "servicio REST de RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método para validar la Matrícula del Vehículo
     * Que no esté registrado ya en el RUE
     * @return 
     */
    private String validarMatricula() {
        String result = "";
        try{
            // instancio el cliente para verificar la existencia de una Persona con el mismo CUIT
            List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo> listVehiculos = new ArrayList<>();
            vehiculoClient = new VehiculoClient();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>>() {};
            Response response = vehiculoClient.findByQuery_JSON(Response.class, null, rueVehiculoMatricula, null);
            listVehiculos = response.readEntity(gType);
            // cierro el cliente
            vehiculoClient.close();
            if(!listVehiculos.isEmpty()){
                // actúo según sea una actualización o un insert
                if(vehiculoRue.getId() == 0){
                    // si inserta devuelvo el error
                    result = "Ya existe un Vehículo en el Registro Unico con la matrícula ingresada, por favor, verifique los datos.";
                }else{
                    // si edita, verifico que no sea la misma Persona
                    if(!Objects.equals(listVehiculos.get(0).getId(), vehiculoRue.getId())){
                        // si no son la misma Persona, devuelvo error
                        result = "Ya existe un Vehículo en el Registro Unico con el matrícula ingresada, por favor, verifique los datos.";
                    }
                }
            }
        }catch(ClientErrorException ex){
            result = "Hubo error validando la matrícula en el Registro Unico. " + ex.getMessage();
        }
        return result;
    }

    /**
     * Método para cargar el listado de Transportistas
     */
    private void cargarTitulares() {
        EntidadServicio titular;
        List<Persona> listTransp;
        
        try{
            // obtengo los Transportistas
            Parametrica rolTansp = obtenerRol(ResourceBundle.getBundle("/Config").getString("Transportista"));
            listTransp = perFacade.findByRol(rolTansp);
            listTitulares = new ArrayList<>();
            for(Persona tit : listTransp){
                // asingno los datos del RUE cacheados para no contactar el servicio
                titular = new EntidadServicio(tit.getIdRue(), tit.getNombreCompleto());
                listTitulares.add(titular);
            }

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Transportistas para su selección. " + ex.getMessage());
        }
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

    /**
     * Método para cargar el listado de Marcas para su selección
     */    
    private List<EntidadServicio> cargarMarcas(List<EntidadServicio> lMarcas) {
        EntidadServicio marca;
        List<Marca> listSrv;

        try{
            // instancio el cliente para la selección de las Marcas de Vehículos
            marcaClient = new MarcaClient();
            // obtengo el listado de marcas
            GenericType<List<Marca>> gType = new GenericType<List<Marca>>() {};
            Response response = marcaClient.findAll_JSON(Response.class);
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            lMarcas = new ArrayList<>();
            for(Marca mrc : listSrv){
                marca = new EntidadServicio(mrc.getId(), mrc.getNombre());
                lMarcas.add(marca);
            }
            
            // cierro el cliente
            marcaClient.close();
            
            return lMarcas;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Marcas para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Marcas desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
            return null;
        }        
    }

    /**
     * Método que obtiene una Vehículo desde la API RUE, según su id
     * @return 
     */    
    private ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo buscarVehiculoRueById() {
        try{
            // instancio el cliente para la selección de la Persona RUE
            vehiculoClient = new VehiculoClient();
            ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo response = vehiculoClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo.class, String.valueOf(vehiculo.getIdRue()));
            vehiculoClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo el Vehículo del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Vehículo por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método que obtine un Vehículo del RUE según su id
     */
    private void cargarVehiculo() {
        try{
            // instancio el cliente para la obtención de la Persona
            vehiculoClient = new VehiculoClient();
            vehiculoRue = vehiculoClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo.class, String.valueOf(vehiculo.getIdRue()));
            vehiculoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos del Vehículo del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Vehículo por id desde el "
                    + "servicio REST de RUE", ex.getMessage()});
        }
    }
    
    /**
     * Obtiene el vehículo a partir de la id
     * método para el converter
     * @param key
     * @return 
     */
    private Object getVehiculo(Long key) {
        return vehiculoFacade.find(key);
    }

    /**
     * Método que valida la selección del modelo del vehículo a registrar
     * @return 
     */
    private String validarModelo() {
        String result = "";
        if(vehiculoRue.getModelo() == null){
            result = "Debe seleccionar un Modelo para el Vehículo.";
        }
        return result;
    }

    private String validarAnio() {
        String result = "";
        if(rueVehiculoAnio <= 1950){
            result = "Debe ingresar un año válido para el modelo.";
        }
        return result;
    }

    
    /*****************************
    ** Converter para Inmueble  **
    ******************************/ 
    @FacesConverter(forClass = Vehiculo.class)
    public static class VehiculoConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbVehiculo controller = (MbVehiculo) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbVehiculo");
            return controller.getVehiculo(getKey(value));
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
            if (object instanceof Vehiculo) {
                Vehiculo o = (Vehiculo) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Vehiculo.class.getName());
            }
        }
    }      
}
