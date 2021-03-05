package ar.gob.ambiente.sacvefor.localcompleto.service;

import ar.gob.ambiente.sacvefor.localcompleto.annotation.Secured;
import ar.gob.ambiente.sacvefor.localcompleto.dto.*;
import ar.gob.ambiente.sacvefor.localcompleto.entities.FactoresTransformacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Producto;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoEspecieLocal;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoSubClase;
import ar.gob.ambiente.sacvefor.localcompleto.entities.ProductoUnidadMedida;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.facades.FactoresTransFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoClaseFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoEspecieLocalFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoSubClaseFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ProductoUnidadMedidaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
 * @author rincostante
 */
@Stateless
@Path("productos")
public class ProductoTrazFacadeREST {
    
    static final Logger LOG = Logger.getLogger(ProductoTrazFacadeREST.class);
    @EJB
    private ProductoFacade prodFacade;
    @EJB
    private ProductoUnidadMedidaFacade unidadFacade;
    @EJB
    private ProductoClaseFacade claseFacade;
    @EJB
    private ProductoSubClaseFacade subClaseFacade;
    @EJB
    private ProductoEspecieLocalFacade especieFacade; 
    @EJB
    private TipoParamFacade tipoParamFacade;
    @EJB
    private ParametricaFacade paramFacade;
    @EJB
    private UsuarioFacade usFacade;
    @EJB
    private FactoresTransFacade factoresFacade;
    
    /************
     * ESPECIES * 
     ************/
    
    /**
     * Método que retorna una especie local según el nombre científico para ser obtenida desde el TRAZ
     * @param nom_cient String nombre científico de la especie
     * @return Response con el EspecieLocalDTO
     */
    @GET
    @Path("especies_locales/nombre_cientifico/{nom_cient}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response findEspecieByNomCientifico(@PathParam("nom_cient") String nom_cient){
        try{
            ProductoEspecieLocal especie = especieFacade.getExistenteXNomCientifico(nom_cient);
            if(especie != null){
                EspecieLocalDTO especieDTO = new EspecieLocalDTO();
                especieDTO.setId(especie.getId());
                especieDTO.setId_tax(especie.getIdTax());
                especieDTO.setNombre_cientifico(especie.getNombreCientifico());
                especieDTO.setNombre_vulgar(especie.getNombreVulgar());
                especieDTO.setGrupo_especie(especie.getGrupoEspecie());
                especieDTO.setObservaciones(especie.getObs());
                especieDTO.setHabilitado(especie.isHabilitado());
                return Response.ok(especieDTO).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay especie con el nombre científico recibido")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }   
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener la especie local para TRAZ: " + nom_cient + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener la especie local. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que retorna todas las especies locales habilitadas en el CGL
     * Para ser obtenidas desde el TRAZ 
     * @return Response con el listado de EspecieLocalGenericoDTO
     */
    @GET
    @Path("especies_locales/hab")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getEspeciesHab(){
        List<EspecieLocalGenericoDTO> especiesDTO = new ArrayList<>();
        EspecieLocalGenericoDTO especieDTO;
        List<ProductoEspecieLocal> especiesHab = especieFacade.getHabilitadasOrdNomCient();
        if(!especiesHab.isEmpty()){
            // recorre el listado y setea las especies
            for(ProductoEspecieLocal especie : especiesHab){
                especieDTO = new EspecieLocalGenericoDTO();
                especieDTO.setId(especie.getId());
                especieDTO.setNombre_cientifico(especie.getNombreCientifico());
                especieDTO.setNombre_vulgar(especie.getNombreVulgar());
                especiesDTO.add(especieDTO);
            }
        }
        return Response.ok(especiesDTO).build();
    }
    
    /**
     * Método para crear una especie local nueva o editar una existente desde TRAZ
     * Valida que no haya una registrada con el idTax (que incluye el nombre científico)
     * ni con el mismo nombre vulgar. Tanto para la creación como para la edición
     * @param entity EspecieLocalDTO con los atributos de la especie local
     * @return Response con el EspecieLocalDTO
     */
    @POST
    @Path("especies_locales")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createEspecieLocal(EspecieLocalDTO entity){
        boolean valida = true;
        try{
            // si no tiene id se inserta
            if(entity.getId() == null){
                // verifico que la especie no esté registrada ya localmente
                ProductoEspecieLocal especie = especieFacade.getExistenteXidTax(entity.getId_tax());
                if(especie == null){
                    // si el idTax no está registrado, verifico el nombre vulgar
                    especie = especieFacade.getExistenteXNomVulgar(entity.getNombre_vulgar());
                    if(especie != null) valida = false;
                }else{
                    valida = false;
                }
                
                if(valida){
                    // solo coninúa si no está registrada la especie previamente
                    especie = new ProductoEspecieLocal();
                    setearEspecie(especie, entity);

                    especieFacade.create(especie);

                    entity.setId(especie.getId());

                    return Response.status(201).entity(entity).build();
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("Ya hay una especie para el id_tax y/o nombre vulgar remitidos")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                // busco la especie, si no está respondo
                ProductoEspecieLocal especie = especieFacade.find(entity.getId());
                if(especie != null){
                    // si existe primero verifico que no vaya a duplicar idTax y/o nombre vulgar ya existentes
                    ProductoEspecieLocal especie2 = especieFacade.getExistenteXidTax(entity.getId_tax());
                    Long idByNombreVulgar = especie2.getId();
                    especie2 = especieFacade.getExistenteXNomVulgar(entity.getNombre_vulgar());
                    Long idByNomVulg = especie2.getId();
                    if(!Objects.equals(especie2.getId(), idByNombreVulgar) || !Objects.equals(especie2.getId(), idByNomVulg)){
                        // si ya existe una especie con el mismo idTax y/o nombre vulgar registrada, respondo
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("Ya hay una especie local de producto con el idTax y/o nombre vulgar remitidos")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }else{
                        // si valida todo setea y edita
                        setearEspecie(especie, entity);
                        especieFacade.edit(especie);
                        return Response.ok().entity(entity).build();
                    }
                }else{
                    return Response
                            .status(Response.Status.NO_CONTENT)
                            .entity("No hay una especie local registrada con la id recibida")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al insertar la especie local desde TRAZ: " + entity.getNombre_cientifico() + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al insertar la especie local. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();  
        }
    }
    
    /**********************
     * UNIDADES DE MEDIDA * 
     **********************/
    /**
     * Método que retorna todas las unidades de medida registradas
     * @return Response con el Listado con las UnidadMedidaDTO
     */
    @GET
    @Path("unidades")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUnidades(){
        List<UnidadMedidaDTO> unidadesDTO = new ArrayList<>();
        UnidadMedidaDTO unidadDTO;
        List<ProductoUnidadMedida> unidades = unidadFacade.findAll();
        if(!unidades.isEmpty()){
            // recorro el listado y seteo las unidades
            for(ProductoUnidadMedida unidad : unidades){
                unidadDTO = new UnidadMedidaDTO();
                setearUnidadResponse(unidadDTO, unidad);
                unidadesDTO.add(unidadDTO);
            }
        }
        return Response.ok(unidadesDTO).build();
    }
    
    /**
     * Método que retorna todas las unidades de medida habilitadas
     * @return Response con el listado de UnidadMedidaDTO
     */
    @GET
    @Path("unidades/hab")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response getUnidadesHabilitadas(){
        List<UnidadMedidaDTO> unidadesDTO = new ArrayList<>();
        UnidadMedidaDTO unidadDTO;
        List<ProductoUnidadMedida> unidades = unidadFacade.getHabilitados();
        if(!unidades.isEmpty()){
            // recorro el listado y seteo las unidades
            for(ProductoUnidadMedida unidad : unidades){
                unidadDTO = new UnidadMedidaDTO();
                setearUnidadResponse(unidadDTO, unidad);
                unidadesDTO.add(unidadDTO);
            }
        }
        return Response.ok(unidadesDTO).build();
    }
    
    /**
     * Método para crear una unidad de medida nueva o editar una existente desde TRAZ
     * Valida que no haya registrada previamente el nombre y la abreviatura
     * Tanto para la creación como para la edición
     * @param entity UnidadMedidaDTO con los atributos de la unidad de medida
     * @return Response con el UnidadMedidaDTO
     */
    @POST
    @Path("unidades")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createEditUnidad(UnidadMedidaDTO entity){
        boolean valida = true;
        try{
            // si no tiene id se inserta
            if(entity.getId() == null){
                // verifico que el nombre de la unidad no esté registrada ya localmente
                ProductoUnidadMedida unidad = unidadFacade.getExistenteByNombre(entity.getNombre());
                if(unidad == null) {
                    // si el nombre no está registrado, verifico la abreviatura
                    unidad = unidadFacade.getExistenteByAbrev(entity.getAbreviatura());
                    if(unidad != null) valida = false;
                }else{
                    valida = false;
                }
                
                if(valida){
                    // solo coninúa si valida el no registro la unidad previamente
                    unidad = new ProductoUnidadMedida();
                    setearUnidad(unidad, entity);

                    unidadFacade.create(unidad);

                    entity.setId(unidad.getId());

                    return Response.status(201).entity(entity).build();
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("Ya hay una unidad de medida con el nombre y/o abreviatura remitidos")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                // busco la unidad de medida, si no está respondo
                ProductoUnidadMedida unidad = unidadFacade.find(entity.getId());
                if(unidad != null){
                    // si existe primero verifico que no vaya a duplicar nombre y abreviatura ya existentes
                    ProductoUnidadMedida unidad2 = unidadFacade.getExistenteByNombre(entity.getNombre());
                    Long idByNombre = unidad2.getId();
                    unidad2 = unidadFacade.getExistenteByAbrev(entity.getAbreviatura());
                    Long idByAbrev = unidad2.getId();
                    if(!Objects.equals(unidad2.getId(), idByNombre) || !Objects.equals(unidad2.getId(), idByAbrev)){
                        // si ya existe una unidad con el nombre o la abreviatura de la unidad a ingresar, respondo
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("Ya hay una unidad de mediad de producto con el nombre y/o la abreviatura remitidos")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }else{
                        // si valida todo setea y edita
                        setearUnidad(unidad, entity);

                        unidadFacade.edit(unidad);
                        return Response.ok().entity(entity).build();
                    }
                }else{
                    return Response
                            .status(Response.Status.NOT_FOUND)
                            .entity("No hay una unidad de medida registrada con la id recibida")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al insertar la unidad de medida desde TRAZ: " + entity.getNombre() + "." + ex.getMessage());
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Hubo un error al insertar la unidad de medida. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /********************
     * CLASES / ESTADOS * 
     ********************/
    
    /**
     * Método que retorna todas las clases de productos disponibles en formato genérico
     * constituido por id - nombre - unidad.nombre
     * @return Response con el listado de ClaseProdGenericoResponseDTO
     */
    @GET
    @Path("clases")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClases(){
        List<ClaseProdGenericoResponseDTO> clasesGenDTO = new ArrayList<>();
        ClaseProdGenericoResponseDTO claseGenDTO;
        List<ProductoClase> clases = claseFacade.findAll();
        if(!clases.isEmpty()){
            // recorro el listado y seteo las ClaseProdGenericoResponseDTO
            for(ProductoClase clase : clases){
                claseGenDTO = new ClaseProdGenericoResponseDTO();
                claseGenDTO.setId(clase.getId());
                claseGenDTO.setNombre(clase.getNombre());
                claseGenDTO.setNombre_unidad(clase.getUnidad().getNombre());
                clasesGenDTO.add(claseGenDTO);
            }
            
        }
        return Response.ok(clasesGenDTO).build();
    }
    
    /**
     * Método que retorna todas las clases de productos habilitadas en el CGL
     * Para ser obtenidad desde TRAZ
     * @return Listado con las ClaseProdGenericoResponseDTO
     */
    @GET
    @Path("clases/hab")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClasesHabilitadas(){
        List<ClaseProdGenericoResponseDTO> clasesDTO = new ArrayList<>();
        ClaseProdGenericoResponseDTO claseDTO;
        List<ProductoClase> clasesHab = claseFacade.getHabilitadas();
        if(!clasesHab.isEmpty()){
            // recorre el listado y setea las clases
            for(ProductoClase clase : clasesHab){
                claseDTO = new ClaseProdGenericoResponseDTO();
                setearClaseGenericaResponse(claseDTO, clase);
                clasesDTO.add(claseDTO);
            }
        }
        return Response.ok(clasesDTO).build();
    }
    
    /**
     * Método que retorna una clase de productos según su id para ser obtenida desde el TRAZ
     * Responde un objeto completo
     * @param id Long id de la clase
     * @return Response con el ClaseProdResponseDTO
     */
    @GET
    @Path("clases/{id}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClaseById(@PathParam("id") Long id){
        try{
            ProductoClase clase = claseFacade.find(id);
            if(clase != null){
                // instancia el ResponseDTO
                ClaseProdResponseDTO claseDTO = new ClaseProdResponseDTO();
                // setea el ResponseDTO
                setearClaseResponse(claseDTO, clase);
                
                return Response.ok(claseDTO).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay clase de producto con el id recibido")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener la clase de producto para TRAZ: " + id + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener la clase de producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método que retorna las clases origen para otra, según el nivel de transformación de esta última
     * @param niv_transf int número entero que será 1 o 2
     * @return Response con el listado de ClaseProdGenericoResponseDTO
     */
    @GET
    @Path("clases_origen/{niv_transf}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getClaseOrigenByNivTransf(@PathParam("niv_transf") int niv_transf){
        List<ClaseProdGenericoResponseDTO> clasesGenDTO = new ArrayList<>();
        ClaseProdGenericoResponseDTO claseGenDTO;
        List<ProductoClase> clases = claseFacade.getClasesOrigen(niv_transf);
        if(!clases.isEmpty()){
            // recorro el listado y seteo las ClaseProdGenericoResponseDTO
            for(ProductoClase clase : clases){
                claseGenDTO = new ClaseProdGenericoResponseDTO();
                setearClaseGenericaResponse(claseGenDTO, clase);
                clasesGenDTO.add(claseGenDTO);
            }
        }
        return Response.ok(clasesGenDTO).build();
    }
    
    /**
     * Método para la creación o edición de una clase de producto desde TRAZ
     * Valida que no haya registrada previamente el nombre y la unidad
     * Tanto para la creación como para la edición
     * @param entity ClaseProdRequestDTO con los atributos de la unidad de medida
     * @return Response con el ClaseProdResponseDTO
     */
    @POST
    @Path("clases")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createEditClase(ClaseProdRequestDTO entity){
        try{
            // si no tiene id se inserta
            if(entity.getId() == null){
                // verifico que no haya registrada una clase con el mismo nombre y unidad
                ProductoUnidadMedida unidad = unidadFacade.find(entity.getId_unidad());
                ProductoClase clase = claseFacade.getExistente(entity.getNombre(), unidad);
                if(clase == null){
                    // solo coninúa si valida el no registro la clase previamente
                    clase = new ProductoClase();
                    setearClase(clase, entity, unidad);
                    // inserta
                    claseFacade.create(clase);
                    // instancia el Response completo
                    ClaseProdResponseDTO claseResp = new ClaseProdResponseDTO();
                    // setea el Response
                    setearClaseResponse(claseResp, clase);
                    // responde
                    return Response.status(201).entity(claseResp).build();  
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("Ya hay una clase de producto con el nombre y unidad de medida remitidos")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                // busco la clase, si no está respondo
                ProductoClase clase = claseFacade.find(entity.getId());
                if(clase != null){
                    // si existe primero verifico que no vaya a duplicar nombre y unidad ya existentes
                    ProductoUnidadMedida unidad = unidadFacade.find(entity.getId_unidad());
                    ProductoClase clase2 = claseFacade.getExistente(entity.getNombre(), unidad);
                    if(!Objects.equals(clase2.getId(), clase.getId())){
                        // si ya existe una clase con el nombre y la unidad a ingresar, respondo
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("Ya hay una clase de producto con el nombre y unidad de medida remitidos")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }else{
                        // si valida todo setea y edita
                        setearClase(clase, entity, unidad);
                        claseFacade.edit(clase);
                        // instancia el Response completo
                        ClaseProdResponseDTO claseResp = new ClaseProdResponseDTO();
                        // setea el request
                        setearClaseResponse(claseResp, clase);
                        return Response.ok().entity(claseResp).build();
                    }
                }else{
                    return Response
                            .status(Response.Status.NO_CONTENT)
                            .entity("No hay una clase de producto registrada con la id recibida")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al insertar la clase de producto desde TRAZ: " + entity.getNombre() + "." + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al insertar la clase de producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método para obtener las clases de subproductos derivadas la clase y la especie del producto de la clase origen
     * @param id_especie_local Long identificador de la especie local del producto de la clase origen
     * @param id_clase_origen Long identificador de la clase origen
     * @return Response con el listado de ClaseProdGenericoResponseDTO
     */
    @GET
    @Path("/clases/derivadas")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getSubClasesByIdClaseOrigenAndIdEspecieLocal(
            @QueryParam("id_especie_local") Long id_especie_local,
            @QueryParam("id_clase_origen") Long id_clase_origen){
        try{
            // verifica la existencia del producto origen
            Producto prod = prodFacade.getExistenteXid_especieYid_clase(id_especie_local, id_clase_origen);
            if(prod != null){
                // si el producto origen se validó se obtienen los subproductos
                List<ClaseProdGenericoResponseDTO> clasesDTO = new ArrayList<>();
                ClaseProdGenericoResponseDTO claseGenDTO;
                List<Producto> sub_prods = prodFacade.getSubProductosHabilitadosByIdClaseAndIdEspecie(id_clase_origen, id_especie_local);
                if(sub_prods != null){
                    // si hay retorno se mapea
                    for(Producto sub_prod : sub_prods){
                        claseGenDTO = new ClaseProdGenericoResponseDTO();
                        setearClaseGenericaResponse(claseGenDTO, sub_prod.getClase());
                        clasesDTO.add(claseGenDTO);
                    }
                }
                return Response.ok(clasesDTO).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay un producto origen con los id de especie local y clase recibidos")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener las subclases para TRAZ del producto con la especie: " + id_especie_local + " y la clase: " 
                    + id_clase_origen + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener las subclases. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método para obtener la secuencia de clases origenes a partir de un producto transformado
     * Validando que pertenezcan a productos de la misma especie
     * @param id_especie_local identificador de la especie de todos los productos
     * @param id_clase_destino identificador de la clade destino desde la cual se inicia la secuencia
     * @return Response con el listado de ClaseProdGenericoResponseDTO
     */
    @GET
    @Path("/clases/secuencias")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response getClasesSecuenciaTransfOrigenFin(
            @QueryParam("id_especie_local") Long id_especie_local,
            @QueryParam("id_clase_destino") Long id_clase_destino){
        try{
            // verifica la existencia del producto destino
            Producto prod = prodFacade.getExistenteXid_especieYid_clase(id_especie_local, id_clase_destino);
            if(prod != null && prod.getClase().getClaseOrigen() != null){
                // si el producto destino se validó continua
                List<ProductoClase> clases = new ArrayList<>();
                // setea la clase inicial
                clases.add(prod.getClase());
                // guardo el último grado
                int grado_ultimo = prod.getClase().getGradoElaboracion();
                int iSecuencia;
                for(int i = grado_ultimo; i >= 0; i --){
                    iSecuencia = clases.size() - 1;
                    if(clases.get(iSecuencia).getClaseOrigen() != null){
                        // si hay clase origen continúo obteniendo el producto anterior
                        Producto p = prodFacade.getExistenteXid_especieYid_clase(id_especie_local, clases.get(iSecuencia).getClaseOrigen().getId());
                        // guarda la clase del producto anterior
                        clases.add(p.getClase());
                    }
                }
                // una vez obtenida la secuencia en clases se setea el listado de ResponseDTO
                List<ClaseProdGenericoResponseDTO> clasesGenDTO = new ArrayList<>();
                ClaseProdGenericoResponseDTO claseGenDTO;
                for(ProductoClase clase : clases){
                    claseGenDTO = new ClaseProdGenericoResponseDTO();
                    setearClaseGenericaResponse(claseGenDTO, clase);
                    clasesGenDTO.add(claseGenDTO);
                }
                return Response.ok(clasesGenDTO).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay un producto destino con los id de especie local y clase recibidos o su clase no tiene origen")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener la secuencia de transformación para la especie: " + id_especie_local + " hasta la clase: " 
                    + id_clase_destino + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener las subclases. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /*************
     * SUBCLASES * 
     *************/   
    
    /**
     * Método para la creación o edición de una sub clase de producto desde TRAZ
     * @param entity SubClaseProdRequestDTO con los atributos a persistir
     * @return Response con el SubClaseProdResponseDTO
     */
    @POST
    @Path("subclases")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response createEditSubClase(SubClaseProdRequestDTO entity){
        try{
            // registra o edita según el caso
            if(entity.getId() == null){
                // valida que exista la clase principal
                ProductoClase cPrincipal = claseFacade.find(entity.getId_clase_principal());
                if(cPrincipal == null){
                return Response
                        .status(Response.Status.NO_CONTENT)
                        .entity("No hay una clase principal con el id: " + entity.getId_clase_principal())
                        .type(MediaType.TEXT_PLAIN)
                        .build();
                }
                // valida que exista la clase derivada
                ProductoClase cDerivada = claseFacade.find(entity.getId_clase_derivada());
                if(cDerivada == null){
                return Response
                        .status(Response.Status.NO_CONTENT)
                        .entity("No hay una clase derivada con el id: " + entity.getId_clase_derivada())
                        .type(MediaType.TEXT_PLAIN)
                        .build();
                }
                // valida que no exista una subclase para la principal con la derivada recibida
                ProductoSubClase scExistente = subClaseFacade.getSubClaseByIdPrincipalAndIdDerivada(
                        entity.getId_clase_principal(), entity.getId_clase_derivada());
                if(scExistente != null){
                return Response
                        .status(Response.Status.NO_CONTENT)
                        .entity("Ya hay una subclase para la clase princial con el id: " + entity.getId_clase_principal() + 
                                " y la derivada con el id: " + entity.getId_clase_derivada())
                        .type(MediaType.TEXT_PLAIN)
                        .build();
                }
                // si todo se validó se registra
                ProductoSubClase subClase = new ProductoSubClase();
                subClase.setClasePrincipal(cPrincipal);
                subClase.setClaseDerivada(cDerivada);
                subClase.setFactorRelativo(entity.getFactor_relativo());
                subClaseFacade.create(subClase);
                
                SubClaseProdResponseDTO subClaseDTO = new SubClaseProdResponseDTO();
                subClaseDTO.setId(subClase.getId());
                subClaseDTO.setFactor_relativo(subClase.getFactorRelativo());
                subClaseDTO.setId_clase_derivada(subClase.getClaseDerivada().getId());
                subClaseDTO.setNombre_clase_derivada(subClase.getClaseDerivada().getNombre());
                subClaseDTO.setUnidad_clase_derivada(subClase.getClaseDerivada().getUnidad().getNombre());
                
                return Response.status(201).entity(subClaseDTO).build();  
            }else{
                // busco la subclase, si no está respondo
                ProductoSubClase subClase = subClaseFacade.find(entity.getId());
                if(subClase != null){
                    // si existe primero verifico que no vaya a duplicar nombre y unidad ya existentes
                    ProductoSubClase subClase2 = subClaseFacade.getSubClaseByIdPrincipalAndIdDerivada(
                            entity.getId_clase_principal(), entity.getId_clase_derivada());
                    if(!Objects.equals(subClase2.getId(), subClase.getId())){
                        // si ya existe otra subclase para la principal y derivada, respondo
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("Ya hay otra Sub Clase de producto con el la id_clase_principal e id_clase_derivada remitidos")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }else{
                        // si valida todo setea y edita
                        subClase.setClasePrincipal(claseFacade.find(entity.getId_clase_principal()));
                        subClase.setClaseDerivada(claseFacade.find(entity.getId_clase_derivada()));
                        subClase.setFactorRelativo(entity.getFactor_relativo());
                        subClaseFacade.edit(subClase);

                        SubClaseProdResponseDTO subClaseDTO = new SubClaseProdResponseDTO();
                        subClaseDTO.setId(subClase.getId());
                        subClaseDTO.setFactor_relativo(subClase.getFactorRelativo());
                        subClaseDTO.setId_clase_derivada(subClase.getClaseDerivada().getId());
                        subClaseDTO.setNombre_clase_derivada(subClase.getClaseDerivada().getNombre());
                        subClaseDTO.setUnidad_clase_derivada(subClase.getClaseDerivada().getUnidad().getNombre());
                
                        return Response.ok().entity(subClaseDTO).build();
                    }
                }else{
                    return Response
                            .status(Response.Status.NO_CONTENT)
                            .entity("No hay una subclase de productos registrada con la id recibida")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al insertar/editar la sub clase de productos desde TRAZ, "
                    + "para la clase origen principal con id: " + entity.getId_clase_principal() + ", "
                            + "y la clase derivada con el id: " + entity.getId_clase_derivada() + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al insertar/registrar la sub clase de producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /*************
     * PRODUCTOS * 
     *************/    
    
    /**
     * Método que retorna un Producto los id de la especie local y de la clase
     * Responde un objeto completo. Para ser obtenida desde el TRAZ
     * @param id_especie Long identificador de la especie local
     * @param id_clase Long identificador de la clase
     * @return Response con el ProductoResponseDTO
     */
    @GET
    @Path("/{id_especie}/{id_clase}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProductoByidEspecieIdClase(@PathParam("id_especie") Long id_especie, @PathParam("id_clase") Long id_clase){
        try{
            Producto prod = prodFacade.getExistenteXid_especieYid_clase(id_especie, id_clase);
            if(prod != null){
                // instancia el ResponseDTO
                ProductoResponseDTO prodResp = new ProductoResponseDTO();
                // setea el ResponseDTO
                setearProductoResponse(prodResp, prod);
                
                return Response.ok(prodResp).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay un producto con los id de especie local y clase recibidos")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener el producto para TRAZ para la especie: " + id_especie + " y la clase: " + id_clase + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener el producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método para la creación o edición de Producto desde TRAZ
     * Valida que no haya registrado previamente un producto para le especie y la clase correspondientes
     * Tanto para la creación como para la edición
     * @param entity ProductoRequestDTO con los atributos del producto
     * @return Response con el ProductoResponseDTO
     */
    @POST
    @Path("")
    @Secured
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createEditProducto(ProductoRequestDTO entity){
        try{
            // si no tiene id se inserta
            if(entity.getId() == null){
                // verifico que no haya registrado un producto con para la misma especie y clase seteadas
                Producto prodExistente = prodFacade.getExistenteXid_especieYid_clase(entity.getId_especie_local(), entity.getId_clase());
                if(prodExistente == null){
                    // solo coninúa si valida el no registro del producto previamente
                    ProductoEspecieLocal especie_local = new ProductoEspecieLocal();
                    ProductoClase clase = new ProductoClase();
                    Producto prod = new Producto();
                    // setea el producto
                    setearProducto(prod, entity, especie_local, clase);
                    // setea habilitado
                    prod.setHabilitado(true);
                    // setea usuario reservado para el registro de parte de TRAZ
                    TipoParam tipo = tipoParamFacade.getExistente("ROL_USUARIOS");
                    prod.setUsuario(usFacade.getByRol(paramFacade.getExistente("CLIENTE_TRAZ", tipo).getId()));
                    // setea fecha de alta
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    prod.setFechaAlta(fechaAlta);
                    // inserta
                    prodFacade.create(prod);
                    // instancia el Response completo
                    ProductoResponseDTO prodResp = new ProductoResponseDTO();
                    // setea el response
                    setearProductoResponse(prodResp, prod);
                    // responde
                    return Response.status(201).entity(prodResp).build(); 
                }else{
                    return Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("Ya hay un producto con la especie y la clase remitidas")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }else{
                // busco el producto, si no está respondo
                Producto prod = prodFacade.find(entity.getId());
                if(prod != null){
                    // si existe primero verifico que no vaya a duplicar especie y clase ya existentes
                    Producto prodExistente = prodFacade.getExistenteXid_especieYid_clase(entity.getId_especie_local(), entity.getId_clase());
                    if(!Objects.equals(prodExistente.getId(), prod.getId())){
                        // si ya existe una clase con el nombre y la unidad a ingresar, respondo
                        return Response
                                .status(Response.Status.FORBIDDEN)
                                .entity("Ya hay un producto con la especie y clase remitidos")
                                .type(MediaType.TEXT_PLAIN)
                                .build();
                    }else{
                        // si valida todo setea y edita
                        ProductoEspecieLocal especie_local = new ProductoEspecieLocal();
                        ProductoClase clase = new ProductoClase();
                        setearProducto(prod, entity, especie_local, clase);
                        // setea habilitado
                        prod.setHabilitado(entity.isHabilitado());
                        // edita
                        prodFacade.edit(prod);
                        // instancia el Response completo
                        ProductoResponseDTO prodResp = new ProductoResponseDTO();
                        // setea el response
                        setearProductoResponse(prodResp, prod);
                        return Response.ok().entity(prodResp).build();
                    }
                }else{
                    return Response
                            .status(Response.Status.NO_CONTENT)
                            .entity("No hay un producto registrado con la id recibida")
                            .type(MediaType.TEXT_PLAIN)
                            .build();
                }
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al insertar el producto desde TRAZ: especie: " + entity.getId_especie_local() + " clase: " + entity.getId_clase() + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al insertar el producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }    
    
    /**
     * Método para obtener un producto según su id
     * @param id Long identificador del producto
     * @return Response con el ProductoResponseDTO o el mensaje que corresponda
     */
    @GET
    @Path("/{id}")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getProductoById(@PathParam("id") Long id) {
        try{
            Producto prod = prodFacade.find(id);
            if(prod != null){
                // instancia el ResponseDTO
                ProductoResponseDTO prodResp = new ProductoResponseDTO();
                // setea el ResponseDTO
                setearProductoResponse(prodResp, prod);
                
                return Response.ok(prodResp).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay un producto con la id remitida.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
            
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener el producto para TRAZ según la id: " + id + ". " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener el producto. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /************
     * FACTORES * 
     ************/    
    
    /**
     * Método para obtener los factores de transformación y residuos para según 
     * los tres parámetros.
     * Para CGL y TRAZ
     * @param grupo int grupo de la especie del producto
     * @param nivel int nivel de desempeño del establecimiento
     * @param grado int grado de elaboración de la clase a elaborar
     * @return Response con el FactoresTransformacionDTO o el mensaje que corresponda
     */    
    @GET
    @Path("factores/grados")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)    
    public Response getFactoresByGrupoAndNivelAndGrado(
            @QueryParam("grupo") @DefaultValue("0") int grupo, 
            @QueryParam("nivel") @DefaultValue("0") int nivel, 
            @QueryParam("grado") @DefaultValue("0") int grado) {
        try{
            FactoresTransformacion factor = factoresFacade.getFatoresByGrupoNivelGrado(grupo, nivel, grado);
            if(factor != null){
                // setea el ResponseDTO
                FactoresTransformacionDTO factorDTO = new FactoresTransformacionDTO();
                setearFactorTansformacionResponse(factorDTO, factor);
                return Response.ok(factorDTO).build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("No hay factores de transformación con los grupo, nivel y grado recibidos")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }
        }catch(IllegalArgumentException | UriBuilderException ex){
            LOG.fatal("Hubo un error al obtener los factores de transformación para el grupo: " + grupo + ", el nivel: " + nivel + " y el grado" + grado + ". En TRAZ " + ex.getMessage());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Hubo un error al obtener los factores de transformación de productos. " + ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Método para obtener los factores de transformación y residuos para según 
     * los dos parámetros.
     * Para CGL y TRAZ
     * @param grupo int grupo de la especie del producto
     * @param nivel int nivel de desempeño del establecimiento
     * @return Response con el listado de FactoresTransformacionDTO
     */
    @GET
    @Path("factores")
    @Secured
    @Produces(value = MediaType.APPLICATION_JSON)      
    public Response getFactoresByGrupoAndNivel(
            @QueryParam("grupo") @DefaultValue("0") int grupo, 
            @QueryParam("nivel") @DefaultValue("0") int nivel) {

        List<FactoresTransformacionDTO> factoresDTO = new ArrayList<>();
        FactoresTransformacionDTO factorDTO;
        List<FactoresTransformacion> factores = factoresFacade.getFactoresByGrupoNivel(grupo, nivel);
        if(!factores.isEmpty()){
            for(FactoresTransformacion factor : factores){
                factorDTO = new FactoresTransformacionDTO();
                setearFactorTansformacionResponse(factorDTO, factor);
                factoresDTO.add(factorDTO);
            }
        }
        return Response.ok(factoresDTO).build();
    }
    /********************
     * Métodos privados *
     ********************/
    
    /**
     * Método para setear los atributos de una especie local
     * @param especie ProductoEspecieLocal especie vacía
     * @param entity EspecieLocalDTO con los datos a setear
     */
    private void setearEspecie(ProductoEspecieLocal especie, EspecieLocalDTO entity){
        especie.setIdTax(entity.getId_tax());
        especie.setNombreCientifico(entity.getNombre_cientifico());
        especie.setNombreVulgar(entity.getNombre_vulgar());
        especie.setGrupoEspecie(entity.getGrupo_especie());
        especie.setObs(entity.getObservaciones());
        especie.setHabilitado(entity.isHabilitado());
    }

    /**
     * Método para setear los atributos de una unidad de medida
     * @param unidad ProductoUnidadMedida unidad de medida vacía
     * @param entity UnidadMedidaDTO con los datos a setear
     */
    private void setearUnidad(ProductoUnidadMedida unidad, UnidadMedidaDTO entity) {
        unidad.setNombre(entity.getNombre());
        unidad.setAbreviatura(entity.getAbreviatura());
        TipoParam tipo = tipoParamFacade.getExistente("TIPO_NUMERICO");
        unidad.setTipoNum(paramFacade.getExistente(entity.getTipo_numero(), tipo));
        unidad.setHabilitado(entity.isHabilitado());
    }

    /**
     * Método para setear los atributos de una clase de producto
     * @param clase ProductoClase Clase de producto vacía
     * @param entity ClaseProdRequestDTO con los datos a setar
     */
    private void setearClase(ProductoClase clase, ClaseProdRequestDTO entity, ProductoUnidadMedida unidad) {
        clase.setNombre(entity.getNombre());
        clase.setUnidad(unidad);
        clase.setHabilitado(entity.isHabilitado());
        clase.setNivelTransformacion(entity.getNivel_transformacion());
        // obtengo la clase origen si no es nula
        if(entity.getId_clase_origen() != null){
            ProductoClase claseOrigen = claseFacade.find(entity.getId_clase_origen());
            clase.setClaseOrigen(claseOrigen);
        }
        // continúo
        clase.setGradoElaboracion(entity.getGrado_elaboracion());
        clase.setGeneraResiduos(entity.isGenera_residuos());
        clase.setFactorTransfDirecto(entity.getFactor_transf_directo());
        clase.setDefinePiezas(entity.isDefine_piezas());
    }  

    /**
     * Método para setear un ResponseDTO de clase a partir de una entidad PoductoClase
     * @param claseResp ClaseProdResponseDTO a setear
     * @param clase ProductoClase con los datos a setear
     */
    private void setearClaseResponse(ClaseProdResponseDTO claseResp, ProductoClase clase) {
        claseResp.setId(clase.getId());
        claseResp.setNombre(clase.getNombre());
        // seteo la unidad
        UnidadMedidaDTO unidad = new UnidadMedidaDTO();
        setearUnidadResponse(unidad, clase.getUnidad());      
        // continúa
        claseResp.setUnidad(unidad);
        claseResp.setHabilitado(clase.isHabilitado());
        claseResp.setNivel_transformacion(clase.getNivelTransformacion());
        // seteo clase origen si contine una [el genérico va sin subclases]
        if(clase.getClaseOrigen() != null){
            ClaseProdGenericoResponseDTO claseOrigen = new ClaseProdGenericoResponseDTO();
            claseOrigen.setId(clase.getClaseOrigen().getId());
            claseOrigen.setNombre(clase.getClaseOrigen().getNombre());
            claseOrigen.setNombre_unidad(clase.getClaseOrigen().getUnidad().getNombre());            
            claseResp.setClase_origen(claseOrigen);
        }
        // continúa
        claseResp.setGrado_elaboracion(clase.getGradoElaboracion());
        claseResp.setGenera_residuos(clase.isGeneraResiduos());
        claseResp.setFactor_transf_directo(clase.getFactorTransfDirecto());
        claseResp.setDefine_piezas(clase.isDefinePiezas());
        
        if(!clase.getSubClases().isEmpty()){
            // si hay subclases, las agrego
            List<SubClaseProdResponseDTO> subclases = new ArrayList<>();
            for(ProductoSubClase scl : clase.getSubClases()){
                SubClaseProdResponseDTO sub = new SubClaseProdResponseDTO();
                sub.setId(scl.getId());
                sub.setFactor_relativo(scl.getFactorRelativo());
                sub.setId_clase_derivada(scl.getClaseDerivada().getId());
                sub.setNombre_clase_derivada(scl.getClaseDerivada().getNombre());
                sub.setUnidad_clase_derivada(scl.getClaseDerivada().getUnidad().getNombre());
                subclases.add(sub);
            }
            claseResp.setSub_clases(subclases);
        }        
    }

    /**
     * Método para setear un ResponseDTO de producto a partir de una entidad Poducto
     * @param prodResp ProductoResponseDTO a setear
     * @param prod Producto con los datos a setear en el response
     */
    private void setearProductoResponse(ProductoResponseDTO prodResp, Producto prod) {
        prodResp.setId(prod.getId());
        // especie local
        EspecieLocalDTO esp_local = new EspecieLocalDTO();
        setearEspecieResponse(esp_local, prod.getEspecieLocal());
        prodResp.setEspecie_local(esp_local);
        // clase
        ClaseProdGenericoResponseDTO clase = new ClaseProdGenericoResponseDTO();
        setearClaseGenericaResponse(clase, prod.getClase());
        prodResp.setClase(clase);
        // completa
        prodResp.setEquival_kg(prod.getEquivalKg());
        prodResp.setEquival_m3(prod.getEquivalM3());
        prodResp.setFecha_alta(prod.getFechaAlta().toString());
        prodResp.setHabilitado(prod.isHabilitado());
    }
    
    /**
     * Metodo para setear los atributos básicos del producto
     * tanto para insertar nuevo como para editar.
     * No incluye usuario, fecha de alta ni habilitado, que se usarán solo para registro
     * @param prod Producto a setear
     * @param entity ProductoRequestDTO objeto recibido para registrar
     * @param especie_local ProductoEspecieLocal para setear y asignar a prtir del id
     * @param clase ProductoClase para setear y asignar a partir de su id
     */
    private void setearProducto(Producto prod, ProductoRequestDTO entity, ProductoEspecieLocal especie_local, ProductoClase clase) {
        // seta la especie
        especie_local = especieFacade.find(entity.getId_especie_local());
        prod.setEspecieLocal(especie_local);
        // setea la clase
        clase = claseFacade.find(entity.getId_clase());
        prod.setClase(clase);
        prod.setEquivalKg(entity.getEquival_kg());
        prod.setEquivalM3(entity.getEquival_m3());
    }

    /**
     * Método para seter una UnidadMedidaDTO Response
     * @param unidadDTO UnidadMedidaDTO a setear
     * @param unidad ProductoUnidadMedida con los datos a setear
     */
    private void setearUnidadResponse(UnidadMedidaDTO unidadDTO, ProductoUnidadMedida unidad) {
        unidadDTO.setId(unidad.getId());
        unidadDTO.setNombre(unidad.getNombre());
        unidadDTO.setAbreviatura(unidad.getAbreviatura());
        unidadDTO.setTipo_numero(unidad.getTipoNum().getNombre());
        unidadDTO.setHabilitado(unidad.isHabilitado());
    }

    /**
     * Método para setear una EspecieLocalDTO Response
     * @param especieDTO EspecieLocalDTO a setear
     * @param especie ProductoEspecieLocal con los datos a setear
     */
    private void setearEspecieResponse(EspecieLocalDTO especieDTO, ProductoEspecieLocal especie) {
        especieDTO.setId(especie.getId());
        especieDTO.setId_tax(especie.getIdTax());
        especieDTO.setNombre_cientifico(especie.getNombreCientifico());
        especieDTO.setNombre_vulgar(especie.getNombreVulgar());
        especieDTO.setGrupo_especie(especie.getGrupoEspecie());
        especieDTO.setObservaciones(especie.getObs());
        especieDTO.setHabilitado(especie.isHabilitado());
    }

    /**
     * Método para setear una ClaseProdGenericoResponseDTO Response
     * @param claseDTO ClaseProdGenericoResponseDTO a setear
     * @param clase ProductoClase con los datos a setear
     */
    private void setearClaseGenericaResponse(ClaseProdGenericoResponseDTO claseDTO, ProductoClase clase) {
        claseDTO.setId(clase.getId());
        claseDTO.setNombre(clase.getNombre());
        claseDTO.setNombre_unidad(clase.getUnidad().getNombre());
        if(!clase.getSubClases().isEmpty()){
            // si hay subclases, las agrego
            List<SubClaseProdResponseDTO> subclases = new ArrayList<>();
            for(ProductoSubClase scl : clase.getSubClases()){
                SubClaseProdResponseDTO sub = new SubClaseProdResponseDTO();
                sub.setId(scl.getId());
                sub.setFactor_relativo(scl.getFactorRelativo());
                sub.setId_clase_derivada(scl.getClaseDerivada().getId());
                sub.setNombre_clase_derivada(scl.getClaseDerivada().getNombre());
                sub.setUnidad_clase_derivada(scl.getClaseDerivada().getUnidad().getNombre());
                subclases.add(sub);
            }
            claseDTO.setSub_clases(subclases);
        }
    }
    
    /**
     * Método para setear un FactoresTransformacionDTO response
     * @param factorDTO FactoresTransformacionDTO objeto a setear
     * @param factor FactoresTransformacion objeto origen
     */
    private void setearFactorTansformacionResponse(FactoresTransformacionDTO factorDTO, FactoresTransformacion factor){
        factorDTO.setId(factor.getId());
        factorDTO.setGrupo_especie(factor.getGrupoEspecie());
        factorDTO.setNivel_desempenio(factor.getNivelDesempenio());
        factorDTO.setGrado_elaboracion(factor.getGradoElaboracion());
        factorDTO.setFactor_residuos_grado(factor.getFactorResiduosGrado());
        factorDTO.setFactor_transf_total(factor.getFactorTransfTotal());
    }
}