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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.certificate.AbstractCertStatus;
import es.mityc.javasign.certificate.ElementNotFoundException;
import es.mityc.javasign.certificate.ICertStatus;
import es.mityc.javasign.certificate.IOCSPCertStatus;
import es.mityc.javasign.certificate.IRecoverElements;
import es.mityc.javasign.certificate.IX509CRLCertStatus;
import es.mityc.javasign.certificate.OCSPResponderID;
import es.mityc.javasign.certificate.UnknownElementClassException;
import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.utilidades.UtilidadFicheros;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;

/**
 * Almacena los elementos de una firma XAdES-C/XAdES-X en el disco duro.
 * 
 * <br/><br/>Basa el almacenamiento en el cálculo de la huella Adler32 del objeto a almacenar. Calcula el CRC del elemento y crea un fichero
 * incluyendo esta información en el nombre en la ruta configurada. Los nombres cumplen el formato:
 * <ul>
 * 	<li>Certificado: cert-&lt;CRC&gt;.cer</li>
 * 	<li>OCSP: ocsp-&lt;CRC&gt;.ocs</li>
 * 	<li>CRL: crl-&lt;CRC&gt;.crl</li>
 * </ul>
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class LocalFileStoreElements implements IStoreElements, IRecoverElements {
	
	private static Log log = LogFactory.getLog(LocalFileStoreElements.class);
    private static II18nManager i18n = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);
    
    private final static String PREFIX_CERT = "cert-";
    private final static String EXT_CERT = ".cer";
    private final static String PREFIX_OCSP = "ocsp-";
    private final static String EXT_OCSP = ".ors";
    private final static String PREFIX_CRL = "crl-";
    private final static String EXT_CRL = ".crl";
    
    private class OCSPResp extends AbstractCertStatus implements IOCSPCertStatus {
    	private byte[] data;
    	private OCSPResp(byte[] data) {
    		this.data = data;
    	}
    	public byte[] getEncoded() {
    		return data;
    	}
		/**
		 * @see es.mityc.javasign.certificate.IOCSPCertStatus#getResponderID()
		 */
		public String getResponderID() {
			// TODO: indicar que es una operación no permitida
			throw new UnsupportedOperationException("Not implemented yet");
		}
		/**
		 * @see es.mityc.javasign.certificate.IOCSPCertStatus#getResponderType()
		 */
		public TYPE_RESPONDER getResponderType() {
			// TODO: indicar que es una operación no permitida
			throw new UnsupportedOperationException("Not implemented yet");
		}
		/**
		 * @see es.mityc.javasign.certificate.IOCSPCertStatus#getResponseDate()
		 */
		public Date getResponseDate() {
			// TODO: indicar que es una operación no permitida
			throw new UnsupportedOperationException("Not implemented yet");
		}
    }
    

    private URI base = null;
	
	private static ThreadLocal<Adler32> digester = new ThreadLocal<Adler32>() {
        protected Adler32 initialValue() {
            return new Adler32();
        }
    };


	public LocalFileStoreElements() {
		init(null);
	}
	
	public LocalFileStoreElements(String base) {
		init(base);
	}

	
	private URI getWorkdir() {
		return new File(".").toURI();
	}
	
	public void init(String base) {
		if ((base == null) || (base.trim().length() == 0)) {
			this.base = getWorkdir();
		} else {
			try {
				this.base = URI.create(base);
			} catch (IllegalArgumentException ex) {
				log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_5, ex.getMessage()));
				this.base = getWorkdir();
			}
		} 	
	}
	
	private void saveData(File file, final byte[] data) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
		} catch (FileNotFoundException ex) {
			log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_6, ex.getMessage(), file.getAbsolutePath()));
		} catch (IOException ex) {
			log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_6, ex.getMessage(), file.getAbsolutePath()));
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException ex) {
				}
			}
		}
	}
	
	private long digest(final byte[] data) {
		Checksum digest = digester.get();
		digest.reset();
		digest.update(data, 0, data.length);
		return digest.getValue();
	}

	/**
	 * Almacena los elementos en el directorio base indicado (el directorio actual de trabajo si no se ha indicado otro) con los 
	 * siguientes nombres:
	 * <ul>
	 * 	<li>Certificado: cert-[digest Adler32 en hexadecimal].cer</li>
	 * 	<li>Estado:
	 * 		<ul>
	 * 			<li>OCSP:</li>
	 * 			<li>CRL: </li>
	 * 		</ul>
	 * 	</li>
	 * </ul>
	 * @see es.mityc.javasign.xml.xades.IStoreElements#storeCertAndStatus(java.security.cert.X509Certificate, es.mityc.firmaJava.certificates.status.ICertStatusElement)
	 */
	public String[] storeCertAndStatus(X509Certificate certificate, ICertStatus certStatus) {
		String[] names = new String[2];
		File dir = new File(base);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				return names;
			}
		}
		
		if (certificate != null) {
			try {
				// Obtiene el identificador del certificado
				byte[] data = certificate.getEncoded();
				String nameCert = PREFIX_CERT + Long.toHexString(digest(data)) + EXT_CERT;
				// Guarda el certificado
				File certFile = new File(dir, nameCert);
				saveData(certFile, data);
				names[0] = certFile.getName();
			} catch (CertificateEncodingException ex) {
				log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_6, ex.getMessage()));
			}
		}

		if (certStatus != null) {
			if (certStatus instanceof IOCSPCertStatus) {
				IOCSPCertStatus respOcsp = (IOCSPCertStatus) certStatus;
//				if (respOcsp.getFilename() != null) {
//					names[1] = respOcsp.getFilename();
//				} else {
					// Obtiene la respuesta OCSP
					byte[] data = respOcsp.getEncoded();
					String ocspName = PREFIX_OCSP + Long.toHexString(digest(data)) + EXT_OCSP;
	    			// Guardamos la respuesta OCSP
					File ocspFile = new File(dir, ocspName);
					saveData(ocspFile, data);
					names[1] = ocspFile.getName();
//				}
			} else if (certStatus instanceof IX509CRLCertStatus) {
				IX509CRLCertStatus respCRL = (IX509CRLCertStatus) certStatus;
//				if (respCRL.getFilename() != null) {
//					names[1] = respCRL.getFilename();
//				} else {
//					try {
						// Obtiene la CRL
						byte[] data = respCRL.getEncoded();
						String crlName = PREFIX_CRL + Long.toHexString(digest(data)) + EXT_CRL;
		    			// Guardamos la CRL
						File crlFile = new File(dir, crlName);
						saveData(crlFile, data);
						names[1] = crlFile.getName();
//					} catch (CRLException ex) {
//						log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_6, ex.getMessage()));
//					}
//				}
			}
		}
		return names;
	}
	
	/**
	 * Recupera certificados, respuestas OCSP y CRLs en función de los datos provistos.
	 * 
	 * <br/><br/>Este almacenador de elementos es capaz de responder a las propiedades:
	 * <ul>
	 * 	<li><code>uri</code>: (String)uri donde se encuentra ubicado el elemento (certificado, crl y ocsp)</li>
	 * 	<li><code>issuer.name</code>: (X500Principal) nombre del issuer emisor del elemento (certificado y crl)</li>
	 *  <li><code>serial.number</code>: (BigInteger) número serie del elemento (certificado y crl)</li>
	 *  <li><code>emission.date</code>: (Date) emisión del elemento (crl y ocsp)</li>
	 * </ul>
	 * Es capaz de recuperar elementos de los tipos:
	 * <ul>
	 * 	<li>X509Certificate: elemento certificado</li>
	 * 	<li>X509CRL: elemento crl</li>
	 *  <li>IOCSP: elemento ocsp</li>
	 * </ul> 
	 * @see es.mityc.javasign.certificate.IRecoverElements#getElement(java.util.Map, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T>T getElement(Map<String, Object> props, Class<T> elementClass) throws ElementNotFoundException, UnknownElementClassException {
		if (X509Certificate.class.isAssignableFrom(elementClass)) {
			X509Certificate cert = getCertificate(props);
			if (cert != null) {
				return (T)cert;
			} else {
				throw new ElementNotFoundException();
			}
		} else if (IOCSPCertStatus.class.isAssignableFrom(elementClass)) {
			IOCSPCertStatus ocsp = getOCSPResponse(props);
			if (ocsp != null) {
				return (T)ocsp;
			} else {
				throw new ElementNotFoundException();
			}
		} else if (X509CRL.class.isAssignableFrom(elementClass)) {
			X509CRL crl = getCRL(props);
			if (crl != null) {
				return (T)crl;
			} else {
				throw new ElementNotFoundException();
			}
		} else {
			throw new UnknownElementClassException();
		}
	}
	
	private X509CRL getCRL(Map<String, Object> props) {
		if (props != null) {
			Object uriObj = props.get(PROP_URI);
			if ((uriObj != null) && (uriObj instanceof String)) {
				return getCRL((String)uriObj);
			}
		}
		return null;
	}
	
	private X509CRL getCRL(String uri) {
		X509CRL crl = null;
		File dir = new File(base);
		try {
			FileInputStream fis = new FileInputStream(new File(dir, uri));
			try
			{
				// Se obtiene el certificado en su formato del archivo de la URI
				CertificateFactory cf = CertificateFactory.getInstance(ConstantesXADES.X_509);
				crl = (X509CRL)cf.generateCRL(fis);
			} catch (CertificateException ex) {
			} catch (CRLException ex) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		} catch (FileNotFoundException e) {
		}
		return crl;
	}

	
	private IOCSPCertStatus getOCSPResponse(Map<String, Object> props) {
		if (props != null) {
			Object uriObj = props.get(PROP_URI);
			if ((uriObj != null) && (uriObj instanceof String)) {
				return getOCSPResponse((String)uriObj);
			}
			Object emissiondateObj = props.get(PROP_EMISSION_DATE);
			Object digestalgObj = props.get(PROP_DIGEST_ALGORITHM);
			Object digestvalueObj = props.get(PROP_DIGEST_VALUE);
			Object issuernameObj = props.get(PROP_ISSUER_NAME);
			Object issuerhashObj = props.get(PROP_ISSUER_HASH);
			if (((digestalgObj != null) && (digestalgObj instanceof String) &&
				 (digestvalueObj != null) && (digestvalueObj instanceof byte[])) &&
				((emissiondateObj != null) && (emissiondateObj instanceof Date) &&
				(((issuernameObj != null) && (issuernameObj instanceof X500Principal)) ||
				 ((issuerhashObj != null) && (issuerhashObj instanceof byte[]))))) {
				OCSPResponderID issuer = null;
				if (issuernameObj != null) {
					issuer = OCSPResponderID.getOCSPResponderID((X500Principal) issuernameObj);
				} else if (issuerhashObj != null) {
					issuer = OCSPResponderID.getOCSPResponderID((byte[]) issuerhashObj);
				}
				return getOCSPResponse((String)digestalgObj, (byte[])digestvalueObj, issuer, (Date)emissiondateObj);
			}
		}
		return null;
	}
	
	private IOCSPCertStatus getOCSPResponse(String uri) {
		OCSPResp ocsp = null;
		File file = new File(new File(base), uri);
		if (file.exists()) {
			byte[] data = UtilidadFicheros.readFile(file);
			if (data != null) {
				ocsp = new OCSPResp(data);
			}
		}
		return ocsp;
	}
	
	private IOCSPCertStatus getOCSPResponse(String digestAlg, byte[] digestValue, OCSPResponderID issuer, Date emission) {
		OCSPResp ocsp = null;
		// TODO: implementar localizador de respuesta OCSP
		// NOTA: desactivada esta sección de código ya que no se implementa ningún sistema estable de búsqueda
//		CertificateFinder cf = new CertificateFinder(base.toString());
//		byte[] data = cf.searchOCSP(digestAlg, Base64.encode(digestValue), issuer, emission);
//		if (data != null) {
//			ocsp = new OCSPResp(data);
//		}
		return ocsp; 
	}
	
	private X509Certificate getCertificate(Map<String, Object> props) {
		if (props != null) {
			Object uriObj = props.get(PROP_URI);
			if ((uriObj != null) && (uriObj instanceof String)) {
				return getCertificate((String)uriObj);
			}
			Object issuernameObj = props.get(PROP_ISSUER_NAME);
			Object serialnumberObj = props.get(PROP_SERIAL_NUMBER);
			if ((issuernameObj != null) && (issuernameObj instanceof X500Principal) &&
				(serialnumberObj != null) && (serialnumberObj instanceof BigInteger)) {
				return getCertificate((X500Principal)issuernameObj, (BigInteger)serialnumberObj);
			}
		}
		return null;
	}
	
	private X509Certificate getCertificate(String uri) {
		X509Certificate certificate = null;
		File dir = new File(base);
		try {
			FileInputStream fis = new FileInputStream(new File(dir, uri));
			try
			{
				// Se obtiene el certificado en su formato del archivo de la URI
				CertificateFactory cf = CertificateFactory.getInstance(ConstantesXADES.X_509);
				certificate = (X509Certificate)cf.generateCertificate(fis);
			} catch (CertificateException e1) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		} catch (FileNotFoundException e) {
		}
		return certificate;
	}

	private X509Certificate getCertificate(X500Principal issuername, BigInteger serialnumber) {
		X509Certificate certificate = null;
		// TODO: implementar localizador de certificado
		return certificate;
	}

}
