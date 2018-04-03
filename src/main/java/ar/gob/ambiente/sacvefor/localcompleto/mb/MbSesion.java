
package ar.gob.ambiente.sacvefor.localcompleto.mb;

import ar.gob.ambiente.sacvefor.localcompleto.entities.Usuario;
import ar.gob.ambiente.sacvefor.localcompleto.facades.UsuarioFacade;
import ar.gob.ambiente.sacvefor.localcompleto.util.CriptPass;
import ar.gob.ambiente.sacvefor.localcompleto.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 * Bean de respaldo para la gestión de la sesión del usuario.
 * Gestiona las vistas login.xhtml, primeraVez.xhtml e index.xhtml para el cambio de clave
 * @author rincostante
 */
public class MbSesion implements Serializable{

    /**
     * Variable privada: dni que actúa como login del usuario
     */  
    private Long dni;
    
    /**
     * Variable privada: clave ingresada por el usuario
     */   
    private String clave;
    
    /**
     * Variable privada: clave encriptada para su validación
     */  
    private String claveEncript;
    
    /**
     * Variable privada: nueva clave solicitada al usuario en su primera sesión
     */  
    private String newClave;
    
    /**
     * Variable privada: repetición de la nueva clave
     */  
    private String newClave2;
    
    /**
     * Variable privada: Usuario logueado
     */ 
    private Usuario usuario;
    
    /**
     * Variable privada: flag que indica si el bean ya está instanciado
     */
    private boolean iniciando;
    
    /**
     * Variable privada: indica si el usuario está o no logeado
     */  
    private boolean logeado = false;
    
    /**
     * Variable privada: Logger para escribir en el log del server
     */  
    static final Logger LOG = Logger.getLogger(MbSesion.class);
    
    ////////////////////////////////////////////
    // campos para la notificación al Usuario //
    ////////////////////////////////////////////
    
    /**
     * Variable privada: sesión de mail del servidor
     */
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    
    /**
     * Variable privada: String mensaje a enviar por correo electrónico
     */  
    private Message mensaje;       
    
    /**
     * Variable privada: EJB inyectado para el acceso a datos de Usuario
     */  
    @EJB
    private UsuarioFacade usuarioFacade;    
    
    /**
     * Constructor
     */
    public MbSesion() {
    }

    ///////////////////////
    // métodos de acceso //
    ///////////////////////
    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getClaveEncript() {
        return claveEncript;
    }

    public void setClaveEncript(String claveEncript) {
        this.claveEncript = claveEncript;
    }

    public String getNewClave() {
        return newClave;
    }

    public void setNewClave(String newClave) {
        this.newClave = newClave;
    }

    public String getNewClave2() {
        return newClave2;
    }

    public void setNewClave2(String newClave2) {
        this.newClave2 = newClave2;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isIniciando() {
        return iniciando;
    }

    public void setIniciando(boolean iniciando) {
        this.iniciando = iniciando;
    }

    public boolean isLogeado() {
        return logeado;
    }

    public void setLogeado(boolean logeado) {
        this.logeado = logeado;
    }
    
    ///////////////////////////////
    // Métodos de inicialización //
    ///////////////////////////////

    /**
     * Método que se ejecuta luego de instanciada la clase, setea el flag de inicio
     */      
    @PostConstruct
    public void init(){
        iniciando = true;
    }       
    
    ////////////////////////
    // Métodos operativos //
    ////////////////////////
    
    /**
     * Método para validar los datos del usuario
     */
    public void login(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        // encripto la contraseña recibida
        claveEncript = "";
        claveEncript = CriptPass.encriptar(clave);
        
        try{
            // valdo el usuario
            usuario = usuarioFacade.validar(dni, claveEncript);
            if(usuario != null){
                logeado = true;
                JsfUtil.addSuccessMessage("Bienvenid@ " + usuario.getNombreCompleto());
                
                // verifico si es la primera vez que inicia sesión
                String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
                if(!usuario.isPrimeraVez()){
                    contextoExterno.redirect(ctxPath);
                }else{
                    contextoExterno.redirect(ctxPath + "/faces/primeraVez.xhtml");
                }
                Date fecha = new Date(System.currentTimeMillis());
                usuario.setFechaUltimoLogin(fecha);
                usuarioFacade.edit(usuario);
                iniciando = false;
            }else{
                logeado = false;
                JsfUtil.addErrorMessage("No se han validado los datos ingresados, alguno de los dos o ambos son incorrectos.");
            }
        }catch(IOException ex){
            LOG.fatal("Hubo un error validando las credenciales del Usuario." + ex.getMessage());
            JsfUtil.addErrorMessage("Hubo un error validando las credenciales del Usuario.");
        }
    }
    
    /**
     * Método para limpiar el formulario de inicio de sesión
     */
    public void limpiarFormLogin(){
        dni = null;
        clave = null;
    }
    
    /**
     * Método para limpiar el formulario de cambio de contraseña
     */
    public void limpiarFormNewClave(){
        newClave2 = null;
        newClave = null;
    }    
    
    /**
     * Método para redireccionar al formulario de cambio de clave
     */
    public void cambiarClave(){
        newClave2 = null;
        newClave = null;
        try{
            ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
            contextoExterno.redirect(ctxPath + "/faces/primeraVez.xhtml");
        }catch(IOException ex){
            LOG.fatal("Hubo un error redireccionando al cambio de clave." + ex.getMessage());
            JsfUtil.addErrorMessage("Hubo un error redireccionando al cambio de clave.");
        }
    }    
    
    /**
     * Método que actualiza la clave del Usuario
     */
    public void actualizarClave(){
        boolean correoEnviado = false;
        // solo proceso si ambas claves son iguales
        if(newClave.equals(newClave2)){
            // seteo primeraVez false y actualizo la clave
            usuario.setPrimeraVez(false);
            claveEncript = CriptPass.encriptar(newClave);
            usuario.setClave(claveEncript);
            // envío correo
            if(!enviarCorreo(usuario.getEmail(), "Se ha modificado la contraseña de su cuenta de Usuario del componente local del SACVeFor.")){
                correoEnviado = false;
                JsfUtil.addErrorMessage("Hubo un error enviando el correo al usuario.");
            }else{
                correoEnviado = true;
                JsfUtil.addSuccessMessage("Se envió un correo electrónico al destinatario con las nuevas credenciales.");
            }
            // actualico el usuario solo si se envió el correo al usuario
            if(correoEnviado){
                try{
                    usuarioFacade.edit(usuario);
                    JsfUtil.addSuccessMessage("La clave se actualizó correctamente.");
                    logout();
                }catch(Exception ex){
                    LOG.fatal("Hubo un error actualizando la clave." + ex.getMessage());
                    JsfUtil.addErrorMessage("Hubo un error actualizando la clave.");
                }
            }else{
                JsfUtil.addErrorMessage("No se ha cambiado la contraseña del Usuario.");
            }
        }else{
            JsfUtil.addErrorMessage("Las claves ingresadas no coinciden.");
        }
    }
    
    /**
     * Método para cerrar la sesión del usuario
     */
    public void logout(){
        ExternalContext contextoExterno = FacesContext.getCurrentInstance().getExternalContext();
        try {
            String ctxPath = ((ServletContext) contextoExterno.getContext()).getContextPath();
            contextoExterno.redirect(ctxPath + "/faces/login.xhtml");
            HttpSession session = (HttpSession) contextoExterno.getSession(false);
            session.invalidate();
        } catch (IOException ex) {
            JsfUtil.addErrorMessage("Hubo un error cerrando la sesión.");
            LOG.fatal("Hubo un error cerrando la sesión." + ex.getMessage());
        } 
    }    
    
    //////////////////////
    // Métodos privados //
    //////////////////////

    /**
     * Método privado para notificar al usuario la actualización de su clave.
     * Envía un correo electrónico al usuario con la nueva clave ingresada.
     * mediante el objeto de sesión para el envío de mails "mailSesion".
     * @param correo String dirección de correo electrónico del usuario
     * @param motivo String cadena con el motívo de la notificación.
     * Utilizado en actualizarClave()
     * @return boolean true o false según el correo se haya enviado correctamente o no
     */
    private boolean enviarCorreo(String correo, String motivo){  
        boolean result;
        String bodyMessage;
        mensaje = new MimeMessage(mailSesion);
        bodyMessage = "<p>Estimado/a</p> "
                + "<p>" + motivo + "</p> "
                + "<p>Las credenciales de acceso son las siguientes:</p> "
                + "<p><strong>usuario:</strong> " + usuario.getLogin() + "<br/> "
                + "<strong>contraseña:</strong> " + newClave + "</p> "
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
