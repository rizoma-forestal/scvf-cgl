
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.entities.EstadoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.EstadoGuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la entidad Guia
 * @author rincostante
 */
@Stateless
@Path("guias")
public class GuiaFacadeREST {

    @EJB
    private GuiaFacade guiaFacade;
    @EJB
    private EstadoGuiaFacade estadoFacade;
    @EJB
    private ItemProductivoFacade itemFacade;

    /**
     * Método para editar una Guia existente. Solo se editará el estado y la fecha correspondiente
     * @param id : Id de la Guia a editar
     * @param entity : Guia a editar
     * @return : El código de repuesta que corresponda según se haya realizado o no la operación: 200 o 400
     */     
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response edit(@PathParam("id") Long id, ar.gob.ambiente.sacvefor.servicios.cgl.Guia entity) {
        try{
            // obtengo la Guía
            Guia guia = guiaFacade.find(id);
            // obtengo y seteo el estado
            EstadoGuia estado = estadoFacade.find(entity.getEstado().getId());
            // actualizo la Guía con el Estado
            guia.setEstado(estado);
            // seteo la fecha de cierre solo si el estado es CERRADA
            if(entity.getEstado().getNombre().equals(ResourceBundle.getBundle("/Config").getString("GuiaCerrada"))){
                guia.setFechaCierre(entity.getFechaCierre());
            }
            // actualizo
            guiaFacade.edit(guia);
            // armo la respuesta exitosa
            return Response.ok().build();
        }catch(IllegalArgumentException | UriBuilderException ex){
            // armo la respuesta de error
            return Response.notModified().entity("Hubo un error procesado la actualización en el Componente de Trazabilidad. " + ex.getMessage()).build();
        }             
    }

    /**
     * Método para obtener una Guía según su id
     * Ej: [PATH]/guias/1
     * @param id
     * @return 
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Guia find(@PathParam("id") Long id) {
        return guiaFacade.find(id);
    }
    
    /**
     * Método que devuelve todos las Items correspondientes a la Guía cuyo id se recibe como parámetro
     * Ej: [PATH]/guias/1/items
     * @param id
     * @return 
     */
    @GET
    @Path("{id}/items")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ItemProductivo> findItemsByGuia(@PathParam("id") Long id){
        return itemFacade.getByIdGuia(id);
    }        
    
    /**
     * Método que retorna las Guías según el parámetro de consulta
     * Solo podrá ser uno a la vez
     * @param codigo : Código de la Guía
     * @param matricula : Matrícula del Vehículo de transporte
     * @param destino : CUIT del Destinatario de la Guía
     * Ej: [PATH]/guias/query?cuitDestino=27184706526
     * @return 
     */        
    @GET
    @Path("/query")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findByQuery(@QueryParam("codigo") String codigo, @QueryParam("matricula") String matricula, @QueryParam("destino") Long destino) {
        List<Guia> result = new ArrayList<>();
        if(codigo != null){
            Guia guia = guiaFacade.getExistente(codigo);
            result.add(guia);
        }else if(matricula != null){
            result = guiaFacade.getByMatricula(matricula);
        }else if(destino != null){
            result = guiaFacade.getEmitidasByDestinatario(destino);
        }
        return result;
    }      

    /**
     * Método que retorna todos las Guías registradas
     * Ej: [PATH]/guias
     * @return 
     */      
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findAll() {
        return guiaFacade.findAll();
    }

    /**
     * Método que obtiene un listado de Guías cuyos id se encuentran entre los parámetros de inicio y fin recibidos
     * Ej: [PATH]/guias/1/10
     * @param from: parámetro 'desde' el cual se inicia el listado
     * @param to: parámetro 'hasta' el cual se completa el listado
     * @return 
     */        
    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Guia> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return guiaFacade.findRange(new int[]{from, to});
    }

    /**
     * Método que devuelve un entero con la totalidad de las Guías registradas
     * Ej: [PATH]/guias/count
     * @return 
     */        
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(guiaFacade.count());
    }
}
