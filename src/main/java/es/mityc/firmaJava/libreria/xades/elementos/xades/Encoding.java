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
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.net.URI;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class Encoding extends AbstractXadesURIElement {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public Encoding(XAdESSchemas schema, URI data) {
		super(schema, ConstantesXADES.XADES_TAG_ENCODING, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public Encoding(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_ENCODING);
	}

}
