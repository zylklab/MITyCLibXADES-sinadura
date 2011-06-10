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
public class PostalCode extends AbstractXadesStringElement {

	public PostalCode(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_POSTAL_CODE, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public PostalCode(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_POSTAL_CODE);
	}

}
