/**
 * LICENCIA LGPL:
 * 
 * Esta librería es Software Libre; Usted puede redistribuirlo y/o modificarlo
 * bajo los términos de la GNU Lesser General Public License (LGPL)
 * tal y como ha sido publicada por la Free Software Foundation; o
 * bien la versión 2.1 de la Licencia, o (a su elección) cualquier versión posterior.
 * 
 * Esta librería se distribuye con la esperanza de que sea útil, pero SIN NINGUNA
 * GARANTÍA; tampoco las implícitas garantías de MERCANTILIDAD o ADECUACIÓN A UN
 * PROPÓSITO PARTICULAR. Consulte la GNU Lesser General Public License (LGPL) para más
 * detalles
 * 
 * Usted debe recibir una copia de la GNU Lesser General Public License (LGPL)
 * junto con esta librería; si no es así, escriba a la Free Software Foundation Inc.
 * 51 Franklin Street, 5º Piso, Boston, MA 02110-1301, USA.
 * 
 */
package es.mityc.javasign.issues;

import org.junit.Assert;
import org.junit.Test;

import es.mityc.firmaJava.ValidationBase;

/**
 * <p>Prueba de Issue #295.</p>
 * <p>Campo <code>SigningCertificate</code> mal formado.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class Test295 extends ValidationBase {
	
	@Test
	public void test() {
		try {
			if (validateStreamThrowable(loadRes("/issues/295/295.xml"), null, null)) {
				Assert.fail("La firma del test #295 debería ser inválida por malformación del nodo SigningCertificate pero ha dado válida");
			}
		} catch (Throwable th) {
			LOGGER.info(th.getMessage());
			LOGGER.info("", th);
			Assert.fail("Error en test #295: " + th.getMessage());
		}
	}

}
