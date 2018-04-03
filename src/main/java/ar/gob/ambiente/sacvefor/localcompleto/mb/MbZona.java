
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.SubZona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion;
import ar.gob.ambiente.sacvefor.localcompleto.facades.SubZonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ZonaIntervencionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
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
 * Bean de respaldo para la gestión de Zonas de intervención forestal
 * y sus respectivas Sub zonas
 * ZonaIntervención: Se refiere a las diferentes Zonas según el ordenamiento ambiental:
 * Roja, Amarilla, Verde, etc
 * SubZona: Se refiere a las diferentes sub zonas en las que se pueden agrupar las zonas verde y amarilla.
 * Gestiona las vista aut/zonas/subZona.xhtml y zona.xhtml
 * @author rincostante
 */
public class MbZona {

    /**
     * Variable privada: zona a gestionar
     */
    private ZonaIntervencion zona;
    
    /**
     * Variable privada: subzona a gestionar
     */
    private SubZona subZona;
    
    /**
     * Variable privada: listado de las zonas existentes
     */
    private List<ZonaIntervencion> lstZonas;
    
    /**
     * Variable privada: listado de las sub zonas existentes
     */
    private List<SubZona> lstSubZonas;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de las zonas
     */
    private List<ZonaIntervencion> lstZonaFilters;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de las sub zonas
     */
    private List<SubZona> lstSubZonaFilters;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando es existente
     */
    private boolean edit;

    ///////////////////////////////////////////////////
    // acceso a datos mediante inyección de recursos //
    ///////////////////////////////////////////////////
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de ZonaIntervencion
     */  
    @EJB
    private ZonaIntervencionFacade zonaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de SubZona
     */  
    @EJB
    private SubZonaFacade subZonaFacade;
    
    /**
     * Constructor
     */
    public MbZona() {
    }
        
    ///////////////////////
    // Métodos de acceso //
    /////////////////////// 
    public boolean isEdit() {
        return edit;
    }
      
    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public ZonaIntervencion getZona() {
        return zona;
    }

    public void setZona(ZonaIntervencion zona) {
        this.zona = zona;
    }

    public SubZona getSubZona() {
        return subZona;
    }

    public void setSubZona(SubZona subZona) {
        this.subZona = subZona;
    }

    public List<ZonaIntervencion> getLstZonas() {
        lstZonas = zonaFacade.findAll();
        return lstZonas;
    }

    public void setLstZonas(List<ZonaIntervencion> lstZonas) {
        this.lstZonas = lstZonas;
    }

    public List<SubZona> getLstSubZonas() {
        lstSubZonas = subZonaFacade.findAll();
        return lstSubZonas;
    }

    public void setLstSubZonas(List<SubZona> lstSubZonas) {
        this.lstSubZonas = lstSubZonas;
    }

    public List<ZonaIntervencion> getLstZonaFilters() {
        return lstZonaFilters;
    }

    public void setLstZonaFilters(List<ZonaIntervencion> lstZonaFilters) {
        this.lstZonaFilters = lstZonaFilters;
    }

    public List<SubZona> getLstSubZonaFilters() {
        return lstSubZonaFilters;
    }

    public void setLstSubZonaFilters(List<SubZona> lstSubZonaFilters) {
        this.lstSubZonaFilters = lstSubZonaFilters;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }
    
    
    ////////////////////////
    // Métodos operativos //
    ////////////////////////
    
    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar
     */  
    @PostConstruct
    public void init(){
        zona = new ZonaIntervencion();
        subZona = new SubZona();
        lstZonas = zonaFacade.getHabilitadas();
    }     
    
    /**
     * Método para guardar la Zona de intervención, sea inserción o edición.
     * Previa validación
     */
    public void saveZona(){    
        boolean valida = true;
        String msgValidador = "";
        try{
            ZonaIntervencion zonaExitente = zonaFacade.getExistenteByNombre(zona.getNombre().toUpperCase());
            // valido por el nombre
            if(zonaExitente != null){
                if(zona.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!zonaExitente.equals(zona)) valida = false;
                    msgValidador = "Ya existe una Zona de intervención con el nombre ingresado.";
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    msgValidador = "Ya existe una Zona de intervención con el nombre ingresado.";
                }
            }
            // si el nombre validó, valido por el código
            if(valida){
                zonaExitente = zonaFacade.getExistenteByCodigo(zona.getCodigo().toUpperCase());
                if(zonaExitente != null){
                    if(zona.getId() != null){
                        // si edita, no habilito si no es el mismo
                        if(!zonaExitente.equals(zona)) valida = false;
                        msgValidador = "Ya existe una Zona de intervención con el código ingresado.";
                    }else{
                        // si no edita no habilito de ninguna manera
                        valida = false;
                        msgValidador = "Ya existe una Zona de intervención con el código ingresado.";
                    }
                }
            }
            // si continuó validando
            if(valida){
                String tempNombre = zona.getNombre();
                zona.setNombre(tempNombre.toUpperCase());
                String tempCodigo = zona.getCodigo();
                zona.setCodigo(tempCodigo.toUpperCase());
                if(zona.getId() != null){
                    zonaFacade.edit(zona);
                    JsfUtil.addSuccessMessage("La Zona de intervención fue guardado con exito");
                }else{
                    zona.setHabilitado(true);
                    zonaFacade.create(zona);
                    JsfUtil.addSuccessMessage("La Zona de intervención fue registrado con exito");
                }   
            }else{
                JsfUtil.addErrorMessage(msgValidador + ", por favor verifique los datos ingresados.");
            }
            limpiarFormZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Zona de intervención: " + ex.getMessage());
        }
    }
    
    /**
     * Método para guardar la Sub zona de intervención, sea inserción o edición.
     * Previa validación
     */
    public void saveSubZona(){
        boolean valida = true;
        try{
            SubZona subZonaExitente = subZonaFacade.getExistente(subZona.getNombre().toUpperCase(), subZona.getZona());
            if(subZonaExitente != null){
                if(subZona.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!subZonaExitente.equals(subZona)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombre = subZona.getNombre();
                subZona.setNombre(tempNombre.toUpperCase());
                if(subZona.getId() != null){
                    subZonaFacade.edit(subZona);
                    JsfUtil.addSuccessMessage("La Sub zona de intervención fue guardada con exito");
                }else{
                    subZona.setHabilitado(true);
                    subZonaFacade.create(subZona);
                    JsfUtil.addSuccessMessage("La Sub zona de intervención fue registrada con exito");
                }   
            }else{
                JsfUtil.addErrorMessage("La Sub zona de intervención que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarFormSubZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando la Sub Zona de intervención : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para deshabilitar una Zona de intervención
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitarZona(){
        try{
            zona.setHabilitado(false);
            zonaFacade.edit(zona);
            limpiarFormZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Zona de intervención: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para deshabilitar una Sub Zona de intervención
     * No estará disponible en los listados correspondientes
     */
    public void deshabilitarSubZona(){
        try{
            subZona.setHabilitado(false);
            subZonaFacade.edit(subZona);
            limpiarFormSubZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Sub Zona de intervención: " + ex.getMessage());
        }
    }       
    
    /**
     * Método para habilitar una Zona de intervención
     * Volverá a estar disponible en los listados
     */
    public void habilitarZona(){   
        try{
            zona.setHabilitado(true);
            zonaFacade.edit(zona);
            limpiarFormZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Zona de intervención: " + ex.getMessage());
        }
    } 
    
    /**
     * Método para habilitar una Sub Zona de intervención
     * Volverá a estar disponible en los listados
     */    
    public void habilitarSubZona(){
        try{
            subZona.setHabilitado(true);
            subZonaFacade.edit(subZona);
            limpiarFormSubZona();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Sub Zona de intervención: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para limpiar el objeto Zona de intervención del formulario correspondiente
     */
    public void limpiarFormZona(){
        zona = new ZonaIntervencion();
    }    

    /**
     * Método para limpiar el objeto Sub Zona de intervención del formulario correspondiente
     */
    public void limpiarFormSubZona(){
        subZona = new SubZona();
    }       
    
    /**
     * Método para habilitar el formulario de creación o edición
     * de Zonas de intervención
     */
    public void prepareNewZona(){
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar el formulario de edición
     * de Sub zonas
     */
    public void preparaNewSubZona(){
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar el formulario de vista detalle 
     * del Tipo de Parámetro
     */
    public void prepareViewZona(){
        view = true;
    }
    
    /**
     * Metodo para incializar y abrir el díalogo que muestra las Zonas de intervención
     * a la sub zona seleccionada
     */    
    public void prepareViewZonas(){
        Map<String,Object> options = new HashMap<>();
        options.put("contentWidth", 900);
        RequestContext.getCurrentInstance().openDialog("dlgViewZonas", options, null);
    }    
    

    
    //////////////////////
    // Métodos privados //
    //////////////////////
    
    /**
     * Meétodo que obtiene una zona de intervención según si id para el converter de Zonas
     * @param key Long identificador de la zona
     * @return Object zona de intervención correspondiente al id
     */
    private Object getZonaIntervencion(Long key) {
        return zonaFacade.find(key);
    }

    /**
     * Meétodo que obtiene una sub zona de intervención según si id para el converter de sub zonas
     * @param key Long identificador de la sub zona
     * @return Object sub zona de intervención correspondiente al id
     */
    private Object getSubZona(Long key) {
        return subZonaFacade.find(key);
    }

 
    /////////////////////////////////
    // Converter ZonaIntervencion  //
    ///////////////////////////////// 
    @FacesConverter(forClass = ZonaIntervencion.class)
    public static class ZonaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbZona controller = (MbZona) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbZona");
            return controller.getZonaIntervencion(getKey(value));
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
            if (object instanceof ZonaIntervencion) {
                ZonaIntervencion o = (ZonaIntervencion) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ZonaIntervencion.class.getName());
            }
        }
    }     
    
    ////////////////////////
    // Converter SubZona  //
    ////////////////////////
    @FacesConverter(forClass = SubZona.class)
    public static class SubZonaConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbZona controller = (MbZona) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbZona");
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
            if (object instanceof SubZona) {
                SubZona o = (SubZona) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + SubZona.class.getName());
            }
        }
    }      
}
