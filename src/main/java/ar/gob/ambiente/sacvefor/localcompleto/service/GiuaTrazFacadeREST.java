
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.dto.GenericoParamCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.GuiaFiscCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.OrigenGuiaFiscCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.entities.EntidadGuia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoGuia;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import java.text.SimpleDateFormat;
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
 * Para Guías de fiscalización
 * @author rincostante
 */

@Stateless
@Path("guias_fisc")
public class GiuaTrazFacadeREST {
    
    static final Logger LOG = Logger.getLogger(GiuaTrazFacadeREST.class);
    
    @EJB
    private GuiaFacade guiaFacade;
    
    /**
     * Método que obtiene las Guías de fiscalización que estén vigentes y con productos disponibles.
     * Válido tanto para primer movimiento como para removido
     * @param removido boolean condición de removido, si es true lo confirma y si es false es primer movimiento
     * @param cod_autoriz String número de Autorización fuente, solo para el caso de primer movimiento
     * @param id_establecimiento Long identificación del Establecimiento origen, solo si es removido
     * @param saldo_min float cantidad mínima que deberá haber de saldo en los productos de la guía
     * @return Response con las guías obtenidas
     */
    @GET
    @Path("disponibles")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response findVigentesConSaldoPorEstYNum(@QueryParam("removido") @DefaultValue("false") boolean removido, 
            @QueryParam("cod_autoriz") String cod_autoriz,
            @QueryParam("id_establecimiento") Long id_establecimiento,
            @QueryParam("saldo_min") @DefaultValue("0") float saldo_min){
        
        // a partir del valor de removido valida cod_autoriz o id_establecimiento
        String mensaje = "";
        if(removido){
            if(id_establecimiento == null){
                mensaje = "id_establecimiento no puede ser nulo para removido.";
            }
        }else{
            if(cod_autoriz == null){
                mensaje = "cod_autoriz no puede ser nulo para primer movimiento.";
            }
        }
        // solo continúa si se validaron los parámetros
        if("".equals(mensaje)){
            List<Guia> guias = new ArrayList<>();
            // se busca según la condición de removido
            if(removido){
                guias = guiaFacade.findFiscByIdEstablecimiento(id_establecimiento);
                
            }else{
                guias = guiaFacade.findFiscByNumFuente(cod_autoriz);
            }
            
            // continúa si hay guías retornadas
            if(!guias.isEmpty()){
                List<GuiaFiscCGLResponseDTO> guiasDTO = new ArrayList<>();
                // verifica que haya al menos un producto con saldo mayor al recibido
                for(Guia g : guias){
                    for(ItemProductivo item : g.getItems()){
                        if(item.getSaldo() > saldo_min){
                            // si tiene un item con saldo, se agrega al response
                            GuiaFiscCGLResponseDTO guiaDTO = new GuiaFiscCGLResponseDTO();
                            setearGuiaDTO(guiaDTO, g);
                            guiasDTO.add(guiaDTO);
                            break;
                        }
                    }
                }
                // si el listado está vacío es porque hay Guías pero sin saldo disponible
                return Response.ok(guiasDTO).build();
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("No hay guías de fiscalización para los parámetros remitidos.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
        }else{
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(mensaje)
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    /**
     * Método privado que setea un objeto DTO para Guía
     * @param guiaDTO GuiaFiscCGLResponseDTO a setear 
     * @param g Guía de fiscalización con los datos a mapear
     */
    private void setearGuiaDTO(GuiaFiscCGLResponseDTO guiaDTO, Guia g) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        guiaDTO.setId(g.getId());
        guiaDTO.setCodigo(g.getCodigo());
        guiaDTO.setTipo(setearGenericoParametrica(g.getTipo()));
        guiaDTO.setNum_fuente(g.getNumFuente());
        guiaDTO.setOrigen(setearOrigenGuia(g.getOrigen()));
        guiaDTO.setFecha_emision_guia(dateFormat.format(g.getFechaEmisionGuia()));
        guiaDTO.setFecha_vencimiento(dateFormat.format(g.getFechaVencimiento()));
        guiaDTO.setDestino_externo(g.isDestinoExterno());
        if(g.getRodales()!= null){
            guiaDTO.setRodales(setearRodales(g.getRodales()));
        }   
    }

    /**
     * Método privado que setea una parametrica genérica
     * @param tipo Paramétrica a setear
     * @return GenericoParamCGLResponseDTO DTO mapeado
     */
    private GenericoParamCGLResponseDTO setearGenericoParametrica(TipoGuia tipo) {
        GenericoParamCGLResponseDTO paramDTO = new GenericoParamCGLResponseDTO();
        paramDTO.setId(tipo.getId());
        paramDTO.setNombre(tipo.getNombre());
        return paramDTO;
    }

    /**
     * Método privado para setear el origen de la guía
     * @param origen EntidadGuia con los datos del origen
     * @return OrigenGuiaFiscCGLResponseDTO DTO mapeado
     */
    private OrigenGuiaFiscCGLResponseDTO setearOrigenGuia(EntidadGuia origen) {
        OrigenGuiaFiscCGLResponseDTO origenDTO = new OrigenGuiaFiscCGLResponseDTO();
        origenDTO.setId(origen.getId());
        origenDTO.setCuit_actuante(origen.getCuit());
        origenDTO.setNombre_actuante(origen.getNombreCompleto());
        if(origen.getEmail() != null){
            origenDTO.setEmail_actuante(origen.getEmail());
        }
        if(origen.getId_establecimiento() != null){
            origenDTO.setId_establecimiento(origen.getId_establecimiento());
        }
        return origenDTO;
    }

    /**
     * Método privado para setear listado de rodales vinculados a una guía de fiscalización
     * Solo en el caso en que sea de primer movimiento
     * @param rodales List<Rodal> listado con los datos a setear
     * @return List<GenericoParamCGLResponseDTO> listado de los DTO mapeados
     */
    private List<GenericoParamCGLResponseDTO> setearRodales(List<Rodal> rodales) {
        List<GenericoParamCGLResponseDTO> rdls = new ArrayList<>();
        for(Rodal rodal : rodales){
            GenericoParamCGLResponseDTO rodalDTO = new GenericoParamCGLResponseDTO();
            rodalDTO.setId(rodal.getId());
            rodalDTO.setNombre(rodal.getNumOrden());
            rdls.add(rodalDTO);
        }
        return rdls;
    }
}
