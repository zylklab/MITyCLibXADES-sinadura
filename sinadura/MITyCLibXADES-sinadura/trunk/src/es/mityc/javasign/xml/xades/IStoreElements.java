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

import es.mityc.javasign.certificate.ICertStatus;

/**
 * Interfaz que ha de implementar la clase que gestione el almacenamiento de elementos de una firma XAdES externos a la firma (certificados,
 * respuestas OCSP y CRLs) para los casos de firmas con elementos externos (XAdES-C y XAdES-X).
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public interface IStoreElements {
	
	/**
	 * Inicializa el almacenador de elementos indicándole cuál es la URI base de la firma
	 * @param baseURI
	 */
	public void init(String baseURI);

	/**
	 * Indica cuál es el certificado y el status del certificado que hay que almacenar. Se espera de vuelta el nombre que se le ha
	 * asignado a los dos elementos para referenciarlos en la firma XAdES.
	 *  
	 * @param certificate
	 * @param certStatus
	 * @return <ul><li>String[0]: Nombre del elemento certificado</li><li>String[1]: Nombre del elemento estado del certificado</li></ul>
	 */
	public String[] storeCertAndStatus(X509Certificate certificate, ICertStatus certStatus);
	
}
