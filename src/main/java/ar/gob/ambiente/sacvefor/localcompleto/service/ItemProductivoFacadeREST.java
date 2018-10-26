
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import java.net.URI;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad ItemProductivo
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("itemsprod")
public class ItemProductivoFacadeREST {
    
    @EJB
    private GuiaFacade guiaFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;    
    @EJB
    private ParametricaFacade parmaFacade;
    @EJB
    private UsuarioFacade usFacade;
    @EJB
    private ItemProductivoFacade itemFacade;    
    @Context
    UriInfo uriInfo;      
    
    /**
     * @api {post} /itemsprod Registrar un Item productivo
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/gestionLocal/rest/itemsprod -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.ItemProductivo} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos del ítem a registrar
     * @apiParamExample {java} Ejemplo de ItemProductivo
     *      {"entity":{
     *              "id_usuario":"1",
     *              "nom_cientif":"Zanthoxylum coco",
     *              "nom_vulgar":"COCHUCHO-SAUCO",
     *              "id_prod": "14",
     *              "clase":"ROLLO",
     *              "unidad":"METRO CUBICO",
     *              "id_esp_tax":"3079",
     *              "id_guia":"15",
     *              "kg_x_unidad":"1000",
     *              "total":"23",
     *              "total_kg":"23000",
     *              "cod_traz":"14|Zanthoxylum coco|COCHUCHO-SAUCO|ROLLO|METRO CUBICO|364/2017/PRLE|CHACO"
     *          }
     *      }
     * @apiDescription Método para registrar un ItemProductivo a una Guía registrada de forma remota.
     * Setea todos los parámetros directos, y obtiene los bojetos mediante sus id: Guía, TipoItem y Usuario.
     * @apiSuccess {String} Location url de acceso mediante GET al ItemProductivo registrado.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/gestionLocal/rest/itemsprod/:id"
     *       }
     *     }
     *
     * @apiError ItemNoRegistrado No se registró el ItemProductivo.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesado la inserción del ItemProductivo en el CGL"
     *     }
     */          
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.cglsicma.ItemProductivo entity) {
        // instancio el item local
        ItemProductivo itemLocal = new ItemProductivo();
        try{
            itemLocal.setNombreCientifico(entity.getNom_cientif());
            itemLocal.setNombreVulgar(entity.getNom_vulgar());
            itemLocal.setClase(entity.getClase());
            itemLocal.setUnidad(entity.getUnidad());
            itemLocal.setIdEspecieTax(entity.getId_esp_tax());
            itemLocal.setIdProd(entity.getId_prod());
            itemLocal.setKilosXUnidad(entity.getKg_x_unidad());
            itemLocal.setTotal(entity.getTotal());
            itemLocal.setTotalKg(entity.getTotal_kg());
            itemLocal.setCodigoProducto(entity.getCod_traz());
            // seteo la guía
            itemLocal.setGuia(guiaFacade.find(entity.getId_guia()));
            // instancio y seteo el tipo de item
            TipoParam tipoParam = tipoParamFacade.getExistente("TIPO_ITEM");
            itemLocal.setTipoActual(parmaFacade.getExistente("EXTRAIDOS", tipoParam));
            // seteo los saldos
            itemLocal.setSaldo(entity.getTotal());
            itemLocal.setSaldoKg(entity.getTotal_kg());
            // seteo observaciones y habilitado
            itemLocal.setObs("Item agregado a una Guía registrada desde la API de integración SICMA-SACVeFor");
            itemLocal.setHabilitado(true);
            // seteo datos de registro
            itemLocal.setUsuario(usFacade.find(entity.getId_usuario()));
            itemLocal.setFechaAlta(new Date(System.currentTimeMillis()));
            // inserto el ítem productivo
            itemFacade.create(itemLocal);
            // armo la respuesta exitosa
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            URI uri = uriBuilder.path(itemLocal.getId().toString()).build();
            return Response.created(uri).build();  
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción del Item Productivo en el CGL. " + ex.getMessage()).build();
        }      
    }
}
