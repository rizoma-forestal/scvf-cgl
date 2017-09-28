
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import java.math.BigInteger;
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
 * Acceso a datos para la entidad Guía
 * @author rincostante
 */
@Stateless
public class GuiaFacade extends AbstractFacade<Guia> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GuiaFacade() {
        super(Guia.class);
    }
    
    /**
     * Metodo para validar una Guía existente según el codigo
     * @param codigo
     * @return 
     */
    public Guia getExistente(String codigo) {
        List<Guia> lstGuias;
        em = getEntityManager();
        
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.codigo = :codigo";
        Query q = em.createQuery(queryString)
                .setParameter("codigo", codigo);
        lstGuias = q.getResultList();
        if(lstGuias.isEmpty()){
            return null;
        }else{
            return lstGuias.get(0);
        }
    }   
    
    /**
     * Método para obetener las Guías cuyo origen tiene el CUIT recibido como parámetro
     * @param cuit : CUIT de la Persona vinculada a la Guía como origen 
     * @return 
     */
    public List<Guia> getByOrigen(Long cuit){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }   
    
    /**
     * Método para obetener las Guías cuyo destino tiene el CUIT recibido como parámetro
     * @param cuit : CUIT de la Persona vinculada a la Guía como destino 
     * @return 
     */
    public List<Guia> getByDestino(Long cuit){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    } 

    /**
     * Método para obtener las Guías según el id del Producto extraído
     * @param idProd : identificación del Producto incluido en la Guía
     * @return 
     */
    public List<Guia> getByIdProd(Long idProd){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "INNER JOIN guia.items item "
                + "WHERE item.idProd = :idProd";
        Query q = em.createQuery(queryString)
                .setParameter("idProd", idProd);
        return q.getResultList();
    }    
    
    /**
     * Método para obtener las Guías según su estado
     * @param estado : estado en el cual deberán estar las Guías retornadas
     * @return 
     */
    public List<Guia> getByEstado(EstadoGuia estado){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.estado = :estado";
        Query q = em.createQuery(queryString)
                .setParameter("estado", estado);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve las Guías de trasporte vinculadas a un vehículo determinado
     * identificado por su matrícula
     * @param matricula : matrícula del Vehículo cuyas Guías se quiere obtener.
     * @return 
     */
    public List<Guia> getByVehiculo(String matricula){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.transporte.vehiculo.matricula = :matricula";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        return q.getResultList();
    }
    
    /**
     * Devuelve el último id registrado para una Guía
     * @return 
     */
    public int getUltimoId(){    
        em = getEntityManager();    
        String queryString = "SELECT MAX(id) FROM guia"; 
        Query q = em.createNativeQuery(queryString);
        BigInteger result = (BigInteger)q.getSingleResult();
        if(result == null){
            return 0;
        }else{
            return result.intValue();
        }
    }      
    
    /**
     * Método que devuelve las Guías vinculadas a un número de fuente determinado.
     * Sea este de una Autorización o de una Guía
     * @param numFuente : número de la fuente a buscar
     * @return 
     */
    public List<Guia> findByNumFuente(String numFuente){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.numFuente = :numFuente";
        Query q = em.createQuery(queryString)
                .setParameter("numFuente", numFuente);
        return q.getResultList();
    }
    
    /**
     * Metodo que devuelve las Guías en condiciones de ser fuentes de productos para el Productor cuyo CUIT se recibe
     * @param cuit : cuit del Productor cuyas Guías se busca
     * @return 
     */
    public List<Guia> getFuenteByTitular(Long cuit){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.cuit = :cuit "
                + "AND guia.tipo.habilitaDesc = true "
                + "AND guia.estado.habilitaFuenteProductos = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }
    
    /**
     * Metodo que devuelve las Guías emitidas que tengan como Destinatario al CUIT recibido
     * @param cuit : cuit del Destinatario cuyas Guías se busca
     * @return 
     */
    public List<Guia> getEmitidasByDestinatario(Long cuit){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit "
                + "AND guia.estado.nombre = 'EMITIDA'";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve las Guías de Transporte emitidas cuyo Vehículo se corresponde con el de la matrícula ingresada
     * @param matricula
     * @return 
     */
    public List<Guia> getByMatricula(String matricula){
        em = getEntityManager();
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.transporte.vehiculo.matricula = :matricula "
                + "AND guia.estado.nombre = 'EMITIDA'";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param codigo : Código único identificatorio de la Guía
     * @return 
     */
    public List<Guia> findRevisions(String codigo){  
        List<Guia> lstGuias = new ArrayList<>();
        Guia guia = this.getExistente(codigo);
        if(guia != null){
            Long id = this.getExistente(codigo).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Guia.class, id);
            for (Number n : revisions) {
                Guia cli = reader.find(Guia.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTipo());
                Hibernate.initialize(cli.getEstado());
                Hibernate.initialize(cli.getItems());
                Hibernate.initialize(cli.getOrigen());
                Hibernate.initialize(cli.getDestino());
                Hibernate.initialize(cli.getTransporte());
                lstGuias.add(cli);
            }
        }
        return lstGuias;
    }             
}
