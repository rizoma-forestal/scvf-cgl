
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoTasa;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para las Tasas a abonar de Productos
 * @author rincostante
 */
@Stateless
public class ProductoTasaFacade extends AbstractFacade<ProductoTasa> {

    public ProductoTasaFacade() {
        super(ProductoTasa.class);
    }
    
    /**
     * Metodo para validar un ProductoTasa existente según su nombre para un tipo determinado
     * @param nombre : Nombre a validar
     * @param tipo : Tipo de Tasa a validar junto con el CUIT
     * @return 
     */
    public ProductoTasa getExistente(String nombre, Parametrica tipo) {
        List<ProductoTasa> lstTasas;
        String queryString = "SELECT tasa FROM ProductoTasa tasa "
                + "WHERE tasa.nombre = :nombre "
                + "AND tasa.tipo = :tipo";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("tipo", tipo);
        lstTasas = q.getResultList();
        if(lstTasas.isEmpty()){
            return null;
        }else{
            return lstTasas.get(0);
        }
    }     
    
    /**
     * Método sobreescrito que lista las ProductoTasa ordenados por nombre
     * @return 
     */
    @Override
    public List<ProductoTasa> findAll(){
        String queryString = "SELECT tasa FROM ProductoTasa tasa "
                + "ORDER BY tasa.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }
    
    /**
     * Mátodo que solo devuelve los ProductoTasa habilitados.
     * Para poblar combos de selección.
     * @return 
     */
    public List<ProductoTasa> getHabilitados(){
        String queryString = "SELECT tasa FROM ProductoTasa tasa "
                + "WHERE tasa.habilitado = true "
                + "ORDER BY tasa.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }         
}
