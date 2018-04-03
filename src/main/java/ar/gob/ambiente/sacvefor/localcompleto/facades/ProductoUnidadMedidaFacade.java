
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para las Unidades de medida de las clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoUnidadMedidaFacade extends AbstractFacade<ProductoUnidadMedida> {

    /**
     * Constructor
     */
    public ProductoUnidadMedidaFacade() {
        super(ProductoUnidadMedida.class);
    }
    
    /**
     * Método para validar la existencia de una Unidad según su nombre
     * @param nombre String nombre a validar
     * @return ProductoUnidadMedida unidad de medida existente
     */
    public ProductoUnidadMedida getExistenteByNombre(String nombre) {
        List<ProductoUnidadMedida> lstUnidad;
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "WHERE unidad.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstUnidad = q.getResultList();
        if(lstUnidad.isEmpty()){
            return null;
        }else{
            return lstUnidad.get(0);
        }
    }    
    
    /**
     * Método para validar la existencia de una Unidad según su aberviatura
     * @param abrev String aberviatura a validar
     * @return ProductoUnidadMedida unidad de medida existente según la abreviatura remitida
     */
    public ProductoUnidadMedida getExistenteByAbrev(String abrev) {
        List<ProductoUnidadMedida> lstUnidad;
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "WHERE unidad.abreviatura = :abrev";
        Query q = em.createQuery(queryString)
                .setParameter("abrev", abrev);
        lstUnidad = q.getResultList();
        if(lstUnidad.isEmpty()){
            return null;
        }else{
            return lstUnidad.get(0);
        }
    }  
    
    /**
     * Método sobreescrito que lista las ProductoUnidadMedida ordenadas por nombre
     * @return List<ProductoUnidadMedida> listado de las unidades de mediada ordenadas
     */
    @Override
    public List<ProductoUnidadMedida> findAll(){
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Mátodo que solo devuelve los ProductoUnidadMedida habilitadas ordenadas por nombre.
     * Para poblar combos de selección.
     * @return List<ProductoUnidadMedida> listado de las unidades de medida habilitadas
     */
    public List<ProductoUnidadMedida> getHabilitados(){
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "WHERE unidad.habilitado = true "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
}
