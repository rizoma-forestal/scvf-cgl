
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para las Especies locales de Productos
 * @author rincostante
 */
@Stateless
public class ProductoEspecieLocalFacade extends AbstractFacade<ProductoEspecieLocal> {

    /**
     * Constructor
     */
    public ProductoEspecieLocalFacade() {
        super(ProductoEspecieLocal.class);
    }
    
    /**
     * Método para validar la existencia de una Especie según su nombre vulgar
     * @param nombreVulgar String Nombre vulgar a validar
     * @return ProductoEspecieLocal especie local con el nombre vulgar remitido
     */
    public ProductoEspecieLocal getExistenteXNomVulgar(String nombreVulgar) {
        List<ProductoEspecieLocal> lstEspecies;
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "WHERE especie.nombreVulgar = :nombreVulgar";
        Query q = em.createQuery(queryString)
                .setParameter("nombreVulgar", nombreVulgar);
        lstEspecies = q.getResultList();
        if(lstEspecies.isEmpty()){
            return null;
        }else{
            return lstEspecies.get(0);
        }
    }   
    
    /**
     * Método para validar la existencia de una Especie según su nombre científico
     * @param nombreCientifico String nombre científico de la especie
     * @return ProductoEspecieLocal especie local con el nombre vulgar remitido
     */
    public ProductoEspecieLocal getExistenteXNomCientifico(String nombreCientifico){
        List<ProductoEspecieLocal> lstEspecies;
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "WHERE especie.nombreCientifico = :nombreCientifico";
        Query q = em.createQuery(queryString)
                .setParameter("nombreCientifico", nombreCientifico);
        lstEspecies = q.getResultList();
        if(lstEspecies.isEmpty()){
            return null;
        }else{
            return lstEspecies.get(0);
        }
    }
    
    /**
     * Método sobreescrito que lista las ProductoEspecieLocal ordenadas por nombre
     * @return List<ProductoEspecieLocal> listado de especies locales ordenadas por nombre vulgar
     */
    @Override
    public List<ProductoEspecieLocal> findAll(){
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "ORDER BY especie.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }      
    
    /**
     * Mátodo que solo devuelve las ProductoEspecieLocal habilitadas.
     * Para poblar combos de selección.
     * @return List<ProductoEspecieLocal> listado de las especies locales habilitadas
     */
    public List<ProductoEspecieLocal> getHabilitadas(){
        String queryString = "SELECT especie FROM ProductoEspecieLocal especie "
                + "WHERE especie.habilitado = true "
                + "ORDER BY especie.nombreVulgar";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
}
