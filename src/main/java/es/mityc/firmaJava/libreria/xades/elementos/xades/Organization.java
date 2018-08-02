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
public class Organization extends AbstractXadesStringElement {

	public Organization(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_ORGANIZATION, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public Organization(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_ORGANIZATION);
	}

}
