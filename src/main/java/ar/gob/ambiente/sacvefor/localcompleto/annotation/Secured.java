
package ar.gob.ambiente.sacvefor.localcompleto.annotation;

/**
 * Interface para la securización de los métodos de las API rest
 * @author rincostante
 */

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@javax.ws.rs.NameBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Secured {

}
