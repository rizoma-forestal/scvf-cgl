
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Bean de respaldo para la gestión del listado y vista detalle de las guías registradas.
 * Gestiona carga perezosa, filtros y paginado.
 * @author rincostante
 */
public class MbGuiaList implements Serializable{
    
    /**
     * Variable privada: objeto principal a gestionar
     */
    private Guia guia;
    
    /**
     * Variable privada: listado de las guías que tomaron productos de la que se está gestionando
     */
    private List<Guia> lstGuiasHijas;
    
    /**
     * Variable privada: flag que indica que la guía que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: Listado de inmuebles vinculados a la Autorización del predio
     * desde el cual provienen los productos vinculados a la Guía,
     * cualquier sea su tipo. A mostrar en el detalle de la Guía.
     */
    private List<Inmueble> lstInmueblesOrigen;

    /**
     * Variable privada: Listado de guías registradas para su carga perezosa
     */
    private LazyDataModel<Guia> model;
    
    /**
     * Variable privada: listado para filtrar la tabla de guías existentes
     */
    private List<Guia> listadoFilter;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */       
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */   
    @EJB
    private AutorizacionFacade autFacade;    

    public List<Guia> getListadoFilter() {
        return listadoFilter;
    }

    public void setListadoFilter(List<Guia> listadoFilter) {
        this.listadoFilter = listadoFilter;
    }

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    public List<Guia> getLstGuiasHijas() {
        return lstGuiasHijas;
    }

    public void setLstGuiasHijas(List<Guia> lstGuiasHijas) {
        this.lstGuiasHijas = lstGuiasHijas;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public List<Inmueble> getLstInmueblesOrigen() {
        return lstInmueblesOrigen;
    }

    public void setLstInmueblesOrigen(List<Inmueble> lstInmueblesOrigen) {
        this.lstInmueblesOrigen = lstInmueblesOrigen;
    }

    public LazyDataModel<Guia> getModel() {
        return model;
    }

    public void setModel(LazyDataModel<Guia> model) {
        this.model = model;
    }    
    
    public MbGuiaList() {
    }
    
    /**
     * Método que se ejecuta luego de instanciada la clase. Puebla el listado de guías según reciba o no filtros para el paginado
     */
    @PostConstruct
    public void init(){
        model = new LazyDataModel<Guia>() {
            private static final long serialVersionUID = 1L;
            
            @Override
            public List<Guia> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Guia> guias;
                int count;
                if(filters != null && filters.size() > 0){
                    // si viene filtrado llamo a las query de filtrado
                    guias = guiaFacade.findAllPagFilterJpa(first, pageSize, filters);
                    count = guiaFacade.findAllPagFilterJpaCount(filters);
                }else{
                    // si no viene filtrado llamo a las query comunes
                    guias = guiaFacade.findAllPaginated(first, pageSize);
                    count = guiaFacade.count();
                }
                model.setRowCount(count);
                return guias;
            }
        };
    }
    
    /**
     * Método que prepara la vista detalla de la guía seleccionada
     */
    public void prepareViewDetalle(){
        if(guia.getTipo().isHabilitaDesc()){
            lstGuiasHijas = guiaFacade.findHijas(guia);
        }
        // Busco los inmuebles si la guía no se emite de forma remota
        if(!ResourceBundle.getBundle("/Config").getString("emiteRemota").equals("si")){
            setearInmueblesOrigen();
        }
        view = true;
    } 
    
    /**
     * Método para inicializar el listado Autorizaciones
     */
    public void prepareList(){
        guia = null;
        view = false;
    }    
    
    /**
     * Método para listar los rodales asignados a una guía.
     * En el caso de una Guía que descuente de una Autorización, no hace nada.
     * En el caso que descuente de otra guía, setea los rodales de forma temporal 
     * en la guía que se está viendo, dado que el paso de los rodales de una a la otra es automático
     */
    public void prepareViewRodalesAsignados(){
        if(!guia.getTipo().isDescuentaAutoriz()){
            guia.getRodales().clear();
            guia.getRodales().addAll(guia.getGuiasfuentes().get(0).getRodales());
        }
    }    
    
    /**
     * Método para listar los inmuebles vinculados a la Autorización de la cual provienen los productos de la Guía.
     * Si descuenta de autorización busco la misma a partir de su número de fuente, 
     * si descuenta de guías, busco la autorización desde en número de fuente de cualquiera de las guías de las que
     * se seleccionaron para el descuento de productos
     * Utilizado en prepareView() y prepareViewDetalle()
     */
    private void setearInmueblesOrigen() {
        lstInmueblesOrigen = new ArrayList<>();
        // obtengo la Autorización según el tipo de Guía
        Autorizacion aut;
        if(guia.getTipo().isDescuentaAutoriz()){
            // si la guía descuenta productos directamente de una Autorización, la obtengo
            aut = autFacade.getExistente(guia.getNumFuente());
        }else{
            // si la guía descuenta productos de otras guías, primero las busco y después busco la Autorización
            // fuente de cualquiera de ellas
            aut = autFacade.getExistente(guia.getGuiasfuentes().get(0).getNumFuente());
        }
        for(Inmueble inm : aut.getInmuebles()){
            lstInmueblesOrigen.add(inm);
        }
    }
}
