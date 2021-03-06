
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoClaseFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoEspecieLocalFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoUnidadMedidaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.tax.client.EspecieClient;
import ar.gob.ambiente.sacvefor.localcompleto.tax.client.FamiliaClient;
import ar.gob.ambiente.sacvefor.localcompleto.tax.client.GeneroClient;
import ar.gob.ambiente.sacvefor.localcompleto.tax.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.especies.Especie;
import ar.gob.ambiente.sacvefor.servicios.especies.Familia;
import ar.gob.ambiente.sacvefor.servicios.especies.Genero;
import java.io.IOException;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Bean de respaldo para la administración de Entidades vinculadas a los Productos forestales:
 * ProductoUnidadMedida
 * ProductoClase
 * ProductoEspecieLocal
 * Producto
 * Gestiona las vistas prod/
 * @author rincostante
 */
public class MbProducto {
    ///////////////////////////
    // campos para gestionar //
    ///////////////////////////
    
    /**
     * Variable privada: objeto a gestionar
     */
    private Producto producto;
    
    /**
     * Variable privada: especie local del producto
     */
    private ProductoEspecieLocal prodEspecie;
    
    /**
     * Variable privada: clase del producto
     */
    private ProductoClase prodClase;
    
    /**
     * Variable privada: tasa del prodcto
     */
    private ProductoTasa prodTasa;
    
    /**
     * Variable privada: unidad de medida del producto
     */
    private ProductoUnidadMedida prodUnidad;
    
    /**
     * Variable privada: listado de los productos registrados
     */
    private List<Producto> lstProductos;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de Productos
     */
    private List<Producto> lstProdFilters;
    
    /**
     * Variable privada: listado de las especies locales
     */
    private List<ProductoEspecieLocal> lstProdEspecie;
    
    /**
     * Variable privada: listado para el filtrado de las especies locales
     */
    private List<ProductoEspecieLocal> lstProdEspFilters;
    
    /**
     * Variable privada: listado de las clases de productos
     */
    private List<ProductoClase> lstProdClases;
    
    /**
     * Variable privada: listado para el filtrado de las clases de los productos
     */
    private List<ProductoClase> lstProdClsFilters;
    
    /**
     * Variable privada: listado de los tipos de tasas a asignar al producto
     */
    private List<Parametrica> lstTiposTasas;
    
    /**
     * Variable privada: listado de las unidades de medida de los productos
     */
    private List<ProductoUnidadMedida> lstProdUnidades;
    
    /**
     * Variable privada: litado para el filtrado de las unidades de medida
     */
    private List<ProductoUnidadMedida> lstProdUnidFilters;
    
    /**
     * Variable privada: listado de los tipos numéricos
     */
    private List<Parametrica> lstTipoNumerico; 
    
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
    private static final Logger logger = Logger.getLogger(MbProducto.class.getName());
    
    /**
     * Variable privada: listado de las revisiones de la Persona
     */
    private List<Producto> lstRevisions; 
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */
    private Usuario usLogueado;
    
    ///////////////////////////////////////////////////
    // acceso a datos mediante inyección de recursos //
    ///////////////////////////////////////////////////
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Producto
     */  
    @EJB
    private ProductoFacade prodFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ProductoEspecieLocal
     */  
    @EJB
    private ProductoEspecieLocalFacade prodEspFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ProductoClase
     */  
    @EJB
    private ProductoClaseFacade prodClsFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ProductoUnidadMedida
     */  
    @EJB
    private ProductoUnidadMedidaFacade prodUnidadFacade;
    
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
    ////////////////////////////////////////////////////////////////////
    // Clientes REST para la gestión del API de taxonomía de especies //
    ////////////////////////////////////////////////////////////////////
    
    /**
     * Variable privada: Cliente para la API Rest de Especie en el Taxonomías
     */
    private EspecieClient especieClient; 
    
    /**
     * Variable privada: Cliente para la API Rest de Familias en el Taxonomías
     */    
    private FamiliaClient familiaClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Géneros en el Taxonomías
     */        
    private GeneroClient generoClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Usuarios en Taxonomías
     */
    private UsuarioClient usuarioClient;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Taxonomías
     */
    private Token token;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Taxonomías
     */
    private String strToken;
    
    ///////////////////////////////////////////////////////////////////////////////
    // Campos para la gestión de los elementos forestales provenientes de la API //
    // de taxonomía en los combos del formulario. /////////////////////////////////
    // Las Entidades de servicio se componen de un par {id | nombre} //////////////
    ///////////////////////////////////////////////////////////////////////////////
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Familias
     */      
    private List<EntidadServicio> listFamilias;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Familia seleccionada del combo
     */    
    private EntidadServicio familiaSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los Géneros
     */     
    private List<EntidadServicio> listGeneros;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos del Génerop seleccionado del combo
     */       
    private EntidadServicio generoSelected;
    
     /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Especies
     */     
    private List<EntidadServicio> listEspecies;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Especie seleccionada del combo
     */       
    private EntidadServicio especieSelected;

    ///////////////////////
    // Métodos de acceso //
    ///////////////////////
    public Producto getProducto() {
        return producto;
    }
         
    public List<Parametrica> getLstTiposTasas() {
        return lstTiposTasas;
    }
 
    public void setLstTiposTasas(List<Parametrica> lstTiposTasas) {
        this.lstTiposTasas = lstTiposTasas;
    }

    public ProductoTasa getProdTasa() {
        return prodTasa;
    }

    public void setProdTasa(ProductoTasa prodTasa) {
        this.prodTasa = prodTasa;
    }

    public List<Parametrica> getLstTipoNumerico() {
        return lstTipoNumerico;
    }
 
    public void setLstTipoNumerico(List<Parametrica> lstTipoNumerico) {
        this.lstTipoNumerico = lstTipoNumerico;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ProductoEspecieLocal getProdEspecie() {
        return prodEspecie;
    }

    public void setProdEspecie(ProductoEspecieLocal prodEspecie) {
        this.prodEspecie = prodEspecie;
    }

    public ProductoClase getProdClase() {
        return prodClase;
    }

    public void setProdClase(ProductoClase prodClase) {
        this.prodClase = prodClase;
    }

    public ProductoUnidadMedida getProdUnidad() {
        return prodUnidad;
    }

    public void setProdUnidad(ProductoUnidadMedida prodUnidad) {
        this.prodUnidad = prodUnidad;
    }

    public List<Producto> getLstProductos() {
        lstProductos = prodFacade.findAll();
        return lstProductos;
    }

    public void setLstProductos(List<Producto> lstProductos) {
        this.lstProductos = lstProductos;
    }

    public List<Producto> getLstProdFilters() {
        return lstProdFilters;
    }

    public void setLstProdFilters(List<Producto> lstProdFilters) {
        this.lstProdFilters = lstProdFilters;
    }

    public List<ProductoEspecieLocal> getLstProdEspecie() {
        lstProdEspecie = prodEspFacade.findAll();
        return lstProdEspecie;
    }

    public void setLstProdEspecie(List<ProductoEspecieLocal> lstProdEspecie) {
        this.lstProdEspecie = lstProdEspecie;
    }

    public List<ProductoEspecieLocal> getLstProdEspFilters() {
        return lstProdEspFilters;
    }

    public void setLstProdEspFilters(List<ProductoEspecieLocal> lstProdEspFilters) {
        this.lstProdEspFilters = lstProdEspFilters;
    }

    public List<ProductoClase> getLstProdClases() {
        lstProdClases = prodClsFacade.findAll();
        return lstProdClases;
    }

    public void setLstProdClases(List<ProductoClase> lstProdClases) {
        this.lstProdClases = lstProdClases;
    }

    public List<ProductoClase> getLstProdClsFilters() {
        return lstProdClsFilters;
    }

    public void setLstProdClsFilters(List<ProductoClase> lstProdClsFilters) {
        this.lstProdClsFilters = lstProdClsFilters;
    }

    public List<ProductoUnidadMedida> getLstProdUnidades() {
        lstProdUnidades = prodUnidadFacade.findAll();
        return lstProdUnidades;
    }

    public void setLstProdUnidades(List<ProductoUnidadMedida> lstProdUnidades) {
        this.lstProdUnidades = lstProdUnidades;
    }

    public List<ProductoUnidadMedida> getLstProdUnidFilters() {
        return lstProdUnidFilters;
    }

    public void setLstProdUnidFilters(List<ProductoUnidadMedida> lstProdUnidFilters) {
        this.lstProdUnidFilters = lstProdUnidFilters;
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

    public List<Producto> getLstRevisions() {
        return lstRevisions;
    }

    public void setLstRevisions(List<Producto> lstRevisions) {
        this.lstRevisions = lstRevisions;
    }

    public List<EntidadServicio> getListFamilias() {
        return listFamilias;
    }

    public void setListFamilias(List<EntidadServicio> listFamilias) {
        this.listFamilias = listFamilias;
    }

    public EntidadServicio getFamiliaSelected() {
        return familiaSelected;
    }

    public void setFamiliaSelected(EntidadServicio familiaSelected) {
        this.familiaSelected = familiaSelected;
    }

    public List<EntidadServicio> getListGeneros() {
        return listGeneros;
    }

    public void setListGeneros(List<EntidadServicio> listGeneros) {
        this.listGeneros = listGeneros;
    }

    public EntidadServicio getGeneroSelected() {
        return generoSelected;
    }

    public void setGeneroSelected(EntidadServicio generoSelected) {
        this.generoSelected = generoSelected;
    }

    public List<EntidadServicio> getListEspecies() {
        return listEspecies;
    }

    public void setListEspecies(List<EntidadServicio> listEspecies) {
        this.listEspecies = listEspecies;
    }

    public EntidadServicio getEspecieSelected() {
        return especieSelected;
    }

    public void setEspecieSelected(EntidadServicio especieSelected) {
        this.especieSelected = especieSelected;
    }
    
    /**
     * Constructor
     */
    public MbProducto() {
    }
    
    ///////////////////////
    // Métodos de inicio //
    ///////////////////////

    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar
     * y el bean de sesión y el usuario
     */  
    @PostConstruct
    public void init(){
        prodUnidad = new ProductoUnidadMedida();
        prodClase = new ProductoClase();
        prodEspecie = new ProductoEspecieLocal();
        producto = new Producto();
        cargarFamilias();        
        TipoParam tipoParamNum = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoNumerico"));
        lstTipoNumerico = paramFacade.getHabilitadas(tipoParamNum);
        prodTasa = new ProductoTasa();
        TipoParam tipoParamTasa = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoTasa"));
        lstTiposTasas = paramFacade.getHabilitadas(tipoParamTasa);
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
    }    

    ////////////////////////
    // Métodos operativos //
    ////////////////////////
    
    /**
     * Método para actualizar el listado de Géneros según la Familia seleccionada
     */    
    public void familiaChangeListener(){
        prodEspecie = new ProductoEspecieLocal();
        getGenerosSrv(familiaSelected.getId());
    }      
    
    /**
     * Método para actualizar el listado de Especies según el Género seleccionado
     */    
    public void generoChangeListener(){
        getEspeciesSrv(generoSelected.getId());
    }   
    
    /**
     * Método para guardar la Unidad de medida, sea inserción o edición.
     * Previa validación
     */      
    public void saveUnidad(){
        boolean valida = true;
        String msgValidador = "";
        
        try{
            ProductoUnidadMedida unidadExitente = prodUnidadFacade.getExistenteByNombre(prodUnidad.getNombre().toUpperCase());
            // valido por el nombre
            if(unidadExitente != null){
                if(prodUnidad.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!unidadExitente.equals(prodUnidad)) valida = false;
                    msgValidador = "Ya existe una Unidad de medida con el nombre ingresado";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    msgValidador = "Ya existe una Unidad de medida con el nombre ingresado";
                }
            }
            // si el nombre validó, valido por la abreviatura
            if(valida){
                unidadExitente = prodUnidadFacade.getExistenteByAbrev(prodUnidad.getAbreviatura().toUpperCase());
                if(unidadExitente != null){
                    if(prodUnidad.getId() != null){
                        // si edita, no habilito si no es el mismo
                        if(!unidadExitente.equals(prodUnidad)) valida = false;
                        msgValidador = "Ya existe una Unidad de medida con la abreviatura ingresada";
                    }else{
                        // si no edita no habilito de ninguna manera
                        valida = false;
                        msgValidador = "Ya existe una Unidad de medida con la abreviatura ingresada";
                    }
                }
            }      
            // si continuó validando
            if(valida){
                String tempNombre = prodUnidad.getNombre();
                prodUnidad.setNombre(tempNombre.toUpperCase());
                String tempAbrev = prodUnidad.getAbreviatura();
                prodUnidad.setAbreviatura(tempAbrev.toUpperCase());
                if(prodUnidad.getId() != null){
                    prodUnidadFacade.edit(prodUnidad);
                    JsfUtil.addSuccessMessage("La Unidad de medida fue guardada con exito");
                }else{
                    prodUnidad.setHabilitado(true);
                    prodUnidadFacade.create(prodUnidad);
                    JsfUtil.addSuccessMessage("La Unidad de medida fue registrada con exito");
                }
            }else{
                JsfUtil.addErrorMessage(msgValidador + ", por favor verifique los datos ingresados.");
            }
            limpiarFormUnidad();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Unidad de medida : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar la Clase, sea inserción o edición.
     * Previa validación
     */      
    public void saveClase(){
        boolean valida = true;
        
        try{
            ProductoClase claseExitente = prodClsFacade.getExistente(prodClase.getNombre().toUpperCase(), prodClase.getUnidad());
            // valido por el nombre
            if(claseExitente != null){
                if(prodClase.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!claseExitente.equals(prodClase)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = prodClase.getNombre();
                prodClase.setNombre(tempNombre.toUpperCase());
                if(prodClase.getId() != null){
                    prodClsFacade.edit(prodClase);
                    JsfUtil.addSuccessMessage("La Clase de Producto fue guardada con exito");
                }else{
                    prodClase.setHabilitado(true);
                    prodClsFacade.create(prodClase);
                    JsfUtil.addSuccessMessage("La Clase de Producto fue registrada con exito");
                }  
            }else{
                JsfUtil.addErrorMessage("La Clase de Producto que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormClase();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Clase de Producto : " + ex.getMessage());
        }
    }     
    
    /**
     * Método para guardar la Especie, sea inserción o edición.
     * Previa validación
     */      
    public void saveEspecie(){
        boolean valida = true;
        ProductoEspecieLocal especieVulgarExistente, especieCientExistente;
        String msg = "";
        
        try{
            // valido por el nombre vulgar
            especieVulgarExistente = prodEspFacade.getExistenteXNomVulgar(prodEspecie.getNombreVulgar().toUpperCase());
            
            if(especieVulgarExistente != null){
                if(prodEspecie.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!especieVulgarExistente.equals(prodEspecie)){
                        valida = false;
                        msg = "Ya existe una Especie local con el nombre vulgar que está ingresando, por favor verifique los datos ingresados.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    msg = "Ya existe una Especie local con el nombre vulgar que está ingresando, por favor verifique los datos ingresados.";
                }
            }
            
            // valido por el nombre científico
            especieCientExistente = prodEspFacade.getExistenteXNomCientifico(especieSelected.getNombre());
            if(especieCientExistente != null){
                if(prodEspecie.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!especieCientExistente.equals(prodEspecie)) {
                        valida = false;
                        if(!msg.equals("")){
                            msg = msg + " " + "Ya existe una Especie local con el nombre científico que está ingresando, por favor verifique los datos ingresados.";
                        }else{
                            msg = "Ya existe una Especie local con el nombre científico que está ingresando, por favor verifique los datos ingresados.";
                        }
                        
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    if(!msg.equals("")){
                        msg = msg + " " + "Ya existe una Especie local con el nombre científico que está ingresando, por favor verifique los datos ingresados.";
                    }else{
                        msg = "Ya existe una Especie local con el nombre científico que está ingresando, por favor verifique los datos ingresados.";
                    }
                }
            }
            
            if(valida){
                String tempNombre = prodEspecie.getNombreVulgar();
                prodEspecie.setNombreVulgar(tempNombre.toUpperCase());
                // asigno la Especie del servicio
                prodEspecie.setIdTax(especieSelected.getId());
                // seteo el nombre científico (que viene como 'nombre' en la entidad de servicio)
                prodEspecie.setNombreCientifico(especieSelected.getNombre());
                if(prodEspecie.getId() != null){
                    prodEspFacade.edit(prodEspecie);
                    JsfUtil.addSuccessMessage("La Especie local fue guardada con exito");
                }else{
                    prodEspecie.setHabilitado(true);
                    prodEspFacade.create(prodEspecie);
                    JsfUtil.addSuccessMessage("La Especie local fue registrada con exito");
                }  
            }else{
                JsfUtil.addErrorMessage(msg);
            }
            limpiarFormEspecie();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Especie local : " + ex.getMessage());
        }
    }   
    
    /**
     * Método para guardar el Producto, sea inserción o edición.
     * Previa validación
     */      
    public void saveProducto(){
        boolean valida = true;
        
        try{
            Producto prodExitente = prodFacade.getExistente(producto.getClase(), producto.getEspecieLocal());
            // valido por el nombre
            if(prodExitente != null){
                if(producto.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!prodExitente.equals(producto)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                producto.setUsuario(usLogueado);
                
                if(producto.getId() != null){
                    prodFacade.edit(producto);
                    JsfUtil.addSuccessMessage("El Producto forestal fue guardado con exito");
                }else{
                    // seteo la fecha de alta
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    producto.setFechaAlta(fechaAlta);
                    // seteo condición de habilitado
                    producto.setHabilitado(true);
                    prodFacade.create(producto);
                    JsfUtil.addSuccessMessage("El Producto forestal fue registrado con exito");
                }  
            }else{
                JsfUtil.addErrorMessage("Ya existe una Producto forestal para esta Clase y Especie, por favor verifique los datos ingresados.");
            }
            limpiarFormProducto();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Producto forestal : " + ex.getMessage());
        }
    }        
    
    /**
     * Método para habilitar la vista nuevo/edit del formulario.
     * Para las especies: solo crear.
     * Para el resto crear y editar.
     */
    public void prepareNewEdit(){
        view = false;
        edit = true;
    }    
    
    /**
     * Método para habilitar la vista para editar en el formulario
     * para las especies
     */
    public void prepareEditSrv(){
        // cargo las entidades de taxonomía
        cargarEntidadesSrv(prodEspecie.getIdTax());
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
     * Método para deshabilitar una Unidad. Modificará su condición de habilitado a false.
     * Las Unidades deshabilitadas no estarán disponibles para su selección.
     */
    public void deshabilitarUnidad(){
        try{
            prodUnidad.setHabilitado(false);
            prodUnidadFacade.edit(prodUnidad);
            limpiarFormUnidad();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Unidad de medida: " + ex.getMessage());
        }
    }    

    /**
     * Método para deshabilitar una Clase. Modificará su condición de habilitado a false.
     * Las Clases deshabilitadas no estarán disponibles para su selección.
     */    
    public void deshabilitarClase(){
        try{
            prodClase.setHabilitado(false);
            prodClsFacade.edit(prodClase);
            limpiarFormClase();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Clase del Producto forestal: " + ex.getMessage());
        }
    }        
    
    /**
     * Método para deshabilitar una Especie. Modificará su condición de habilitado a false.
     * Las Especies deshabilitadas no estarán disponibles para su selección.
     */    
    public void deshabilitarEspecie(){
        try{
            prodEspecie.setHabilitado(false);
            prodEspFacade.edit(prodEspecie);
            limpiarFormEspecie();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Especie local: " + ex.getMessage());
        }
    }  
    
    /**
     * Método para deshabilitar un Producto. Modificará su condición de habilitado a false.
     * Los Productos deshabilitados no estarán disponibles para su selección.
     */    
    public void deshabilitarProducto(){
        try{
            producto.setHabilitado(false);
            prodFacade.edit(producto);
            limpiarFormEspecie();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Producto forestal: " + ex.getMessage());
        }
    }        
    
    /**
     * Metodo para volver a las Unidades a su condición normal.
     */
    public void habilitarUnidad(){
        try{
            prodUnidad.setHabilitado(true);
            prodUnidadFacade.edit(prodUnidad);
            limpiarFormUnidad();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Unidad de medida: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a las Clases a su condición normal.
     */
    public void habilitarClase(){
        try{
            prodClase.setHabilitado(true);
            prodClsFacade.edit(prodClase);
            limpiarFormClase();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Clase del Producto forestal: " + ex.getMessage());
        }
    }        
    
    /**
     * Metodo para volver a las Especies a su condición normal.
     */
    public void habilitarEspecie(){
        try{
            prodEspecie.setHabilitado(true);
            prodEspFacade.edit(prodEspecie);
            limpiarFormEspecie();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Especie local: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a los Productos a su condición normal.
     */
    public void habilitarProducto(){
        try{
            producto.setHabilitado(true);
            prodFacade.edit(producto);
            limpiarFormEspecie();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Producto forestal: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para limpiar el formulario de registro o edición de la Unidad de medida
     */
    public void limpiarFormUnidad() {
        prodUnidad = new ProductoUnidadMedida();
    }  
    
    /**
     * Método para limpiar el formulario de registro o edición de la Clase de Producto
     */
    public void limpiarFormClase() {
        prodClase = new ProductoClase();
    }    

    /**
     * Método para limpiar el formulario de registro o edición de la Especie local
     */
    public void limpiarFormEspecie() {
        prodEspecie = new ProductoEspecieLocal();
        familiaSelected = new EntidadServicio();
        generoSelected = new EntidadServicio();
        especieSelected = new EntidadServicio();
    } 
    
    /**
     * Método para inicializar el formulario de agregado de Tasas a un Producto
     */
    public void prepareAddTasa(){
        

    }

    /**
     * Método para limpiar el formulario de registro o edición del Producto
     */
    public void limpiarFormProducto() {
        producto = new Producto();
    }   

    //////////////////////////////////////
    // Métodos para la gestión de Tasas //
    //////////////////////////////////////
    /**
     * Método para agregar una Tasa al producto.
     * Primero valida que ya no esté agregada
     */
    public void agregarTasaProd(){
        boolean valida = true;
        
        // valido que la Tasa no esté ya aplicada al producto
        for(ProductoTasa ts : producto.getTasas()){
            if(Objects.equals(ts.getTipo(), prodTasa.getTipo())){
                valida = false;
                JsfUtil.addErrorMessage("La Tasa que está tratando de asociar, ya está vinculada al Producto.");
            }
        }
        
        try{
            if(valida){
                producto.getTasas().add(prodTasa);
                prodTasa = new ProductoTasa();
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al agregar la Tasa al Producto: " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el formulario de selección de Tasas para el Producto
     */
    public void limpiarFormTasaProd(){
        prodTasa = new ProductoTasa();
    }
    
    /**
     * Método para desagregar una Tasa de un Producto
     */
    public void desagregarTasaProd(){
        int i = 0, j = 0;
        
        for(ProductoTasa ts : producto.getTasas()){
            if(Objects.equals(ts.getId(), prodTasa.getId())){
                j = i;
            }
            i = i+= 1;
        }
        producto.getTasas().remove(j);
        prodTasa = new ProductoTasa();
    }

    
    //////////////////////
    // Métodos privados //
    //////////////////////
    /**
     * Método para obtener las Familias de Especies forestales del servicio de Taxonomía
     * Utilizado en init() y  cargarEntidadesSrv(Long idTax) 
     */
    private void cargarFamilias() {
        EntidadServicio familia;
        List<Familia> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTax();
            }else try {
                if(!token.isVigente()){
                    getTokenTax();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            
            // instancio el cliente para la selección de las provincias
            familiaClient = new FamiliaClient();
            // obtengo el listado de provincias 
            GenericType<List<Familia>> gType = new GenericType<List<Familia>>() {};
            Response response = familiaClient.findAll_JSON(Response.class, token.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listFamilias = new ArrayList<>();
            for(Familia fam : listSrv){
                familia = new EntidadServicio(fam.getId(), fam.getNombre());
                listFamilias.add(familia);
                //provincia = null;
            }
            // cierro el cliente
            familiaClient.close();
            
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Familias para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Familias desde "
                    + "el servicio REST de Taxonomía.", ex.getMessage()});
        }  
    }

    /**
     * Método para obtener el Producto por id, para el converter
     * @param key Long identificación del Producto
     * @return Object producto solicitado
     */
    private Object getProducto(Long key) {
        return prodFacade.find(key);
    }

    /**
     * Método para obtener la Especie por id, para el converter
     * @param key Long identificación de la Especie local
     * @return Object especie local solicitada
     */
    private Object getProductoEspecieLocal(Long key) {
        return prodEspFacade.find(key);
    }

    /**
     * Método para obtener la Clase por id, para el converter
     * @param key Long identificación de la clase del producto
     * @return Object clase del producto solicitada
     */
    private Object getProductoClase(Long key) {
        return prodClsFacade.find(key);
    }

    /**
     * Método para obtener la Unidad de medida por id, para el converter
     * @param key Long identificación de la unidad de medida del producto
     * @return Object unidad de medida del producto solicitada
     */
    private Object getProductoUnidadMedida(Long key) {
        return prodUnidadFacade.find(key);
    }

    /**
     * Método para poblar el listado de Génerps según la Familia seleccionada del servicio REST de Taxonomía.
     * Utilizada en cargarEntidadesSrv(Long idTax), getGenerosSrv(Long idFamilia) y familiaChangeListener()
     */       
    private void getGenerosSrv(Long idFamilia) {
        EntidadServicio genero;
        List<Genero> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTax();
            }else try {
                if(!token.isVigente()){
                    getTokenTax();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            
            // instancio el cliente para la selección de las provincias
            familiaClient = new FamiliaClient();
            // obtengo el listado
            GenericType<List<Genero>> gType = new GenericType<List<Genero>>() {};
            Response response = familiaClient.findByFamilia_JSON(Response.class, String.valueOf(idFamilia), token.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listGeneros = new ArrayList<>();
            for(Genero dpt : listSrv){
                genero = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listGeneros.add(genero);
            }
            
            familiaClient.close();            
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Géneros de la Familia seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Géneros por Familia "
                    + "del servicio REST de Taxonomía", ex.getMessage()});
        }    
    }

    /**
     * Método para poblar el listado de Especies según el Género seleccionado del servicio REST de Taxonomía.
     * Utilizado en generoChangeListener(), cargarEntidadesSrv(Long idTax) y getEspeciesSrv(Long idGenero)
     */    
    private void getEspeciesSrv(Long idGenero) {
        EntidadServicio especie;
        List<Especie> listSrv;
        
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTax();
            }else try {
                if(!token.isVigente()){
                    getTokenTax();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            
            // instancio el cliente para la selección de las provincias
            generoClient = new GeneroClient();
            // obtngo el listado
            GenericType<List<Especie>> gType = new GenericType<List<Especie>>() {};
            Response response = generoClient.findByGenero_JSON(Response.class, String.valueOf(idGenero), token.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listEspecies = new ArrayList<>();
            for(Especie esp : listSrv){
                especie = new EntidadServicio(esp.getId(), esp.getNombrecientifico());
                listEspecies.add(especie);
            }
            
            generoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo las Especies del Género seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo las Especies por Género "
                    + "del servicio REST de Taxonomía", ex.getMessage()});
        }
    }

    /**
     * Método para cargar entidades de servicio de Taxonomía y los listados, 
     * para actualizar la Especie local. Utilizado en prepareEditSrv()
     */    
    private void cargarEntidadesSrv(Long idTax) {
         Especie espLocal;
         
        try{
            // obtengo el token si no está seteado o está vencido
            if(token == null){
                getTokenTax();
            }else try {
                if(!token.isVigente()){
                    getTokenTax();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token", ex.getMessage()});
            }
            
            // instancio el cliente para la obtención de la Especie
            especieClient = new EspecieClient();
            espLocal = especieClient.find_JSON(Especie.class, String.valueOf(idTax), token.getStrToken());
            // cierro el cliente
            especieClient.close();
            // instancio las Entidades servicio
            especieSelected = new EntidadServicio(espLocal.getId(), espLocal.getNombre());
            generoSelected = new EntidadServicio(espLocal.getGenero().getId(), espLocal.getGenero().getNombre());
            familiaSelected = new EntidadServicio(espLocal.getGenero().getFamilia().getId(), espLocal.getGenero().getFamilia().getNombre());
            // cargo el listado de provincias
            cargarFamilias();
            // cargo el listado de Departamentos
            getGenerosSrv(familiaSelected.getId());
            // cargo el listado de Localidades
            getEspeciesSrv(generoSelected.getId());
            
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de Taxonomía vinculados a la Especie local. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Especie por id desde el "
                    + "servicio REST de Taxonomía", ex.getMessage()});
        }
    }
    
    /**
     * Método privado que obtiene y setea el token para autentificarse ante la API rest de Taxonomías
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en cargarEntidadesSrv(Long idTax), getEspeciesSrv(Long idGenero), getGenerosSrv(Long idFamilia) y cargarFamilias()
     */
    private void getTokenTax(){
        try{
            usuarioClient = new UsuarioClient();
            Response responseUs = usuarioClient.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTax"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strToken = (String)lstHeaders.get(0); 
            token = new Token(strToken, System.currentTimeMillis());
            usuarioClient.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }    

    //////////////////////////////
    // Converter para Producto  //
    //////////////////////////////
    @FacesConverter(forClass = Producto.class)
    public static class ProductoConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbProducto controller = (MbProducto) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbProducto");
            return controller.getProducto(getKey(value));
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
            if (object instanceof Producto) {
                Producto o = (Producto) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Producto.class.getName());
            }
        }
    }       
    
    /////////////////////////////////////////
    // Converter para ProductoEspecieLocal //
    /////////////////////////////////////////    
    @FacesConverter(forClass = ProductoEspecieLocal.class)
    public static class ProductoEspecieLocalConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbProducto controller = (MbProducto) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbProducto");
            return controller.getProductoEspecieLocal(getKey(value));
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
            if (object instanceof ProductoEspecieLocal) {
                ProductoEspecieLocal o = (ProductoEspecieLocal) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ProductoEspecieLocal.class.getName());
            }
        }
    }   
    
    ///////////////////////////////////
    // Converter para ProductoClase  //
    ///////////////////////////////////
    @FacesConverter(forClass = ProductoClase.class)
    public static class ProductoClaseConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbProducto controller = (MbProducto) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbProducto");
            return controller.getProductoClase(getKey(value));
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
            if (object instanceof ProductoClase) {
                ProductoClase o = (ProductoClase) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ProductoClase.class.getName());
            }
        }
    }    
    
    /////////////////////////////////////////
    // Converter para ProductoUnidadMedida //
    /////////////////////////////////////////
    @FacesConverter(forClass = ProductoUnidadMedida.class)
    public static class ProductoUnidadMedidaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbProducto controller = (MbProducto) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbProducto");
            return controller.getProductoUnidadMedida(getKey(value));
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
            if (object instanceof ProductoUnidadMedida) {
                ProductoUnidadMedida o = (ProductoUnidadMedida) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ProductoUnidadMedida.class.getName());
            }
        }
    }     
}
