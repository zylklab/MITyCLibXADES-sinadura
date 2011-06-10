/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

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
public class DigestAlgAndValue extends DigestAlgAndValueType {

	public DigestAlgAndValue(XAdESSchemas schema) {
		super(schema);
	}

	/**
	 * @param method
	 * @param value
	 */
	public DigestAlgAndValue(XAdESSchemas schema, String method, String value) {
		super(schema, method, value);
	}
	
	public DigestAlgAndValue(XAdESSchemas schema, DigestAlgAndValueType daaavt) {
		super(schema, daaavt.getDigestMethod().getAlgorithm(), daaavt.getDigestValue().getValue());
	}
	
	public DigestAlgAndValue(XAdESSchemas schema, String method, byte[] data) throws InvalidInfoNodeException {
		super(schema, method, data);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.DigestAlgAndValueType#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), ConstantesXADES.DIGEST_ALG_AND_VALUE);
		super.load(element);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.DigestAlgAndValueType#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), ConstantesXADES.DIGEST_ALG_AND_VALUE);
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
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + ConstantesXADES.DIGEST_ALG_AND_VALUE);
		super.addContent(res, namespaceXAdES, namespaceXDsig);
		return res;
	}

}
