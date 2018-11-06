
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Localidad
 * Generado para la integración con otros sistemas
 * @author rincostante
 */
@Stateless
@Path("localidades")
public class LocalidadFacadeREST {
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del TERR
     */
    private String strToken; 
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del TERR
     */       
    private Token token;   
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(PersonaFacadeREST.class);       
    /**
     * Variable privada: Cliente para la API Rest de validación de usuarios en el TERR
     */
    private UsuarioClient usClientTerr;   
    /**
     * Variable privada: Cleinte para la API Rest de Provincias
     */
    private ProvinciaClient provClient;
    /**
     * Variable privada: Cleinte para la API Rest de Departamentos
     */
    private DepartamentoClient deptoClient;
    /**
     * Variable privada: Cleinte para la API Rest de Localidades
     */
    private LocalidadClient localidadClient;
    
    @GET
    @Path("/query")
    @Secured
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CentroPoblado findByQuery(@QueryParam("nom_prov") String nom_prov, 
            @QueryParam("nom_depto") String nom_depto,
            @QueryParam("nom_loc") String nom_loc){
        CentroPoblado result = new CentroPoblado();
        // Busco la provincia a partir del nombre
        validarTokenTerr();
        provClient = new ProvinciaClient();
        Provincia prov = provClient.findByQuery_JSON(Provincia.class, nom_prov.toUpperCase(), token.getStrToken());
        provClient.close();
        // Si obtuve la Provincia busco el Departamento a partir del nombre recibido y el id de la Provincia obtenida
        if(prov != null){
            validarTokenTerr();
            deptoClient = new DepartamentoClient();
            Departamento depto = deptoClient.findByQuery_JSON(Departamento.class, nom_depto.toUpperCase(), prov.getId().toString(), token.getStrToken());
            deptoClient.close();
            // Si obtuve el Departamento busco la Localidad a partir del nombre recibido y el id del Departamento obtenido
            if(depto != null){
                localidadClient = new LocalidadClient();
                result = localidadClient.findByQuery_JSON(CentroPoblado.class, nom_loc.toUpperCase(), depto.getId().toString(), token.getStrToken());
            }
        }
        return result;
    }    
    
    /********************
     * Métodos privados *
     ********************/
    
    /**
     * Método privado que valida la existencia y vigencia del token de acceso al TERR
     * Utilizado en findByQuery()
     */
    private void validarTokenTerr(){
        if(token == null){
            getTokenTerr();
        }else try {
            if(!token.isVigente()){
                getTokenTerr();
            }
        } catch (IOException ex) {
            LOG.fatal("Hubo un error obteniendo la vigencia del token TERR." + ex.getMessage());
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en validarToken()
     */
    private void getTokenTerr(){
        try{
            usClientTerr = new ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient();
            Response responseUs = usClientTerr.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strToken = (String)lstHeaders.get(0); 
            token = new Token(strToken, System.currentTimeMillis());
            usClientTerr.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API TERR: " + ex.getMessage());
        }
    }   
}
