
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
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

    /**
     * Constructor
     */
    public EntidadGuiaFacade() {
        super(EntidadGuia.class);
    }
    
    /**
     * Metodo para validar una EntidadGuia origen existente según su cuit y el inmueble
     * @param cuit Long Cuit a validar
     * @param inmNombre String Nombre del Inmueble del cual extrae los Productos el Productor
     * @return EntidadGuia entidad guía correspondiente a los parámetros indicados
     */
    public EntidadGuia getOrigenExistente(Long cuit, String inmNombre) {
        List<EntidadGuia> lstEntidadGuia;
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
     * Metodo para validar una EntidadGuia origen existente según su cuit y el número de la Autorización.
     * Creado para el servicio requerido para la integración con SICMA
     * @param cuit Long Cuit a validar
     * @param numAut String Nombre número de la Autorización que permite la extracción del predio
     * @return EntidadGuia entidad guía correspondiente a los parámetros indicados
     */
    public EntidadGuia getOrigenCuitAut(Long cuit, String numAut) {
        List<EntidadGuia> lstEntidadGuia;
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.numAutorizacion = :numAut";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit)
                .setParameter("numAut", numAut);
        lstEntidadGuia = q.getResultList();
        if(lstEntidadGuia.isEmpty()){
            return null;
        }else{
            return lstEntidadGuia.get(0);
        }
    }
    
    /**
     * Metodo para validar una EntidadGuia destino existente según su cuit, tipo y domicilio
     * @param cuit Long Cuit a validar
     * @param tipoEntidadGuia Parametrica Tipo entidad correspondiente al tipo de EntidadGuia, en este caso: Destino
     * @param dom domicilio seleccionado del destinatario.
     * @return EntidadGuia entidad guía crrespondiente a los parámetros indicados
     */
    public EntidadGuia getDestinoExistente(Long cuit, Parametrica tipoEntidadGuia, Domicilio dom) {    
        List<EntidadGuia> lstEntidadGuia;
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.tipoEntidadGuia = :tipoEntidadGuia "
                + "AND entidad.idLocGT = :domIdLoc "
                + "AND entidad.inmDomicilio = :domTexto";
        Query q = em.createQuery(queryString)
                .setParameter("domIdLoc", dom.getIdLoc())
                .setParameter("domTexto", dom.getCalle() + "-" + dom.getNumero())
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
     * Metodo para validar una EntidadGuia destino existente según su cuit, tipo y domicilio
     * para la primera etapa de la integración SICMA porque no hay "idLocGT"
     * @param cuit Long Cuit a validar
     * @param tipoEntidadGuia Parametrica Tipo entidad correspondiente al tipo de EntidadGuia, en este caso: Destino
     * @param dom domicilio seleccionado del destinatario.
     * @return EntidadGuia entidad guía crrespondiente a los parámetros indicados
     */
    public EntidadGuia getDestinoExistenteSICMA(Long cuit, Parametrica tipoEntidadGuia, Domicilio dom) {    
        List<EntidadGuia> lstEntidadGuia;
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "WHERE entidad.cuit = :cuit "
                + "AND entidad.tipoEntidadGuia = :tipoEntidadGuia "
                + "AND entidad.inmDomicilio = :domTexto";
        Query q = em.createQuery(queryString)
                .setParameter("domTexto", dom.getCalle() + "-" + dom.getNumero())
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
     * @return List<EntidadGuia> listado de las entidades guía ordenadas por su nombre completo
     */
    @Override
    public List<EntidadGuia> findAll(){
        String queryString = "SELECT entidad FROM EntidadGuia entidad "
                + "ORDER BY entidad.nombreCompleto";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }     
    
    /**
     * Método para obtener un listado de EntidadGuia cuyo nombre completo 
     * (o Razón social) contenga la cadena recibida como parámetro
     * @param param String Cadena que deberá contener el nombr completo o razón social de la Persona
     * @return List<EntidadGuia> listado de las entidades guía habilitadas cuyo nombre completo incluye la cadena recibida
     */
    public List<EntidadGuia> findByNombreCompeto(String param){
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
     * @param cuit Long Cuit a buscar
     * @return EntidadGuia correspondiente al cuit indicado
     */
    public EntidadGuia findByCuit(Long cuit){
        List<EntidadGuia> lstEntidadGuia;
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
     * @param tipoEntidadGuia Parametrica Tipo de Entidad que deberán tener las EntidadGuia seleccionadas (Origen, Destino)
     * @return List<EntidadGuia> listado de las entidades guía habilitadas ordenadas por nombre completo del tipo indicado
     */
    public List<EntidadGuia> findByTipo (Parametrica tipoEntidadGuia){
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
     * @param cuit Long Cuit de la Persona de la que se quieren ver sus revisiones
     * @param inmNombre String Nombre del inmueble
     * @return List<EntidadGuia> listado de las revisiones de la entidad guía correspondiente al cuit y nombre del inmueble
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
