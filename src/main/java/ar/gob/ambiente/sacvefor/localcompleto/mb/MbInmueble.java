
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.facades.InmuebleFacade;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Bean de respaldo para la gestión de Inmuebles
 * @author rincostante
 */
public class MbInmueble {

    private Inmueble inmueble;
    private List<Inmueble> listado;
    private List<Inmueble> listFilters;
    private List<Inmueble> lstInmOrigen;
    private boolean view;
    private boolean edit;
    private static final Logger logger = Logger.getLogger(Inmueble.class.getName());    
    
    // inyección de recursos
    @EJB
    private InmuebleFacade inmFacade;
    
    // Clientes REST para la selección de datos territoriales
    private ProvinciaClient provClient;    
    private DepartamentoClient deptoClient;
    private LocalidadClient localidadClient;    

    /**
     * Campos para la gestión de los elementos territoriales en los combos del formulario.
     * Las Entidades de servicio se componen de un par {id | nombre}
     */ 
    private List<EntidadServicio> listProvincias;
    private EntidadServicio provSelected;
    private List<EntidadServicio> listDepartamentos;
    private EntidadServicio deptoSelected;
    private List<EntidadServicio> listLocalidades;
    private EntidadServicio localSelected;       
    
    public MbInmueble() {
    }
        
    /**********************
     * Métodos de acceso **
     **********************/   
    public List<Inmueble> getLstInmOrigen() {
        lstInmOrigen = inmFacade.getHabilitados();
        return lstInmOrigen;
    }
     
    public void setLstInmOrigen(List<Inmueble> lstInmOrigen) {
        this.lstInmOrigen = lstInmOrigen;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public List<Inmueble> getListado() {
        listado = inmFacade.findAll();
        return listado;
    }

    public void setListado(List<Inmueble> listado) {
        this.listado = listado;
    }

    public List<Inmueble> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Inmueble> listFilters) {
        this.listFilters = listFilters;
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

    public List<EntidadServicio> getListProvincias() {
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public List<EntidadServicio> getListDepartamentos() {
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public List<EntidadServicio> getListLocalidades() {
        if(listLocalidades == null) listLocalidades = new ArrayList<>();
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    
    /***********************
     * Mátodos operativos **
     ***********************/
    @PostConstruct
    public void init(){
        inmueble = new Inmueble();
        cargarProvincias();
    }   

    /**
     * Método para actualizar el listado de departamentos según la Provincia seleccionada
     */    
    public void provinciaChangeListener(){
        localSelected = new EntidadServicio();
        getDepartamentosSrv(provSelected.getId());
    }   
    
    /**
     * Método para actualizar el listado de localidades según el Departamento seleccionado
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
    }  
    
    /**
     * Método para guardar el Inmueble, sea inserción o edición.
     * Previa validación
     */      
    public void save(){
        boolean valida = true;
        
        try{
            Inmueble inmExitente = inmFacade.getExistenteByCatastro(inmueble.getIdCatastral());
            // valido por el idCatastral
            if(inmExitente != null){
                if(inmueble.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!inmExitente.equals(inmueble)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                inmueble.setIdCatastral(inmueble.getIdCatastral().toUpperCase());
                String tempNombre = inmueble.getNombre();
                inmueble.setNombre(tempNombre.toUpperCase());
                String tempDomicilio = inmueble.getDomicilio();
                inmueble.setDomicilio(tempDomicilio.toUpperCase());
                // seteo los datos territoriales
                inmueble.setIdLocGt(localSelected.getId());
                inmueble.setLocalidad(localSelected.getNombre());
                inmueble.setDepartamento(deptoSelected.getNombre());
                inmueble.setProvincia(provSelected.getNombre());
                if(inmueble.getId() != null){
                    inmFacade.edit(inmueble);
                    JsfUtil.addSuccessMessage("El Inmueble fue guardado con exito");
                }else{
                    inmueble.setHabilitado(true);
                    inmFacade.create(inmueble);
                    JsfUtil.addSuccessMessage("El Inmueble fue registrado con exito");
                }  
            }else{
                JsfUtil.addErrorMessage("El Inmueble que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el Inmueble : " + ex.getMessage());
        }     
    }
    
    /**
     * Método que setea la provPersona según el id correspondiente que la Persona tenga seteado para ser editada
     */
    public void prepareEdit(){
        // cargo las entidades territoriales
        cargarEntidadesSrv(inmueble.getIdLocGt());
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
     * Método para habilitar la muestra nuevo del formulario
     */
    public void prepareNew(){
        view = false;
        edit = true;
    } 

    /**
     * Método para deshabilitar un Inmueble. Modificará su condición de habilitado a false.
     * Los Inmuebles deshabilitados no estarán disponibles para su selección
     */
    public void deshabilitar(){
        try{
            inmueble.setHabilitado(false);
            inmFacade.edit(inmueble);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar el Inmueble: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a las Clases a su condición normal.
     */
    public void habilitar(){
        try{
            inmueble.setHabilitado(false);
            inmFacade.edit(inmueble);
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar el Inmueble: " + ex.getMessage());
        }
    }
    
    /**
     * Limpia el formulario de inserción/edición
     */
    public void limpiarForm() {
        inmueble = new Inmueble();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
    }    
    
    
    /*********************
     * Métodos privados **
     *********************/      
    private Object getInmueble(Long key) {
        return inmFacade.find(key);
    }

    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class);
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();
            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
                //provincia = null;
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Provincias para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Provincias desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        }        
    }

    /**
     * Método para poblar el listado de Departamentos según la Provincia seleccionada del servicio REST de centros poblados
     */  
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv));
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listDepartamentos = new ArrayList<>();
            for(Departamento dpt : listSrv){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
            }
            
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Departamentos de la Provincia seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Departamentos por Provincia "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Método para poblar el listado de Localidades según el Departamento seleccionado del servicio REST de centros poblados
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // instancio el cliente para la selección de las provincias
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto));
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listLocalidades = new ArrayList<>();
            for(CentroPoblado loc : listSrv){
                local = new EntidadServicio(loc.getId(), loc.getNombre() + " - " + loc.getCentroPobladoTipo().getNombre());
                listLocalidades.add(local);
            }
            
            deptoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo las Localidades del Departamento seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo las Localidades por Departamento "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Método para cargar entidades de servicio y los listados, para actualizar el Domicilio de la Persona
     */    
    private void cargarEntidadesSrv(Long idLocGt) {
        CentroPoblado cp;
        
        try{
            // instancio el cliente para la selección de las provincias
            localidadClient = new LocalidadClient();
            cp = localidadClient.find_JSON(CentroPoblado.class, String.valueOf(idLocGt));
            // cierro el cliente
            localidadClient.close();
            // instancio las Entidades servicio
            localSelected = new EntidadServicio(cp.getId(), cp.getNombre());
            deptoSelected = new EntidadServicio(cp.getDepartamento().getId(), cp.getDepartamento().getNombre());
            provSelected = new EntidadServicio(cp.getDepartamento().getProvincia().getId(), cp.getDepartamento().getProvincia().getNombre());
            // cargo el listado de provincias
            cargarProvincias();
            // cargo el listado de Departamentos
            getDepartamentosSrv(provSelected.getId());
            // cargo el listado de Localidades
            getLocalidadesSrv(deptoSelected.getId());

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos territoriales del Inmueble. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Localidad por id desde el "
                    + "servicio REST de centros poblados", ex.getMessage()});
        }
    }
    
    
    /*****************************
    ** Converter para Inmueble  **
    ******************************/ 
    @FacesConverter(forClass = Inmueble.class)
    public static class InmuebleConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MbInmueble controller = (MbInmueble) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "mbInmueble");
            return controller.getInmueble(getKey(value));
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
            if (object instanceof Inmueble) {
                Inmueble o = (Inmueble) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Inmueble.class.getName());
            }
        }
    }       
}
