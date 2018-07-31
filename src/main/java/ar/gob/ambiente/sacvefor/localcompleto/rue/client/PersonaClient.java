
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso PersonaFacadeREST de la API de Registro de Entidades (RUE)
 * [personas]<br>
 * USAGE:
 * <pre>
 *        PersonaClient client = new PersonaClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * Cliente para la entidad Persona del API REST del servicio de Registro Unico de Entidades del SACVeFor (RUE)
 * @author Administrador
 */
public class PersonaClient {

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
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerEntidades") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlEntidades");

    /**
     * Constructor que instancia el cliente y WebTarget
     */
    public PersonaClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("personas");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para editar una Persona del Registro único de entidades (RUE) según su id en formato XML
     * PUT /personas/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Persona Entidad Persona para encapsular los datos de la Persona que se quiere editar
     * @param id String id de la Persona que se quier editar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response edit_XML(Object requestEntity, String id, String token) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    /**
     * Método para editar una Persona del Registro único de entidades (RUE) según su id en formato JSON
     * PUT /personas/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Persona Entidad Persona para encapsular los datos de la Persona que se quiere editar
     * @param id String id de la Persona que se quier editar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response edit_JSON(Object requestEntity, String id, String token) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    /**
     * Método para obtener una o más personas según su tipo, cuit y su condición de habilitada.
     * Si tiene cuit, los restantes serán nulos, si tiene tipo, el cuit será nulo y podrá tener habilitado, si no especifica habilitado
     * devolverá todos. Si tiene habilitado, el cuit será nulo y podrá tener el tipo, 
     * si no especifica tipo devolverá todos.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato XML
     * GET /personas/query?tipo=:tipo,hab=:hab
     * GET /personas/query?tipo=:tipo
     * GET /personas/query?cuit=:cuit
     * GET /personas/query?hab=:hab
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param tipo String tipo de persona: FISCA o JURIDICA
     * @param cuit String cuit de la persona
     * @param hab String condición de habilitado de la/s persona/s
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_XML(Class<T> responseType, String tipo, String cuit, String hab, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (tipo != null) {
            resource = resource.queryParam("tipo", tipo);
        }
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (hab != null) {
            resource = resource.queryParam("hab", hab);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener una o más personas según su tipo, cuit y su condición de habilitada.
     * Si tiene cuit, los restantes serán nulos, si tiene tipo, el cuit será nulo y podrá tener habilitado, si no especifica habilitado
     * devolverá todos. Si tiene habilitado, el cuit será nulo y podrá tener el tipo, 
     * si no especifica tipo devolverá todos.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato JSON
     * GET /personas/query?tipo=:tipo,hab=:hab
     * GET /personas/query?tipo=:tipo
     * GET /personas/query?cuit=:cuit
     * GET /personas/query?hab=:hab
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param tipo String tipo de persona: FISCA o JURIDICA
     * @param cuit String cuit de la persona
     * @param hab String condición de habilitado de la/s persona/s
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_JSON(Class<T> responseType, String tipo, String cuit, String hab, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (tipo != null) {
            resource = resource.queryParam("tipo", tipo);
        }
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (hab != null) {
            resource = resource.queryParam("hab", hab);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene una Persona registrada habilitada según su id en formato XML
     * GET /personas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param id String id de la Persona a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Persona persona obtenida según el id remitido
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
     * Método que obtiene una Persona registrada habilitada según su id en formato JSON
     * GET /personas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param id String id de la Persona a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Persona persona obtenida según el id remitido
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
     * Método para crear una Persona para su control en el RUE. En formato XML
     * POST /personas
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Persona Entidad Persona para encapsular los datos de la Persona que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Persona creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    /**
     * Método para crear una Persona para su control en el RUE. En formato JSON
     * POST /personas
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Persona Entidad Persona para encapsular los datos de la Persona que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Persona creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    /**
     * Método que obtiene todos las personas registradas en formato XML
     * GET /personas
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
     * Método que obtiene todos las personas registradas en formato JSON
     * GET /personas
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
     * Método para cerrar el cliente
     */   
    public void close() {
        client.close();
    }
}
