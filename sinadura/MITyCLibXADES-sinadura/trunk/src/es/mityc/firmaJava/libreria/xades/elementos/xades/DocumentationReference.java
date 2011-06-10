/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.net.URI;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;

/**
 * @author dsantose
 *
 */
public class DocumentationReference extends AbstractXadesURIElement  {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public DocumentationReference(XAdESSchemas schema, URI data) {
		super(schema, ConstantesXADES.XADES_TAG_DOCUMENTATION_REFERENCE, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public DocumentationReference(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_DOCUMENTATION_REFERENCE);
	}

}
