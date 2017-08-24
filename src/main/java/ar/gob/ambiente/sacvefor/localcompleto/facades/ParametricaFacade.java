
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad Parametrica
 * @author rincostante
 */
@Stateless
public class ParametricaFacade extends AbstractFacade<Parametrica> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametricaFacade() {
        super(Parametrica.class);
    }
    
    /**
     * Método para validar la existencia de un parámetro
     * @param nombre : Nombre cuya existencia se quiere validar
     * @param tipo : TipoParam correspondiente a la Paramétrica a validar
     * @return 
     */
    public Parametrica getExistente(String nombre, TipoParam tipo) {
        List<Parametrica> lstParam;
        em = getEntityManager();
        
        String queryString = "SELECT param FROM Parametrica param "
                + "WHERE param.nombre = :nombre "
                + "AND param.tipo = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("tipo", tipo);
        lstParam = q.getResultList();
        if(lstParam.isEmpty()){
            return null;
        }else{
            return lstParam.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las Paramétricas ordenadas por nombre
     * @return 
     */
    @Override
    public List<Parametrica> findAll(){
        em = getEntityManager();
        String queryString = "SELECT param FROM Parametrica param "
                + "ORDER BY param.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }    
    
    /**
     * Mátodo que solo devuelve las Parametricas habilitadas, según el tipo.
     * Para poblar combos de selección.
     * @param tipo : Tipo de Paramétrica buscado
     * @return 
     */
    public List<Parametrica> getHabilitadas(TipoParam tipo){
        em = getEntityManager();
        String queryString = "SELECT param FROM Parametrica param "
                + "WHERE param.habilitado = true "
                + "AND param.tipo = :tipo "
                + "ORDER BY param.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);
        return q.getResultList();
    }    
}
