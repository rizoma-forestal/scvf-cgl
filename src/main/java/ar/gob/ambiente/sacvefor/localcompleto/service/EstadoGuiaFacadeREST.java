
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad EstadoGuia
 * @author rincostante
 */
@Stateless
@Path("estadosguia")
public class EstadoGuiaFacadeREST {

    @EJB
    private EstadoGuiaFacade estadoFacade;
    
    /**
     * Método para obtener un EstadoGuia según si id
     * Ej: [PATH]/estadosguia/1
     * @param id: id del EstadoGuia a obtener
     * @return 
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public EstadoGuia find(@PathParam("id") Long id) {
        return estadoFacade.find(id);
    }

    /**
     * Método para obtener los EstadoGuia habilitados
     * Ej: [PATH]/estadosguia
     * @return 
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findAll() {
        return estadoFacade.getHabilitados();
    }
    
    /**
     * Método que retorna un Estdo según el parámetro de consulta
     * Por ahora será el nombre
     * @param nombre : Nombre del Estado a consultar
     * Ej: [PATH]/estadosguia/query?nombre=CERRADA
     * @return 
     */        
    @GET
    @Path("/query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findByQuery(@QueryParam("nombre") String nombre) {
        List<EstadoGuia> result = new ArrayList<>();
        if(nombre != null){
            EstadoGuia estado = estadoFacade.getExistente(nombre);
            result.add(estado);
        }
        return result;
    }       

    /**
     * Método que obtiene un listado de EstadoGuia cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/estadosguia/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<EstadoGuia> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return estadoFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de los Usuarios registrados
     * Ej: [PATH]/estadosguia/count
     * @return 
     */
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(estadoFacade.count());
    }
}
