/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.math.BigInteger;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;

/**
 * @author dsantose
 *
 */
public class Number extends AbstractXadesIntegerElement {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public Number(XAdESSchemas schema, BigInteger data) {
		super(schema, ConstantesXADES.XADES_TAG_NUMBER, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public Number(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_NUMBER);
	}

}
