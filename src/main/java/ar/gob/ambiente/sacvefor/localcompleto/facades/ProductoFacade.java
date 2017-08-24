
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
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
 * Acceso a datos de los Productos que serán autorizados para la extracción
 * @author rincostante
 */
@Stateless
public class ProductoFacade extends AbstractFacade<Producto> {

    @PersistenceContext(unitName = "sacvefor-gestionLocalCompletoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoFacade() {
        super(Producto.class);
    }
    
    /**
     * Método para validar la existencia de un Producto según su Clase y Especie local
     * @param clase : Clase cuya existencia se va a validar junto con la Especie
     * @param especieLocal : Especie cuya existencia se va a validar junto con la Clase
     * @return 
     */
    public Producto getExistente(ProductoClase clase, ProductoEspecieLocal especieLocal) {
        List<Producto> lstProductos;
        em = getEntityManager();
        
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.clase = :clase "
                + "AND prod.especieLocal = :especieLocal";
        Query q = em.createQuery(queryString)
                .setParameter("clase", clase)
                .setParameter("especieLocal", especieLocal);
        lstProductos = q.getResultList();
        if(lstProductos.isEmpty()){
            return null;
        }else{
            return lstProductos.get(0);
        }
    }      
    
    /**
     * Método sobreescrito que lista los Productos ordenadas por el nombre vulgar de la Especie
     * @return 
     */
    @Override
    public List<Producto> findAll(){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "ORDER BY prod.especieLocal.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve los Productos habilitados.
     * Para poblar combos de selección.
     * @return 
     */
    public List<Producto> getHabilitados(){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.habilitado = true "
                + "ORDER BY prod.especieLocal.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }  
    
    public List<Producto> getByEspecieLocal(ProductoEspecieLocal especieLocal){
        em = getEntityManager();
        String queryString = "SELECT prod FROM Producto prod "
                + "WHERE prod.habilitado = true "
                + "AND prod.especieLocal = :especieLocal "
                + "ORDER BY prod.especieLocal.nombreVulgar";
        Query q = em.createQuery(queryString)
                .setParameter("especieLocal", especieLocal);;
        return q.getResultList();
    }
    
    /**
     * Método para obtener todas las revisiones de la entidad
     * @param clase : Clase que junto a la Especie definen el Producto cuyas revisiones se busca
     * @param especieLocal : Especie que junto a la Clase definen el Producto cuyas revisiones se busca
     * @return 
     */
    public List<Producto> findRevisions(ProductoClase clase, ProductoEspecieLocal especieLocal){  
        List<Producto> lstProductos = new ArrayList<>();
        Producto prod = this.getExistente(clase, especieLocal);
        if(prod != null){
            Long id = this.getExistente(clase, especieLocal).getId();
            AuditReader reader = AuditReaderFactory.get(getEntityManager());
            List<Number> revisions = reader.getRevisions(Producto.class, id);
            for (Number n : revisions) {
                Producto cli = reader.find(Producto.class, id, n);
                cli.setFechaRevision(reader.getRevisionDate(n));
                Hibernate.initialize(cli.getClase());
                Hibernate.initialize(cli.getEspecieLocal());
                lstProductos.add(cli);
            }
        }
        return lstProductos;
    }          
}
