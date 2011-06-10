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
public class CountryName extends AbstractXadesStringElement {

	public CountryName(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_COUNTRY_NAME, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public CountryName(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_COUNTRY_NAME);
	}

}
