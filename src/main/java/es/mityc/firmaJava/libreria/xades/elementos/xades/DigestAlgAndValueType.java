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

import java.security.MessageDigest;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.utilidades.Base64Coder;
import es.mityc.firmaJava.libreria.utilidades.UtilidadFirmaElectronica;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.elementos.xmldsig.DigestMethod;
import es.mityc.firmaJava.libreria.xades.elementos.xmldsig.DigestValue;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * Clase para manejar nodos del tipo DigestAlgAndValueType
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public abstract class DigestAlgAndValueType extends AbstractXADESElement {
	
	private DigestMethod method;
	private DigestValue value;
	
	public DigestAlgAndValueType(XAdESSchemas schema) {
		super(schema);
	}
	
	public DigestAlgAndValueType(XAdESSchemas schema, String method, byte[] data) throws InvalidInfoNodeException {
		super(schema);
		MessageDigest md = UtilidadFirmaElectronica.getMessageDigest(method);
		if (md == null)
			throw new InvalidInfoNodeException("Método desconocido para calcular el digest: " + method);
		this.method = new DigestMethod(method);
		md.update(data);
		byte[] result = md.digest();
		this.value = new DigestValue(new String(Base64Coder.encode(result)));
	}
	
	/**
	 * Construye el objeto indicándole los datos que contendrá
	 */
	public DigestAlgAndValueType(XAdESSchemas schema, String method, String value) {
		super(schema);
		this.method = new DigestMethod(method);
		this.value = new DigestValue(value);
	}

	public DigestMethod getDigestMethod() {
		return method;
	}

	public void setMethod(DigestMethod method) {
		this.method = method;
	}

	public DigestValue getDigestValue() {
		return value;
	}

	public void setValue(DigestValue value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DigestAlgAndValueType) {
			DigestAlgAndValueType huella = (DigestAlgAndValueType) obj;
			if (!method.equals(huella.getDigestMethod()))
				return false;
			if (value.equals(huella.getDigestValue()))
				return true;
		}
		return false;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xmldsig.AbstractXDsigElement#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		Node node = getFirstNonvoidNode(element);
		
		DigestMethod method = new DigestMethod(null);
		if (!method.isThisNode(node))
			throw new InvalidInfoNodeException("Se esperaba nodo DigestMethod en DigestAlgAndValueType");
		method.load((Element)node);
		
		node = getNextNonvoidNode(node);
		DigestValue value = new DigestValue(null);
		if (!value.isThisNode(node))
			throw new InvalidInfoNodeException("Se esperaba nodo DigestValue en DigestAlgAndValueType");
		value.load((Element)node);
			
		this.method = method;
		this.value = value;
	}


	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.AbstractXADESElement#addContent(org.w3c.dom.Element, java.lang.String, java.lang.String)
	 */
	@Override
	public void addContent(Element element, String namespaceXAdES, String namespaceXDsig) throws InvalidInfoNodeException {
		super.addContent(element, namespaceXAdES, namespaceXDsig);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#addContent(org.w3c.dom.Element)
	 */
	@Override
	protected void addContent(Element element) throws InvalidInfoNodeException {
		if ((method == null) || (value == null))
			throw new InvalidInfoNodeException("Información insuficiente para escribir nodo DigestAlgAndValueType");
		element.appendChild(method.createElement(element.getOwnerDocument(), namespaceXDsig));
		element.appendChild(value.createElement(element.getOwnerDocument(), namespaceXDsig));
	}
	

}
