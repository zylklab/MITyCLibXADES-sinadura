package es.mityc.firmaJava.libreria.xades ;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPResp;

import es.mityc.javasign.certificate.CertStatusException;
import es.mityc.javasign.certificate.ICertStatus;
import es.mityc.javasign.certificate.ICertStatusRecoverer;
import es.mityc.javasign.certificate.IOCSPCertStatus;
import es.mityc.javasign.certificate.IX509CRLCertStatus;



public class FirmaXMLAux {
	
	private static Log log = LogFactory.getLog(FirmaXMLAux.class);
	
	
	public static final void getListRec(X509Certificate cert, ICertStatusRecoverer certStatusManager,
			Map<String, ICertStatus> ocspStatusMap) throws CertStatusException, CertStoreException, NoSuchAlgorithmException,
			NoSuchProviderException, IOException, OCSPException {
		
		if (!ocspStatusMap.containsKey(getUniqueID(cert))) {
			
			List<ICertStatus> certStatusChain = certStatusManager.getCertChainStatus(cert); // aqui se hacen las ocsp

			for (ICertStatus iCertStatus : certStatusChain) {
				
				ocspStatusMap.put(getUniqueID(iCertStatus.getCertificate()), iCertStatus);					
				// se obtine el firmante de la ocsp
				X509Certificate ocspSignerCert = getOCSPSigner(iCertStatus);
				// rec
				getListRec(ocspSignerCert, certStatusManager, ocspStatusMap);				
			}			
		}
	}

	private static final X509Certificate getOCSPSigner(ICertStatus respStatus) throws IOException, OCSPException, CertStoreException, NoSuchAlgorithmException, NoSuchProviderException {
		
		X509Certificate ocspSignerCert = null;
		
		if (respStatus instanceof IOCSPCertStatus) {
			
			IOCSPCertStatus ocspStatus = (IOCSPCertStatus) respStatus;
			OCSPResp ocspResp = new OCSPResp(ocspStatus.getEncoded());
			BasicOCSPResp basicResponse = (BasicOCSPResp) ocspResp.getResponseObject();
			
			CertStore cs = basicResponse.getCertificates("Collection", null);
			Collection<? extends Certificate> certs = cs.getCertificates(null);
			
			for (Certificate cert : certs) {
				if (cert instanceof X509Certificate) {
					ocspSignerCert = ((X509Certificate) cert);
					if (basicResponse.verify(ocspSignerCert.getPublicKey(), null)) {
						return ocspSignerCert;
					}
				}
			}
			
		} else if (respStatus instanceof IX509CRLCertStatus) {
			// TODO revisar este caso. Probablemente no se añada la informacion de revocacion, de los certificados de la TSA ni del OCSP responder.
			log.warn("estado por CRL para las firmas XL no revisado");
		}
		
		return null;
	}
		
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	//////////////// REFACTOR
	//////////////// CODIGO REPETIDO
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	
	/**
	 * Metodo repetido. Está tambien en CertificateUtil (sinaduraCore), pero desde las libreias del mitic no es accesible sinaduraCore. 
	 * 
	 * @param cert
	 * @return
	 */
	private static String getUniqueID(X509Certificate cert) {
		
		return (cert.getSubjectX500Principal().getName() + cert.getSerialNumber());		
	}
	
	/**
	 * Metodo repetido. Está tambien en CertificateUtil (sinaduraCore), pero desde las libreias del mitic no es accesible sinaduraCore.
	 * 
	 * Este metodo ademas es publico!!
	 * 
	 * @param cert
	 * @return
	 */
	public static String getOCSPURL(X509Certificate certificate) {

		try {
			DERObject obj = getExtensionValue(certificate, X509Extensions.AuthorityInfoAccess.getId());
			if (obj == null) {
				return null;
			}

			ASN1Sequence AccessDescriptions = (ASN1Sequence) obj;
			for (int i = 0; i < AccessDescriptions.size(); i++) {
				ASN1Sequence AccessDescription = (ASN1Sequence) AccessDescriptions.getObjectAt(i);
				if (AccessDescription.size() != 2) {
					continue;
				} else {
					if (AccessDescription.getObjectAt(0) instanceof DERObjectIdentifier
							&& ((DERObjectIdentifier) AccessDescription.getObjectAt(0)).getId().equals("1.3.6.1.5.5.7.48.1")) {
						String AccessLocation = getStringFromGeneralName((DERObject) AccessDescription.getObjectAt(1));
						if (AccessLocation == null) {
							return null;
						} else {
							return AccessLocation;
						}
					}
				}
			}
			
			return null;
			
		} catch (IOException e) {
			
			return null;
		}
	}
	
	private static DERObject getExtensionValue(X509Certificate cert, String oid) throws IOException {
		
        byte[] bytes = cert.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }
	
	private static String getStringFromGeneralName(DERObject names) throws IOException {
		
        DERTaggedObject taggedObject = (DERTaggedObject) names ;
        return new String(ASN1OctetString.getInstance(taggedObject, false).getOctets(), "ISO-8859-1");
    }
	
}
