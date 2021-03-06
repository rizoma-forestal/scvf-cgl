
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.GuiaCtrlClient;
import ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.ParamCtrlClient;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Transporte;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EntidadGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.TipoParamClient;
import ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Guía
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("guias_sicma")
public class GuiaSICMAFacadeREST {
    
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en CTRL
     */
    private ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.UsuarioApiClient usApiClientCtrl;
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en TRAZ
     */
    private ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioApiClient usApiClientTraz;    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Control y verificación
     */ 
    private String strTokenCtrl;   
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Trazabilidad
     */ 
    private String strTokenTraz;        
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Control y verificación
     */    
    private Token tokenCtrl;    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Trazabilidad
     */    
    private Token tokenTraz;        
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(GuiaSICMAFacadeREST.class);      
    /**
     * Variable privada: cliente para el acceso a la API de Guías de Control y verificación
     */
    private GuiaCtrlClient guiaCtrlClient;    
    /**
     * Variable privada: ParamCtrlClient Cliente para la API REST de Control y Verificación
     */
    private ParamCtrlClient paramCtrlClient;    
    /**
     * Variable privada: cliente para el acceso a la API de usuarios de Trazabilidad
     */
    private UsuarioClient usuarioClientTraz;    
    /**
     * Variable privada: cliente para el acceso a la API de Tipo de paramétrica de Trazabilidad
     */
    private TipoParamClient tipoParamClient;   
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba") 
    private Session mailSesion;    
        
    @EJB
    private GuiaFacade guiaFacade;
    @EJB
    private EstadoGuiaFacade estadoFacade;
    @EJB
    private TipoGuiaFacade tipoGuiaFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;    
    @EJB
    private EntidadGuiaFacade entidadFacade;    
    @EJB
    private VehiculoFacade vehiculoFacade;
    @EJB
    private UsuarioFacade usFacade;     
    @EJB
    private ItemProductivoFacade itemFacade;    
    
    @Context
    UriInfo uriInfo;         
    
    /**
     * @api {post} /guias_sicma Registrar una Guia
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/gestionLocal/rest/guias_sicma -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la Guia a registrar
     * @apiParamExample {java} Ejemplo de Guia
     *      {"entity":{
     *              "id_usuario":"1",
     *              "codigo":"TT-25-00037-2018",
     *              "id_tipo":"7",
     *              "id_estado":"10",
     *              "num_fuente":"3689/2018",
     *              "id_origen":"8",
     *              "id_destino":"10",
     *              "transporte":
     *                      {
     *                          "id_usuario":"1",
     *                          "cond_nombre":"OLIVERIO GARCIA",
     *                          "cond_dni":"26985471",
     *                          "id_veh":"5"
     *                      }
     *          }
     *      }
     * @apiDescription Método para registrar una nueva Guia de forma remota. 
     * Se setean los datos a partir de la entidad recibida como parámetro
     * La crea mediante el método local create(Guia guia).
     * Junto con la Guía persiste la entidad Transporte (con el Vehículo incluído) que se actualiza en cascada
     * @apiSuccess {String} Location url de acceso mediante GET a la Guia registrada.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/gestionLocal/rest/guias_sicma/:id"
     *       }
     *     }
     *
     * @apiError GuiaNoRegistrada No se registró la Guia.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesado la inserción de la Guia en el CGL"
     *     }
     */          
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia entity) {
        // instancio la Guia a persistir
        Guia guiaCgl = new Guia();
        try{
            // instancio los datos generales de la entidad recibida
            guiaCgl.setCodigo(entity.getCodigo());
            guiaCgl.setNumFuente(entity.getNum_fuente());
            // seteo tipo de guía a partir del id_tipo
            guiaCgl.setTipo(tipoGuiaFacade.find(entity.getId_tipo()));
            // seteo el tipo de fuente
            TipoParam tipoParam = tipoParamFacade.getExistente("TIPO_FUENTE");
            guiaCgl.setTipoFuente(paramFacade.getExistente("AUTORIZACION", tipoParam));
            // busco el origen a partir del id_origen
            guiaCgl.setOrigen(entidadFacade.find(entity.getId_origen()));
            // busco el destino a partir del id_destino
            guiaCgl.setDestino(entidadFacade.find(entity.getId_destino()));

            // instancio el transporte
            Transporte transporte = new Transporte();
            transporte.setCondNombre(entity.getTransporte().getCond_nombre().toUpperCase());
            transporte.setCondDni(entity.getTransporte().getCond_dni());
            if(!entity.getTransporte().getAcoplado().equals("default")){
                transporte.setAcoplado(entity.getTransporte().getAcoplado());
            }else{
                transporte.setAcoplado("");
            }
            // seteo el vehículo al transporte
            transporte.setVehiculo(vehiculoFacade.find(entity.getTransporte().getId_veh()));
            // seteo el transporte a la Guía
            guiaCgl.setTransporte(transporte);
            // seteo el estado
            guiaCgl.setEstado(estadoFacade.find(entity.getId_estado()));
            // seteo las observaciones
            guiaCgl.setObs("Guía registrada desde la API de integración SICMA-SACVeFor");
            // seteo datos de registro
            guiaCgl.setUsuario(usFacade.find(entity.getId_usuario()));
            guiaCgl.setFechaAlta(new Date(System.currentTimeMillis()));
            // inserto la guía
            guiaFacade.create(guiaCgl);
            // armo la respuesta exitosa
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            URI uri = uriBuilder.path(guiaCgl.getId().toString()).build();
            return Response.created(uri).build();
            
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            LOG.fatal("Hubo un error registrando la guía remota: " + entity.getCodigo() + "." + ex.getMessage());
            return Response.status(400).entity("Hubo un error procesado la inserción de la Guia en el CGL. " + ex.getMessage()).build();
        }
    }   
    
    /**
     * @api {put} /guias_sicma/:id Emite una guía registrada previamente
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]:cgl-prov/rest/guias_sicma/32 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la Guia a emitir
     * @apiParam {Long} Id Identificador único de la Guía a actualizar
     * @apiParamExample {java} Ejemplo de Guia
     *      {"entity":{
     *                  "id_usuario":"1",
     *                  "codigo":"TT-25-00037-2018",
     *                  "id_tipo":"7",
     *                  "id_estado":"9",
     *                  "num_fuente":"3689/2018",
     *                  "id_origen":"8",
     *                  "id_destino":"10",
     *                  "vigencia":"24",
     *                  "transporte":
     *                          {
     *                               "id_usuario": "1",
     *                               "acoplado": "APC-369",
     *                               "cond_nombre": "ERNESTO FERNANDEZ",
     *                               "cond_dni": "36235415",
     *                               "id_veh":"129"
     *                          }
     *              }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "32"
     *      }
     * @apiDescription Método para emitir una Guía registrada previamente luego de haber registrado los items productivos.
     * Los pasos que se ejecutan son los siguientes:
     * Se obtiene la guía a partir del id
     * Se setean las fechas, incluyendo el vencimiento
     * Se setea el estado de EMITIDA
     * Se asigna el usuario de registro y se actualiza la guía
     * Se instancia la guía CTRL con sus correspondientes ítems productivos
     * Se registra la guía en CTRL mediant la API respectiva
     * Si todo salió bien, se verifica la existencia de una cuenta de usuario para el destinatario de la guía en TRAZ
     * Si la cuenta existe, se notifica al destinatario la emisión de la guía a su nombre
     * Si la cuenta no existe, se la crea mediante la API correspondiente.
     * Finalmente, se arma el mensaje de respuesta al usuario
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError GuiaNoEmitida No se emitió la Guía.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la emisión de la Guía."
     *     }
     */  
    @PUT
    @Path("{id}")
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response emitir(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia entity){
        boolean persistidaCcv = true;
        boolean persistidaCgt = true;
        boolean mensajeEnviado = true;
        String msgError = "";
        try{
            // obtengo la guía
            Guia guiaCgl = guiaFacade.find(id);
            // seteo de fecha de emisión y de vencimiento
            Date fechaEmision = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fechaEmision); 
            calendar.add(Calendar.HOUR, entity.getVigencia());
            Date fechaVencimiento = calendar.getTime(); 
            guiaCgl.setFechaEmisionGuia(fechaEmision);
            guiaCgl.setFechaVencimiento(fechaVencimiento);
            // seteo el estado de EMITIDA
            guiaCgl.setEstado(estadoFacade.find(entity.getId_estado()));
            // setear usuario modificación
            guiaCgl.setUsuario(usFacade.find(entity.getId_usuario()));
            // actualizo guía
            guiaFacade.edit(guiaCgl);
            /* registro la guía emitida en el CTRL */
            validarTokenCtrl();
            // instancio los ítems CTRL
            List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item> lstItemCtrl = new ArrayList<>();
            ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item itemCtrl;
            for(ItemProductivo item : guiaCgl.getItems()){
                itemCtrl = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Item();
                itemCtrl.setCodigoOrigen(item.getCodigoProducto());
                itemCtrl.setNombreCientifico(item.getNombreCientifico());
                itemCtrl.setNombreVulgar(item.getNombreVulgar());
                itemCtrl.setClase(item.getClase());
                itemCtrl.setUnidad(item.getUnidad());
                itemCtrl.setTotal(item.getTotal());
                itemCtrl.setTotalKg(item.getTotalKg());
                lstItemCtrl.add(itemCtrl);
            }
            // instancio la guía CTRL
            ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCrtl = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
            guiaCrtl.setCodigo(entity.getCodigo());
            guiaCrtl.setTipo(guiaCgl.getTipo().getNombre());
            guiaCrtl.setTipoFuente(guiaCgl.getTipoFuente().getNombre());
            guiaCrtl.setNumFuente(guiaCgl.getNumFuente());
            guiaCrtl.setItems(lstItemCtrl);
            guiaCrtl.setNombreOrigen(guiaCgl.getOrigen().getNombreCompleto());
            guiaCrtl.setCuitOrigen(guiaCgl.getOrigen().getCuit());
            guiaCrtl.setLocOrigen(guiaCgl.getOrigen().getLocalidad() + " - " + guiaCgl.getOrigen().getProvincia());
            guiaCrtl.setNombreDestino(guiaCgl.getDestino().getNombreCompleto());
            guiaCrtl.setCuitDestino(guiaCgl.getDestino().getCuit());
            guiaCrtl.setLocDestino(guiaCgl.getDestino().getLocalidad() + " - " + guiaCgl.getDestino().getProvincia());
            guiaCrtl.setMatVehiculo(guiaCgl.getTransporte().getVehiculo().getMatricula());
            if(!entity.getTransporte().getAcoplado().equals("default")){
                guiaCrtl.setMatAcoplado(entity.getTransporte().getAcoplado());
            }
            guiaCrtl.setNombreConductor(entity.getTransporte().getCond_nombre());
            guiaCrtl.setDniConductor(entity.getTransporte().getCond_dni());
            guiaCrtl.setFechaEmision(guiaCgl.getFechaEmisionGuia());
            guiaCrtl.setFechaVencimiento(guiaCgl.getFechaVencimiento());
            guiaCrtl.setProvincia(guiaCgl.getProvincia());
            // instancio el cliente de la API
            guiaCtrlClient = new GuiaCtrlClient();
            Response responseCcv = guiaCtrlClient.create_JSON(guiaCrtl, tokenCtrl.getStrToken());
            guiaCtrlClient.close();
            // solo continúo si se persistió
            if(responseCcv.getStatus() != 201){
                persistidaCcv = true;
            }
            if(persistidaCcv){
                /* si todo salió bien, sigo con el componente TRAZ */
                // valido el token para obtener el usuario del destinatario
                validarTokenTraz();
                usuarioClientTraz = new UsuarioClient();
                GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>> gTypeUs = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario>>() {};
                Response responseCgt = usuarioClientTraz.findByQuery_JSON(Response.class, String.valueOf(guiaCgl.getDestino().getCuit()), null, tokenTraz.getStrToken());
                List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario> listUs = responseCgt.readEntity(gTypeUs);
                if(!listUs.isEmpty()){
                    // el destinatario ya tiene cuenta, así que solo notifico
                    usuarioClientTraz.close();
                    if(!notificarEmision(guiaCgl)){
                        mensajeEnviado = false;
                        msgError = "La guía se emitió correctamente, se registró en el componente de Control y Vefificación "
                                + "pero no se pudo notificar al destinatario, que deberá ser notificado por algún medio. ";
                    }
                }else{
                    // se deberá registrar un usuario para el destinatario en el componente TRAZ
                    validarTokenTraz();
                    // obtengo el TipoParam para el Rol
                    tipoParamClient = new TipoParamClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam>> gTypeTipo = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam>>() {};
                    responseCgt = tipoParamClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("RolUsuarios"), tokenTraz.getStrToken());
                    List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam> listTipos = responseCgt.readEntity(gTypeTipo);
                    ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica rol = null;
                    if(!listTipos.isEmpty()){
                        // obtengo el rol                    
                        GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica>> gTypeParam = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica>>() {};
                        responseCgt = tipoParamClient.findParametricasByTipo_JSON(Response.class, String.valueOf(listTipos.get(0).getId()), tokenTraz.getStrToken());
                        List<ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica> listParam = responseCgt.readEntity(gTypeParam);
                        tipoParamClient.close();
                        for(ar.gob.ambiente.sacvefor.servicios.trazabilidad.Parametrica param : listParam){
                            if(param.getNombre().equals(ResourceBundle.getBundle("/Config").getString("Transformador"))){
                                rol = param;
                            }
                        }
                    }else{
                        tipoParamClient.close();
                    }
                    // con el rol obtenido registro el Usuario vía API-TRAZ
                    ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario usTraz;
                    usTraz = new ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario();
                    usTraz.setEmail(guiaCgl.getDestino().getEmail());
                    usTraz.setJurisdiccion(guiaCgl.getDestino().getProvincia());
                    usTraz.setLogin(guiaCgl.getDestino().getCuit());
                    usTraz.setNombreCompleto(guiaCgl.getDestino().getNombreCompleto());
                    usTraz.setRol(rol);
                    responseCgt = usuarioClientTraz.create_JSON(usTraz, tokenTraz.getStrToken());
                    usuarioClientTraz.close();
                    // valido errores
                    if(responseCgt.getStatus() != 201){
                        persistidaCcv = false;
                        msgError = "La guía se emitió correctamente, se registró en el componente CTRL, pero hubo un error creando la cuenta del destinatario en TRAZ.";
                    }
                }
            }else{
                msgError = "La guía se emitió correctamente pero hubo un error registrándola en el componente CTRL.";
            }
            if(persistidaCcv && persistidaCgt && mensajeEnviado ){
                return Response.ok().build();
            }else{
                return Response.notModified().entity(msgError).build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            LOG.fatal("Hubo un error emitiendo la guía remota: " + entity.getCodigo() + "." + ex.getMessage());
            return Response.notModified().entity("Hubo un error emitiendo la guía remota. " + ex.getMessage()).build();
        }
    }    
    
    /**
     * @api {put} /guias_sicma/cancelar:id Cancela una guía emitida en vigencia
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]:cgl-prov/rest/guias_sicma/cancelar/26 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la Guia a cancelar
     * @apiParam {Long} Id Identificador único de la Guía a cancelar
     * @apiParamExample {java} Ejemplo de Guia
     *      {"entity":{
     *                  "id_usuario":"6",
     *                  "codigo":"TT-25-00026-2019",
     *                  "id_tipo":"2",
     *                  "id_estado":"9",
     *                  "num_fuente":"369/2018/LQMT",
     *                  "id_origen":"13",
     *                  "id_destino":"12",
     *                  "transporte":
     *                          {
     *                               "id_usuario": "1",
     *                               "cond_nombre": "FARRUGIA GONZALO",
     *                               "cond_dni": "36985741",
     *                               "id_veh":"4"
     *                          }
     *              }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "26"
     *      }
     * @apiDescription Método para cancelar una Guía ya emitida y todavía en vigencia.
     * Los pasos que se ejecutan son los siguientes:
     * Se obtiene la guía a partir del id
     * Se setean el estado (CANCELADA), el usuario y las observaciones
     * Luego se actualiza la Guía en el componente local.
     * Posteriormente se recorre el listado de items de la guía y se retorna su saldo al cupo del que descontó.
     * Se accede al CTRL y se obtiene la guía para actualizar su estado a CANCELADA
     * Finalmente, se arma el mensaje de respuesta al usuario.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError GuiaNoCancelada No se canceló la Guía.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la cancelación de la Guía."
     *     }
     */
    @PUT
    @Path("/cancelar/{id}")
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response cancelar(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia entity){
        boolean actualizadaCcv = false, actualizadaLocal = false, 
                guiaCcvNoEncontrada = false, estadoCcvNoEncontrado = false, titularNotificado = false, destinatarioNotificado = false;
        String msgError = "";
        try{
            //////////////////////////////////
            // actualizo la guía localmente //
            //////////////////////////////////
            // obtengo la guía
            Guia guiaCgl = guiaFacade.find(id);
            // seteo el estado de CANCELADA
            guiaCgl.setEstado(estadoFacade.getExistente(ResourceBundle.getBundle("/Config").getString("GuiaCancelada")));
            // setear usuario modificación
            guiaCgl.setUsuario(usFacade.find(entity.getId_usuario()));
            // seteo las observaciones por la cancelación
            guiaCgl.setObs("Guía cancelada por decisión de la Autoridad local.");
            // actualizo guía
            guiaFacade.edit(guiaCgl);
            // obtengo los items
            List<ItemProductivo> lstItems = guiaCgl.getItems();
            // recorro el listado y por cada uno retorno los productos a su origen original y deshabilito
            for (ItemProductivo item : lstItems){
                // obtengo el id del item origen
                Long idItemOrigen = item.getItemOrigen();
                if(idItemOrigen != null){
                    // obtengo el item origen
                    ItemProductivo itemOrigen = itemFacade.find(idItemOrigen);
                    // seteo los saldos actuales
                    float saldoActual = itemOrigen.getSaldo();
                    float saldoKgActual = itemOrigen.getSaldoKg();
                    // actualizo los saldos
                    itemOrigen.setSaldo(saldoActual + item.getTotal());
                    itemOrigen.setSaldoKg(saldoKgActual + item.getTotalKg());
                    // actualizo el origen
                    itemFacade.edit(itemOrigen);
                    // deshabilito el item actual
                    item.setHabilitado(false);
                    itemFacade.edit(item);
                }
            }
            actualizadaLocal = true;
            
            ////////////////////////////////////////////////////////////////////////
            // actualizo la guía en el componente de control y verificación (CCV) //
            ////////////////////////////////////////////////////////////////////////            
            // valido el token de acceso a CCV
            validarTokenCtrl();
            // busco la Guía
            guiaCtrlClient = new GuiaCtrlClient();
            List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
            Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guiaCgl.getCodigo(), null, null, tokenCtrl.getStrToken());
            lstGuias = response.readEntity(gTypeG);
            // valido que tuve respuesta
            if(lstGuias.get(0) != null){
                // obtengo el id de la guía en CCV
                Long idGuiaCtrl = lstGuias.get(0).getId();
                // instancio la guía a editar
                ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                // obtengo el estado "CANCELADA" del CCV
                List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica> lstParmEstados;
                paramCtrlClient = new ParamCtrlClient();
                GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>> gTypeParam = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Parametrica>>() {};
                Response responseParam = paramCtrlClient.findByQuery_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("CtrlTipoParamEstGuia"), ResourceBundle.getBundle("/Config").getString("CtrlGuiaCancelada"), tokenCtrl.getStrToken());
                lstParmEstados = responseParam.readEntity(gTypeParam);
                // solo continúo si encontré el Estado correspondiente
                if(lstParmEstados.get(0) != null){
                    // seteo la Guía solo con los valores que necesito para editarla
                    guiaCtrol.setId(idGuiaCtrl);
                    guiaCtrol.setEstado(lstParmEstados.get(0));
                    // valido el token
                    validarTokenCtrl();
                    response = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                    if(response.getStatus() == 200){
                        // se completaron todas las operaciones
                        actualizadaCcv = true;
                    }
                }else{
                    estadoCcvNoEncontrado = true;
                }
                // cierro los clientes
                paramCtrlClient.close();
                guiaCtrlClient.close();
            }else{
                // no se encontró la guía a editar en el CCV
                guiaCcvNoEncontrada = true;
            }
            
            // notifico al titular
            titularNotificado = notificarCancelTitular(guiaCgl);
            // notifico al destinatario      
            destinatarioNotificado = notificarCancelDestino(guiaCgl);
            
            // armo el retorno. Si llegó hasta acá se actualizó localmente
            if(!actualizadaCcv || guiaCcvNoEncontrada || estadoCcvNoEncontrado || !titularNotificado || !destinatarioNotificado){
                // si hubo algún problema en las comunicaciones y/o actualizaciones en 
                msgError = "La guía fue cancelada localmente. ";
                if(!actualizadaCcv) msgError += "No se actualizó la guía en el CCV. ";
                if(guiaCcvNoEncontrada ) msgError += "No se encontró la guía para actualizar en el CCV. ";
                if(estadoCcvNoEncontrado ) msgError += "No se encontró el estado CANCELADA en el CCV. ";
                if(!titularNotificado ) msgError += "No se pudo notificar al titular. ";
                if(!destinatarioNotificado ) msgError += "No se pudo notificar al destinatario.";
                return Response.accepted().entity(msgError).build();
            }else{
                // si no hubo problemas
                return Response.ok().build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            msgError = "Hubo un error en la cancelación de la guía. ";
            if(!actualizadaLocal ) msgError += "No se actualizó la guía localmente. ";
            if(!actualizadaCcv) msgError += "No se actualizó la guía en el CCV. ";
            if(guiaCcvNoEncontrada ) msgError += "No se encontró la guía para actualizar en el CCV. ";
            if(estadoCcvNoEncontrado ) msgError += "No se encontró el estado CANCELADA en el CCV. ";
            if(!titularNotificado ) msgError += "No se pudo notificar al titular. ";
            if(!destinatarioNotificado ) msgError += "No se pudo notificar al destinatario.";
            
            LOG.fatal("Hubo un error en la cancelación de la guía remota: " + entity.getCodigo() + ". " + ex.getMessage());
            return Response.notModified().entity(msgError + ". " + ex.getMessage()).build();
        }
    }
    
    /**
     * @api {put} /guias_sicma/extender:id Extiende la vigencia de una Guía ya emitida y aún no vencida
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]:cgl-prov/rest/guias_sicma/extender/26 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutExtenderGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la Guia a extender su vigencia
     * @apiParam {Long} Id Identificador único de la Guía a extender su vigencia
     * @apiParamExample {java} Ejemplo de Guia
     *      {"entity":{
     *                  "id_usuario":"6",
     *                  "codigo":"TT-25-00026-2019",
     *                  "id_tipo":"2",
     *                  "id_estado":"9",
     *                  "num_fuente":"369/2018/LQMT",
     *                  "id_origen":"13",
     *                  "id_destino":"12",
     *                  "vigencia":"24",
     *                  "fecha_venc":"2019-06-09",
     *                  "transporte":
     *                          {
     *                                  "id_usuario":"6",
     *                                  "cond_nombre":"FARRUGIA GONZALO",
     *                                  "cond_dni":"36985741",
     *                                  "id_veh":"4"
     *                          }
     *                 }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "26"
     *      }
     * @apiDescription Método para extender la vigencia de una Guía ya emitida sin haber vencido.
     * Los pasos que se ejecutan son los siguientes:
     * Se obtiene la guía a partir del id y valida que el nuevo vencimiento sea posterior al existente.
     * Se setean la nueva fecha de vencimiento, el usuario y las observaciones.
     * Luego se actualiza la Guía en el componente local.
     * Si todo anduvo bien, se obtiene la Guía del CTRL y se actualiza su vencimiento.
     * Luego de validado lo anterior se notifica al titular y al destinatario de la guía la modificación de su vencimiento.
     * Finalmente, se arma el mensaje de respuesta al usuario.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError GuiaNoExtendida No se extendió la vigencia de la Guía.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la extensión de la vigencia de la Guía."
     *     }
     */    
    @PUT
    @Path("/extender/{id}")
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response extender(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.cglsicma.Guia entity){
        boolean actualizoCtrl = false, actualizadaLocal = false, titularNotificado = false, 
                destinatarioNotificado = false, guiaCcvNoEncontrada = false;
        String msgError = "";    
        try{
            //////////////////////////////////
            // actualizo la guía localmente //
            //////////////////////////////////
            // obtengo la guía
            Guia guiaCgl = guiaFacade.find(id);
            // solo continúo si la fecha de vencimiento es validada
            if(!guiaCgl.getFechaVencimiento().after(entity.getFecha_venc())){
                // actualizo el vencimiento
                guiaCgl.setFechaVencimiento(entity.getFecha_venc());
                // actualizo el usuario de modificación
                guiaCgl.setUsuario(usFacade.find(entity.getId_usuario()));
                // seteo las observaciones por la extensión de la vigencia
                guiaCgl.setObs("Guía con la vigencia extendida por decisión de la Autoridad local.");
                // actualizo la guía
                guiaFacade.edit(guiaCgl);
                actualizadaLocal  = true;
                //////////////////////////////
                // actualizo la guía en CCV //
                //////////////////////////////
                // valido el token de acceso a CCV
                validarTokenCtrl();
                // busco la Guía
                guiaCtrlClient = new GuiaCtrlClient();
                List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia> lstGuias;
                GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>> gTypeG = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia>>() {};
                Response response = guiaCtrlClient.findByQuery_JSON(Response.class, guiaCgl.getCodigo(), null, null, tokenCtrl.getStrToken());
                lstGuias = response.readEntity(gTypeG);
                // valido que tuve respuesta
                if(lstGuias.get(0) != null){
                    // obtengo el id de la guía en CCV
                    Long idGuiaCtrl = lstGuias.get(0).getId();
                    // instancio la guía a editar
                    ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia guiaCtrol = new ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia();
                    // seteo la nueva fecha de venicimiento y el nuevo qr
                    guiaCtrol.setId(idGuiaCtrl);
                    guiaCtrol.setFechaVencimiento(guiaCgl.getFechaVencimiento());
                    // seteo el estado y el cuit destino porque los chequea la API
                    guiaCtrol.setEstado(lstGuias.get(0).getEstado());
                    guiaCtrol.setCuitDestino(lstGuias.get(0).getCuitDestino());
                    // vuelvo a validar el token
                    validarTokenCtrl();
                    response = guiaCtrlClient.edit_JSON(guiaCtrol, String.valueOf(guiaCtrol.getId()), tokenCtrl.getStrToken());
                    if(response.getStatus() == 200){
                        actualizoCtrl = true;
                    }else{
                        actualizoCtrl = false;
                    }
                    // cierro el cliente
                    guiaCtrlClient.close();

                }else{
                    guiaCcvNoEncontrada = true;
                }
                
                // si todo anduvo bien notifico
                if(actualizoCtrl){
                    // notifico al titular
                    if(guiaCgl.getOrigen().getEmail() != null){
                        titularNotificado = notificarExtVigencia(guiaCgl, guiaCgl.getOrigen().getEmail());
                    }
                    // notifico al destinatario
                    if(guiaCgl.getDestino().getEmail() != null){
                        destinatarioNotificado = notificarExtVigencia(guiaCgl, guiaCgl.getDestino().getEmail());
                    }
                }
                // armo el retorno. Si llegó hasta acá se actualizó localmente
                if(!actualizoCtrl || guiaCcvNoEncontrada || !titularNotificado || !destinatarioNotificado){
                    // si hubo algún problema en las comunicaciones y/o actualizaciones en 
                    msgError = "La vigencia de la guía fue exitosa localmente. ";
                    if(!actualizoCtrl) msgError += "No se actualizó la guía en el CCV. ";
                    if(guiaCcvNoEncontrada ) msgError += "No se encontró la guía para actualizar en el CCV. ";
                    if(!titularNotificado ) msgError += "No se pudo notificar al titular. ";
                    if(!destinatarioNotificado ) msgError += "No se pudo notificar al destinatario.";
                    return Response.accepted().entity(msgError).build();
                }else{
                    // si no hubo problemas
                    return Response.ok().build();
                }
            }else{
                return Response.notModified().entity("La nueva fecha de vencimiento debe ser mayor que la existente.").build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            msgError = "Hubo un error en la cancelación de la guía. ";
            if(!actualizadaLocal ) msgError += "No se actualizó la guía localmente. ";
            if(!actualizoCtrl) msgError += "No se actualizó la guía en el CCV. ";
            if(guiaCcvNoEncontrada ) msgError += "No se encontró la guía para actualizar en el CCV. ";
            if(!titularNotificado ) msgError += "No se pudo notificar al titular. ";
            if(!destinatarioNotificado ) msgError += "No se pudo notificar al destinatario.";
            
            LOG.fatal("Hubo un error extendiendo la vigencia de la guía remota: " + entity.getCodigo() + ". " + ex.getMessage());
            return Response.notModified().entity(msgError + ". " + ex.getMessage()).build();
        }
    }
    
    /**
     * Método privado que valida la existencia y vigencia del token de acceso al CTRL
     * Utilizado en emitir()
     */
    private void validarTokenCtrl(){
        if(tokenCtrl == null){
            getTokenCtrl();
        }else try {
            if(!tokenCtrl.isVigente()){
                getTokenCtrl();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token CTRL." + ex.getMessage());
        }
    }    
    /**
     * Método privado que obtiene y setea el tokenTraz para autentificarse ante la API rest de control y verificación
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en validarTokenCtrl()
     */    
    private void getTokenCtrl(){
        try{
            usApiClientCtrl = new ar.gob.ambiente.sacvefor.localcompleto.ctrl.client.UsuarioApiClient();
            Response responseUs = usApiClientCtrl.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestSvf"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenCtrl = (String)lstHeaders.get(0); 
            tokenCtrl = new Token(strTokenCtrl, System.currentTimeMillis());
            usApiClientCtrl.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API CTRL: " + ex.getMessage());
        }
    }       
    
    /**
     * Método privado que valida la existencia y vigencia del token de acceso al TRAZ
     * Utilizado en emitir()
     */
    private void validarTokenTraz(){
        if(tokenTraz == null){
            getTokenTraz();
        }else try {
            if(!tokenTraz.isVigente()){
                getTokenTraz();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token TRAZ." + ex.getMessage());
        }
    }      
    /**
     * Método privado que obtiene y setea el tokenTraz para autentificarse ante la API rest de trazabilidad
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en validarTokenTraz()
     */    
    private void getTokenTraz(){
        try{
            usApiClientTraz = new ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client.UsuarioApiClient();
            Response responseUs = usApiClientTraz.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestSvf"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenTraz = (String)lstHeaders.get(0); 
            tokenTraz = new Token(strTokenTraz, System.currentTimeMillis());
            usApiClientTraz.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para enviar un correo electrónico al usuario.
     * Utilizado en emitir()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarEmision(Guia guia){  
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean result;
        String bodyMessage;
        Message mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>Se acaba de emitir la Guía " + guia.getCodigo() + " a su nombre, proveniente de la Provincia de " + ResourceBundle.getBundle("/Config").getString("Provincia") + ".</p>"
                + "<p>Dispone hasta el " + formateador.format(guia.getFechaVencimiento()) + " para aceptarla y completar el ciclo.</p>"
                + "<p>Podrá hacerlo ingresando a " + ResourceBundle.getBundle("/Config").getString("TrazServer") + ResourceBundle.getBundle("/Config").getString("TrazRutaAplicacion") + " "
                + "con sus credenciales de acceso.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guia.getDestino().getEmail()));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Aviso de emisión de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            LOG.fatal("Hubo un error enviando el correo de emisión de la guía remota: " + guia.getCodigo() + " "
                    + "a su destinatario: " + guia.getDestino().getNombreCompleto() + ". " + ex.getMessage());
        }
        
        return result;
    }     
    
    /**
     * Método para notificar a titular de una guía que la misma fue cancelada.
     * Utilizado por cancelar()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarCancelTitular(Guia guia){
        boolean result;
        String bodyMessage;
        Message mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>La Guía " + guia.getCodigo() + " de la cual es titular, acaba de ser cancelada. Todo lo documentado en la misma queda sin efecto. "
                + "Los productos descontados han sido retornados a sus fuentes de origen. Por cualquier consulta podrá dirigirse a la Autoridad local.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";        
        
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guia.getOrigen().getEmail()));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Notificación de cancelación de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            LOG.fatal("Hubo un error enviando el correo de cancelación de la guía remota: " + guia.getCodigo() + " "
                    + "a su titular: " + guia.getOrigen().getNombreCompleto() + ". " + ex.getMessage());
        }        
        
        return result;
    }    
    
    /**
     * Método para notificar al destinatario de una guía que la misma fue cancelada.
     * Utilizado por cancelar()
     * @return boolean verdadero o falso según el correo se haya enviado o no
     */
    private boolean notificarCancelDestino(Guia guia){
        boolean result;
        String bodyMessage;
        Message mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>La Guía " + guia.getCodigo() + " que le fuera remitida ha sido cancelada por su titular " + guia.getOrigen().getNombreCompleto() + ". Todo lo documentado en la misma queda sin efecto.</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";        
        
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(guia.getDestino().getEmail()));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Notificación de cancelación de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            LOG.fatal("Hubo un error enviando el correo de cancelación de la guía remota: " + guia.getCodigo() + " "
                    + "a su destinatario: " + guia.getDestino().getNombreCompleto() + ". " + ex.getMessage());
        }        
        
        return result;
    }     
    
    /**
     * Método para enviar un correo electrónico al titular y al destinatario de una guía
     * para notificarlos de la extensión del período de vigencia.
     * @param mail String dirección de correo electrónico del destinatario del mensaje
     * @return boolean verdadero o false según el resultado del envío.
     */
    private boolean notificarExtVigencia(Guia guia, String email) {
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date hoy = new Date(System.currentTimeMillis());
        boolean result;
        String bodyMessage;
        Message mensaje = new MimeMessage(mailSesion);
        
        bodyMessage = "<p>Estimado/a</p> "
                + "Hoy: " + formateador.format(hoy) + " se ha extendido la vigencia de la "
                + "guía " + guia.getCodigo() + " emitida el " + formateador.format(guia.getFechaEmisionGuia()) + ", "
                + "que ahora vencerá el " + formateador.format(guia.getFechaVencimiento()) + ".</p>"
                
                + "<p>Por favor, no responda este correo.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "<br/> "
                + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "</a></p>";

        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mensaje.setSubject(ResourceBundle.getBundle("/Bundle").getString("Aplicacion") + ": Aviso de extensión del período de vigencia de Guía");
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date();
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
            
        }catch(MessagingException ex){
            result = false;
            LOG.fatal("Hubo un error enviando el correo de extensión de vigencia de la guía remota: " + guia.getCodigo() + " "
                    + "a : " + email + ". " + ex.getMessage());
        }
        
        return result;               
    }     
}
