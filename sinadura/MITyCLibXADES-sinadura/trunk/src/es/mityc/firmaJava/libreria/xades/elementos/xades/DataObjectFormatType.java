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

import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class DataObjectFormatType extends AbstractXADESElement {
	
	private URI objectReference;
	private Description description;
	private ObjectIdentifier objectIdentifier;
	private MimeType mimetype;
	private Encoding encoding;


	/**
	 * @param schema
	 */
	public DataObjectFormatType(XAdESSchemas schema, URI reference, String description, String mimeType) {
		super(schema);
		this.objectReference = reference;
		if (description != null)
			this.description = new Description(schema, description);
		if (mimeType != null)
			this.mimetype = new MimeType(schema, mimeType);
	}
	
	/**
	 * @param schema
	 */
	public DataObjectFormatType(XAdESSchemas schema) {
		super(schema);
	}
	
	public Description getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = new Description(schema, description);
	}
	
	public void setDescription(Description description) {
		this.description = description;
	}
	
	public MimeType getMimeType() {
		return mimetype;
	}
	
	public void setMimeType(String mimeType) {
		this.mimetype = new MimeType(schema, mimeType);
	}
	
	public void setMimeType(MimeType mimeType) {
		this.mimetype = mimeType;
	}
	
	public Encoding getEnconding() {
		return encoding;
	}
	
	public void setEncoding(URI encoding) {
		this.encoding = new Encoding(schema, encoding);
	}
	
	public void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}
	
	public ObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}
	
	public void setObjectIdentifier(ObjectIdentifier objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataObjectFormatType) {
			DataObjectFormatType doft = (DataObjectFormatType)obj;
			if (!compare(description, doft.description))
				return false;
			if (!compare(objectIdentifier, doft.objectIdentifier))
				return false;
			if (!compare(mimetype, doft.mimetype))
				return false;
			if (!compare(encoding, doft.encoding))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		String uri = element.getAttribute(ConstantesXADES.XADES_TAG_OBJECT_REFERENCE);
		if (uri == null)
			throw new InvalidInfoNodeException("No hay ObjectReference en nodo DataObjectFormatType");
		try {
			objectReference = new URI(uri);
		} catch (URISyntaxException ex) {
			throw new InvalidInfoNodeException("URI en ObjectReference inválida");
		}
		
		Description description = null; 
		ObjectIdentifier objIdentifier = null;
		MimeType mimeType = null;
		Encoding encoding = null;
		
		Node node = getFirstNonvoidNode(element);
		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de DataObjectFormatType");
			description = new Description(schema);
			if (description.isThisNode(node)) {
				description.load((Element) node);
				node = getNextNonvoidNode(node);
			} else 
				description = null;
		}
		
		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de DataObjectFormatType");
			objIdentifier = new ObjectIdentifier(schema);
			if (objIdentifier.isThisNode(node)) {
				objIdentifier.load((Element) node);
				node = getNextNonvoidNode(node);
			} else 
				objIdentifier = null;
		}

		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de DataObjectFormatType");
			mimeType = new MimeType(schema);
			if (mimeType.isThisNode(node)) {
				mimeType.load((Element) node);
				node = getNextNonvoidNode(node);
			} else 
				mimeType = null;
		}

		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de DataObjectFormatType");
			encoding = new Encoding(schema);
			if (encoding.isThisNode(node)) {
				encoding.load((Element) node);
				node = getNextNonvoidNode(node);
			} else 
				encoding = null;
		}
		
		if ((description == null) && (objIdentifier == null) && (mimeType == null))
			throw new InvalidInfoNodeException("No hay información de formato de objeto en nodo DataObjectFormatType");
		
		this.description = description;
		this.objectIdentifier = objIdentifier;
		this.mimetype = mimeType;
		this.encoding = encoding;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.AbstractXADESElement#addContent(org.w3c.dom.Element, java.lang.String)
	 */
	@Override
	public void addContent(Element element, String namespaceXAdES) throws InvalidInfoNodeException {
		super.addContent(element, namespaceXAdES);
	}

	/**
	 * @param doc
	 * @param res
	 */
	protected void addContent(Element element) throws InvalidInfoNodeException {
		if (objectReference == null)
			throw new InvalidInfoNodeException("Información insuficiente para escribir nodo DataObjectFormatType");
		element.setAttributeNS(null, ConstantesXADES.XADES_TAG_OBJECT_REFERENCE, objectReference.toString());
		
		if ((description == null) && (objectIdentifier == null) && (mimetype == null))
			throw new InvalidInfoNodeException("Información insuficiente para escribir nodo DataObjectFormatType");
		
		if (description != null)
			element.appendChild(description.createElement(element.getOwnerDocument(), namespaceXAdES));
		if (objectIdentifier != null) 
			element.appendChild(objectIdentifier.createElement(element.getOwnerDocument(), namespaceXAdES));
		if (mimetype != null)
			element.appendChild(mimetype.createElement(element.getOwnerDocument(), namespaceXAdES));
		if (encoding != null)
			element.appendChild(encoding.createElement(element.getOwnerDocument(), namespaceXAdES));
	}

}
