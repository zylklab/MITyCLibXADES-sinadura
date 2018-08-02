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
 * Este interfaz permite la obtención de información referente a recursos de la firma (disponible en nodos Reference).
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public interface IResourceData {

	/**
	 * Obtiene acceso a la información del elemento indicado.
	 *  
	 * @param name Nombre del elemento del que se quiere obtener acceso
	 * @param baseURI Ruta base del elemento
	 * @return Objeto del tipo InputStream o byte[] que da acceso a los datos del elemento
	 * @throws ResourceDataException lanzada cuando no se puede acceder a la información por alguna razón
	 */
	public Object getAccess(String name, String baseURI) throws ResourceDataException;
	
	/**
	 * Indica si esta implementación puede acceder a la información indicada
	 * 
	 * @param name Nombre del elemento
	 * @param baseURI Ruta base del elemento
	 * @return <code>true<code> si puede acceder, <code>false</code> en otro caso
	 */
	public boolean canAccess(String name, String baseURI);

}
