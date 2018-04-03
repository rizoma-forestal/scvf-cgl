
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos para la entidad Autorización
 * @author rincostante
 */
@Stateless
public class AutorizacionFacade extends AbstractFacade<Autorizacion> {

    /**
     * Constructor
     */
    public AutorizacionFacade() {
        super(Autorizacion.class);
    }
    
    /**
     * Metodo para validar una Autorización existente según el numero
     * @param numero String numero de la autorización a buscar 
     * @return Autorizacion obtenida
     */
    public Autorizacion getExistente(String numero) {
        List<Autorizacion> lstAut;
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "WHERE aut.numero = :numero";
        Query q = em.createQuery(queryString)
                .setParameter("numero", numero);
        lstAut = q.getResultList();
        if(lstAut.isEmpty()){
            return null;
        }else{
            return lstAut.get(0);
        }
    }    
    
    /**
     * Método que devuelve las Autorizaciones vinculadas al Titular de una Guía
     * para ser tomada como fuente. Un estado que habilita emisión de Guía
     * @param per Persona proponente cuyas autorizaciones se buscan
     * @return List<Autorizacion> listado de Autorizaciones vinculadas al proponente
     */
    public List<Autorizacion> getFuenteByProponente(Persona per){
        String rolProponente = ResourceBundle.getBundle("/Config").getString("Proponente");
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.personas per "
                + "INNER JOIN per.rolPersona rol "
                + "WHERE per = :per "
                + "AND rol.nombre = :rolProponente "
                + "AND aut.estado.habilitaEmisionGuia = true";
        Query q = em.createQuery(queryString)
                .setParameter("per", per)
                .setParameter("rolProponente", rolProponente);
        return q.getResultList();
    } 
    
    /**
     * Método que devuevle las Autorizaciones vinculadas a una Persona mediante un Rol determinado
     * @param per Persona Persona cuyas Autorizaciones se quiere consultar
     * @param rol Rol Rol mediante el cual se vinculan las Autorizaciones a la Persona
     * @return List<Autorizacion> listado de Autorizaciones vinculadas a la persona
     */
    public List<Autorizacion> getByPersona(Persona per, Parametrica rol){
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.personas per "
                + "INNER JOIN per.rolPersona rol "
                + "WHERE per = :per "
                + "AND rol = :rol";
        Query q = em.createQuery(queryString)
                .setParameter("per", per)
                .setParameter("rol", rol);
        return q.getResultList(); 
    }
    
    /**
     * Método para obtener las Autorizaciones según el id del Producto autorizado
     * @param idProd Long identificación del Producto incluido en la Autorización
     * @return List<Autorizacion> listado de Autorizaciones que incluyen al producto entre sus ítems autorizados
     */
    public List<Autorizacion> getByIdProd(Long idProd){
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.items item "
                + "WHERE item.idProd = :idProd";
        Query q = em.createQuery(queryString)
                .setParameter("idProd", idProd);
        return q.getResultList();
    }
    
    /**
     * Método para obtener las Autorizaciones según su estado
     * @param estado EstadoAutorizacion estado en el cual deberán estar las Autorizaciones retornadas
     * @return List<Autorizacion> listado de Autorizaciones con el estado indicado
     */
    public List<Autorizacion> getByEstado(EstadoAutorizacion estado){
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "WHERE aut.estado = :estado";
        Query q = em.createQuery(queryString)
                .setParameter("estado", estado);
        return q.getResultList();
    }   
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param numero String Número del instrumento autorizante
     * @return List<Autorizacion> listado de revisiones para la autorización
     */
    public List<Autorizacion> findRevisions(String numero){  
        List<Autorizacion> lstProductos = new ArrayList<>();
        Autorizacion aut = this.getExistente(numero);
        if(aut != null){
            Long id = this.getExistente(numero).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Autorizacion.class, id);
            for (Number n : revisions) {
                Autorizacion cli = reader.find(Autorizacion.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getLstApoderados());
                Hibernate.initialize(cli.getLstProponentes());
                Hibernate.initialize(cli.getLstTecnicos());
                Hibernate.initialize(cli.getEstado());
                Hibernate.initialize(cli.getInmuebles());
                Hibernate.initialize(cli.getItems());
                lstProductos.add(cli);
            }
        }
        return lstProductos;
    }          
}
