
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad ZonaIntervencion
 * @author rincostante
 */
@Stateless
public class ZonaIntervencionFacade extends AbstractFacade<ZonaIntervencion> {

    /**
     * Constructor
     */
    public ZonaIntervencionFacade() {
        super(ZonaIntervencion.class);
    }
    
    /**
     * Metodo para validar una Zona existente según su nombre
     * @param nombre String Nombre que se pretende validar
     * @return ZonaIntervencion zona existente
     */
    public ZonaIntervencion getExistenteByNombre(String nombre) {
        List<ZonaIntervencion> lstZonas;
        String queryString = "SELECT zona FROM ZonaIntervencion zona "
                + "WHERE zona.nombre = :nombre";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre);
        lstZonas = q.getResultList();
        if(lstZonas.isEmpty()){
            return null;
        }else{
            return lstZonas.get(0);
        }
    }     
    
    /**
     * Metodo para validar una Zona existente según su codigo
     * @param codigo String Codigo que se pretende validar
     * @return ZonaIntervencion zona existente
     */
    public ZonaIntervencion getExistenteByCodigo(String codigo) {
        List<ZonaIntervencion> lstZonas;
        String queryString = "SELECT zona FROM ZonaIntervencion zona "
                + "WHERE zona.codigo = :codigo";
        Query q = em.createQuery(queryString)
                .setParameter("codigo", codigo);
        lstZonas = q.getResultList();
        if(lstZonas.isEmpty()){
            return null;
        }else{
            return lstZonas.get(0);
        }
    }   
    
    /**
     * Método sobreescrito que lista las ZonaIntervencion ordenadas por nombre
     * @return List<ZonaIntervencion> listado de zonas ordenadas
     */
    @Override
    public List<ZonaIntervencion> findAll(){
        String queryString = "SELECT zona FROM ZonaIntervencion zona "
                + "ORDER BY zona.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Método para obtener todos los ZonaIntervencion habilitadas ordenados por nombre
     * @return List<ZonaIntervencion> listado de zonas habilitadas
     */
    public List<ZonaIntervencion> getHabilitadas(){
        String queryString = "SELECT zona FROM ZonaIntervencion zona "
                + "WHERE zona.habilitado = true "
                + "ORDER BY zona.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}
