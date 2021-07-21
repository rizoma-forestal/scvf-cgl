
package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.dto.AutorizacionCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.GenericoParamCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.GenericoPersonaCGLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.dto.PredioGCLResponseDTO;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Inmueble;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ItemProductivo;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Rodal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ZonaIntervencion;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilderException;
import org.apache.log4j.Logger;

/**
 * Servicio que implementa los métodos expuestos por la API REST para la versión 2 de TRAZ
 * Para autorizaciones
 * @author rincostante
 */

@Stateless
@Path("autorizaciones")
public class AutorizacionTrazFacadeREST {
    
    static final Logger LOG = Logger.getLogger(AutorizacionTrazFacadeREST.class);
    
    @EJB
    private AutorizacionFacade autorizacionFacade;
    @EJB
    private PersonaFacade personaFacade;
    @EJB
    private TipoParamFacade tipoParamFacade;
    @EJB
    private ParametricaFacade paramFacade;
    
    /**
     * Método que retorna las Autorizaciones vigentes, habilitadas y con al menos un producto
     * con un saldo mayor a 0, según el CUIT del proponente
     * @param cuit Long cuit del Proponente vinculado a la Autorización
     * @return Response con el List<AutorizacionCGLResponseDTO> 
     */
    @GET
    @Path("disponibles/cuit/{cuit}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response findAutDisponiblesConProductos(@PathParam("cuit") Long cuit){
        try{
            // verifica que haya una Proponente con el CUIT ingresado
            Parametrica rolProp = obtenerRol(ResourceBundle.getBundle("/Config").getString("Proponente"));
            // obtiene la Persona
            Persona per = personaFacade.findByCuitRol(cuit, rolProp);
            // solo sigue si hay un Proponente
            if(per != null){
                List<Autorizacion> autorizaciones = autorizacionFacade.getFuenteByProponente(per);
                if(!autorizaciones.isEmpty()){
                    // solo continúa si hay Autorizaciones vigentes para el Proponente
                    List<AutorizacionCGLResponseDTO> autorizacionesDTO = new ArrayList<>();
                    // si retornó Autorizaciones vigentes se valida el saldo de los productos
                    for(Autorizacion aut : autorizaciones){
                        // recorre el listado para verificar el saldo de cada una
                        for(ItemProductivo item : aut.getItems()){
                            if(item.getSaldo() > 0){
                                // si tiene un item con saldo, se agrega al response
                                AutorizacionCGLResponseDTO autDTO = new AutorizacionCGLResponseDTO();
                                setearAutDTO(autDTO, aut);
                                autorizacionesDTO.add(autDTO);
                                break;
                            }
                        }
                    }
                    // si el listado está vacío es porque hay Autorizaciones pero sin saldo disponible
                    return Response.ok(autorizacionesDTO).build(); 
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("No hay Autorizaciones habilitadas y vigentes para el Proponente con el cuit: " + cuit + ".")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }        
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("No hay un Proponente registrado con el CUIT remitido.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch (IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener las Autorizaciones disponibles de: " + cuit + ", para TRAZ. " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener las Autorizaciones según CUIT. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que retorna la Autorizacion cuyo número coincida con el código recibido
     * valida que esté habilitada y vigente. Además que tenga al menos un item con saldo > 0
     * @param codigo String número de la Autorización
     * @return AutorizacionCGLResponseDTO objeto seleccionado
     */
    @GET
    @Path("disponibles")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response findAutDisponiblePorNumero(@QueryParam("codigo") String codigo){
        Autorizacion autorizacion = autorizacionFacade.getExistente(codigo);
        if(autorizacion != null){
            // si la encontró valida el estado y la vigencia
            Date hoy = new Date(System.currentTimeMillis());
            if(autorizacion.getEstado().isHabilitaEmisionGuia() == true && autorizacion.getFechaVencAutoriz().after(hoy)){
                // si está vigente valido que tenga al menos un producto con cupo
                List<AutorizacionCGLResponseDTO> autorizacionesDTO = new ArrayList<>();
                for(ItemProductivo item : autorizacion.getItems()){
                    if(item.getSaldo() > 0){
                        // si tiene un item con saldo, se agrega al response
                        AutorizacionCGLResponseDTO autDTO = new AutorizacionCGLResponseDTO();
                        setearAutDTO(autDTO, autorizacion);
                        autorizacionesDTO.add(autDTO);
                        break;
                    }
                }
                return Response.ok(autorizacionesDTO).build(); 
            }else{
                return Response
                        .status(Response.Status.FORBIDDEN)
                        .entity("La Autorización obtenida no está vigente.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }else{
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("No hay una Autorización con el código recibido.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método para obtener el rol de la persona según la cadena recibida
     * @param sRol String nombre del rol de la persona a buscar
     * @return Parametrica paramétrica con los datos del rol obtenido
     */
    public Parametrica obtenerRol(String sRol) {
        TipoParam tipoParam = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolPersonas"));
        return paramFacade.getExistente(sRol, tipoParam);
    }    

    /**
     * Método privado que setea un objeto DTO para Autorización
     * @param autDTO AutorizacionCGLResponseDTO a setear 
     * @param aut Autorizacion con los datos a mapear
     */
    private void setearAutDTO(AutorizacionCGLResponseDTO autDTO, Autorizacion aut) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        autDTO.setId(aut.getId());
        if(aut.getCuencaForestal() != null){
            // si tiene cuencas las seteo
            autDTO.setCuenca_forestal(setearGenericoParametrica(aut.getCuencaForestal()));
        }
        autDTO.setFecha_instrumento(dateFormat.format(aut.getFechaInstrumento()));
        autDTO.setFecha_vencimiento(dateFormat.format(aut.getFechaVencAutoriz()));
        autDTO.setIntervencion(setearGenericoParametrica(aut.getIntervencion()));
        autDTO.setNum_exp(aut.getNumExp());
        autDTO.setNumero(aut.getNumero());
        autDTO.setTipo(setearGenericoParametrica(aut.getTipo()));
        autDTO.setPredios(setearPredios(aut.getInmuebles()));
        autDTO.setProponentes(setearProponentes(aut.getPersonas()));
        if(aut.getZonas() != null){
            // si tiene zonas las seteo
            autDTO.setZonas(setearZonas(aut.getZonas()));
        }
    }

    /**
     * Método privada para setear un GenericoParamCGLResponseDTO
     * @param param Paramétrica con los datos a mapear
     * @return GenericoParamCGLResponseDTO genericoDTO seteado
     */
    private GenericoParamCGLResponseDTO setearGenericoParametrica(Parametrica param) {
        GenericoParamCGLResponseDTO paramDTO = new GenericoParamCGLResponseDTO();
        paramDTO.setId(param.getId());
        paramDTO.setNombre(param.getNombre());
        return paramDTO;
    }

    /**
     * Método privado que setea un listado de predios
     * @param inmuebles List<Inmueble> listado de Inmuebles
     * @return List<PredioGCLResponseDTO> Listado de predios
     */
    private List<PredioGCLResponseDTO> setearPredios(List<Inmueble> inmuebles) {
        List<PredioGCLResponseDTO> predios = new ArrayList<>();
        for(Inmueble inm : inmuebles){
            PredioGCLResponseDTO predioDTO = new PredioGCLResponseDTO();
            predioDTO.setId(inm.getId());
            predioDTO.setId_catastral(inm.getIdCatastral());
            predioDTO.setId_loc_gt(inm.getIdLocGt());
            predioDTO.setLocalidad(inm.getLocalidad());
            predioDTO.setDepartamento(inm.getDepartamento());
            predioDTO.setDomicilio(inm.getDomicilio());
            predioDTO.setNombre(inm.getNombre());
            // setea origen si tiene configurado
            if(inm.getOrigen() != null){
                GenericoParamCGLResponseDTO origenDTO = new GenericoParamCGLResponseDTO();
                origenDTO.setId(inm.getOrigen().getId());
                origenDTO.setNombre(inm.getOrigen().getNombre());
                predioDTO.setOrigen(origenDTO);
            }
            // setea rodales si los tiene configurados
            if(!inm.getRodales().isEmpty()){
                List<PredioGCLResponseDTO.RodalDTO> lstRDto = new ArrayList<>();
                for (Rodal r : inm.getRodales()) {
                    PredioGCLResponseDTO.RodalDTO rDTO = new PredioGCLResponseDTO.RodalDTO();
                    rDTO.setId(r.getId());
                    rDTO.setNumOrden(r.getNumOrden());
                    lstRDto.add(rDTO);
                }
                predioDTO.setRodales(lstRDto);
            }

            predios.add(predioDTO);
        }
        return predios;
    }

    /**
     * Método privado para setear los proponentes de una Autorización
     * @param personas List<Persona> listado de personas
     * @return List<GenericoPersonaCGLResponseDTO> listado actuantes retornado
     */
    private List<GenericoPersonaCGLResponseDTO> setearProponentes(List<Persona> personas) {
        List<GenericoPersonaCGLResponseDTO> actuantes = new ArrayList<>();
        for(Persona per : personas){
            GenericoPersonaCGLResponseDTO actuanteDTO = new GenericoPersonaCGLResponseDTO();
            // solo lo seteo si es Proponente
            if(per.getRolPersona().getNombre().equals(ResourceBundle.getBundle("/Config").getString("Proponente"))){
                actuanteDTO.setCuit(per.getCuit());
                actuanteDTO.setNombre(per.getNombreCompleto());
                actuantes.add(actuanteDTO);
            }
        }
        return actuantes;
    }

    /**
     * Método privado para setear las zonas de una Autorización
     * @param zonas List<ZonaIntervencion> listado de Zonas
     * @return List<GenericoParamCGLResponseDTO> listado de zonas dto retornado
     */
    private List<GenericoParamCGLResponseDTO> setearZonas(List<ZonaIntervencion> zonas) {
        List<GenericoParamCGLResponseDTO> zns = new ArrayList<>();
        for(ZonaIntervencion zona : zonas){
            GenericoParamCGLResponseDTO zonaDTO = new GenericoParamCGLResponseDTO();
            zonaDTO.setId(zona.getId());
            zonaDTO.setNombre(zona.getNombre());
            zns.add(zonaDTO);
        }
        return zns;
    }
}
