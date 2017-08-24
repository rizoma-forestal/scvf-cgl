
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.facades.CopiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Bean de respaldo para la gestión de Paramétricas de Guías
 * TipoGuia: Se refiere a los diferentes Tipos de Guías que podrán configurarse:
 * Acopio, Acopio y Transporte, Transporte, etc.
 * EstadoGuia: Se refiere a los diferentes Estados que podrá tomar una Guía:
 Carga inicial, Cerrada, En tránsito, Intervenida, Extraviada, etc.
 CopiaGuia: Se refiere a los tipos de copia que deben imprimirse por cada tipo de Guía
 * @author Administrador
 */
public class MbParamGuia {

    private TipoGuia tipoGuia;
    private EstadoGuia estadoGuia;
    private CopiaGuia copia;
    private List<TipoGuia> lstTipos;
    private List<EstadoGuia> lstEstados;
    private List<CopiaGuia> lstCopias;
    private List<TipoGuia> lstTiposFilters;
    private List<EstadoGuia> lstEstadosFilters;
    private List<CopiaGuia> lstCopiasFilters;
    private List<CopiaGuia> lstCopiasXTipo;
    private List<TipoGuia> lstTiposHab;
    private boolean view;
    private boolean edit;
    
    @EJB
    private TipoGuiaFacade tipoFacade;
    @EJB
    private EstadoGuiaFacade estadoFacade;
    @EJB
    private CopiaFacade copiaFacade;
    
    public MbParamGuia() {
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
    
    /***********************
     * Mátodos operativos **
     ***********************/
    @PostConstruct
    public void init(){
        tipoGuia = new TipoGuia();
        estadoGuia = new EstadoGuia();
        copia = new CopiaGuia();
        lstTiposHab = tipoFacade.getHabilitados();
    }      
    
    /**
     * Método para guardar el Tipo de Guía, sea inserción o edición.
     * Previa validación
     */
    public void saveTipo(){  
        boolean valida = true;
        try{
            TipoGuia tipoExitente = tipoFacade.getExistente(tipoGuia.getNombre().toUpperCase());
            if(tipoExitente != null){
                if(tipoGuia.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!tipoExitente.equals(tipoGuia)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
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
                JsfUtil.addErrorMessage("El Tipo de Guía que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
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
            CopiaGuia copiaExitente = copiaFacade.getExistente(copia.getNombre().toUpperCase(), copia.getDestino().toUpperCase());
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

    
    /*********************
     * Métodos privados **
     *********************/     
    private Object getTipoGuia(Long key) {
        return tipoFacade.find(key);
    }

    private Object getEstadoGuia(Long key) {
        return estadoFacade.find(key);
    }
    
    private Object getCopia(Long key) {
        return copiaFacade.find(key);
    }    
   
    
    /************************
     ** Converter TipoGuia **
     ************************/    
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
    
    /**************************
     ** Converter EstadoGuia **
     **************************/    
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
    
    /*********************
     ** Converter Copia **
     *********************/ 
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
