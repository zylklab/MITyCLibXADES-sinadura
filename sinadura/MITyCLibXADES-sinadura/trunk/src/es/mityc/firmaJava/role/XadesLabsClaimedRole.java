/**
 * 
 */
package es.mityc.firmaJava.role;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author dsantose
 *
 */
public class XadesLabsClaimedRole implements IClaimedRole {
	
	private String type;
	private boolean executiveDirector;
	private boolean shareHolder;
	private boolean vendor;
	private boolean purchaser;
	private boolean engineer;
	
	public XadesLabsClaimedRole(String type) {
		this.type = type;
	}
	

	public void setExecutiveDirector(boolean executiveDirector) {
		this.executiveDirector = executiveDirector;
	}

	public void setShareHolder(boolean shareHolder) {
		this.shareHolder = shareHolder;
	}

	public void setVendor(boolean vendor) {
		this.vendor = vendor;
	}

	public void setPurchaser(boolean purchaser) {
		this.purchaser = purchaser;
	}

	public void setEngineer(boolean engineer) {
		this.engineer = engineer;
	}

	/* (non-Javadoc)
	 * @see es.mityc.firmaJava.role.IClaimedRole#createClaimedRoleContent(org.w3c.dom.Document)
	 */
	public Node createClaimedRoleContent(Document doc) {
		Element root = doc.createElementNS("http://xadeslabs.com/xades", "xl:XadesLabs");
		root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xl", "http://xadeslabs.com/xades");
		Element roles = doc.createElementNS("http://xadeslabs.com/xades", "xl:Roles");
		roles.setAttributeNS(null, "type", type);
		root.appendChild(roles);
		Element child = doc.createElementNS("http://xadeslabs.com/xades", "xl:ExecutiveDirector");
		child.appendChild(doc.createTextNode(Boolean.toString(this.executiveDirector)));
		roles.appendChild(child);
		child = doc.createElementNS("http://xadeslabs.com/xades", "xl:ShareHolder");
		child.appendChild(doc.createTextNode(Boolean.toString(this.shareHolder)));
		roles.appendChild(child);
		child = doc.createElementNS("http://xadeslabs.com/xades", "xl:Vendor");
		child.appendChild(doc.createTextNode(Boolean.toString(this.vendor)));
		roles.appendChild(child);
		child = doc.createElementNS("http://xadeslabs.com/xades", "xl:Purchaser");
		child.appendChild(doc.createTextNode(Boolean.toString(this.purchaser)));
		roles.appendChild(child);
		child = doc.createElementNS("http://xadeslabs.com/xades", "xl:Engineer");
		child.appendChild(doc.createTextNode(Boolean.toString(this.engineer)));
		roles.appendChild(child);
		return root;
	}

}
