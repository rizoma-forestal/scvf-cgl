
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
