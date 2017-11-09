
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import java.util.ArrayList;
import java.util.List;
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

    public ItemProductivoFacade() {
        super(ItemProductivo.class);
    }
    
    /**
     * Metodo para validar un Item productivo existente según el código del producto
     * @param codigoProducto
     * @return 
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
     * Devuelve los items según la Autorización enviada, todos los estados
     * @param aut
     * @return 
     */
    public List<ItemProductivo> getByAutorizacion(Autorizacion aut){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.autorizacion = :aut";
        Query q = em.createQuery(queryString)
                .setParameter("aut", aut);
        return q.getResultList();
    }
    
    /**
     * Devuelve los items según la Guía enviada, todos los estados
     * @param guia
     * @return 
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
     * Para el servicio.
     * @param idGuia
     * @return 
     */
    public List<ItemProductivo> getByIdGuia(Long idGuia){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.guia.id = :idGuia "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("idGuia", idGuia);
        return q.getResultList();
    }       
    
    /**
     * Devuelve los items habiliados según la Autorización enviada
     * @param aut
     * @return 
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
     * Devuelve los items habiliados según la Guía enviada
     * @param guia
     * @return 
     */
    public List<ItemProductivo> getByGuiaHabilitados(Guia guia){
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.guia = :guia "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("guia", guia);
        return q.getResultList();
    }
    
    public List<ItemProductivo> getAutorizadosHabilitados(){
        String autorizados = ResourceBundle.getBundle("/Config").getString("Autorizados");
        String queryString = "SELECT item FROM ItemProductivo item "
                + "WHERE item.tipoActual.nombre = :autorizados "
                + "AND item.habilitado = true";
        Query q = em.createQuery(queryString)
                .setParameter("autorizados", autorizados);
        return q.getResultList();
    }
    
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
     * Método para obtener todas las revisiones de la entidad
     * @param codigoProducto : Código del producto del Item del que se quieren ver sus revisiones
     * @return 
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
