
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Localidad
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("localidades")
public class LocalidadFacadeREST {
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del TERR
     */
    private String strToken; 
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del TERR
     */       
    private Token token;   
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(PersonaFacadeREST.class);       
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en el TERR
     */
    private UsuarioClient usClientTerr;   
    /**
     * Variable privada: Cleinte para la API Rest de Provincias
     */
    private ProvinciaClient provClient;
    /**
     * Variable privada: Cleinte para la API Rest de Departamentos
     */
    private DepartamentoClient deptoClient;
    /**
     * Variable privada: Cleinte para la API Rest de Localidades
     */
    private LocalidadClient localidadClient;
    
    /**
     * @api {get} /localidades/query?nom_prov=:nom_prov&nom_depto=:nom_depto&nom_loc=:nom_loc Ver Localidades según su nombre, 
     * el del Departamento y el de la Provincia.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/localidades/query?nom_prov=CHACO&nom_depto=LIBERTAD&nom_loc=PUERTO TIROL -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetLocalidadesQuery
     * @apiGroup Localidad
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} nom_prov nombre de la Provincia que incluye la Localidad solicitada
     * @apiParam {String} nom_depto nombre del Departamento que incluye la Localidad solicitada
     * @apiParam {String} nom_loc nombre de la Localidad solicitada 
     * @apiDescription Método para obtener una Localidad según su nombre, el del Departamento y el de la Provincia que la contienen.
     * Obtiene la Provincia a partir del nombre, luego el Departamento a partir de su nombre y el id de la Provincia y,
     * finalmente, la Localidad a partir de su nombre y el id del Departamento que la contien.
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado} CentroPoblado localidad obtenida.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *              {
     *                  "id":"1137",
     *                  "nombre":"TACUARI",
     *                  "centropobladotipo":{
     *                              "id":"9",
     *                              "nombre":"PARAJE"
     *                          }
     *                  "departamento":{
     *                      "id":"172",
     *                      "nombre":"BERMEJO",
     *                          "provincia":{
     *                                  "id":"6",
     *                                  "nombre":"CHACO"
     *                              }
     *                      }
     *              }
     * @apiError PersonaNotFound No existe Localidad registrada con ese nombre para el Departamento y Provincia solicitados.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay Localidad registrada con con ese nombre para el Departamento y Provincia solicitados."
     *     }
     */        
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CentroPoblado findByQuery(@QueryParam("nom_prov") String nom_prov, 
            @QueryParam("nom_depto") String nom_depto,
            @QueryParam("nom_loc") String nom_loc){
        CentroPoblado result = new CentroPoblado();
        // Busco la provincia a partir del nombre
        validarTokenTerr();
        provClient = new ProvinciaClient();
        Provincia prov = provClient.findByQuery_JSON(Provincia.class, nom_prov.toUpperCase(), token.getStrToken());
        provClient.close();
        // Si obtuve la Provincia busco el Departamento a partir del nombre recibido y el id de la Provincia obtenida
        if(prov != null){
            validarTokenTerr();
            deptoClient = new DepartamentoClient();
            Departamento depto = deptoClient.findByQuery_JSON(Departamento.class, nom_depto.toUpperCase(), prov.getId().toString(), token.getStrToken());
            deptoClient.close();
            // Si obtuve el Departamento busco la Localidad a partir del nombre recibido y el id del Departamento obtenido
            if(depto != null){
                localidadClient = new LocalidadClient();
                result = localidadClient.findByQuery_JSON(CentroPoblado.class, nom_loc.toUpperCase(), depto.getId().toString(), token.getStrToken());
            }
        }
        return result;
    }    
    
    /********************
     * Métodos privados *
     ********************/
    
    /**
     * Método privado que valida la existencia y vigencia del token de acceso al TERR
     * Utilizado en findByQuery()
     */
    private void validarTokenTerr(){
        if(token == null){
            getTokenTerr();
        }else try {
            if(!token.isVigente()){
                getTokenTerr();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token TERR." + ex.getMessage());
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en validarToken()
     */
    private void getTokenTerr(){
        try{
            usClientTerr = new ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient();
            Response responseUs = usClientTerr.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strToken = (String)lstHeaders.get(0); 
            token = new Token(strToken, System.currentTimeMillis());
            usClientTerr.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API TERR: " + ex.getMessage());
        }
    }   
}
