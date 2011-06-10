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

import java.math.BigInteger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class Cert extends CertIDType {
	
	public Cert(XAdESSchemas schema) {
		super(schema);
	}

	/**
	 * @param schema
	 * @param digest
	 * @param issuerSerial
	 */
	public Cert(XAdESSchemas schema, CertDigest digest, IssuerSerial issuerSerial) {
		super(schema, digest, issuerSerial);
	}

	/**
	 * @param schema
	 * @param digestMethod
	 * @param digestValue
	 * @param issuerName
	 * @param serialNumber
	 * @throws InvalidInfoNodeException
	 */
	public Cert(XAdESSchemas schema, String digestMethod, byte[] digestValue, String issuerName, BigInteger serialNumber)
			throws InvalidInfoNodeException {
		super(schema, digestMethod, digestValue, issuerName, serialNumber);
	}

	/**
	 * @param schema
	 * @param digestMethod
	 * @param digestValue
	 * @param issuerName
	 * @param serialNumber
	 */
	public Cert(XAdESSchemas schema, String digestMethod, String digestValue, String issuerName, BigInteger serialNumber) {
		super(schema, digestMethod, digestValue, issuerName, serialNumber);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.DigestAlgAndValueType#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), ConstantesXADES.CERT);
		super.load(element);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.DigestAlgAndValueType#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), ConstantesXADES.CERT);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.AbstractXADESElement#createElement(org.w3c.dom.Document, java.lang.String, java.lang.String)
	 */
	@Override
	public Element createElement(Document doc, String namespaceXDsig, String namespaceXAdES) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXDsig, namespaceXAdES);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.DigestAlgAndValueType#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + ConstantesXADES.CERT);
		super.addContent(res, namespaceXAdES, namespaceXDsig);
		return res;
	}

}
