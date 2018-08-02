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
package es.mityc.javasign.xml.refs;

import es.mityc.firmaJava.libreria.ConstantesXADES;

/**
 * <p>Representa una firma que es contrafirmada.</p>
 * <p>Este objeto sólo debe utilizarse en una firma XAdES 1.3.2 o superior. Si es está firmando mediante un XAdES 1.1.1 o 1.2.2 se
 * recomienda utilizar la clase {@link InternObjectToSign}.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class SignObjectToSign extends InternObjectToSign {

	/**
	 * <p>Constructor.</p>
	 * @param id identidad que tiene la firma contrafirmada
	 */
	public SignObjectToSign(String id) {
		super(id);
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#getType()
	 */
	@Override
	public String getType() {
		return ConstantesXADES.SCHEMA_COUNTER_SIGNATURE;
	}

}
