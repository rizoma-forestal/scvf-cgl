
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.MarcaClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.ModeloClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.VehiculoClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.rue.Marca;
import ar.gob.ambiente.sacvefor.servicios.rue.Modelo;
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
import javax.ws.rs.PathParam;
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
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Vehiculo
 * @author rincostante
 */
@Stateless
@Path("vehiculos")
public class VehiculoFacadeREST {
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(VehiculoFacadeREST.class);    
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en el RUE
     */
    private UsuarioClient usClientRue;
    /**
     * Variable privada: Cliente para la API Rest de Vehiculo en el RUE
     */
    private VehiculoClient vehiculoClient; 
    /**
     * Variable privada: Cliente para la API Rest de Marca de Vehículos en el RUE
     */    
    private MarcaClient marcaClient;   
    /**
     * Variable privada: Cliente para la API Rest de Modeol de Vehículos en el RUE
     */ 
    private ModeloClient modeloClient;
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del RUE
     */
    private String strToken; 
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del RUE
     */       
    private Token token;

    @EJB
    private VehiculoFacade vehiculoFacade;
    @EJB
    private UsuarioFacade usFacade;    
    @Context
    UriInfo uriInfo;        
    
    /**
     * @api {post} /vehiculos Registrar un Vehículo
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/gestionLocal/rest/vehiculos -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostVehiculo
     * @apiGroup Vehiculo
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Vehiculo} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos del Vehículo a registrar
     * @apiParamExample {java} Ejemplo de Vehículo
     *      {"entity":{
     *              "id_usuario":"1",
     *              "matricula":"MPT-475",
     *              "marca":"Chevrolet",
     *              "modelo":"Alcara",
     *              "anio":"2012"
     *      }
     * @apiDescription Método para registrar un nuevo Vehículo de forma remota. 
     * Se setean los datos a partir de la entidad recibida como parámetro.
     * Se verifica la existencia del Vehículo en el RUE y sus entidades vinculadas mediante la API-RUE, 
     * si está obtiene su id para asingarlo al local.
     * Si no existe el vehículo en el RUE se verifica su marca. Si no está se la crea y luego el modelo respectivo (en el RUE)
     * Si está registrada la marca se consulta sobre el modelo, si no está se lo crea.
     * Con la marca y el modelo se registra el Vehículo en el RUE para setear luego su id en el CGL.
     * Una vez seteado el idRue del Vehículo se lo registra en el CGL mediante el método local create(Vehiculo vehiculo) 
     * @apiSuccess {String} Location url de acceso mediante GET al Vehículo registrado.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/gestionLocal/rest/vehiculos/:id"
     *       }
     *     }
     *
     * @apiError VehiuloNoRegistrado No se registró el Vehículo.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesado la inserción del Vehíuclo en el CGL"
     *     }
     */       
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.cglsicma.Vehiculo entity) {
        // variables para el vehículo RUE
        ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo vehRue = new ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo();
        Marca marcaRue;
        Modelo modeloRue;
        MultivaluedMap<String, Object> headers;
        String location;
        int barra;
        // mensaje de error
        String msgError = "";
        try{
            // consulto la existencia del Vehículo en el RUE
            validarTokenRue();
            // instancio el cliente REST
            vehiculoClient = new VehiculoClient();
            List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo> listVehiculos = new ArrayList<>();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>> gTypeVeh = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Vehiculo>>() {};
            Response responseVeh = vehiculoClient.findByQuery_JSON(Response.class, null, entity.getMatricula().toUpperCase(), null, token.getStrToken());
            listVehiculos = responseVeh.readEntity(gTypeVeh); 
            // cierro el cliente
            vehiculoClient.close();        
            // si está vacío, no existe continúo
            if(listVehiculos.isEmpty()){
                // busco la marca en el RUE
                validarTokenRue();
                marcaClient = new MarcaClient();
                marcaRue = marcaClient.findByName_JSON(Marca.class, entity.getMarca().toUpperCase(), token.getStrToken());
                // si no la encontró, continúo
                if(marcaRue == null){
                    // creo la marca en el RUE y el modelo
                    marcaRue = new Marca();
                    modeloRue = new Modelo();
                    marcaRue.setNombre(entity.getMarca().toUpperCase());
                    Response responseMarca = marcaClient.create_JSON(marcaRue, token.getStrToken());
                    // cierro el cliente
                    marcaClient.close();
                    if(responseMarca.getStatus() == 201){
                        // asigno el id a la marca creada obtenido del header 'location' del response: 
                        // http://localhost:8080/rue/rest/vehiculos_marcas/[id]
                        headers = responseMarca.getHeaders();
                        location = headers.getFirst("location").toString();
                        barra = location.lastIndexOf("/");
                        marcaRue.setId(Long.valueOf(location.substring(barra + 1)));
                        // continúo y registro el modelo
                        modeloRue = new Modelo();
                        modeloRue.setMarca(marcaRue);
                        modeloRue.setNombre(entity.getModelo().toUpperCase());
                        modeloClient = new ModeloClient();
                        Response responseModelo = modeloClient.create_JSON(modeloRue, token.getStrToken());
                        // cierro el cliente
                        modeloClient.close();
                        if(responseModelo.getStatus() == 201){
                            // asigno el id al modelo creado obtenido del header 'location' del response: 
                            // http://localhost:8080/rue/rest/vehiculos_modelos/[id]
                            headers = responseModelo.getHeaders();
                            location = headers.getFirst("location").toString();
                            barra = location.lastIndexOf("/");
                            modeloRue.setId(Long.valueOf(location.substring(barra + 1)));
                        }else{
                            // si hubo error seteo los mensajes
                            msgError = msgError + " Se registró la Marca pero hubo un error registrando el Modelo en el RUE.";
                            LOG.fatal("Hubo un error registrando un Modelo en el RUE.");
                        }
                    }else{
                        msgError = msgError + " No se pudo persistir la Marca en el RUE.";
                        LOG.fatal("Hubo un error registrando una Marca en el RUE.");
                    }
                }else{
                    // cierro el cliente
                    marcaClient.close();
                    // busco el modelo correspondiente
                    validarTokenRue();
                    modeloClient = new ModeloClient();
                    modeloRue = modeloClient.findByName_JSON(Modelo.class, entity.getModelo().toUpperCase(), token.getStrToken());
                    // si no lo encontró, lo creo
                    if(modeloRue == null){
                        // creo el modelo con la marca respectiva
                        modeloRue = new Modelo();
                        modeloRue.setMarca(marcaRue);
                        modeloRue.setNombre(entity.getModelo().toUpperCase());
                        Response responseModelo = modeloClient.create_JSON(modeloRue, token.getStrToken());
                        modeloClient.close();
                        if(responseModelo.getStatus() == 201){
                            // asigno el id al modelo creado obtenido del header 'location' del response: 
                            // http://localhost:8080/rue/rest/vehiculos_modelos/[id]
                            headers = responseModelo.getHeaders();
                            location = headers.getFirst("location").toString();
                            barra = location.lastIndexOf("/");
                            modeloRue.setId(Long.valueOf(location.substring(barra + 1)));
                        }else{
                            // si hubo error seteo los mensajes
                            msgError = msgError + " Se encontró la Marca pero hubo un error registrando el Modelo en el RUE.";
                            LOG.fatal("Hubo un error registrando un Modelo en el RUE."); 
                        }
                    }
                    // cierro el cliente
                    modeloClient.close();
                }
                // solo continúo si no hubo errores
                if(msgError.equals("")){
                    // seteo la marca y el modelo en el vehículo
                    vehRue.setModelo(modeloRue);
                    // seteo el resto de los datos
                    vehRue.setAnio(entity.getAnio());
                    vehRue.setIdProvinciaGt(Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")));
                    vehRue.setMatricula(entity.getMatricula());
                    vehRue.setProvinciaGestion(ResourceBundle.getBundle("/Config").getString("Provincia"));
                    vehRue.setFechaAlta(new Date(System.currentTimeMillis()));
                    // registro el vehículo en el RUE
                    vehiculoClient = new VehiculoClient();
                    responseVeh = vehiculoClient.create_JSON(vehRue, token.getStrToken());
                    vehiculoClient.close();
                    if(responseVeh.getStatus() == 201){
                        // asigno el id al vehículo creado obtenido del header 'location' del response: 
                        // http://localhost:8080/rue/rest/vehiculos/[id]
                        headers = responseVeh.getHeaders();
                        location = headers.getFirst("location").toString();
                        barra = location.lastIndexOf("/");
                        vehRue.setId(Long.valueOf(location.substring(barra + 1)));
                    }else{
                        // si hubo error seteo los mensajes
                        msgError = msgError + " Hubo un error registrando el Modelo en el RUE.";
                        LOG.fatal("Hubo un error registrando un Vehículo en el RUE. " + entity.getMatricula());
                    }
                }
            }else{
                // guardo el vehículo obtenido en la primera lectura al RUE
                vehRue = listVehiculos.get(0);
                // limpio el listado
                listVehiculos.clear();
            }
            // solo continúo si no hubo errores seteo los datos generales
            if(msgError.equals("")){
                Vehiculo vehCgl = new Vehiculo();
                vehCgl.setAnio(entity.getAnio());
                vehCgl.setHabilitado(true);
                vehCgl.setIdRue(vehRue.getId());
                vehCgl.setMarca(entity.getMarca().toUpperCase());
                vehCgl.setMatricula(entity.getMatricula().toUpperCase());
                vehCgl.setModelo(entity.getModelo().toUpperCase());
                // seteo los datos de registro
                vehCgl.setUsuario(usFacade.find(entity.getId_usuario()));
                vehCgl.setFechaAlta(new Date(System.currentTimeMillis()));
                // registro le vehículo localmente
                vehiculoFacade.create(vehCgl);
                // armo la respuesta exitosa
                UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
                URI uri = uriBuilder.path(vehCgl.getId().toString()).build();
                return Response.created(uri).build();   
            }else{
                // si hubo errores no persisto y mando el mensaje
                return Response.status(400).entity("Hubo un error procesado la inserción del Vehículo en el CGL. " + msgError).build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción del Vehículo en el CGL. " + ex.getMessage()).build();
        }
    }

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
