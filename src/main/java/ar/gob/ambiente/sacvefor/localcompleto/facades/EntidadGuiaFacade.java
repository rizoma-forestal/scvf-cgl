
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
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
 * Acceso a datos para la enitdad EntidadGuia
 * @author rincostante
 */
@Stateless
public class EntidadGuiaFacade extends AbstractFacade<EntidadGuia> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadGuiaFacade() {
        super(EntidadGuia.class);
    }
    
    /**
     * Metodo para validar una EntidadGuia origen existente según su cuit y el inmueble
     * @param cuit : Cuit a validar
     * @param inmNombre : Nombre del Inmueble del cual extrae los Productos el Productor
     * @return 
     */
    public EntidadGuia getOrigenExistente(Long cuit, String inmNombre) {
        List<EntidadGuia> lstEntidadGuia;
        em = getEntityManager();
        
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.inmNombre = :inmNombre";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("inmNombre", inmNombre);
        lstEntidadGuia = q.getResultList();
        if(lstEntidadGuia.isEmpty()){
            return null;
        }else{
            return lstEntidadGuia.get(0);
        }
    }    
    
    /**
     * Metodo para validar una EntidadGuia destino existente según su cuit y tipo
     * @param cuit : Cuit a validar
     * @param tipoEntidadGuia : Tipo entidad correspondiente al tipo de EntidadGuia, en este caso: Destino
     * @return 
     */
    public EntidadGuia getDestinoExistente(Long cuit, Parametrica tipoEntidadGuia) {    
        List<EntidadGuia> lstEntidadGuia;
        em = getEntityManager();
        
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.tipoEntidadGuia = :tipoEntidadGuia";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("tipoEntidadGuia", tipoEntidadGuia);
        lstEntidadGuia = q.getResultList();
        if(lstEntidadGuia.isEmpty()){
            return null;
        }else{
            return lstEntidadGuia.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las EntidadGuia ordenadas por nombre completo
     * @return 
     */
    @Override
    public List<EntidadGuia> findAll(){
        em = getEntityManager();
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "ORDER BY entidad.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Método para obtener un listado de EntidadGuia cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param : Cadena que deberá contener el nombr completo o razón social de la Persona
     * @return 
     */
    public List<EntidadGuia> findByNombreCompeto(String param){
        em = getEntityManager();
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE LOWER(entidad.nombreCompleto) LIKE :param "
                + "AND entidad.habilitado = true "
                + "ORDER BY entidad.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("param", "%" + param + "%".toLowerCase());
        return q.getResultList();
    }    
    
    /**
     * Método para buscar una EntidadGuia habilitada según su CUIT
     * @param cuit : Cuit a buscar
     * @return 
     */
    public EntidadGuia findByCuit(Long cuit){
        List<EntidadGuia> lstEntidadGuia;
        em = getEntityManager();
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        lstEntidadGuia = q.getResultList();
        if(lstEntidadGuia.isEmpty()){
            return null;
        }else{
            return lstEntidadGuia.get(0);
        }
    }   
    
    /**
     * Método para obtener las EntidadGuia según el tipo recibido
     * @param tipoEntidadGuia : Tipo de Entidad que deberán tener las EntidadGuia seleccionadas (Origen, Destino)
     * @return 
     */
    public List<EntidadGuia> findByTipo (Parametrica tipoEntidadGuia){
        em = getEntityManager();
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.tipoEntidadGuia = :tipoEntidadGuia "
                + "AND entidad.habilitado = true "
                + "ORDER BY entidad.nombreCompleto";
        Query q = em.createQuery(queryString)
                .setParameter("tipoEntidadGuia", tipoEntidadGuia);
        return q.getResultList();
    }         
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param cuit : Cuit de la Persona de la que se quieren ver sus revisiones
     * @param inmNombre : Nombre del inmueble
     * @return 
     */
    public List<EntidadGuia> findRevisions(Long cuit, String inmNombre){  
        List<EntidadGuia> lstEntidades = new ArrayList<>();
        EntidadGuia entGuia = this.getOrigenExistente(cuit, inmNombre);
        if(entGuia != null){
            Long id = this.getOrigenExistente(cuit, inmNombre).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(EntidadGuia.class, id);
            for (Number n : revisions) {
                EntidadGuia cli = reader.find(EntidadGuia.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTipoEntidadGuia());
                lstEntidades.add(cli);
            }
        }
        return lstEntidades;
    } 
}
