
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuiaTasa;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.CopiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Bean de respaldo para la gestión de Paramétricas de Guías.
 * Gestiona las vistas guia/param/
 * TipoGuia: Se refiere a los diferentes Tipos de Guías que podrán configurarse:
 * Acopio, Acopio y Transporte, Transporte, etc.
 * EstadoGuia: Se refiere a los diferentes Estados que podrá tomar una Guía:
 * Carga inicial, Cerrada, En tránsito, Intervenida, Extraviada, etc.
 * CopiaGuia: Se refiere a los tipos de copia que deben imprimirse por cada tipo de Guía
 * @author Administrador
 */
public class MbParamGuia {

    /**
     * Variable privada: objeto de tipo TipoGuia
     */
    private TipoGuia tipoGuia;
    
    /**
     * Variable privada: objeto de tipo EstadoGuia
     */
    private EstadoGuia estadoGuia;
    
    /**
     * Variable privada: objeto de tipo CopiaGuia
     */
    private CopiaGuia copia;
    
    /**
     * Variable privada: listado de los tipos de guía existentes
     */
    private List<TipoGuia> lstTipos;
    
    /**
     * Variable privada: listado de los tipos de los estados de guía existentes
     */
    private List<EstadoGuia> lstEstados;
    
    /**
     * Variable privada: listado de las copias de guía existentes
     */
    private List<CopiaGuia> lstCopias;
    
    /**
     * Variable privada: listado para filtrar los tipos en la tabla correspondiente
     */
    private List<TipoGuia> lstTiposFilters;
    
    /**
     * Variable privada: listado para filtrar los estados en la tabla correspondiente
     */
    private List<EstadoGuia> lstEstadosFilters;
    
    /**
     * Variable privada: listado para filtrar las copias en la tabla correspondiente
     */
    private List<CopiaGuia> lstCopiasFilters;
    
    /**
     * Variable privada: listado de las copias a imprimir por tipo de guía
     */
    private List<CopiaGuia> lstCopiasXTipo;
    
    /**
     * Variable privada: listado de los tipos de guía habilitados
     */
    private List<TipoGuia> lstTiposHab;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando es existente
     */
    private boolean edit;
    
    ////////////////////////////
    // tasas por tipo de Guía //
    ////////////////////////////
    
    /**
     * Variable privada: tasa correspondiente al tipo de  guía
     */
    private TipoGuiaTasa tipoGuiaTasa;
    
    /**
     * Variable privada: listado de tasas por tipo de guía
     */
    private List<Parametrica> lstTiposTasas;
    
    ///////////////////////////////////////////////////
    // acceso a datos mediante inyección de recursos //
    ///////////////////////////////////////////////////
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoGuia
     */ 
    @EJB
    private TipoGuiaFacade tipoFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de EstadoGuia
     */ 
    @EJB
    private EstadoGuiaFacade estadoFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Copia
     */ 
    @EJB
    private CopiaFacade copiaFacade;
    
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
     * Constructor
     */
    public MbParamGuia() {
    }

    ///////////////////////
    // métodos de acceso //
    ///////////////////////
    public TipoGuiaTasa getTipoGuiaTasa() {
        return tipoGuiaTasa;
    }

    public void setTipoGuiaTasa(TipoGuiaTasa tipoGuiaTasa) {
        this.tipoGuiaTasa = tipoGuiaTasa;
    }
    public List<Parametrica> getLstTiposTasas() {
        return lstTiposTasas;
    }

    public void setLstTiposTasas(List<Parametrica> lstTiposTasas) {
        this.lstTiposTasas = lstTiposTasas;
    }

    public List<TipoGuia> getLstTiposHab() {
        return lstTiposHab;
    }

    public void setLstTiposHab(List<TipoGuia> lstTiposHab) {
        this.lstTiposHab = lstTiposHab;
    }

    public List<CopiaGuia> getLstCopiasXTipo() {
        return lstCopiasXTipo;
    }

    public void setLstCopiasXTipo(List<CopiaGuia> lstCopiasXTipo) {
        this.lstCopiasXTipo = lstCopiasXTipo;
    }

    public CopiaGuia getCopia() {
        return copia;
    }

    public void setCopia(CopiaGuia copia) {
        this.copia = copia;
    }

    public List<CopiaGuia> getLstCopias() {
        lstCopias = copiaFacade.findAll();
        return lstCopias;
    }

    public void setLstCopias(List<CopiaGuia> lstCopias) {
        this.lstCopias = lstCopias;
    }

    public List<CopiaGuia> getLstCopiasFilters() {
        return lstCopiasFilters;
    }

    public void setLstCopiasFilters(List<CopiaGuia> lstCopiasFilters) {
        this.lstCopiasFilters = lstCopiasFilters;
    }

    public TipoGuia getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(TipoGuia tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public EstadoGuia getEstadoGuia() {
        return estadoGuia;
    }

    public void setEstadoGuia(EstadoGuia estadoGuia) {
        this.estadoGuia = estadoGuia;
    }

    public List<TipoGuia> getLstTipos() {
        lstTipos = tipoFacade.findAll();
        return lstTipos;
    }

    public void setLstTipos(List<TipoGuia> lstTipos) {
        this.lstTipos = lstTipos;
    }

    public List<EstadoGuia> getLstEstados() {
        lstEstados = estadoFacade.findAll();
        return lstEstados;
    }

    public void setLstEstados(List<EstadoGuia> lstEstados) {
        this.lstEstados = lstEstados;
    }

    public List<TipoGuia> getLstTiposFilters() {
        return lstTiposFilters;
    }

    public void setLstTiposFilters(List<TipoGuia> lstTiposFilters) {
        this.lstTiposFilters = lstTiposFilters;
    }

    public List<EstadoGuia> getLstEstadosFilters() {
        return lstEstadosFilters;
    }

    public void setLstEstadosFilters(List<EstadoGuia> lstEstadosFilters) {
        this.lstEstadosFilters = lstEstadosFilters;
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
    
    ////////////////////////
    // Mátodos operativos //
    ////////////////////////
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar
     */  
    @PostConstruct
    public void init(){
        tipoGuia = new TipoGuia();
        estadoGuia = new EstadoGuia();
        copia = new CopiaGuia();
        lstTiposHab = tipoFacade.getHabilitados();
        tipoGuiaTasa = new TipoGuiaTasa();
        TipoParam tipoParamTasa = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("TipoTasa"));
        lstTiposTasas = paramFacade.getHabilitadas(tipoParamTasa);
    }

    /**
     * Método para guardar el Tipo de Guía, sea inserción o edición.
     * Previa validación
     */
    public void saveTipo(){  
        boolean valida = true;
        String msg = "";
        try{
            TipoGuia tipoExitente = tipoFacade.getExistente(tipoGuia.getNombre().toUpperCase());
            if(tipoExitente != null){
                if(tipoGuia.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!tipoExitente.equals(tipoGuia)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    msg = "El Tipo de Guía que está tratando de persisitir ya existe, por favor verifique los datos ingresados.";
                }
            }
            if(tipoGuia.isAbonaTasa()){
                if(tipoGuia.getTasas().isEmpty()){
                    valida = false;
                    if(msg.equals("")) msg = "El tipo de Guía abona tasas pero no tiene ninguna configurada.";
                    else msg = msg + " El tipo de Guía abona tasas pero no tiene ninguna configurada";
                }
            }
            
            if(valida){
                String tempNombre = tipoGuia.getNombre();
                tipoGuia.setNombre(tempNombre.toUpperCase());
                if(tipoGuia.getId() != null){
                    tipoFacade.edit(tipoGuia);
                    JsfUtil.addSuccessMessage("El Tipo de Guía fue guardado con exito");
                }else{
                    tipoGuia.setHabilitado(true);
                    tipoFacade.create(tipoGuia);
                    JsfUtil.addSuccessMessage("El Tipo de Guía fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage(msg);
            }
            limpiarFormTipo();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Tipo de Guía: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar el Estado de la Guía, sea inserción o edición.
     * Previa validación
     */
    public void saveEstado(){
        boolean valida = true;
        try{
            EstadoGuia estadoExitente = estadoFacade.getExistente(estadoGuia.getNombre().toUpperCase());
            if(estadoExitente != null){
                if(estadoGuia.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!estadoExitente.equals(estadoGuia)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = estadoGuia.getNombre();
                estadoGuia.setNombre(tempNombre.toUpperCase());
                if(estadoGuia.getId() != null){
                    estadoFacade.edit(estadoGuia);
                    JsfUtil.addSuccessMessage("El Estado de Guía fue guardado con exito");
                }else{
                    estadoGuia.setHabilitado(true);
                    estadoFacade.create(estadoGuia);
                    JsfUtil.addSuccessMessage("El Estado de Guía fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage("El Estado de Guía que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormEstado();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Estado de Guía: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar la CopiaGuia de un Tipo de Guía, sea inserción o edición.
     * Previa validación
     */
    public void saveCopia(){
        boolean valida = true;
        try{
            CopiaGuia copiaExitente = copiaFacade.getExistente(copia.getNombre().toUpperCase(), copia.getDestino().toUpperCase(), copia.getTipoGuia());
            if(copiaExitente != null){
                if(copia.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!copiaExitente.equals(copia)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = copia.getNombre();
                copia.setNombre(tempNombre.toUpperCase());
                String tempDestino = copia.getDestino();
                copia.setDestino(tempDestino.toUpperCase());
                if(copia.getId() != null){
                    copiaFacade.edit(copia);
                    JsfUtil.addSuccessMessage("La Copia del tipo de Guía fue guardada con exito");
                }else{
                    copia.setHabilitado(true);
                    copiaFacade.create(copia);
                    JsfUtil.addSuccessMessage("La Copia del tipo de Guía fue registrada con exito");
                }   
            }else{
                JsfUtil.addErrorMessage("La Copia del tipo de Guía que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormCopia();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Copia de Tipo de Guía: " + ex.getMessage());
        }
    }
    
    //////////////////////////////////////
    // Métodos para la gestión de Tasas //
    //////////////////////////////////////
    /**
     * Método para agregar una Tasa al producto.
     * Primero valida que ya no esté agregada //tipoGuia
     */
    public void agregarTasa(){
        boolean valida = true;
        
        // valido que la Tasa no esté ya aplicada al producto
        for(TipoGuiaTasa tgs : tipoGuia.getTasas()){
            if(Objects.equals(tgs.getTipo(), tipoGuiaTasa.getTipo())){
                valida = false;
                JsfUtil.addErrorMessage("La Tasa que está tratando de asociar, ya está vinculada al Tipo de Guía.");
            }
        }
        
        try{
            if(valida){
                tipoGuia.getTasas().add(tipoGuiaTasa);
                tipoGuiaTasa = new TipoGuiaTasa();
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al agergar la Tasa al Tipo de Guía: " + ex.getMessage());
        }        
    }
    
    /**
     * Método para limpiar el formulario de selección de Tasas para el Tipo de Guía
     */
    public void limpiarFormTasa(){
        tipoGuiaTasa = new TipoGuiaTasa();
    }   
    
    /**
     * Método para desagregar una Tasa de un Tipo de Guía
     */
    public void desagregarTasa(){
        int i = 0, j = 0;
        
        for(TipoGuiaTasa tgs : tipoGuia.getTasas()){
            if(Objects.equals(tgs.getTipo(), tipoGuiaTasa.getTipo())){
                j = i;
            }
            i = i+= 1;
        }
        tipoGuia.getTasas().remove(j);
        tipoGuiaTasa = new TipoGuiaTasa();
    }    
    
    /**
     * Método para deshabilitar un Tipo de Guìa
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitaTipo(){
        try{
            tipoGuia.setHabilitado(false);
            tipoFacade.edit(tipoGuia);
            limpiarFormTipo();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Tipo de Guía: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para deshabilitar un Estado de Guía
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitarEstado(){  
        try{
            estadoGuia.setHabilitado(false);
            estadoFacade.edit(estadoGuia);
            limpiarFormEstado();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Estado de Guía: " + ex.getMessage());
        }
    }  

    /**
     * Método para deshabilitar una CopiaGuia de un tipo de Guía
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitarCopia(){
        try{
            copia.setHabilitado(false);
            copiaFacade.edit(copia);
            limpiarFormCopia();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Copia de Tipo de Guía: " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar un Tipo de Guìa
     * Volverá a estar disponible en los listados
     */
    public void habilitarTipo(){    
        try{
            tipoGuia.setHabilitado(true);
            tipoFacade.edit(tipoGuia);
            limpiarFormTipo();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Tipo de Guía: " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar un Estado de Guía
     * Volverá a estar disponible en los listados
     */    
    public void habilitarEstado(){    
        try{
            estadoGuia.setHabilitado(true);
            estadoFacade.edit(estadoGuia);
            limpiarFormEstado();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Estado de Guía: " + ex.getMessage());
        }        
    }
    
    /**
     * Método para habilitar una Cipia de tipo de Guía
     * Volverá a estar disponible en los listados
     */    
    public void habilitarCopia(){
        try{
            copia.setHabilitado(true);
            copiaFacade.edit(copia);
            limpiarFormCopia();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Copia de Tipo de Guía: " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el objeto Tipo de Guía del formulario correspondiente
     */
    public void limpiarFormTipo(){
        tipoGuia = new TipoGuia();
    }    

    /**
     * Método para limpiar el objeto Estado de Guía del formulario correspondiente
     */
    public void limpiarFormEstado(){
        estadoGuia = new EstadoGuia();
    } 
    
    /**
     * Método para limpiar el objeto CopiaGuia del Tipo de Guía del formulario correspondiente
     */
    public void limpiarFormCopia(){
        copia = new CopiaGuia();
    } 
    
    /**
     * Método para habilitar el formulario de creación o edición
     * de Tipos de Guía
     */
    public void prepareNewTipo(){
        lstCopiasXTipo = copiaFacade.getHabilitadosByTipo(tipoGuia);
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar el formulario de edición
     * de Estado de Guía
     */
    public void preparaNewEstado(){
        view = false;
        edit = true;
    }    
    
    /**
     * Método para habilitar el formulario de edición
     * de Copia de Tipo de Guía
     */
    public void preparaNewCopia(){
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

    
    //////////////////////
    // Métodos privados //
    //////////////////////

    /**
     * Método para obtener un Tipo de Guía según si id
     * Utilizado por el converter de Tipo de Guía
     * @param key Long identificador del Tipo de Guía
     * @return Object Tipo de guía solicitado
     */
    private Object getTipoGuia(Long key) {
        return tipoFacade.find(key);
    }

    /**
     * Método para obtener un Estado de Guía según si id
     * Utilizado por el converter de Estado de Guía
     * @param key Long identificador del Estado de Guía
     * @return Object Estado de guía solicitado
     */    
    private Object getEstadoGuia(Long key) {
        return estadoFacade.find(key);
    }
    
    /**
     * Método para obtener un Copia de Guía según si id
     * Utilizado por el converter de Copia de Guía
     * @param key Long identificador del Copia de Guía
     * @return Object Copia de guía solicitado
     */   
    private Object getCopia(Long key) {
        return copiaFacade.find(key);
    }    
   
    
    ////////////////////////
    // Converter TipoGuia //
    ////////////////////////   
    @FacesConverter(forClass = TipoGuia.class)
    public static class TipoGuiaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbParamGuia controller = (MbParamGuia) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbParamGuia");
            return controller.getTipoGuia(getKey(value));
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
            if (object instanceof TipoGuia) {
                TipoGuia o = (TipoGuia) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TipoGuia.class.getName());
            }
        }
    }   
    
    //////////////////////////
    // Converter EstadoGuia //
    //////////////////////////    
    @FacesConverter(forClass = EstadoGuia.class)
    public static class EstadoGuiaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbParamGuia controller = (MbParamGuia) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbParamGuia");
            return controller.getEstadoGuia(getKey(value));
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
            if (object instanceof EstadoGuia) {
                EstadoGuia o = (EstadoGuia) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EstadoGuia.class.getName());
            }
        }
    }    
    
    /////////////////////
    // Converter Copia //
    ///////////////////// 
    @FacesConverter(forClass = CopiaGuia.class)
    public static class CopiaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbParamGuia controller = (MbParamGuia) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbParamGuia");
            return controller.getCopia(getKey(value));
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
            if (object instanceof CopiaGuia) {
                CopiaGuia o = (CopiaGuia) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CopiaGuia.class.getName());
            }
        }
    }   
}
