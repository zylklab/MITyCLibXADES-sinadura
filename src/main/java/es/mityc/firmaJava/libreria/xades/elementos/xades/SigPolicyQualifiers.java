/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.util.ArrayList;

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
public class SigPolicyQualifiers extends SigPolicyQualifiersListType {

	/**
	 * @param schema
	 */
	public SigPolicyQualifiers(XAdESSchemas schema) {
		super(schema);
	}

	/**
	 * @param schema
	 * @param list
	 */
	public SigPolicyQualifiers(XAdESSchemas schema, ArrayList<SigPolicyQualifier> list) {
		super(schema, list);
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.CertificateValuesType#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), ConstantesXADES.XADES_TAG_SIG_POLICY_QUALIFIERS);
		super.load(element);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), ConstantesXADES.XADES_TAG_SIG_POLICY_QUALIFIERS);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.AbstractXADESElement#createElement(org.w3c.dom.Document, java.lang.String)
	 */
	@Override
	public Element createElement(Document doc, String namespaceXAdES) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXAdES);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + ConstantesXADES.XADES_TAG_SIG_POLICY_QUALIFIERS);
		super.addContent(res, namespaceXAdES);
		return res;
	}

}
