
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad TipoGuia
 * @author rincostante
 */
@Stateless
public class TipoGuiaFacade extends AbstractFacade<TipoGuia> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoGuiaFacade() {
        super(TipoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un TipoGuia en función de su nombre
     * @param nombre : Nombre del TipoGuia a validar
     * @return 
     */
    public TipoGuia getExistente(String nombre) {
        List<TipoGuia> lstTipos;
        em = getEntityManager();
        
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "WHERE tipo.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstTipos = q.getResultList();
        if(lstTipos.isEmpty()){
            return null;
        }else{
            return lstTipos.get(0);
        }
    }   
    
    /**
     * Método sobreescrito que lista los EstadoGuia ordenadas por nombre
     * @return 
     */
    @Override
    public List<TipoGuia> findAll(){
        em = getEntityManager();
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Método que devuelve todos los tipos de Guía habilitados
     * @return 
     */
    public List<TipoGuia> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "WHERE tipo.habilitado = true "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }  
}
