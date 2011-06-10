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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ObjectIdentifierType extends AbstractXADESElement {
	
	private Identifier identifier;
	private Description description;
	private DocumentationReferences references;

	public ObjectIdentifierType(XAdESSchemas schema, URI uri, String description) {
		super(schema);
		identifier = new Identifier(schema, uri);
		if (description != null)
			this.description = new Description(schema, description);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public ObjectIdentifierType(XAdESSchemas schema) {
		super(schema);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xmldsig.AbstractXDsigElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectIdentifierType) {
			ObjectIdentifierType oit = (ObjectIdentifierType) obj;
			if (identifier.equals(oit.identifier))
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
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE))
			throw new InvalidInfoNodeException("Se esperaba elemento como hijo de ObjectIdentifierType");
		
		Identifier identifier = new Identifier(schema);
		identifier.load((Element)node);
		
		// El siguiente elemento puede ser un elemento Description
		node = getNextNonvoidNode(node);
		Description description = null; 
		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de ObjectIdentifierType");
			description = new Description(schema);
			if (description.isThisNode(node)) {
				description.load((Element) node);
				node = getNextNonvoidNode(node);
			}
		}
		
		DocumentationReferences references = null;
		if (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new InvalidInfoNodeException("Se esperaba elemento como hijo de ObjectIdentifierType");
			references = new DocumentationReferences(schema);
			if (references.isThisNode(node)) {
				references.load((Element) node);
			}
		}
		
		this.identifier = identifier;
		this.description = description;
		this.references = references;
	}

	/**
	 * @return the identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the description
	 */
	public Description getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(Description description) {
		this.description = description;
	}

	public DocumentationReferences getReferences() {
		return references;
	}

	public void setReferences(DocumentationReferences references) {
		this.references = references;
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
		if (identifier == null)
			throw new InvalidInfoNodeException("Información insuficiente para escribir nodo ObjectIdentifierType");
		element.appendChild(identifier.createElement(element.getOwnerDocument(), namespaceXAdES));
		if (description != null) {
			element.appendChild(description.createElement(element.getOwnerDocument(), namespaceXAdES));
		}
	}
	
}
