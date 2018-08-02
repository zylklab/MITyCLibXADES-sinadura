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
package es.mityc.javasign.xml.refs;

import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Document;

import es.mityc.javasign.xml.resolvers.MITyCResourceResolver;
import es.mityc.javasign.xml.transform.Transform;

/**
 * Interfaz para señalar las clases que representan objetos a ser firmados
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public abstract class AbstractObjectToSign {

	/** Transformadas. */
	private ArrayList<Transform> transforms = new ArrayList<Transform>();
	
	/**
	 * <p>Incluye la transformada indicada para que se aplique en el objeto a firmar.</p>
	 * 
	 * @param t transformada
	 */
	public void addTransform(Transform t) {
		// evita que se añada una transformada que ya está incluida que no aporte nada nuevo
		if (t != null) {
			boolean mustadd = true;
			String alg = t.getAlgorithm();
			if ((alg != null) &&
				(Transforms.TRANSFORM_ENVELOPED_SIGNATURE.equals(alg))) {
				for (Transform trans : transforms) {
					if (alg.equals(trans.getAlgorithm())) {
						mustadd = false;
						break;
					}
				}
			}
			if (mustadd) {
				transforms.add(t);
			}
		}
	}
	
	/**
	 * <p>Devuelve el listado de transformadas que se quieren aplicar al objeto.</p>
	 * @return lista de transformadas
	 */
	@SuppressWarnings("unchecked")
	public List<Transform> getTransforms() {
		return (List<Transform>) transforms.clone();
	}
	
	/**
	 * <p>Devuelve una URI que sirve para indicar dónde se encuentra el objeto a ser firmado.</p>
	 * @return URI de referencia
	 */
	public abstract String getReferenceURI();
	
	/**
	 * <p>Devuelve el tipo de referencia que tendrá el objeto a firmar.</p>
	 * <p>Este método deberá ser sobreescrito por las clases hijas que quieran devolver un tipo específico.</p>
	 * @return devuelve <code>null</code>
	 */
	public String getType() {
		return null;
	}
	
	/**
	 * <p>Devuelve un conjunto de contenedores de objetos que se añadirán a la firma.</p>
	 * <p>Este método deberá ser sobreescrito por las clases hijas que quieran incluir nuevos objetos de firma.</p>
	 * @param doc Document en el que irán los objetos
	 * @return devuelve una lista vacía
	 */
	public List<ObjectContainer> getObjects(Document doc) {
		return new ArrayList<ObjectContainer>();
	}
	
	/**
	 * <p>Devuelve un Resolver extra para tratar este objeto a ser firmado.</p>
	 * <p>Este método deberá ser sobreescrito por las clases hijas que quieran incluir Resolver extra.</p>
	 * @return devuelve <code>null</code>
	 */
	public MITyCResourceResolver getResolver() {
		return null;
	}
	
}
