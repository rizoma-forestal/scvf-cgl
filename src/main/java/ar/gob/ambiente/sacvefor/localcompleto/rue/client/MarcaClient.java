
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso MarcaFacadeREST de la API de Registro de Entidades (RUE)
 * [vehiculos_marcas]<br>
 * USAGE:
 * <pre>
 *        MarcaClient client = new MarcaClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * Cliente para la entidad Marca del API REST del servicio de Registro Unico de Entidades del SACVeFor (RUE)
 * @author rincostante
 */
public class MarcaClient {

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
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerEntidades") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlEntidades");

    /**
     * Constructor que instancia el cliente y WebTarget
     */
    public MarcaClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("vehiculos_marcas");
    }

    /**
     * Método para obtener los modelos relacionados a una marca de vehículos según el id de la Marca en formato XML
     * GET/vehiculos_marcas/id/modelos.
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Modelo
     * @param id String id de la marca
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Modelo> modelos vinculados a la marca remitida 
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findModelosByMarca_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/modelos", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener los modelos relacionados a una marca de vehículos según el id de la Marca en formato JSON
     * GET/vehiculos_marcas/id/modelos.
     * El token irá incluído en el header
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Modelo
     * @param id String id de la marca
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Modelo> modelos vinculados a la marca remitida 
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findModelosByMarca_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/modelos", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para editar una Marca del Registro único de entidades (RUE) según su id en formato XML
     * PUT /vehiculos_marcas/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Marca Entidad Marca para encapsular los datos de la Marca que se quiere editar
     * @param id String id de la Marca que se quier editar
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
     * Método para editar una Marca del Registro único de entidades (RUE) según su id en formato JSON
     * PUT /vehiculos_marcas/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Marca Entidad Marca para encapsular los datos de la Marca que se quiere editar
     * @param id String id de la Marca que se quier editar
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
     * Método que obtiene una marca registrada habilitada según su id en formato XML
     * GET /vehiculos_marcas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Marca
     * @param id String id de la Marca a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Marca marca obtenida según el id remitido
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T find_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    /**
     * Método que obtiene una marca registrada habilitada según su id en formato JSON
     * GET /vehiculos_marcas/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Marca
     * @param id String id de la Marca a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Marca marca obtenida según el id remitido
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
     * Método para crear una Marca para su control en el RUE. En formato XML
     * POST /vehiculos_marcas
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Marca Entidad Marca para encapsular los datos de la Marca que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Marca creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    /**
     * Método para crear una Marca para su control en el RUE. En formato JSON
     * POST /vehiculos_marcas
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Marca Entidad Marca para encapsular los datos de la Marca que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Marca creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    /**
     * Método para obtener una marca según su nombre en formato XML
     * GET /vehiculos_marcas?name=:name
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Marca
     * @param name String cadena a buscar dentro del nombre de la Marca
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Marca> marcas que contien en su nombre la cadena recibida como parámetro
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByName_XML(Class<T> responseType, String name, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (name != null) {
            resource = resource.queryParam("name", name);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener una marca según su nombre en formato JSON
     * GET /vehiculos_marcas?name=:name
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Marca
     * @param name String cadena a buscar dentro del nombre de la Marca
     * @param token String token recibido al validar el usuario en la API 
     * @return List<Marca> marcas que contien en su nombre la cadena recibida como parámetro
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByName_JSON(Class<T> responseType, String name, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (name != null) {
            resource = resource.queryParam("name", name);
        }
        resource = resource.path("query");
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene todos las Marcas registradas en formato XML
     * GET /vehiculos_marcas
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
     * Método que obtiene todos las Marcas registradas en formato JSON
     * GET /vehiculos_marcas
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

    public void remove(String id, String token) throws ClientErrorException {
        webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id}))
                .request()
                .header(HttpHeaders.AUTHORIZATION, token)
                .delete();
    }

    /**
     * Método para cerrar el cliente
     */  
    public void close() {
        client.close();
    }
}
