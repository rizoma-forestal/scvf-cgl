
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad EstadoAutorizacion
 * @author rincostante
 */
@Stateless
public class EstadoAutorizacionFacade extends AbstractFacade<EstadoAutorizacion> {

    /**
     * Constructor
     */
    public EstadoAutorizacionFacade() {
        super(EstadoAutorizacion.class);
    }
    
    /**
     * Método para validar la existencia de un EstadoAutorizacion en función de su nombre
     * @param nombre String Nombre del Estado cuya existencia se pretende validar
     * @return EstadoAutorizacion estado de la autorización con el nombre recibido
     */
    public EstadoAutorizacion getExistenteByNombre(String nombre) {
        List<EstadoAutorizacion> lstEstados;
        String queryString = "SELECT est FROM EstadoAutorizacion est "
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
     * Método para validar la existencia de un EstadoAutorizacion en función de su código
     * @param codigo String Código del Estado cuya existencia se pretende validar
     * @return EstadoAutorizacion estado de la autorización con el código recibido
     */
    public EstadoAutorizacion getExistenteByCodigo(String codigo) {
        List<EstadoAutorizacion> lstEstados;
        String queryString = "SELECT est FROM EstadoAutorizacion est "
                + "WHERE est.codigo = :codigo";
        Query q = em.createQuery(queryString)
                .setParameter("codigo", codigo);
        lstEstados = q.getResultList();
        if(lstEstados.isEmpty()){
            return null;
        }else{
            return lstEstados.get(0);
        }
    }       
    
    /**
     * Método sobreescrito que lista los EstadoAutorizacion ordenadas por nombre
     * @return List<EstadoAutorizacion> listado de los estados ordenados por su nombre
     */
    @Override
    public List<EstadoAutorizacion> findAll(){
        String queryString = "SELECT est FROM EstadoAutorizacion est "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Mátodo que solo devuelve las EstadoAutorizacion habilitados, menos el acutal.
     * Para poblar combos de selección de cambios de Estado.
     * @param codigo String Código del EstadoAutorizacion que no debe estar incluido en la selección
     * @return List<EstadoAutorizacion> listado de los estados habilitados restantes al indicado como parámetro
     */
    public List<EstadoAutorizacion> getHabilitadosSinUno(String codigo){
        String queryString = "SELECT est FROM EstadoAutorizacion est "
                + "WHERE est.habilitado = true "
                + "AND est.codigo <> :codigo "
                + "ORDER BY est.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("codigo", codigo);
        return q.getResultList();
    }    
}
