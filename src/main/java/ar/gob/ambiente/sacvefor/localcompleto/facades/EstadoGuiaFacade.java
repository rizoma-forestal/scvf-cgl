
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad EstadoGuia
 * @author rincostante
 */
@Stateless
public class EstadoGuiaFacade extends AbstractFacade<EstadoGuia> {

    /**
     * constructor
     */
    public EstadoGuiaFacade() {
        super(EstadoGuia.class);
    }
    
    /**
     * Método para validar la existencia de un EstadoGuia en función de su nombre
     * @param nombre String nombre del estado de guía buscado
     * @return EstadoGuia estado de guía con el nombre indicado
     */
    public EstadoGuia getExistente(String nombre) {
        List<EstadoGuia> lstEstados;
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
     * @return List<EstadoGuia> listado de los estados de guía ordenados por su nombre
     */
    @Override
    public List<EstadoGuia> findAll(){
        String queryString = "SELECT est FROM EstadoGuia est "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve las EstadoGuia habilitados, menos el acutal.
     * Para poblar combos de selección de cambios de Estado.
     * @param nombre String nombre del estado que no se incluirá en el listado obtenido
     * @return List<EstadoGuia> listado de los estados de guía excluyendo aquel cuyo nombre se indica
     */
    public List<EstadoGuia> getHabilitadosSinUno(String nombre){
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
     * @return List<EstadoGuia> listado de todos los estados de guía habilitados
     */
    public List<EstadoGuia> getHabilitados(){
        String queryString = "SELECT est FROM EstadoGuia est "
                + "WHERE est.habilitado = true "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }        
}
