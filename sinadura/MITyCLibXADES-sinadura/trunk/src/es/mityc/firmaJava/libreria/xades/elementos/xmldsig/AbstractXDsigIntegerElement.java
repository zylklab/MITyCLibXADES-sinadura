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
package es.mityc.firmaJava.libreria.xades.elementos.xmldsig;

import java.math.BigInteger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.elementos.XMLDataIntegerType;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class AbstractXDsigIntegerElement extends AbstractXDsigElement {

	private XMLDataIntegerType data;
	private String nameElement;


	public AbstractXDsigIntegerElement(String nameElement, BigInteger data) {
		super();
		this.nameElement = nameElement;
		this.data = new XMLDataIntegerType(data);
	}

	/**
	 * @param nameElement
	 */
	public AbstractXDsigIntegerElement(String nameElement) {
		super();
		this.nameElement = nameElement;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		if (data == null) {
			throw new InvalidInfoNodeException("Información insuficiente para escribir elemento " + nameElement);
		}
		Element res = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, namespaceXDsig + ":" + nameElement);
		data.addContent(res);
		return res;
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xmldsig.AbstractXDsigElement#createElement(org.w3c.dom.Document, java.lang.String)
	 */
	@Override
	public Element createElement(Document doc, String namespaceXDsig) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXDsig);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractXDsigIntegerElement) {
			AbstractXDsigIntegerElement desc = (AbstractXDsigIntegerElement) obj;
			if ((nameElement.equals(desc.nameElement)) && (data.equals(desc.data))) {
				return true;
			}
		} else {
			return data.equals(obj);
		}
		return false;
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, ConstantesXADES.SCHEMA_DSIG, nameElement);
		data = new XMLDataIntegerType(null);
		data.load(element);
	}
	
	public void setValue(BigInteger value) {
		if (data == null) {
			data = new XMLDataIntegerType(value);
		} else {
			data.setValue(value);
		}
	}
	
	public BigInteger getValue() {
		if (data != null) {
			return data.getValue();
		}
		return null;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), ConstantesXADES.SCHEMA_DSIG, nameElement);
	}
}
