
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.AutSupConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProdConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal;
import java.util.ArrayList;
import java.util.Date;
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
     * para ser tomada como fuente. En estado que habilita emisión de Guía y en vigencia
     * @param per Persona proponente cuyas autorizaciones se buscan
     * @return List<Autorizacion> listado de Autorizaciones vinculadas al proponente
     */
    public List<Autorizacion> getFuenteByProponente(Persona per){
        Date hoy = new Date(System.currentTimeMillis());
        String rolProponente = ResourceBundle.getBundle("/Config").getString("Proponente");
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.personas per "
                + "INNER JOIN per.rolPersona rol "
                + "WHERE per = :per "
                + "AND rol.nombre = :rolProponente "
                + "AND aut.estado.habilitaEmisionGuia = true "
                + "AND aut.fechaVencAutoriz >= :hoy";
        Query q = em.createQuery(queryString)
                .setParameter("per", per)
                .setParameter("hoy", hoy)
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
     * Método que obtiene las Autorizaciones en estado de HABILITADA que se hayan registrado entre
     * las fechas de inicio y fin recibidas como parámetro
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @return List<Autorizacion> listado de Autorizaciones dadas de alta entre las fechas indicadas
     */
    public List<Autorizacion> getByFechaAlta(Date inicio, Date fin){
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "WHERE aut.estado.nombre = 'HABILITADO' "
                + "AND aut.fechaAlta >= :inicio "
                + "AND aut.fechaAlta <= :fin";
        Query q = em.createQuery(queryString)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin);
        return q.getResultList(); 
    }
    
    /**
     * Método que obtiene las Autorizaciones en estado de HABILITADA que se hayan registrado entre
     * las fechas de inicio y fin recibidas como parámetro en el departamento consultado
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param depto String nombre del departamento en el cual se encuentra el o los inmuebles autorizados
     * @return List<Autorizacion> listado de Autorizaciones dadas de alta entre las fechas indicadas
     */
    public List<Autorizacion> getByFechaAltaDepto(Date inicio, Date fin, String depto){
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.inmuebles inm "
                + "WHERE aut.estado.nombre = 'HABILITADO' "
                + "AND inm.departamento = :depto "
                + "AND aut.fechaAlta >= :inicio "
                + "AND aut.fechaAlta <= :fin";
        Query q = em.createQuery(queryString)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .setParameter("depto", depto);
        return q.getResultList(); 
    }
    
    /**
     * Método para obtener los datos de un producto autorizado para la extracción durante un período para toda la provincia
     * @param inicio Date fecha límite de inicio del período dentro del cual se autorizaron los productos consultados
     * @param fin Date fecha límite de final del período dentro del cual se autorizaron los productos consultados
     * @param idProd Long identificador único del producto que se está consultando
     * @return ProdConsulta ProdConsulta que encapsula los datos del producto consultado.
     */
    public ProdConsulta getProdQuery(Date inicio, Date fin, Long idProd){
        String queryString = "SELECT i.idprod, i.nombrevulgar, i.clase, i.unidad, SUM(i.total) AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN autorizacion a ON a.id = i.autorizacion_id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND i.idprod = ?3 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY i.idprod, i.nombrevulgar, i.clase, i.unidad"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, idProd);
        return (ProdConsulta)q.getSingleResult();
    }  
    
    /**
     * Método para obtener los datos de un producto autorizado para la extracción durante un período para un departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se autorizaron los productos consultados
     * @param fin Date fecha límite de final del período dentro del cual se autorizaron los productos consultados
     * @param depto String nombre del departamento de origen de los productos autorizados
     * @param idProd Long identificador único del producto que se está consultando
     * @return ProdConsulta ProdConsulta que encapsula los datos del producto consultado.
     */
    public ProdConsulta getProdDeptoQuery(Date inicio, Date fin, String depto, Long idProd){
        String queryString = "SELECT i.idprod, i.nombrevulgar, i.clase, i.unidad, SUM(i.total) AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN vi_autcondeptos a ON a.id = i.autorizacion_id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND a.departamento = ?3 "
                + "AND i.idprod = ?4 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY i.idprod, i.nombrevulgar, i.clase, i.unidad"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto)
                .setParameter(4, idProd);
        return (ProdConsulta)q.getSingleResult();
    }      
    
    /**
     * Método para obtener el resumen de superficies vinculadas a las autorizaciones según el tipo de intervención:
     * total del predio, solicitada y autorizada.
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @return List<AutSupConsulta> listado del detalle de las superficies por tipo de intervención
     */
    public List<AutSupConsulta> getSupGral(Date inicio, Date fin){
        String queryString = "SELECT interv.id idtipoint, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, interv.nombre as tipoint "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY interv.id";
        Query q = em.createNativeQuery(queryString, AutSupConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin);   
        return (List<AutSupConsulta>) q.getResultList();
    }
    
    /**
     * Método para obtener el resumen de superficies vinculadas a las autorizaciones según 
     * el Departamento del predio y el tipo de intervención: total del predio, solicitada y autorizada.
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param depto String nombre del Departamento dentro del cual se encuentra el predio del que se autorizó la extracción
     * @return List<AutSupConsulta> listado del detalle de las superficies por tipo de intervención y Departamento.
     */
    public List<AutSupConsulta> getSupGralDepto(Date inicio, Date fin, String depto){
        String queryString = "SELECT interv.id idtipoint, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, interv.nombre as tipoint "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND a.departamento = ?3 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY interv.id, a.departamento";
        Query q = em.createNativeQuery(queryString, AutSupConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto);   
        return (List<AutSupConsulta>) q.getResultList();
    }
    
    /**
     * Método para obtener las Autorizaciones vinculadas a un rodal.
     * Solo para los CGL configurados para trabajar con rodales de inmuebles.
     * @param rodal Rodal a buscar las Autorizaciones vinculadas.
     * @return List<Autorizacion> listado de las Autorizaciones vinculadas al rodal.
     */
    public List<Autorizacion> getByRodal(Rodal rodal){
        Date fechaHoy = new Date(System.currentTimeMillis());
        String queryString = "SELECT aut FROM Autorizacion aut "
                + "INNER JOIN aut.rodales rodal "
                + "WHERE rodal = :rodal "
                + "AND aut.fechaVencAutoriz > :fechaHoy";
        Query q = em.createQuery(queryString)
                .setParameter("rodal", rodal)
                .setParameter("fechaHoy", fechaHoy);
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
