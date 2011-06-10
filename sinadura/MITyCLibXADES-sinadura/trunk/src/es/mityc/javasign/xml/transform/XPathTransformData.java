/**
 * LICENCIA LGPL:
 * 
 * Esta librería es Software Libre; Usted puede redistribuirlo y/o modificarlo
 * bajo los términos de la GNU Lesser General Public License (LGPL)
 * tal y como ha sido publicada por la Free Software Foundation; o
 * bien la versión 2.1 de la Licencia, o (a su elección) cualquier versión posterior.
 * 
 * Esta librería se distribuye con la esperanza de que sea útil, pero SIN NINGUNA
 * GARANTÍA; tampoco las implícitas garantías de MERCANTILIDAD o ADECUACIÓN A UN
 * PROPÓSITO PARTICULAR. Consulte la GNU Lesser General Public License (LGPL) para más
 * detalles
 * 
 * Usted debe recibir una copia de la GNU Lesser General Public License (LGPL)
 * junto con esta librería; si no es así, escriba a la Free Software Foundation Inc.
 * 51 Franklin Street, 5º Piso, Boston, MA 02110-1301, USA.
 * 
 */
package es.mityc.javasign.xml.transform;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.mityc.firmaJava.libreria.ConstantesXADES;

/**
 * <p>Indica un conjunto de transformadas XPath.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class XPathTransformData implements ITransformData {
	
	private List<String> paths = new ArrayList<String>();
	
	/**
	 * <p>Incluye el path indicado para la transformada.</p>
	 * @param path
	 */
	public void addPath(String path) {
		paths.add(path);
	}

	/**
	 * @see es.mityc.javasign.xml.transform.ITransformData#getExtraData(org.w3c.dom.Document))
	 */
	public NodeList getExtraData(Document doc) {
		SimpleNodeList nl = null;
		if (paths.size() > 0) {
			nl = new SimpleNodeList();
			for (String path : paths) {
				Element pathElement = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, "ds:XPath");
				pathElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", ConstantesXADES.SCHEMA_DSIG);
				pathElement.setTextContent(path);
				nl.addNode(pathElement);
			}
		}
		return nl;
	}
}
