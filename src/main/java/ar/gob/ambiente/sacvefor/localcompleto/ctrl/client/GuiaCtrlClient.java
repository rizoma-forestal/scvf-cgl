
package ar.gob.ambiente.sacvefor.localcompleto.ctrl.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso GuiaFacadeREST de la API sacvefor-controlVerificacion [guias]<br>
 * USAGE:
 * <pre>
        GuiaCtrlClient client = new GuiaCtrlClient();
        Object response = client.XXX(...);
        // do whatever with response
        client.close();
 </pre>
 * Cliente para el servicio de gestión de Guías a controlar del API sacvefor-controlVerificacion
 * @author rincostante
 */
public class GuiaCtrlClient {

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
    public GuiaCtrlClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("guias");
    }
    
    public String countREST() throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path("count");
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }
    
    /**
     * Método para editar una Guía del componente de Control y Verificación según su id en formato XML
     * PUT /guias/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia Entidad Guía para encapsular los datos de la Guía que se quiere editar
     * @param id String id de la Guía que se quier editar
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
     * Método para editar una Guía del componente de Control y Verificación según su id en formato JSON
     * PUT /guias/:id
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia Entidad Guía para encapsular los datos de la Guía que se quiere editar
     * @param id String id de la Guía que se quier editar
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
     * Método para obtener uno o más Registros de Control realizados a una o más guías según un parámetro que podrá ser el código de la Guía, 
     * la matrícula del transporte o la Provincia de emisión. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato XML
     * GET /guias/query?codigo=:codigo
     * GET /guias/query?matricula=:matricula
     * GET /guias/query?provincia=:provincia
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Guia
     * @param codigo String identificación del código único de la Guía
     * @param matricula String Matrícula del Vehículo de transporte
     * @param provincia String Provincia desde la cual se emitió la Guía
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Guia guia o guias obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T findByQuery_XML(Class<T> responseType, String codigo, String matricula, String provincia, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (codigo != null) {
            resource = resource.queryParam("codigo", codigo);
        }
        if (matricula != null) {
            resource = resource.queryParam("matricula", matricula);
        }
        if (provincia != null) {
            resource = resource.queryParam("provincia", provincia);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método para obtener uno o más Registros de Control realizados a una o más guías según un parámetro que podrá ser el código de la Guía, 
     * la matrícula del transporte o la Provincia de emisión. Solo se podrá pasar el valor de un parámetro, los restantes deberán ser nulos.
     * En formato XML
     * GET /guias/query?codigo=:codigo
     * GET /guias/query?matricula=:matricula
     * GET /guias/query?provincia=:provincia
     * @param <T> Tipo genérico
     * @param responseType Tipo que en el que se setearán los datos serializados obtenidos, en este caso será Guia
     * @param codigo String identificación del código único de la Guía
     * @param matricula String Matrícula del Vehículo de transporte
     * @param provincia String Provincia desde la cual se emitió la Guía
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return Guia guia o guias obtenida/s según los parámetros enviados
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T findByQuery_JSON(Class<T> responseType, String codigo, String matricula, String provincia, String token) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (codigo != null) {
            resource = resource.queryParam("codigo", codigo);
        }
        if (matricula != null) {
            resource = resource.queryParam("matricula", matricula);
        }
        if (provincia != null) {
            resource = resource.queryParam("provincia", provincia);
        }
        resource = resource.path("query");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .get(responseType);
    }

    /**
     * Método que obtiene una Guía registrada habilitada según su id en formato XML
     * GET /guias/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Guia
     * @param id String id de la Guía a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Guia guía obtenida según el id remitido
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
     * Método que obtiene una Guía registrada habilitada según su id en formato JSON
     * GET /guias/:id
     * @param <T> Tipo genérico
     * @param responseType Entidad en la que se setearán los datos serializados obtenidos, en este caso será Guia
     * @param id String id de la Guía a obtener
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return <T> Guia guía obtenida según el id remitido
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
     * Método para crear una Guía para su control en el Componente de control y verificación. En formato XML
     * POST /guias
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia Entidad Guía para encapsular los datos de la Guía que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Guía creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
    }

    /**
     * Método para crear una Guía para su control en el Componente de control y verificación. En formato JSON
     * POST /guias
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.ctrlverif.Guia Entidad Guía para encapsular los datos de la Guía que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso a la Guía creada mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Método que obtiene todos las guías registradas en formato XML
     * GET /guias
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
     * GET /guias
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
