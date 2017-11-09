
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.CopiaGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la entidad CopiaGuia
 * @author rincostante
 */
@Stateless
public class CopiaFacade extends AbstractFacade<CopiaGuia> {

    public CopiaFacade() {
        super(CopiaGuia.class);
    }
    
    /**
     * Método para validar la existencia de una Copia en función de su nombre y destino
     * @param nombre : Nombre de la CopiaGuia a validar
     * @param destino : Destino de la CopiaGuia a validar
     * @param tipo : Tipo de Guía
     * @return 
     */
    public CopiaGuia getExistente(String nombre, String destino, TipoGuia tipo) {
        List<CopiaGuia> lstCopias;
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.nombre = :nombre "
                + "AND copia.tipoGuia = :tipo "
                + "AND copia.destino = :destino";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("tipo", tipo)
                .setParameter("destino", destino);
        lstCopias = q.getResultList();
        if(lstCopias.isEmpty()){
            return null;
        }else{
            return lstCopias.get(0);
        }
    }    
    
    /**
     * Método que devuelve todos las Copias habilitadas
     * @param tipo : Tipo de Guía de la cual se pide las Copias
     * @return 
     */
    public List<CopiaGuia> getHabilitadosByTipo(TipoGuia tipo){
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.habilitado = true "
                + "AND copia.tipoGuia = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);
        return q.getResultList();
    }       
    
    /**
     * Método que devuelve todos las Copias habilitadas
     * @return 
     */
    public List<CopiaGuia> getHabilitados(){
        String queryString = "SELECT copia FROM CopiaGuia copia "
                + "WHERE copia.habilitado = true";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
}
