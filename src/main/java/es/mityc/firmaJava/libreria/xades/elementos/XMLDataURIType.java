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
package es.mityc.firmaJava.libreria.xades.elementos;

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class XMLDataURIType extends AbstractXMLElement {
	
	protected URI value;

	/**
	 * 
	 */
	public XMLDataURIType(URI value) {
		this.value = value;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#addContent(org.w3c.dom.Element)
	 */
	@Override
	public void addContent(Element element) throws InvalidInfoNodeException {
		if (value == null)
			throw new InvalidInfoNodeException("Información insuficiente para escribir nodo XMLDataURIType");
		
		element.setTextContent(value.toString());
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof XMLDataURIType) {
			if (value.equals(((XMLDataURIType)obj).value))
				return true;
		} else if (obj instanceof URI) {
			if (value.equals(obj))
				return true;
		}
		return false;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		Node node = getFirstNonvoidNode(element);
		if (node.getNodeType() != Node.TEXT_NODE)
			throw new InvalidInfoNodeException("Nodo xsd:anyURI no contiene CDATA como primer valor");

		URI uri;
		
		String data = node.getNodeValue();
		if (data == null)
			throw new InvalidInfoNodeException("No hay URI en nodo xsd:anyURI");
		
		try {
			// FIX: Cambia los espacios por %20 para evitar problemas con la clase URI
			data = data.replace(" ", "%20");
			uri = new URI(data);
		} catch (URISyntaxException ex) {
			throw new InvalidInfoNodeException("URI malformada en nodo xsd:anyURI", ex);
		}
		
		this.value = uri;
	}

	/**
	 * @return the value
	 */
	public URI getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(URI value) {
		this.value = value;
	}
	
}
