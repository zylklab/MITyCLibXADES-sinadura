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
package es.mityc.javasign.issues;

import java.security.cert.X509Certificate;

import es.mityc.javasign.pkstore.IPassStoreKS;

/**
 * <p>Permite automatizar el acceso a las contraseñas de los almacenes de certificados de testeo.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class PassStoreKS implements IPassStoreKS {
	
	/** Contraseña de acceso al almacén. */
	private transient String password;
	
	/**
	 * <p>Crea una instancia con la contraseña que se utilizará con el almacén relacionado.</p>
	 * @param pass Contraseña del almacén
	 */
	public PassStoreKS(final String pass) {
		this.password = new String(pass);
	}

	/**
	 * <p>Devuelve la contraseña configurada para este almacén.</p>
	 * @param certificate No se utiliza
	 * @param alias no se utiliza
	 * @return contraseña configurada para este almacén
	 * @see es.mityc.javasign.pkstore.IPassStoreKS#getPassword(java.security.cert.X509Certificate, java.lang.String)
	 */
	public char[] getPassword(final X509Certificate certificate, final String alias) {
		return password.toCharArray();
	}

}
