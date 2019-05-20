
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Cliente REST Jersey generado para el recurso DomicilioFacadeREST de la API de Registro de Entidades (RUE)
 * [domicilios]<br>
 * USAGE:
 * <pre>
 *        DomicilioClient client = new DomicilioClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rincostante
 */
public class DomicilioClient {

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
    public DomicilioClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("domicilios");
    }

    /**
     * Método para crear un Domicilio para su control en el RUE. En formato XML
     * POST /domicilios
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Domicilio Entidad Domicilio para encapsular los datos del domicilio que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Domicilio creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */    
    public Response create_XML(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_XML), Response.class);
    }

    /**
     * Método para crear un Domicilio para su control en el RUE. En formato JSON
     * POST /domicilios
     * @param requestEntity ar.gob.ambiente.sacvefor.servicios.rue.Domicilio Entidad Domicilio para encapsular los datos del domicilio que se quiere registrar
     * @param token String token recibido previamente al validar el usuario en la API. Irá en el header.
     * @return javax.ws.rs.core.Response con el resultado de la operación que incluye la url de acceso al Domicilio creado mediante GET
     * @throws ClientErrorException Excepcion a ejecutar
     */    
    public Response create_JSON(Object requestEntity, String token) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Método para cerrar el cliente
     */       
    public void close() {
        client.close();
    }
}
