
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioApiFacade;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Recurso para validar usuario de acceso a la API y devolver el token
 * @author rincostante
 */
@Path("usuarioapi")
public class UsuarioApiResource {
    
    @EJB
    private UsuarioApiFacade usApiFacade;    
    
    /**
     * @api {get} /login/ Obtener token
     * @apiVersion 1.0.0
     * @apiName GetUsuarioApi
     * @apiGroup UsuarioApi
     * 
     * @apiDescription Método para obtener el token para incluir en los header de los request a efectuar por parte del cliente.
     * Recibe un nombre de usuarioApi, valida que esté registrado correctamente con el método local validarUsuarioApi(String user) y, de ser así,  
     * genera un token y lo devuelve al usuario.
     *
     * @apiParam {String} nombre Nombre del usuarioApi autorizado para consumir el servicio.
     *
     * @apiSuccess {String} authorization  Token de autenticación para enviar como header en los métodos de consulta.
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "authorization": "{{token}}"
     *     }
     *
     * @apiError UsuarioApiInvalido Mensaje vacío.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 404 Not Authorized
     *     {}
     */          
    @GET
    @Path("login")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response authenticateUser(@QueryParam("user") String user){
        
        // valido que el usuario esté registrado con acceso a la API
        if(usApiFacade.validarUsuarioApi(user)){
            // obtengo el token para el usuario
            String token = issueToken(user);
            // envío el token en la cabecera del mensaje
            return Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
        }else{
            // envío un mensaje de error
            return Response.noContent().build();
        }
    }
    
    /**
     * Método privado para generar el token con vigencia de 60 min.
     * Codificado mediante la KEY del RestSecurityFilter
     * @param login String nombre del usuario
     * @return String token generado
     */
    private String issueToken(String login) {
        
    	//Calculamos la fecha de expiración del token
    	Date issueDate = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(issueDate);
    	calendar.add(Calendar.MINUTE, 60);
        Date expireDate = calendar.getTime();
        
	//Creamos el token
        String jwtToken = Jwts.builder()
        	.claim("rol", "a,b,c")
                .setSubject(login)
                .setIssuer(ResourceBundle.getBundle("/Config").getString("Server"))
                .setIssuedAt(issueDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, RestSecurityFilter.KEY)
                .compact();
        return jwtToken;        
    }
}
