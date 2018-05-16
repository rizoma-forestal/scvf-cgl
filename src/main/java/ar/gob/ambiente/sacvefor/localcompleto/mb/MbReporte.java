
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.AutSupConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProdConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Bean de respaldo para la gestión de listados parametrizados para exportar en planilla electrónica
 * Gestiona las vistas /rep/movProductos.xhtml y /rep/autProductos.xhtml
 * @author rincostante
 */
public class MbReporte {
    
    /**
     * Variable privada: listado de superficies vinculadas a las autorizaciones según el tipo de intervención
     */
    private List<AutSupConsulta> lstAutSup;
    
    /**
     * Variable privada: listado para filtrar las superficies
     */
    private List<AutSupConsulta> lstAutSupFilter;
    
    /**
     * Variable privada: listado de productos autorizados
     */
    private List<ProdConsulta> lstProdAut;
    
    /**
     * Variable privada: listado para mostrar los productos removidos por departamento
     */
    private List<ProdConsulta> lstProdMovidos;
    
    /**
     * Variable privada: listado para filtrar los productos removidos por departamento
     */
    private List<ProdConsulta> lstProdFilter;    
    
    //////////////////////////////////////////////
    // Variables para la consulta a la API Terr //
    //////////////////////////////////////////////
    
    /**
     * Variable privada: Cliente para la API Rest de Departamentos en Organización territorial
     */
    private ProvinciaClient provClient;    
    
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
    
    /**
     * Variable privada: Listado de entidades de servicio con el id y nombre para los Departamentos
     */ 
    private List<EntidadServicio> lstDepartamentos;    
    
    /**
     * Variable privada: departamento seleccionado del combo para consultar
     */
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: boolean indica si la consulta realizada no brindó resultados
     */
    private boolean sinResultados;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MbReporte.class);        
    
    //////////////////////////////////////////
    // Accesos a datos, recursos inyectados //
    //////////////////////////////////////////
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */      
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */      
    @EJB
    private AutorizacionFacade autFacade;
    
    /**
     * Variable privada: 
     */
    private Date inicioPeriodo;
    
    /**
     * Variable privada: 
     */    
    private Date finPeriodo;

    public boolean isSinResultados() {
        return sinResultados;
    }

    public void setSinResultados(boolean sinResultados) {
        this.sinResultados = sinResultados;
    }

    public List<AutSupConsulta> getLstAutSup() {
        return lstAutSup;
    }

    public void setLstAutSup(List<AutSupConsulta> lstAutSup) {
        this.lstAutSup = lstAutSup;
    }

    public List<AutSupConsulta> getLstAutSupFilter() {
        return lstAutSupFilter;
    }

    public void setLstAutSupFilter(List<AutSupConsulta> lstAutSupFilter) {
        this.lstAutSupFilter = lstAutSupFilter;
    }

    public List<ProdConsulta> getLstProdAut() {
        return lstProdAut;
    }

    public void setLstProdAut(List<ProdConsulta> lstProdAut) {
        this.lstProdAut = lstProdAut;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<ProdConsulta> getLstProdMovidos() {
        return lstProdMovidos;
    }

    public void setLstProdMovidos(List<ProdConsulta> lstProdMovidos) {
        this.lstProdMovidos = lstProdMovidos;
    }

    public List<ProdConsulta> getLstProdFilter() {
        return lstProdFilter;
    }

    public void setLstProdFilter(List<ProdConsulta> lstProdFilter) {
        this.lstProdFilter = lstProdFilter;
    }

    public List<EntidadServicio> getLstDepartamentos() {
        return lstDepartamentos;
    }

    public void setLstDepartamentos(List<EntidadServicio> lstDepartamentos) {
        this.lstDepartamentos = lstDepartamentos;
    }

    public Date getInicioPeriodo() {
        return inicioPeriodo;
    }

    public void setInicioPeriodo(Date inicioPeriodo) {
        this.inicioPeriodo = inicioPeriodo;
    }

    public Date getFinPeriodo() {
        return finPeriodo;
    }

    public void setFinPeriodo(Date finPeriodo) {
        this.finPeriodo = finPeriodo;
    }
    
    /**
     * Constructor
     */
    public MbReporte() {
    }
    
    ////////////////////////
    // Métodos operativos //
    ////////////////////////
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa el listado de departamentos.
     * Consultando a la API de TERR
     */  
    @PostConstruct
    public void init(){
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
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(String.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt"))), tokenTerr.getStrToken());
            List<Departamento> lstDeptos = response.readEntity(gType);
            // lleno el listado de los combos
            lstDepartamentos = new ArrayList<>();
            for(Departamento dpt : lstDeptos){
                EntidadServicio depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                lstDepartamentos.add(depto);
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Departamentos de la Provincia para su selección.");
            // lo escribo en el log del server
            LOG.fatal("Hubo un error cargando los Departamentos desde el servicio REST del TERR. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para obtener un listado con todos los productos movidos según las guías emitidas
     * durante el período de tiempo definido por el usuario, ya sea por departamento o para toda la provincia.
     * Obtiene las guías emitidas y de sus ítems los distintos productos.
     * Finalmente obtiene los totales para cada producto
     */
    public void listarProdMovidos(){
        // listado para los códigos de productos
        List<Long> lstCodProd = new ArrayList<>();
        // intancio el listado 
        lstProdMovidos = new ArrayList<>();
        // completo el listado de guías según el inicio y fin del príodo definido y si es para toda la provincia o para un departamento
        List<Guia> lstGuias;
        if(deptoSelected != null){
            lstGuias = guiaFacade.getByFechaEmisionDepto(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
        }else{
            lstGuias = guiaFacade.getByFechaEmision(inicioPeriodo, finPeriodo);
        }
        
        // recorro las guías y sus items para guardar los productos incluidos en ellas
        for(Guia g : lstGuias){
            for (ItemProductivo i : g.getItems()){
                if(!lstCodProd.contains(i.getIdProd())){
                    // solo guardo el id del producto una vez
                    lstCodProd.add(i.getIdProd());
                }
            }    
        }
        // recorro el listado de códigos de productos y por cada uno consulto y agrego al listado de productos movidos
        for(Long idProd : lstCodProd){
            if(deptoSelected != null){
                // si hay departamento seleccionado consulto por departamento
                ProdConsulta prod = guiaFacade.getProdDeptoQuery(inicioPeriodo, finPeriodo, deptoSelected.getNombre(), idProd);
                lstProdMovidos.add(prod);
            }else{
                // si no, consulto para toda la provincia
                ProdConsulta prod = guiaFacade.getProdQuery(inicioPeriodo, finPeriodo, idProd);
                lstProdMovidos.add(prod);
            }
        }
        
        // seteo el flag si no hubo resultados
        if(lstProdMovidos.isEmpty()) sinResultados = true;
    }
    
    /**
     * Método para obtener un listado con todos los productos autoririzados según las Autorizaciones registradas
     * durante el período de tiempo definido por el usuario, sea por departamento o para toda la provincia.
     * Obtiene todas las Autorizaciones de ellas sus items, por cada uno va obeteniendo y guardando los id de los productos en un listado.
     * Finalmente, recorre el listado de productos y consulta los totales autorizados según corresponda.
     */
    public void listarProdAutorizados(){
        // listado para los códigos de productos
        List<Long> lstCodProd = new ArrayList<>();
        // intancio el listado 
        lstProdAut = new ArrayList<>();
        // completo el listado de autorizaciones según el inicio y fin del príodo definido de su habilitación 
        // y si es para toda la provincia o para un departamento
        List<Autorizacion> lstAut;
        if(deptoSelected != null){
            lstAut = autFacade.getByFechaAltaDepto(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
        }else{
            lstAut = autFacade.getByFechaAlta(inicioPeriodo, finPeriodo);
        }
        // recorro las guías y sus items para guardar los productos incluidos en ellas
        for(Autorizacion a : lstAut){
            for (ItemProductivo i : a.getItems()){
                if(!lstCodProd.contains(i.getIdProd())){
                    // solo guardo el id del producto una vez
                    lstCodProd.add(i.getIdProd());
                }
            }    
        }
        // recorro el listado de códigos de productos y por cada uno consulto y agrego al listado de productos movidos
        for(Long idProd : lstCodProd){
            if(deptoSelected != null){
                // si hay departamento seleccionado consulto por departamento
                ProdConsulta prod = autFacade.getProdDeptoQuery(inicioPeriodo, finPeriodo, deptoSelected.getNombre(), idProd);
                lstProdAut.add(prod);
            }else{
                // si no, consulto para toda la provincia
                ProdConsulta prod = autFacade.getProdQuery(inicioPeriodo, finPeriodo, idProd);
                lstProdAut.add(prod);
            }
        }
        
        // seteo el flag si no hubo resultados
        if(lstProdAut.isEmpty()) sinResultados = true;
    }
    
    /**
     * Método para obtener un listado de superficies (total, solicitada y autorizada) según el tipo de intervención y el departamento
     */
    public void listarSupAut(){
        // según haya seleccionado o no un departamento, hago la consulta correspondiente.
        if(deptoSelected != null){
            lstAutSup = autFacade.getSupGralDepto(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
        }else{
            lstAutSup = autFacade.getSupGral(inicioPeriodo, finPeriodo);
        }
        
        // seteo el flag si no hubo resultados
        if(lstAutSup.isEmpty()) sinResultados = true;
    }
    
    /**
     * Método para limpiar los datos de inicio y fin del período a consultar
     */
    public void limpiarPeriodo(){
        deptoSelected = null;
        inicioPeriodo = null;
        finPeriodo = null;
        sinResultados = false;
        if(lstProdAut != null){
            lstProdAut.clear();
        }
        if(lstProdMovidos != null){
            lstProdMovidos.clear();
        }
        if(lstAutSup != null){
            lstAutSup.clear();
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
