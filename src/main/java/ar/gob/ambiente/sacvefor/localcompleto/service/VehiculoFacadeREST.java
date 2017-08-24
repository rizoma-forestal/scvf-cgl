
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Vehiculo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.VehiculoFacade;
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
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Vehiculo
 * @author rincostante
 */
@Stateless
@Path("vehiculos")
public class VehiculoFacadeREST {

    @EJB
    private VehiculoFacade vehiculoFacade;
    
    /**
     * Método para obtener un Vehículo según su id
     * Ej: [PATH]/vehiculos/1
     * @param id
     * @return 
     */    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Vehiculo find(@PathParam("id") Long id) {
        return vehiculoFacade.find(id);
    }

    /**
     * Método que retorna el Vehículo correspondiente a la Matrícula recibida
     * @param mat
     * Ej: [PATH]/vehiculos/query?mat=ABC-123
     * @return 
     */        
    @GET
    @Path("/query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Vehiculo findByMatricula(@QueryParam("mat") String mat) {
        Vehiculo result = new Vehiculo();
        if(mat != null){
            result = vehiculoFacade.getExistente(mat);
        }
        return result;
    }       
    
    /**
     * Método que retorna todos los Vehículos registrados
     * Ej: [PATH]/vehiculos
     * @return 
     */      
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Vehiculo> findAll() {
        return vehiculoFacade.findAll();
    }

    /**
     * Método que obtiene un listado de Vehículos cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/vehiculos/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */      
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Vehiculo> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return vehiculoFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de los Vehículos registradas
     * Ej: [PATH]/guias/count
     * @return 
     */      
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(vehiculoFacade.count());
    }
}
