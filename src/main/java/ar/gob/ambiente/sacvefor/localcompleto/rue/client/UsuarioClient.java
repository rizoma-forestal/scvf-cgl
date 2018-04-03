
package ar.gob.ambiente.sacvefor.localcompleto.rue.client;

import java.util.ResourceBundle;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Cliente REST Jersey generado para el recurso UsuarioResource de la API RUE [usuario]
 * USAGE:
 * <pre>
        UsuarioClient client = new UsuarioClient();
        Object response = client.XXX(...);
        // do whatever with response
        client.close();
 </pre>
 *
 * @author rincostante
 */
public class UsuarioClient {

    /**
     * Variable privada: WebTarget path de acceso a la API específica de Usuarios de la API
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
     * Constructor que instancia el cliente y el webTarget
     */    
    public UsuarioClient() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("usuario");
    }

    /**
     * Método que obtiene un token para el usuario, previa autenticación del mismo. En formato XML
     * GET /usuario/login?user=:user
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param user String nombre del usuario a autenticar
     * @return <T> javax.ws.rs.core.Response resultados de la consulta con la metadata que incluye el token.
     * @throws ClientErrorException Excepcion a ejecutar
     */
    public <T> T authenticateUser_XML(Class<T> responseType, String user) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (user != null) {
            resource = resource.queryParam("user", user);
        }
        resource = resource.path("login");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(responseType);
    }

    /**
     * Método que obtiene un token para el usuario, previa autenticación del mismo. En formato JSON
     * GET /usuario/login?user=:user
     * @param <T> Tipo genérico
     * @param responseType javax.ws.rs.core.Response
     * @param user String nombre del usuario a autenticar
     * @return <T> javax.ws.rs.core.Response resultados de la consulta con la metadata que incluye el token.
     * @throws ClientErrorException Excepcion a ejecutar
     */ 
    public <T> T authenticateUser_JSON(Class<T> responseType, String user) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (user != null) {
            resource = resource.queryParam("user", user);
        }
        resource = resource.path("login");
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    /**
     * Método para cerrar el cliente
     */
    public void close() {
        client.close();
    }
}
