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
package es.mityc.javasign.xml.xades.policy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.utilidades.NombreNodo;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.firmaJava.libreria.xades.elementos.xades.SignaturePolicyIdentifier;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;
import es.mityc.firmaJava.libreria.xades.errores.PolicyException;

/**
 * <p>Utilidad para facilitar la gestión de algunas tareas relacionadas con políticas.</p>
 *  
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class PoliciesTool {
	
	/**
	 * <p>Inserta un nodo de política.</p>
	 * 
	 * <p>Busca el lugar correspondiente donde debería ir la política en la firma e incluye el nodo con los datos indicados. Si ya existe un 
	 * nodo de política lo sustituye (sólo puede haber una política por firma).</p>
	 * 
	 * @param signNode Nodo que tiene la firma en la que se quiere insertar el nodo de política
	 * @param namespaceDS Namespace del nodo de firma
	 * @param namespaceXAdES Namespace de la parte XAdES en la que se insertará la firma
	 * @param schema Versión de esquema XAdES
	 * @param spi Nodo de política
	 * @throws PolicyException Lanazada cuando no se encuentra dónde colocar la política o surge algún problema al insertar el nodo
	 */
	public static void insertPolicyNode(Element signNode, String namespaceDS, String namespaceXAdES, XAdESSchemas schema, SignaturePolicyIdentifier spi) throws PolicyException {
		// Buscar el nodo SignedSignatureProperties para añadirle la política 
		NodeList list = signNode.getElementsByTagNameNS(schema.getSchemaUri(), ConstantesXADES.SIGNED_SIGNATURE_PROPERTIES);
		if ((list.getLength() != 1) || (list.item(0).getNodeType() != Node.ELEMENT_NODE)) {
			throw new PolicyException("No hay nodo SignedSignatureProperties claro al que aplicar la política");
		}
		// Crea el nodo de política
		Element policy;
		try {
			policy = spi.createElement(signNode.getOwnerDocument(), namespaceDS, namespaceXAdES);
		} catch (InvalidInfoNodeException ex) {
			throw new PolicyException("Error en la creación de la política:" + ex.getMessage(), ex);
		}
		
		NombreNodo SIGNING_TIME = new NombreNodo(schema.getSchemaUri(), ConstantesXADES.SIGNING_TIME); 
		NombreNodo SIGNING_CERTIFICATE = new NombreNodo(schema.getSchemaUri(), ConstantesXADES.SIGNING_CERTIFICATE); 
		NombreNodo SIGNATURE_POLICY_IDENTIFIER = new NombreNodo(schema.getSchemaUri(), ConstantesXADES.SIGNATURE_POLICY_IDENTIFIER); 
		// busca la posición donde poner el nodo
		Element signedSignatureProperties = (Element)list.item(0);
		Node node = signedSignatureProperties.getFirstChild();
		while (node != null) {
			if (node.getNodeType() != Node.ELEMENT_NODE)
				throw new PolicyException("Error en el formato de SignedSignatureProperties");
			NombreNodo nombre = new NombreNodo(node.getNamespaceURI(), node.getLocalName());
			if ((!nombre.equals(SIGNING_TIME)) && (!nombre.equals(SIGNING_CERTIFICATE)))
				break;
			node = node.getNextSibling();
		}
		// Si existe un nodo policy, lo sustituyes 
		if ((node != null) && (SIGNATURE_POLICY_IDENTIFIER.equals(new NombreNodo(node.getNamespaceURI(), node.getLocalName())))) {
			signedSignatureProperties.replaceChild(policy, node);
		} else {
			signedSignatureProperties.insertBefore(policy, node);
		}
	}


}
