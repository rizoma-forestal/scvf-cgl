
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la versión 2 de TRAZ
 * Que retornan las imágenes del logo de cada componente local y los martillos de inmuebles y obrajeros
 * @author rincostante
 */
@Stateless
@Path("imagenes")
public class ImagenesTrazFacadeREST {
    
    static final Logger LOG = Logger.getLogger(ImagenesTrazFacadeREST.class);
    
    @EJB
    private GuiaFacade guiaFacade;
    
    @EJB
    private AutorizacionFacade autFacade;
    
    /**
     * Método que obtiene el martillo del primer obrajero vinculado a una Guía madre (fiscalización)
     * Si la guía no existe retrona NOT_FOUND
     * Si la guía no tiene obrajeros o tiene pero sin martillo, retorna el martillo "sinObrajero"
     * @param cod_guia_madre String código de la guía de fiscalización de la que se busca el martillo del obrajero
     * @return Response con la imagen 
     */
    @GET
    @Path("martillos/obrajeros/{cod_guia_madre}")
    @Secured
    @Produces("image/jpeg, image/png")
    public Response getMartilloObrajero(@PathParam("cod_guia_madre") String cod_guia_madre){
        String pathMartillo = "";
        Guia g = guiaFacade.getExistente(cod_guia_madre);
        if(g != null){
            // si existe la guía busco los obrajeros
            if(!g.getObrajeros().isEmpty()){
                // si tiene obrajeros verifico que tenga martillo y lo seteo
                if(g.getObrajeros().get(0).getNombreArchivo() != null){
                    pathMartillo = g.getObrajeros().get(0).getRutaArchivo() + g.getObrajeros().get(0).getNombreArchivo();
                }
            }
            if("".equals(pathMartillo)){
                // si no hay martillo o no hay obrajero, obtengo el martillo "sinObrajero"
                pathMartillo = ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                                ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + 
                                ResourceBundle.getBundle("/Config").getString("MrtSinObrajero");
            }
        }
        File repositoryFile = new File(pathMartillo);
        return returnFile(repositoryFile, cod_guia_madre);
    }
    
    /**
     * Método que obtiene el martillo del primer inmueble vinculado a la Autorización
     * Si la autorización no existe retrona NOT_FOUND
     * Si el inmueble de la autorización no tiene martillo, retorna el martillo "sinMartillo"
     * @param cod_autorizacion String código de la autorizaciónde la que se busca el martillo del inmueble
     * @return Response con la imagen 
     */
    @GET
    @Path("martillos/inmuebles")
    @Secured
    @Produces("image/jpeg, image/png")
    public Response getMaritilloInmueble(@QueryParam("cod_autorizacion") String cod_autorizacion){
        String pathMartillo = "";
        Autorizacion aut = autFacade.getExistente(cod_autorizacion);
        if(aut != null){
            // si existe la autorizacion, verifico que el primer inmueble tenga martillo
            if(aut.getInmuebles().get(0).getNombreArchivo() != null){
                pathMartillo = aut.getInmuebles().get(0).getRutaArchivo() + aut.getInmuebles().get(0).getNombreArchivo();
            }
            if("".equals(pathMartillo)){
                // si no hay martillo, obtengo el martillo "sinMartillo"
                pathMartillo = ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                                ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + 
                                ResourceBundle.getBundle("/Config").getString("MrtSinMartillo");
            }
        }
        File repositoryFile = new File(pathMartillo);
        return returnFile(repositoryFile, cod_autorizacion);        
    }
    
    /**
     * Método que obtiene el logo de una Autoridad local del Componente de gestión local
     * correspondiente.
     * @return Response con la imagen
     */
    @GET
    @Path("logos")
    @Secured
    @Produces("image/jpeg, image/png")    
    public Response getLogoCgl(){
        String codigo = ResourceBundle.getBundle("/Config").getString("Provincia");
        String pathLogo = ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                                ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + "logo.jpg";
        File repositoryFile = new File(pathLogo);
        return returnFile(repositoryFile, codigo);     
    }
    
    /**
     * Método estático que ejecuta el retorno del archivo o del mensaje, si no existe
     * @param file archivo con la imagen
     * @param codigo código del objeto desde el cual se busca la imagen
     * @return Response con el retorno para el método
     */
    public static Response returnFile(File file, String codigo){
        if(!file.exists()){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No hay guía o autorización para obtener martillo con el código remitido. " + codigo )
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        try {
            return Response.ok(new FileInputStream(file)).build();
        }catch(FileNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Hubo un error al obtener la imagen. " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}
