/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.math.BigInteger;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author dsantose
 *
 */
public class CRLIdentifier extends CRLIdentifierType {

	/**
	 * @param schema
	 * @param issuer
	 * @param issueTime
	 * @param number
	 * @param URI
	 */
	public CRLIdentifier(XAdESSchemas schema, String issuer, Date issueTime, BigInteger number, String URI) {
		super(schema, issuer, issueTime, number, URI);
	}
	
	public CRLIdentifier(XAdESSchemas schema) {
		super(schema);
	}
	
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), ConstantesXADES.XADES_TAG_CRL_IDENTIFIER);
		super.load(element);
	}
	
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), ConstantesXADES.XADES_TAG_CRL_IDENTIFIER);
	}
	
	@Override
	public Element createElement(Document doc, String namespaceXAdES) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXAdES);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + ConstantesXADES.XADES_TAG_CRL_IDENTIFIER);
		super.addContent(res, namespaceXAdES);
		return res;
	}

}
