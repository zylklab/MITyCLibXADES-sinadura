/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.io.File;
import java.security.cert.X509CRL;

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
public class CRLRef extends CRLRefType {

	/**
	 * @param schema
	 */
	public CRLRef(XAdESSchemas schema) {
		super(schema);
	}

	/**
	 * @param schema
	 * @param method
	 * @param crl
	 * @throws InvalidInfoNodeException
	 */
	public CRLRef(XAdESSchemas schema, String method, X509CRL crl) throws InvalidInfoNodeException {
		super(schema, method, crl);
	}

	/**
	 * @param schema
	 * @param method
	 * @param crlFile
	 * @throws InvalidInfoNodeException
	 */
	public CRLRef(XAdESSchemas schema, String method, File crlFile) throws InvalidInfoNodeException {
		super(schema, method, crlFile);
	}

	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), ConstantesXADES.XADES_TAG_CRL_REF);
		super.load(element);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.SignaturePolicyIdType#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), ConstantesXADES.XADES_TAG_CRL_REF);
	}
	
	@Override
	public Element createElement(Document doc, String namespaceXDsig, String namespaceXAdES) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXDsig, namespaceXAdES);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.SignaturePolicyIdType#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + ConstantesXADES.XADES_TAG_CRL_REF);
		super.addContent(res, namespaceXAdES, namespaceXDsig);
		return res;
	}

}
