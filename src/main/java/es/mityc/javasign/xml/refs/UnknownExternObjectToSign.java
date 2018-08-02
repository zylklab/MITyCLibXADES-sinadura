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

import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;
import es.mityc.javasign.xml.resolvers.IPrivateData;
import es.mityc.javasign.xml.resolvers.MITyCResourceResolver;
import es.mityc.javasign.xml.resolvers.ResolverPrivateData;
import es.mityc.javasign.xml.transform.Transform;

/**
 * <p>Representa un objeto externo al XML (no definido) que debe ser firmado.</p>
 * <p>Para poder acceder al contenido y obtener su digest se debe proporcionar el digester adecuado que implemente el interfaz
 * <code>IPrivateDate</code>.</p>
 * <p>Este tipo de objetos delega la seguridad e integridad del contenido en el gestionador de la información privada, que será
 * el responsable de asegurar que no se produce ningún ataque sobre la informacón.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class UnknownExternObjectToSign extends AbstractObjectToSign {
	
	/** Internacionalizador. */
	private static final II18nManager I18N = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);
	
	private String name;
	private IPrivateData digester;
	

	/**
	 * 
	 */
	public UnknownExternObjectToSign(String name, IPrivateData privateDataDigester) {
		this.name = name;
		this.digester = privateDataDigester;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public IPrivateData getDigester() {
		return digester;
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#addTransform(es.mityc.javasign.xml.transform.Transform)
	 */
	@Override
	public void addTransform(Transform t) {
		throw new IllegalArgumentException(I18N.getLocalMessage(ConstantsXAdES.I18N_SIGN_10));
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#getReferenceURI()
	 */
	@Override
	public String getReferenceURI() {
		return name;
	}
	
	/**
	 * @see es.mityc.javasign.xml.refs.AbstractObjectToSign#getResolver()
	 */
	@Override
	public MITyCResourceResolver getResolver() {
		return new ResolverPrivateData(getDigester());
	}
}
