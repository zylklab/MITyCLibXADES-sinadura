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

import java.security.cert.X509Certificate;
import java.util.Map;

import es.mityc.javasign.certificate.ElementNotFoundException;
import es.mityc.javasign.certificate.ICertStatus;
import es.mityc.javasign.certificate.IRecoverElements;
import es.mityc.javasign.certificate.UnknownElementClassException;

/**
 * Con esta clase se indica que no se debe intentar recuperar ni guardar ninguno de los elementos relacionados con los certificados de
 * una firma.
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class NullStoreElements implements IStoreElements, IRecoverElements {
	
	public static final NullStoreElements instance = new NullStoreElements(); 

	/**
	 * 
	 */
	public NullStoreElements() {
	}

	/**
	 * @see es.mityc.javasign.xml.xades.IStoreElements#init(java.lang.String)
	 */
	public void init(String baseURI) {
	}

	/**
	 * @see es.mityc.javasign.xml.xades.IStoreElements#storeCertAndStatus(java.security.cert.X509Certificate, es.mityc.firmaJava.certificates.status.ICertStatusElement)
	 */
	public String[] storeCertAndStatus(X509Certificate certificate, ICertStatus certStatus) {
		return new String[0];
	}

	/**
	 * @see es.mityc.javasign.certificate.IRecoverElements#getElement(java.util.Map, java.lang.Class)
	 */
	public <T> T getElement(Map<String, Object> props, Class<T> elementClass) throws ElementNotFoundException, UnknownElementClassException {
		return null; 
	}

}
