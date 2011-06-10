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

import java.net.URI;

/**
 * <p>Guarda información extra sobre codificación y MIME de un objeto.</p>
 *  
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ExtraObjectData {
	
	/** Tipo mime del objeto. */
	private String mimeType = null;
	/** Tipon de encoding en el que se encuentra el objeto. */
	private URI encoding = null;
	
	/**
	 * <p>Constructor.</p>
	 * @param mimeType Tipo mime del objeto 
	 * @param encoding Encoding del objeto (en formato URI) 
	 */
	public ExtraObjectData(String mimeType, URI encoding) {
		super();
		this.mimeType = mimeType;
		this.encoding = encoding;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @return the encoding
	 */
	public URI getEncoding() {
		return encoding;
	}
}
