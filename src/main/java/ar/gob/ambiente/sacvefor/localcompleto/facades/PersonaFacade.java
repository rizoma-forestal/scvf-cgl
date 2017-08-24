
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos para la entidad Persona
 * @author rincostante
 */
@Stateless
public class PersonaFacade extends AbstractFacade<Persona> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonaFacade() {
        super(Persona.class);
    }
    
    /**
     * Metodo para validar una Persona existente según su cuit para un rol determinado
     * @param cuit : Cuit a validar
     * @param rol : Rol a validar junto con el CUIT
     * @return 
     */
    public Persona getExistente(Long cuit, Parametrica rol) {
        List<Persona> lstPersonas;
        em = getEntityManager();
        
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.cuit = :cuit "
                + "AND per.rolPersona = :rol";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("rol", rol);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    }    
    
    /**
     * Método para obtener una Persona a partir del id del Registro Unico de Entidades del SACVeFor.
     * No debería haber dos Personas con el mismo idRue.
     * @param idRue : id de la Persona en el RUE
     * @return 
     */
    public Persona getByIdRue(Long idRue){
        List<Persona> lstPersonas;
        em = getEntityManager();
        
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.idRue = :idRue";
        Query q = em.createQuery(queryString)
                .setParameter("idRue", idRue);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las Personas ordenadas por nombre completo
     * @return 
     */
    @Override
    public List<Persona> findAll(){
        em = getEntityManager();
        String queryString = "SELECT per FROM Persona per "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    public List<Persona> findAllByRol(Parametrica rol){
        em = getEntityManager();
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.rolPersona = :rol "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("rol", rol);
        return q.getResultList();
    }
    
    /**
     * Método para obtener un listado de Personas cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param : Cadena que deberá contener el nombr completo o razón social de la Persona
     * @return 
     */
    public List<Persona> findByNombreCompeto(String param){
        em = getEntityManager();
        String queryString = "SELECT per FROM Persona per "
                + "WHERE LOWER(per.nombreCompleto) LIKE :param "
                + "AND per.habilitado = true "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }    
    
    /**
     * Método para buscar una Persona habilitada según su CUIT y su Rol
     * @param cuit : Cuit a buscar
     * @param rol : Rol de la persona a buscar
     * @return 
     */
    public Persona findByCuitRol(Long cuit, Parametrica rol){
        List<Persona> lstPersonas;
        em = getEntityManager();
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.cuit = :cuit "
                + "AND per.rolPersona = :rol "
                + "AND per.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("rol", rol);
        lstPersonas = q.getResultList();
        if(lstPersonas.isEmpty()){
            return null;
        }else{
            return lstPersonas.get(0);
        }
    }    
    
    /**
     * Método para obtener las Personas según el rol recibido
     * @param rol : Rol que deberán tener las Personas seleccionadas
     * @return 
     */
    public List<Persona> findByRol(Parametrica rol){
        em = getEntityManager();
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.rolPersona = :rol "
                + "AND per.habilitado = true "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("rol", rol);
        return q.getResultList();
    }       
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param cuit : Cuit de la Persona de la que se quieren ver sus revisiones
     * @param rol : Rol de la Persona
     * @return 
     */
    public List<Persona> findRevisions(Long cuit, Parametrica rol){  
        List<Persona> lstPers = new ArrayList<>();
        Persona per = this.getExistente(cuit, rol);
        if(per != null){
            Long id = per.getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Persona.class, id);
            for (Number n : revisions) {
                Persona cli = reader.find(Persona.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getRolPersona());
                lstPers.add(cli);
            }
        }
        return lstPers;
    }           
}
