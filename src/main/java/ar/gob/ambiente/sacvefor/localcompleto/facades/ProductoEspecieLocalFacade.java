
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para las Especies locales de Productos
 * @author rincostante
 */
@Stateless
public class ProductoEspecieLocalFacade extends AbstractFacade<ProductoEspecieLocal> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoEspecieLocalFacade() {
        super(ProductoEspecieLocal.class);
    }
    
    /**
     * Método para validar la existencia de una Especie según su nombre vulgar
     * @param nombreVulgar : Nombre vulgar a validar
     * @return 
     */
    public ProductoEspecieLocal getExistente(String nombreVulgar) {
        List<ProductoEspecieLocal> lstEspecies;
        em = getEntityManager();
        
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "WHERE especie.nombreVulgar = :nombreVulgar";
        Query q = em.createQuery(queryString)
                .setParameter("nombreVulgar", nombreVulgar);
        lstEspecies = q.getResultList();
        if(lstEspecies.isEmpty()){
            return null;
        }else{
            return lstEspecies.get(0);
        }
    }   
    
    /**
     * Método sobreescrito que lista las ProductoEspecieLocal ordenadas por nombre
     * @return 
     */
    @Override
    public List<ProductoEspecieLocal> findAll(){
        em = getEntityManager();
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "ORDER BY especie.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve las ProductoEspecieLocal habilitadas.
     * Para poblar combos de selección.
     * @return 
     */
    public List<ProductoEspecieLocal> getHabilitadas(){
        em = getEntityManager();
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "WHERE especie.habilitado = true "
                + "ORDER BY especie.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}