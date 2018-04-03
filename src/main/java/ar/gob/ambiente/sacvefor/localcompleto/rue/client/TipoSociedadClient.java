
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;

/**
 * Cliente REST Jersey generado para el recurso TipoSociedadFacadeREST de la API de Registro de Entidades (RUE)
 * [tiposociedad]<br>
 * USAGE:
 * <pre>
 *        TipoSociedadClient client = new TipoSociedadClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * Cliente para la entidad Tipo de Sociedad del API REST del servicio de Registro Unico de Entidades del SACVeFor (RUE)
 * @author Administrador
 */
public class TipoSociedadClient {

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
            + "" + ResourceBundle.getBundle("/Config").getString("UrlEntidades");

    /**
     * Constructor que instancia el cliente y WebTarget
     */
    public TipoSociedadClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("tiposociedad");
    }

    /**
     * Método para obtener las personas según el id del tipo de sociedad. En formato XML
     * GET /tiposociedad/:id/personas
     * @param <T> Tipo genérico
     * @param responseType Tipo en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param id String id del tipo de sociedad remitido
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según el tipo de sociedad
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findPersonasByEntidad_XML(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/personas", new Object[]{id}));
        return resource
                .request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener las personas según el id del tipo de sociedad. En formato JSON
     * GET /tiposociedad/:id/personas
     * @param <T> Tipo genérico
     * @param responseType Tipo en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param id String id del tipo de sociedad remitido
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según el tipo de sociedad
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findPersonasByEntidad_JSON(Class<T> responseType, String id, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/personas", new Object[]{id}));
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
     * Método que obtiene un Tipo de Sociedad registrado habilitado según su id en formato XML
     * GET /tiposociedad/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será TipoSociedad
     * @param id String id del TipoSociedad a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> TipoSociedad tipo de sociedad obtenido según el id remitido
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
     * Método que obtiene un Tipo de Sociedad registrado habilitado según su id en formato JSON
     * GET /tiposociedad/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será TipoSociedad
     * @param id String id del TipoSociedad a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> TipoSociedad tipo de sociedad obtenido según el id remitido
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
     * Método para obtener un tipo de sociedad según su nombre en formato XML
     * GET /tiposociedad?name=:name
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será TipoSociedad
     * @param name String cadena a buscar dentro del nombre del Tipo de sociedad
     * @param token String token recibido al validar el usuario en la API 
     * @return List<TipoSociedad> tipos de sociedad que contien en su nombre la cadena recibida como parámetro
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByName_XML(Class<T> responseType, String name, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (name != null) {
            resource = resource.queryParam("name", name);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener un tipo de sociedad según su nombre en formato JSON
     * GET /tiposociedad?name=:name
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será TipoSociedad
     * @param name String cadena a buscar dentro del nombre del Tipo de sociedad
     * @param token String token recibido al validar el usuario en la API 
     * @return List<TipoSociedad> tipos de sociedad que contien en su nombre la cadena recibida como parámetro
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByName_JSON(Class<T> responseType, String name, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (name != null) {
            resource = resource.queryParam("name", name);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene todos las guías registradas en formato XML
     * GET /tiposociedad
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
     * GET /tiposociedad
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
