
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad EstadoAutorizacion
 * @author rincostante
 */
@Stateless
public class InmuebleFacade extends AbstractFacade<Inmueble> {

    /**
     * Constructor
     */
    public InmuebleFacade() {
        super(Inmueble.class);
    }
    
    /**
     * Método para validar la existencia de un Inmmueble en función de su identificación catastral
     * @param idCatastral String Identificación catastral del inmueble que se quiere validar
     * @return Inmueble inmueble correspondiente
     */
    public Inmueble getExistenteByCatastro(String idCatastral) {
        List<Inmueble> lstInmuebles;
        String queryString = "SELECT inm FROM Inmueble inm "
                + "WHERE inm.idCatastral = :idCatastral";
        Query q = em.createQuery(queryString)
                .setParameter("idCatastral", idCatastral);
        lstInmuebles = q.getResultList();
        if(lstInmuebles.isEmpty()){
            return null;
        }else{
            return lstInmuebles.get(0);
        }
    }  

    /**
     * Método sobreescrito que lista los Inmuebles ordenadas por nombre
     * @return List<Inmueble> listado de todos los inmuebles ordenados por nombre
     */
    @Override
    public List<Inmueble> findAll(){
        String queryString = "SELECT inm FROM Inmueble inm "
                + "ORDER BY inm.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }   

    /**
     * Método para seleccionar Inmuebles según contengan en su nombre
     * el parámetro recibido. Solo serán habilitados
     * @param param String Cadena que deberá contener el nombre del Inmueble para ser seleccionado
     * @return List<Inmueble> listado de todos los inmuebles cuyo nombre contenga la cadena remitida
     */
    public List<Inmueble> findByNombre(String param){
        String queryString = "SELECT inm FROM Inmueble inm "
                + "WHERE LOWER(inm.nombre) LIKE :param "
                + "AND inm.habilitado = true"
                + "ORDER BY inm.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }
    
    /**
     * Método para obtener todos los inmuebles habilitados ordenados por nombre
     * @return List<Inmueble> listado de todos los inmuebles habiltiados
     */
    public List<Inmueble> getHabilitados(){
        String queryString = "SELECT inm FROM Inmueble inm "
                + "WHERE inm.habilitado = true "
                + "ORDER BY inm.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
}
