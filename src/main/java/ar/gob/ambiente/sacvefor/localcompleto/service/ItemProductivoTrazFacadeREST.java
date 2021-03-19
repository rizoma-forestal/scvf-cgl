
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.dto.DetallePiezasCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.ItemProductivoCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.DetallePiezas;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ItemProductivoFacade;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la versión 2 de TRAZ
 * Para items productivos vinculados a Guías de fiscalización y Autorizaciones
 * @author rincostante
 */

@Stateless
@Path("items")
public class ItemProductivoTrazFacadeREST {
    
    static final Logger LOG = Logger.getLogger(GiuaTrazFacadeREST.class);
    
    @EJB
    private ItemProductivoFacade itemFacade;
    @EJB
    private AutorizacionFacade autFacade;
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Método que obtiene los items productivos de una Autorización según su id
     * Si recibe el parámetro valida_saldo en true solo retorna los que tengan un saldo mayor a 0
     * @param id_autorizacion Long identificación de la Autorización
     * @param valida_saldo boolean indica si valida saldo o no
     * @return Response con los items obtenidos
     */
    @GET
    @Path("acreditaciones")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response findPorAutorizacion(@QueryParam("id_autorizacion") Long id_autorizacion,
            @QueryParam("valida_saldo") @DefaultValue("false") boolean valida_saldo){
        
        // verifica el id_autorizacion
        if(id_autorizacion != null){
            // solo continúa si hay un id_autorizacion
            Autorizacion aut = autFacade.find(id_autorizacion);
            if(aut != null){
                // solo continúa si obtuvo la Autorización
                List<ItemProductivo> items = new ArrayList<>();
                items = itemFacade.getByAutorizacion(aut);
                if(!items.isEmpty()){
                    List<ItemProductivoCGLResponseDTO> itemsDTO = new ArrayList<>();
                    // si tiene items y valida saldo, verifico el saldo de cada producto y solo incluyo los que tienen
                    for(ItemProductivo item : items){
                        ItemProductivoCGLResponseDTO itemDTO = new ItemProductivoCGLResponseDTO();
                        if(valida_saldo){
                            // si valida saldo solo lo agrego si el saldo es mayor a 0
                            if(item.getSaldo() > 0){
                                setearItemDTO(itemDTO, item);
                                itemsDTO.add(itemDTO);
                            }
                        }else{
                            // si no valida lo agrego directamente
                            setearItemDTO(itemDTO, item);
                            itemsDTO.add(itemDTO);
                        }
                    }
                    return Response.ok(itemsDTO).build();
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("No hay items productivos para la Autorización solicitada.")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("No hay una Autorización registrada con el id_autorizacion remitido.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }else{
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("id_autorizacion no puede ser nulo.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que obtiene los items productivos de una Guía de fiscalización según su id
     * Si recibe el parámetro valida_saldo en true solo retorna los que tengan un saldo mayor a 0
     * @param id_guia Long identificación de la Guía de fiscalización
     * @param valida_saldo boolean indica si valida saldo o no
     * @return Response con los items obtenidos
     */
    @GET
    @Path("guias")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response findPorGuiaFiscalizacion(@QueryParam("id_guia") Long id_guia,
            @QueryParam("valida_saldo") @DefaultValue("false") boolean valida_saldo){
        
        // verifica el id_autorizacion
        if(id_guia != null){
            // solo continúa si hay un id_guia
            Guia guia = guiaFacade.find(id_guia);
            if(guia != null){
                // solo continúa si obtuvo la Guía de fiscalización
                List<ItemProductivo> items = new ArrayList<>();
                items = itemFacade.getByGuia(guia);
                if(!items.isEmpty()){
                    List<ItemProductivoCGLResponseDTO> itemsDTO = new ArrayList<>();
                    // si tiene items y valida saldo, verifico el saldo de cada producto y solo incluyo los que tienen
                    for(ItemProductivo item : items){
                        ItemProductivoCGLResponseDTO itemDTO = new ItemProductivoCGLResponseDTO();
                        if(valida_saldo){
                            // si valida saldo solo lo agrego si el saldo es mayor a 0
                            if(item.getSaldo() > 0){
                                setearItemDTO(itemDTO, item);
                                itemsDTO.add(itemDTO);
                            }
                        }else{
                            // si no valida lo agrego directamente
                            setearItemDTO(itemDTO, item);
                            itemsDTO.add(itemDTO);
                        }
                    }
                    return Response.ok(itemsDTO).build(); 
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("No hay items productivos para la Autorización solicitada.")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("No hay una Guía de fiscalización registrada con el id_guia remitido.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }else{
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("id_guia no puede ser nulo.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que obtiene un Item productivo según la id, para ser obtenido desde TRAZ
     * @param id Long identificación del item
     * @return Response con el ItemProductivoCGLResponseDTO
     */
    @GET
    @Path("{id}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response findPorId(@PathParam("id") Long id) {
        LOG.info("Se está consultando mediante REST un item productivo mendiante su id: " + id);
            try{
            // verifica si existe el item buscado
            ItemProductivo item = itemFacade.find(id);
            if(item != null){
                ItemProductivoCGLResponseDTO itemDTO = new ItemProductivoCGLResponseDTO();
                setearItemDTO(itemDTO, item);
                return Response.ok(itemDTO).build();
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("No existe item productivo con el id: " + id)
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener el item productivo: " + id + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener el item producctivo. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que actualiza el saldo de un item productivo descontado desde TRAZ
     * @param entity ItemProductivoCGLResponseDTO con los atributos del ItemProductivo
     * @return Response con el ItemProductivoCGLResponseDTO
     */
    @POST
    @Path("")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateItemProductivo(ItemProductivoCGLResponseDTO entity) {
        LOG.info("Se está actualizando mediante REST el saldo de un item productivo, id: " + entity.getId());
        
        try{
            if(entity.getId() != null){
                // obtengo el item productivo
                ItemProductivo item = itemFacade.find(entity.getId());
                if(item != null){
                    // se verifica la correspondencia de detalle de piezas
                    if(item.getDetallePiezas() != null && entity.getDetalle_piezas() == null) {
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("El item a editar tiene detalle de piezas.")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }
                    if(item.getDetallePiezas() == null && entity.getDetalle_piezas() != null){
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("El item a editar no tiene detalle de piezas.")
                                .type(MediaType.TEXT_PLAIN)
                                .build();  
                    }
                    // actualiza detalle
                    if(item.getDetallePiezas() != null){
                        item.getDetallePiezas().setSaldoPiezas(entity.getDetalle_piezas().getSaldo_piezas());
                        item.getDetallePiezas().setSaldoVolumen(entity.getDetalle_piezas().getSaldo_volumen());
                    }
                    // actualiza el saldo general del item
                    item.setSaldo(entity.getSaldo());
                    item.setSaldoKg(entity.getSaldo_kg());
                    item.setSaldoM3(entity.getSaldo_m3());
                    
                    // se actualiza el item
                    itemFacade.edit(item);
                    
                    // se retorna el item editado
                    ItemProductivoCGLResponseDTO itemDTO = new ItemProductivoCGLResponseDTO();
                    setearItemDTO(itemDTO, item);
                    return Response.ok().entity(itemDTO).build();
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("No existe item productivo con el id: " + entity.getId())
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("Solo se puede editar un item existente, el id no puede ser null.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al actualizar el item productivo con la id: " + entity.getId() + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al actualizar el item productivo. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    /**
     * Método privado que setea un objeto DTO para item productivo
     * @param itemDTO ItemProductivoCGLResponseDTO a setear
     * @param item item productivo a mapear
     */
    private void setearItemDTO(ItemProductivoCGLResponseDTO itemDTO, ItemProductivo item) {
        itemDTO.setId(item.getId());
        itemDTO.setNom_cientifico(item.getNombreCientifico());
        itemDTO.setNom_vulgar(item.getNombreVulgar());
        itemDTO.setClase(item.getClase());
        itemDTO.setUnidad(item.getUnidad());
        itemDTO.setId_especie_tax(item.getIdEspecieTax());
        itemDTO.setGrupo_especie(item.getGrupoEspecie());
        itemDTO.setId_prod(item.getIdProd());
        itemDTO.setKilos_x_unidad(item.getKilosXUnidad());
        itemDTO.setM3_x_unidad(item.getM3XUnidad());
        itemDTO.setTotal_kg(item.getTotalKg());
        itemDTO.setTotal_m3(item.getTotalM3());
        itemDTO.setSaldo(item.getSaldo());
        itemDTO.setSaldo_kg(item.getSaldoKg());
        itemDTO.setSaldo_m3(item.getSaldoM3());
        itemDTO.setCod_prod(item.getCodigoProducto());
        itemDTO.setTotal(item.getTotal());
        itemDTO.setItem_origen(item.getItemOrigen());
        if(item.getDetallePiezas() != null){
            itemDTO.setDetalle_piezas(setearDetallePiezas(item.getDetallePiezas()));
        }
    }

    /**
     * Método privado para setear on objeto DTO para el detalle de piezas de un item productivo
     * @param detallePiezas DetallePiezasCGLResponseDTO DTO a setear
     * @return detalle de piezas mapeado
     */
    private DetallePiezasCGLResponseDTO setearDetallePiezas(DetallePiezas detallePiezas) {
        DetallePiezasCGLResponseDTO detalleDTO = new DetallePiezasCGLResponseDTO();
        detalleDTO.setId(detallePiezas.getId());
        detalleDTO.setAncho(detallePiezas.getAncho());
        detalleDTO.setCant_piezas(detallePiezas.getCantPiezas());
        detalleDTO.setEspesor(detallePiezas.getEspesor());
        detalleDTO.setLargo(detallePiezas.getLargo());
        detalleDTO.setSaldo_piezas(detallePiezas.getSaldoPiezas());
        detalleDTO.setSaldo_volumen(detallePiezas.getSaldoVolumen());
        detalleDTO.setVolumen_total(detallePiezas.getVolumenTotal());
        detalleDTO.setVolumen_x_pieza(detallePiezas.getVolumenXPieza());
        return detalleDTO;
    }
}
