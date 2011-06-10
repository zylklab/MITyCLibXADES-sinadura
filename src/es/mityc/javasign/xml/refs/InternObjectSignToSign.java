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

import java.util.List;

import org.apache.xml.security.signature.ObjectContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;

/**
 * Representa un objeto que se quiere añadir como un objeto (ds:object) de la firma.
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class InternObjectSignToSign extends AbstractObjectToSign {

	private String encoding;
	private String mimeType;
    private Element data;
	/** Identidad del objeto interno a firmar. */
	private String id = null;
	
	public InternObjectSignToSign() {
	}
	
	public InternObjectSignToSign(String encoding, String mimeType) {
		this.encoding = encoding;
		this.mimeType = mimeType;
	}
	
	public void setData(Element data) {
		this.data = data;
	}
	
	public Element getData() {
		return data;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#getReferenceURI()
	 */
	@Override
	public String getReferenceURI() {
		return id;
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#getObjects(org.w3c.dom.Document)
	 */
	@Override
	public List<ObjectContainer> getObjects(Document doc) {
		List<ObjectContainer> list = super.getObjects(doc);

		ObjectContainer container = new ObjectContainer(doc);
		// Es muy importante añadir el nodo antes de generar el nuevo Id para evitar colisiones (ids repetidos)
        container.appendChild(doc.adoptNode(getData().cloneNode(true)));

        id = UtilidadTratarNodo.newID(doc, "Object-ID-");
		container.setId(id);
		if (getEncoding() != null) {
			container.setEncoding(getEncoding());
		}
		if (getMimeType() != null) {
			container.setMimeType(getMimeType());
		}
		id = ConstantesXADES.ALMOHADILLA + id;

		list.add(container);
		return list;
	}

}
