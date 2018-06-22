
package ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso TipoParamFacadeREST de la API de Trazabilidad
 * [tipoparams]<br>
 * USAGE:
 * <pre>
 *        TipoParamClient client = new TipoParamClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * Cliente para el servicio de gestión de TipoParam del API-TRAZ
 * @author rincostante
 */
public class TipoParamClient {

    /**
     * Variable privada: WebTarget path de acceso a la API de RUE
     */
    private WebTarget webTarget;
    
    /**
     * Variable privada: Client cliente a setear a partir de webTarget
     */
    private Client client;
    
    /**
     * Variable privada estática y final: String url general de acceso al servicio.
     * A partir de datos configurados en archivo de propiedades
     */
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerTrazabilidad") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlTrazabilidad");

    /**
     * Constructor que instancia el cliente y WebTarget
     */
    public TipoParamClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("tipoparams");
    }

    /**
     * Método para obtener las paramétricas de un tipo determinado según el id del mismo en formato XML
     * GET/tipoparams/id/parametricas.
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param id String id de la del tipo de paramétrica
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Parametrica> paramétricas con el rol cuyo id se remite
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findParametricasByTipo_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/parametricas", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener las paramétricas de un tipo determinado según el id del mismo en formato JSON
     * GET/tipoparams/id/parametricas.
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param id String id de la del tipo de paramétrica
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Parametrica> paramétricas con el rol cuyo id se remite
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findParametricasByTipo_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/parametricas", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para editar un Tipo de Parametrica del componente de trazabilidad según su id en formato XML
     * PUT /tipoparams/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam Entidad TipoParam para encapsular los datos del tipo de paramétrica que se quiere editar
     * @param id String id de la TipoParam que se quier editar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response edit_XML(Object requestEntity, String id, String token) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
    }

    /**
     * Método para editar un Tipo de Parametrica del componente de trazabilidad según su id en formato JSON
     * PUT /tipoparams/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam Entidad TipoParam para encapsular los datos del tipo de paramétrica que se quiere editar
     * @param id String id de la TipoParam que se quier editar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response edit_JSON(Object requestEntity, String id, String token) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Método para obtener uno o más tipos de paramétricas según su nombre.
     * Además del nombre deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato XML
     * GET /tipoparams/query?nombre=:nombre
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será TipoParam
     * @param nombre String cadena que deberá contener el nombre del tipo de paramétrica a obtener
     * @param token String token recibido al validar el usuario en la API
     * @return TipoParam tipo de paramétrica o tipos obtenido/s según el nombre remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_XML(Class<T> responseType, String nombre, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más tipos de paramétricas según su nombre.
     * Además del nombre deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato JSON
     * GET /tipoparams/query?nombre=:nombre
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será TipoParam
     * @param nombre String cadena que deberá contener el nombre del tipo de paramétrica a obtener
     * @param token String token recibido al validar el usuario en la API
     * @return TipoParam tipo de paramétrica o tipos obtenido/s según el nombre remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_JSON(Class<T> responseType, String nombre, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un tipo de paramétrica registrado habilitado según su id en formato XML
     * GET /tipoparams/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será TipoParam
     * @param id String id del tipo de paramétrica a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> TipoParam tipo de parametrica obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un tipo de paramétrica registrado habilitado según su id en formato JSON
     * GET /tipoparams/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será TipoParam
     * @param id String id del tipo de paramétrica a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> TipoParam tipo de parametrica obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    public <T> T findRange_XML(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T findRange_JSON(Class<T> responseType, String from, String to) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{from, to}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    /**
     * Método para crear un tipo de paramétrica en el componente de trazabilidad. En formato XML
     * POST /tipoparams
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam Entidad TipoParam para encapsular los datos del tipo de paramétrica que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al tipo de paramétrica creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
    }

    /**
     * Método para crear un tipo de paramétrica en el componente de trazabilidad. En formato JSON
     * POST /tipoparams
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.TipoParam Entidad TipoParam para encapsular los datos del tipo de paramétrica que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al tipo de paramétrica creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Método que obtiene todos los tipos de paramétricas registradaos en formato XML
     * GET /tipoparams
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_XML(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene todos los tipos de paramétricas registradaos en formato XML
     * GET /tipoparams
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_JSON(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para cerrar el cliente
     */  
    public void close() {
        client.close();
    }
}
