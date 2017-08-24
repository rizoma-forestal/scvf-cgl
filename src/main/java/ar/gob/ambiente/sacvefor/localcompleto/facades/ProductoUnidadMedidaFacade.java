
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para las Unidades de medida de las clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoUnidadMedidaFacade extends AbstractFacade<ProductoUnidadMedida> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoUnidadMedidaFacade() {
        super(ProductoUnidadMedida.class);
    }
    
    /**
     * Método para validar la existencia de una Unidad según su nombre
     * @param nombre : nombre a validar
     * @return 
     */
    public ProductoUnidadMedida getExistenteByNombre(String nombre) {
        List<ProductoUnidadMedida> lstUnidad;
        em = getEntityManager();
        
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
     * @param abrev : aberviatura a validar
     * @return 
     */
    public ProductoUnidadMedida getExistenteByAbrev(String abrev) {
        List<ProductoUnidadMedida> lstUnidad;
        em = getEntityManager();
        
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
     * @return 
     */
    @Override
    public List<ProductoUnidadMedida> findAll(){
        em = getEntityManager();
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Mátodo que solo devuelve los ProductoUnidadMedida habilitados.
     * Para poblar combos de selección.
     * @return 
     */
    public List<ProductoUnidadMedida> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT unidad FROM ProductoUnidadMedida unidad "
                + "WHERE unidad.habilitado = true "
                + "ORDER BY unidad.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
}