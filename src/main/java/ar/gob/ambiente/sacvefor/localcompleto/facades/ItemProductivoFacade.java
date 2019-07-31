
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

/**
 * Acceso a datos para la entidad Item productivo
 * @author rincostante
 */
@Stateless
public class ItemProductivoFacade extends AbstractFacade<ItemProductivo> {

    /**
     * Constructor
     */
    public ItemProductivoFacade() {
        super(ItemProductivo.class);
    }
    
    /**
     * Metodo para validar un Item productivo existente según el código del producto.
     * Debe recordarse que el código de producto es único.
     * @param codigoProducto String código del producto
     * @return ItemProductivo item correspondiente al producto remitido
     */
    public ItemProductivo getExistente(String codigoProducto) {
        List<ItemProductivo> lstItems;
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.codigoProducto = :codigoProducto";
        Query q = em.createQuery(queryString)
                .setParameter("codigoProducto", codigoProducto);
        lstItems = q.getResultList();
        if(lstItems.isEmpty()){
            return null;
        }else{
            return lstItems.get(0);
        }
    }   
    
    /**
     * Método que devuelve los items según la Autorización enviada, todos los estados
     * @param aut Autorizacion autorización que contiene los ítems
     * @return List<ItemProductivo> listado de los ítems de una Autorización
     */
    public List<ItemProductivo> getByAutorizacion(Autorizacion aut){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.autorizacion = :aut";
        Query q = em.createQuery(queryString)
                .setParameter("aut", aut);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve los items según la Guía enviada, todos los estados
     * @param guia Guia guía que contiene a los ítems
     * @return List<ItemProductivo> listado de los ítems de la guía
     */
    public List<ItemProductivo> getByGuia(Guia guia){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.guia = :guia";
        Query q = em.createQuery(queryString)
                .setParameter("guia", guia);
        return q.getResultList();
    }   
    
    /**
     * Método que devuelve los items correspondientes a una Guía según su id.
     * Dado que una guía puede tener vinculados distintos items con un mismo producto,
     * cuando haya descontado un producto de guías madre distintas, 
     * el método devuelve los ítems agrupados.
     * Para el servicio.
     * @param idGuia Long identificador único de la guía
     * @return List<ItemProductivo> listado de los items agrupados habilitados de la guía
     */
    public List<ItemProductivo> getByIdGuia(Long idGuia){
        // obtengo el total de items de la guía y los guardo en un listado
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.guia.id = :idGuia "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("idGuia", idGuia);
        List<ItemProductivo> lstItems = q.getResultList();
        // seteo el listado de agrupados para luego comparar
        List<ItemProductivo> itemsAgrupados = new ArrayList<>();
        for (ItemProductivo item : lstItems){
            boolean existe = false;
            // recorro el listado de agrupados
            for (ItemProductivo itemAgr : itemsAgrupados){
                if(Objects.equals(itemAgr.getIdProd(), item.getIdProd())){
                    existe = true;
                }
            }
            if(!existe){
                itemsAgrupados.add(item);
            }
        }
        // comparo la cantidad de items entre los listados
        if(itemsAgrupados.size() != lstItems.size()){
            // si hay diferencia vuelvo a recorrer el listado original para hacer la agrupación
            for (ItemProductivo item : lstItems){
                // por cada item verifico si ya está registrado entre los agrupados
                for (ItemProductivo itemAgr : itemsAgrupados){
                    // si ya está registrado el producto entre los agrupados sumo los totales (total, totalKg)
                    if(Objects.equals(itemAgr.getIdProd(), item.getIdProd()) && !Objects.equals(itemAgr.getId(), item.getId())){
                        float total = itemAgr.getTotal();
                        float totalKg = itemAgr.getTotalKg();
                        // actualizo total
                        itemAgr.setTotal(total + item.getTotal());
                        // actualizo el totalKg
                        itemAgr.setTotalKg(totalKg + item.getTotalKg());
                    }
                }
            } 
            return itemsAgrupados;
        }else{
            return lstItems;
        }
    }       
    
    /**
     * Método que devuelve los items habiliados según la Autorización enviada
     * @param aut Autorizacion autorización cuyos ítems se desea obtener
     * @return List<ItemProductivo> listado de ítems habilitados de la autorización remitida
     */
    public List<ItemProductivo> getByAutorizacionHabilitados(Autorizacion aut){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.autorizacion = :aut "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("aut", aut);
        return q.getResultList();
    }    
    
    /**
     * Método que devuelve los items habiliados según la Guía enviada
     * @param guia Guia Guía cuyos ítems se desa obtener
     * @return List<ItemProductivo> listado de los ítmes habilitados de la guía
     */
    public List<ItemProductivo> getByGuiaHabilitados(Guia guia){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.guia = :guia "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("guia", guia);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve todos los ítems habilitados autorizados
     * @return List<ItemProductivo> listado de todos los ítems habilitados autorizados
     */
    public List<ItemProductivo> getAutorizadosHabilitados(){
        String autorizados = ResourceBundle.getBundle("/Config").getString("Autorizados");
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.tipoActual.nombre = :autorizados "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("autorizados", autorizados);
        return q.getResultList();
    }
    
    /**
     * Método que devuelve todos los ítems extraídos habilitados
     * @return List<ItemProductivo> listado de todos los ítems extraídos autorizados
     */
    public List<ItemProductivo> getExtraidosHabilitados(){
        String extraidos = ResourceBundle.getBundle("/Config").getString("Extraidos");
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.tipoActual.nombre = :extraidos "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("extraidos", extraidos);
        return q.getResultList();
    }
    
    /**
     * Método para obtener los productos removidos durante un período determinado, 
     * según la autorización que sirvió de fuente
     * @param inicio Date fecha límite de inicio del período dentro del cual se emitieron las guías que transportaron los productos
     * @param fin Date fecha límite de fin del período dentro del cual se emitieron las guías que transportaron los productos
     * @return List<Object[]> matriz con los datos obtenidos
     */
    public List<Object[]> getExtraidosSegunAut(Date inicio, Date fin){
        String queryString = "SELECT g.numfuente, item.nombrevulgar, item.clase, item.unidad, SUM(item.saldo) AS total "
                + "FROM itemproductivo item "
                + "INNER JOIN guia g ON g.id = item.guia_id "
                + "INNER JOIN tipoguia tipo ON tipo.id = g.tipo_id "
                + "INNER JOIN entidadguia entguia ON g.origen_id = entguia.id "
                + "INNER JOIN estadoguia estguia ON estguia.id = g.estado_id "
                + "WHERE tipo.habilitatransp = true "
                + "AND g.fechaemisionguia >= ?1 "
                + "AND g.fechaemisionguia <= ?2 "
                + "AND (estguia.nombre = 'EMITIDA' OR estguia.nombre = 'CERRADA') "
                + "GROUP BY g.numfuente, item.nombrevulgar, item.clase, item.unidad "
                + "ORDER BY g.numfuente, item.nombrevulgar";
        Query q = em.createNativeQuery(queryString)
                .setParameter(1, inicio)
                .setParameter(2, fin);
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param codigoProducto String Código del producto del Item del que se quieren ver sus revisiones
     * @return List<ItemProductivo> listado de todas las revisiones de un ítem para su auditoría
     */
    public List<ItemProductivo> findRevisions(String codigoProducto){  
        List<ItemProductivo> lstProductos = new ArrayList<>();
        ItemProductivo item = this.getExistente(codigoProducto);
        if(item != null){
            Long id = this.getExistente(codigoProducto).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(ItemProductivo.class, id);
            for (Number n : revisions) {
                ItemProductivo cli = reader.find(ItemProductivo.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getTipoActual());
                Hibernate.initialize(cli.getTipoOrigen());
                Hibernate.initialize(cli.getAutorizacion());
                lstProductos.add(cli);
            }
        }
        return lstProductos;
    }      
}
