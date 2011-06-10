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
public class StateOrProvince extends AbstractXadesStringElement {

	public StateOrProvince(XAdESSchemas schema, String data) {
		super(schema, ConstantesXADES.XADES_TAG_STATE_OR_PROVINCE, data);
	}

	/**
	 * @param namespaceXAdES
	 * @param namespaceXDSig
	 * @param schema
	 */
	public StateOrProvince(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_STATE_OR_PROVINCE);
	}

}
