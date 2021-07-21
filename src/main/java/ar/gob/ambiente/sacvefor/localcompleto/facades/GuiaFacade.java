
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProdConsulta;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
     * Método para obtener las guías vinculadas a un obrajero
     * @param obrj Persona obrajero cuyas guías se quieren obtener
     * @return List<Guia> listado de las guías vinculadas al obrajero
     */
    public List<Guia> getByObrajero(Persona obrj){
        String queryString = "SELECT guia FROM Guia guia "
                + "INNER JOIN guia.obrajeros obrj "
                + "WHERE obrj = :obrj";
        Query q = em.createQuery(queryString)
                .setParameter("obrj", obrj);
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
     * Método que devuelve las Guías vinculadas a un número de fuente (Autorización) determinado.
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
     * Método para obtener un listado de las Guías que tomaron productos de la guía
     * recibida como parámetro.
     * @param gDesc Guía de la cual se buscan sus hijas
     * @return List<Guia> listado de las guías vinculadas a la madre recibida como parámetro
     */
    public List<Guia> findHijas (Guia gDesc){
        String queryString = "SELECT gHija FROM Guia gHija "
                + "INNER JOIN gHija.guiasfuentes gDesc "
                + "WHERE gDesc = :gDesc";
        Query q = em.createQuery(queryString)
                .setParameter("gDesc", gDesc);
        return q.getResultList();
    }
    
    /**
     * Metodo que devuelve las Guías disponibles para el descuento de productos para otras guías 
     * según el Productor cuyo CUIT se recibe y la Fuente (Autorización)
     * @param cuit Long cuit del Productor cuyas Guías se busca
     * @param fuente String número de la Autorización que oficia de fuente de productos para las guías a descontar
     * @return List<Guia> listado de las guías correspondientes
     */
    public List<Guia> getDispByTitularYFuente(Long cuit, String fuente){
        Date hoy = new Date(System.currentTimeMillis());
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.cuit = :cuit "
                + "AND guia.tipo.habilitaDesc = true "
                + "AND guia.estado.habilitaFuenteProductos = true "
                + "AND guia.numFuente = :fuente "
                + "AND guia.fechaVencimiento >= :hoy";
        Query q = em.createQuery(queryString)
                .setParameter("hoy", hoy)
                .setParameter("fuente", fuente)
                .setParameter("cuit", cuit);
        return q.getResultList();
    }
    
    /**
     * Obtiene todas las guías cuyo tipo no habilita transporte, emitidas y vigentes,  
     * según el número de fuente (para guías de fiscalización de primer movimiento)
     * Requerido para los servicios de TRAZ
     * @param fuente String código de la Autorización de la que descontó
     * @return List<Guia> listado de las Guías retornadas
     */
    public List<Guia> findFiscByNumFuente(String fuente){
        Date hoy = new Date(System.currentTimeMillis());
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.numFuente = :fuente "
                + "AND guia.tipo.habilitaTransp = false "
                + "AND guia.estado.habilitaFuenteProductos = true "
                + "AND guia.fechaVencimiento >= :hoy";
        Query q = em.createQuery(queryString)
              .setParameter("hoy", hoy)  
              .setParameter("fuente", fuente);
        return q.getResultList();
    }
    
    /**
     * Obtiene todas las guías cuyo tipo no habilita transporte, emitidas y vigentes, 
     * según el id_establecimiento del origen (para guías de fiscalización de removido)
     * Requerido para los servicios de TRAZ
     * @param id_establecimiento Long identificación del Establecimiento origen registrado en TRAZ
     * @return List<Guia> listado de las Guías retornadas
     */
    public List<Guia> findFiscByIdEstablecimiento(Long id_establecimiento){
        Date hoy = new Date(System.currentTimeMillis());
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.id_establecimiento = :id_establecimiento "
                + "AND guia.tipo.habilitaTransp = false "
                + "AND guia.estado.habilitaFuenteProductos = true "
                + "AND guia.fechaVencimiento >= :hoy";
        Query q = em.createQuery(queryString)
                .setParameter("hoy", hoy)
                .setParameter("id_establecimiento", id_establecimiento);
        return q.getResultList();
    }
    
    /**
     * Metodo que devuelve las Guías emitidas que tengan como Destinatario al CUIT recibido.
     * Que no sean de movimiento interno
     * @param cuit Long cuit del Destinatario cuyas Guías se busca
     * @return List<Guia> listado de las guías destinadas al cuit remitido
     */
    public List<Guia> getEmitidasByDestinatario(Long cuit){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.destino.cuit = :cuit "
                + "AND guia.tipo.movInterno = false "
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
     * Método que obtiene las Guías en estado de EMITIDA o CERRADA que se hayan emitido entre
     * las fechas de inicio y fin recibidas como parámetro, para toda la provincia
     * @param inicio Date fecha límite de inicio del período dentro del cual se emitieron las guías consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se emitieron las guías consultadas
     * @return List<Guia> listado de las guías emitidas durante el período correspondiente.
     */
    public List<Guia> getByFechaEmision(Date inicio, Date fin){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.fechaEmisionGuia >= :inicio "
                + "AND guia.fechaEmisionGuia <= :fin "
                + "AND guia.tipo.habilitaTransp = true "
                + "AND guia.tipo.movInterno = false "
                + "AND (guia.estado.nombre = 'EMITIDA' "
                + "OR guia.estado.nombre = 'CERRADA')";
        Query q = em.createQuery(queryString)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin);
        return q.getResultList();
    }
    
    /**
     * Método que obtiene las Guías en estado de EMITIDA o CERRADA que se hayan emitido entre
     * las fechas de inicio y fin recibidas como parámetro, para un departamento determinado
     * @param inicio Date fecha límite de inicio del período dentro del cual se emitieron las guías consultadas
     * @param fin Date fecha límite de fin del período dentro del cual se emitieron las guías consultadas
     * @param depto String nombre del departamento de origen de los productos de la guía
     * @return List<Guia> listado de las guías emitidas durante el período correspondiente.
     */
    public List<Guia> getByFechaEmisionDepto(Date inicio, Date fin, String depto){
        String queryString = "SELECT guia FROM Guia guia "
                + "INNER JOIN guia.origen origen "
                + "WHERE guia.fechaEmisionGuia >= :inicio "
                + "AND guia.fechaEmisionGuia <= :fin "
                + "AND guia.tipo.habilitaTransp = true "
                + "AND guia.tipo.movInterno = false "
                + "AND origen.departamento =:depto "
                + "AND (guia.estado.nombre = 'EMITIDA' "
                + "OR guia.estado.nombre = 'CERRADA')";
        Query q = em.createQuery(queryString)
                .setParameter("depto", depto)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener los datos de un producto movido durante un período para toda la provincia
     * @param inicio Date fecha límite de inicio del período dentro del cual se emitieron las guías de los productos consultados
     * @param fin Date fecha límite del fin del período dentro del cual se emitieron las guías de los productos consultados
     * @param idProd Long identificador único del producto a buscar
     * @return ProdConsulta ProdConsulta que encapsula los datos del producto consultado.
     */
    public ProdConsulta getProdQuery(Date inicio, Date fin, Long idProd){
        String queryString = "SELECT i.idprod, i.nombrevulgar, i.clase, i.unidad, SUM(i.total) AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN guia g ON g.id = i.guia_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN estadoguia est ON est.id = g.estado_id "
                + "WHERE g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND tipo.habilitatransp = TRUE "
                + "AND tipo.movInterno = FALSE "
                + "AND i.idprod = ?3 "
                + "AND (est.nombre = 'EMITIDA' OR est.nombre = 'CERRADA') "
                + "GROUP BY i.idprod, i.nombrevulgar, i.clase, i.unidad"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, idProd);
        return (ProdConsulta)q.getSingleResult();
    }    
    
    /**
     * Método para obtener los datos de un producto movido durante un período para un departamento
     * @param inicio Date fecha límite de inicio del período dentro del cual se emitieron las guías de los productos consultados
     * @param fin Date fecha límite del fin del período dentro del cual se emitieron las guías de los productos consultados
     * @param depto String nombre del departamento de origen de los productos movidos
     * @param idProd Long identificador único del producto a buscar
     * @return ProdConsulta ProdConsulta que encapsula los datos del producto consultado.
     */
    public ProdConsulta getProdDeptoQuery(Date inicio, Date fin, String depto, Long idProd){
        String queryString = "SELECT i.idprod, i.nombrevulgar, i.clase, i.unidad, SUM(i.total) AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN guia g ON g.id = i.guia_id "
                + "INNER JOIN entidadguia ent ON ent.id = g.origen_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN estadoguia est ON est.id = g.estado_id "
                + "WHERE g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND tipo.habilitatransp = TRUE "
                + "AND tipo.movInterno = FALSE "
                + "AND ent.departamento = ?3 "
                + "AND i.idprod = ?4 "
                + "AND (est.nombre = 'EMITIDA' OR est.nombre = 'CERRADA') "
                + "GROUP BY i.idprod, i.nombrevulgar, i.clase, i.unidad"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto)
                .setParameter(4, idProd);
        return (ProdConsulta)q.getSingleResult();
    }
    
    /**
     * Método para obtener la cantidad de formularios emitidos por una guía madre
     * @param id Long identificador de la guía a consultar
     * @return Integer cantidad de formularios emitidos por la guía
     */
    public Integer getCantFormProv(Long id){
        String queryString = "SELECT formemitidos FROM guia WHERE id = ?1";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, id);
        return (Integer)q.getSingleResult();
    }
    
    /**
     * Método para obtener las guías emitidas por el titular cuyo cuit se recibe,
     * que adeudan el pago de tasas
     * @param cuit cuit del titular de la guía
     * @return List<Guia> listado de las guías correspondientes
     */
    public List<Guia> getSinPago(Long cuit){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.origen.cuit = :cuit "
                + "AND guia.codRecibo = null "
                + "AND guia.estado.nombre = 'EMITIDA' "
                + "AND guia.tipo.abonaTasa = true";
        Query q = em.createQuery(queryString)
                .setParameter("cuit", cuit);
        return q.getResultList(); 
    }
    
    /**
     * Método para obtener todas las guías registradas de un tipo determinado.
     * @param tipo TipoGuia tipo de guías a buscar
     * @return List<Guia> listado correspondiente.
     */
    public List<Guia> getByTipo(TipoGuia tipo){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.tipo = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("tipo", tipo);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las guías registradas cuyo número de fuente sea el
     * recibido como parámetro
     * @param numFuente String número de la Autorización fuente
     * @return List<Guia> listado correspondiente.
     */
    public List<Guia> getByNumFuente(String numFuente){
        String queryString = "SELECT guia FROM Guia guia "
                + "WHERE guia.numFuente = :numFuente";
        Query q = em.createQuery(queryString)
                .setParameter("numFuente", numFuente);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las guías emitidas entre una fecha y otra para un departamento determinado
     * @param inicio Date fecha de inicio de la emision de las guias consultadas
     * @param fin Date fecha de fin de la emision de las guias consultadas
     * @param depto String departamento desde el cual se emitieron las guías
     * @return List<Long> listado de los id de las guías correspondientes.
     */
    public List<Long> getIdByFechaEmisionDepto(Date inicio, Date fin, String depto){
        String queryString = "SELECT guia.id FROM Guia guia "
                + "INNER JOIN guia.origen origen "
                + "WHERE guia.fechaEmisionGuia >= :inicio "
                + "AND guia.fechaEmisionGuia <= :fin "
                + "AND guia.tipo.habilitaTransp = true "
                + "AND guia.tipo.movInterno = false "
                + "AND origen.departamento =:depto "
                + "AND (guia.estado.nombre = 'EMITIDA' "
                + "OR guia.estado.nombre = 'CERRADA')";
        Query q = em.createQuery(queryString)
                .setParameter("depto", depto)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener un listado de departamentos desde los cuales se han emitido guías.
     * @return List<String> listado de los departamentos consultados 
     */
    public List<String> findDeptoByOrigen(){
        String queryString = "SELECT DISTINCT guia.origen.departamento "
                + "FROM Guia guia "
                + "WHERE guia.estado.nombre = 'EMITIDA' "
                + "OR guia.estado.nombre = 'CERRADA' "
                + "ORDER BY guia.origen.departamento";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Método para obtener los productos removidos entre una fecha y otra.
     * @param inicio Date fecha de inicio de la emision de las guias consultadas
     * @param fin Date fecha de fin de la emision de las guias consultadas
     * @return List <ProdConsulta> listado de los productos consultados
     */
    public List<ProdConsulta> getProdRemov(Date inicio, Date fin){
        String queryString = "SELECT DISTINCT i.idprod, i.nombrevulgar, i.clase, i.unidad, 0 AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN guia g ON g.id = i.guia_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN estadoguia est ON est.id = g.estado_id "
                + "WHERE g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND tipo.habilitatransp = TRUE "
                + "AND tipo.movInterno = FALSE "
                + "AND (est.nombre = 'EMITIDA' OR est.nombre = 'CERRADA') "
                + "ORDER BY i.nombrevulgar, i.clase"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return (List<ProdConsulta>)q.getResultList();
    }
    
    /**
     * Método para obtener los productos removidos entre una fecha y otra para un departamento determinado.
     * @param inicio Date fecha de inicio de la emision de las guias consultadas
     * @param fin Date fecha de fin de la emision de las guias consultadas
     * @param depto String departamento desde el cual se emitieron las guías
     * @return List<ProdConsulta> listado de los productos consultados
     */
    public List<ProdConsulta> getProdRemovXDepto(Date inicio, Date fin, String depto){
        String queryString = "SELECT DISTINCT i.idprod, i.nombrevulgar, i.clase, i.unidad, 0 AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN guia g ON g.id = i.guia_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN estadoguia est ON est.id = g.estado_id "
                + "INNER JOIN entidadguia ent ON ent.id = g.origen_id "
                + "WHERE g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND ent.departamento = ?3 "
                + "AND tipo.habilitatransp = TRUE "
                + "AND tipo.movInterno = FALSE "
                + "AND (est.nombre = 'EMITIDA' OR est.nombre = 'CERRADA') "
                + "ORDER BY i.nombrevulgar, i.clase"; 
        Query q = em.createNativeQuery(queryString, ProdConsulta.class)
                .setParameter(1, inicio)
                .setParameter(2, fin)
                .setParameter(3, depto);
        return (List<ProdConsulta>)q.getResultList();
    }
    
    /**
     * Método para obtener el total de productos removidos (sin especificar especies y clase) entre una fecha y otra.
     * @param inicio Date fecha de inicio de la emision de las guias consultadas
     * @param fin Date fecha de fin de la emision de las guias consultadas
     * @return float cantidad de productos consultada
     */
    public float getTotalProdRemov(Date inicio, Date fin){
        String queryString = "SELECT SUM(i.totalkg) AS total "
                + "FROM itemproductivo i "
                + "INNER JOIN guia g ON g.id = i.guia_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN estadoguia est ON est.id = g.estado_id "
                + "WHERE g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND tipo.habilitatransp = TRUE "
                + "AND tipo.movInterno = FALSE "
                + "AND (est.nombre = 'EMITIDA' OR est.nombre = 'CERRADA')"; 
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        if(q.getSingleResult() == null){
            return 0;
        }else{
            return (float)q.getSingleResult();
        }
    }
    
    /**
     * Método para paginar la totalidad de las guías
     * @param first int número de orden inicial a partir del cual tomar los registros
     * @param count int cantidad de registros a tomar a partir del first
     * @return List<Guia> listad de las guías consultadas
     */
    public List<Guia> findAllPaginated(int first, int count){
        String queryString = "SELECT guia FROM Guia guia ORDER BY guia.id";
        Query q = em.createQuery(queryString)
                .setFirstResult(first)
                .setMaxResults(count);
        return q.getResultList();
    }
    
    /**
     * Método para paginar la totalidad de las guías con una serie de parámetros para filtrar.
     * @param first int número de orden inicial a partir del cual tomar los registros
     * @param count int cantidad de registros a tomar a partir del first
     * @param filters Map<String, Object> mapeo de los pares campo-valor a filtrar
     * @return List<Guia> listado de guías consultadas
     */
    public List<Guia> findAllPagFilterJpa(int first, int count, Map<String, Object> filters){
        StringBuilder qString; 
        qString = new StringBuilder();
        qString.append("SELECT guia FROM Guia guia WHERE 1=1 ");
        for(Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            String param;
            // verifico si es un campo directo
            if(field.contains(".")){
                // si es indirecto (contiene '.') lo saco
                param = field.replace(".", "");
            }else{
                // si es directo lo paso como viene
                param = field;
            }
            // si consulta por CUIT hago un CAST
            if(field.contains("cuit")){
                qString.append("AND CAST(guia.").append(field).append(" AS text) LIKE :").append(param).append(" ");
            }else{
                qString.append("AND guia.").append(field).append(" LIKE :").append(param).append(" ");
            }
        }
        qString.append("ORDER BY guia.id");
        Query q = em.createQuery(qString.toString());
        
        for(Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            String param;
            // verifico si es un campo directo
            if(field.contains(".")){
                // si es indirecto (contiene '.') lo saco
                param = field.replace(".", "");
            }else{
                // si es directo lo paso como viene
                param = field;
            }
            q.setParameter(param, "%" + value.toString().toUpperCase() + "%");
        }
        q.setFirstResult(first);
        q.setMaxResults(count);
        
        return q.getResultList();
    }  
    
    /**
     * Metodo para obtener la cantidad total de guías conforme a los parámetros de consulta.
     * @param filters Map<String, Object> mapeo de los pares campo-valor a filtrar
     * @return int cantidad de guías consultada
     */
    public int findAllPagFilterJpaCount(Map<String, Object> filters){
        StringBuilder qString; 
        qString = new StringBuilder();
        qString.append("SELECT guia.id FROM Guia guia WHERE 1=1 ");
        for(Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            String param;
            // verifico si es un campo directo
            if(field.contains(".")){
                // si es indirecto (contiene '.') lo saco
                param = field.replace(".", "");
            }else{
                // si es directo lo paso como viene
                param = field;
            }
            // si consulta por CUIT hago un CAST
            if(field.contains("cuit")){
                qString.append("AND CAST(guia.").append(field).append(" AS text) LIKE :").append(param).append(" ");
            }else{
                qString.append("AND guia.").append(field).append(" LIKE :").append(param).append(" ");
            }
        }
        Query q = em.createQuery(qString.toString());
        
        for(Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            String param;
            // verifico si es un campo directo
            if(field.contains(".")){
                // si es indirecto (contiene '.') lo saco
                param = field.replace(".", "");
            }else{
                // si es directo lo paso como viene
                param = field;
            }
            q.setParameter(param, "%" + value.toString().toUpperCase() + "%");
        }
        return q.getResultList().size();
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
