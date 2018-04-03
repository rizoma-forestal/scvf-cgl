
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
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

    /**
     * Constructor
     */
    public GuiaFacade() {
        super(Guia.class);
    }
    
    /**
     * Metodo para validar una Guía existente según el codigo
     * @param codigo String código único de la guía
     * @return Guia guía solicitada
     */
    public Guia getExistente(String codigo) {
        List<Guia> lstGuias;
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
     * @param cuit Long CUIT de la Persona vinculada a la Guía como origen 
     * @return List<Guia> listado de las guías cuyo origen corresponde al cuit
     */
    public List<Guia> getByOrigen(Long cuit){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }   
    
    /**
     * Método para obetener las Guías cuyo destino tiene el CUIT recibido como parámetro
     * @param cuit Long CUIT de la Persona vinculada a la Guía como destino 
     * @return List<Guia> listado de las guías cuyo destino corresponde al cuit
     */
    public List<Guia> getByDestino(Long cuit){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    } 

    /**
     * Método para obtener las Guías según el id del Producto extraído
     * @param idProd Long identificación del Producto incluido en la Guía
     * @return List<Guia> listado de las guías que contienen el producto
     */
    public List<Guia> getByIdProd(Long idProd){
        String queryString = "SELECT guia FROM Guia guia "
                + "INNER JOIN guia.items item "
                + "WHERE item.idProd = :idProd";
        Query q = em.createQuery(queryString)
                .setParameter("idProd", idProd);
        return q.getResultList();
    }    
    
    /**
     * Método para obtener las Guías según su estado
     * @param estado EstadoGuia estado en el cual deberán estar las Guías retornadas
     * @return List<Guia> listado de las guías con el estado remitido
     */
    public List<Guia> getByEstado(EstadoGuia estado){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.estado = :estado";
        Query q = em.createQuery(queryString)
                .setParameter("estado", estado);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve las Guías de trasporte vinculadas a un vehículo determinado
     * identificado por su matrícula
     * @param matricula String matrícula del Vehículo cuyas Guías se quiere obtener.
     * @return List<Guia> listado de las guías transportadas por el vehículo
     */
    public List<Guia> getByVehiculo(String matricula){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.transporte.vehiculo.matricula = :matricula";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve el último id registrado para una Guía
     * @return int identificador de la última guía registrada
     */
    public int getUltimoId(){      
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
     * @param numFuente String número de la fuente a buscar
     * @return List<Guia> listado de las guías vinculadas a la fuente cuyo número se remite
     */
    public List<Guia> findByNumFuente(String numFuente){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.numFuente = :numFuente";
        Query q = em.createQuery(queryString)
                .setParameter("numFuente", numFuente);
        return q.getResultList();
    }
    
    /**
     * Metodo que devuelve las Guías en condiciones de ser fuentes de productos para el Productor cuyo CUIT se recibe
     * @param cuit Long cuit del Productor cuyas Guías se busca
     * @return List<Guia> listado de las guías correspondientes
     */
    public List<Guia> getFuenteByTitular(Long cuit){
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
     * @param cuit Long cuit del Destinatario cuyas Guías se busca
     * @return List<Guia> listado de las guías destinadas al cuit remitido
     */
    public List<Guia> getEmitidasByDestinatario(Long cuit){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit "
                + "AND guia.estado.nombre = 'EMITIDA'";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve las Guías de Transporte emitidas cuyo Vehículo se corresponde con el de la matrícula ingresada
     * @param matricula String matrícula del vehículo
     * @return List<Guia> listado de las guías transportadas por el vehículo en estado de "emitida"
     */
    public List<Guia> getByMatricula(String matricula){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.transporte.vehiculo.matricula = :matricula "
                + "AND guia.estado.nombre = 'EMITIDA'";
        Query q = em.createQuery(queryString)
                .setParameter("matricula", matricula);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param codigo String Código único identificatorio de la Guía
     * @return List<Guia> listado de las revisiones de una guía para su auditoría
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
