
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para las Clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoClaseFacade extends AbstractFacade<ProductoClase> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoClaseFacade() {
        super(ProductoClase.class);
    }
    
    /**
     * Método para validar la existencia de una Especie según su nombre y unidad de medida
     * @param nombre : Nombre a validar junto con la unidad de medida
     * @param unidad : Unidad de medida a validar junto con el nombre
     * @return 
     */
    public ProductoClase getExistente(String nombre, ProductoUnidadMedida unidad) {
        List<ProductoClase> lstClases;
        em = getEntityManager();
        
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.nombre = :nombre "
                + "AND clase.unidad = :unidad";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("unidad", unidad);
        lstClases = q.getResultList();
        if(lstClases.isEmpty()){
            return null;
        }else{
            return lstClases.get(0);
        }
    }       
    
    /**
     * Método sobreescrito que lista las ProductoClase ordenadas por nombre
     * @return 
     */
    @Override
    public List<ProductoClase> findAll(){
        em = getEntityManager();
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Mátodo que solo devuelve las ProductoClase habilitadas.
     * Para poblar combos de selección.
     * @return 
     */
    public List<ProductoClase> getHabilitadas(){
        em = getEntityManager();
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.habilitado = true "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }        
}
