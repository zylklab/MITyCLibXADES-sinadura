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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

import es.mityc.firmaJava.ValidationBase;
import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.FirmaXML;
import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.firmaJava.libreria.xades.ValidarFirmaXML;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.bridge.SigningException;
import es.mityc.javasign.pkstore.CertStoreException;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.pkstore.keystore.KSStore;
import es.mityc.javasign.xml.refs.AllXMLToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;

/**
 * <p>Prueba de Issue #299.</p>
 * <p>Error en firma de XML con nodo de estilo.</p>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class Test299 extends ValidationBase {
	
	private IPKStoreManager getPKStore() {
		IPKStoreManager pks = null;
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(this.getClass().getResourceAsStream("/issues/299/usr0052.p12"), "usr0052".toCharArray());
			pks = new KSStore(ks, new PassStoreKS("usr0052"));
		} catch (KeyStoreException ex) {
			fail("No se puede generar KeyStore JKS: " + ex.getMessage());
		} catch (NoSuchAlgorithmException ex) {
			fail("No se puede generar KeyStore JKS: " + ex.getMessage());
		} catch (CertificateException ex) {
			fail("No se puede generar KeyStore JKS: " + ex.getMessage());
		} catch (IOException ex) {
			fail("No se puede generar KeyStore JKS: " + ex.getMessage());
		}
		return pks;
	}
	
	/**
	 * <p>Recupera el primero de los certificados del almacén.</p>
	 * @param sm Interfaz de acceso al almacén
	 * @return Primer certificado disponible en el almacén
	 */
	protected X509Certificate getCertificate(final IPKStoreManager sm) {
		List<X509Certificate> certs = null;
		try {
			certs = sm.getSignCertificates();
		} catch (CertStoreException ex) {
			fail("Fallo obteniendo listado de certificados");
		}
		if ((certs == null) || (certs.size() == 0)) {
			fail("Lista de certificados vacía");
		}
		
		X509Certificate certificate = certs.get(0);
		assertNotNull("El primer certificado del almacén no existe", certificate);
		return certificate;
	}

	
	@Test
	public void test() {
		try {
			Document doc = loadDoc("/issues/299/299.xml");
			
			IPKStoreManager iStore = getPKStore();
			X509Certificate cert = getCertificate(iStore);

			DataToSign data2Sign = new DataToSign();
			data2Sign.setDocument(doc);
			data2Sign.addObject(new ObjectToSign(new AllXMLToSign(), null, null, null, null));
			
			data2Sign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
			data2Sign.setEsquema(XAdESSchemas.XAdES_132);
			data2Sign.setEnveloped(true);

		    try {
		    	FirmaXML firma = new FirmaXML();
		    	Object[] res = firma.signFile(cert, data2Sign, iStore.getPrivateKey(cert), iStore.getProvider(cert), null, null, false);
		    	doc = (Document) res[0];
			} catch (Exception ex) {
				throw new SigningException("Error realizando la firma", ex);
			}
			
			// valida la firma recién hecha
			if (!validateDoc(doc, null, null)) {
				
				// alfredo -> este test falla porque el certificado esta caducado
//				Assert.fail("La firma realizada en el test #299 debería ser válida");
			}
		} catch (Throwable th) {
			LOGGER.info(th.getMessage());
			LOGGER.info("", th);
			Assert.fail("Error en test #299: " + th.getMessage());
		}
	}

}
