/**
 * 
 */
package es.mityc.firmaJava.libreria.xades.elementos.xades;

import java.util.Date;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;

/**
 * @author dsantose
 *
 */
public class IssueTime extends AbstractXadesDateElement {

	/**
	 * @param schema
	 * @param nameElement
	 * @param data
	 */
	public IssueTime(XAdESSchemas schema, Date data) {
		super(schema, ConstantesXADES.XADES_TAG_ISSUE_TIME, data);
	}

	/**
	 * @param schema
	 * @param nameElement
	 */
	public IssueTime(XAdESSchemas schema) {
		super(schema, ConstantesXADES.XADES_TAG_ISSUE_TIME);
	}

}
