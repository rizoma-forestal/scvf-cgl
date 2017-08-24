
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;

/**
 * Bean de respaldo para la gestión de entidades paramétricas
 * TipoParametro: Caracterización de cada una de las enitdades paramétricas registradas
 * Parametrica: Entidad paramétrica a utilizar por las entidades principales del modelo
 * @author rincostante
 */
public class MbParametrica implements Serializable{

    private TipoParam tipoParam;
    private Parametrica parametrica;
    private List<TipoParam> lstTipoParam;
    private List<Parametrica> lstParam;
    private List<TipoParam> lstTipoFilters;
    private List<Parametrica> lstParamFilters;
    private boolean view;
    private boolean edit;
    
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    public MbParametrica() {
    }
        
    /**********************
     * Métodos de acceso **
     **********************/  
    public boolean isEdit() {
        return edit;
    }
      
    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public TipoParam getTipoParam() {
        return tipoParam;
    }

    public void setTipoParam(TipoParam tipoParam) {
        this.tipoParam = tipoParam;
    }

    public Parametrica getParametrica() {
        return parametrica;
    }

    public void setParametrica(Parametrica parametrica) {
        this.parametrica = parametrica;
    }

    public List<TipoParam> getLstTipoParam() {
        lstTipoParam = tipoParamFacade.findAll();
        return lstTipoParam;
    }

    public void setLstTipoParam(List<TipoParam> lstTipoParam) {
        this.lstTipoParam = lstTipoParam;
    }

    public List<Parametrica> getLstParam() {
        lstParam = paramFacade.findAll();
        return lstParam;
    }

    public void setLstParam(List<Parametrica> lstParam) {
        this.lstParam = lstParam;
    }

    public List<TipoParam> getLstTipoFilters() {
        return lstTipoFilters;
    }

    public void setLstTipoFilters(List<TipoParam> lstTipoFilters) {
        this.lstTipoFilters = lstTipoFilters;
    }

    public List<Parametrica> getLstParamFilters() {
        return lstParamFilters;
    }

    public void setLstParamFilters(List<Parametrica> lstParamFilters) {
        this.lstParamFilters = lstParamFilters;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
    
    /***********************
     * Mátodos operativos **
     ***********************/
    @PostConstruct
    public void init(){
        tipoParam = new TipoParam();
        parametrica = new Parametrica();
        lstTipoParam = tipoParamFacade.getHabilitados();
    }      
    
    /**
     * Método para guardar la Parametrica, sea inserción o edición.
     * Previa validación
     */
    public void saveParametrica(){
        boolean valida = true;
        try{
            Parametrica paramExitente = paramFacade.getExistente(parametrica.getNombre().toUpperCase(), parametrica.getTipo());
            if(paramExitente != null){
                if(parametrica.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!paramExitente.equals(parametrica)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = parametrica.getNombre();
                parametrica.setNombre(tempNombre.toUpperCase());
                if(parametrica.getId() != null){
                    paramFacade.edit(parametrica);
                    JsfUtil.addSuccessMessage("El Parámetro fue guardado con exito");
                }else{
                    parametrica.setHabilitado(true);
                    paramFacade.create(parametrica);
                    JsfUtil.addSuccessMessage("El Parámetro fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage("El Parámetro que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error procesando el Parámetro : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar el TipoParam, sea inserción o edición.
     * Previa validación
     */
    public void saveTipoParam(){
        boolean valida = true;
        try{
            TipoParam tipoParamExitente = tipoParamFacade.getExistente(tipoParam.getNombre().toUpperCase());
            if(tipoParamExitente != null){
                if(tipoParam.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!tipoParamExitente.equals(tipoParam)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = tipoParam.getNombre();
                tipoParam.setNombre(tempNombre.toUpperCase());
                if(tipoParam.getId() != null){
                    tipoParamFacade.edit(tipoParam);
                    JsfUtil.addSuccessMessage("El Tipo de Parámetro fue guardado con exito");
                }else{
                    tipoParam.setHabilitado(true);
                    tipoParamFacade.create(tipoParam);
                    JsfUtil.addSuccessMessage("El Tipo de Parámetro fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage("Tipo de Parámetro que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormTipoParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error procesando el Tipo de Parámetro: " + ex.getMessage());
        }
    }  
    
    /**
     * Método para deshabilitar un Parámetro
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitarParametrica(){
        try{
            parametrica.setHabilitado(false);
            paramFacade.edit(parametrica);
            limpiarFormParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al deshabilitar el Parámetro: " + ex.getMessage());
        }
    }
    
    /**
     * Método para deshabilitar un Tipo de parámetro
     * No estará disponible en los listados correspondientes
     */    
    public void deshabilitarTipoParam(){
        try{
            tipoParam.setHabilitado(false);
            tipoParamFacade.edit(tipoParam);
            limpiarFormTipoParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al deshabilitar el Tipo de Parámetro: " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar un Parámetro
     * Volverá a estar disponible en los listados
     */
    public void habilitarParametrica(){
        try{
            parametrica.setHabilitado(true);
            paramFacade.edit(parametrica);
            limpiarFormParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al habilitar el Parámetro: " + ex.getMessage());
        }
    }
    
    /**
     * Método para habilitar un Tipo de parámetro
     * Volverá a estar disponible en los listados
     */    
    public void habilitarTipoParam(){
        try{
            tipoParam.setHabilitado(true);
            tipoParamFacade.edit(tipoParam);
            limpiarFormTipoParam();
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error al habilitar el Tipo de Parámetro: " + ex.getMessage());
        }
    }
    
    /**
     * Método para limpiar el objeto Parametrica del formulario correspondiente
     */
    public void limpiarFormParam(){
        parametrica = new Parametrica();
    }    

    /**
     * Método para limpiar el objeto TipoParam del formulario correspondiente
     */
    public void limpiarFormTipoParam(){
        tipoParam = new TipoParam();
    }   
    
    /**
     * Método para habilitar el formulario de creación o edición
     * de Tipos de Parámetros
     */
    public void prepareNewTipoParam(){
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar el formulario de edición
     * de Entidades paramétricas
     */
    public void prepareNewEntParam(){
        view = false;
        edit = true;   
    }
    
    /**
     * Método para habilitar el formulario de vista detalle 
     * del Tipo de Parámetro
     */
    public void prepareViewTipoParam(){
        view = true;
    }
    
    /**
     * Metodo para incializar y abrir el díalogo que muestra los parámetros asociados
     * al tipo seleccionado
     */
    public void prepareViewParametros(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 250);
        RequestContext.getCurrentInstance().openDialog("dlgViewParam", options, null);
    }
    
    /*********************
     * Métodos privados **
     *********************/
    private Object getTipoParam(Long key) {
        return tipoParamFacade.find(key);
    }

    private Object getParametrica(Long key) {
        return paramFacade.find(key);
    }
    
    
    /*************************
    ** Converter TipoParam  **
    **************************/    
    @FacesConverter(forClass = TipoParam.class)
    public static class TipoParamConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbParametrica controller = (MbParametrica) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbParametrica");
            return controller.getTipoParam(getKey(value));
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
            if (object instanceof TipoParam) {
                TipoParam o = (TipoParam) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TipoParam.class.getName());
            }
        }
    }     
    
    /***************************
    ** Converter Parametrica  **
    ****************************/ 
    @FacesConverter(forClass = Parametrica.class)
    public static class ParametricaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbParametrica controller = (MbParametrica) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbParametrica");
            return controller.getParametrica(getKey(value));
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
            if (object instanceof Parametrica) {
                Parametrica o = (Parametrica) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Parametrica.class.getName());
            }
        }
    }   
}
