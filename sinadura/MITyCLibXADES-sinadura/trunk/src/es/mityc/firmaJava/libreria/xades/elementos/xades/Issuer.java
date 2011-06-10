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
public class Issuer extends AbstractXadesStringElement {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public Issuer(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_ISSUER, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public Issuer(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_ISSUER);
	}

}
