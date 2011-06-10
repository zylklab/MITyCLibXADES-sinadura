/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.javasign.xml.xades.policy.PolicyException;

/**
 * @author dsantose
 *
 */
public interface IPolicyQualifier {
	
	public Node createPolicyQualifierContent(Document doc) throws PolicyException;
	public void loadPolicyQualifierContent(Element element) throws PolicyException;


}
