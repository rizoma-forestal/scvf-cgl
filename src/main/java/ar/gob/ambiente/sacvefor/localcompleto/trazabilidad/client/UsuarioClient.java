
package ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso UsuarioFacadeREST de la API de Trazabilidad
 * [usuarios]<br>
 * USAGE:
 * <pre>
 *        UsuarioClient client = new UsuarioClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 * Cliente para el servicio de gestión de Usuarios del API-TRAZ
 * @author rincostante
 */
public class UsuarioClient {

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
    public UsuarioClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("usuarios");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para obtener uno o más usuarios según su cuit y su jurisdicción.
     * Si tiene cuit, devolverá el usuario respectivo, si tiene jurisdicción devolverá todos los usuarios de la misma.
     * Solo se remitirá un parámetro y el restante será nulo.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato XML
     * GET /usuarios/query?cuit=:cuit
     * GET /personas/query?juris=:juris
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Usuario
     * @param cuit String cuit del usuario a buscar
     * @param juris String jurisdicción a la que pertenecen los usuarios buscados
     * @param token String token recibido al validar el usuario en la API
     * @return Usuario usuario o usuarios obtenido/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_XML(Class<T> responseType, String cuit, String juris, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (juris != null) {
            resource = resource.queryParam("juris", juris);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más usuarios según su cuit y su jurisdicción.
     * Si tiene cuit, devolverá el usuario respectivo, si tiene jurisdicción devolverá todos los usuarios de la misma.
     * Solo se remitirá un parámetro y el restante será nulo.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato JSON
     * GET /usuarios/query?cuit=:cuit
     * GET /usuarios/query?juris=:juris
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Usuario
     * @param cuit String cuit del usuario a buscar
     * @param juris String jurisdicción a la que pertenecen los usuarios buscados
     * @param token String token recibido al validar el usuario en la API
     * @return Usuario usuario o usuarios obtenido/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_JSON(Class<T> responseType, String cuit, String juris, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (juris != null) {
            resource = resource.queryParam("juris", juris);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un usuario registrado habilitado según su id en formato XML
     * GET /usuarios/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Usuario
     * @param id String id del usuario a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Usuario usuario obtenido según el id remitido
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
     * Método que obtiene un usuario registrado habilitado según su id en formato JSON
     * GET /usuarios/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Usuario
     * @param id String id del usuario a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Usuario usuario obtenido según el id remitido
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
     * Método para crear un Usuario en el componente de Trazabilidad. En formato XML
     * POST /usuarios
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario Entidad Usuario para encapsular los datos del Usuario que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Usuario creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
    }

    /**
     * Método para crear un Usuario en el componente de Trazabilidad. En formato JSON
     * POST /usuarios
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.trazabilidad.Usuario Entidad Usuario para encapsular los datos del Usuario que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Usuario creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Método que obtiene todos los usuarios registrados en formato XML
     * GET /usuarios
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
     * Método que obtiene todos los usuarios registrados en formato JSON
     * GET /usuarios
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
