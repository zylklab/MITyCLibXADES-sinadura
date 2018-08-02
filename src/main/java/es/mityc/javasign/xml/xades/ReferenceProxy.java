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
package es.mityc.javasign.xml.xades;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.w3c.dom.Element;

/**
 * <p>Clase proxy para trabajar con un elemento Reference de XMLSec.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ReferenceProxy {
	
	/** Guarda la referencia a la instancia Reference. */
	private Reference reference;
	
	/**
	 * <p>Construye una instancia proxy a un Reference.</p>
	 * @param ref referencia
	 */
	public ReferenceProxy(Reference ref) {
		this.reference = ref;
	}
	
	/**
	 * <p>Devuelve la Id del nodo Reference.</p>
	 * @return id
	 */
	public String getID() {
		return reference.getId();
	}
	
	/**
	 * <p>Devuelve la URI señalada por la Reference.</p>
	 * @return URI
	 */
	public String getURI() {
		return reference.getURI();
	}
	
	/**
	 * <p>Devuelve un listado de las transformadas aplicadas al nodo.</p>
	 * @return
	 */
	public List<TransformProxy> getTransforms() {
		ArrayList<TransformProxy> proxys = new ArrayList<TransformProxy>();
		Transforms trans = null;
		try {
			trans = reference.getTransforms();
		} catch (XMLSignatureException ex) {
		} catch (InvalidTransformException ex) {
		} catch (TransformationException ex) {
		} catch (XMLSecurityException ex) {
		}
		if (trans != null) {
			for (int i = 0; i < trans.getLength(); i++) {
				try {
					proxys.add(new TransformProxy(trans.item(i)));
				} catch (TransformationException ex) {
				}
			}
		}
		return proxys;
	}
	
	/**
	 * <p>Devuelve la información en binario del contenido indicado en la referencia.</p>
	 * @return byte[] con los datos, <code>null</code> si se produce un error en el acceso
	 */
	public byte[] getBytes() {
		byte[] data = null;
		try {
			XMLSignatureInput si = reference.getContentsAfterTransformation();
			data = si.getBytes();
		} catch (XMLSignatureException ex) {
		} catch (CanonicalizationException ex) {
		} catch (IOException ex) {
		}
		return data;
	}
	
	/**
	 * <p>Escribe el contenido del nodo referenciado en un stream de salida.</p>
	 * @param os Stream de salida
	 */
	public void writeToStream(OutputStream os) throws IOException {
		try {
			XMLSignatureInput si = reference.getContentsAfterTransformation();
			si.updateOutputStream(os);
		} catch (XMLSignatureException ex) {
		} catch (CanonicalizationException ex) {
		}
	}
	
	/**
	 * <p>Devuelve el Element que representa al Reference.</p>
	 * @return Element
	 */
	public Element getElement() {
		return reference.getElement();
	}
	
}
