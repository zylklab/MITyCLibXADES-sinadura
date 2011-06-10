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
package es.mityc.firmaJava;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Batería de test para comprobar el estado de la validación de los componentes.
 * 
 */
public class TestValidation extends ValidationBase {

	@Test
	@Ignore
	public void validaCli111BesValid() {
		assertTrue("Firma de cliente 1.1.1, XAdES-BES no validada", validateStream(loadRes("/cliente_1_1_1/BES.xml"), null, null));
	}
	
	@Test
	@Ignore
	public void validaCli111TValid() {
		assertTrue("Firma de cliente 1.1.1, XAdES-T no validada", validateStream(loadRes("/cliente_1_1_1/T.xml"), null, null));
	}

	@Test
	@Ignore
	public void validaCli111XLValid() {
		assertTrue("Firma de cliente 1.1.1, XAdES-XL no validada", validateStream(loadRes("/cliente_1_1_1/XL.xml"), null, null));
	}

	@Test public void validaCli122BesValid() {
		assertTrue("Firma de cliente 1.2.2, XAdES-BES no validada", validateStream(loadRes("/cliente_1_2_2/BES.xml"), null, null));
	}
	
	@Test public void validaCli122TValid() {
		assertTrue("Firma de cliente 1.2.2, XAdES-T no validada", validateStream(loadRes("/cliente_1_2_2/T.xml"), null, null));
	}

	@Test public void validaCli122XLValid() {
		assertTrue("Firma de cliente 1.2.2, XAdES-XL no validada", validateStream(loadRes("/cliente_1_2_2/XL.xml"), null, null));
	}

	@Test public void validaCli132BesValid() {
		assertTrue("Firma de cliente 1.3.2, XAdES-BES no validada", validateStream(loadRes("/cliente_1_3_2/BES.xml"), null, null));
	}
	
	@Test public void validaCli132TValid() {
		assertTrue("Firma de cliente 1.3.2, XAdES-T no validada", validateStream(loadRes("/cliente_1_3_2/T.xml"), null, null));
	}

	@Test public void validaCli132XLValid() {
		assertTrue("Firma de cliente 1.3.2, XAdES-XL no validada", validateStream(loadRes("/cliente_1_3_2/XL.xml"), null, null));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestValidation.class);
	}


}
