
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Delegacion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad Delegación
 * @author rincostante
 */
@Stateless
public class DelegacionFacade extends AbstractFacade<Delegacion> {

    /**
     * Constructor
     */
    public DelegacionFacade() {
        super(Delegacion.class);
    }
    
    /**
     * Método para validar la existencia de una Delegacion en función de su nombre y localidad
     * @param nombre String Nombre de la Delegación cuya existencia se quiere validar
     * @param localidad String Nombre de la localidad
     * @return Delegacion delegación forestal existente
     */
    public Delegacion getExistente(String nombre, String localidad) {
        List<Delegacion> lstDelegaciones;
        String queryString = "SELECT del FROM Delegacion del "
                + "WHERE del.nombre = :nombre "
                + "AND del.localidad = :localidad";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("localidad", localidad);
        lstDelegaciones = q.getResultList();
        if(lstDelegaciones.isEmpty()){
            return null;
        }else{
            return lstDelegaciones.get(0);
        }
    }       
    
    /**
     * Método sobreescrito que lista las Delegaciones ordenadas por nombre
     * para poblar el listado de todas las registradas
     * @return 
     */
    @Override
    public List<Delegacion> findAll(){
        String queryString = "SELECT del FROM Delegacion del "
                + "ORDER BY del.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método que solo devuelve las Delegaciones habilitadas.
     * Para poblar los combos de selección
     * @return 
     */
    public List<Delegacion> getHabilitadas(){
        String queryString = "SELECT del FROM Delegacion del "
                + "WHERE del.habilitada = true "
                + "ORDER BY del.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
}
