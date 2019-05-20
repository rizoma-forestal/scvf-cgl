
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.PersonaClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoEntidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoSociedadClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoEntidad;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoPersona;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoSociedad;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Persona
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("personas")
public class PersonaFacadeREST {
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del RUE
     */
    private String strToken; 
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del RUE
     */       
    private Token token;   
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(PersonaFacadeREST.class);       
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en el RUE
     */
    private UsuarioClient usClientRue;   
    /**
     * Variable privada: Cleinte para la API Rest de Personas
     */
    private PersonaClient perClient;
    /**
     * Variable privada: Cleinte para la API Rest de TipoEntidad
     */
    private TipoEntidadClient tipoEntClient;
    /**
     * Variable privada: Cleinte para la API Rest de TipoEntidad
     */
    private TipoSociedadClient tipoSocClient;    
    
    @EJB
    private PersonaFacade personaFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private UsuarioFacade usFacade;
    @Context
    UriInfo uriInfo;         
    
    /**
     * @api {post} /personas Registrar una Persona
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/gestionLocal/rest/personas -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostPersona
     * @apiGroup Persona
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Persona} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la Persona a registrar
     * @apiParamExample {java} Ejemplo de Persona
     *      {
     *         "id_usuario":"6",
     *         "razon_social":"frecuencia ovejuna",
     *         "cuit":"30693846210", 
     *         "email":"rincostante@ambiente.gob.ar",
     *         "id_rol":"7",
     *         "tipo_persona":"JURIDICA",
     *         "id_prov":"6",
     *         "prov":"CHACO",
     *         "id_tipo_soc":"5"
     *      }
     * @apiDescription Método para registrar una nueva Persona de forma remota. 
     * Se setean los datos a partir de la entidad recibida como parámetro.
     * Se verifica la existencia de la Persona en el RUE mediante la API-RUE, 
     * si está obtiene su id para asingarlo al local.
     * Si no existe la Persona se la setea a partir de los valores recibidos como parámetros, en los casos de entidades paramétricas,
     * se obtendrán desde la misma API del RUE. Luego se la registra mediante la misma API
     * Una vez registrada la Persona en el RUE y seteado el idRue correspondiente se la registra en el CGL 
     * mediante el método local create(Persona persona) 
     * @apiSuccess {String} Location url de acceso mediante GET a la Persona registrada.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/gestionLocal/rest/personas/:id"
     *       }
     *     }
     *
     * @apiError PersonaNoRegistrada No se registró la Persona.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesado la inserción de la Persona en el CGL"
     *     }
     */        
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.cglsicma.Persona entity) {
        Persona perCgl = new Persona();
        ar.gob.ambiente.sacvefor.servicios.rue.Persona perRue = new ar.gob.ambiente.sacvefor.servicios.rue.Persona();
        String msgError = "";
        MultivaluedMap<String, Object> headers;
        String location;
        int barra;
        try{
            // busco la persona en el RUE
            validarTokenRue();
            // instancio el cliente REST
            perClient = new PersonaClient();
            List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gTypePer = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
            Response responsePer = perClient.findByQuery_JSON(Response.class, null, entity.getCuit().toString(), null, token.getStrToken());
            listPersonas = responsePer.readEntity(gTypePer); 
            // cierro el cliente
            perClient.close();  
            // si está vacío, no existe continúo
            if(listPersonas.isEmpty()){
                /* obtengo el TipoEntidad que corresponda: 
                 * para id_rol = 7 => PRODUCTOR
                 * para id_rol = 26 => TRANSFORMADOR
                 * para id_rol = 27 => TRANSPORTISTA
                 */
                String tipoEntidad;
                switch(entity.getId_rol().intValue()){
                    case 7:
                        tipoEntidad = "PRODUCTOR";
                        break;
                    case 26:
                        tipoEntidad = "TRANSFORMADOR";
                        break;
                    case 27:
                        tipoEntidad = "TRANSPORTISTA";
                        break;
                    default:
                        tipoEntidad = "";
                        break;
                }
                if(!tipoEntidad.equals("")){
                    // valido el token
                    validarTokenRue();
                    // instancio el cliente REST
                    tipoEntClient = new TipoEntidadClient();
                    TipoEntidad responseTipoEnt = tipoEntClient.findByName_JSON(TipoEntidad.class, tipoEntidad, token.getStrToken());
                    tipoEntClient.close();
                    perRue.setEntidad(responseTipoEnt);
                }else{
                    msgError = msgError + " No se obtuvo el tipo de entidad en el RUE.";
                }
                // seteo el tipo de persona
                if(msgError.equals("") && entity.getTipo_persona().equals("FISICA")){
                    perRue.setTipo(TipoPersona.FISICA);
                    perRue.setNombreCompleto(entity.getNom_persona().toUpperCase());
                }else if(msgError.equals("") && entity.getTipo_persona().equals("JURIDICA")){
                    perRue.setTipo(TipoPersona.JURIDICA);
                    // busco el tipo de sociedad. Valido el token
                    validarTokenRue();
                    // instancio el cliente REST
                    tipoSocClient = new TipoSociedadClient();
                    TipoSociedad responseTipoSoc = tipoSocClient.find_JSON(TipoSociedad.class, String.valueOf(entity.getId_tipo_soc()), token.getStrToken());
                    tipoSocClient.close();
                    perRue.setTipoSociedad(responseTipoSoc);
                    perRue.setRazonSocial(entity.getRazon_social().toUpperCase());
                }else{
                    msgError = msgError + " No se obtuvo el tipo de Persona en el RUE.";
                }
                
                // si no hubo errores seteo el resto de los atributos de la persona RUE y persisto
                if(msgError.equals("")){
                    perRue.setCorreoElectronico(entity.getEmail());
                    perRue.setCuit(entity.getCuit());
                    perRue.setIdProvinciaGt(Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")));
                    perRue.setProvinciaGestion(ResourceBundle.getBundle("/Config").getString("Provincia"));
                    perRue.setFechaAlta(new Date(System.currentTimeMillis()));
                    // pongo el domicilio en null porque no se va a registrar
                    perRue.setDomicilio(null);
                    // valido el tocken
                    validarTokenRue();
                    // instancio el cliente RUE
                    perClient = new PersonaClient();
                    Response res = perClient.create_JSON(perRue, token.getStrToken());
                    perClient.close();
                    if(res.getStatus() != 201){
                        msgError = msgError + " Hubo un error registrando la Persona en el RUE.";
                    }else{
                        // asigno el id a la Persona creada en el RUE
                        // http://localhost:8080/rue/rest/personas/[id]
                        headers = res.getHeaders();
                        location = headers.getFirst("location").toString();
                        barra = location.lastIndexOf("/");
                        perRue.setId(Long.valueOf(location.substring(barra + 1)));
                    }
                }
            }else{
                // si la persona ya estaba registrada en el RUE, seteo 'perRue'
                perRue = listPersonas.get(0);
            }
            // si no hay error continúo
            if(msgError.equals("")){
                // seteo los atributos de la persona en el CGL
                perCgl.setCuit(perRue.getCuit());
                perCgl.setEmail(entity.getEmail());
                perCgl.setIdRue(perRue.getId());
                // seteo el nombre y el tipo de persona según corresponda
                if(entity.getTipo_persona().equals("FISICA")){
                    perCgl.setNombreCompleto(perRue.getNombreCompleto().toUpperCase());
                    perCgl.setTipo("Persona Física");
                }else{
                    perCgl.setNombreCompleto(perRue.getRazonSocial().toUpperCase());
                    perCgl.setTipo("Persona Jurídica");
                }
                // seteo el Rol
                perCgl.setRolPersona(paramFacade.find(entity.getId_rol()));
                // seteo los datos de registro
                perCgl.setUsuario(usFacade.find(entity.getId_usuario()));
                perCgl.setFechaAlta(new Date(System.currentTimeMillis()));
                perCgl.setHabilitado(true);
                // persisto
                personaFacade.create(perCgl);
                // armo la respuesta existosa
                UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
                URI uri = uriBuilder.path(perCgl.getId().toString()).build();
                return Response.created(uri).build();  
            }else{
                // si hubo errores mando el mensaje
                return Response.status(400).entity("Hubo un error procesado la inserción de la Persona en el CGL. " + msgError).build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción de la Persona en el CGL. " + ex.getMessage()).build();
        }  
    }    
    
    /**
     * @api {get} /personas/query?cuit=:cuit&id_rol=:id_rol Ver Personas según su CUIT y Rol.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/personas/query?cuit=30693846210&id_rol=26 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetPersonasQuery
     * @apiGroup Persona
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} cuit de la Persona solicitada
     * @apiParam {String} id_rol rol de la Persona solicitada 
     * @apiDescription Método para obtener una Perosna según su CUIT y su Rol.
     * Obtiene la Persona con el método local findVigenteByCuitRol(Long.valueOf(cuit), rol)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Persona} Persona persona obtenida.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *              {
     *                  "id_usuario":"6",
     *                  "razon_social":"frecuencia ovejuna",
     *                  "cuit":"30693846210", 
     *                  "email":"rincostante@ambiente.gob.ar",
     *                  "id_rol":"7",
     *                  "tipo_persona":"JURIDICA",
     *                  "id_prov":"6",
     *                  "prov":"CHACO",
     *                  "id_tipo_soc":"5"
     *              }
     * @apiError PersonaNotFound No existe Persona registrada con ese nombre.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay Persona registrada con con ese nombre"
     *     }
     */          
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Persona findByQuery(@QueryParam("cuit") String cuit, 
            @QueryParam("id_rol") String id_rol){
        Persona result = new Persona();
        // obtengo la paramétrica del Rol
        Parametrica rol = paramFacade.find(Long.valueOf(id_rol));
        if(rol != null){
            // obtento la persona
            result = personaFacade.findVigenteByCuitRol(Long.valueOf(cuit), rol);
        }
        return result;
    }
    
    /********************
     * Métodos privados *
     ********************/
    
    /**
     * Método privado que valida la existencia y vigencia del token de acceso al RUE
     * Utilizado en create()
     */
    private void validarTokenRue(){
        if(token == null){
            getTokenRue();
        }else try {
            if(!token.isVigente()){
                getTokenRue();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token RUE." + ex.getMessage());
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en validarToken()
     */
    private void getTokenRue(){
        try{
            usClientRue = new ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient();
            Response responseUs = usClientRue.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestRue"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strToken = (String)lstHeaders.get(0); 
            token = new Token(strToken, System.currentTimeMillis());
            usClientRue.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API RUE: " + ex.getMessage());
        }
    }      
}
