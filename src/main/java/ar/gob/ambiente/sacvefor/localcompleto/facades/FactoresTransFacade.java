
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.FactoresTransformacion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author rincostante
 */
@Stateless
public class FactoresTransFacade  extends AbstractFacade<FactoresTransformacion> {
    
    /**
     * Constructor
     */
    public FactoresTransFacade() {
        super(FactoresTransformacion.class);
    }
    
    /**
     * Método que obtiene el factor de transformación total y el de residuos según los atributos recibidos
     * @param grupo int grupo especie del producto
     * @param nivel int nivel de desempeño del establecimiento
     * @param grado grado de elaboración de la clase del producto
     * @return FactoresTransformacion objeto que contiene los factores
     */
    public FactoresTransformacion getFatoresByGrupoNivelGrado(int grupo, int nivel, int grado) {
        List<FactoresTransformacion> lstFact;
        String queryString = "SELECT factores FROM FactoresTransformacion factores "
                + "WHERE factores.grupoEspecie = :grupo "
                + "AND factores.nivelDesempenio = :nivel "
                + "AND factores.gradoElaboracion = :grado";
        Query q = em.createQuery(queryString)
                .setParameter("grupo", grupo)
                .setParameter("nivel", nivel)
                .setParameter("grado", grado);
        lstFact = q.getResultList(); 
        if(lstFact.isEmpty()){
            return null;
        }else{
            return lstFact.get(0);
        }
    }
    
    /**
     * Método que obtiene el conjunto de factores de transformación total y de residuos para todos los grados según
     * el grupo de la especie y el nivel de desempeño del Establecimiento
     * @param grupo int grupo especie del producto
     * @param nivel int nivel de desempeño del establecimiento
     * @return List<FactoresTransformacion> listado de FactoresTransformacion para los parámetros recibidos
     */
    public List<FactoresTransformacion> getFactoresByGrupoNivel(int grupo, int nivel) {
        String queryString = "SELECT factores FROM FactoresTransformacion factores "
                + "WHERE factores.grupoEspecie = :grupo "
                + "AND factores.nivelDesempenio = :nivel ";
        Query q = em.createQuery(queryString)
                .setParameter("grupo", grupo)
                .setParameter("nivel", nivel);
        return q.getResultList(); 
    }
}
