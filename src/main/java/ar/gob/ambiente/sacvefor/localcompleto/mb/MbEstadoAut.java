
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoAutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Bean de respaldo para la gestión de Estados de una Autorización
 * Bloqueado
 * Carga Inicial
 * Habilitado
 * Suspendido
 * etc.
 * @author rincostante
 */
public class MbEstadoAut {

    private EstadoAutorizacion estadoAut;
    private List<EstadoAutorizacion> lstEstadosAut;
    private List<EstadoAutorizacion> lstFilters;
    private boolean view;
    private boolean edit;
    
    @EJB
    private EstadoAutorizacionFacade estadoFacade;
    
    public MbEstadoAut() {
    }

    /**********************
     * Métodos de acceso **
     **********************/      
    public EstadoAutorizacion getEstadoAut() {
        return estadoAut;
    }

    public void setEstadoAut(EstadoAutorizacion estadoAut) {
        this.estadoAut = estadoAut;
    }

    public List<EstadoAutorizacion> getLstEstadosAut() {
        lstEstadosAut = estadoFacade.findAll();
        return lstEstadosAut;
    }

    public void setLstEstadosAut(List<EstadoAutorizacion> lstEstadosAut) {
        this.lstEstadosAut = lstEstadosAut;
    }

    public List<EstadoAutorizacion> getLstFilters() {
        return lstFilters;
    }

    public void setLstFilters(List<EstadoAutorizacion> lstFilters) {
        this.lstFilters = lstFilters;
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
        limpiarForm();
    }      
    
    /**
     * Método para guardar el Estado de la Autorización, sea inserción o edición.
     * Previa validación
     */
    public void save(){    
        boolean valida = true;
        String msgValidador = "";
        try{
            EstadoAutorizacion estAutExitente = estadoFacade.getExistenteByNombre(estadoAut.getNombre().toUpperCase());
            // valido por el nombre
            if(estAutExitente != null){
                if(estadoAut.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!estAutExitente.equals(estadoAut)) valida = false;
                    msgValidador = "Ya existe un Estado de Guía con el nombre ingresado.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    msgValidador = "Ya existe un Estado de Guía con el nombre ingresado.";
                }
            }
            // si el nombre validó, valido por el código
            if(valida){
                estAutExitente = estadoFacade.getExistenteByCodigo(estadoAut.getCodigo().toUpperCase());
                if(estAutExitente != null){
                    if(estadoAut.getId() != null){
                        // si edita, no habilito si no es el mismo
                        if(!estAutExitente.equals(estadoAut)) valida = false;
                        msgValidador = "Ya existe un Estado de Guía con el código ingresado.";
                    }else{
                        // si no edita no habilito de ninguna manera
                        valida = false;
                        msgValidador = "Ya existe un Estado de Guía con el código ingresado.";
                    }
                }
            }
            // si continuó validando
            if(valida){
                String tempNombre = estadoAut.getNombre();
                estadoAut.setNombre(tempNombre.toUpperCase());
                String tempCodigo = estadoAut.getCodigo();
                estadoAut.setCodigo(tempCodigo.toUpperCase());
                if(estadoAut.getId() != null){
                    estadoFacade.edit(estadoAut);
                    JsfUtil.addSuccessMessage("El Estado de Guía fue guardado con exito");
                }else{
                    estadoAut.setHabilitado(true);
                    estadoFacade.create(estadoAut);
                    JsfUtil.addSuccessMessage("El Estado de Guía fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage(msgValidador + ", por favor verifique los datos ingresados.");
            }
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Estado de Guía : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para deshabilitar un Estado de Autorización
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitar(){
        try{
            estadoAut.setHabilitado(false);
            estadoFacade.edit(estadoAut);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Estado de Guía: " + ex.getMessage());
        } 
    }    
    
    /**
     * Método para deshabilitar un Estado de Autorización
     * No estará disponible en los listados correspondientes
     */
    public void habilitar(){
        try{
            estadoAut.setHabilitado(true);
            estadoFacade.edit(estadoAut);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Estado de Guía: " + ex.getMessage());
        } 
    }  
    
    /**
     * Método para limpiar el objeto Zona de intervención del formulario correspondiente
     */
    public void limpiarForm(){
        estadoAut = new EstadoAutorizacion();
    }      
    
    /**
     * Método para habilitar el formulario de creación o edición
     */
    public void prepareNew(){
        view = false;
        edit = true;
    }   
    
    /**
     * Método para habilitar el formulario de vista detalle 
     */
    public void prepareView(){
        view = true;
        edit = false;
    }    

    
    
    /*********************
     * Métodos privados **
     *********************/     
    private Object getSubZona(Long key) {
        return estadoFacade.find(key);
    }
    
    
    /**********************************
    ** Converter EstadoAutorizacion  **
    ***********************************/ 
    @FacesConverter(forClass = EstadoAutorizacion.class)
    public static class EstadoAutorizacionConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbEstadoAut controller = (MbEstadoAut) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbEstadoAut");
            return controller.getSubZona(getKey(value));
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
            if (object instanceof EstadoAutorizacion) {
                EstadoAutorizacion o = (EstadoAutorizacion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EstadoAutorizacion.class.getName());
            }
        }
    }         
}
