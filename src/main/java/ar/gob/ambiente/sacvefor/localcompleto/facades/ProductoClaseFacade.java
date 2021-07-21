
package ar.gob.ambiente.sacvefor.localcompleto.facades;

import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 * Acceso a datos para las Clases de Productos
 * @author rincostante
 */
@Stateless
public class ProductoClaseFacade extends AbstractFacade<ProductoClase> {

    /**
     * Constructor
     */
    public ProductoClaseFacade() {
        super(ProductoClase.class);
    }
    
    /**
     * Método para validar la existencia de una Clase según su nombre y unidad de medida
     * @param nombre String Nombre a validar junto con la unidad de medida
     * @param unidad ProductoUnidadMedida Unidad de medida a validar junto con el nombre
     * @return ProductoClase clase de producto existente
     */
    public ProductoClase getExistente(String nombre, ProductoUnidadMedida unidad) {
        List<ProductoClase> lstClases;
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.nombre = :nombre "
                + "AND clase.unidad = :unidad";
        Query q = em.createQuery(queryString)
                .setParameter("nombre", nombre)
                .setParameter("unidad", unidad);
        lstClases = q.getResultList();
        if(lstClases.isEmpty()){
            return null;
        }else{
            return lstClases.get(0);
        }
    }       
    
    /**
     * Método sobreescrito que lista las ProductoClase ordenadas por nombre
     * @return List<ProductoClase> listado de todas las clases ordenadas por nombre
     */
    @Override
    public List<ProductoClase> findAll(){
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }       
    
    /**
     * Mátodo que solo devuelve las ProductoClase habilitadas.
     * Para poblar combos de selección.
     * @return List<ProductoClase> listado de todas las Clases habilitadas
     */
    public List<ProductoClase> getHabilitadas(){
        String queryString = "SELECT clase FROM ProductoClase clase "
                + "WHERE clase.habilitado = true "
                + "ORDER BY clase.nombre";
        Query q = em.createQuery(queryString);
        return q.getResultList();
    }        
    
    /**
     * Método que obtiene todas las clases habiltiadas cuyo nivelTransformacion
     * es uno menos que el recibido.
     * Valida que el nivel recibido sea 1 o dos, si no retorna null
     * @param nivelTransformacion int nivel de transformación de la clase que busca origenes
     * @return List<ProductoClase> listado con las Clases solicitadas
     */
    public List<ProductoClase> getClasesOrigen(int nivelTransformacion){
        List<ProductoClase> lstClases = new ArrayList<>();
        if(nivelTransformacion == 1 || nivelTransformacion == 2){
            nivelTransformacion = nivelTransformacion - 1;
            String queryString = "SELECT clase FROM ProductoClase clase "
                    + "WHERE clase.habilitado = true "
                    + "AND clase.nivelTransformacion = :nivelTransformacion "
                    + "ORDER BY clase.nombre";
            Query q = em.createQuery(queryString)
                    .setParameter("nivelTransformacion", nivelTransformacion);
            return q.getResultList();
        }else{
            return lstClases;
        }
    }
}
