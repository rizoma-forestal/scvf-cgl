
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EntidadGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import java.net.URI;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad EntidadGuia
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("entidadesguia")
public class EntidadGuiaFacadeREST {
    
    @EJB
    private EntidadGuiaFacade entidadFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    @EJB
    private UsuarioFacade usFacade;
    @Context
    UriInfo uriInfo;     

    /**
     * @api {post} /entidadesdguia Registrar una EntidadGuia
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X POST -d [PATH_SERVER]/gestionLocal/rest/entidadesdguia -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PostEntidadGuia
     * @apiGroup EntidadGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cglsicma.EntidadGuia} entity Objeto java del paquete paqGestionLocalSICMA.jar con los datos de la EntidadGuia a registrar
     * @apiParamExample {java} Ejemplo de EntidadGuia
     *      {"entity":{
     *              "id_usuario":"1",
     *              "id_rue":"36",
     *              "tipo_ent":"ORIGEN",
     *              "nom_persona":"ERNESTO FERNANDEZ",
     *              "tipo_persona":"FISICA",
     *              "cuit":"1234567892",
     *              "email":"pepe@juan.com",
     *              "id_loc":"21",
     *              "loc":"LA ESPINA",
     *              "depto":"OLIVA", 
     *              "prov": "CHACO",
     *              "id_catastral":"3698-p",
     *              "nom_predio":"LOS LUNARES",
     *              "num_aut":"3689/2018",
     *              "dom_calle": "SOLORZANO",
     *              "dom_numero":"3269"
     *          }
     *      }
     * @apiDescription Método para registrar una nueva EntidadGuia. 
     * Sea esta de ORIGEN o DESTINO de una Guía, la instancia como
     * paso previo al registro remoto de la Guía, la crea mediante el método local create(EnitdadGuia entGuia) 
     * @apiSuccess {String} Location url de acceso mediante GET a la EnitidadGuia registrada.
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 201 OK
     *     {
     *       {
     *          "Location": "[PATH_SERVER]/gestionLocal/rest/entidadesdguia/:id"
     *       }
     *     }
     *
     * @apiError EntidadGuiaNoRegistrada No se registró la Guia.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "Hubo un error procesado la inserción de la EntidadGuia en el CGL"
     *     }
     */         
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(ar.gob.ambiente.sacvefor.servicios.cglsicma.EntidadGuia entity) {
        // instancio la EntidadGuia a persistir
        EntidadGuia entGuia = new EntidadGuia();
        try{
            // instancio los datos generales de la entidad recibida
            entGuia.setCuit(entity.getCuit());
            entGuia.setDepartamento(entity.getDepto());
            entGuia.setEmail(entity.getEmail());
            entGuia.setIdLocGT(entity.getId_loc());
            entGuia.setIdRue(entity.getId_rue());
            entGuia.setLocalidad(entity.getLoc());
            entGuia.setNombreCompleto(entity.getNom_persona());
            entGuia.setProvincia(entity.getProv());
            entGuia.setTipoPersona(entity.getTipo_persona());
            // instancio los datos de ubicación según sea ORIGEN o DESTINO y el tipo de EntidadGuia
            TipoParam tipoParam = tipoParamFacade.getExistente("TIPO_ENT_GUIA");
            if(entity.getTipo_ent().equals("ORIGEN")){
                entGuia.setNumAutorizacion(entity.getNum_aut());
                entGuia.setInmCatastro(entity.getId_catastral());
                entGuia.setInmNombre(entity.getNom_predio());
                entGuia.setTipoEntidadGuia(paramFacade.getExistente("ORIGEN", tipoParam));
            }else{
                entGuia.setInmDomicilio(entity.getDom_calle() + "-" + entity.getDom_numero());       
                entGuia.setTipoEntidadGuia(paramFacade.getExistente("DESTINO", tipoParam));
            }
            // instancio los datos de registro
            entGuia.setUsuario(usFacade.find(entity.getId_usuario()));
            entGuia.setFechaAlta(new Date(System.currentTimeMillis()));
            entGuia.setHabilitado(true);     
            // inserto la entidad guía
            entidadFacade.create(entGuia);
            // armo la respuesta exitosa
            UriBuilder uriBuilder = uriInfo.getRequestUriBuilder();
            URI uri = uriBuilder.path(entGuia.getId().toString()).build();
            return Response.created(uri).build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.status(400).entity("Hubo un error procesado la inserción de la EntidadGuia en el CGL. " + ex.getMessage()).build();
        }  
    }

    /**
     * @api {get} /entidadesdguia/:id Ver una EntidadGuía según su id
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/gestionLocal/rest/entidadesguia/4 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEntidadGuia
     * @apiGroup EntidadGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único de la EntidadGuía
     * @apiDescription Método para obtener una EntidadGuía existente según el id remitido.
     * Obtiene la EntidadGuía mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cglsicma.EntidadGuia} Entidad Guía Detalle de la EntidadGuía registrada.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *          {
     *              "id":"4",
     *              "id_usuario":"1",
     *              "id_rue":"36",
     *              "tipo_ent":"ORIGEN",
     *              "nom_persona":"ERNESTO FERNANDEZ",
     *              "tipo_persona":"FISICA",
     *              "cuit":"1234567892",
     *              "email":"pepe@juan.com",
     *              "id_loc":"21",
     *              "loc":"LA ESPINA",
     *              "depto":"OLIVA", 
     *              "prov": "CHACO",
     *              "id_catastral":"3698-p",
     *              "nom_predio":"LOS LUNARES",
     *              "num_aut":"3689/2018",
     *              "dom_calle": "SOLORZANO",
     *              "dom_numero":"3269"
     *          }
     * @apiError EntidadGuiaNotFound No existe entidad guía registrada con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay entidad guía registrada con el id recibido"
     *     }
     */      
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EntidadGuia find(@PathParam("id") Long id) {
        return entidadFacade.find(id);
    }

    /**
     * @api {get} /entidadesguia Ver todas las Entidades Guía
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/gestionLocal/rest/entidadesguia -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEntidadesGuia
     * @apiGroup EntidadGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de las entidades guia existentes.
     * Obtiene las guias mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cglsicma.EntidadGuia} EntidadGuia Listado con todas las Entidades Guía registradas.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "enitdadesguia": [
     *          {
     *              "id":"4",
     *              "id_usuario":"1",
     *              "id_rue":"36",
     *              "tipo_ent":"ORIGEN",
     *              "nom_persona":"ERNESTO FERNANDEZ",
     *              "tipo_persona":"FISICA",
     *              "cuit":"1234567892",
     *              "email":"pepe@juan.com",
     *              "id_loc":"21",
     *              "loc":"LA ESPINA",
     *              "depto":"OLIVA", 
     *              "prov": "CHACO",
     *              "id_catastral":"3698-p",
     *              "nom_predio":"LOS LUNARES",
     *              "num_aut":"3689/2018",
     *              "dom_calle": "SOLORZANO",
     *              "dom_numero":"3269"
     *          },
     *          {
     *              "id":"5",
     *              "id_usuario":"1",
     *              "id_rue":"38",
     *              "tipo_ent":"ORIGEN",
     *              "nom_persona":"ANIBAL HUGO",
     *              "tipo_persona":"FISICA",
     *              "cuit":"28345673698",
     *              "email":"pepe@local.com",
     *              "id_loc":"29",
     *              "loc":"LOS OLIMAREÑOS",
     *              "depto":"CANDELA", 
     *              "prov": "CHACO",
     *              "id_catastral":"3697-p",
     *              "nom_predio":"LAS GARZAS",
     *              "num_aut":"9687/2018",
     *              "dom_calle": "VENTURA",
     *              "dom_numero":"391"
     *          }
     *       ]
     *     }
     * @apiError EntidadesGuiaNotFound No existen Entidades Guía registradas.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay entidades guía registradas"
     *     }
     */      
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EntidadGuia> findAll() {
        return entidadFacade.findAll();
    }
    
    /**
     * @api {get} /entidadesguia/query?cuit=:cuit,num_aut=:num_aut,dom_calle=:dom_calle,dom_numero=:dom_numero,loc=:loc,depto=:depto,prov=:prov Ver Entidades Guía según parámetros.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/gestionLocal/rest/entidadesguia/query?cuit=369874561,aut=3698/2018 -H "authorization: xXyYvWzZ"
     *     curl -X GET -d [PATH_SERVER]/gestionLocal/rest/entidadesguia/query?cuit=369874561,dom_calle=VENTURA,dom_numero=391,loc=LOS OLIMAREÑOS,depto=CANDELA,prov=CHACO -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetEntidadGuiaQuery
     * @apiGroup EntidadGuia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} cuit Cuit de la persona que, según se trate de un ORIGEN o DESTINO será el titular o destinatario de la Guía.
     * @apiParam {String} num_aut En caso de ser un ORIGEN, se trata del número de la Autorización de extracción.
     * @apiParam {String} dom_calle En caso de ser un DESTINO, se trata del nombre de la Calle del domicilio del destino de la Guía
     * @apiParam {String} dom_numero En caso de ser un DESTINO, se trata del número de puerta del domicilio del destino de la Guía
     * @apiParam {String} loc En caso de ser un DESTINO, se trata de la Localidad del domicilio del destino de la Guía
     * @apiParam {String} depto En caso de ser un DESTINO, se trata del Departamento de la Localidad del domicilio del destino de la Guía
     * @apiParam {String} prov En caso de ser un DESTINO, se trata del Provincia del destino de la Guía
     * @apiDescription Método para obtener la/s  entidadesguía/s según el cuit del titular y el N° de Autorización (si es un ORIGEN) o
     * según el cuit del destinatario y el nombre de la calle, número de puerta, localidad, departamento y provincia, de su domicilio (si es un DESTINO)
     * Según el caso, obtiene las entidadesguia mediante los métodos getOrigenCuitAut() (en el primer caso) o getDestinoExistente() (en el segundo),
     * ambos del recurso EntidadGuiaFacade.
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cglsicma.EntidadGuia} EntidadGuia entidad guía o listado de las entidadesguia obtenidas.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *          "entidadguia": {
     *              "id":"5",
     *              "id_usuario":"1",
     *              "id_rue":"38",
     *              "tipo_ent":"ORIGEN",
     *              "nom_persona":"ANIBAL HUGO",
     *              "tipo_persona":"FISICA",
     *              "cuit":"28345673698",
     *              "email":"pepe@local.com",
     *              "id_loc":"29",
     *              "loc":"LOS OLIMAREÑOS",
     *              "depto":"CANDELA", 
     *              "prov": "CHACO",
     *              "id_catastral":"3697-p",
     *              "nom_predio":"LAS GARZAS",
     *              "num_aut":"9687/2018",
     *              "dom_calle": "VENTURA",
     *              "dom_numero":"391"
     *          }
     * @apiError EntidadGuiaNotFound No existe entidad guía registrada con esos parámetros.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay entidad guía registrada con los parámetros recibidos"
     *     }
     */          
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EntidadGuia findByQuery(@QueryParam("cuit") String cuit, 
            @QueryParam("num_aut") String num_aut, 
            @QueryParam("dom_calle") String dom_calle,
            @QueryParam("dom_numero") String dom_numero,
            @QueryParam("loc") String loc,
            @QueryParam("depto") String depto,
            @QueryParam("prov") String prov) {
        EntidadGuia result = new EntidadGuia();
        if(cuit != null && num_aut != null){
            // obtengo el origen según el cuit y el número de autorización
            result =  entidadFacade.getOrigenCuitAut(Long.valueOf(cuit), num_aut);
        }else if(cuit != null && dom_calle != null && dom_numero != null && loc != null && depto != null && prov != null){
            // obtengo el destino según el cuit y la calle y número del domicilio del destinatario
            // primero instancio el tipo
            TipoParam tipoParam = tipoParamFacade.getExistente("TIPO_ENT_GUIA");
            Parametrica tipoEnt = paramFacade.getExistente("DESTINO", tipoParam);
            // después instancio el domicilio
            Domicilio dom = new Domicilio();
            dom.setCalle(dom_calle);
            dom.setDepartamento(depto);
            dom.setLocalidad(loc);
            dom.setNumero(dom_numero);
            dom.setProvincia(prov);
            // busco el destino => PARA LA 2da ETAPA SERA EL METODO getDestinoExistente() QUE BUSCARA TAMBIEN POR idLocGT
            result =  entidadFacade.getDestinoExistenteSICMA(Long.valueOf(cuit), tipoEnt, dom);
        }
        return result;
    }

    @GET
    @Path("{from}/{to}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EntidadGuia> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return entidadFacade.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Secured
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(entidadFacade.count());
    }
}
