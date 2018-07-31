
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso VehiculoFacadeREST de la API RUE
 * [vehiculos]<br>
 * USAGE:
 * <pre>
 *        VehiculoClient client = new VehiculoClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * Cliente para la entidad Vehiculo del API REST del servicio de Registro Unico de Entidades del SACVeFor (RUE)
 * @author rincostante
 */
public class VehiculoClient {

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
    public VehiculoClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("vehiculos");
    }

    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Método para editar un Vehículo del Registro únicoVehiculo de entidades (RUE) según su id en formato XML
     * PUT /vehiculos/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue. Entidad Vehiculo para encapsular los datos del Vehículo que se quiere editar
     * @param id String id de la Vehiculo que se quier editar
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
     * Método para editar un Vehículo del Registro únicoVehiculo de entidades (RUE) según su id en formato JSON
     * PUT /vehiculos/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue. Entidad Vehiculo para encapsular los datos del Vehículo que se quiere editar
     * @param id String id de la Vehiculo que se quier editar
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
     * Método para obtener uno o más vehículos según su matrícula, el cuit de su titular y su condición de habilitada.
     * Si tiene cuit, los restantes serán nulos, si tiene matrícula, el cuit será nulo y podrá tener habilitado, si no especifica habilitado
     * devolverá todos. Si tiene habilitado, el cuit será nulo y podrá tener la matrícula, 
     * si no especifica matrícula devolverá todos.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * GET /vehiculos/query?matricula=:matricula,hab=:hab
     * GET /vehiculos/query?matricula=:matricula
     * GET /vehiculos/query?cuit=:cuit
     * GET /vehiculos/query?hab=:hab
     * En formato XML
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param cuit String cuit del titular del vehículo
     * @param matricula String matrícula del vehículo
     * @param hab String condición de habilitado de la/s persona/s
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_XML(Class<T> responseType, String cuit, String matricula, String hab, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (matricula != null) {
            resource = resource.queryParam("matricula", matricula);
        }
        if (hab != null) {
            resource = resource.queryParam("hab", hab);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más vehículos según su matrícula, el cuit de su titular y su condición de habilitada.
     * Si tiene cuit, los restantes serán nulos, si tiene matrícula, el cuit será nulo y podrá tener habilitado, si no especifica habilitado
     * devolverá todos. Si tiene habilitado, el cuit será nulo y podrá tener la matrícula, 
     * si no especifica matrícula devolverá todos.
     * Además de los parámetros deberá agregar el token obtenido luego de validar el usuario en la API
     * En formato JSON
     * GET /vehiculos/query?matricula=:matricula,hab=:hab
     * GET /vehiculos/query?matricula=:matricula
     * GET /vehiculos/query?cuit=:cuit
     * GET /vehiculos/query?hab=:hab
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Persona
     * @param cuit String cuit del titular del vehículo
     * @param matricula String matrícula del vehículo
     * @param hab String condición de habilitado de la/s persona/s
     * @param token String token recibido al validar el usuario en la API
     * @return Persona persona o personas obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_JSON(Class<T> responseType, String cuit, String matricula, String hab, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (cuit != null) {
            resource = resource.queryParam("cuit", cuit);
        }
        if (matricula != null) {
            resource = resource.queryParam("matricula", matricula);
        }
        if (hab != null) {
            resource = resource.queryParam("hab", hab);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene un Vehículo registrado habilitado según su id en formato XML
     * GET /vehiculos/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Vehículo
     * @param id String id del Vehículo a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Vehiculo vehículo obtenida según el id remitido
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
     * Método que obtiene un Vehículo registrado habilitado según su id en formato JSON
     * GET /vehiculos/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Vehículo
     * @param id String id del Vehículo a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Vehiculo vehículo obtenida según el id remitido
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
     * Método para crear un Vehículo para su control en el RUE. En formato XML
     * POST /vehiculos
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Vehículo Entidad Vehículo para encapsular los datos del Vehículo que se quiere registrar
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Vehículo creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML));
    }

    /**
     * Método para crear un Vehículo para su control en el RUE. En formato JSON
     * POST /vehiculos
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Vehículo Entidad Vehículo para encapsular los datos del Vehículo que se quiere registrar
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Vehículo creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON));
    }

    /**
     * Método que obtiene todos los vehículos registradas en formato XML
     * GET /vehiculos
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
     * Método que obtiene todos los vehículos registradas en formato JSON
     * GET /vehiculos
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
