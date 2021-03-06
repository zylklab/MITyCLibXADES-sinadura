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

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public enum QualifierEnum {

	OIDAsURI("OIDAsURI"),
	OIDAsURN("OIDAsURN");

	
	private String value;
	
	private QualifierEnum(String value) {
		this.value = value;
	}
	
	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return new String(value);
	}
	
	public static QualifierEnum getQualifierEnum(String value) {
		if (value == null)
			return null;
		if (OIDAsURI.toString().equals(value))
			return OIDAsURI;
		else if (OIDAsURN.toString().equals(value))
			return OIDAsURN;
		return null;
	}

}
