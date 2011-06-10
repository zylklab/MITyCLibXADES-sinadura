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
 * 51 Franklin Street, 5º Piso, Boston, MA 02110-1301, USA o consulte
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright 2008 Ministerio de Industria, Turismo y Comercio
 * 
 */

package es.mityc.javasign.xml.xades.policy;

import org.w3c.dom.Element;

import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;

/**
 * Interfaz que han de implementar los validadores de policies que gestiona el manager de policies.
 * 
 * Además los validadores de policies deben tener un constructor por defecto sin parámetros.
 *
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public interface IValidacionPolicy {
	
	/**
	 * Este método deberá encargarse de validar que la firma cumple la policy implementada.
	 * 
	 * @param nodoFirma nodo raíz (de firma) de la firma que se está validando
	 * @param resultadoValidacion resultado de la validacion de una firma
	 * 
	 * @return devuelve el resultado de la validación de la policy
	 */
	public PolicyResult validaPolicy(Element nodoFirma, final ResultadoValidacion resultadoValidacion);
	
	/**
	 * Devuelve una cadena que sirve para identificar la policy
	 * @return identificación de la policy
	 */
	public String getIdentidadPolicy();
	
}
