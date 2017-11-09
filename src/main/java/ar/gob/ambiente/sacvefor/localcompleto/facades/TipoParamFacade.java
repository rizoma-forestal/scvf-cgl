
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad TipoParam
 * @author rincostante
 */
@Stateless
public class TipoParamFacade extends AbstractFacade<TipoParam> {

    public TipoParamFacade() {
        super(TipoParam.class);
    }
    
    /**
     * Método para validar la existencia de un TipoParam según su nombre
     * @param nombre : Nombre del TipoParam a validar
     * @return 
     */
    public TipoParam getExistente(String nombre) {
        List<TipoParam> lstParam;
        String queryString = "SELECT tipoParam FROM TipoParam tipoParam "
                + "WHERE tipoParam.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstParam = q.getResultList();
        if(lstParam.isEmpty()){
            return null;
        }else{
            return lstParam.get(0);
        }
    }    
    
    /**
     * Método sobreescrito que lista los TipoParam ordenados por nombre
     * @return 
     */
    @Override
    public List<TipoParam> findAll(){
        String queryString = "SELECT tipoParam FROM TipoParam tipoParam "
                + "ORDER BY tipoParam.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }  
    
    /**
     * Mátodo que solo devuelve los Tipos de Parametricas habilitados.
     * @return 
     */
    public List<TipoParam> getHabilitados(){
        String queryString = "SELECT tipoParam FROM TipoParam tipoParam "
                + "WHERE tipoParam.habilitado = true "
                + "ORDER BY tipoParam.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
}
