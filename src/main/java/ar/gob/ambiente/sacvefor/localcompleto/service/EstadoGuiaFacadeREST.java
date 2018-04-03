
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad EstadoGuia
 * @author rincostante
 */
@Stateless
@Path("estadosguia")
public class EstadoGuiaFacadeREST {

    @EJB
    private EstadoGuiaFacade estadoFacade;

    /**
     * @api {get} /estadosguia/:id Ver un Estado de guía según su id
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/estadosguia/3 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEstadoGuia
     * @apiGroup EstadoGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único del EstadoGuia
     * @apiDescription Método para obtener un EstadoGuia existente según el id remitido.
     * Obtiene el estado de guía mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia} EstadoGuia Detalle del estado de guía registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *          {"id": "3",
     *          "nombre": "EN TRANSITO"}
     *     }
     * @apiError EstadoGuiaNotFound No existe estado de guía registrado con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay estado de guía registrado con el id recibido"
     *     }
     */        
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EstadoGuia find(@PathParam("id") Long id) {
        return estadoFacade.find(id);
    }

    /**
     * @api {get} /estadosguia Ver todos los EstadoGuia
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/estadosguia -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEstadoGuia
     * @apiGroup EstadoGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de los estados de guías existentes.
     * Obtiene los estados de guías mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia} EstadoGuia Listado con todos los estados de guías registrados.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "estadosguia": [
     *          {"id": "1",
     *          "nombre": "CARGA INICIAL"},
     *          {"id": "2",
     *          "nombre": "CERRADA"},
     *          {"id": "3",
     *          "nombre": "EN TRANSITO"}
     *       ]
     *     }
     * @apiError EstadosGuiaNotFound No existen estados de guías registrados.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay estados de guías registrados"
     *     }
     */
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findAll() {
        return estadoFacade.getHabilitados();
    }

    /**
     * @api {get} /estadosguia/query?nombre=:nombre Ver Estados de guías según su nombre.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/estadosguia/query?nombre=CERRADA -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEstadoGuiaQuery
     * @apiGroup EstadoGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} nombre nombre de la Parametrica solicitada
     * @apiDescription Método para obtener la  paramétrica según su nombre.
     * Obtiene la paramétrica con el método local getExistente(String nombre)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.EstadoGuia} EstadoGuia estado de la guía obtenida.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "estadoguia": 
     *          {"id": "3",
     *          "nombre": "EN TRANSITO"}
     *     }
     * @apiError EstadoGuiaNotFound No existe estado de guía registrado con ese nombre.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay estado de guía registrado con con ese nombre"
     *     }
     */     
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findByQuery(@QueryParam("nombre") String nombre) {
        List<EstadoGuia> result = new ArrayList<>();
        if(nombre != null){
            EstadoGuia estado = estadoFacade.getExistente(nombre);
            result.add(estado);
        }
        return result;
    }       

    @GET
    @Path("{from}/{to}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return estadoFacade.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Secured
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(estadoFacade.count());
    }
}
