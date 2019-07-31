
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoAutorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
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
     * Método para obtener un listado de los productos según la clase y el tipo de intervención, 
     * con la autorización, el cupo otorgado, su saldo disponible y su cuenca asociada.
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones de los productos consultados
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param idInterv Long identificador de la paramétrica que indica el tipo de intervención
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getCuposSaldos(Date inicio, Date fin, Long idInterv){
        String queryString = "SELECT aut.numero, i.clase, SUM(i.total) AS cupo, SUM(i.saldo) AS saldo, i.unidad, cuenca.nombre AS cuenca, inm.departamento "
                + "FROM autorizacion aut "
                + "INNER JOIN inmueblesxautorizaciones inmaut ON inmaut.autorizacion_fk = aut.id "
                + "INNER JOIN inmueble inm ON inmaut.inmueble_fk = inm.id "
                + "LEFT JOIN parametrica cuenca ON cuenca.id = aut.param_cuenca_id "
                + "INNER JOIN parametrica interv ON interv.id = aut.param_intervencion_id "
                + "INNER JOIN itemproductivo i ON i.autorizacion_id = aut.id "
                + "INNER JOIN estadoautorizacion est ON est.id = aut.estado_id "
                + "WHERE interv.id = ?3 "
                + "AND aut.fechaalta >= ?1 "
                + "AND aut.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY aut.numero, i.clase, i.unidad, cuenca.nombre, inm.departamento "
                + "ORDER BY aut.numero";
        Query q = em.createNativeQuery(queryString)
                .setParameter(3, idInterv)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener un listado de los productos según la clase, el tipo de intervención y el departamento del predio, 
     * con la autorización, el cupo otorgado, su saldo disponible y su cuenca asociada.
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones de los productos consultados
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param idInterv Long identificador de la paramétrica que indica el tipo de intervención
     * @param depto String nombre del departamento del inmueble autorizado
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getCuposSaldosByDepto(Date inicio, Date fin, Long idInterv, String depto){
        String queryString = "SELECT aut.numero, i.clase, SUM(i.total) AS cupo, SUM(i.saldo) AS saldo, i.unidad, cuenca.nombre AS cuenca, inm.departamento "
                + "FROM autorizacion aut "
                + "INNER JOIN inmueblesxautorizaciones inmaut ON inmaut.autorizacion_fk = aut.id "
                + "INNER JOIN inmueble inm ON inmaut.inmueble_fk = inm.id "
                + "LEFT JOIN parametrica cuenca ON cuenca.id = aut.param_cuenca_id "
                + "INNER JOIN parametrica interv ON interv.id = aut.param_intervencion_id "
                + "INNER JOIN itemproductivo i ON i.autorizacion_id = aut.id "
                + "INNER JOIN estadoautorizacion est ON est.id = aut.estado_id "
                + "WHERE inm.departamento = ?4 "
                + "AND interv.id = ?3 "
                + "AND aut.fechaalta >= ?1 "
                + "AND aut.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "GROUP BY aut.numero, i.clase, i.unidad, cuenca.nombre, inm.departamento "
                + "ORDER BY aut.numero";
        Query q = em.createNativeQuery(queryString)
                .setParameter(4, depto)
                .setParameter(3, idInterv)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }

    /**
     * Método para obtener los productos autorizados según el tipo de intervención durante un período determinado.
     * Muestra productor, autorización, inmueble, departamento, producto, cupo autorizado y saldo disponible.
     * @param inicio Date fecha límite de inicio del período dentro del cual se autorizaron los productos consultados
     * @param fin Date fecha límite de fin del período dentro del cual se autorizaron los productos consultados
     * @param idInterv Long identificador de la paramétrica que indica el tipo de intervención
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getProdAut(Date inicio, Date fin, Long idInterv){
        String queryString = "SELECT per.nombrecompleto, per.cuit, aut.numero, inm.nombre, inm.idcatastral, inm.departamento, "
                + "i.clase, i.nombrevulgar, SUM(i.total) AS cupo, SUM(i.saldo) AS saldo, i.unidad, cuenca.nombre AS cuenca "
                + "FROM autorizacion aut "
                + "INNER JOIN inmueblesxautorizaciones inmaut ON inmaut.autorizacion_fk = aut.id "
                + "INNER JOIN inmueble inm ON inmaut.inmueble_fk = inm.id "
                + "LEFT JOIN parametrica cuenca ON cuenca.id = aut.param_cuenca_id "
                + "INNER JOIN parametrica interv ON interv.id = aut.param_intervencion_id "
                + "INNER JOIN itemproductivo i ON i.autorizacion_id = aut.id "
                + "INNER JOIN estadoautorizacion est ON est.id = aut.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = aut.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "WHERE interv.id = ?3 "
                + "AND aut.fechaalta >= ?1 "
                + "AND aut.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.nombrecompleto, per.cuit, aut.numero, inm.nombre, inm.idcatastral, i.clase, nombrevulgar, "
                + "i.unidad, cuenca.nombre, inm.departamento "
                + "ORDER BY aut.numero";
        Query q = em.createNativeQuery(queryString)
                .setParameter(3, idInterv)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener los productos autorizados según el tipo de intervención y el departamento del inmueble autorizado, 
     * durante un período determinado.
     * Muestra productor, autorización, inmueble, departamento, producto, cupo autorizado y saldo disponible.
     * @param inicio Date fecha límite de inicio del período dentro del cual se autorizaron los productos consultados
     * @param fin Date fecha límite de fin del período dentro del cual se autorizaron los productos consultados
     * @param idInterv Long identificador de la paramétrica que indica el tipo de intervención
     * @param depto String nombre del departamento en el cual se encuentra el o los inmuebles autorizados
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getProdAutDepto(Date inicio, Date fin, Long idInterv, String depto){
        String queryString = "SELECT per.nombrecompleto, per.cuit, aut.numero, inm.nombre, inm.idcatastral, inm.departamento, "
                + "i.clase, i.nombrevulgar, SUM(i.total) AS cupo, SUM(i.saldo) AS saldo, i.unidad, cuenca.nombre AS cuenca "
                + "FROM autorizacion aut "
                + "INNER JOIN inmueblesxautorizaciones inmaut ON inmaut.autorizacion_fk = aut.id "
                + "INNER JOIN inmueble inm ON inmaut.inmueble_fk = inm.id "
                + "LEFT JOIN parametrica cuenca ON cuenca.id = aut.param_cuenca_id "
                + "INNER JOIN parametrica interv ON interv.id = aut.param_intervencion_id "
                + "INNER JOIN itemproductivo i ON i.autorizacion_id = aut.id "
                + "INNER JOIN estadoautorizacion est ON est.id = aut.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = aut.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "WHERE inm.departamento = ?4 "
                + "AND interv.id = ?3 "
                + "AND aut.fechaalta >= ?1 "
                + "AND aut.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.nombrecompleto, per.cuit, aut.numero, inm.nombre, inm.idcatastral, i.clase, nombrevulgar, "
                + "i.unidad, cuenca.nombre, inm.departamento "
                + "ORDER BY aut.numero";
        Query q = em.createNativeQuery(queryString)
                .setParameter(4, depto)
                .setParameter(3, idInterv)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }

    /**
     * Método para obtener la sumas de superficies autorizadas para cada productor, sin especificar departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getSupGralSinRodal(Date inicio, Date fin){
        String queryString = "SELECT per.nombrecompleto, per.cuit, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, a.departamento "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = a.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.id, a.departamento";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener la sumas de superficies autorizadas para cada productor, especificando departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param depto String nombre del Departamento dentro del cual se encuentra el predio del que se autorizó la extracción
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getSupGralDeptoSinRodal(Date inicio, Date fin, String depto){
        String queryString = "SELECT per.nombrecompleto, per.cuit, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, a.departamento "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = a.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND a.departamento = ?3 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.id, a.departamento";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto);
        return q.getResultList();
    }
    
    /**
     * Método para obtener la sumas de superficies autorizadas para cada productor por rodal, sin especificar departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getSupGralConRodal(Date inicio, Date fin){
        String queryString = "SELECT per.nombrecompleto, per.cuit, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, a.departamento, rod.numorden "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = a.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "LEFT JOIN rodalesxautorizaciones rodaut ON rodaut.autorizacion_fk = a.id "
                + "LEFT JOIN rodal rod ON rod.id = rodaut.rodal_fk "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.id, a.departamento, rod.numorden";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    } 
    
    /**
     * Método para obtener la sumas de superficies autorizadas para cada productor por rodal, especificando departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se registraron las autorizaciones consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se registraron las autorizaciones consultadas
     * @param depto String nombre del Departamento dentro del cual se encuentra el predio del que se autorizó la extracción
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getSupGralDeptoConRodal(Date inicio, Date fin, String depto){
        String queryString = "SELECT per.nombrecompleto, per.cuit, SUM(a.suptotal) suptotal, SUM(a.supsolicitada) supsol, SUM(a.supautorizada) supaut, a.departamento, rod.numorden "
                + "FROM vi_autcondeptos a "
                + "INNER JOIN parametrica interv ON a.param_intervencion_id = interv.id "
                + "INNER JOIN estadoautorizacion est ON est.id = a.estado_id "
                + "INNER JOIN personasxautorizaciones peraut ON peraut.autorizacion_fk = a.id "
                + "INNER JOIN persona per ON per.id = peraut.persona_fk "
                + "INNER JOIN parametrica rol ON rol.id = per.rol_id "
                + "LEFT JOIN rodalesxautorizaciones rodaut ON rodaut.autorizacion_fk = a.id "
                + "LEFT JOIN rodal rod ON rod.id = rodaut.rodal_fk "
                + "WHERE a.fechaalta >= ?1 "
                + "AND a.fechaalta <= ?2 "
                + "AND a.departamento = ?3 "
                + "AND est.habilitaemisionguia = TRUE "
                + "AND rol.nombre = 'PROPONENTE' "
                + "GROUP BY per.id, a.departamento, rod.numorden";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto);
        return q.getResultList();
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
     * Método para obtener los tipos de intervenciones autorizadas.
     * Para autorizaciones habilitadas
     * @return List<Parametrica> listado con las paramétricas solicitadas
     */
    public List<Parametrica> findTiposIntervAut(){
        String queryString = "SELECT DISTINCT aut.intervencion "
                + "FROM Autorizacion aut "
                + "WHERE aut.estado.nombre = 'HABILITADO' "
                + "ORDER BY aut.intervencion.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método para obtener los departamentos de los inmuebles a los cuales se
     * los autorizó a extraer productos. El estado de la Autorización será "HABILITADO"
     * @return 
     */
    public List<Object> findDeptoByOrigen(){
        String queryString = "SELECT DISTINCT inm.departamento "
                + "FROM inmueble inm "
                + "INNER JOIN inmueblesxautorizaciones inmaut ON inmaut.inmueble_fk = inm.id "
                + "INNER JOIN autorizacion aut ON aut.id = inmaut.autorizacion_fk "
                + "INNER JOIN estadoautorizacion est ON est.id = aut.estado_id "
                + "WHERE est.nombre = 'HABILITADO' "
                + "ORDER BY inm.departamento";
        Query q = em.createNativeQuery(queryString);
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
