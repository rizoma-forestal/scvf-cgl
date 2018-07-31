
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Guia
 * @author rincostante
 */
@Stateless
@Path("guias")
public class GuiaFacadeREST {

    @EJB
    private GuiaFacade guiaFacade;
    @EJB
    private EstadoGuiaFacade estadoFacade;
    @EJB
    private ItemProductivoFacade itemFacade;

    /**
     * @api {put} /guias/:id Actualizar una Guía existente
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X PUT -d [PATH_SERVER]:cgl-prov/rest/guias/32 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName PutGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {ar.gob.ambiente.sacvefor.servicios.cgl.Guia} entity Objeto java del paquete paqGestionLocal.jar con los datos de la Guia a actualizar
     * @apiParam {Long} Id Identificador único de la Guía a actualizar
     * @apiParamExample {java} Ejemplo de Guia
     *      {"entity":{
     *              "id": "32",   
     *              "items": [
     *                  {"id": "150",
     *                  "nombreCientifico": "Anadenanthera.colubrina", 
     *                  "nombreVulgar": "HUILCO",
     *                  "clase": "POSTE",
     *                  "unidad": "UNIDAD",
     *                  "idEspecieTax": "3076",
     *                  "kilosXUnidad": "425",
     *                  "total": "3",
     *                  "totalKg": "1275",
     *                  "saldo": "3",
     *                  "saldoKg": "1275", 
     *                  "obs": "",
     *                  "codigoProducto": "null|Anadenanthera.colubrina|HUILCO|POSTE|UNIDAD|270/3190/PPAA|[Provincia]"},
     *                  {"id": "151",
     *                  "nombreCientifico": "Maclura tinctoria",
     *                  "nombreVulgar": "MORA AMARILLA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3126",
     *                  "kilosXUnidad": "800",
     *                  "total": "13",
     *                  "totalKg": "10400",
     *                  "saldo": "13",
     *                  "saldoKg": "10400",
     *                  "obs": "",
     *                  "codigoProducto": "null|Maclura tinctoria|MORA AMARILLA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"},
     *                  {"id": "152",
     *                  "nombreCientifico": "Myroxylon peruiferum",
     *                  "nombreVulgar": "QUINA COLORADA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3105",
     *                  "kilosXUnidad": "1190",
     *                  "total": "7",
     *                  "totalKg": "8330",
     *                  "saldo": "7",
     *                  "saldoKg": "8330",
     *                  "obs": "",
     *                  "codigoProducto": "null|Myroxylon peruiferum|QUINA COLORADA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"}
     *              ],
     *              "codigo": "TT-10-00003-2017",
     *              "numFuente": "270/3190/PPAA",
     *              "destino": {
     *                  "id": "7",
     *                  "idRue": "28",
     *                  "nombreCompleto": "GALVAN IRINA ESTEFANIA",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "27455992112",
     *                  "email": "destino@algo.com"",
     *                  "idLocGT": "3101",
     *                  "localidad": "CPO. PAULETTI",
     *                  "departamento": "EMPEDRADO",
     *                  "provincia": "CORRIENTES"
     *              },
     *              "transporte": {
     *                  "id": "64",
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
     *                  },
     *                  "acoplado": "",
     *                  "condNombre": "JULIAN AGUADA",
     *                  "condDni": "15911458"
     *              }
     *              "origen": {
     *                  "id": "18",
     *                  "idRue": "22",
     *                  "nombreCompleto": "PAMPA DEL ZORRO - PARAJE",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "23394011789",
     *                  "email": "origen@algo.com",
     *                  "idLocGT": "138",
     *                  "localidad": "PAMPA DEL ZORRO - PARAJE",
     *                  "departamento": "9 DE JULIO",
     *                  "provincia": "CHACO"
     *              },
     *              "fechaAlta": "2017-11-27 17:48:20.932",
     *              "fechaEmisionGuia": "2017-11-27 17:55:13.835",
     *              "fechaVencimiento": "2017-12-02 17:55:13.835",
     *              "fechaCierre": "2017-11-27 18:01:48.145",
     *              "estado": {
     *                  "id": "2",
     *                  "nombre": "CERRADA"
     *              }
     *          }
     *      }
     * @apiParamExample {json} Emplo de id
     *      {
     *          "id": "32"
     *      }
     * @apiDescription Método para actualizar una Guía existente. Obtiene la Guía correspondiente al id recibido 
     * mediante el método local find(Long id), actualiza solo el estado de la entidad recibida y la edita mediante 
     * el método local edit(Guia guia).
     * @apiSuccessExample Response exitosa:
     *     HTTP/1.1 200 OK
     *     {}
     * @apiError GuiaNoActualizada No se actualizó la Guía.
     * @apiErrorExample Respuesta de Error:
     *     HTTP/1.1 400 Not Modified
     *     {
     *       "error": "Hubo un error procesado la actualización en el Componente de Trazabilidad."
     *     }
     */       
    @PUT
    @Path("{id}")
    @Secured
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.cgl.Guia entity) {
        try{
            // obtengo la Guía
            Guia guia = guiaFacade.find(id);
            // obtengo y seteo el estado
            EstadoGuia estado = estadoFacade.find(entity.getEstado().getId());
            // actualizo la Guía con el Estado
            guia.setEstado(estado);
            // seteo la fecha de cierre solo si el estado es CERRADA
            if(entity.getEstado().getNombre().equals(ResourceBundle.getBundle("/Config").getString("GuiaCerrada"))){
                guia.setFechaCierre(entity.getFechaCierre());
            }
            // actualizo
            guiaFacade.edit(guia);
            // armo la respuesta exitosa
            return Response.ok().build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.notModified().entity("Hubo un error procesado la actualización en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }             
    }

    /**
     * @api {get} /guias/:id Ver una Guía según su id
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-prov/rest/guias/32 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único de la Guía
     * @apiDescription Método para obtener una Guía existente según el id remitido.
     * Obtiene la Guía mediante el método local find(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Guia} Guia Detalle de la Guía registrada.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *          {
     *              "id": "32",   
     *              "items": [
     *                  {"id": "150",
     *                  "nombreCientifico": "Anadenanthera.colubrina", 
     *                  "nombreVulgar": "HUILCO",
     *                  "clase": "POSTE",
     *                  "unidad": "UNIDAD",
     *                  "idEspecieTax": "3076",
     *                  "kilosXUnidad": "425",
     *                  "total": "3",
     *                  "totalKg": "1275",
     *                  "saldo": "3",
     *                  "saldoKg": "1275", 
     *                  "obs": "",
     *                  "codigoProducto": "null|Anadenanthera.colubrina|HUILCO|POSTE|UNIDAD|270/3190/PPAA|[Provincia]"},
     *                  {"id": "151",
     *                  "nombreCientifico": "Maclura tinctoria",
     *                  "nombreVulgar": "MORA AMARILLA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3126",
     *                  "kilosXUnidad": "800",
     *                  "total": "13",
     *                  "totalKg": "10400",
     *                  "saldo": "13",
     *                  "saldoKg": "10400",
     *                  "obs": "",
     *                  "codigoProducto": "null|Maclura tinctoria|MORA AMARILLA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"},
     *                  {"id": "152",
     *                  "nombreCientifico": "Myroxylon peruiferum",
     *                  "nombreVulgar": "QUINA COLORADA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3105",
     *                  "kilosXUnidad": "1190",
     *                  "total": "7",
     *                  "totalKg": "8330",
     *                  "saldo": "7",
     *                  "saldoKg": "8330",
     *                  "obs": "",
     *                  "codigoProducto": "null|Myroxylon peruiferum|QUINA COLORADA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"}
     *              ],
     *              "codigo": "TT-10-00003-2017",
     *              "numFuente": "270/3190/PPAA",
     *              "destino": {
     *                  "id": "7",
     *                  "idRue": "28",
     *                  "nombreCompleto": "GALVAN IRINA ESTEFANIA",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "27455992112",
     *                  "email": "destino@algo.com"",
     *                  "idLocGT": "3101",
     *                  "localidad": "CPO. PAULETTI",
     *                  "departamento": "EMPEDRADO",
     *                  "provincia": "CORRIENTES"
     *              },
     *              "transporte": {
     *                  "id": "64",
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
     *                  },
     *                  "acoplado": "",
     *                  "condNombre": "JULIAN AGUADA",
     *                  "condDni": "15911458"
     *              }
     *              "origen": {
     *                  "id": "18",
     *                  "idRue": "22",
     *                  "nombreCompleto": "PAMPA DEL ZORRO - PARAJE",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "23394011789",
     *                  "email": "origen@algo.com",
     *                  "idLocGT": "138",
     *                  "localidad": "PAMPA DEL ZORRO - PARAJE",
     *                  "departamento": "9 DE JULIO",
     *                  "provincia": "CHACO"
     *              },
     *              "fechaAlta": "2017-11-27 17:48:20.932",
     *              "fechaEmisionGuia": "2017-11-27 17:55:13.835",
     *              "fechaVencimiento": "2017-12-02 17:55:13.835",
     *              "fechaCierre": "2017-11-27 18:01:48.145",
     *              "estado": {
     *                  "id": "2",
     *                  "nombre": "CERRADA"
     *              }
     *          }
     * @apiError GuiaNotFound No existe guía registrada con ese id.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay guía registrada con el id recibido"
     *     }
     */    
    @GET
    @Path("{id}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Guia find(@PathParam("id") Long id) {
        return guiaFacade.find(id);
    }

    /**
     * @api {get} /guias/:id/items Ver los ítems de una Guía
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-provincia/rest/guias/32/items -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetItemsXGuia
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiParam {Long} id Identificador único del Departamento
     * @apiDescription Método para obtener las Localidades asociadas a un Departamento existente según el id remitido.
     * Obtiene las localidades mediante el método local getCentrosXDepto(Long id)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.ItemProductivo} ItemProductivo Listado de los Items productivos vinculados a la guía.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *      {
     *              "items": [
     *                  {"id": "150",
     *                  "nombreCientifico": "Anadenanthera.colubrina", 
     *                  "nombreVulgar": "HUILCO",
     *                  "clase": "POSTE",
     *                  "unidad": "UNIDAD",
     *                  "idEspecieTax": "3076",
     *                  "kilosXUnidad": "425",
     *                  "total": "3",
     *                  "totalKg": "1275",
     *                  "saldo": "3",
     *                  "saldoKg": "1275", 
     *                  "obs": "",
     *                  "codigoProducto": "null|Anadenanthera.colubrina|HUILCO|POSTE|UNIDAD|270/3190/PPAA|[Provincia]"},
     *                  {"id": "151",
     *                  "nombreCientifico": "Maclura tinctoria",
     *                  "nombreVulgar": "MORA AMARILLA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3126",
     *                  "kilosXUnidad": "800",
     *                  "total": "13",
     *                  "totalKg": "10400",
     *                  "saldo": "13",
     *                  "saldoKg": "10400",
     *                  "obs": "",
     *                  "codigoProducto": "null|Maclura tinctoria|MORA AMARILLA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"},
     *                  {"id": "152",
     *                  "nombreCientifico": "Myroxylon peruiferum",
     *                  "nombreVulgar": "QUINA COLORADA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3105",
     *                  "kilosXUnidad": "1190",
     *                  "total": "7",
     *                  "totalKg": "8330",
     *                  "saldo": "7",
     *                  "saldoKg": "8330",
     *                  "obs": "",
     *                  "codigoProducto": "null|Myroxylon peruiferum|QUINA COLORADA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"}
     *              ]
     *      }
     * @apiError ItemsNotFound No existen ítems registrados vinculados a la guía.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay ítems registradas vinculados a la Guía recibida."
     *     }
     */  
    @GET
    @Path("{id}/items")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ItemProductivo> findItemsByGuia(@PathParam("id") Long id){
        return itemFacade.getByIdGuia(id);
    }        

    /**
     * @api {get} /guias/query?codigo=:codigo,matricula=:matricula,destino=:destino Ver Guías según parámetros.
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-provincia/rest/guias/query?codigo=TT-10-00003-2017 -H "authorization: xXyYvWzZ"
     *     curl -X GET -d [PATH_SERVER]/:cgl-provincia/rest/guias/query?matricula=ABC-128 -H "authorization: xXyYvWzZ"
     *     curl -X GET -d [PATH_SERVER]/:cgl-provincia/rest/guias/query?destino=36251478523 -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetGuiaQuery
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     }
     * @apiParam {String} codigo Código de la Guía solicitada
     * @apiParam {String} matricula Matrícula del vehículo de transporte de los productos amparados por las Guía buscadas
     * @apiParam {String} destino cuit del destinatario de la guía
     * @apiDescription Método para obtener la/s  guía/s según un código, una matrícula de vehículo de tansporte o un destinatario.
     * Solo uno de los parámetros tendrá un valor y los restantes nulos.
     * Según el caso, obtiene las guías en cuestión con los métodos locales getExistente(String codigo), getByVehiculo(String matricula) o getEmitidasByDestinatario(Long destino)
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Guia} Guia Guía o listado de las Guías obtenidas.
     * @apiSuccessExample Respuesta exitosa:
     *     HTTP/1.1 200 OK
     *     {
     *       "guias": [
     *          {
     *              "id": "32",   
     *              "items": [
     *                  {"id": "150",
     *                  "nombreCientifico": "Anadenanthera.colubrina", 
     *                  "nombreVulgar": "HUILCO",
     *                  "clase": "POSTE",
     *                  "unidad": "UNIDAD",
     *                  "idEspecieTax": "3076",
     *                  "kilosXUnidad": "425",
     *                  "total": "3",
     *                  "totalKg": "1275",
     *                  "saldo": "3",
     *                  "saldoKg": "1275", 
     *                  "obs": "",
     *                  "codigoProducto": "null|Anadenanthera.colubrina|HUILCO|POSTE|UNIDAD|270/3190/PPAA|[Provincia]"},
     *                  {"id": "151",
     *                  "nombreCientifico": "Maclura tinctoria",
     *                  "nombreVulgar": "MORA AMARILLA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3126",
     *                  "kilosXUnidad": "800",
     *                  "total": "13",
     *                  "totalKg": "10400",
     *                  "saldo": "13",
     *                  "saldoKg": "10400",
     *                  "obs": "",
     *                  "codigoProducto": "null|Maclura tinctoria|MORA AMARILLA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"},
     *                  {"id": "152",
     *                  "nombreCientifico": "Myroxylon peruiferum",
     *                  "nombreVulgar": "QUINA COLORADA",
     *                  "clase": "ROLLO",
     *                  "unidad": "METRO CUBICO",
     *                  "idEspecieTax": "3105",
     *                  "kilosXUnidad": "1190",
     *                  "total": "7",
     *                  "totalKg": "8330",
     *                  "saldo": "7",
     *                  "saldoKg": "8330",
     *                  "obs": "",
     *                  "codigoProducto": "null|Myroxylon peruiferum|QUINA COLORADA|ROLLO|METRO CUBICO|270/3190/PPAA|[Provincia]"}
     *              ],
     *              "codigo": "TT-10-00003-2017",
     *              "numFuente": "270/3190/PPAA",
     *              "destino": {
     *                  "id": "7",
     *                  "idRue": "28",
     *                  "nombreCompleto": "GALVAN IRINA ESTEFANIA",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "27455992112",
     *                  "email": "destino@algo.com"",
     *                  "idLocGT": "3101",
     *                  "localidad": "CPO. PAULETTI",
     *                  "departamento": "EMPEDRADO",
     *                  "provincia": "CORRIENTES"
     *              },
     *              "transporte": {
     *                  "id": "64",
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
     *                  },
     *                  "acoplado": "",
     *                  "condNombre": "JULIAN AGUADA",
     *                  "condDni": "15911458"
     *              }
     *              "origen": {
     *                  "id": "18",
     *                  "idRue": "22",
     *                  "nombreCompleto": "PAMPA DEL ZORRO - PARAJE",
     *                  "tipoPersona": "Persona Física",
     *                  "cuit": "23394011789",
     *                  "email": "origen@algo.com",
     *                  "idLocGT": "138",
     *                  "localidad": "PAMPA DEL ZORRO - PARAJE",
     *                  "departamento": "9 DE JULIO",
     *                  "provincia": "CHACO"
     *              },
     *              "fechaAlta": "2017-11-27 17:48:20.932",
     *              "fechaEmisionGuia": "2017-11-27 17:55:13.835",
     *              "fechaVencimiento": "2017-12-02 17:55:13.835",
     *              "fechaCierre": "2017-11-27 18:01:48.145",
     *              "estado": {
     *                  "id": "2",
     *                  "nombre": "CERRADA"
     *              }
     *          }
     *        ]
     *     }
     * @apiError GuiasNotFound No existen guías registradas con esos parámetros.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay guías registradas con los parámetros recibidos"
     *     }
     */   
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findByQuery(@QueryParam("codigo") String codigo, @QueryParam("matricula") String matricula, @QueryParam("destino") Long destino) {
        List<Guia> result = new ArrayList<>();
        if(codigo != null){
            Guia guia = guiaFacade.getExistente(codigo);
            result.add(guia);
        }else if(matricula != null){
            result = guiaFacade.getByMatricula(matricula);
        }else if(destino != null){
            result = guiaFacade.getEmitidasByDestinatario(destino);
        }
        return result;
    }      

    /**
     * @api {get} /guias Ver todas las Guías
     * @apiExample {curl} Ejemplo de uso:
     *     curl -X GET -d [PATH_SERVER]/:cgl-provincia/rest/guias -H "authorization: xXyYvWzZ"
     * @apiVersion 1.0.0
     * @apiName GetGuias
     * @apiGroup Guia
     * @apiHeader {String} Authorization Token recibido al autenticar el usuario
     * @apiHeaderExample {json} Ejemplo de header:
     *     {
     *       "Authorization": "xXyYvWzZ"
     *     } 
     * @apiDescription Método para obtener un listado de las guias existentes.
     * Obtiene las guias mediante el método local findAll()
     * @apiSuccess {ar.gob.ambiente.sacvefor.servicios.cgl.Guia} Guia Listado con todas las Guías registradas.
     * @apiError GuiasNotFound No existen Guías registradas.
     * @apiErrorExample Respuesta de error:
     *     HTTP/1.1 400 Not Found
     *     {
     *       "error": "No hay guías registradas"
     *     }
     */      
    @GET
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findAll() {
        return guiaFacade.findAll();
    }
    
    @GET
    @Path("{from}/{to}")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return guiaFacade.findRange(new int[]{from, to});
    }
    
    @GET
    @Path("count")
    @Secured
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(guiaFacade.count());
    }
}
