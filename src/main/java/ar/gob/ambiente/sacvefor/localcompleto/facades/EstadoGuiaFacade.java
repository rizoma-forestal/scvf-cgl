
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad EstadoGuia
 * @author rincostante
 */
@Stateless
public class EstadoGuiaFacade extends AbstractFacade<EstadoGuia> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstadoGuiaFacade() {
        super(EstadoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un EstadoGuia en función de su nombre
     * @param nombre
     * @return 
     */
    public EstadoGuia getExistente(String nombre) {
        List<EstadoGuia> lstEstados;
        em = getEntityManager();
        
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstEstados = q.getResultList();
        if(lstEstados.isEmpty()){
            return null;
        }else{
            return lstEstados.get(0);
        }
    }  
    
    /**
     * Método sobreescrito que lista los EstadoGuia ordenadas por nombre
     * @return 
     */
    @Override
    public List<EstadoGuia> findAll(){
        em = getEntityManager();
        String queryString = "SELECT est FROM EstadoGuia est "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve las EstadoGuia habilitados, menos el acutal.
     * Para poblar combos de selección de cambios de Estado.
     * @param nombre
     * @return 
     */
    public List<EstadoGuia> getHabilitadosSinUno(String nombre){
        em = getEntityManager();
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.habilitado = true "
                + "AND est.nombre <> :nombre "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        return q.getResultList();
    }      
    
    /**
     * Método que devuelve todos los EstadoGuia habilitados
     * @return 
     */
    public List<EstadoGuia> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.habilitado = true "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }        
}
