
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoSubClase;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para la SubClase
 * @author rincostante
 */
@Stateless
public class ProductoSubClaseFacade extends AbstractFacade<ProductoSubClase> {

    public ProductoSubClaseFacade() {
        super(ProductoSubClase.class);
    }
    
    /**
     * Método para obtener una subclase existente para una clase principal, de una clase derivada.
     * Para el servicio de la API para TRAZ
     * @param id_clase_principal Long identificador de la Clase principal
     * @param id_clase_derivada Long identificador de la Clase derivada
     * @return ProductoSubClase subclase requerida
     */
    public ProductoSubClase getSubClaseByIdPrincipalAndIdDerivada(Long id_clase_principal, Long id_clase_derivada){
        List<ProductoSubClase> lstSubClases;
        String queryString = "SELECT subclase FROM ProductoSubClase subclase "
                + "WHERE subclase.clasePrincipal.id = :id_clase_principal "
                + "AND subclase.claseDerivada.id = :id_clase_derivada";
        Query q = em.createQuery(queryString)
                .setParameter("id_clase_principal", id_clase_principal)
                .setParameter("id_clase_derivada", id_clase_derivada);
        lstSubClases = q.getResultList();
        if(lstSubClases.isEmpty()){
            return null;
        }else{
            return lstSubClases.get(0);
        }
    }
}
