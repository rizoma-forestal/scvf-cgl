
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.util.AutSupConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProdConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.ProdAutorizado;
import ar.gob.ambiente.sacvefor.localcompleto.util.ProdReporte;
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
    private List<ProdAutorizado> lstProdAut;
    
    /**
     * Variable privada: listado cupos y saldos de productos autorizados
     */
    private List<ProdReporte> lstProdReporte;
    
    /**
     * Variable privada: listado para filtrar los cupos y saldos de productos autorizados
     */
    private List<ProdReporte> lstProdCupoSaldoFilter;
    
    /**
     * Variable privada: listado para mostrar los productos removidos por departamento
     */
    private List<ProdConsulta> lstProdMovidos;
    
    /**
     * Variable privada: listado para filtrar los productos removidos por departamento
     */
    private List<ProdAutorizado> lstProdFilter;    
    
    /**
     * Variable privada: Listado de entidades de servicio con el id y nombre para los Departamentos
     */ 
    private List<EntidadServicio> lstDepartamentos;    
    
    /**
     * Variable privada: departamento seleccionado del combo para consultar
     */
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: flag que indica si el reporte de superficies autorizadas discrimina rodal o no
     */
    private boolean conRodal;
    
    /**
     * Variable privada: tipo de intervención seleccionada
     */
    private EntidadServicio intervSelected;
    
    /**
     * Variable privada: listado de tipos de intervenciones autorizadas
     */
    private List<EntidadServicio> lstTipoInterv;
    
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
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ItemProductivo
     */
    @EJB
    private ItemProductivoFacade itemFacade;

    public boolean isConRodal() {
        return conRodal;
    }

    public void setConRodal(boolean conRodal) {
        this.conRodal = conRodal;
    }

    public List<ProdReporte> getLstProdCupoSaldoFilter() {
        return lstProdCupoSaldoFilter;
    }

    public void setLstProdCupoSaldoFilter(List<ProdReporte> lstProdCupoSaldoFilter) {
        this.lstProdCupoSaldoFilter = lstProdCupoSaldoFilter;
    }

    public List<EntidadServicio> getLstTipoInterv() {
        return lstTipoInterv;
    }

    public void setLstTipoInterv(List<EntidadServicio> lstTipoInterv) {
        this.lstTipoInterv = lstTipoInterv;
    }

    public EntidadServicio getIntervSelected() {
        return intervSelected;
    }

    public void setIntervSelected(EntidadServicio intervSelected) {
        this.intervSelected = intervSelected;
    }

    public List<ProdReporte> getLstProdReporte() {
        return lstProdReporte;
    }

    public void setLstProdReporte(List<ProdReporte> lstProdReporte) {
        this.lstProdReporte = lstProdReporte;
    }

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

    public List<ProdAutorizado> getLstProdAut() {
        return lstProdAut;
    }

    public void setLstProdAut(List<ProdAutorizado> lstProdAut) {
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

    public List<ProdAutorizado> getLstProdFilter() {
        return lstProdFilter;
    }

    public void setLstProdFilter(List<ProdAutorizado> lstProdFilter) {
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
        lstBusqueda.add(new EntidadServicio(Long.valueOf(3), "por Autorizaciones"));
        lstBusqueda.add(new EntidadServicio(Long.valueOf(4), "total de madera removida"));
        
        // pueblo el combo de departamentos y el combo de tipos de intervención
        try{
            // obtengo los departamentos de las guías emitidas
            List<Object> deptos = autFacade.findDeptoByOrigen();
            // pueblo el listado de EntidadServicio para el combo
            int i = 0;
            lstDepartamentos = new ArrayList<>();
            for(Object depto : deptos){
                i += 1;
                EntidadServicio dptSrv = new EntidadServicio(Long.valueOf(i), depto.toString());
                lstDepartamentos.add(dptSrv);
            }
            
            // obtengo los tipos de intervención autorizadas
            List<Parametrica> tipoInts = autFacade.findTiposIntervAut();
            // pueblo el listado de EntidadServicio para el combo
            lstTipoInterv = new ArrayList<>();
            for (Parametrica ti : tipoInts){
                EntidadServicio tipo = new EntidadServicio(ti.getId(), ti.getNombre());
                lstTipoInterv.add(tipo);
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
        lstProdReporte = new ArrayList<>();
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
     * Método para listar los productos removidos según la Autorización
     * según las fechas que definen el período durante el cual se emitieron las guías
     * agrupados por según el número de la autorización, la especie local, la clase, unidad y el total
     */
    public void listarPorAut(){
        List<Object[]> result;
        lstProdReporte = new ArrayList<>();
        sinResultados = false;
        //pueblo el listado
        result = itemFacade.getExtraidosSegunAut(inicioPeriodo, finPeriodo);
        
        if(result.isEmpty()){
            // si no hay resultados seteo el flag
            sinResultados = true;
        }else{
            for (Object[] data : result){
                int i = 0;
                ProdReporte cupoSaldo = new ProdReporte();
                for (Object f : data){
                    if(f != null){
                        switch (i){
                            case 0:
                                cupoSaldo.setNumero(f.toString());
                                break;
                            case 1:
                                cupoSaldo.setNombreVulgar(f.toString());
                                break;
                            case 2:
                                cupoSaldo.setClase(f.toString());
                                break;
                            case 3:
                                cupoSaldo.setUnidad(f.toString());
                                break;
                            default:
                                cupoSaldo.setSaldo(Float.parseFloat(f.toString()));
                                break;
                        }   
                    }
                    i += 1;
                }
                lstProdReporte.add(cupoSaldo);
            }
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
     * Método para obtener un listado con todos los productos autorizados según las Autorizaciones registradas
     * durante el período de tiempo definido por el usuario, sea por departamento o para toda la provincia.
     * Obtiene un listado de objetos y lo recorre instanciando cada ProdAutorizado y agregandolo al listado
     * Finalmente, recorre el listado de productos y consulta los totales autorizados según corresponda.
     */
    public void listarProdAutorizados(){
        List<Object[]> result;
        lstProdAut = new ArrayList<>();
        sinResultados = false;
        // sigo según haya o no un departamento seleccionado
        if(deptoSelected != null){
            result = autFacade.getProdAutDepto(inicioPeriodo, finPeriodo, intervSelected.getId(), deptoSelected.getNombre());
        }else{
            result = autFacade.getProdAut(inicioPeriodo, finPeriodo, intervSelected.getId());
        }
        if(result.isEmpty()){
            sinResultados = true;
        }else{
            for (Object[] data : result){
                int i = 0;
                ProdAutorizado prodAut = new ProdAutorizado();
                for (Object f : data){
                    if(f != null){
                        switch (i){
                            case 0:
                                prodAut.setNombreCompleto(f.toString());
                                break;
                            case 1:
                                prodAut.setCuit(Long.valueOf(f.toString()));
                                break;
                            case 2:
                                prodAut.setNumAut(f.toString());
                                break;
                            case 3:
                                prodAut.setNombreInm(f.toString());
                                break;
                            case 4:
                                prodAut.setIdCastastral(f.toString());
                                break;
                            case 5:
                                prodAut.setDepto(f.toString());
                                break;
                            case 6:
                                prodAut.setClase(f.toString());
                                break;
                            case 7:
                                prodAut.setEspecie(f.toString());
                                break;
                            case 8:
                                prodAut.setCupo(Float.parseFloat(f.toString()));
                                break;
                            case 9:
                                prodAut.setSaldo(Float.parseFloat(f.toString()));
                                break;
                            case 10:
                                prodAut.setUnidad(f.toString());
                                break;
                            default:
                                prodAut.setCuenca(f.toString());
                        }
                    }
                    i += 1;
                }
                lstProdAut.add(prodAut);
            }
        }
    }
    
    /**
     * Método para listar cupos, descuentos por extracción y saldos de productos,
     * según las fechas para la autorización, el tipo de intervención y el departamento
     * agrupados por según la clase, unidad, cuenca, departamento y autorización
     */
    public void listarCuposYSaldos(){
        List<Object[]> result;
        lstProdReporte = new ArrayList<>();
        sinResultados = false;
        // sigo según haya o no un departamento seleccionado
        if(deptoSelected != null){
            result = autFacade.getCuposSaldosByDepto(inicioPeriodo, finPeriodo, intervSelected.getId(), deptoSelected.getNombre());
        }else{
            result = autFacade.getCuposSaldos(inicioPeriodo, finPeriodo, intervSelected.getId());
        }
        if(result.isEmpty()){
            sinResultados = true;
        }else{
            for (Object[] data : result){
                int i = 0;
                ProdReporte cupoSaldo = new ProdReporte();
                for (Object f : data){
                    if(f != null){
                        switch (i){
                            case 0:
                                cupoSaldo.setNumero(f.toString());
                                break;
                            case 1:
                                cupoSaldo.setClase(f.toString());
                                break;
                            case 2:
                                cupoSaldo.setCupo(Float.parseFloat(f.toString()));
                                break;
                            case 3:
                                cupoSaldo.setSaldo(Float.parseFloat(f.toString()));
                                break;
                            case 4:
                                cupoSaldo.setUnidad(f.toString());
                                break;
                            case 5:
                                cupoSaldo.setCuenca(f.toString());
                                break;
                            default:
                                cupoSaldo.setDepartamento(f.toString());
                                break;
                        }   
                    }
                    i += 1;
                }
                lstProdReporte.add(cupoSaldo);
            }
        }
    }
    
    /**
     * Método para obtener un listado de superficies (total, solicitada y autorizada) según el tipo de intervención y el departamento
     */
    public void listarSupAut(){
        List<Object[]> result;
        lstAutSup = new ArrayList<>();
        sinResultados = false;        
        // según haya seleccionado o no un departamento o la opción para discriminar rodal, hago la consulta correspondiente.
        if(deptoSelected == null && !conRodal){
            result = autFacade.getSupGralSinRodal(inicioPeriodo, finPeriodo);
        }else if(deptoSelected != null && !conRodal){
            result = autFacade.getSupGralDeptoSinRodal(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
        }else if(deptoSelected == null && conRodal){
            result = autFacade.getSupGralConRodal(inicioPeriodo, finPeriodo);
        }else{
            result = autFacade.getSupGralDeptoConRodal(inicioPeriodo, finPeriodo, deptoSelected.getNombre());
        }
        // si no hubo resultados seteo el flag
        if(result.isEmpty()){
            sinResultados = true;
        }else{
            // recorro el listado
            for (Object[] data : result){
                int i = 0;
                AutSupConsulta supCons = new AutSupConsulta();
                for (Object f : data){
                    if(f != null){
                        switch (i){
                            case 0:
                                supCons.setNombreTitular(f.toString());
                                break;
                            case 1:
                                supCons.setCuitTitular(Long.valueOf(f.toString()));
                                break;
                            case 2:
                                supCons.setSupTotal(Float.parseFloat(f.toString()));
                                break;
                            case 3:
                                supCons.setSupSol(Float.parseFloat(f.toString()));
                                break;
                            case 4:
                                supCons.setSupAut(Float.parseFloat(f.toString()));
                                break;
                            case 5:
                                supCons.setDepto(f.toString());
                                break;
                            default:
                                supCons.setNumOrden(f.toString());
                                break;
                        }   
                    }
                    i += 1;
                }
                lstAutSup.add(supCons);
            }
        }
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
        if(lstProdReporte != null){
            lstAutSup.clear();
        }
    }
}
