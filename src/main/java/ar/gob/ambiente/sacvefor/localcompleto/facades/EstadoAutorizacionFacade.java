
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

    public EstadoAutorizacionFacade() {
        super(EstadoAutorizacion.class);
    }
    
    /**
     * Método para validar la existencia de un EstadoAutorizacion en función de su nombre
     * @param nombre : Nombre del Estado cuya existencia se pretende validar
     * @return 
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
     * @param codigo : Código del Estado cuya existencia se pretende validar
     * @return 
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
     * @return 
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
     * @param codigo : Código del EstadoAutorizacion que no debe estar incluido en la selección
     * @return 
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
