
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
 * Bean de respaldo para la gestión de la sesión del usuario
 * @author rincostante
 */
public class MbSesion implements Serializable{

    private Long dni;
    private String clave;
    private String claveEncript;
    private String newClave;
    private String newClave2;
    private Usuario usuario;
    private boolean iniciando;
    private boolean logeado = false;
    static final Logger LOG = Logger.getLogger(MbSesion.class);
    
    // campos para la notificación al Usuario
    @Resource(mappedName ="java:/mail/ambientePrueba")    
    private Session mailSesion;
    private Message mensaje;       
    
    @EJB
    private UsuarioFacade usuarioFacade;     
    public MbSesion() {
    }

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
    
    /******************************
     * Métodos de inicialización **
     ******************************/ 

    @PostConstruct
    public void init(){
        iniciando = true;
    }       
    
    /***********************
     * Métodos operativos **
     ***********************/ 
    
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
