
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoClaseFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoEspecieLocalFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoUnidadMedidaFacade;
import ar.gob.ambiente.sacvefor.servicios.cglsicma.Producto;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Producto
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("productos")
public class ProductoFacadeREST {
    
    @EJB
    private ProductoFacade prodFacade;
    @EJB
    private ProductoUnidadMedidaFacade unidadFacade;
    @EJB
    private ProductoClaseFacade claseFacade;
    @EJB
    private ProductoEspecieLocalFacade especieFacade;
    
    /**
     * @api {get} /productos/query?nom_cientifico=:nombre_cientifico&clase=:clase&unidad=:unidad Obtiene un Producto según el nombre científico de la especie, la clase y la unidad de medida
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/gestionLocal/rest/entidadesguia/query?nom_cientifico=Zanthoxylum coco&clase=rollo&unidad=metro cubico -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetProductoQuery
     * @apiGroup Producto
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} nom_cientifico Nombre científico de la especie del producto.
     * @apiParam {String} clase Clase mediante la que se comercializa el producto.
     * @apiParam {String} unidad Unidad de medida en la que se comercializa la clase del producto
     * @apiDescription Método para obtener un producto a partir del nombre científico de la Especie, la clase en la que se comercializa y la unidad de medida.
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cglsicma.Producto} Producto producto que corresponda a los parámetros remitidos.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *          "producto": {
     *              "clase":"ROLLO",
     *              "id":"14",
     *              "id_esp_tax":"3115",
     *              "kg_x_unidad":"1350"
     *              "nom_cientifico":"Zanthoxylum coco",
     *              "nom_vulgar":"COCHUCHO-SAUCO",
     *              "unidad":"METRO CUBICO"
     *          }
     * @apiError ProductoNotFound No existe producto registrado con esos parámetros.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay entidad producto registrado con los parámetros recibidos"
     *     }
     */     
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Producto findByQuery(@QueryParam("nom_cientifico") String nom_cientifico, 
            @QueryParam("clase") String clase, 
            @QueryParam("unidad") String unidad){
        
        ProductoClase claseCgl;
        ProductoEspecieLocal especieCgl;
        ar.gob.ambiente.sacvefor.localcompleto.entities.Producto prodCgl;
        
        // obtengo la unidad de medida a partir de su nombre
        ProductoUnidadMedida unidadCgl = unidadFacade.getExistenteByNombre(unidad.toUpperCase());
        // obtengo la clase a partir de la unidad de medida y el nombre (si no hubo error)
        if(unidadCgl != null){
            claseCgl = claseFacade.getExistente(clase.toUpperCase(), unidadCgl);
        }else{
            claseCgl = null;
        }

        // obtengo la especie local (si no hubo error)
        if(claseCgl != null){
            especieCgl = especieFacade.getExistenteXNomCientifico(nom_cientifico);
        }else{
            especieCgl = null;
        }
        
        // obtengo el producto (si no hubo error)
        if(especieCgl != null ){
            prodCgl = prodFacade.getExistente(claseCgl, especieCgl);
        }else{
            prodCgl = null;
        }
        
        // seteo el producto a serializar si obtuve el producto del CGL
        if(prodCgl != null && claseCgl != null && especieCgl != null){
            Producto prod = new Producto();
            prod.setClase(clase.toUpperCase());
            prod.setId(prodCgl.getId());
            prod.setNom_cientifico(nom_cientifico);
            prod.setNom_vulgar(especieCgl.getNombreVulgar().toUpperCase());
            prod.setUnidad(unidad.toUpperCase());
            prod.setId_esp_tax(especieCgl.getIdTax());
            prod.setKg_x_unidad(prodCgl.getEquivalKg());
            return prod; 
        }else{
            return null;
        }
    }
}
