
package ar.gob.ambiente.sacvefor.localcompleto.util;

import ar.gob.ambiente.sacvefor.localcompleto.mb.MbSesion;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Filtro para manejar el acceso
 * @author rincostante
 */
public class LoginFilter implements Filter {
    
    private static final boolean debug = true;

    private FilterConfig filterConfig = null;
    
    public LoginFilter() {
    }    

    /**
     * Método principal de filtrado
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
         // Obtengo el bean que representa el usuario desde el scope sesión
        MbSesion mbSesion = (MbSesion) req.getSession().getAttribute("mbSesion");

        //Proceso la URL que está requiriendo el cliente
        String urlStr = req.getRequestURL().toString().toLowerCase();

        //Si no requiere protección continúo normalmente.
        if (noProteger(urlStr)) {
          chain.doFilter(request, response);
          return;
        }

        //El usuario no está logueado
        if (mbSesion == null || !mbSesion.isLogeado()) {
          res.sendRedirect(req.getContextPath() + "/faces/login.xhtml");
          return;
        }


        //El recurso requiere protección, pero el usuario ya está logueado.
        chain.doFilter(request, response);  
    }
    
    /**
     * Método que devuelve true si la ruta contiene o finaliza con alguno de los valores excptuados del filtrado
     * @param urlStr
     * @return 
     */
    private boolean noProteger(String urlStr) {
    /*
     * Finales o contenidos de rutas que se excluirán del filtro
     */
      if (urlStr.contains("/rest/"))
        return true;
      if (urlStr.endsWith("login.xhtml"))
        return true;
      if (urlStr.endsWith(".jpg"))
        return true;  
      if (urlStr.endsWith(".css"))
        return true;        
      if (urlStr.endsWith(".png"))
        return true;    
      if (urlStr.endsWith(".gif"))
        return true;  
      if (urlStr.endsWith(".ico"))
        return true;       
      return urlStr.contains("/javax.faces.resource/");
    }     

    /**
     * Return the filter configuration object for this filter.
     * @return 
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {        
    }

    /**
     * Init method for this filter
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("LoginFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LoginFilter()");
        }
        StringBuffer sb = new StringBuffer("LoginFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}
