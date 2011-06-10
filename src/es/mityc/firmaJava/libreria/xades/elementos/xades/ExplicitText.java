/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;

/**
 * @author dsantose
 *
 */
public class ExplicitText extends AbstractXadesStringElement {

	public ExplicitText(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_EXPLICIT_TEXT, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public ExplicitText(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_EXPLICIT_TEXT);
	}

}
