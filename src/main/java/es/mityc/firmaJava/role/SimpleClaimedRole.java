/**
 * 
 */
package es.mityc.firmaJava.role;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author dsantose
 *
 */
public class SimpleClaimedRole implements IClaimedRole {
	
	private String text;
	
	public SimpleClaimedRole(String text) {
		this.text = text;
	}

	public Node createClaimedRoleContent(Document doc) {
		return doc.createTextNode(text);
	}

}
