package es.mityc.firmaJava.libreria.xades.elementos.xades;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.elementos.XMLDataStringType;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;

public abstract class AbstractXadesStringElement extends AbstractXADESElement {

	private XMLDataStringType data;
	private String nameElement;


	public AbstractXadesStringElement(XAdESSchemas schema, String nameElement, String data) {
		super(schema);
		this.nameElement = nameElement;
		this.data = new XMLDataStringType(data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public AbstractXadesStringElement(XAdESSchemas schema, String nameElement) {
		super(schema);
		this.nameElement = nameElement;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#createElement(org.w3c.dom.Document)
	 */
	@Override
	protected Element createElement(Document doc) throws InvalidInfoNodeException {
		if (data == null)
			throw new InvalidInfoNodeException("Información insuficiente para escribir elemento " + nameElement);
		Element res = doc.createElementNS(schema.getSchemaUri(), namespaceXAdES + ":" + nameElement);
		data.addContent(res);
		return res;
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.xades.AbstractXADESElement#createElement(org.w3c.dom.Document, java.lang.String)
	 */
	@Override
	public Element createElement(Document doc, String namespaceXAdES) throws InvalidInfoNodeException {
		return super.createElement(doc, namespaceXAdES);
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractXadesStringElement) {
			AbstractXadesStringElement desc = (AbstractXadesStringElement) obj;
			if ((nameElement.equals(desc.nameElement)) && (data.equals(desc.data)))
				return true;
		}
		else
			return data.equals(obj);
		return false;
	}
	
	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#load(org.w3c.dom.Element)
	 */
	@Override
	public void load(Element element) throws InvalidInfoNodeException {
		checkElementName(element, schema.getSchemaUri(), nameElement);
		data = new XMLDataStringType(null);
		data.load(element);
	}
	
	public void setValue(String value) {
		if (data == null)
			data = new XMLDataStringType(value);
		else
			data.setValue(value);
	}
	
	public String getValue() {
		if (data != null)
			return data.getValue();
		return null;
	}

	/**
	 * @see es.mityc.firmaJava.libreria.xades.elementos.AbstractXMLElement#isThisNode(org.w3c.dom.Node)
	 */
	@Override
	public boolean isThisNode(Node node) {
		return isElementName(nodeToElement(node), schema.getSchemaUri(), nameElement);
	}
}
