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

import es.mityc.firmaJava.libreria.xades.elementos.xades.ObjectIdentifier;

/**
 * Contiene un objeto que se firmará e información de apoyo.
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ObjectToSign {
	
	private AbstractObjectToSign objectToSign;
	private String id;
	
	// Información adicional
	private String description = null;
	private ObjectIdentifier objectIdentifier = null;
	private ExtraObjectData extraData = null;
	
	/**
	 * Permite pasar un objeto a firmar, junto con la información sobre dicho objeto a firmar.
	 * 
	 * @param objectToSign .- Objeto a firmar
	 * @param desc .- Descripción del objeto a firmar
	 * @param id .- Objecto identificador del objeto descrito
	 * @param mimeType .- Tipo MIME del objeto descrito
	 * @param encoding .- Codificación en la firma del objeto descrito
	 */
	public ObjectToSign(AbstractObjectToSign objectToSign, String desc, ObjectIdentifier id,
			String mimeType, URI encoding) {
		this.objectToSign = objectToSign;
		this.description = desc;
		this.objectIdentifier = id;
		this.extraData = new ExtraObjectData(mimeType, encoding);
	}
	
	public void setObjectToSign(AbstractObjectToSign objectToSign) {
		this.objectToSign = objectToSign;
	}
	
	public AbstractObjectToSign getObjectToSign() {
		return this.objectToSign;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	public ObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}

	public void setObjectIdentifier(ObjectIdentifier identificador) {
		this.objectIdentifier = identificador;
	}

	public String getMimeType() {
		return extraData.getMimeType();
	}

	public URI getEncoding() {
		return extraData.getEncoding();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
