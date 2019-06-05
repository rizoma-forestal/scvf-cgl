
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.AutSupConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProdConsulta;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ws.rs.ClientErrorException;

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
    
    /**
     * Variable privada: inicio del período a buscar
     */
    private Date inicioPeriodo;
    
    /**
     * Variable privada: fin del período a buscar
     */    
    private Date finPeriodo;
    
    /**
     * Variable privada: listado de opciones de búsqueda:
     * 1: por departamento de origen
     * 2: por producto
     * 3: total de madera movida
     */
    private List<EntidadServicio> lstBusqueda;
    
    /**
     * Variable privada: tipo de búsqueda seleccionado del combo
     */
    private EntidadServicio busqSelected;
    
    /**
     * Variable privada: listado de productos removidos a buscar
     */
    private List<ProdConsulta> lstProdABuscar;
    
    /**
     * Variable privada: producto seleccionado para buscar removidos
     */
    private ProdConsulta prodSelected;
    
    /**
     * Variable privada: cantidad total de productos removidos consultados
     */
    private float totalProdRemovidos;    
    
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

    public float getTotalProdRemovidos() {
        return totalProdRemovidos;
    }
    
    public void setTotalProdRemovidos(float totalProdRemovidos) {
        this.totalProdRemovidos = totalProdRemovidos;
    }

    public List<ProdConsulta> getLstProdABuscar() {
        return lstProdABuscar;
    }

    public void setLstProdABuscar(List<ProdConsulta> lstProdABuscar) {
        this.lstProdABuscar = lstProdABuscar;
    }

    public ProdConsulta getProdSelected() {
        return prodSelected;
    }

    public void setProdSelected(ProdConsulta prodSelected) {
        this.prodSelected = prodSelected;
    }

    public EntidadServicio getBusqSelected() {
        return busqSelected;
    }

    public void setBusqSelected(EntidadServicio busqSelected) {
        this.busqSelected = busqSelected;
    }

    public List<EntidadServicio> getLstBusqueda() {
        return lstBusqueda;
    }

    public void setLstBusqueda(List<EntidadServicio> lstBusqueda) {
        this.lstBusqueda = lstBusqueda;
    }    

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
     * Método que se ejecuta luego de instanciada la clase.
     * Puebla el listado con las opciones para reportar y los departamentos desde los cuales se emitieron guías
     * Consultando a la API de TERR
     */  
    @PostConstruct
    public void init(){
        lstBusqueda = new ArrayList<>();
        lstBusqueda.add(new EntidadServicio(Long.valueOf(1), "por Departamento de Origen"));
        lstBusqueda.add(new EntidadServicio(Long.valueOf(2), "por Producto removido"));
        lstBusqueda.add(new EntidadServicio(Long.valueOf(3), "total de madera removida"));
        
        // pueblo el combo de departamentos
        try{
            // obtengo los departamentos de las guías emitidas
            List<String> deptos = guiaFacade.findDeptoByOrigen();
            // pueblo el listado de EntidadServicio para el combo
            int i = 0;
            lstDepartamentos = new ArrayList<>();
            for(String depto : deptos){
                i += 1;
                EntidadServicio dptSrv = new EntidadServicio(Long.valueOf(i), depto);
                lstDepartamentos.add(dptSrv);
            }
        }catch(ClientErrorException ex){
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Departamentos de la Provincia para su selección.");
            LOG.fatal("Hubo un error cargando los Departamentos. " + ex.getMessage());
        }
    }   
    
    /**
     * Método para resetear los listadode de productos (removidos y a buscar)
     * Accionado con la selección de una opción de reporte
     */
    public void busqChangeListener(){
        lstProdMovidos = new ArrayList<>();
        lstProdABuscar = new ArrayList<>();
        totalProdRemovidos = -1;
    }
    
    /**
     * Método que resetea el listado de productos removidos e instancia los productos a buscar
     */
    public void prepareBusqueda(){
        lstProdMovidos = new ArrayList<>();
        try{
            lstProdABuscar = guiaFacade.getProdRemov(inicioPeriodo, finPeriodo);
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los productos removidos.");
            LOG.fatal("Hubo un error cargando los Productos removidos para el listado de reporte. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para listar los productos removidos por departamento.
     * Primero puebla el listado de los productos incluidos en las guías emitidas (lstProdABuscar)
     * entre las fechas de inicio y fin para un departamento específico (sin totales).
     * Luego, por cada producto leído obtiene los subtotales y puebla el listado de removidos (lstProdMovidos).
     * Finalmente limpia el listado de productos a buscar.
     */
    public void listarProdMovXDepto(){
        lstProdMovidos = new ArrayList<>();
        try{
            // obtengo los productos deptoSelected.getNombre()
            lstProdABuscar = guiaFacade.getProdRemovXDepto(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
            // recorro el listado y voy poblando los totales por producto
            for (ProdConsulta prod : lstProdABuscar){
                ProdConsulta p = guiaFacade.getProdDeptoQuery(inicioPeriodo, finPeriodo, deptoSelected.getNombre(), prod.getIdProd());
                lstProdMovidos.add(p);
            }
            lstProdABuscar.clear();   
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los productos del Departamento.");
            LOG.fatal("Hubo un error obteniendo los productos del Departamento " + deptoSelected.getNombre() + ". " + ex.getMessage());
        }
    }
    
    /**
     * Método que obtiene el total para un producto seleccionado y lo quita del listado de los que quedan para seleccionar
     */
    public void addTotProd(){
        try{
            ProdConsulta p = guiaFacade.getProdQuery(inicioPeriodo, finPeriodo, prodSelected.getIdProd());
            lstProdMovidos.add(p);
            lstProdABuscar.remove(prodSelected);
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo el total para el producto.");
            LOG.fatal("Hubo un error obteniendo el total para el producto " + prodSelected.getNombreVulgar() + " - " + prodSelected.getClase() + ". " + ex.getMessage());
        }
    }
    
    /**
     * Método para calcular el total de productos sin discriminar los departamentos
     */
    public void calcularAllProd(){
        try{
            for(ProdConsulta prod : lstProdABuscar){
                ProdConsulta p = guiaFacade.getProdQuery(inicioPeriodo, finPeriodo, prod.getIdProd());
                lstProdMovidos.add(p);
            }
            lstProdABuscar.clear();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los subtotales de los productos removidos.");
            LOG.fatal("Hubo un error obteniendo los subtotales de los productos removidos. " + ex.getMessage());
        }
    }    
    
    /**
     * Método para obtener el total de madera removida
     */
    public void totalProductosRemovidos(){
        try{
            totalProdRemovidos = guiaFacade.getTotalProdRemov(inicioPeriodo, finPeriodo);
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo la cantidad total de productos removidos.");
            LOG.fatal("Hubo un error obteniendo la cantidad total de productos removidos." + ex.getMessage());
        }
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
}
