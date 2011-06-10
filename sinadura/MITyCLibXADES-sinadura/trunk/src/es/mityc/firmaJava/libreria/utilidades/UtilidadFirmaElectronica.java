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

package es.mityc.firmaJava.libreria.utilidades;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;


/**
 * Funciones de utilidades varias
 *
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 0.9 beta
 */
public class UtilidadFirmaElectronica { // implements ConstantesXADES

	/** Logger. */
	private static final Log LOG = LogFactory.getLog(UtilidadFirmaElectronica.class);
	/** Internationalizador. */
	private static final II18nManager I18N = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);

	/** OID de la política de autenticación del DNIe. */
	private static final String POLICY_DNIE_AUTHENTICATE = "2.16.724.1.2.2.2.4";
	/** OID de la política de firma del DNIe. */
	private static final String POLICY_DNIE_SIGN = "2.16.724.1.2.2.2.3";

	/** Selector para indicar si un certificado es del DNIe y de autenticación. */
	private static final X509CertSelector CS_DNIE_AUTHENTICATE = new X509CertSelector();
	/** Selector para indicar si un certificado es del DNIe y de firma. */
	private static final X509CertSelector CS_DNIE_SIGN = new X509CertSelector();
	static {
		try {
			CS_DNIE_AUTHENTICATE.setPolicy(new HashSet<String>(Arrays.asList(POLICY_DNIE_AUTHENTICATE)));
			CS_DNIE_SIGN.setPolicy(new HashSet<String>(Arrays.asList(POLICY_DNIE_SIGN)));
		} catch (IOException ex) {
			LOG.warn(I18N.getLocalMessage(ConstantsXAdES.I18N_UTILS_2));
			if (LOG.isDebugEnabled()) {
				LOG.debug(ex.getMessage(), ex);
			}
		}
	}

    /**
     * Decodifica una cadena a UTF-8
     * @param input Cadena a decodificar
     * @return cadena en UTF-8
     */
    public static String decodeUTF( byte[] input ) {
    	int longitud = input.length;
        char [] output = new char [ longitud ];
        int i = 0;
        int j = 0;
        while ( i < longitud ) {
            int b = input[ i++ ] & 0xff;
            // clasificado según el alto orden de 3 bits 
            switch ( b >>> 5 ) {
                default :
                    // codificación del 1 byte
                    // 0xxxxxxx
                    // uso justo de orden bajo de 7 bits
                    // 00000000 0xxxxxxx
                    output[ j++ ] = (char) ( b & 0x7f );
                    break;
                case 6:
                    // codificación del 2 byte
                    // 110yyyyy 10xxxxxx
                    // uso bajo de orden de 6 bits
                    int y = b & 0x1f;
                    // uso del orden bajo de 6 bits del byte siguiente
                    // debe tener un orden alto de 10 bits, el cual no comprobaremos
                    int x = input[ i++ ] & 0x3f;
                    // 00000yyy yyxxxxxx
                    output[ j++ ] = (char) ( y << 6 | x );
                    break;
                case 7:
                    // codificación del 3 byte 
                    // 1110zzzz 10yyyyyy 10xxxxxx
                    // assert ( b & 0x10 )
                    // == 0 : "UTF8Decoder does not handle 32-bit characters";
                	if ( (b & 0x10) == 0 ){
                    	throw new RuntimeException ( ConstantesXADES.UTF8DECODER_ERROR) ;
                    	
                    }
                    // uso del orden bajo de 4 bits
                    int z = b & 0x0f;
                    // uso del orden bajo de 6 bits del siguiente byte
                    // debería tener un orden alto de 10 bits, el cual no comprobaremos
                    y = input[ i++ ] & 0x3f;
                    // uso bajo del orden de 6 bits del siguiente byte
                    // debería tener un orden alto de 10 bits, el cual no comprobaremos
                    x = input[ i++ ] & 0x3f;
                    // zzzzyyyy yyxxxxxx
                    int asint = ( z << 12 | y << 6 | x );
                    output[ j++ ] = (char) asint;
                    break;
            } 
        } 
        return new String( output, 0 , j  );
    }
    
    /**
     * @param listaCertificadosTemp Lista de certificados temporales
     * @param emisorDN
     * @return
     * 
     * TODO: revisar este método y reconstruirlo decentemente
     */
    public static List<X509Certificate> filtraCertificados(List<X509Certificate> listaCertificadosTemp, String emisorDN){
        String[] allIssuers = emisorDN.split(ConstantesXADES.ALMOHADILLA) ;
        List<X509Certificate> devuelveCertificados = new ArrayList<X509Certificate>();
        Date hoy = new Date();
        int longitudCertificados = listaCertificadosTemp.size();
        for (int a=0; a < longitudCertificados; a++){
            X509Certificate certTemp = listaCertificadosTemp.get(a) ;
            int longitudIssuers = allIssuers.length;
            for (int b=0; b < longitudIssuers; b++){
            	if (certTemp.getIssuerDN().toString().indexOf(allIssuers[b]) >= 0) {
            		try {
            			certTemp.checkValidity(hoy);
            		} catch (CertificateExpiredException e) {
            			break;
            		} catch (CertificateNotYetValidException e) {
            			break;
            		}
            		devuelveCertificados.add(certTemp);
            		break;
            	}
            }
        }
        return devuelveCertificados;
    }
    
    /**
     * 
     * @param listaCertificadosTemp Lista de certificados temporales
     * @return
     */
    public static List<X509Certificate> filtraDNIe(List<X509Certificate> listaCertificadosTemp){
    	if (LOG.isTraceEnabled()) {
    		LOG.trace("Filtrando certificados del DNIe...");
    	}
    	List<X509Certificate> returnCertificates = new ArrayList<X509Certificate>();
    	Date hoy = new Date();
//    	ASN1InputStream asn1IS = null;
    	Iterator<X509Certificate> it = listaCertificadosTemp.iterator();
    	while (it.hasNext()) {
    		X509Certificate certTemp = it.next();
    		try {
    			certTemp.checkValidity(hoy);
    		} catch (CertificateExpiredException e) {
    			continue;
    		} catch (CertificateNotYetValidException e) {
    			continue;
    		}
    		if (!CS_DNIE_AUTHENTICATE.match(certTemp)){
    			returnCertificates.add(certTemp);
    		}
    	}
//    	int longitudCertificados = listaCertificadosTemp.size(); 
//    	for (int a = 0; a < longitudCertificados; a++) {
//    		X509Certificate certTemp = listaCertificadosTemp.get(a) ;
//    		try {
//    			certTemp.checkValidity(hoy);
//    		} catch (CertificateExpiredException e) {
//    			continue;
//    		} catch (CertificateNotYetValidException e) {
//    			continue;
//    		}
//    		if (!CS_DNIE_AUTHENTICATE.match(certTemp)){
//    			returnCertificates.add(certTemp);
//    		}
//    		if (UtilidadDNIe.isCertDNIe(certTemp.getIssuerDN().toString())) {
//    			try {
//    				// El certificado de autenticación tiene una certificate policy 2.16.724.1.2.2.2.4
//    				// El certificado de firma tiene una certificate policy 2.16.724.1.2.2.2.3
//    				// Recupera la certificate policy de este certificado
//    				byte[] policies = certTemp.getExtensionValue(ConstantesXADES.CERTIFICATE_POLICIES_OID);
//    				if (policies != null)  {
//    					//Falsos positivos
//    					asn1IS = new ASN1InputStream(policies);
//    					// Una extensión de certificado va como DER-encoded OCTET (ver getExtensionValue de X509Extension)
//    					ASN1OctetString ext = (ASN1OctetString)((ASN1InputStream)asn1IS).readObject();
//    					//asn1IS.close();
//    					asn1IS = new ASN1InputStream(ext.getOctets());
//    					ASN1Sequence seq = (ASN1Sequence)asn1IS.readObject();
//    					// Solo hay una PolicyInformation para el DNIe
//    					PolicyInformation pi = new PolicyInformation((ASN1Sequence)seq.getObjectAt(0));
//    					if (ConstantesXADES.POLICY_OID_CERTIFICADO_AUTENTICACION_DNIE.equals(pi.getPolicyIdentifier().getId()))
//    						continue;
//    				}
//    				returnCertificates.add(certTemp);
//    			} 
//    			catch (Exception ex) {
//    				returnCertificates.add(certTemp);
//    			}
//    			
//    			finally{
//    				if (asn1IS != null){
//    					try {
//							asn1IS.close();
//						} catch (IOException e) {
//							LOG.error(e);
//						}
//    				}
//    			}
//    		}
//    		else
//    			returnCertificates.add(certTemp);
//    	}
    	return returnCertificates;
    }
    
    public static String obtenerTipoReference(String esquema) {
    	
    	String tipoEsquema = null;
    	
		if ((ConstantesXADES.SCHEMA_XADES_132).equals(esquema))
			tipoEsquema = ConstantesXADES.SCHEMA_XADES  + ConstantesXADES.SIGNED_PROPERTIES;
		else if ((ConstantesXADES.SCHEMA_XADES_122).equals(esquema))
			tipoEsquema = ConstantesXADES.SCHEMA_XADES_122  + ConstantesXADES.SIGNED_PROPERTIES;
		else if ((ConstantesXADES.SCHEMA_XADES_111).equals(esquema))
			tipoEsquema = ConstantesXADES.SCHEMA_XADES_111  + ConstantesXADES.SIGNED_PROPERTIES;
		else {
			LOG.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_VALIDARFIRMA_ERROR1));
			return null;
		}
		
		return tipoEsquema;
    }
    
    // TODOLARGO: incluir también los OIDs
    public static final String DIGEST_ALG_SHA1   = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String DIGEST_ALG_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#sha256";
    public static final String DIGEST_ALG_SHA256_enc = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String DIGEST_ALG_SHA256_hmac = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String DIGEST_ALG_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#sha512";
    public static final String DIGEST_ALG_SHA512_enc = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String DIGEST_ALG_SHA512_hmac = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String DIGEST_ALG_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#sha224";
    public static final String DIGEST_ALG_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
    public static final String DIGEST_ALG_MD2 = "http://www.w3.org/2001/04/xmldsig-more#md2";
    public static final String DIGEST_ALG_MD4 = "http://www.w3.org/2001/04/xmldsig-more#md4";
    public static final String DIGEST_ALG_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
    public static final String DIGEST_ALG_RIPEMD128 = "http://www.w3.org/2001/04/xmldsig-more#ripemd128";
    public static final String DIGEST_ALG_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#ripemd160";
    public static final String DIGEST_ALG_RIPEMD256 = "http://www.w3.org/2001/04/xmldsig-more#ripemd256";
    public static final String DIGEST_ALG_RIPEMD320 = "http://www.w3.org/2001/04/xmldsig-more#ripemd320";
    public static final String DIGEST_ALG_TIGER = "http://www.w3.org/2001/04/xmldsig-more#tiger";
    public static final String DIGEST_ALG_WHIRLPOOL = "http://www.w3.org/2001/04/xmldsig-more#whirlpool";
    public static final String DIGEST_ALG_GOST3411 = "http://www.w3.org/2001/04/xmldsig-more#gost3411";
    
    
    /**
     * Devuelve el MessageDigest asociado a la uri (según la rfc 3275 y la rfc 4051).
     * 
     * @param uri Uri que define el algoritmo de digest (según las rfc 3275 y 4051).
     * @return MessageDigest asociado o null si no hay ninguno disponible para el algoritmo indicado.
     */
    public static MessageDigest getMessageDigest(String uri) {
    	MessageDigest md = null;
    	if (uri != null) {
			try {
	    		if (uri.equals(DIGEST_ALG_SHA1))
					md = MessageDigest.getInstance("SHA-1");
	    		else if (uri.equals(DIGEST_ALG_SHA256) || uri.equals(DIGEST_ALG_SHA256_enc) || uri.equals(DIGEST_ALG_SHA256_hmac))
	    			md = MessageDigest.getInstance("SHA-256");
	    		else if (uri.equals(DIGEST_ALG_SHA512) || uri.equals(DIGEST_ALG_SHA512_enc) || uri.equals(DIGEST_ALG_SHA512_hmac))
	    			md = MessageDigest.getInstance("SHA-512");
	    		else if (uri.equals(DIGEST_ALG_SHA224))
	    			md = MessageDigest.getInstance("SHA-224");
	    		else if (uri.equals(DIGEST_ALG_SHA384))
	    			md = MessageDigest.getInstance("SHA-384");
	    		else if (uri.equals(DIGEST_ALG_MD2))
	    			md = MessageDigest.getInstance("MD2");
	    		else if (uri.equals(DIGEST_ALG_MD4))
	    			md = MessageDigest.getInstance("MD4");
	    		else if (uri.equals(DIGEST_ALG_MD5))
	    			md = MessageDigest.getInstance("MD5");
	    		else if (uri.equals(DIGEST_ALG_RIPEMD128))
	    			md = MessageDigest.getInstance("RIPEDM128");
	    		else if (uri.equals(DIGEST_ALG_RIPEMD160))
	    			md = MessageDigest.getInstance("RIPEMD160");
	    		else if (uri.equals(DIGEST_ALG_RIPEMD256))
	    			md = MessageDigest.getInstance("RIPEMD256");
	    		else if (uri.equals(DIGEST_ALG_RIPEMD320))
	    			md = MessageDigest.getInstance("RIPEMD320");
	    		else if (uri.equals(DIGEST_ALG_TIGER))
	    			md = MessageDigest.getInstance("Tiger");
	    		else if (uri.equals(DIGEST_ALG_WHIRLPOOL))
	    			md = MessageDigest.getInstance("WHIRLPOOL");
	    		else if (uri.equals(DIGEST_ALG_GOST3411))
	    			md = MessageDigest.getInstance("GOST3411");
			} catch (NoSuchAlgorithmException ex) {
				LOG.info("Algoritmo de digest no disponible para: " + uri, ex);
			}
    	}
    	return md;
    }

}
