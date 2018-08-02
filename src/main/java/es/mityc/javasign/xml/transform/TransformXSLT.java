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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;
import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada que aplica transformaciones XSLT.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformXSLT extends Transform implements ITransformData {
	
	/** Internacionalizador. */
	private static final II18nManager I18N = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);

	
	/** Elemento raíz de la hoja de estilo. */
	private Element stylesheet;
	
	/**
	 * <p>Constructor.</p>
	 */
	public TransformXSLT() {
		super(TransformProxy.TRANSFORM_XSLT, null);
		setTransformData(this);
	}
	
	/**
	 * @see es.mityc.javasign.xml.transform.Transform#getExtraData(org.w3c.dom.Document)
	 */
	@Override
	public NodeList getExtraData(Document doc) {
		SimpleNodeList nl = null;
		if ((stylesheet != null)) {
			try {
				Node node = doc.importNode(stylesheet, true);
				nl = new SimpleNodeList();
				nl.addNode(node);
			} catch (DOMException ex) {
			}
		}
		return nl;
	}
	
	/**
	 * <p>Establece la hoja de estilo de esta transformada.</p>
	 * @param stylesheet Hoja de estilo
	 * @throws IllegalArgumentException si el elemento no se corresponde con una hoja de estilo
	 */
	public void setStyleSheet(Element stylesheet) throws IllegalArgumentException {
		if (!"http://www.w3.org/1999/XSL/Transform".equals(stylesheet.getNamespaceURI())) {
			throw new IllegalArgumentException(I18N.getLocalMessage(ConstantsXAdES.I18N_SIGN_8, stylesheet.getNamespaceURI()));
		}
		if (!"stylesheet".equals(stylesheet.getLocalName())) {
			throw new IllegalArgumentException(I18N.getLocalMessage(ConstantsXAdES.I18N_SIGN_9, stylesheet.getLocalName()));
		}
		this.stylesheet = stylesheet;
	}
	
}
