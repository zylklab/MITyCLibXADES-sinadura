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
package es.mityc.javasign.xml.resolvers;

/**
 * Este interfaz permite la obtención del hash de información para ser firmada que permanece privada a la librería de firma.
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public interface IPrivateData {
	
	/**
	 * Obtiene el digest del elemento utilizando el algoritmo de hashing indicado.
	 *  
	 * @param name Nombre del elemento del que se quiere calcular el hashing
	 * @param baseURI Ruta base del elemento
	 * @param algName Nombre del algoritmo de hashing
	 * @return Digest calculado de la información privada
	 * @throws ResourceDataException lanzada cuando no se puede acceder a la información por alguna razón
	 */
	public byte[] getDigest(String name, String baseURI, String algName) throws ResourceDataException;
	
	/**
	 * Indica si esta implementación puede acceder a la información indicada para calcular su digest
	 * 
	 * @param name Nombre del elemento
	 * @param baseURI Ruta base del elemento
	 * @return <code>true<code> si puede calcular su digest, <code>false</code> en otro caso
	 */
	public boolean canDigest(String name, String baseURI);

}
