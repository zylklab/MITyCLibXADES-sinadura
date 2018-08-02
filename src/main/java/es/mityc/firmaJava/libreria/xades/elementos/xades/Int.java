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
public class Int extends AbstractXadesIntegerElement {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public Int(XAdESSchemas schema, BigInteger data) {
		super(schema, ConstantesXADES.XADES_TAG_INT, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public Int(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_INT);
	}

}
