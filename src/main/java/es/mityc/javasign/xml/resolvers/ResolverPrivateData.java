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

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.w3c.dom.Attr;

/**
 * Este ResourceResolverSpi permite acceder a información privada obtiendo su digest para poder realizar una firma XML.
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ResolverPrivateData extends MITyCResourceResolver {
	
	private final static String[] keys = { "digest.algorithm" };
	
	private IPrivateData internalResolver;

	/**
	 * 
	 */
	public ResolverPrivateData(IPrivateData internalResolver) {
		super();
		this.internalResolver = internalResolver;
	}

	/**
	 * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineCanResolve(org.w3c.dom.Attr, java.lang.String)
	 */
	@Override
	public boolean engineCanResolve(Attr uri, String BaseURI) {
		if (internalResolver == null) {
			return false;
		}
		return internalResolver.canDigest(uri.getValue(), BaseURI);
	}

	/**
	 * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineResolve(org.w3c.dom.Attr, java.lang.String)
	 */
	@Override
	public XMLSignatureInput engineResolve(Attr uri, String BaseURI) throws ResourceResolverException {
		if (internalResolver == null) {
			throw new ResourceResolverException("", uri, BaseURI);
		}
		String algName = engineGetProperty(Constants.PROPERTY_RR_ALG_DIGEST);
		if (algName == null) {
			throw new ResourceResolverException("", uri, BaseURI);
		}
		try {
			byte[] data = internalResolver.getDigest(uri.getValue(), BaseURI, algName);
			XMLSignatureInput xsi = new XMLSignatureInput(data);
			return xsi;
		} catch (ResourceDataException ex) {
			throw new ResourceResolverException("", uri, BaseURI);
		}
	}
	
	/**
	 * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineIsPrivateData()
	 */
	@Override
	public boolean engineIsPrivateData() {
		return true;
	}
	
	/**
	 * @see org.apache.xml.security.utils.resolver.ResourceResolverSpi#engineGetPropertyKeys()
	 */
	@Override
	public String[] engineGetPropertyKeys() {
		return keys;
	}
}
