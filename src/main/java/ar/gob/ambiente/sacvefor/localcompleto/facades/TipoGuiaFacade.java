
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad TipoGuia
 * @author rincostante
 */
@Stateless
public class TipoGuiaFacade extends AbstractFacade<TipoGuia> {

    /**
     * Constructor
     */
    public TipoGuiaFacade() {
        super(TipoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un TipoGuia en función de su nombre
     * @param nombre String Nombre del TipoGuia a validar
     * @return TipoGuia tipo de guía existente
     */
    public TipoGuia getExistente(String nombre) {
        List<TipoGuia> lstTipos;
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
     * @return List<TipoGuia> listado de los tipos de guía ordenados
     */
    @Override
    public List<TipoGuia> findAll(){
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Método que devuelve todos los tipos de Guía habilitados
     * @return List<TipoGuia> listado de los tipos de guía habilitados
     */
    public List<TipoGuia> getHabilitados(){
        String queryString = "SELECT tipo FROM TipoGuia tipo "
                + "WHERE tipo.habilitado = true "
                + "ORDER BY tipo.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }  
}
