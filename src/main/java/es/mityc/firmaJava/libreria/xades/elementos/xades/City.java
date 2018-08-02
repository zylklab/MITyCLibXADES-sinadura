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
public class City extends AbstractXadesStringElement {

	public City(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_CITY, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public City(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_CITY);
	}

}
