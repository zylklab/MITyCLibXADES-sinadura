package es.mityc.firmaJava.libreria.xades;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.utilidades.I18n;
import es.mityc.firmaJava.libreria.utilidades.NombreNodo;
import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;
import es.mityc.firmaJava.libreria.xades.errores.BadFormedSignatureException;
import es.mityc.firmaJava.libreria.xades.errores.FirmaXMLError;

public class UtilidadXadesA {
	
	private static Log log = LogFactory.getLog(UtilidadXadesA.class);

	/**
	 * Devuelve un array de bytes con los nodos involucrados en un sello de tiempo XAdES A.
	 * Los nodos a incluir requeridos ya deben estar creados. En caso contrario, 
	 * se devuelve un BadFormedSignatureException.
	 * 
	 * Según esquema, punto 7.7, los nodos a agregar, si existen, en el caso no distribuido son:
	 * 		- Todos los nodos Reference, por orden de aparición
	 * 		- El nodo SignedInfo
	 * 		- El nodo Signaturevalue
	 * 		- El nodo KeyInfo
	 * 	(Hasta aqui, son elementos propios del esquema XmlSignature)
	 * 
	 * 		- El nodo SignatureTimeStamp (XAdES T)
	 * 		- Los nodos CounterSignature (No se podrán modificar ni agregar más)
	 * 		- El nodo CompleteCertificateRefs (XAdES C)
	 * 		- El nodo CompleteRevocationRefs
	 * 		- El nodo AttributeCertificateRefs
	 * 		- El nodo AttributeRevocationRefs
	 * 		- El nodo SigAndRefsTimeStamp o RefsOnlyTimeStamp (XAdES X)
	 * 		- El nodo CertificateValues (XAdES XL) REQUERIDO
	 * 		- El nodo RevocationValues (XAdES XL) REQUERIDO
	 * 		- El nodo AttrAuthoritiesCertValues (Condicionado)
	 * 		- El nodo AttributeRevocationValues (Condicionado)
	 * 		- Los nodos ArchiveTimeStamp previos al actual
	 * 	(Hasta aqui, son elementos propios del esquema XAdES)
	 * 
	 * 		- Cualquier ds:Object no referenciado en <ds:Reference>
	 * 	(Se trata de nodos extra, fuera de esquema)
	 * 
	 * @param String esquemaURI .- Es la URI del esquema XAdES empleado
	 * @param Element firma.- Es el nodo de firma del que penden los nodos relacionados con el sello
	 * @param Element selloA.- En caso de validar un sello de tiempo A, no se deben incluir nodos a partir 
	 * 						   del validado (y quitando el validado). Para incluir todo, pasar un null.
	 * 
	 * @return byte[] .- Array de bytes concatenados de los nodos de entrada del sello (Véase lista superior).
	 */
	public static byte[] obtenerListadoXadesA(String esquemaURI, XMLSignature xmlSig, Element selloA)
						throws BadFormedSignatureException, FirmaXMLError, XMLSecurityException, IOException	{
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
		SignedInfo si = xmlSig.getSignedInfo();  	
    	Element firma = xmlSig.getElement();
    	CanonicalizationEnum cannon = null;
    	cannon = CanonicalizationEnum.getCanonicalization(si.getCanonicalizationMethodURI());
    	if (cannon == null || CanonicalizationEnum.UNKNOWN.equals(cannon)) {
    		log.warn("No se reconoce el algoritmo de canonicalización " + si.getCanonicalizationMethodURI() + ". Se toma el valor por defecto.");
    		cannon = CanonicalizationEnum.C14N_OMIT_COMMENTS;
    	}
		
	// Se agregan los nodos Reference
		ArrayList<Element> referenceNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.REFERENCE));
		if(referenceNodes.size() == 0)
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
					ConstantesXADES.REFERENCE + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
					ConstantesXADES.ESPACIO + referenceNodes.size());
		else {
			Reference referenceNode = null;
			for (int i = 0; i < si.getLength(); i++) {
				referenceNode = si.item(i);
				//baos.write(si.getReferencedContentAfterTransformsItem(i).getBytes());
				if (referenceNode != null) {
//					try {
//						baos.write(referenceNode.getReferencedBytes());
//					} catch (NullPointerException e) {
					Element reference = referenceNodes.get(i);
					String referenceUri = reference.getAttribute(ConstantesXADES.URI_MAYUS);
					if (referenceUri != ConstantesXADES.CADENA_VACIA &&
							!referenceUri.startsWith(ConstantesXADES.ALMOHADILLA)) {
						File firmado = new File(referenceUri);
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(firmado);
						} catch (FileNotFoundException e1) {
							throw new IOException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_VALIDARFIRMA_ERROR58));
						} 
						byte[] entrada = null; 
						fis.read(entrada);
						baos.write(entrada);	    					
					} else if (referenceUri != ConstantesXADES.CADENA_VACIA) {			
						Element nodo = UtilidadTratarNodo.getElementById(xmlSig.getDocument(), referenceUri.substring(1));
						if (nodo != null)
							baos.write(UtilidadTratarNodo.obtenerByte(
									nodo,
									obtenerCanonicalization(nodo, cannon))
							);
					}
				}
			}
		}
		
	// Se agrega el nodo SignedInfo
		ArrayList<Element> signedInfoNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNED_INFO));
		if(signedInfoNodes.size() != 1)
			// El nodo SignedInfo no se encuentra o no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
					ConstantesXADES.SIGNED_INFO + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
					ConstantesXADES.ESPACIO + signedInfoNodes.size());
		
		baos.write(si.getCanonicalizedOctetStream());
		
	// Se agrega el nodo SignatureValue
		Element signatureValueNode = null;
		ArrayList<Element> signatureValueNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE));
		if(signatureValueNodes.size() == 1)
			signatureValueNode = (Element)signatureValueNodes.get(0);
		else
			// El nodo SignatureValue no se encuentra o no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
					ConstantesXADES.SIGNATURE_VALUE + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
					ConstantesXADES.ESPACIO + signatureValueNodes.size());
		
		baos.write(UtilidadTratarNodo.obtenerByte(
				signatureValueNode, 
				obtenerCanonicalization(signatureValueNode, cannon)
			));
		
	// Se agrega el nodo KeyInfo
		Element keyInfoNode = null;
		ArrayList<Element> keyInfoNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.LIBRERIAXADES_KEY_INFO));
		if(keyInfoNodes.size() == 1)
			keyInfoNode = (Element)keyInfoNodes.get(0);
		else
			// El nodo SignatureValue no se encuentra o no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
					ConstantesXADES.LIBRERIAXADES_KEY_INFO + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
					ConstantesXADES.ESPACIO + keyInfoNodes.size());
		
		baos.write(UtilidadTratarNodo.obtenerByte(
				keyInfoNode, 
				obtenerCanonicalization(keyInfoNode, cannon)
			));
		
	// Se agregan los nodos SignatureTimeStamp 
		ArrayList<Element> nodosSigTimeStamp = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.SIGNATURE_TIME_STAMP));
		
		for (int i = 0; i < nodosSigTimeStamp.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					nodosSigTimeStamp.get(i), 
					obtenerCanonicalization(nodosSigTimeStamp.get(i), cannon)
				));
		}
		
	// Se agregan los nodos CounterSignature
		ArrayList<Element> counterSignatureNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.COUNTER_SIGNATURE));
		
		for (int i = 0; i < counterSignatureNodes.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					counterSignatureNodes.get(i), 
					obtenerCanonicalization(counterSignatureNodes.get(i), cannon)
				));
		}
		
	// Se agrega el nodo CompleteCertificateRefs 
		ArrayList<Element> completeCertificateRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.COMPLETE_CERTIFICATE_REFS));
		
		if(completeCertificateRefsNodes.size() > 1)
			// El nodo CompleteCertificateRefs no es único. Número de nodos encontrados: 
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.COMPLETE_CERTIFICATE_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					completeCertificateRefsNodes.size());
		
		for (int i = 0; i < completeCertificateRefsNodes.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					completeCertificateRefsNodes.get(i), 
					obtenerCanonicalization(completeCertificateRefsNodes.get(i), cannon)
				));
		}
		
	// Se agrega el nodo CompleteRevocationRefs 
		ArrayList<Element> completeRevocationRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.COMPLETE_REVOCATION_REFS));
		
		if(completeRevocationRefsNodes.size() > 1)
			// El nodo CompleteRevocationRefs no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.COMPLETE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					completeRevocationRefsNodes.size());

		for (int i = 0; i < completeRevocationRefsNodes.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					completeRevocationRefsNodes.get(i), 
					obtenerCanonicalization(completeRevocationRefsNodes.get(i), cannon)
				));
		}
		
	// Se agrega el nodo AttributeCertificateRefs, si existe 
		ArrayList<Element> attributeCertificateRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.ATTRIBUTE_CERTIFICATE_REFS));

		if((attributeCertificateRefsNodes.size() > 1))
			// El nodo AttributeCertificateRefs no es único. número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_CERTIFICATE_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					attributeCertificateRefsNodes.size());

		for (int i = 0; i < attributeCertificateRefsNodes.size();++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					attributeCertificateRefsNodes.get(i), 
					obtenerCanonicalization(attributeCertificateRefsNodes.get(i), cannon)
				));
		}
		
	// Se agrega el nodo AttributeRevocationRefs, si existe 
		ArrayList<Element> attributeRevocationRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.ATTRIBUTE_REVOCATION_REFS));

		if((attributeRevocationRefsNodes.size() > 1))
			// El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					attributeRevocationRefsNodes.size());

		for (int i = 0; i < attributeRevocationRefsNodes.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					attributeRevocationRefsNodes.get(i), 
					obtenerCanonicalization(attributeRevocationRefsNodes.get(i), cannon)
				));
		}

	// Se agrega el nodo SigAndRefsTimeStamp, si existe 
		ArrayList<Element> sigAndRefsTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.SIG_AND_REFS_TIME_STAMP));

		if((sigAndRefsTimeStampNodes.size() > 1))
			// El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					sigAndRefsTimeStampNodes.size());

		for (int i = 0; i < sigAndRefsTimeStampNodes.size(); ++i) {	
			baos.write(UtilidadTratarNodo.obtenerByte(
					sigAndRefsTimeStampNodes.get(i), 
					obtenerCanonicalization(sigAndRefsTimeStampNodes.get(i), cannon)
				));
		}
		
	// Se agrega el nodo RefsOnlyTimeStamp, si existe 
		ArrayList<Element> refsOnlyTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.REFS_ONLY_TIME_STAMP));

		if((refsOnlyTimeStampNodes.size() > 1))
			// El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					refsOnlyTimeStampNodes.size());

		for (int i = 0; i < refsOnlyTimeStampNodes.size(); ++i) {
			baos.write(UtilidadTratarNodo.obtenerByte(
					refsOnlyTimeStampNodes.get(i), 
					obtenerCanonicalization(refsOnlyTimeStampNodes.get(i), cannon)
				));
		}
			
	// Se agrega el nodo CertificateValues 
		ArrayList<Element> certificateValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.CERTIFICATE_VALUES));

		if((certificateValuesNodes.size() != 1))
			// El nodo CertificateValuesNodes no existe o no es único. número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.CERTIFICATE_VALUES + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					certificateValuesNodes.size());

		baos.write(UtilidadTratarNodo.obtenerByte(
				certificateValuesNodes.get(0), 
				obtenerCanonicalization(certificateValuesNodes.get(0), cannon)
			));
		
	// Se agrega el nodo RevocationValues 
		ArrayList<Element> revocationValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.REVOCATION_VALUES));

		if((revocationValuesNodes.size() != 1))
			// El nodo CertificateValuesNodes no existe o no es único. número de nodos encontrados:
			throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
					ConstantesXADES.ESPACIO + ConstantesXADES.CERTIFICATE_VALUES + ConstantesXADES.ESPACIO + 
					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
					revocationValuesNodes.size());

		baos.write(UtilidadTratarNodo.obtenerByte(
				revocationValuesNodes.get(0), 
				obtenerCanonicalization(revocationValuesNodes.get(0), cannon)
			));
		
	// Se agregan el nodo AttrAuthoritiesCertValues condicionalmente
		if (attributeCertificateRefsNodes.size() > 0 &&
				certificateValuesNodes.get(0).getChildNodes().getLength() < 
				completeCertificateRefsNodes.get(0).getChildNodes().getLength()) {
			
			ArrayList<Element> AttrValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
					new NombreNodo(esquemaURI, ConstantesXADES.ATTR_AUTH_CERT_VALUES));

			if((AttrValuesNodes.size() != 1))
				// El nodo AttrAuthoritiesCertValues no es único. número de nodos encontrados:
				throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
						ConstantesXADES.ESPACIO + ConstantesXADES.CERTIFICATE_VALUES + ConstantesXADES.ESPACIO + 
						I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
						AttrValuesNodes.size());

			baos.write(UtilidadTratarNodo.obtenerByte(
					AttrValuesNodes.get(0), 
					obtenerCanonicalization(AttrValuesNodes.get(0), cannon)
				));
		}
		
	// Se agrega el nodo AttributeRevocationValues condicionalmente 
		if (attributeCertificateRefsNodes.size() > 0 &&
				revocationValuesNodes.get(0).getChildNodes().getLength() < 
				completeRevocationRefsNodes.get(0).getChildNodes().getLength()) {
			ArrayList<Element> AttributeRevocationValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
					new NombreNodo(esquemaURI, ConstantesXADES.ATTRIBUTE_REVOCATION_VALUES));

			if((AttributeRevocationValuesNodes.size() != 1))
				// El nodo AttributeRevocationValues no es único. número de nodos encontrados:
				throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
						ConstantesXADES.ESPACIO + ConstantesXADES.CERTIFICATE_VALUES + ConstantesXADES.ESPACIO + 
						I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
						AttributeRevocationValuesNodes.size());

			baos.write(UtilidadTratarNodo.obtenerByte(
					AttributeRevocationValuesNodes.get(0), 
					obtenerCanonicalization(AttributeRevocationValuesNodes.get(0), cannon)
				));
		}	
		
	// Se agregan los sellos de tiempo ArchiveTimeStamp preexistentes
		ArrayList<Element> archiveTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
				new NombreNodo(esquemaURI, ConstantesXADES.ARCHIVE_TIME_STAMP));

		for (int i = 0; i < archiveTimeStampNodes.size(); ++i) {
			Element selloANode = archiveTimeStampNodes.get(i);
			// Sólo se han de añadir los sellos previos al validado, si es el caso
			if (selloA != null && selloA.equals(selloANode))
				break;
			baos.write(UtilidadTratarNodo.obtenerByte(
					selloANode, 
					obtenerCanonicalization(archiveTimeStampNodes.get(i), cannon)
				));
		}
		
	// Se agregan los nodos Object no referenciados por ningún reference en SignedProperties, exceptuando al que contiene a las QualifyingProperties
    	ObjectContainer object = null;
    	Element reference = null;
    	String objectId = null;
    	String referenceUri = null;
		for (int i = 0; i < xmlSig.getObjectLength(); i++) {
			object = xmlSig.getObjectItem(i);
			objectId = object.getId();
			boolean incluir = true;
			for (int j = 0; j < referenceNodes.size(); ++j) {
				reference = referenceNodes.get(j);
				referenceUri = reference.getAttribute(ConstantesXADES.URI_MAYUS);
				if (referenceUri != null && referenceUri.substring(1).equals(objectId)) {
					incluir = false;
					break;
				}
			}
			// Se excluye el nodo si las QualifyingProperties de la firma son hijo del nodo Object comprobado
			ArrayList<Element> quialifyingProp = UtilidadTratarNodo.obtenerNodos(firma, 2, new NombreNodo(esquemaURI, ConstantesXADES.QUALIFYING_PROPERTIES));
			for (int j = 0; j < quialifyingProp.size() && incluir; ++j) {
				if (quialifyingProp.get(j).equals(object.getElement().getFirstChild()))
					incluir = false;
			}
			
			if (incluir) {
				baos.write(UtilidadTratarNodo.obtenerByte(
						object.getElement(), 
						obtenerCanonicalization(object.getElement(), cannon)
					));
			}
		}
		
		return baos.toByteArray();
	}
	
	/**
	 * Recoge la canonicalización de un nodo, si está presente. 
	 * En caso contrario devuelve el parámetro "porDefecto".
	 * 
	 * @param nodo .- Nodo a leer
	 * @param porDefecto .- Canonicalización por defecto
	 * @return CanonicalizationEnum .- Canonicalización obtenida
	 * @throws FirmaXMLError .- En caso de que la canonicalización obtenida no se pueda leer
	 */
	private static CanonicalizationEnum obtenerCanonicalization(Element nodo, CanonicalizationEnum porDefecto) throws FirmaXMLError{
		// Se valida, si existe, el nodo CanonicalizationMethod    
		NodeList nodosCanonicalizationMethod = nodo.getElementsByTagNameNS(ConstantesXADES.SCHEMA_DSIG, 
					ConstantesXADES.CANONICALIZATION_METHOD);
		int numNodosCanonicalization = nodosCanonicalizationMethod.getLength();
		CanonicalizationEnum canonicalization = porDefecto;
		if (numNodosCanonicalization > 0) {
			Element nodoCanonicalizationMethod = (Element)nodosCanonicalizationMethod.item(0);
			String method = nodoCanonicalizationMethod.getAttribute(ConstantesXADES.ALGORITHM);
			canonicalization = CanonicalizationEnum.getCanonicalization(method);
			if (canonicalization.equals(CanonicalizationEnum.UNKNOWN)) { 
				// No se puede validar el método de canonalización:
				throw new FirmaXMLError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_VALIDARFIRMA_ERROR103) + 
						ConstantesXADES.ESPACIO + method);
			}
		}
		
		return canonicalization;
	}        

	/**
	 * Listado completo de identificadores de los nodos involucrados 
	 * en un sello de tiempo XAdES-A según los esquemas 1.1.1 y 1.2.2
	 * 
	 * @param esquemaURI
	 * @param xmlSig
	 * @param selloA
	 * @return
	 * @throws BadFormedSignatureException
	 * @throws FirmaXMLError
	 * @throws XMLSecurityException
	 */
    public static ArrayList<String> obtenerListadoIdsElementosXadesA(String esquemaURI, XMLSignature xmlSig, Element selloA)
    throws BadFormedSignatureException, FirmaXMLError, XMLSecurityException	{

    	ArrayList<String> input = new ArrayList<String> ();
    	
    	SignedInfo si = xmlSig.getSignedInfo();  	
    	Element firma = xmlSig.getElement();

//  	Se agregan las ids de los nodos apuntados en las URI de los nodos Reference
    	ArrayList<Element> referenceNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.REFERENCE));
    	if(referenceNodes.size() == 0)
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
    				ConstantesXADES.REFERENCE + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
    				ConstantesXADES.ESPACIO + referenceNodes.size());
    	else {	
    		Reference referenceNode = null;			
    		for (int i = 0; i < referenceNodes.size(); ++i) {
    			referenceNode = si.item(i);
    			String idRef = referenceNode.getElement().getAttribute(ConstantesXADES.URI_MAYUS);    	
    			if (idRef == null)
    				throw new BadFormedSignatureException("No se puede recuperar la URI del nodo Reference");
    			input.add(idRef); 
    		}
    	}

//  	Se agrega el nodo SignedInfo
    	Element signedInfoNode = null;
    	ArrayList<Element> signedInfoNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNED_INFO));
    	if(signedInfoNodes.size() == 1)
    		signedInfoNode = (Element)signedInfoNodes.get(0);
    	else
//  		El nodo SignedInfo no se encuentra o no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
    				ConstantesXADES.SIGNED_INFO + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
    				ConstantesXADES.ESPACIO + signedInfoNodes.size());
    	
    	String idSigInfo = signedInfoNode.getAttribute(ConstantesXADES.ID);
    	if (idSigInfo == null)
			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo SignedInfo");
    	input.add(ConstantesXADES.ALMOHADILLA + idSigInfo);

//  	Se agrega el nodo SignatureValue
    	Element signatureValueNode = null;
    	ArrayList<Element> signatureValueNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE));
    	if(signatureValueNodes.size() == 1)
    		signatureValueNode = (Element)signatureValueNodes.get(0);
    	else
//  		El nodo SignatureValue no se encuentra o no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
    				ConstantesXADES.SIGNATURE_VALUE + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
    				ConstantesXADES.ESPACIO + signatureValueNodes.size());

    	String idSigValue = signatureValueNode.getAttribute(ConstantesXADES.ID);
    	if (idSigValue == null)
			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo SignatureValue");
    	input.add(ConstantesXADES.ALMOHADILLA + idSigValue);

//  	Se agrega el nodo KeyInfo
    	Element keyInfoNode = null;
    	ArrayList<Element> keyInfoNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.LIBRERIAXADES_KEY_INFO));
    	if(keyInfoNodes.size() == 1)
    		keyInfoNode = (Element)keyInfoNodes.get(0);
    	else
//  		El nodo SignatureValue no se encuentra o no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
    				ConstantesXADES.LIBRERIAXADES_KEY_INFO + ConstantesXADES.ESPACIO +	I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) +
    				ConstantesXADES.ESPACIO + keyInfoNodes.size());

    	String idKeyInfo = keyInfoNode.getAttribute(ConstantesXADES.ID);
    	if (idKeyInfo == null)
			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo KeyInfo");
    	input.add(ConstantesXADES.ALMOHADILLA + idKeyInfo);

//  	Se agregan los nodos SignatureTimeStamp 
    	ArrayList<Element> nodosSigTimeStamp = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.SIGNATURE_TIME_STAMP));

    	if(nodosSigTimeStamp.size() < 1)
//  		No se pudo encontrar el nodo SignatureTimeStamp
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_21));

    	for (int i = 0; i < nodosSigTimeStamp.size(); ++i) {
        	String idSigTimeStamp = nodosSigTimeStamp.get(i).getAttribute(ConstantesXADES.ID);
        	if (idSigTimeStamp == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID de un sello de tiempo XAdES-T");
    		input.add(ConstantesXADES.ALMOHADILLA + idSigTimeStamp);
    	}

//  	Se agregan los nodos CounterSignature
    	ArrayList<Element> counterSignatureNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.COUNTER_SIGNATURE));

    	for (int i = 0; i < counterSignatureNodes.size(); ++i) {
        	String idCounterSig = counterSignatureNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idCounterSig == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID de una contrafirma");
    		input.add(ConstantesXADES.ALMOHADILLA + idCounterSig);
    	}

//  	Se agrega el nodo CompleteCertificateRefs 
    	ArrayList<Element> completeCertificateRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.COMPLETE_CERTIFICATE_REFS));

    	if(completeCertificateRefsNodes.size() > 1)
//  		El nodo CompleteCertificateRefs no es único. Número de nodos encontrados: 
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.COMPLETE_CERTIFICATE_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				completeCertificateRefsNodes.size());

    	for (int i = 0; i < completeCertificateRefsNodes.size(); ++i) {
        	String idCompCertRef = completeCertificateRefsNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idCompCertRef == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo CompleteCertificateRefs");
    		input.add(ConstantesXADES.ALMOHADILLA + idCompCertRef);
    	}

//  	Se agrega el nodo CompleteRevocationRefs 
    	ArrayList<Element> completeRevocationRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.COMPLETE_REVOCATION_REFS));

    	if(completeRevocationRefsNodes.size() > 1)
//  		El nodo CompleteRevocationRefs no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.COMPLETE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				completeRevocationRefsNodes.size());

    	for (int i = 0; i < completeRevocationRefsNodes.size(); ++i) {
        	String idCompRevRef = completeRevocationRefsNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idCompRevRef == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo CompleteRevocationRefs");
    		input.add(ConstantesXADES.ALMOHADILLA + idCompRevRef);
    	}

//  	Se agrega el nodo AttributeCertificateRefs, si existe 
    	ArrayList<Element> attributeCertificateRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.ATTRIBUTE_CERTIFICATE_REFS));

    	if((attributeCertificateRefsNodes.size() > 1))
//  		El nodo AttributeCertificateRefs no es único. número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_CERTIFICATE_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				attributeCertificateRefsNodes.size());

    	for (int i = 0; i < attributeCertificateRefsNodes.size();++i) {
        	String idAttrCertRef = attributeCertificateRefsNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idAttrCertRef == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo AttributeCertificateRefs");
    		input.add(ConstantesXADES.ALMOHADILLA + idAttrCertRef);
    	}

//  	Se agrega el nodo AttributeRevocationRefs, si existe 
    	ArrayList<Element> attributeRevocationRefsNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.ATTRIBUTE_REVOCATION_REFS));

    	if((attributeRevocationRefsNodes.size() > 1))
//  		El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				attributeRevocationRefsNodes.size());

    	for (int i = 0; i < attributeRevocationRefsNodes.size(); ++i) {
    		String idAttrRevRef = attributeRevocationRefsNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idAttrRevRef == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo AttributeRevocationRefs");
    		input.add(ConstantesXADES.ALMOHADILLA + idAttrRevRef);
    	}

//  	Se agrega el nodo SigAndRefsTimeStamp, si existe 
    	ArrayList<Element> sigAndRefsTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.SIG_AND_REFS_TIME_STAMP));

    	if((sigAndRefsTimeStampNodes.size() > 1))
//  		El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				sigAndRefsTimeStampNodes.size());

    	for (int i = 0; i < sigAndRefsTimeStampNodes.size(); ++i) {
    		String idSigAndRefs = sigAndRefsTimeStampNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (idSigAndRefs == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo SigAndRefsTimeStamp");
    		input.add(ConstantesXADES.ALMOHADILLA + idSigAndRefs);	
    	}

//  	Se agrega el nodo RefsOnlyTimeStamp, si existe 
    	ArrayList<Element> refsOnlyTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.REFS_ONLY_TIME_STAMP));

    	if((refsOnlyTimeStampNodes.size() > 1))
//  		El nodo AttributeCertificateRefs no es único. Número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.ATTRIBUTE_REVOCATION_REFS + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				refsOnlyTimeStampNodes.size());

    	for (int i = 0; i < refsOnlyTimeStampNodes.size(); ++i) {
    		String refsOnlyTimeStamp = refsOnlyTimeStampNodes.get(i).getAttribute(ConstantesXADES.ID);
        	if (refsOnlyTimeStamp == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo RefsOnlyTimeStamp");
    		input.add(ConstantesXADES.ALMOHADILLA + refsOnlyTimeStamp);
    	}

//  	Se agrega el nodo CertificateValues 
    	ArrayList<Element> certificateValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.CERTIFICATE_VALUES));

    	if((certificateValuesNodes.size() != 1))
//  		El nodo CertificateValuesNodes no existe o no es único. número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.CERTIFICATE_VALUES + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				certificateValuesNodes.size());
		
    	String certValueId = certificateValuesNodes.get(0).getAttribute(ConstantesXADES.ID);
    	if (certValueId == null)
			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo CertificateValues");
    	input.add(ConstantesXADES.ALMOHADILLA + certValueId);

//  	Se agrega el nodo RevocationValues 
    	ArrayList<Element> revocationValuesNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.REVOCATION_VALUES));

    	if((revocationValuesNodes.size() != 1))
//  		El nodo RevocationValuesNodes no existe o no es único. número de nodos encontrados:
    		throw new BadFormedSignatureException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.REVOCATION_VALUES + ConstantesXADES.ESPACIO + 
    				I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_38) + ConstantesXADES.ESPACIO + 
    				revocationValuesNodes.size());

    	String revValuesId = revocationValuesNodes.get(0).getAttribute(ConstantesXADES.ID);
    	if (revValuesId == null)
			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo RevocationValues");
    	input.add(ConstantesXADES.ALMOHADILLA + revValuesId);

//  	Se agregan los sellos de tiempo ArchiveTimeStamp preexistentes
    	ArrayList<Element> archiveTimeStampNodes = UtilidadTratarNodo.obtenerNodos(firma, 5, 
    			new NombreNodo(esquemaURI, ConstantesXADES.ARCHIVE_TIME_STAMP));

    	for (int i = 0; i < archiveTimeStampNodes.size(); ++i) {
    		Element selloANode = archiveTimeStampNodes.get(i);
    		if (selloA != null && selloA.equals(selloANode))
    			break;
        	String aTimeStampId = selloANode.getAttribute(ConstantesXADES.ID);
        	if (aTimeStampId == null)
    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo ArchiveTimeStamp");
    		input.add(ConstantesXADES.ALMOHADILLA + aTimeStampId);
    	}

    	// Se agregan los nodos Object no referenciados por ningún reference en SignedProperties, exceptuando al que contiene a las QualifyingProperties
    	ObjectContainer object = null;
    	Element reference = null;
    	String objectId = null;
    	String referenceUri = null;
		for (int i = 0; i < xmlSig.getObjectLength(); i++) {
			object = xmlSig.getObjectItem(i);
			objectId = object.getId();
			boolean incluir = true;
			for (int j = 0; j < referenceNodes.size(); ++j) {
				reference = referenceNodes.get(j);
				referenceUri = reference.getAttribute(ConstantesXADES.URI_MAYUS);
				if (referenceUri != null && referenceUri.substring(1).equals(objectId)) {
					incluir = false;
					break;
				}
			}
			// Se excluye el nodo si las QualifyingProperties de la firma son hijo del nodo Object comprobado
			ArrayList<Element> quialifyingProp = UtilidadTratarNodo.obtenerNodos(firma, 2, new NombreNodo(esquemaURI, ConstantesXADES.QUALIFYING_PROPERTIES));
			for (int j = 0; j < quialifyingProp.size() && incluir; ++j) {
				if (quialifyingProp.get(j).equals(object.getElement().getFirstChild()))
					incluir = false;
			}
			
			if (incluir) {
	        	String objectNodeId = object.getElement().getAttribute(ConstantesXADES.ID);
	        	if (objectNodeId == null)
	    			throw new BadFormedSignatureException("No se puede recuperar la ID del nodo Object");
				input.add(ConstantesXADES.ALMOHADILLA + objectNodeId);
			}
		}

    	return input;
    }
    
    /**
     * Listado de Includes del sello XAdES-A, según el esquema 1.3.2, caso distribuido.
     * (No comparten el mismo padre que el nodo de firma, son URIs externas).
     * 
     * Los nodos a referenciar son los siguientes, por orden:
     * 
     * 	- SignatureTimeStamp, cuantos existan.
	 *  - CounterSignature, cuantos existan.
	 *  - CompleteCertificateRefs, si existe.
	 *  - CompleteRevocationRefs, si existe.
	 *  - AttributeCertificateRefs, si existe.
	 *  - AttributeRevocationRefs, si existe.
	 *  - SigAndRefsTimeStamp, cuantos existan.
	 *  - RefsOnlyTimeStamp, cuantos existan.
	 *  - CertificateValues. Se debe añadir si no existe.
	 *  - RevocationValues. Se debe añadir si no existe.
	 *  - AttrAuthoritiesCertValues. Se debe añadir si no existe, condicionalmente (ver especificación).
	 *  - AttributeRevocationValues. Se debe añadir si no existe, condicionalmente (ver especificación).
	 *  - ArchiveTimestamp, cuantos existan, previos al actual.
     */
    public static ArrayList<String> casoDistribuido() {
    	/*TODO: Caso distribuido con nodos externos a la firma*/	
    	ArrayList<String> input = new ArrayList<String> ();
    	
    	return input;
    }
}
