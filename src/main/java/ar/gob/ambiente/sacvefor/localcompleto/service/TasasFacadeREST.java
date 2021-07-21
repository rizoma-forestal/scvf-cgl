/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Servicio que publica las tasas configuradas en el CGL para ser obtenidas por TRAZ
 * para la configuración de los tipos de guía
 * @author rincostante
 */
@Stateless
@Path("tasas")
public class TasasFacadeREST {
 
    @EJB
    private ParametricaFacade paramFacade;

    @EJB
    private TipoParamFacade tipoParamFacade;

    /**
     * @api {get} /tasas Ver todas las tasas configuradas (Paramétricas)
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/tasas -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetTasas
     * @apiGroup Tasa
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de las tasas configuradas.
     * Obtiene las paramétricas de tipo TIPO_TASA mediante el método getExistente([nombre_tipoParam]) del facade correspondiente
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Parametrica} Paramétrica Listado con todas las tasas configuradas.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "tasas": [
     *              {
     *                  "id": "1",
     *                  "nombre": "RENTAS",
     *                  "tipo":{
     *                              "id":"2",
     *                              "nombre":"TIPO_TASA"
     *                          },
     *                  "habilitado":true
     *              },
     *              {
     *                  "id": "2",
     *                  "nombre": "BOSQUES",
     *                  "tipo":{
     *                              "id":"2",
     *                              "nombre":"TIPO_TASA"
     *                          },
     *                  "habilitado":true
     *              }
     *       ]
     *     }
     * @apiError TasasNotFound No existen tasas habilitadas registradas.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay Tasas habilitadas registradas"
     *     }
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public List<Parametrica> findAll() {
        TipoParam tipo = tipoParamFacade.getExistente("TIPO_TASA");
        return paramFacade.getHabilitadas(tipo);
    }
}
