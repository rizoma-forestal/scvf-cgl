
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
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
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Vehiculo
 * @author rincostante
 */
@Stateless
@Path("vehiculos")
public class VehiculoFacadeREST {

    @EJB
    private VehiculoFacade vehiculoFacade;

    /**
     * @api {get} /vehiculos/:id Ver un Vehículo según su id
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/vehiculos/3 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetVehiculo
     * @apiGroup Vehiculo
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único del Vehiculo
     * @apiDescription Método para obtener un Vehiculo existente según el id remitido.
     * Obtiene el vehículo mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Vehiculo} Vehiculo Detalle del vehículo registrado.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *                  "vehiculo": {
     *                      "id": "8",
     *                      "matricula": "ABC-128",
     *                      "idRue": "10",
     *                      "marca": "LA MODERNA",
     *                      "modelo": "CHATA",
     *                      "anio": "1971",
     *                      "titular": {
     *                          "id": "20",
     *                          "idRue": "26",
     *                          "nombreCompleto": "TRANSPORTES JURANGO SRL",
     *                          "cuit": "20213349881",
     *                          "email": "transportista@algo.com",
     *                          "tipo": ""Persona Jurídica""
     *                      }
     *                  }
     *     }
     * @apiError VehiculoNotFound No existe vehículo registrado con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay vehículo registrado con el id recibido"
     *     }
     */      
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Vehiculo find(@PathParam("id") Long id) {
        return vehiculoFacade.find(id);
    }

    /**
     * @api {get} /vehiculos/query?mat=:mat Ver Vehículos según su matrícula.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/vehiculos/query?mat=ABC-128 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetVehiculosQuery
     * @apiGroup Vehiculo
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} matrícula del Vehiculo solicitado
     * @apiDescription Método para obtener un vehículo según su matrícula.
     * Obtiene el vehículo con el método local getExistente(String mat)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Vehiculo} Vehiculo vehículo obtenido.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *                  "vehiculo": {
     *                      "id": "8",
     *                      "matricula": "ABC-128",
     *                      "idRue": "10",
     *                      "marca": "LA MODERNA",
     *                      "modelo": "CHATA",
     *                      "anio": "1971",
     *                      "titular": {
     *                          "id": "20",
     *                          "idRue": "26",
     *                          "nombreCompleto": "TRANSPORTES JURANGO SRL",
     *                          "cuit": "20213349881",
     *                          "email": "transportista@algo.com",
     *                          "tipo": ""Persona Jurídica""
     *                      }
     *                  }
     * @apiError VehiculoNotFound No existe estado Vehiculo registrado con ese nombre.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay Vehiculo registrado con con ese nombre"
     *     }
     */      
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Vehiculo findByMatricula(@QueryParam("mat") String mat) {
        Vehiculo result = new Vehiculo();
        if(mat != null){
            result = vehiculoFacade.getExistente(mat);
        }
        return result;
    }       

    /**
     * @api {get} /vehiculos Ver todos los vehiculos
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/vehiculos -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetVehiculos
     * @apiGroup Vehiculo
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de los vehículos existentes.
     * Obtiene los estados de guías mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Vehiculo} Vehiculo Listado con todos los vehículos registrados.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "estadosguia": [
     *                  "vehiculo": {
     *                      "id": "8",
     *                      "matricula": "ABC-128",
     *                      "idRue": "10",
     *                      "marca": "LA MODERNA",
     *                      "modelo": "CHATA",
     *                      "anio": "1971",
     *                      "titular": {
     *                          "id": "20",
     *                          "idRue": "26",
     *                          "nombreCompleto": "TRANSPORTES JURANGO SRL",
     *                          "cuit": "20218889881",
     *                          "email": "transportista@algo.com",
     *                          "tipo": ""Persona Jurídica""
     *                      }
     *                  },{
     *                      "id": "21",
     *                      "matricula": "ABC-128",
     *                      "idRue": "10",
     *                      "marca": "DORITA",
     *                      "modelo": "CHATA",
     *                      "anio": "1971",
     *                      "titular": {
     *                          "id": "20",
     *                          "idRue": "23",
     *                          "nombreCompleto": "TRANSPORTES SALDAÑO SRL",
     *                          "cuit": "20213339881",
     *                          "email": "transportista@algo.com",
     *                          "tipo": ""Persona Jurídica""
     *                      }
     *                  },{
     *                      "id": "63",
     *                      "matricula": "ABC-126",
     *                      "idRue": "8",
     *                      "marca": "VAQUEANO",
     *                      "modelo": "DI SALVO LANDIVIA",
     *                      "anio": "1973",
     *                      "titular": {
     *                          "id": "37",
     *                          "idRue": "206",
     *                          "nombreCompleto": "TRANSPORTES JURANGO SRL",
     *                          "cuit": "20236949881",
     *                          "email": "transportista@algo.com",
     *                          "tipo": ""Persona Jurídica""
     *                      }
     *                  }
     * 
     *       ]
     *     }
     * @apiError VehiculosNotFound No existen estados de guías registrados.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay Vehiculos registrados"
     *     }
     */
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Vehiculo> findAll() {
        return vehiculoFacade.findAll();
    }
 
    @GET
    @Path("{from}/{to}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Vehiculo> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return vehiculoFacade.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(vehiculoFacade.count());
    }
}
