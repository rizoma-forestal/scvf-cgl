
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.SubZona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad SubZona
 * @author rincostante
 */
@Stateless
public class SubZonaFacade extends AbstractFacade<SubZona> {

    /**
     * Constructor
     */
    public SubZonaFacade() {
        super(SubZona.class);
    }
    
    /**
     * Método para validar la existencia de una SubZona en función de su nombre y Zona
     * @param nombre String Nombre cuya existencia se quiere validar
     * @param zona ZonaIntervencion ZonaIntervencion correspondiente a la SubZona a validar
     * @return SubZona sub zona existente
     */
    public SubZona getExistente(String nombre, ZonaIntervencion zona) {
        List<SubZona> lstSubZonas;
        String queryString = "SELECT sub FROM SubZona sub "
                + "WHERE sub.nombre = :nombre "
                + "AND sub.zona = :zona";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("zona", zona);
        lstSubZonas = q.getResultList();
        if(lstSubZonas.isEmpty()){
            return null;
        }else{
            return lstSubZonas.get(0);
        }
    }    
    
    /**
     * Método sobreescrito que lista las SubZonas ordenadas por nombre
     * @return List<SubZona> listado de las sub zonas ordenadas
     */
    @Override
    public List<SubZona> findAll(){
        String queryString = "SELECT sub FROM SubZona sub "
                + "ORDER BY sub.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Mátodo que solo devuelve las SubZonas habilitadas, según el tipo.
     * Para poblar combos de selección.
     * @param zona ZonaIntervencion Zona a la que pertenece la SubZona
     * @return List<SubZona> listado de las sub zonas correspondientes a la zona remitida
     */
    public List<SubZona> getHabilitadasByZona(ZonaIntervencion zona){
        String queryString = "SELECT sub FROM SubZona sub "
                + "WHERE sub.habilitado = true "
                + "AND sub.zona = :zona "
                + "ORDER BY sub.nombre";
        Query q = em.createQuery(queryString)
                .setParameter("zona", zona);
        return q.getResultList();
    }      
}
