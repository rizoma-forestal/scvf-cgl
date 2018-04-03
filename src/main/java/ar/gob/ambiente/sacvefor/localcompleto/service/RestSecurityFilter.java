
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.security.Key;
import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Filtro para cruzar cada llamada a un método securizado y validar token
 * @author rincostante
 */
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class RestSecurityFilter implements ContainerRequestFilter{
    
    /**
     * Clave para la encriptación
     */
    public static final Key KEY = MacProvider.generateKey();

    /**
     * Método para interceptar solicitudes a la API para validar el token del usuario
     * @param requestContext requerimiento del cliente
     * @throws IOException Gestión del error ante la no coincidencia entre el token recibido y el vigente
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        
        // Recupera la cabecera HTTP Authorization de la petición
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        // solo continúo si trajo un token
        if(authorizationHeader != null){
            try{
                // Extrae el token de la cabecera
                String token = authorizationHeader.substring("Bearer".length()).trim();

                // Valida el token utilizando la cadena secreta
                Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
            }catch(ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException ex){
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }else{
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
    
}
