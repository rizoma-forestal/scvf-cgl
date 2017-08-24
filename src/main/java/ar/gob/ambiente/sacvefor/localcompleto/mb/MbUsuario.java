
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Parametrica;
import ar.gob.ambiente.sacvefor.localcompleto.entities.TipoParam;
import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.ParametricaFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.TipoParamFacade;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.CriptPass;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Bean de respaldo para la administración de Usuarios
 * @author rincostante
 */
public class MbUsuario {

    // campos para gestionar
    private Usuario usuario;
    private List<Usuario> lstUsuarios;
    private List<Usuario> lstFilters;
    private List<Parametrica> lstRoles;    
    private String pass;
    private boolean view;
    private boolean edit;
    
    // inyección de recursos
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ParametricaFacade rolFacade;     
    @EJB
    private TipoParamFacade tipoParamFacade;
 
    @Resource(mappedName ="java:/mail/ambientePrueba")
    private Session mailSesion;
    private Message mensaje;    
    
    public MbUsuario() {
    }

    /**********************
     * Métodos de acceso **
     **********************/       
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getLstUsuarios() {
        lstUsuarios = usuarioFacade.findAll();
        return lstUsuarios;
    }

    public void setLstUsuarios(List<Usuario> lstUsuarios) {
        this.lstUsuarios = lstUsuarios;
    }

    public List<Usuario> getLstFilters() {
        return lstFilters;
    }

    public void setLstFilters(List<Usuario> lstFilters) {
        this.lstFilters = lstFilters;
    }

    public List<Parametrica> getLstRoles() {
        return lstRoles;
    }

    public void setLstRoles(List<Parametrica> lstRoles) {
        this.lstRoles = lstRoles;
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
    
    /***********************
     * Mátodos operativos **
     ***********************/
    @PostConstruct
    public void init(){
        usuario = new Usuario();
        TipoParam tipoParam = tipoParamFacade.getExistente(ResourceBundle.getBundle("/Config").getString("RolUsuarios"));
        lstRoles = rolFacade.getHabilitadas(tipoParam);
    }   

    /**
     * Método para guardar el Usuario, sea inserción o edición.
     * Previa validación
     */      
    public void save(){
        boolean valida = true;
        boolean correoEnviado = false;
        String passEncrpitpado;
        
        try{
            Usuario usExitente = usuarioFacade.getExistente(usuario.getLogin());
            if(usExitente != null){
                if(usuario.getId() != null){
                    // si edita, no habilito si no es el mismo
                    if(!usExitente.equals(usuario)) valida = false;
                }else{
                    // si no edita no habilito de ninguna manera
                    valida = false;
                }
            }
            if(valida){
                String tempNombreCompleto = usuario.getNombreCompleto();
                usuario.setNombreCompleto(tempNombreCompleto.toUpperCase());
                
                if(usuario.getId() != null){
                    // Si es una edición, en cualquier caso no toco la contraseña y seteo la fecha de modificación
                    Date fechaMod = new Date(System.currentTimeMillis());
                    usuario.setFechaModif(fechaMod);
                    usuarioFacade.edit(usuario);
                    JsfUtil.addSuccessMessage("El Usuario fue guardado con exito");
                }else{
                    pass = CriptPass.generar();
                    passEncrpitpado = CriptPass.encriptar(pass);
                    // asigno la contraseña encriptada
                    usuario.setClave(passEncrpitpado);
                    // envío correo
                    if(!enviarCorreo(usuario.getEmail(), "Se ha creado una cuenta Usuario de acceso al componente local del SACVeFor.")){
                        correoEnviado = false;
                        JsfUtil.addErrorMessage("Hubo un error enviando el correo al usuario");
                    }else{
                        correoEnviado = true;
                        JsfUtil.addSuccessMessage("El correo con las credenciles de acceso fue enviado exitosamente al usuario");
                    }
                    
                    // creo el usuario si se envió el correo
                    if(correoEnviado){
                        // seteo la fecha de alta
                        Date fechaAlta = new Date(System.currentTimeMillis());
                        usuario.setFechaAlta(fechaAlta);
                        // seteo la condición de habilitado
                        usuario.setHabilitado(true);

                        // creo el usuario
                        usuarioFacade.create(usuario);
                        JsfUtil.addSuccessMessage("El Usuario fue registrado con exito.");
                    }else{
                        JsfUtil.addErrorMessage("No se ha podido registrar el Usuario.");
                    }
                }
            }else{
                JsfUtil.addErrorMessage("El Usuario que está tratando de persisitir ya existe, por favor verifique los datos ingresados.");
            }
            limpiarForm();
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error al procesando el Usuario: " + ex.getMessage());
        }
    }

    /**
     * Método para deshabilitar un Usuario. Modificará su condición de habilitado a false.
     * Los Usuarios deshabilitados no podrán acceder al Sistema
     */
    public void deshabilitar(){
        // seteo los datos de modificación
        Date fechaMod = new Date(System.currentTimeMillis());
        usuario.setFechaModif(fechaMod);
        usuario.setHabilitado(false);
        try{
            usuarioFacade.edit(usuario);
            limpiarForm();
            JsfUtil.addSuccessMessage("El Usuario fue deshabilitado con exito");
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error deshabilitando el Usuario: " + ex.getMessage());
        }        
    }    
    
    /**
     * Metodo para volver a los Usuarios a su condición normal
     */
    public void habilitar(){
        // seteo los datos de modificación
        Date fechaMod = new Date(System.currentTimeMillis());
        usuario.setFechaModif(fechaMod);
        usuario.setHabilitado(true);
        try{
            usuarioFacade.edit(usuario);
            limpiarForm();
            JsfUtil.addSuccessMessage("El Usuario fue habilitado con exito");
        }catch(Exception ex){
            JsfUtil.addErrorMessage(ex, "Hubo un error habilitando el Usuario: " + ex.getMessage());
        }  
    }     
    
    /**
     * Método para limpiar el formulario de registro o edición del objeto
     */
    public void limpiarForm(){
        usuario = new Usuario();
    }  
    
    /**
     * Método para habilitar la vista detalle del formulario
     */
    public void prepareView(){
        view = true;
        edit = false;
    }
    
    /**
     * Método para habilitar la vista nuevo del formulario
     */
    public void prepareNew(){
        view = false;
        edit = true;
    }   
    
    /**
     * Método para blanquear la contraseña del Usuario
     * Genera una nueva y la envía por correo al destinatario.
     */
    public void cambiarPass(){
        String passEncrpitpado;
        boolean correoEnviado = false;
        // genero la nueva
        pass = CriptPass.generar();
        passEncrpitpado = CriptPass.encriptar(pass);
        // asigno la contraseña encriptada
        usuario.setClave(passEncrpitpado);
        // envío correo
        if(!enviarCorreo(usuario.getEmail(), "Se ha modificado la contraseña de su cuenta de Usuario del componente local del SACVeFor.")){
            correoEnviado = false;
            JsfUtil.addErrorMessage("Hubo un error enviando el correo al usuario.");
        }else{
            correoEnviado = true;
            JsfUtil.addSuccessMessage("Se envió un correo electrónico al destinatario con las nuevas credenciales.");
        }
        // cambio la contraseña si se envió el correo
        if(correoEnviado){
            usuarioFacade.edit(usuario);
            JsfUtil.addSuccessMessage("La contraseña se modificó con exito");
        }else{
            JsfUtil.addErrorMessage("No se ha cambiado la contraseña del Usuario.");
        }
    }    
    
    /*********************
     * Métodos privados **
     *********************/
    private boolean enviarCorreo(String correo, String motivo){  
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>" + motivo + "</p> "
                + "<p>Las credenciales de acceso son las siguientes:</p> "
                + "<p><strong>usuario:</strong> " + usuario.getLogin() + "<br/> "
                + "<strong>contraseña:</strong> " + pass + "</p> "
                + "Una vez iniciada la sesión por primera vez, podrá cambiar la contraseña por una de su agrado, "
                + "como en el presente caso, las credenciales le serán enviadas a "
                + "esta dirección de correo.</p>"
                + "<p>Por favor, no responda este correo. No divulgue ni comparta las credenciales de acceso.</p> "
                + "<p>Saludos cordiales</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("AutoridadLocal") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DependienteDe") + "</p> "
                + "<p>" + ResourceBundle.getBundle("/Config").getString("DomicilioAutLocal") + "</p><br /> "
                + "Teléfono: " + ResourceBundle.getBundle("/Config").getString("TelAutLocal") + "<br /> "
                + "Correo electrónico: <a href=\"mailto:" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + "\">" + ResourceBundle.getBundle("/Config").getString("CorreoAutLocal") + " </a></p>";     
        try{
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(correo));
            mensaje.setSubject("SACVeFor - Componente local: " + ResourceBundle.getBundle("/Config").getString("Provincia")  + " - Credenciales de acceso" );
            mensaje.setContent(bodyMessage, "text/html; charset=utf-8");
            
            Date timeStamp = new Date(System.currentTimeMillis());
            mensaje.setSentDate(timeStamp);
            
            Transport.send(mensaje);
            
            result = true;
        }catch(MessagingException ex){
            result = false;
            System.out.println("Hubo un error enviando el correo de registro de usuario" + ex.getMessage());
        }
        
        return result;
    }    
}
