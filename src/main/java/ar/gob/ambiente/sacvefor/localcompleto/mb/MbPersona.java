
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Autorizacion;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Guia;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Persona;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.AutorizacionFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.GuiaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.PersonaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.PersonaClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoEntidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.rue.client.TipoSociedadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.DepartamentoClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.LocalidadClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.ProvinciaClient;
import ar.gob.ambiente.sacvefor.localcompleto.territ.client.UsuarioClient;
import ar.gob.ambiente.sacvefor.localcompleto.util.EntidadServicio;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import ar.gob.ambiente.sacvefor.localcompleto.util.Token;
import ar.gob.ambiente.sacvefor.servicios.rue.Domicilio;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoEntidad;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoPersona;
import ar.gob.ambiente.sacvefor.servicios.rue.TipoSociedad;
import ar.gob.ambiente.sacvefor.servicios.territorial.CentroPoblado;
import ar.gob.ambiente.sacvefor.servicios.territorial.Departamento;
import ar.gob.ambiente.sacvefor.servicios.territorial.Provincia;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Bean de respaldo para las entidades Persona, cuyos roles podrán ser:
 * Proponente
 * Técino
 * Apoderado
 * Productor
 * Destinatario
 * etc.
 * Gestinoa las vistas aut/personas/ y guia/personas
 * Para el caso de los destinatarios, se deberá consiganar (localmente) al menos un domicilio.
 * Todos los demás podrán consignar un domicilio en el RUE que sería el oficial.
 * @author rincostante
 */
public class MbPersona implements Serializable {
    ///////////////////////////
    // campos para gestionar //
    ///////////////////////////
    
    /**
     * Variable privada: objeto a gestionar
     */
    private Persona persona;
    
    /**
     * Variable privada: listado de los proponentes registrados
     */
    private List<Persona> lstProp;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de proponentes
     */
    private List<Persona> listPropFilter;
    
    /**
     * Variable privada: listado de los técnicos registrados
     */
    private List<Persona> lstTecnicos;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de técnicos
     */
    private List<Persona> listTecFilter;
    
    /**
     * Variable privada: listado de los apoderados registrados
     */
    private List<Persona> lstApod;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de apoderados
     */
    private List<Persona> listApodFilter;
    
    /**
     * Variable privada: listado de los destinatarios registrados
     */
    private List<Persona> lstDestinatarios;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de destinatarios
     */
    private List<Persona> lstDestFilter;
    
    /**
     * Variable privada: listado de los obrajeros registrados
     */
    private List<Persona> lstObrajeros;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de obrajeros
     */
    private List<Persona> lstObrajFilter;
    
    /**
     * Variable privada: objeto para setear el domicilio en el RUE en el caso que la persona sea un Destinatario
     */    
    private Domicilio domicilio;
    
    /**
     * Variable privada: listado de los Transportistas registrados
     */
    private List<Persona> lstTransportistas;
    
    /**
     * Variable privada: listado para el filtrado de la tabla de Transportistas
     */
    private List<Persona> lstTranspFilter;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando no está editable
     */
    private boolean view;
    
    /**
     * Variable privada: flag que indica que el objeto que se está gestionando es existente
     */
    private boolean edit;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */ 
    private static final Logger logger = Logger.getLogger(MbPersona.class.getName());
    
    /**
     * Variable privada: listado de las revisiones de la Persona
     */
    private List<Persona> lstRevisions; 
    
    /**
     * Variable privada: cuit para buscar la persona en el RUE
     */
    private Long cuitBusqRue;
    
    /**
     * Variable privada: objeto persona del paquete paqRue.jar para gestionar las Personas del RUE
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona personaRue;
    
    /**
     * Variable privada: flag que indica si se está subiendo un martillo
     */
    private boolean subeMartillo;
    
    /**
     * Variable privada: MbSesion para gestionar las variables de sesión del usuario
     */  
    private MbSesion sesion;
    
    /**
     * Variable privada: Usuario de sesión
     */
    private Usuario usLogueado;
    
    /**
     * Variable privada: Domicilio a registrar localmente para la persona si es un Destinatario
     * Podrán ser más de uno
     */
    private ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio domicilioLocal;
    
    ///////////////////////////
    // inyección de recursos //
    ///////////////////////////
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Persona
     */  
    @EJB
    private PersonaFacade perFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Parametrica
     */  
    @EJB
    private ParametricaFacade paramFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de TipoParam
     */  
    @EJB
    private TipoParamFacade tipoParamFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Autorizacion
     */  
    @EJB
    private AutorizacionFacade autFacade;
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Guia
     */  
    @EJB
    private GuiaFacade guiaFacade;
    
    //////////////////////////////////////////////////////
    // Clientes REST para la gestión del API de Personas//
    //////////////////////////////////////////////////////
    
    /**
     * Variable privada: Cliente para la API Rest de Presonas en el RUE
     */
    private PersonaClient personaClient;  
    
    /**
     * Variable privada: Cliente para la API Rest de TipoEntidad en el RUE
     */
    private TipoEntidadClient tipoEntClient;
    
    /**
     * Variable privada: Cliente para la API Rest de TipoSociedad en el RUE
     */
    private TipoSociedadClient tipoSocClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Usuarios en el RUE
     */
    private ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient usClientRue;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API del RUE
     */
    private Token tokenRue;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API del RUE
     */
    private String strTokenRue; 
    
    ///////////////////////////////////////////////////////
    // Clientes REST para la gestión del API Territorial //
    ///////////////////////////////////////////////////////
    
    /**
     * Variable privada: Cliente para la API Rest de Provincias en Organización territorial
     */
    private ProvinciaClient provClient;    
    
    /**
     * Variable privada: Cliente para la API Rest de Departamentos en Organización territorial
     */
    private DepartamentoClient deptoClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Localidades en Organización territorial
     */
    private LocalidadClient locClient;
    
    /**
     * Variable privada: Cliente para la API Rest de Usuarios en Organización territorial
     */
    private UsuarioClient usClientTerr;
    
    /**
     * Variable privada: Token obtenido al validar el usuario de la API de Organización territorial
     */
    private Token tokenTerr;
    
    /**
     * Variable privada: Token en formato String del obtenido al validar el usuario de la API de Organización territorial
     */
    private String strTokenTerr;
    
    ////////////////////////////////////////////////////////////////////
    // Campos para la gestión de las Entidades provenientes de la API //
    // RUE en los combos del formulario. ///////////////////////////////
    // Las Entidades de servicio se componen de un par {id | nombre} ///
    //////////////////////////////////////////////////////////////////// 
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los TipoEntidad
     */  
    private List<EntidadServicio> listTipoEntidad;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos del TipoEntidad seleccionado del combo
     */
    private EntidadServicio tipoEntidadSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los TipoSoc.
     */  
    private List<EntidadServicio> listTipoSoc;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos del TipoSoc seleccionado del combo
     */
    private EntidadServicio tipoSocSelected;  
    
    ////////////////////////////////////////////////////////////////////
    // Campos para la gestión de las Entidades provenientes de la API //
    // Territorial en los combos del formulario. ///////////////////////
    ////////////////////////////////////////////////////////////////////
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Provincias
     */    
    private List<EntidadServicio> listProvincias;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Provincia seleccionada del combo
     */
    private EntidadServicio provSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para los Departamentos
     */ 
    private List<EntidadServicio> listDepartamentos;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos del Departamento seleccionada del combo
     */
    private EntidadServicio deptoSelected;
    
    /**
     * Variable privada: List<EntidadServicio> Listado de entidades de servicio con el id y nombre para las Localidades
     */ 
    private List<EntidadServicio> listLocalidades;
    
    /**
     * Variable privada: EntidadServicio Entidad de servicio para setear los datos de la Localidad seleccionada del combo
     */
    private EntidadServicio localSelected;  
    
    //////////////////////////////////////
    // Campos para el seteo de Personas //
    //////////////////////////////////////
    // Persona
    /**
     * Variable privada: tipo de persona a asignar a una persona nueva o para editar una existente en el RUE
     */
    private TipoPersona rueTipoPers;
    
    /**
     * Variable privada: nombre completo de la persona en el RUE
     */
    private String rueNombreCompleto; 
    
    /**
     * Variable privada: razón social de la persona jurídica en el RUE
     */
    private String rueRazonSocial;
    
    /**
     * Variable privada: cuit de la persona en el RUE
     */
    private Long rueCuit;
    
    /**
     * Variable privada: correo electrónico de la persona en el RUE
     */
    private String rueCorreoElectronico;
    
    // Domicilio RUE
    /**
     * Variable privada: calle del domicilio de la persona en el RUE
     */
    private String rueCalle;
    
    /**
     * Variable privada: número de puerta del domicilio de la persona en el RUE
     */
    private String rueNumero;
    
    /**
     * Variable privada: piso en el que se encuentra el domicilio de la persona en el RUE
     */
    private String ruePiso;
    
    /**
     * Variable privada: departamento del domicilio de la persona en el RUE
     */
    private String rueDepto;
    
    /**
     * Variable privada: flag que indica si la persona del RUE está estable
     */
    private boolean rueEditable;
    
    //////////////////////////
    // Listados por persona //
    //////////////////////////
    
    // Autorizaciones
    /**
     * Variable privada: listado de autorizaciones vinculadas a la persona según su rol
     */
    private List<Autorizacion> lstAut;
    
    // Guías
    /**
     * Variable privada: Listado de guías vinculadas a la persona según su rol
     */
    private List<Guia> lstGuias;
    
    /**
     * Constructor
     */        
    public MbPersona() {
    }
    
    ///////////////////////
    // Métodos de acceso //
    ///////////////////////    
    public boolean isSubeMartillo() {    
        return subeMartillo;
    }

    public void setSubeMartillo(boolean subeMartillo) {    
        this.subeMartillo = subeMartillo;
    }

    /**
     * Método para poblar el listado de obrajeros.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los obrajeros
     */
    public List<Persona> getLstObrajeros() {
        try{
            lstObrajeros = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Obrajero")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Obrajeros registrados. " + ex.getMessage());
        }
        return lstObrajeros;
    }

    public void setLstObrajeros(List<Persona> lstObrajeros) {
        this.lstObrajeros = lstObrajeros;
    }

    public List<Persona> getLstObrajFilter() {
        return lstObrajFilter;
    }

    public void setLstObrajFilter(List<Persona> lstObrajFilter) {    
        this.lstObrajFilter = lstObrajFilter;
    }

    public ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio getDomicilioLocal() {
        return domicilioLocal;
    }

    public void setDomicilioLocal(ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio domicilioLocal) {    
        this.domicilioLocal = domicilioLocal;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }
 
    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    public List<Autorizacion> getLstAut() {
        return lstAut;
    }

    public void setLstAut(List<Autorizacion> lstAut) {
        this.lstAut = lstAut;
    }

    public List<Guia> getLstGuias() {
        return lstGuias;
    }
   
    public void setLstGuias(List<Guia> lstGuias) {
        this.lstGuias = lstGuias;
    }

    /**
     * Método para poblar el listado de destinatarios.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los destinatarios
     */
    public List<Persona> getLstDestinatarios() {
        try{
            lstDestinatarios = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Destinatario")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Destinatarios registrados. " + ex.getMessage());
        }
        return lstDestinatarios;
    }

    public void setLstDestinatarios(List<Persona> lstDestinatarios) {
        this.lstDestinatarios = lstDestinatarios;
    }

    public List<Persona> getLstDestFilter() {
        return lstDestFilter;
    }

    public void setLstDestFilter(List<Persona> lstDestFilter) {
        this.lstDestFilter = lstDestFilter;
    }

    /**
     * Método para poblar el listado de transportistas.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los transportistas
     */
    public List<Persona> getLstTransportistas() {
        try{
            lstTransportistas = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Transportista")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Transportistas registrados. " + ex.getMessage());
        }
        return lstTransportistas;
    }

    public void setLstTransportistas(List<Persona> lstTransportistas) {
        this.lstTransportistas = lstTransportistas;
    }

    public List<Persona> getLstTranspFilter() {
        return lstTranspFilter;
    }

    public void setLstTranspFilter(List<Persona> lstTranspFilter) {
        this.lstTranspFilter = lstTranspFilter;
    }

    public LocalidadClient getLocClient() {
        return locClient;
    }
    
    public void setLocClient(LocalidadClient locClient) {
        this.locClient = locClient;
    }

    public boolean isRueEditable() {
        return rueEditable;
    }
 
    public void setRueEditable(boolean rueEditable) {
        this.rueEditable = rueEditable;
    }

    public String getRueNombreCompleto() {
        return rueNombreCompleto;
    }

    public void setRueNombreCompleto(String rueNombreCompleto) {
        this.rueNombreCompleto = rueNombreCompleto;
    }

    public String getRueRazonSocial() {
        return rueRazonSocial;
    }

    public void setRueRazonSocial(String rueRazonSocial) {
        this.rueRazonSocial = rueRazonSocial;
    }

    public Long getRueCuit() {
        return rueCuit;
    }

    public void setRueCuit(Long rueCuit) {
        this.rueCuit = rueCuit;
    }

    public String getRueCorreoElectronico() {
        return rueCorreoElectronico;
    }

    public void setRueCorreoElectronico(String rueCorreoElectronico) {
        this.rueCorreoElectronico = rueCorreoElectronico;
    }

    public String getRueCalle() {
        return rueCalle;
    }

    public void setRueCalle(String rueCalle) {
        this.rueCalle = rueCalle;
    }

    public String getRueNumero() {
        return rueNumero;
    }

    public void setRueNumero(String rueNumero) {
        this.rueNumero = rueNumero;
    }

    public String getRuePiso() {
        return ruePiso;
    }

    public void setRuePiso(String ruePiso) {
        this.ruePiso = ruePiso;
    }

    public String getRueDepto() {
        return rueDepto;
    }

    public void setRueDepto(String rueDepto) {
        this.rueDepto = rueDepto;
    }
    
    public TipoPersona getRueTipoPers() {
        return rueTipoPers;
    }
 
    public void setRueTipoPers(TipoPersona rueTipoPers) {
        this.rueTipoPers = rueTipoPers;
    }

    public List<EntidadServicio> getListProvincias() {
        if(listProvincias == null) listProvincias = new ArrayList<>();
        return listProvincias;
    }

    public void setListProvincias(List<EntidadServicio> listProvincias) {
        this.listProvincias = listProvincias;
    }

    public EntidadServicio getProvSelected() {
        return provSelected;
    }

    public void setProvSelected(EntidadServicio provSelected) {
        this.provSelected = provSelected;
    }

    public List<EntidadServicio> getListDepartamentos() {
        if(listDepartamentos == null) listDepartamentos = new ArrayList<>();
        return listDepartamentos;
    }

    public void setListDepartamentos(List<EntidadServicio> listDepartamentos) {
        this.listDepartamentos = listDepartamentos;
    }

    public EntidadServicio getDeptoSelected() {
        return deptoSelected;
    }

    public void setDeptoSelected(EntidadServicio deptoSelected) {
        this.deptoSelected = deptoSelected;
    }

    public List<EntidadServicio> getListLocalidades() {
        if(listLocalidades == null) listLocalidades = new ArrayList<>();
        return listLocalidades;
    }

    public void setListLocalidades(List<EntidadServicio> listLocalidades) {
        this.listLocalidades = listLocalidades;
    }

    public EntidadServicio getLocalSelected() {
        return localSelected;
    }

    public void setLocalSelected(EntidadServicio localSelected) {
        this.localSelected = localSelected;
    }
      
    /**
     * Método para poblar el listado de técnicos.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los técnicos
     */
    public List<Persona> getLstTecnicos() {
        try{
            lstTecnicos = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Tecnico")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Técnicos registrados. " + ex.getMessage());
        }
        return lstTecnicos;
    }

    public void setLstTecnicos(List<Persona> lstTecnicos) {
        this.lstTecnicos = lstTecnicos;
    }

    public List<Persona> getListTecFilter() {
        return listTecFilter;
    }

    public void setListTecFilter(List<Persona> listTecFilter) {
        this.listTecFilter = listTecFilter;
    }

    /**
     * Método para poblar el listado de apoderados.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los apoderados
     */  
    public List<Persona> getLstApod() {
        try{
            lstApod = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Apoderado")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Apoderados registrados. " + ex.getMessage());
        }
        return lstApod;
    }

    public void setLstApod(List<Persona> lstApod) {
        this.lstApod = lstApod;
    }

    public List<Persona> getListApodFilter() {
        return listApodFilter;
    }

    public void setListApodFilter(List<Persona> listApodFilter) {
        this.listApodFilter = listApodFilter;
    }

    public ar.gob.ambiente.sacvefor.servicios.rue.Persona getPersonaRue() {
        return personaRue;
    }

    public void setPersonaRue(ar.gob.ambiente.sacvefor.servicios.rue.Persona personaRue) {
        this.personaRue = personaRue;
    }

    public Long getCuitBusqRue() {
        return cuitBusqRue;
    }
      
    public void setCuitBusqRue(Long cuitBusqRue) {
        this.cuitBusqRue = cuitBusqRue;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    /**
     * Método para poblar el listado de proponentes.
     * Busca todas las personas con pasandole el rol como parámetro
     * @return List<Persona> listado de todos los proponentes
     */     
    public List<Persona> getLstProp() {
        try{
            lstProp = perFacade.findAllByRol(obtenerRol(ResourceBundle.getBundle("/Config").getString("Proponente")));
        }catch(Exception ex){
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Proponentes registrados. " + ex.getMessage());
        }
        return lstProp;
    }

    public void setLstProp(List<Persona> lstProp) {
        this.lstProp = lstProp;
    }

    public List<Persona> getListPropFilter() {
        return listPropFilter;
    }

    public void setListPropFilter(List<Persona> listPropFilter) {
        this.listPropFilter = listPropFilter;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public List<Persona> getLstRevisions() {
        return lstRevisions;
    }

    public void setLstRevisions(List<Persona> lstRevisions) {
        this.lstRevisions = lstRevisions;
    }

    public List<EntidadServicio> getListTipoEntidad() {
        if(listTipoEntidad == null) listTipoEntidad = new ArrayList<>();
        return listTipoEntidad;
    }

    public void setListTipoEntidad(List<EntidadServicio> listTipoEntidad) {
        this.listTipoEntidad = listTipoEntidad;
    }

    public EntidadServicio getTipoEntidadSelected() {
        return tipoEntidadSelected;
    }

    public void setTipoEntidadSelected(EntidadServicio tipoEntidadSelected) {
        this.tipoEntidadSelected = tipoEntidadSelected;
    }

    public List<EntidadServicio> getListTipoSoc() {
        if(listTipoSoc == null) listTipoSoc = new ArrayList<>();
        return listTipoSoc;
    }

    public void setListTipoSoc(List<EntidadServicio> listTipoSoc) {
        this.listTipoSoc = listTipoSoc;
    }

    public EntidadServicio getTipoSocSelected() {
        return tipoSocSelected;
    }

    public void setTipoSocSelected(EntidadServicio tipoSocSelected) {
        this.tipoSocSelected = tipoSocSelected;
    }
    
    public TipoPersona[] getTiposPersona() {
            return TipoPersona.values();
    }       
    
    ///////////////////////
    // Métodos de inicio //
    ///////////////////////

    /**
     * Método que se ejecuta luego de instanciada la clase e inicializa las entidades a gestionar, 
     * el bean de sesión y el usuario
     */  
    @PostConstruct
    public void init(){
        // seteo la persona y el domicilio para el caso que se trate de un destinatario.
        persona = new Persona();
        domicilio = new Domicilio();
    	// obtento el usuario
	ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        sesion = (MbSesion)ctx.getSessionMap().get("mbSesion");
        usLogueado = sesion.getUsuario();
    } 
    
    /**
     * Método para actualizar el listado de Departamentos al seleccionar una Provincia
     * para la inserción de Personas en el RUP
     */
    public void provinciaChangeListener(){
        localSelected = new EntidadServicio();
        if(provSelected != null){
            getDepartamentosSrv(provSelected.getId());
        }else{
            deptoSelected = new EntidadServicio();
            provSelected = new EntidadServicio();
        }
    }    
    
    /**
     * Método para actualizar el listado de Localidades al seleccionar un Departamento
     */    
    public void deptoChangeListener(){
        getLocalidadesSrv(deptoSelected.getId());
    }     
    
    /**
     * Método para limpiar el formulario cuando cabia el tipo de persona
     */
    public void tipoPersChangeListener(){
        limpiarFormPerRue();
    }
    
    ////////////////////////
    // Métodos operativos //
    //////////////////////// 
    
    /**
     * Método para habilitar la vista detalle del formulario
     */
    public void prepareView(){
        view = true;
        edit = false;
    }  
    
    /**
     * Método para preparar el listado de Autorizaciones por Persona según su rol
     * @param rol String nombre del rol de la persona
     */
    public void prepareViewAut(String rol){
        lstAut = new ArrayList<>();
        Parametrica rolPersona = obtenerRol(rol);
        lstAut = autFacade.getByPersona(persona, rolPersona);
    }
    
    /**
     * Método para limpiar el listado de autorizaciones.
     * Llamado al cerrar el diálogo que muestra las autorizaciones vinculadas a una persona.
     */
    public void limpiarLstAut(){
        lstAut = null;
    }
    
    /**
     * Método para preparar el listado de Guías por Persona según su rol
     * @param rol String nombre del rol de la persona
     */
    public void preparaViewGuias(String rol){
        lstGuias = new ArrayList<>();
        if(rol.equals(ResourceBundle.getBundle("/Config").getString("TegFuente"))){
            // obtengo las Guías de las cuales es titular
            lstGuias = guiaFacade.getByOrigen(persona.getCuit());
        }else{
            // obtengo las Guías que lo tienen como destinatario
            lstGuias = guiaFacade.getByDestino(persona.getCuit());
        }
    }
    
    /**
     * Método para preparar el listado de Guías por Obrajero.
     * Llamado al abrir el diálogo con el listado de guías del Obrajero
     */
    public void prepareViewObrjGuias(){
        lstGuias = new ArrayList<>();
        lstGuias = guiaFacade.getByObrajero(persona);
    }
    
    /**
     * Método para limpiar el listado de guías.
     * Llamado al cerrar el diálogo que muestra las guías vinculadas a una persona.
     */
    public void limpiarLstGuias(){
        lstGuias = null;
    }

    /**
     * Método para habilitar el formulario de creación de una Persona
     */
    public void prepareNew(){
        persona = new Persona();
        view = false;
        edit = true;
    }
    
    /**
     * Método para habilitar la vista edit del formulario.
     * En este caso, además se carga la personaRue con los datos obtenidos 
     * a partir del idRue de la Persona local
     * Para el resto crear y editar.
     */
    public void prepareEdit(){
        cargarPersona();
        view = false;
        edit = true;
    }
    
    /**
     * Método para mostrar los datos de la Persona registrada en el RUE
     * En la vista detalle
     */
    public void verDatosRue(){
        cargarPersona();
    }
    
    /**
     * Método para setear los datos obtenidos del RUE en la Persona
     * @param rol String nombre del Rol de la Persona a generar
     */
    public void setearPersonaByRue(String rol){
        if(personaRue != null){
            // seteo el rol de la Persona
            Parametrica rolPersona = obtenerRol(rol);
            persona.setRolPersona(rolPersona);
            // cacheo en la Persona del RUE
            persona.setIdRue(personaRue.getId());
            if(personaRue.getNombreCompleto() == null) persona.setNombreCompleto(personaRue.getRazonSocial());
            if(personaRue.getRazonSocial() == null) persona.setNombreCompleto(personaRue.getNombreCompleto());
            persona.setCuit(personaRue.getCuit());
            persona.setTipo(personaRue.getTipo().getNombre());
            persona.setEmail(personaRue.getCorreoElectronico());
            personaRue = null;
            view = false;
            edit = true;
        }else{
            JsfUtil.addErrorMessage("Debe seleccionar una Persona del Registro Unico.");
        }
    }
    
    /**
     * Método para subir la imagen del martillo en el subdirectorio temporal
     * El subdirectorio temporal se llama "TMP"
     * Se configuran en el archivo de propiedades configurable "Config.properties"
     * Solicitado para los obrajeros, Provincia de Misiones
     * @param event evento de solicitud de subida del archivo
     */
    public void subirMartilloTmp(FileUploadEvent event){ 
        // subo el archivo al directorio temporal
        try{
            UploadedFile fileMartillo = event.getFile();
            String destino = ResourceBundle.getBundle("/Config").getString("SubdirTemp");
            // obtengo el nombre del archivo
            String nombreArchivo = getNombreArchivoASubir(fileMartillo);
            // si todo salió bien, procedo
            if(nombreArchivo != null){
                // si logré subir el archivo, guardo la ruta
                if(JsfUtil.copyFile(nombreArchivo, fileMartillo.getInputstream(), destino)){
                    JsfUtil.addSuccessMessage("El archivo " + fileMartillo.getFileName() + " se ha subido al servidor con el nombre " + nombreArchivo);
                    persona.setRutaArchivo(destino);
                    persona.setNombreArchivo(nombreArchivo);
                    persona.setRutaTemporal(true);
                }
                // actualizo el flag
                subeMartillo = true;
            }else{
                JsfUtil.addErrorMessage("No se pudo obtener el destino de la imagen del Martillo.");
            }
        }catch(IOException e){
            JsfUtil.addErrorMessage("Hubo un error subiendo la imagen del Martillo" + e.getLocalizedMessage());
        }
}     

    /**
     * Método para guardar la Persona con el rol de Proponente, sea inserción o edición.
     * Previa validación
     */      
    public void saveProponente(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolProp = obtenerRol(ResourceBundle.getBundle("/Config").getString("Proponente"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolProp);
            // valido por el nombre
            if(perExitente != null){
                if(persona.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Proponente") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Proponente") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // si no hubo errores en el guardado definitivo del martillo, persisto la persona
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Proponente") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Proponente") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Proponente") + " : " + ex.getMessage());
        }
    }  
    
    /**
     * Método para guardar la Persona con el rol de Técnico, sea inserción o edición.
     * Previa validación
     */      
    public void saveTecnico(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolTec = obtenerRol(ResourceBundle.getBundle("/Config").getString("Tecnico"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolTec);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona sea edición o inserción
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Tecnico") + " : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para guardar la Persona con el rol de Apoderado, sea inserción o edición.
     * Previa validación
     */      
    public void saveApoderado(){
        boolean valida = true;
        String mensaje = "";
        Parametrica rolApod = obtenerRol(ResourceBundle.getBundle("/Config").getString("Apoderado"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolApod);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según se trata de una edición o inserción
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Apoderado") + " : " + ex.getMessage());
        }
    }  
    
    /**
     * Método para guardar la Persona con el rol de Destinatario, sea inserción o edición.
     * Previa validación
     */      
    public void saveDestinatario(){
        boolean valida = true;
        String mensaje = "";
        Parametrica rolDest = obtenerRol(ResourceBundle.getBundle("/Config").getString("Destinatario"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolDest);
            // valido que tenga al menos un domicilio registrado localmente
            if(persona.getDomicilios().isEmpty()){
                valida = false;
                mensaje = "El " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " debe tener al menos un domicilio registrado localmente. ";
            }
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = mensaje + "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = mensaje + "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según sea edición o insercion
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Destinatario") + " : " + ex.getMessage());
        }
    }   
    
    /**
     * Método para guardar la Persona con el ron de Obrajero, sea inserción o edición.
     * El rol de Obrajero fue incorporado a solicitud de la Provincia de Misiones.
     */
    public void saveObrajero(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolObraj = obtenerRol(ResourceBundle.getBundle("/Config").getString("Obrajero"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolObraj);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // procedo al guardado definitivo de la imagen del martillo
                if(saveMartillo()){
                    // si no hubo errores en el guardado definitivo del martillo, persisto al obrador
                    if(persona.getId() != null){
                        perFacade.edit(persona);
                        JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " fue guardado con exito");
                    }else{
                        // seteo la fecha de alta, habilitado y Rols
                        Date fechaAlta = new Date(System.currentTimeMillis());
                        persona.setFechaAlta(fechaAlta);
                        persona.setHabilitado(true);
                        perFacade.create(persona);
                        JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " fue registrado con exito");
                    }
                }

                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Obrajero") + " : " + ex.getMessage());
        }
    }
    
    /**
     * Método para guardar la Persona con el rol de Transportista, sea inserción o edición.
     * Previa validación
     */      
    public void saveTransportista(){
        String mensaje = "";
        boolean valida = true;
        Parametrica rolDest = obtenerRol(ResourceBundle.getBundle("/Config").getString("Transportista"));
        try{
            Persona perExitente = perFacade.getExistente(persona.getCuit(), rolDest);
            // valido por el nombre
            if(perExitente != null){
                if(perExitente.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!perExitente.equals(persona)){
                        valida = false;
                        mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Transportista") + " con el CUIT que está registrando.";
                    }
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                    mensaje = "Ya existe un " + ResourceBundle.getBundle("/Config").getString("Transportista") + " con el CUIT que está registrando.";
                }
            }
            // si validó, continúo
            if(valida){
                persona.setUsuario(usLogueado);
                // persisto la persona según sea edición o insercion
                if(persona.getId() != null){
                    perFacade.edit(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Transportista") + " fue guardado con exito");
                }else{
                    // seteo la fecha de alta, habilitado y Rols
                    Date fechaAlta = new Date(System.currentTimeMillis());
                    persona.setFechaAlta(fechaAlta);
                    persona.setHabilitado(true);
                    perFacade.create(persona);
                    JsfUtil.addSuccessMessage("El " + ResourceBundle.getBundle("/Config").getString("Transportista") + " fue registrado con exito");
                }
                persona = new Persona();
                edit = false;
            }else{
                JsfUtil.addErrorMessage(mensaje);
            }
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error procesando el " + ResourceBundle.getBundle("/Config").getString("Transportista") + " : " + ex.getMessage());
        }
    }    
    
    /**
     * Método para persistir una Persona en la API RUE, validando los datos, tanto para edición como insert
     * @param rolPersona String nombre del Rol de la Persona que se va a persistir. Si bien no es necesario para el insert,
     * sí lo es para la actualización.
     */
    public void savePerRue(String rolPersona){
        boolean valida = true;
        String mensaje = "", valCuit, valNombre, valDom;
        
        if(personaRue == null){
            personaRue = new ar.gob.ambiente.sacvefor.servicios.rue.Persona();
        }
        
        // valido el domicilio si lo tiene
        valDom = validarDomicilio();
        if(!valDom.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el Domicilio ingresado. " + valDom;
        }
        
        // valido el nombre completo o razón social, según corresponda
        valNombre = validarNombre();
        if(!valNombre.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el Nombre ingresado. " + valNombre;
        }
        
        // valido el rueCuit
        valCuit = validarCuit();
        if(!valCuit.equals("")){
            valida = false;
            mensaje = "Hubo un error validando el CUIT ingresado. " + valCuit;
        }
        
        // si validó persisto
        if(valida){
            try{
                if(personaRue.getId() == 0){
                    // seteo la Persona con los campos del formulario
                    personaRue.setCorreoElectronico(rueCorreoElectronico);
                    personaRue.setCuit(rueCuit);
                    // seteo nombre completo o razón social según corresponda
                    if(rueTipoPers.toString().equals("Persona Física")){
                        personaRue.setNombreCompleto(rueNombreCompleto.toUpperCase());
                    }else{
                        personaRue.setRazonSocial(rueRazonSocial.toUpperCase());
                        personaRue.setTipoSociedad(obtenerTipoSociedad());
                    }
                    // si inserto, asigno el Tipo de Persona y la fecha de alta
                    personaRue.setTipo(rueTipoPers);
                    personaRue.setFechaAlta(new Date(System.currentTimeMillis()));
                }else{
                    // pongo nombre o razón social en mayúsculas
                    String nom;
                    if(personaRue.getTipo().toString().equals("Persona Física")){
                        nom = personaRue.getNombreCompleto().toUpperCase();
                        personaRue.setNombreCompleto(nom);
                    }else{
                        nom = personaRue.getRazonSocial().toUpperCase();
                        personaRue.setRazonSocial(nom);
                        personaRue.setTipoSociedad(obtenerTipoSociedad());
                    }
                }
                 // continúo para cualquier caso con el Tipo de Entidad, la idProvincia y el nombre de la Provincia
                personaRue.setEntidad(obtenerTipoEntidad());
                personaRue.setIdProvinciaGt(Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")));
                personaRue.setProvinciaGestion(ResourceBundle.getBundle("/Config").getString("Provincia"));
                
                
                // utilizo el cliente rest secún corresponda, obtengo el tokenTerr si no está seteado o está vencido
                if(tokenRue == null){
                    getTokenRue();
                }else try {
                    if(!tokenRue.isVigente()){
                        getTokenRue();
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
                }
                personaClient = new PersonaClient();
                Response res;
                if(personaRue.getId() == 0){
                    res = personaClient.create_JSON(personaRue, tokenRue.getStrToken());
                }else{
                    res = personaClient.edit_JSON(personaRue, String.valueOf(personaRue.getId()), tokenRue.getStrToken());
                }
                
                personaClient.close();
                
                switch (res.getStatus()) {
                    case 201:
                        JsfUtil.addSuccessMessage("La Persona se registró exitosamente en el Registro Unico, "
                                + "para continuar, busquela por el CUIT ingresado para registrarla localmente.");
                        limpiarFormPerRue();
                        break;
                    case 200:
                        JsfUtil.addSuccessMessage("La Persona se actualizó exitosamente en el Registro Unico, "
                                + "para completar la actualización, guarde los datos del formulario de edición.");
                        limpiarFormPerRue();
                        setearPersonaByRue(rolPersona);
                        break;
                    default:
                        JsfUtil.addErrorMessage("No se pudo guardar la Persona en el Registro Unico: " + res.getStatus() + " Error del servidor RUE.");
                        break;
                }

            }catch(NumberFormatException ex){
                JsfUtil.addErrorMessage("Hubo al intentar guardar la Persona en el Registro Unico: " + ex.getMessage());
            }
        }else{
            JsfUtil.addErrorMessage("No se pudieron validar los datos ingresados: " + mensaje);
        }
    }
    
    /**
     * Método para buscar la Persona en el RUE a partir del CUIT ingresado
     */
    public void buscarPersonaRue(){
        if(cuitBusqRue != null){
            personaRue = obtenerPersonaRueByCuit();
            if(personaRue == null){
                JsfUtil.addErrorMessage("La Persona correspondiente al CUIT ingresado no está registrada en el Registro Unico.");
            }
        }else{
            JsfUtil.addErrorMessage("Debe ingresar un CUIT.");
        }
    }
    
    /**
     * Método para preparar la apertura del formulario de búsqueda
     */
    public void prepareBuscarCuit(){
        cuitBusqRue = null;
        personaRue = new ar.gob.ambiente.sacvefor.servicios.rue.Persona();
    }
    
    /**
     * Método para deshabilitar una Persona. Modificará su condición de habilitado a false.
     * Las Personas deshabilitadas no estarán disponibles para su selección.
     */
    public void deshabilitar(){ 
        try{
            persona.setHabilitado(false);
            perFacade.edit(persona);
            persona = new Persona();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al deshabilitar la Persona seleccionada: " + ex.getMessage());
        }
    }   
    
    /**
     * Metodo para volver a las Personas a su condición normal.
     */
    public void habilitar(){
        try{
            persona.setHabilitado(true);
            perFacade.edit(persona);
            persona = new Persona();
            view = false;
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al habilitar la Persona seleccionada: " + ex.getMessage());
        }
    }    
    
    /**
     * Método para limpiar el formulario de Proponentes
     */
    public void limpiarFormProp() {
        persona = new Persona();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
    }  
    
    /**
     * Método para limpiar el formulario de Técnicos, apoderados, transportistas y destinatarios
     */
    public void limpiarForm() {   
        persona = new Persona();
    } 
    
    /**
     * Método para iniciar el registro de una Persona en el RUE
     */
    public void prepareNewInsertRue(){
        rueCuit = null;
        cargarTiposEntidad();
        cargarTiposSociedad();
        cargarProvincias();
    }
    
    /**
     * Método para preparar el formulario de registro de domicilios para un Destinatario
     */
    public void prepareDomDest(){
        domicilioLocal = new ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio();
        cargarProvincias();
    }
    
    /**
     * Método para agregar un domicilio al Destinatario, registrado localmente.
     * Registra los datos territoriales y lo agrega al listado de domicilios de la persona.
     */
    public void addDomLocal(){
        // seteo la calle en mayúsculas
        String sCalle = domicilioLocal.getCalle().toUpperCase();
        domicilioLocal.setCalle(sCalle);
        // guardo los datos territoriales
        domicilioLocal.setIdLoc(localSelected.getId());
        domicilioLocal.setLocalidad(localSelected.getNombre());
        domicilioLocal.setDepartamento(deptoSelected.getNombre());
        domicilioLocal.setProvincia(provSelected.getNombre());
        // agrego el domicilio al Destinatario
        persona.getDomicilios().add(domicilioLocal);
        // limpio todo
        limpiarFormDomLoc();
    }
    
    public void deleteDomLocal(){
        persona.getDomicilios().remove(domicilioLocal);
    }
    
    /**
     * Método para limpiar el formulario de registro de domicilios para un destinatario
     */
    public void limpiarFormDomLoc(){
        domicilioLocal = new ar.gob.ambiente.sacvefor.localcompleto.entities.Domicilio();
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
    }
    
    /**
     * Método para preparar la edición de un Vehículo en el RUE
     * Si la Persona persistida en el RUE no tiene Domicilio,
     * Creo y le asigno un Domicilio temporal para su posible seteo
     * y cargo las entidades provincias como para la creación de un domicilio nuevo.
     * Si la Persona tiene Domicilio persisitido cargo las entidades territoriales
     * en función del idLocalidad que tenga asignado el Domicilio en el RUE.
     */
    public void preparaEditRue(){
        rueEditable = true;
        personaRue = buscarPersonaRueById();
        //si no tiene domicilio, asigno un domicilio temporal y le reseteo los datos String
        if(personaRue.getDomicilio() == null){
            Domicilio domTemp = new Domicilio();
            //dom.setId(Long.valueOf(-1));
            domTemp.setCalle("");
            domTemp.setNumero("");
            domTemp.setPiso("");
            domTemp.setDepto("");
            personaRue.setDomicilio(domTemp);
        }

        if(Objects.equals(personaRue.getIdProvinciaGt(), Long.valueOf(ResourceBundle.getBundle("/Config").getString("IdProvinciaGt")))){
            if(personaRue.getDomicilio().getId() > 0){
                cargarEntidadesTerr(personaRue.getDomicilio().getIdLocalidadGt());
            }else{
                cargarProvincias();
            }
            if(!personaRue.getTipo().getNombre().equals("Persona Física")){
                cargarTiposSociedad();
                tipoSocSelected = new EntidadServicio();
                tipoSocSelected.setId(personaRue.getTipoSociedad().getId());
                tipoSocSelected.setNombre(personaRue.getTipoSociedad().getNombre());
            }
            cargarTiposEntidad();
            tipoEntidadSelected = new EntidadServicio();
            tipoEntidadSelected.setId(personaRue.getEntidad().getId());
            tipoEntidadSelected.setNombre(personaRue.getEntidad().getNombre());
        }else{
            rueEditable = false;
        }
    }
    
    /**
     * Método para limpiar el formulario de inserción de Persona en el RUE
     */
    public void limpiarFormPerRue() {
        rueNombreCompleto = "";
        rueRazonSocial = "";
        rueCuit = null;
        rueCorreoElectronico = "";
        rueCalle = "";
        rueNumero = "";
        ruePiso = "";
        rueDepto = "";
        provSelected = new EntidadServicio();
        deptoSelected = new EntidadServicio();
        listDepartamentos = new ArrayList<>();
        localSelected = new EntidadServicio();
        listLocalidades = new ArrayList<>();
        tipoEntidadSelected = new EntidadServicio();
        tipoSocSelected = new EntidadServicio();
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
    
    //////////////////////
    // Métodos privados //
    //////////////////////
    /**
     * Método que obtiene una Persona del RUE mediante el uso de la API correspondiente.
     * Utilizado en buscarPersonaRue()
     * @return ar.gob.ambiente.sacvefor.servicios.rue.Persona persona obtenida del RUE
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona obtenerPersonaRueByCuit(){
        List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
        
        try{
            // instancio el cliente para la obtención de la Persona, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            personaClient = new PersonaClient();
            GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
            Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(cuitBusqRue), null, tokenRue.getStrToken());
            listPersonas = response.readEntity(gType); 
            // cierro el cliente
            personaClient.close();
            if(listPersonas.isEmpty()){
                return null;
            }else{
                return listPersonas.get(0);
            }

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por su cuit desde el "
                    + "servicio REST de RUE", ex.getMessage()});
            return null;
        }
    }    

    /**
     * Método que obtine una Persona determinada del RUE según su id.
     * Utilizado en prepareEdit() y verDatosRue()
     */
    private void cargarPersona() {
        try{
            // instancio el cliente para la obtención de la Persona, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            personaClient = new PersonaClient();
            personaRue = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(persona.getIdRue()), tokenRue.getStrToken());
            personaClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
                    + "servicio REST de RUE", ex.getMessage()});
        }
    } 

    /**
     * Método para cargar el listado de Tipos de Entidad para su selección.
     * Utilizado en prepareNewInsertRue() y preparaEditRue()
     */
    private void cargarTiposEntidad() {
        EntidadServicio tipoEntidad;
        List<TipoEntidad> listSrv;
        
        try{
            // instancio el cliente para la selección de los Tipos de Entidad, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            tipoEntClient = new TipoEntidadClient();
            // obtengo el listado de Tipos de Entidad 
            GenericType<List<TipoEntidad>> gType = new GenericType<List<TipoEntidad>>() {};
            Response response = tipoEntClient.findAll_JSON(Response.class, tokenRue.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con los Tipos de Entidad como un objeto Entidad Servicio
            listTipoEntidad = new ArrayList<>();
            for(TipoEntidad te : listSrv){
                tipoEntidad = new EntidadServicio(te.getId(), te.getNombre());
                listTipoEntidad.add(tipoEntidad);
            }
            tipoEntClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Tipos de Entidad para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando los Tipos de Entidad desde "
                    + "el servicio REST del RUE.", ex.getMessage()});
        }
    }

    /**
     * Método para cargar el listado de Tipos de Sociedad para su selección.
     * Utilizado en prepareNewInsertRue() y preparaEditRue()
     */
    private void cargarTiposSociedad() {
        EntidadServicio tipoSociedad;
        List<TipoSociedad> listSrv;
        
        try{
            // instancio el cliente para la selección de los Tipos de Sociedad, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            tipoSocClient = new TipoSociedadClient();
            // obtengo el listado de Tipos de Sociedad 
            GenericType<List<TipoSociedad>> gType = new GenericType<List<TipoSociedad>>() {};
            Response response = tipoSocClient.findAll_JSON(Response.class, tokenRue.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con los Tipos de Sociedad como un objeto Entidad Servicio
            listTipoSoc = new ArrayList<>();
            for(TipoSociedad ts : listSrv){
                tipoSociedad = new EntidadServicio(ts.getId(), ts.getNombre());
                listTipoSoc.add(tipoSociedad);
            }
            tipoSocClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Tipos de Sociedad para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando los Tipos de Sociedad desde "
                    + "el servicio REST del RUE.", ex.getMessage()});
        }
    }

    /**
     * Método para cargar el listado de Provincias para su selección.
     * Utilizado en preparaEditRue() y prepareNewInsertRue()
     */
    private void cargarProvincias() {
        EntidadServicio provincia;
        List<Provincia> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token TERR", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            provClient = new ProvinciaClient();
            // obtengo el listado de provincias 
            GenericType<List<Provincia>> gType = new GenericType<List<Provincia>>() {};
            Response response = provClient.findAll_JSON(Response.class, tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el list con las provincias como un objeto Entidad Servicio
            listProvincias = new ArrayList<>();
            for(Provincia prov : listSrv){
                provincia = new EntidadServicio(prov.getId(), prov.getNombre());
                listProvincias.add(provincia);
                //provincia = null;
            }
            // cierro el cliente
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error cargando el listado de Provincias para su selección.");
            // lo escribo en el log del server
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error cargando las Provincias desde "
                    + "el servicio REST de Centros poblados.", ex.getMessage()});
        }
    }

    /**
     * Método que carga el listado de Departamentos según la Provincia seleccionada.
     * Utilizado en cargarEntidadesTerr(Long idLocalidad) y provinciaChangeListener()
     * @param id Long identificador de la provincia en Organización Territorial
     */
    private void getDepartamentosSrv(Long idProv) {
        EntidadServicio depto;
        List<Departamento> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token TERR", ex.getMessage()});
            }
            // instancio el cliente para la selección de los Departamentos
            provClient = new ProvinciaClient();
            // obtngo el listado
            GenericType<List<Departamento>> gType = new GenericType<List<Departamento>>() {};
            Response response = provClient.findByProvincia_JSON(Response.class, String.valueOf(idProv), tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listDepartamentos = new ArrayList<>();
            for(Departamento dpt : listSrv){
                depto = new EntidadServicio(dpt.getId(), dpt.getNombre());
                listDepartamentos.add(depto);
            }
            
            provClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Departamentos de la Provincia seleccionada. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Departamentos por Provincia "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }

    /**
     * Método que carga el listado de Localidades según el Departamento seleccionado
     * Utilizados en deptoChangeListener() y cargarEntidadesTerr(Long idLocalidad)
     * @param id Long identificación del departamento
     */    
    private void getLocalidadesSrv(Long idDepto) {
        EntidadServicio local;
        List<CentroPoblado> listSrv;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token TERR", ex.getMessage()});
            }
            // instancio el cliente para la selección de las Localidades
            deptoClient = new DepartamentoClient();
            // obtngo el listado
            GenericType<List<CentroPoblado>> gType = new GenericType<List<CentroPoblado>>() {};
            Response response = deptoClient.findByDepto_JSON(Response.class, String.valueOf(idDepto), tokenTerr.getStrToken());
            listSrv = response.readEntity(gType);
            // lleno el listado de los combos
            listLocalidades = new ArrayList<>();
            for(CentroPoblado loc : listSrv){
                local = new EntidadServicio(loc.getId(), loc.getNombre() + " - " + loc.getCentroPobladoTipo().getNombre());
                listLocalidades.add(local);
            }
            
            deptoClient.close();
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los Centros poblados del Departamento seleccionado. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo los Centros poblados por Departamento "
                    + "del servicio REST de centros poblados", ex.getMessage()});
        }
    }
    
    /**
     * Método para cargar entidades de servicio y los listados, para actualizar el Domicilio de la Persona.
     * Utilizado en preparaEditRue()
     */
    private void cargarEntidadesTerr(Long idLocalidad){
        CentroPoblado cp;
        
        try{
            // obtengo el tokenTerr si no está seteado o está vencido
            if(tokenTerr == null){
                getTokenTerr();
            }else try {
                if(!tokenTerr.isVigente()){
                    getTokenTerr();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token TERR", ex.getMessage()});
            }
            // instancio el cliente para la selección de las provincias
            locClient = new LocalidadClient();
            cp = locClient.find_JSON(CentroPoblado.class, String.valueOf(idLocalidad), tokenTerr.getStrToken());
            // cierro el cliente
            locClient.close();
            // instancio las Entidades servicio
            localSelected = new EntidadServicio(cp.getId(), cp.getNombre());
            deptoSelected = new EntidadServicio(cp.getDepartamento().getId(), cp.getDepartamento().getNombre());
            provSelected = new EntidadServicio(cp.getDepartamento().getProvincia().getId(), cp.getDepartamento().getProvincia().getNombre());
            // cargo el listado de provincias
            cargarProvincias();
            // cargo el listado de Departamentos
            getDepartamentosSrv(provSelected.getId());
            // cargo el listado de Localidades
            getLocalidadesSrv(deptoSelected.getId());

        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo los datos territoriales del Domicilio de la Persona. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Centro poblado por id desde el "
                    + "servicio REST de centros poblados", ex.getMessage()});
        }
    }     

    /**
     * Método para validar el CUIT ingresado para persistir la Persona en el RUE.
     * Utilizado en savePerRue(String rolPersona)
     * @return String resultado de la validación
     */
    private String validarCuit() {
        String result = "";
        Long c;
        
        if(personaRue.getId() != 0){
            c = personaRue.getCuit();
        }else{
            c = rueCuit;
        }
        
        String strCuit = String.valueOf(c);
        // dejo solo números
        strCuit = strCuit.replaceAll("[^\\d]", "");
        // valido la cantidad de números
        if(strCuit.length() == 11){
            // armo un array de caracteres
            char[] cuitArray = strCuit.toCharArray();
            // inicializo un array de enterios por cada uno de los cuales se multiplicará cada uno de los dígitos del rueCuit a validar
            Integer[] serie = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
            // instancio una variable auxiliar para guardar los resultados del calculo del número validador
            Integer aux = 0;
            // recorro el ambos arrays y opero
            for (int i=0; i<10; i++){
                aux += Character.getNumericValue(cuitArray[i]) * serie[i];
            }
            // ejecuto la especificación (Módulo 11): 11 menos el resto de de la división de la suma de productos anterior por 11
            aux = 11 - (aux % 11);
            //Si el resultado anterior es 11 el código es 0
            if (aux == 11){
                aux = 0;
            }
            //Si el resultado anterior es 10 el código no tiene que validar, cosa que de todas formas pasa
            //en la siguiente comparación.
            //Devuelve verdadero si son iguales, falso si no lo son
            //(Paso todo a Object para prevenir errores, se puede usar: Integer.valueOf(cuitArray[10]) == aux;)
            Object oUltimo = (Integer)Character.getNumericValue(cuitArray[10]);
            Object oAux = aux;
            
            if(!oUltimo.equals(oAux)){
                result = "El CUIT ingresado es inválido.";
            }
            
            // Si valida, verifico que no está registrado ya en el RUE
            if(result.equals("")){
                try{
                    // instancio el cliente para verificar la existencia de una Persona con el mismo CUIT
                    List<ar.gob.ambiente.sacvefor.servicios.rue.Persona> listPersonas = new ArrayList<>();
                    // obtengo el tokenTerr si no está seteado o está vencido
                    if(tokenRue == null){
                        getTokenRue();
                    }else try {
                        if(!tokenRue.isVigente()){
                            getTokenRue();
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
                    }
                    personaClient = new PersonaClient();
                    GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>> gType = new GenericType<List<ar.gob.ambiente.sacvefor.servicios.rue.Persona>>() {};
                    Response response = personaClient.findByQuery_JSON(Response.class, null, String.valueOf(String.valueOf(c)), null, tokenRue.getStrToken());
                    listPersonas = response.readEntity(gType);
                    // cierro el cliente
                    personaClient.close();
                    if(!listPersonas.isEmpty()){
                        // actúo según sea una actualización o un insert
                        if(personaRue.getId() == 0){
                            // si inserta devuelvo el error
                            result = "Ya existe una Persona en el Registro Unico con el CUIT ingresado, por favor, verifique los datos.";
                        }else{
                            // si edita, verifico que no sea la misma Persona
                            if(!Objects.equals(listPersonas.get(0).getId(), personaRue.getId())){
                                // si no son la misma Persona, devuelvo error
                                result = "Ya existe una Persona en el Registro Unico con el CUIT ingresado, por favor, verifique los datos.";
                            }
                        }
                    }
                }catch(ClientErrorException ex){
                    result = "Hubo error validando el CUIT en el Registro Unico. " + ex.getMessage();
                }
            }            
        }    
        return result;
    }

    /**
     * Método para validar el nombre completo o razón social de la Persona para ser persistida en el RUE, 
     * según sea una edición o una inserción.
     * En cualquier caso, devuelve "" si validó o un mensaje si no validó.
     * Utilizado en savePerRue(String rolPersona)
     * @return String resultado de la validación
     */
    private String validarNombre() {
        String result = "", tipoPer, nomCom, razSoc;
        
        if(personaRue.getId() != 0){
            tipoPer = personaRue.getTipo().toString();
            nomCom = personaRue.getNombreCompleto();
            razSoc = personaRue.getRazonSocial();
        }else{
            tipoPer = rueTipoPers.toString();
            nomCom = rueNombreCompleto;
            razSoc = rueRazonSocial;
        }
        
        if(tipoPer.equals("Persona Física")){
            if(nomCom.equals("")){
                result = result + "Debe ingresar un Nombre completo para la Persona";
            }
        }else{
            if(razSoc.equals("")){
                result = result + "Debe ingresar una Razón social para la Persona";
            }
            if(tipoSocSelected.getId() == null){
                result = result + "Debe ingresar un Tipo de Sociedad";
            }
        }
        
        return result;
    }

    /**
     * Método para validar el domicilio según sea una edición o una inserción.
     * Si se trata de una edición valido los datos y opero según las tres alternativas posibles:
     * 1: Domicilio completo, 2: Domicilio vacío, 3: Domicilio incompleto.
     * caso 1 => seteo el Domicilio;
     * caso 2 => elimino el Domicilio de la Persona;
     * caso 3 => seteo el mensaje de error;
     * En cualquier caso, devuelve "" si validó o un mensaje si no validó.
     * Utilizado en savePerRue(String rolPersona)
     * @return String resultado de la validación
     */
    private String validarDomicilio() {
        String result = "", valid;

        // defino los campos a validar según sea edición o insert
        if(personaRue.getId() != 0){
            /**
             * Actúo según el Domicilio estuviera o no persisitido con anterioridad, 
             * dado que al prepararlo para la edición, en cualquier caso, ya se le asignó un Domicilio, persistido o no.
             */
            if(!personaRue.getDomicilio().getCalle().equals("") &&
                        !personaRue.getDomicilio().getNumero().equals("") &&
                        localSelected != null && deptoSelected != null && provSelected != null){
                // si el Domicilio está completo lo seteo y devuelvo el mensaje vacío
                setearDom();
                result = "";
            }else if(personaRue.getDomicilio().getCalle().equals("") &&
                        personaRue.getDomicilio().getNumero().equals("") &&
                        localSelected == null && deptoSelected == null && provSelected == null){
                // si el domicilio está vacío, lo elimino de la Persona y devuelvo el mensaje vacío
                personaRue.setDomicilio(null);
                result = "";
            }else{
                // si el domicilio está incompleto, seteo el mensaje de error y lo devuelvo
                valid = setearErrorDomEdit();
                result = valid + " No se ha podido validar el Domicilio";
            }
        }else if(!rueCalle.equals("") || !rueNumero.equals("")){
            // se considera que hay domicilio seteado, valido los datos obligarorios
            valid = setearErrorDomNuevo();
            if(!valid.equals("")){
                // si está incompleto termino de armar el mensaje.
                result = valid + " No se ha podido validar el Domicilio";
            }else{
                result = "";
                // instancio el domicilio
                Domicilio dom = new Domicilio();
                dom.setCalle(rueCalle.toUpperCase());
                dom.setNumero(rueNumero);
                dom.setPiso(ruePiso);
                dom.setDepto(rueDepto);
                dom.setIdLocalidadGt(localSelected.getId());
                dom.setDepartamento(deptoSelected.getNombre());
                dom.setProvincia(provSelected.getNombre());
                dom.setLocalidad(localSelected.getNombre());
                // asigno el domicilio a la PersonaRue
                personaRue.setDomicilio(dom);
            }
        }else{
            // si no hay domicilio seteado lo quito
            personaRue.setDomicilio(null);
        }
        
        return result;
    }

    /**
     * Método para obtener el Tipo de Entidad seleccionado a partir de la EntidadServicio.
     * Utilizada en savePerRue(String rolPersona)
     * @return TipoEntidad tipo de entidad solicitada
     */
    private TipoEntidad obtenerTipoEntidad() {
        try{
            // instancio el cliente para la selección de los Tipos de Entidad, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            tipoEntClient = new TipoEntidadClient();
            TipoEntidad response = tipoEntClient.find_JSON(TipoEntidad.class, String.valueOf(tipoEntidadSelected.getId()), tokenRue.getStrToken());
            tipoEntClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo el Tipo de Entidad de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Tipo de Entidad por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método para obteber el Tipo de Sociedad seleccionado a partir de la EntidadServicio.
     * Utilizado en savePerRue(String rolPersona)
     * @return TipoSociedad tipo de sociedad solicitada
     */
    private TipoSociedad obtenerTipoSociedad() {
        try{
            // instancio el cliente para la selección de los Tipos de Sociedad, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            tipoSocClient = new TipoSociedadClient();
            TipoSociedad response = tipoSocClient.find_JSON(TipoSociedad.class, String.valueOf(tipoSocSelected.getId()), tokenRue.getStrToken());
            tipoSocClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo el Tipo de Sociedad de la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo el Tipo de Sociedad por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método que obtiene una Persona desde la API RUE, según su id.
     * Utilizada en preparaEditRue()
     * @return ar.gob.ambiente.sacvefor.servicios.rue.Persona obtenida del RUE
     */
    private ar.gob.ambiente.sacvefor.servicios.rue.Persona buscarPersonaRueById() {
        try{
            // instancio el cliente para la selección de la Persona RUE, obtengo el tokenTerr si no está seteado o está vencido
            if(tokenRue == null){
                getTokenRue();
            }else try {
                if(!tokenRue.isVigente()){
                    getTokenRue();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la vigencia del token RUE", ex.getMessage()});
            }
            personaClient = new PersonaClient();
            ar.gob.ambiente.sacvefor.servicios.rue.Persona response = personaClient.find_JSON(ar.gob.ambiente.sacvefor.servicios.rue.Persona.class, String.valueOf(persona.getIdRue()), tokenRue.getStrToken());
            personaClient.close();
            return response;
        }catch(ClientErrorException ex){
            // muestro un mensaje al usuario
            JsfUtil.addErrorMessage("Hubo un error obteniendo la Persona del Registro Unico. " + ex.getMessage());
            logger.log(Level.SEVERE, "{0} - {1}", new Object[]{"Hubo un error obteniendo la Persona por id desde el "
                    + "servicio REST del RUE", ex.getMessage()});
            return null;
        }
    }

    /**
     * Método para setear el mensaje de error de validación de Domicilio durante la edición.
     * Valida que los datos obligatorios estén completos, en cuyo caso arma un mensaje específico.
     * O devuelve una cadena vacía si el Domicilio está completo.
     * Utilizado en validarDomicilio(String rolPersona)
     * @return String mensaje correspondiente
     */
    private String setearErrorDomEdit() {
        String result = "Está ingresando un domicilio.";
        if(personaRue.getDomicilio().getCalle().equals("")) result = result + " Debe ingresar una Calle.";
        if(personaRue.getDomicilio().getNumero().equals("")) result = result + " Debe ingresar un Némero de puerta.";
        if(localSelected == null) result = result + " Debe seleccionar una Localidad.";
        if(deptoSelected == null) result = result + " Debe seleccionar un Departamento.";
        if(provSelected == null) result = result + " Debe seleccionar una Provincia.";
        if(!result.equals("Está ingresando un domicilio.")){
            result = result + " No se ha podido validar el Domicilio";
        }else{
            result = "";
        }
        return result;
    }

    /**
     * Método que setea el Domicilio para ser persistido en el RUE al editar la Persona.
     * Utilizado en validarDomicilio(String rolPersona)
     */
    private void setearDom() {
        // pongo la calle en mayúsculas
        String calle = personaRue.getDomicilio().getCalle();
        personaRue.getDomicilio().setCalle(calle.toUpperCase());
        // seteo el resto de los elementos
        personaRue.getDomicilio().setIdLocalidadGt(localSelected.getId());
        personaRue.getDomicilio().setDepartamento(deptoSelected.getNombre());
        personaRue.getDomicilio().setProvincia(provSelected.getNombre());
        personaRue.getDomicilio().setLocalidad(localSelected.getNombre());
    }
    /**
     * Método para setear el mensaje de error de validación de Domicilio nuevo.
     * Valida que los datos obligatorios estén completos, en cuyo caso arma un mensaje específico.
     * O devuelve una cadena vacía si el Domicilio está completo.
     * Utilizado en validarDomicilio(String rolPersona)
     * @return String mensaje correspondiente
     */
    private String setearErrorDomNuevo() {
        String result = "Está ingresando un domicilio.";
        if(rueCalle.equals("")) result = result + " Debe ingresar una Calle.";
        if(rueNumero.equals("")) result = result + " Debe ingresar un Némero de puerta.";
        if(localSelected == null) result = result + " Debe seleccionar una Localidad.";
        if(deptoSelected == null) result = result + " Debe seleccionar un Departamento.";
        if(provSelected == null) result = result + " Debe seleccionar una Provincia.";
        if(!result.equals("Está ingresando un domicilio.")){
            result = result + " No se ha podido validar el Domicilio";
        }else{
            result = "";
        }
        return result;
    }
    
    /**
     * Método para nombrear un archivo subido, en este caso, el Martillo del Obrajero
     * @param file archivo a nombrar
     * @return String nombre del archivo
     */
    private String getNombreArchivoASubir(UploadedFile file){
        Date date = new Date();
        String extension = file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1);
        String sufijo = JsfUtil.getDateInString(date);
        String nombreArchivo = ResourceBundle.getBundle("/Config").getString("IdProvinciaGt") + "_" + persona.getCuit() + "_" + sufijo + "." + extension;
        return nombreArchivo;
    }    
    
    /**
     * Método para guardar la imagen del martillo a un directorio definitivo.
     * Completado el renombrado y guardado, elimino el archivo del directorio temporal
     * El directorio es "martillos" y está seteado en el Config.properties
     * @return verdadero o falso según la operación haya resultado exitosa o no.
     */
    private boolean saveMartillo() {
        if(subeMartillo){
            // obtengo la imagen del martillo del directorio temporal
            File martARenombrar = new File(ResourceBundle.getBundle("/Config").getString("SubdirTemp") + persona.getNombreArchivo());
            // instancio un nuevo File para el renombrado con el path al directorio definitivo
            File martDefinitivo = new File(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos") + persona.getNombreArchivo());
            // si existe, lo elimino
            martDefinitivo.delete();
            // renombro y devuelvo el resultado: true si fue existoso, false, si no lo fue.
            if(martARenombrar.renameTo(martDefinitivo)){
                // si todo fue bien, actualizo la ruta en la persona y la condición de temporal de la ruta
                persona.setRutaArchivo(ResourceBundle.getBundle("/Config").getString("RutaArchivos") + 
                        ResourceBundle.getBundle("/Config").getString("SubdirMartillos"));
                persona.setRutaTemporal(false);
                subeMartillo = false;
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    
    /**
     * Método privado que obtiene y setea el tokenTerr para autentificarse ante la API rest de Territorial
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en cargarEntidadesTerr(Long idLocalidad), getDepartamentosSrv(Long idProv) y cargarProvincias()
     */
    private void getTokenTerr(){
        try{
            usClientTerr = new UsuarioClient();
            Response responseUs = usClientTerr.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestTerr"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenTerr = (String)lstHeaders.get(0); 
            tokenTerr = new Token(strTokenTerr, System.currentTimeMillis());
            usClientTerr.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API Territorial: " + ex.getMessage());
        }
    } 
    
    /**
     * Método privado que obtiene y setea el tokenRue para autentificarse ante la API rest del RUE
     * Crea el campo de tipo Token con la clave recibida y el momento de la obtención.
     * Utilizado en buscarPersonaRueById(), obtenerTipoSociedad(), obtenerTipoEntidad(), validarCuit(),
     * cargarTiposSociedad(), cargarTiposEntidad(), cargarPersona(), obtenerPersonaRueByCuit(), savePerRue(String rolPersona)
     */
    private void getTokenRue(){
        try{
            usClientRue = new ar.gob.ambiente.sacvefor.localcompleto.rue.client.UsuarioClient();
            Response responseUs = usClientRue.authenticateUser_JSON(Response.class, ResourceBundle.getBundle("/Config").getString("UsRestRue"));
            MultivaluedMap<String, Object> headers = responseUs.getHeaders();
            List<Object> lstHeaders = headers.get("Authorization");
            strTokenRue = (String)lstHeaders.get(0); 
            tokenRue = new Token(strTokenRue, System.currentTimeMillis());
            usClientRue.close();
        }catch(ClientErrorException ex){
            System.out.println("Hubo un error obteniendo el token para la API RUE: " + ex.getMessage());
        }
    }       
}
