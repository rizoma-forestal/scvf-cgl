
package ar.gob.ambiente.sacvefor.localcompleto.ctrl.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

/**
 * Cliente REST Jersey generado para el recurso ParametricaFacadeREST de la API de Control y Verificación<br>
 * [parametricas]<br>
 * USAGE:
 * <pre>
 *        ParamCtrlClient client = new ParamCtrlClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * Cliente para el servicio de consulta de Paramétricas del API-CCV
 * @author rincostante
 */
public class ParamCtrlClient {

    /**
     * Variable privada: WebTarget path de acceso a la API de Control y Verificación
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
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerServicios") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlCtrlVerif");

    /**
     * Constructor que instancia el cliente y el webTarget
     */
    public ParamCtrlClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("parametricas");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para obtener uno o más Paramétricas según un parámetro que podrá ser el tipo de paramétrica, 
     * o su nombre. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato XML
     * GET /parametricas/query?tipoParam=:tipoParam
     * GET /parametricas/query?nombre=:nombre
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param tipoParam String nombre del tipo de paramétrica/s buscada/s
     * @param nombre String Nombre de la Paramétrica buscada
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Parametrica paramétrica o paramétricas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_XML(Class<T> responseType, String tipoParam, String nombre, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        if (tipoParam != null) {
            resource = resource.queryParam("tipoParam", tipoParam);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más Paramétricas según un parámetro que podrá ser el tipo de paramétrica, 
     * o su nombre. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato JSON
     * GET /parametricas/query?tipoParam=:tipoParam
     * GET /parametricas/query?nombre=:nombre
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param tipoParam String nombre del tipo de paramétrica/s buscada/s
     * @param nombre String Nombre de la Paramétrica buscada
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Parametrica paramétrica o paramétricas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_JSON(Class<T> responseType, String tipoParam, String nombre, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        if (tipoParam != null) {
            resource = resource.queryParam("tipoParam", tipoParam);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene una Paramétrica registrada habilitada según su id en formato XML
     * GET /parametricas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param id String id de la Parametrica a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Parametrica paramétrica obtenida según el id remitido
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
     * Método que obtiene una Paramétrica registrada habilitada según su id en formato JSON
     * GET /parametricas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Parametrica
     * @param id String id de la Parametrica a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Parametrica paramétrica obtenida según el id remitido
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
     * Método que obtiene todos las guías registradas en formato XML
     * GET /parametricas
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
     * Método que obtiene todos las guías registradas en formato JSON
     * GET /parametricas
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

    
    public void close() {
        client.close();
    }
    
}
