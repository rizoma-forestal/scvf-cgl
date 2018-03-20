
package ar.gob.ambiente.sacvefor.localcompleto.trazabilidad.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Cliente REST Jersey generado para el recurso UsuarioApiResource de la API sacvefor-gestionTrazabilidad<br>
 * [usuarioapi]<br>
 * USAGE:
 * <pre>
 *        UsuarioApiClient client = new UsuarioApiClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author rincostante
 */
public class UsuarioApiClient {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = ResourceBundle.getBundle("/Config").getString("ServerServicios") + "/"
            + "" + ResourceBundle.getBundle("/Config").getString("UrlTrazabilidad");    

    public UsuarioApiClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("usuarioapi");
    }

    public <T> T authenticateUser_XML(Class<T> responseType, String user) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (user != null) {
            resource = resource.queryParam("user", user);
        }
        resource = resource.path("login");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    public <T> T authenticateUser_JSON(Class<T> responseType, String user) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (user != null) {
            resource = resource.queryParam("user", user);
        }
        resource = resource.path("login");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
