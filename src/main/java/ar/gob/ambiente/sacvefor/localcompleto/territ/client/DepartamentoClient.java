
package ar.gob.ambiente.sacvefor.localcompleto.territ.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

/**
 * Cliente REST Jersey generado para el recurso DepartamentoFacadeREST de la API Territorial
 * [departamentos]<br>
 * USAGE:
 * <pre>
 *        DepartamentoClient client = new DepartamentoClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rincostante
 */
public class DepartamentoClient {
    
    /**
     * Variable privada: WebTarget path de acceso a la API específica de Departamentos
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
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerServiciosLectura") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlTerritorial");

    /**
     * Constructor que instancia el cliente y el webTarget
     */
    public DepartamentoClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("departamentos");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método que obtiene los Centros poblados de un departamento según su id en formato XML
     * GET /departamentos/:id/centrospoblados
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param id String id del Departamento
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByDepto_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/centrospoblados", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene los Centros poblados de un departamento según su id en formato JSON
     * GET /departamentos/:id/centrospoblados
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param id String id del Departamento
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByDepto_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/centrospoblados", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un Departamento registrado habilitado según su id en formato XML
     * GET /departamentos/:id
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Departamento
     * @param id Long id del Departamento a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Departamento Departamento obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un Departamento registrado habilitado según su id en formato JSON
     * GET /departamentos/:id
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Departamento
     * @param id Long id del Departamento a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Departamento Departamento obtenido según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
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
     * Método que obtiene todos los Departamentos registrados habilitados en formato XML
     * GET /centrospoblados
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_XML(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene todos los Departamentos registrados habilitados en formato JSON
     * GET /centrospoblados
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response resultados de la consulta
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findAll_JSON(Class<T> responseType, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }
    
    /**
     * Método para obtener un Departamento a partir de su nombre y el id de la Provincia a la que pertence, en formato XML
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Departamento
     * @param nombre String nombre del Departamento a buscar
     * @param id_prov String identificador de la Provincia a la cual pertenece el Departamento
     * @param token String token recibido al validar el usuario en la API
     * @return Departamento departamento obtenido según el nombre y el id de la Provincia enviados
     */
    public <T> T findByQuery_XML(Class<T> responseType, String nombre, String id_prov, String token){
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        if (id_prov != null){
            resource = resource.queryParam("id_prov", nombre);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }
    
    /**
     * Método para obtener un Departamento a partir de su nombre y el id de la Provincia a la que pertence, en formato JSON
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Departamento
     * @param nombre String nombre del Departamento a buscar
     * @param id_prov String identificador de la Provincia a la cual pertenece el Departamento
     * @param token String token recibido al validar el usuario en la API
     * @return Departamento departamento obtenido según el nombre y el id de la Provincia enviados
     */    
    public <T> T findByQuery_JSON(Class<T> responseType, String nombre, String id_prov, String token){
        WebTarget resource = webTarget;
        if (nombre != null) {
            resource = resource.queryParam("nombre", nombre);
        }
        if (id_prov != null){
            resource = resource.queryParam("id_prov", id_prov);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
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
