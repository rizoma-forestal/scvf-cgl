
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
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

    /**
     * Constructor
     */
    public PersonaFacade() {
        super(Persona.class);
    }
    
    /**
     * Metodo para validar una Persona existente según su cuit para un rol determinado
     * @param cuit Long Cuit a validar
     * @param rol Parametrica Rol a validar junto con el CUIT
     * @return Persona persona existente
     */
    public Persona getExistente(Long cuit, Parametrica rol) {
        List<Persona> lstPersonas;
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
     * @param idRue Long id de la Persona en el RUE
     * @return Persona persona con el idRue correspondiente
     */
    public Persona getByIdRue(Long idRue){
        List<Persona> lstPersonas;
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
     * @return List<Persona> listado de las personas ornenado por su nombre completo
     */
    @Override
    public List<Persona> findAll(){
        String queryString = "SELECT per FROM Persona per "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    } 
    
    /**
     * Método que obtiene todas las personas según su rol, ordenadas por su nombre completo
     * @param rol Parametrica paramétrica que hace las veces de rol
     * @return List<Persona> listado de todas las personas correspondientes
     */
    public List<Persona> findAllByRol(Parametrica rol){
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.rolPersona = :rol "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("rol", rol);
        return q.getResultList();
    }
    
    /**
     * Método para obtener un listado de las Personas habilitadas cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param String Cadena que deberá contener el nombre completo o razón social de la Persona
     * @return List<Persona> listado de las personas cuyo nombre completo contiene la cadena remitida
     */
    public List<Persona> findByNombreCompeto(String param){
        String queryString = "SELECT per FROM Persona per "
                + "WHERE LOWER(per.nombreCompleto) LIKE :param "
                + "AND per.habilitado = true "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }  
    
    /**
     * Método para obtener un listado de todas las Personas de con un rol determinado 
     * (habilitadas o no) cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param String Cadena que deberá contener el nombre completo o razón social de la Persona
     * @param rol Parametrica Rol de la persona a buscar
     * @return List<Persona> listado de las personas cuyo nombre completo contiene la cadena remitida
     */
    public List<Persona> findAllByNombreYRol(String param, Parametrica rol){
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.nombreCompleto LIKE :param "
                + "AND per.rolPersona = :rol "
                + "ORDER BY per.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("rol", rol)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList(); 
    }
     
    /**
     * Método para buscar una Persona habilitada según su CUIT y su Rol
     * @param cuit Long Cuit a buscar
     * @param rol Parametrica Rol de la persona a buscar
     * @return Persona persona correspondiente a los parámetros
     */
    public Persona findByCuitRol(Long cuit, Parametrica rol){
        List<Persona> lstPersonas;
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
     * Método para buscar una persona habilitada según su CUIT y su Rol,
     * validando que esté vigente. Utilizada para Técnicos y Apoderados.
     * @param cuit Long Cuit a buscar
     * @param rol Parametrica Rol de la persona a buscar
     * @return Persona persona correspondiente a los parámetros
     */
    public Persona findVigenteByCuitRol(Long cuit, Parametrica rol){
        Date hoy = new Date(System.currentTimeMillis());
        List<Persona> lstPersonas;
        String queryString = "SELECT per FROM Persona per "
                + "WHERE per.cuit = :cuit "
                + "AND per.rolPersona = :rol "
                + "AND per.habilitado = true "
                + "AND (per.finVigencia >= :hoy OR per.finVigencia IS NULL)";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("hoy", hoy)
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
     * @param rol Parametrica Rol que deberán tener las Personas seleccionadas
     * @return List<Persona> listado de todas las personas con el rol remitido
     */
    public List<Persona> findByRol(Parametrica rol){
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
     * @param cuit Long Cuit de la Persona de la que se quieren ver sus revisiones
     * @param rol Parametrica Rol de la Persona
     * @return List<Persona> listado de todas las revisiones de una Persona para su auditoría
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
