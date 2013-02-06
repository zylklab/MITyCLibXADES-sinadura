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

package es.mityc.firmaJava.libreria.xades ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.IgnoreAllErrorHandler;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.ocsp.BasicOCSPResp;
import org.bouncycastle.ocsp.CertificateStatus;
import org.bouncycastle.ocsp.OCSPException;
import org.bouncycastle.ocsp.OCSPResp;
import org.bouncycastle.tsp.TimeStampToken;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.firmaJava.libreria.errores.ClienteError;
import es.mityc.firmaJava.libreria.excepciones.AddXadesException;
import es.mityc.firmaJava.libreria.utilidades.Base64Coder;
import es.mityc.firmaJava.libreria.utilidades.I18n;
import es.mityc.firmaJava.libreria.utilidades.NombreNodo;
import es.mityc.firmaJava.libreria.utilidades.UtilidadFechas;
import es.mityc.firmaJava.libreria.utilidades.UtilidadFicheros;
import es.mityc.firmaJava.libreria.utilidades.UtilidadFirmaElectronica;
import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;
import es.mityc.firmaJava.libreria.xades.elementos.xades.CRLRef;
import es.mityc.firmaJava.libreria.xades.elementos.xades.CRLRefs;
import es.mityc.firmaJava.libreria.xades.elementos.xades.CRLValues;
import es.mityc.firmaJava.libreria.xades.elementos.xades.CertificateValues;
import es.mityc.firmaJava.libreria.xades.elementos.xades.DataObjectFormat;
import es.mityc.firmaJava.libreria.xades.elementos.xades.EncapsulatedX509Certificate;
import es.mityc.firmaJava.libreria.xades.elementos.xades.ObjectIdentifier;
import es.mityc.firmaJava.libreria.xades.elementos.xades.SignatureProductionPlace;
import es.mityc.firmaJava.libreria.xades.elementos.xades.SigningTime;
import es.mityc.firmaJava.libreria.xades.errores.BadFormedSignatureException;
import es.mityc.firmaJava.libreria.xades.errores.FirmaXMLError;
import es.mityc.firmaJava.libreria.xades.errores.InvalidInfoNodeException;
import es.mityc.firmaJava.libreria.xades.errores.PolicyException;
import es.mityc.firmaJava.role.IClaimedRole;
import es.mityc.firmaJava.ts.ConstantesTSA;
import es.mityc.firmaJava.ts.TSCliente;
import es.mityc.firmaJava.ts.TSClienteError;
import es.mityc.firmaJava.ts.TSValidacion;
import es.mityc.firmaJava.ts.TSValidator;
import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.certificate.CertStatusException;
import es.mityc.javasign.certificate.ICertStatus;
import es.mityc.javasign.certificate.ICertStatus.CERT_STATUS;
import es.mityc.javasign.certificate.ICertStatusRecoverer;
import es.mityc.javasign.certificate.IOCSPCertStatus;
import es.mityc.javasign.certificate.IX509CRLCertStatus;
import es.mityc.javasign.certificate.ocsp.OCSPLiveConsultant;
import es.mityc.javasign.exception.SignMITyCException;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;
import es.mityc.javasign.pkstore.IPKStoreManager;
import es.mityc.javasign.trust.TrustAbstract;
import es.mityc.javasign.xml.refs.AbstractObjectToSign;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import es.mityc.javasign.xml.refs.SignObjectToSign;
import es.mityc.javasign.xml.resolvers.IPrivateData;
import es.mityc.javasign.xml.resolvers.IResourceData;
import es.mityc.javasign.xml.resolvers.MITyCResourceResolver;
import es.mityc.javasign.xml.resolvers.ResolverPrivateData;
import es.mityc.javasign.xml.resolvers.XAdESResourceResolverSpi;
import es.mityc.javasign.xml.transform.Transform;
import es.mityc.javasign.xml.transform.TransformEnveloped;
import es.mityc.javasign.xml.xades.IStoreElements;
import es.mityc.javasign.xml.xades.policy.IFirmaPolicy;
import es.mityc.javasign.xml.xades.policy.PoliciesManager;

/**
 * Clase principal para la firma de documentos XML
 *
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 0.9 beta
 */
public class FirmaXML {
	
    private static Log log = LogFactory.getLog(FirmaXML.class);
    private static II18nManager i18n = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);

    String 			profileDirectory 	= ConstantesXADES.CADENA_VACIA;
    private String 	xadesNS 			= ConstantsXAdES.DEFAULT_NS_XADES;
    private String	xadesSchema			= null;
    private String 	xmldsigNS 			= ConstantsXAdES.DEFAULT_NS_XMLSIG;
    private String 	servidorTSA 		= null;
    private String 	algoritmoTSA 		= null;
//    final static private String	DIR_OCSP = "./RespuestasOCSP";
//    final static private String	DIR_CERTS = "./Certificados";
    
    // Almacena las id´s para el esquema 1.1.1
    private ArrayList<String> idNodoSelloTiempo = new ArrayList<String>();
    private String idNodoCertificateRefs = null;
    private String idNodoRevocationRefs = null;
    private String idSigProperties = null;
    private String idSignatureValue = null;
    /** Listado de resolvers aplicados. */
    private ArrayList<ResourceResolverSpi> resolvers;

    /**
     * Crea una nueva instancia de FirmaXML
     */
    public FirmaXML() {
    }
    
    /**
     * <p>Establece la TSA que se utilizará para obtener los sellos de tiempo en caso de ser necesario.</p> 
     * @param url ruta de la TSA
     */
    public void setTSA(String url) {
    	this.servidorTSA = url;
    }
    
    /**
     * <p>Establece el namespace que se aplicará a los nodos de XML Signature.</p>
     * @param namespace Namespace aplicado a XMLSig
     */
    public void setDefaultNSXmlSig(String namespace) {
    	this.xmldsigNS = namespace;
    }
    
	/**
	 * <p>Establece el Locale del sistema antiguo de internacionalización.</p>
	 * @param locale Localización a aplicar
	 */
	public void setLocale(String locale) {
		I18n.setLocale(locale, locale.toUpperCase());
	}

    
	/**
	 * Añade una instancia encargada de resolver los accesos a elementos firmados en la firma cuyo contenido es privado.
	 *  
	 * @param resolver objeto que implementa la interfaz IPrivateDate para el acceso a elementos privados
	 */
	public void addResolver(IPrivateData resolver) {
		addResolver(new ResolverPrivateData(resolver));
	}
	
	/**
	 * Añade una instancia encargada de resolver accesos a información.
	 * 
	 * @param resolver resolver
	 */
	public void addResolver(MITyCResourceResolver resolver) {
		if (resolvers == null) {
			resolvers = new ArrayList<ResourceResolverSpi>();
		}
		resolvers.add(resolver);
	}
	
	/**
	 * Añade una instancia encargada de resolver los accesos a elementos firmados en la firma que requieran un acceso especial.
	 *  
	 * @param resolver objeto que implementa la interfaz IResourceData para el acceso a elementos
	 */
	public void addResolver(IResourceData resolver) {
		addResolver(new XAdESResourceResolverSpi(resolver));
	}


    public void sign2Stream(
            X509Certificate firmaCertificado,
            DataToSign xml,
            IPKStoreManager storeManager,
            OutputStream salida, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception{
    	
    	PrivateKey pk = storeManager.getPrivateKey(firmaCertificado);
		signFile(firmaCertificado, xml, pk, salida, storeManager.getProvider(firmaCertificado), truster, tsaOcspUrl, zain);
    }

//    public Document sign2Doc(
//            X509Certificate firmaCertificado,
//            DataToSign xml,
//            IPKStoreManager storeManager) throws Exception{
//    	PrivateKey pk = storeManager.getPrivateKey(firmaCertificado);
//		return sign2Doc(firmaCertificado, xml, pk, storeManager.getProvider());
//    }

    /**
     * Firma un fichero XML
     * @param pk Clave privada del certificado firmante
     * @param firmaCertificado Certificado firmante
     * @param xml Fichero XML a firmar
     * @param directorioPerfil Directorio de configuracion de Firefox
     * @throws java.lang.Exception En caso de error
     * @return Array de bytes con el XML firmado
     */
    public boolean signFile(X509Certificate firmaCertificado,
            DataToSign xml,
            IPKStoreManager storeManager,
            String destino,
            String nombreArchivo, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception{
    	
    	PrivateKey pk = storeManager.getPrivateKey(firmaCertificado);
    	return signFile(firmaCertificado, xml, pk, destino, nombreArchivo, storeManager.getProvider(firmaCertificado), truster, tsaOcspUrl, zain);
    }
    
	private void signFile(X509Certificate certificadoFirma, DataToSign xml,
            PrivateKey pk, OutputStream salida, Provider provider, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {

    	Object[] res = signFile(certificadoFirma, xml, pk, provider, truster, tsaOcspUrl, zain);

    	if (res[1] != null)
        	throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_43));
        
        try
        {
        	XMLUtils.outputDOM((Document)res[0], salida, true);
        }
        catch (Throwable t)
        {
        	if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
        	else
        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
        }
    }
	
//	private Document sign2Doc(X509Certificate certificadoFirma, DataToSign xml, 
//    		PrivateKey pk, Provider provider) throws Exception {
//    	Object[] res = signFile(certificadoFirma, xml, null, pk, provider);
//        
//    	Document doc = (Document) res[0];
//    	return doc;
//	}
//	
//	private Document sign2Doc(X509Certificate certificadoFirma, DataToSign xml, String nodoRaizXml,
//    		PrivateKey pk, Provider provider) throws Exception {
//    	Object[] res = signFile(certificadoFirma, xml, nodoRaizXml, pk, provider);
//        
//    	Document doc = (Document) res[0];
//    	return doc;
//	}

    
	private boolean signFile(X509Certificate certificadoFirma, DataToSign xml,
    		PrivateKey pk, String destino, String nombreArchivo, Provider provider, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {
		
		if (destino == null || nombreArchivo == null) {
			// No se proporcionaron los datos de firma
			throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_31));
		}
		
    	Object[] res = signFile(certificadoFirma, xml, pk, provider, truster, tsaOcspUrl, zain);
        
//        // Si se firma XADES-C exclusivamente, se guardan las respuestaOCSP y los certificados 
//        // con un nombre asociado al fichero de firma y en la misma ruta temporal
    	Document doc = (Document) res[0];
//        if (res[1] != null) {
//        	doc = addURIXadesC(doc, saveOCSPFiles((ArrayList<RespYCerts>)res[1], destino, nombreArchivo), xml.getBaseURI());
//        } 
        
        // Se guarda la firma en su destino
        File fichero = new File(destino + nombreArchivo); 
        FileOutputStream f = new FileOutputStream(fichero);
        
        try
        {
        	XMLUtils.outputDOM(doc, f,true);
        }
        catch (Throwable t)
        {
        	if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
        	else
        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
        } finally {
        	f.close(); 
        }

        return true ;
    }

	// Añadidos los parametros truster y tsaOcspUrl, para añadir todas las ocsps de la cadena del timestamp (compatibilidad ZAIN)
	public Object[] signFile(X509Certificate certificadoFirma, DataToSign dataToSign,
            PrivateKey pk, Provider provider, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {

		ArrayList<RespYCerts> respuestas = new ArrayList<RespYCerts>();
		
//        String nodosCadenaParaFirma = null;
//        if (nodoParaFirmaXml == null){
//            nodosCadenaParaFirma = configuracion.getValor(ConstantesXADES.XML_NODE_TO_SIGN);
//        }else{
//            nodosCadenaParaFirma = nodoParaFirmaXml;
//        }
//        StringTokenizer stTok = new StringTokenizer(nodosCadenaParaFirma, ConstantesXADES.COMA);
//        String[] nodosParaFirma = new String[stTok.countTokens()];
//        int aa = 0;
//        boolean valor = stTok.hasMoreElements();
//        while(valor){
//            nodosParaFirma[aa] = ((String)stTok.nextElement()).trim();
//            aa++;
//            valor = stTok.hasMoreElements();
//        }

        Init.init() ;

        Document doc = dataToSign.getDocument();
        if (doc == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler(new IgnoreAllErrorHandler());
	        try {
	        	InputStream is = dataToSign.getInputStream();
	        	if (is != null) {
		            InputSource isour = new InputSource(is);
		            String encoding = dataToSign.getXMLEncoding();
		            isour.setEncoding(encoding);
		            doc = db.parse(isour);
	        	} else {
	        		doc = db.newDocument();
	        	}
	        } catch (IOException ex) {
	        	throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_50));
	        }
        }
        
        // Se toman las variables de configuración de DataToSign
        XAdESSchemas esquemaTemp = dataToSign.getEsquema();
        if (esquemaTemp != null) {
        	xadesSchema = esquemaTemp.getSchemaUri(); //configuracion.getValor(ConstantesXADES.XADES_SCHEMA);
        } else {
        	xadesSchema = XAdESSchemas.XAdES_132.getSchemaUri();
        }
        
        String algTemp = dataToSign.getAlgDigestTSA();
        if (algTemp != null)
        	algoritmoTSA = algTemp;
        else
        	algoritmoTSA = ConstantesTSA.SHA1;
        
        String algDigestXML = (dataToSign.getAlgDigestXmlDSig() != null) ? dataToSign.getAlgDigestXmlDSig() : UtilidadFirmaElectronica.DIGEST_ALG_SHA1;
        // Consulta si puede resolver el algoritmo antes de continuar. Si no puede, lanza excepción
        if (JCEMapper.translateURItoJCEID(algDigestXML) == null) {
        	throw new SignMITyCException(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_1, algDigestXML));
        }
        
        XMLSignature.setDefaultPrefix(Constants.SignatureSpecNS, xmldsigNS);
        
        XMLSignature firma = new XMLSignature(doc,
        		dataToSign.getBaseURI(),
        		XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
        firma.setId(UtilidadTratarNodo.newID(doc, ConstantesXADES.SIGNATURE_NODE_ID));
        firma.getSignedInfo().setId(UtilidadTratarNodo.newID(doc, ConstantesXADES.SIGNED_INFO_NODE_ID));
		if (resolvers != null) {
			Iterator<ResourceResolverSpi> it = resolvers.iterator();
			while (it.hasNext()) {
				firma.addResourceResolver(it.next());
			}
		}


        firma.setXPathNamespaceContext(xmldsigNS, ConstantesXADES.SCHEMA_DSIG);
        EnumFormatoFirma tipoFirma = dataToSign.getXadesFormat();
        boolean xadesActivo = (tipoFirma.compareTo(EnumFormatoFirma.XAdES_BES)>=0);

        if(xadesActivo){
            firma.setXPathNamespaceContext(xadesNS, xadesSchema);
        }

        Element elementoPrincipal = null;
        // TODO: permitir utilizar cadenas XPATH para indicar el nodo que contendrá la firma
        if (!dataToSign.isEnveloped()) {
        	doc.appendChild(firma.getElement());
        } else {
        	String nodoRaizXml = dataToSign.getParentSignNode();
	        if (nodoRaizXml == null) { // Si no se indicó, se toma el primero del documento
	        	elementoPrincipal = doc.getDocumentElement();//(Element) doc.getFirstChild();
	        } else { // Sí se indicó
	        	NodeList nodos = doc.getElementsByTagName(nodoRaizXml);
	            if(nodos.getLength() != 0) // Si se encuentra el/los nodos se toma el primero 
	                elementoPrincipal = (Element)nodos.item(0);
	            else { // Si no se encuentra el nodo, se realiza la busqueda con un identificador en lugar de TagName
	            	Node nodo = UtilidadTratarNodo.getElementById(doc, nodoRaizXml);
		            if(nodo != null) // Si se encuentra el nodo, se toma como nodo padre de la firma
		                elementoPrincipal = (Element)nodo;
		            else
		            	throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_2)) ;
	            }
	        }
	        elementoPrincipal.appendChild(firma.getElement());
        }
        
        idSigProperties = UtilidadTratarNodo.newID(doc, ConstantesXADES.GUION_SIGNED_PROPERTIES);
        if(xadesActivo) {

        	String tipoEsquema = UtilidadFirmaElectronica.obtenerTipoReference(xadesSchema);
    		
            firma.addDocument(ConstantesXADES.ALMOHADILLA +  firma.getId()
            		+ idSigProperties, null, algDigestXML, 
            		UtilidadTratarNodo.newID(doc, ConstantesXADES.SIGNED_PROPERTIES_ID), tipoEsquema);
        }

        firma.addKeyInfo(certificadoFirma);
        firma.addKeyInfo(certificadoFirma.getPublicKey()) ;
        String idCert = UtilidadTratarNodo.newID(doc, ConstantesXADES.CERTIFICATE1);
        firma.getKeyInfo().setId(idCert);
        
        // TODO: cambiar la manera de indicar los elementos que se firman a una manera más estándar a través de objetos externos,
        // objetos internos en firma y objetos internos fuera de firma.
        // Añadimos los elementos propios de la firma en XADES
        
        // Firma el certificado de firma
        firma.addDocument(ConstantesXADES.ALMOHADILLA + idCert, null, algDigestXML, null, null);
        
        // Incluye los objetos que se quiere firmar
        ArrayList<ObjectToSign> objects = dataToSign.getObjects();
        if (objects != null) {
        	Iterator<ObjectToSign> it = objects.iterator();
        	while (it.hasNext()) {
        		ObjectToSign obj = it.next();
        		AbstractObjectToSign objToSign = obj.getObjectToSign();

    			String refId = UtilidadTratarNodo.newID(doc, "Reference-ID-");
    			List<ObjectContainer> containers = objToSign.getObjects(doc);
    			if (containers != null) {
    				for (ObjectContainer objectContainer : containers) {
    					firma.appendObject(objectContainer);
					}
    			}
    			
                String objId = objToSign.getReferenceURI();
                // Construye las transformadas que se aplicarán al objeto
                Transforms trans = null;
                List<Transform> list = objToSign.getTransforms();
                // Si la firma está dentro de lo firmado indica que la transformada debe ser de enveloped
                if (dataToSign.isEnveloped()) {
                    if (objId != null) {
                        Element aFirmar = UtilidadTratarNodo.getElementById(doc, objId);
                        if (UtilidadTratarNodo.isChildNode(firma.getElement(), aFirmar)) {
                            list.add(new TransformEnveloped());
                        }
                    }
                }
                if (list.size() > 0) {
                    trans = new Transforms(doc);
                    for (Transform transform : list) {
                        trans.addTransform(transform.getAlgorithm(), transform.getExtraData(doc));
                    }
                }

                MITyCResourceResolver resolver = objToSign.getResolver();
    			if (resolver != null) {
    				firma.addResourceResolver(resolver);
    			}
    			
    			String typeInfo = objToSign.getType();
    			firma.addDocument(objId, trans, algDigestXML, refId, typeInfo);

    			obj.setId(ConstantesXADES.ALMOHADILLA + refId);
        	}
        }

        XAdESSchemas schema = null;
        if(xadesActivo){

        	schema = XAdESSchemas.getXAdESSchema(xadesSchema);
        	if (schema == null) {
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_44) 
        				+ ConstantesXADES.ESPACIO + xadesSchema);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_45));
        	}
            addXades(doc,
                    firma.getId(),
                    certificadoFirma,
                    firma.getElement(),
                    schema,
                    dataToSign,
                    algDigestXML);
            if (dataToSign.hasPolicy())  {
            	addXadesEPES(firma.getElement(), dataToSign.getPolicyKey());
            } else if (XAdESSchemas.XAdES_111.equals(schema)) {
            	// Se escribe una política implícita
            	addXadesEPES(firma.getElement(), ConstantesXADES.LIBRERIAXADES_IMPLIEDPOLICY_MANAGER);
            }
        }

        // TODO: este código no es multihilo, corregir
//        String provId = JCEMapper.getProviderId();
        try {
//    		Security.addProvider(provider);
    		JCEMapper.setProviderSignatureThread(provider);

        	firma.sign(pk);
        } catch (Exception ex) {
        	log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4), ex);
        	throw ex;
//        	if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
//        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
//        	else if (t.getMessage().startsWith(ConstantesXADES.FIRMA_NO_CONTIENE_DATOS))
//        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_1));
//        	else
//        		throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
        } finally {
//    		JCEMapper.setProviderId(provId);
        	JCEMapper.removeProviderSignatureThread();
//    		Security.removeProvider(provider.getName());
        }

        // Añadimos el Id al nodo signature value
 
    	Element elementoValorFirma = null ;
        
        NodeList nodoValorFirma = firma.getElement().getElementsByTagNameNS(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE);
        if(nodoValorFirma.getLength() != 0)
            elementoValorFirma = (Element)nodoValorFirma.item(0);
        else
            throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_5));
        // Le añadimos el elemento ID
        Attr idValorFirma = doc.createAttributeNS(null, ConstantesXADES.ID);
        idSignatureValue = UtilidadTratarNodo.newID(doc, ConstantesXADES.SIGNATURE_VALUE);
        idValorFirma.setValue(idSignatureValue);
        NamedNodeMap elementoIdAtributosValorFirma =
                elementoValorFirma.getAttributes();
        elementoIdAtributosValorFirma.setNamedItem(idValorFirma);


        //Comprobamos si se debe de firmar añadiendo el elemento de XADES-T
        
        boolean xadesT =(tipoFirma.compareTo(EnumFormatoFirma.XAdES_T)>=0); 

        log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_1) + xadesT);

        // START - Modificacion XADES-XL (compatibilidad zain).
        X509Certificate tsSigner = null;
        // END - Modificacion XADES-XL (compatibilidad zain).        
        
        if(xadesT) {

            try
            {
            	// Añadimos XADES-T

            	if (servidorTSA!=null && !servidorTSA.trim().equals(ConstantesXADES.CADENA_VACIA))
            	{
                	// Obtenemos la respuesta del servidor TSA
	                TSCliente tsCli = null;
//	                if(estadoProxy)
//	                {
//						System.setProperty("http.proxyHost", servidorProxy);
//						System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
//						if (isProxyAuth) {
//							Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
//						} 
//						else {
//							Authenticator.setDefault(null);
//						}
//	                }
                    tsCli = new TSCliente(servidorTSA, algoritmoTSA);

	                // Se añaden los elementos propios de la firma XADES-T
	                byte[] byteSignature = UtilidadTratarNodo.obtenerByteNodo(firma.getElement(), ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE, CanonicalizationEnum.C14N_OMIT_COMMENTS, 5);
	                
        	        // Modificacion XADES-XL (compatibilidad ZAIN)
	                byte[] selloTiempo = tsCli.generarSelloTiempo(byteSignature);
	                TSValidacion tsValidacion = TSValidator.validarSelloTiempo(byteSignature, selloTiempo);
	                
	                TimeStampToken tst = tsValidacion.getTst();
    				try {
    					CertStore cs = tst.getCertificatesAndCRLs("Collection", null);
    					Collection<? extends Certificate> certs = cs.getCertificates(null);
    					if (certs.size() > 0) {
    						Certificate cert = certs.iterator().next();
    						if (cert instanceof X509Certificate) {
    							tsSigner = ((X509Certificate) cert);
    						}
    					}
    				} catch (NoSuchAlgorithmException ex) {
    					throw ex;
    				} catch (NoSuchProviderException ex) {
    					throw ex;
    				} catch (CMSException ex) {
    					throw ex;
    				} catch (CertStoreException ex) {
    					throw ex;
    				}
	    				
	                addXadesT(firma.getElement(), firma.getId(), selloTiempo);
            	}
            	else
            	{
            		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_6));
            	}
            }
            catch (AddXadesException e)
            {
                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_7) + e.getMessage()) ;
            }
        }

        // Comprobamos si se debe de firmar añadiendo los elementos de XADES-C
        boolean xadesC = (tipoFirma.compareTo(EnumFormatoFirma.XAdES_C)>=0);
        log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_2) + xadesC);
        
        if(xadesC){

        	try
        	{

        		// Comprobamos si se ha realizado antes la firma XADES-T. En caso contrario se le avisa
        		// al usuario que no puede realizarse la firma XADES-C
        		if(xadesT){
        			if (dataToSign.getCertStatusManager() == null) {
        				throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_53));
        			}
        			// Añadimos XADES-C
        			
        			
        	        // Modificacion XADES-XL (compatibilidad ZAIN)
        			List<ICertStatus> allCertList;
        			
        			if (!zain) {
        				// implementacion de xades XL original
        				allCertList = dataToSign.getCertStatusManager().getCertChainStatus(certificadoFirma);
        			
        			} else {
        				
        				// implementacion de xades XL con todas las ocsps
        				
        				// Cadena de firma y de las ocsp
	        			Map<String, ICertStatus> ocspStatusMap = new LinkedHashMap<String, ICertStatus>();
	        			FirmaXMLAux.getListRec(certificadoFirma, dataToSign.getCertStatusManager(), ocspStatusMap);
	
	        			// TSA
	        			String autoTsaOcspUrl = FirmaXMLAux.getOCSPURL(tsSigner);
	        			if (autoTsaOcspUrl == null) {
	        				log.info("no se ha podido obtener la url del ocsp responder de la TSA");
	        				if (tsaOcspUrl != null && !tsaOcspUrl.equals("")) {
	        					autoTsaOcspUrl = tsaOcspUrl;	
	        				} else {
	        					throw new IOException("Error: Es necesario especificar la url del OCSP responder de la cadena de la TSA.");
	        				}
	        			}
	
	        			ICertStatusRecoverer tsaCertStatusRecoverer = new OCSPLiveConsultant(autoTsaOcspUrl, truster);
	        			
	        			FirmaXMLAux.getListRec(tsSigner, tsaCertStatusRecoverer, ocspStatusMap);
	        			
	
	        			// Se revisa que todas las ocsps estan OK
	        			allCertList = new ArrayList<ICertStatus>();
	        			for (String key : ocspStatusMap.keySet()) {
	        				ICertStatus certStatus = ocspStatusMap.get(key);
	        				if (certStatus.getStatus().equals(CERT_STATUS.valid)) {
	        					allCertList.add(certStatus);
	        				} else {
								// La comprobacion de revocacion del firmante ya se hace previamente así que este caso practicamente
								// no se deberia dar (tendria que estar revocado un certificados intermedio, raiz, tsa o ocsp). 
								throw new IOException("No se puede generar una firma XL. Estado de revocacion del certificado "
										+ certStatus.getCertificate().getSubjectX500Principal() + ": " + certStatus.getStatus());
	        				}
						}
        			}
        			
        			respuestas = convertICertStatus2RespYCerts(allCertList);
        			
        			addXadesC(firma.getElement(), respuestas, schema, algDigestXML, zain);

        		} 
        		else
    	        {
    	        	throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_24)) ;
    	        }
        	}
        	catch (CertStatusException e) {
                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_56) + e.getMessage()) ;
        	}
        	catch (AddXadesException e)
        	{
        		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_10) + e.getMessage()) ;
        	}
        }

        
        
        // Comprobamos si se debe de firmar añadiendo los elementos de XADES-X
        boolean xadesX = (tipoFirma.compareTo(EnumFormatoFirma.XAdES_X)>=0);
        log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_3) + xadesX);
        
        boolean xadesXL = (tipoFirma.compareTo(EnumFormatoFirma.XAdES_XL)==0);
        log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_4) + xadesXL);
        			
        //xadesXL = xadesX;	// Si es XAdES-X, se pone xades-XL a true para redondear
        // Si se firma XADES-C exclusivamente, se guardan las respuestaOCSP y los certificados 
        // con un nombre asociado al fichero de firma y en la misma ruta temporal
        // TODO: solucionar nombre de los ficheros OCSP
        if (xadesC && !xadesXL) {
        	try {
        		doc = addURIXadesC(firma.getElement(), saveOCSPFiles(respuestas, dataToSign.getElementsStorer()), dataToSign.getBaseURI());
        	} catch (FirmaXMLError ex) {
    			throw new ClienteError("Error al guardar ficheros de estados de certificados", ex);
        	}
        }
        
        if(xadesX){
           	// Para realizar la firma XADES-XL se deben completar antes los
           	// formatos de firmas XADES-T y XADES-C

        	if (xadesT && xadesC ) {
        		
        		// Se obtiene el nodo raíz de la firma
        		Element signatureElement = firma.getElement();
        		if (!(new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE).equals(
        				new NombreNodo(signatureElement.getNamespaceURI(), signatureElement.getLocalName())))) {
        			// No se encuentra el nodo Signature
        			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
        					ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
        		}
        		
        		// A partir del nodo raíz de la firma se obtiene el nodo UnsignedSignatureProperties
        		Element unsignedSignaturePropertiesElement = null;
        		NodeList unsignedSignaturePropertiesNodes = 
        			signatureElement.getElementsByTagNameNS(xadesSchema, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
        		
        		if (unsignedSignaturePropertiesNodes.getLength() != 1) {
        			// El nodo UnsignedSignatureProperties no existe o no es único 
        			log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
        					ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES +	ConstantesXADES.ESPACIO + 
        					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) + ConstantesXADES.ESPACIO +
    						unsignedSignaturePropertiesNodes.getLength());
        			// El sistema no soporta nodos UnsignedSignatureProperties múltiples
        			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_41));
        		} else
        			unsignedSignaturePropertiesElement = (Element)unsignedSignaturePropertiesNodes.item(0);
        		
        		// Se añaden los elementos propios de la firma XADES-X
        		switch (dataToSign.getXAdESXType()) {
	        		case TYPE_2:
	        			addXadesX2(unsignedSignaturePropertiesElement);
	        			break;
	        		case TYPE_1:
        			default:
        				addXadesX(unsignedSignaturePropertiesElement);
        		}
        	}
        	else
	        {
	        	throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_25)) ;
	        }
        }
	
        if(xadesXL){
           	// Para realizar la firma XADES-XL se deben completar antes los
           	// formatos de firmas XADES-T, XADES-C y XADES-X

	        if (xadesT && xadesC && xadesX) {    	
	            try
	            {
	            	// Añadimos XADES-XL
	            	
	                addXadesXL(firma.getElement(), respuestas, schema, zain);
	            }
	            catch (Exception e)
	            {
	                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_12) + e.getMessage(), e) ;
	            }
	        }
	        else
	        {
	        	throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_13)) ;
	        }
        }
        
//        boolean xadesA = dataToSign.isXadesA();
//        
//        if (xadesA) {
//            try
//            {
//            	// Añadimos el sello de tiempo del nives XADES-A
//
//            	if (servidorTSA!=null && !servidorTSA.trim().equals(ConstantesXADES.CADENA_VACIA))
//            	{
//                	// Obtenemos la respuesta del servidor TSA
//	                TSCliente tsCli = null;
////	                if(estadoProxy)
////	                {
////						System.setProperty("http.proxyHost", servidorProxy);
////						System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
////						if (isProxyAuth) {
////							Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
////						} 
////						else {
////							Authenticator.setDefault(null);
////						}
////	                }
//                    tsCli = new TSCliente(servidorTSA, algoritmoTSA);
//
//                 // Se chequea que existan los nodos requeridos para el sello de tiempo
//                 // CertificateValues y RevocationValues
//                    ArrayList<Element> certificateValuesNodes = UtilidadTratarNodo.obtenerNodos(firma.getElement(), 5, 
//            				new NombreNodo(xadesSchema, ConstantesXADES.CERTIFICATE_VALUES));
//            		ArrayList<Element> revocationValuesNodes = UtilidadTratarNodo.obtenerNodos(firma.getElement(), 5, 
//            				new NombreNodo(xadesSchema, ConstantesXADES.REVOCATION_VALUES));
//
//            		if((certificateValuesNodes.size() != 1))
//            			// Se requiere el nodo CertificateValues para agregar un sello XAdES-A
//            			log.warn("Se va a agregar el nodo requerido para el sello XAdES-A CertificateValues");
//
//            		if((revocationValuesNodes.size() != 1))
//            			// Se requiere el nodo RevocationValues para agregar un sello XAdES-A
//            			log.warn("Se va a agregar el nodo requerido para el sello XAdES-A RevocationValues");
//            		
//            		if((certificateValuesNodes.size() != 1) || (revocationValuesNodes.size() != 1)) {
//            			
//            			// Se busca el nodo UnsignedSignatureProperties. Si no existe, se crea
//            			ArrayList<Element> unsignedSigProperties = UtilidadTratarNodo.obtenerNodos(firma.getElement(), 4, 
//            					new NombreNodo(xadesSchema, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES));
//            			
//            			ArrayList<Element> qualifyingPropertiesNodes = UtilidadTratarNodo.obtenerNodos(firma.getElement(), 2, 
//            					new NombreNodo(xadesSchema, ConstantesXADES.QUALIFYING_PROPERTIES));
//            			if (unsignedSigProperties == null || unsignedSigProperties.size() == 0) {
//            				Element propiedadesElementosNoFirmados =
//            					doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_PROPERTIES);
//
//            				// Creamos los atributos de UnsignedProperties
//            				Attr propiedadesNoFirmadasId = doc.createAttributeNS(null, ConstantesXADES.ID);
//            				propiedadesNoFirmadasId.setValue(UtilidadTratarNodo.newID(doc, 
//            						firma.getId() + ConstantesXADES.GUION_UNSIGNED_PROPERTIES));
//            				NamedNodeMap atributosSinFirmarPropiedadesElemento =
//            					propiedadesElementosNoFirmados.getAttributes();
//            				atributosSinFirmarPropiedadesElemento.setNamedItem(propiedadesNoFirmadasId);
//
//            				Element propiedadesSinFirmarFirmaElementos =
//            					doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
//            				
//            		    	propiedadesElementosNoFirmados.appendChild(propiedadesSinFirmarFirmaElementos);
//            		    	qualifyingPropertiesNodes.get(0).appendChild(propiedadesElementosNoFirmados);
//            		    	
//            		    	respuestas = convertICertStatus2RespYCerts(dataToSign.getCertStatusManager().getCertStatus(certificadoFirma));
//            			}
//            	    	
//            			addXadesXL(firma.getElement(), respuestas, schema);
//            		}
//                    
//   	             // Para los esquemas 1.1.1 y 1.2.2 se añaden includes con los nodos tomados
//            		ArrayList<String> inc = null;
//            		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema) ||
//            				ConstantesXADES.SCHEMA_XADES_122.equals(xadesSchema)) {
//            			inc = UtilidadXadesA.obtenerListadoIdsElementosXadesA(xadesSchema, firma, null);
//            		}
//	                
//                 // Se obtiene la cadena de bytes de entrada del sello XAdES A
//                	byte[] input = UtilidadXadesA.obtenerListadoXadesA(xadesSchema, firma, null);
//	                addXadesA(firma.getElement(), tsCli.generarSelloTiempo(input), inc) ;
//            	}
//            	else
//            	{
//            		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_6));
//            	}
//            }
//            catch (AddXadesException e)
//            {
//                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_7) + e.getMessage()) ;
//            }
//        }
        
        Object[] res = new Object[2];
        res[0] = doc;
        if (xadesC && !xadesXL)
        	res[1] = respuestas;
        else
        	res[1] = null;
        
        return res;
    }
	
	private ArrayList<RespYCerts> convertICertStatus2RespYCerts(List<ICertStatus> status) {
		ArrayList<RespYCerts> resps = new ArrayList<RespYCerts>((status != null) ? status.size() : 0);
		if (status != null) {
			Iterator<ICertStatus> itStatus = status.iterator();
			while (itStatus.hasNext()) {
				RespYCerts resp = new RespYCerts();
				resp.setCertstatus(itStatus.next());
				resps.add(resp);
			}
		}
		return resps;
	}
    
//    private String relativizeRute(String baseUri, File file) {
//		String strFile = null;
//    	try {
//			URI relative = new URI(URIEncoder.encode(baseUri, "UTF-8"));
//			URI uri = file.toURI();//new URI("file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8"));
//			strFile = URIEncoder.relativize(relative.toString(), uri.toString());
//		} catch (UnsupportedEncodingException e) {
////			try {
//				strFile = file.toURI().toString();//"file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
////			} catch (UnsupportedEncodingException ex) {
////				// TODO: lanzar aviso
////			}
//		} catch (URISyntaxException e) {
////			try {
//				strFile = file.toURI().toString();//"file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
////				strFile = "file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
////			} catch (UnsupportedEncodingException ex) {
////				// TODO: lanzar aviso
////			}
//		}
//		
//    	return strFile;
//    }

    /**
     * Este método realiza la implementación de la firma XADES-BES
     * @param doc Documento de firma
     * @param firmaID Identificador del nodo de firma
     * @param firmaCertificado Certificado que realiza la firma
     * @param elementoPrincipalFirma Elemento principal del nodo de firma
     * @return Documento de firma con formato XADES-BES
     * @throws Exception
     */
    private Document addXades(Document doc,
            String firmaID,
            X509Certificate firmaCertificado,
            Element elementoPrincipalFirma, 
            XAdESSchemas schemaXades,
            DataToSign dataToSign,
            String algDigestXML) throws AddXadesException {

        // Añadimos el objeto
        Element elementoObjeto = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.OBJECT);
        elementoObjeto.setAttributeNS(null, ConstantesXADES.ID, UtilidadTratarNodo.newID(doc, firmaID + ConstantesXADES.GUION_OBJECT ));
        elementoPrincipalFirma.appendChild(elementoObjeto) ;

        // Creamos el QualifyingProperties
        Element elemntQualifyingProperties = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.QUALIFYING_PROPERTIES);
        elementoObjeto.appendChild(elemntQualifyingProperties);

        // Creamos los atributos de QualifyingProperties
        elemntQualifyingProperties.setAttributeNS(null, ConstantesXADES.TARGET , ConstantesXADES.ALMOHADILLA + firmaID);
        
        // Creamos el elemento SignedProperties
        Element propiedadesFirmadasElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNED_PROPERTIES);
        elemntQualifyingProperties.appendChild(propiedadesFirmadasElemento);

        // Creamos los atributos de SignedProperties
        propiedadesFirmadasElemento.setAttributeNS(null, ConstantesXADES.ID, firmaID  + idSigProperties);

        // Creamos el xades:SignedSignatureProperties
        Element propiedadesFirmadasElementoFirma = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNED_SIGNATURE_PROPERTIES);
        propiedadesFirmadasElemento.appendChild(propiedadesFirmadasElementoFirma);

        // Creamos el xades:SigningTime
        Date signingDate = dataToSign.getSignDate();
        if ((signingDate == null) && (schemaXades.equals(XAdESSchemas.XAdES_111)))
        	throw new AddXadesException("SigningTime es requerido");
        if (signingDate != null) {
	        SigningTime tiempoFirma = new SigningTime(schemaXades, signingDate);
	        Element tiempoFirmaElemento = null;
			try {
				tiempoFirmaElemento = tiempoFirma.createElement(doc, xadesNS);
			} catch (InvalidInfoNodeException e) {
				 throw new AddXadesException(e.getMessage(), e);
			}
	        propiedadesFirmadasElementoFirma.appendChild(tiempoFirmaElemento);
        }

        // Creamos el xades:SigningCertificate
        Element certificadoFirmaElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNING_CERTIFICATE);
        propiedadesFirmadasElementoFirma.appendChild(certificadoFirmaElemento);

        // Creamos el xades:Cert
        Element certificadoElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CERT);
        File signCertFile = dataToSign.getSigningCert();
        if (signCertFile != null) {
        	String uri = UtilidadFicheros.relativizeRute(dataToSign.getBaseURI(), signCertFile);
        	certificadoElemento.setAttributeNS(null, ConstantesXADES.URI_MAYUS, uri);
        }

        // Creamos el xades:CertDigest
        Element resumenCertificadoElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CERT_DIGEST);


        // Creamos el xades:DigestMethod
        Element metodoResumenElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_METHOD);
        metodoResumenElemento.setAttributeNS(null, ConstantesXADES.ALGORITHM, algDigestXML);

        // Creamos el xades:DigestValue
        String resumenCertificado =ConstantesXADES.CADENA_VACIA;
        
        try {
			MessageDigest resumenCertificadoTemp = UtilidadFirmaElectronica.getMessageDigest(algDigestXML);
			if (resumenCertificadoTemp == null)
	        	throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_16));
            byte[] byteMessageDigest =resumenCertificadoTemp.digest(firmaCertificado.getEncoded());
            resumenCertificado = new String(Base64Coder.encode(byteMessageDigest));
        }
        catch (CertificateEncodingException cee)
        {
        	throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_17));
        }

        Element elementDigestValue = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_VALUE);
        elementDigestValue.appendChild(doc.createTextNode(resumenCertificado));

        // Creamos el xades:IssuerSerial
        Element elementoEmisorSerial = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ISSUER_SERIAL );

        // Creamos el xades:X509IssuerName
        Element elementoX509EmisorNombre = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.X_509_ISSUER_NAME);
        elementoX509EmisorNombre.appendChild(doc.createTextNode(firmaCertificado.getIssuerX500Principal().getName()));

        // Creamos el xades:X509SerialNumber
        Element elementoX509NumeroSerial = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.X_509_SERIAL_NUMBER);
        elementoX509NumeroSerial.appendChild(doc.createTextNode(firmaCertificado.getSerialNumber().toString()));
        
        // Creamos el xades:SignatureProductionPlace.
        String[] produc = dataToSign.getProductionPlace();
        if (produc != null) {
        	SignatureProductionPlace spp = new SignatureProductionPlace(schemaXades, produc[0], produc[1], produc[2], produc[3]);
	        Element productionPlaceElemento = null;
			try {
				productionPlaceElemento = spp.createElement(doc, xadesNS);
			} catch (InvalidInfoNodeException e) {
				 throw new AddXadesException(e.getMessage(), e);
			}
	        propiedadesFirmadasElementoFirma.appendChild(productionPlaceElemento);
        }
        
        
//        String rolesFirmante = configuracion.getValor(ConstantesXADES.SIGNER_ROLES);
//        boolean roleFirmante = false;
//        Element elementoRoleFirmanteElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNER_ROLE);
//        
//        if(!Configuracion.isEmpty(rolesFirmante))
//        {
//        	Element elementoRolesDemandadosElementos = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CLAIMED_ROLES);
//        	elementoRoleFirmanteElemento.appendChild(elementoRolesDemandadosElementos);
//
//        	
//        	// Añadimos un nuevo elemento SignerRoles
//        	StringTokenizer roles = new StringTokenizer(rolesFirmante, ConstantesXADES.COMA);
//        	boolean valor = roles.hasMoreElements();
//	        while(valor){
//	        	String role = (String) roles.nextElement();
//	        	Element elementClaimedRoleElement = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CLAIMED_ROLE);
//	        	elementClaimedRoleElement.appendChild(doc.createTextNode(role.trim()));
//	        	elementoRolesDemandadosElementos.appendChild(elementClaimedRoleElement);
//	        	valor = roles.hasMoreElements();
//	        }
//	        roleFirmante = true;
//	    }
        
        resumenCertificadoElemento.appendChild(metodoResumenElemento);
        resumenCertificadoElemento.appendChild(elementDigestValue);

        certificadoElemento.appendChild(resumenCertificadoElemento);

        elementoEmisorSerial.appendChild(elementoX509EmisorNombre);
        elementoEmisorSerial.appendChild(elementoX509NumeroSerial);

        certificadoElemento.appendChild(elementoEmisorSerial);


        certificadoFirmaElemento.appendChild(certificadoElemento);


//        if(roleFirmante)
//        {
//        	propiedadesFirmadasElementoFirma.appendChild(elementoRoleFirmanteElemento);
//        }
        
        // Se crea el xades:SignerRole. Para ello se consulta el fichero de propiedades
        ArrayList<IClaimedRole> rolesFirmante = dataToSign.getClaimedRoles();
        if (rolesFirmante != null) {
            Element elementoRoleFirmanteElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNER_ROLE);
            propiedadesFirmadasElementoFirma.appendChild(elementoRoleFirmanteElemento);
            
        	Element elementoRolesDemandadosElementos = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CLAIMED_ROLES);
        	elementoRoleFirmanteElemento.appendChild(elementoRolesDemandadosElementos);
            Iterator<IClaimedRole> it = rolesFirmante.iterator();
            while (it.hasNext()) {
	        	Element elementClaimedRoleElement = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CLAIMED_ROLE);
	        	elementClaimedRoleElement.appendChild(it.next().createClaimedRoleContent(doc));
	        	elementoRolesDemandadosElementos.appendChild(elementClaimedRoleElement);
            }
        }
        
        // Se agrega información sobre los objetos firmados
        ArrayList<ObjectToSign> objects = dataToSign.getObjects();
        if (objects != null && objects.size() > 0) {

        	// Se crea el elemento SignedDataObjectProperties
        	Element signedDataObjectProperties = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.XADES_SIGNED_DATA_OBJECT_PROPERTIES);
        
        	Iterator<ObjectToSign> it = objects.iterator();
        	DataObjectFormat dof = null;	// Nodo a escribir
        	ObjectIdentifier oi = null;
        	
        	ObjectToSign obj = null; 		// Objeto de datos
        	String id = null;				// Identificador del objeto
        	String desc = null;				// Descripción del formato del objeto
        	String tipoMIME = null;			// Tipo MIME del objeto firmado
        	URI encoding = null;			// Codificación del objeto
        	
        	URI referenceId = null;
        	while (it.hasNext()) {
        		obj = it.next();
        		
        		if (obj != null) {
        			// Se recupera el identificador
        			id = obj.getId();
        			// Se recoge la descripción
            		desc = obj.getDescription();
            		// Se recoge el tipo MIME
            		tipoMIME = obj.getMimeType();
            		// Se recoge la codificación
            		encoding = obj.getEncoding();
            		// Se recupera ObjectIdentifier
            		oi = obj.getObjectIdentifier();
        		}
        		
        		if ((desc == null) && (tipoMIME == null) && (oi == null))
        			continue;

        		// Identificador del objeto
        		if (id != null) {
        			try {
        				referenceId = new URI(id);
        			} catch (URISyntaxException e) {
        				log.error(e.getMessage(), e);
        			}
        		}
        		
        		if ((referenceId == null) || (schemaXades == null))
        		{
        			log.error("No se puede incluir el objeto DataObjectFormat porque faltan datos");
        			throw new AddXadesException(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_2));
        		}
        		
        		dof = new DataObjectFormat(schemaXades, 
        				referenceId, 
        				desc, 
        				tipoMIME);

        		if (encoding != null)
        			dof.setEncoding(encoding);
        		
        		if (oi != null)
        			dof.setObjectIdentifier(oi);
        		
        		try {
					signedDataObjectProperties.appendChild(dof.createElement(doc, xadesNS));
				} catch (DOMException e) {
					throw new AddXadesException(e.getMessage(), e);
				} catch (InvalidInfoNodeException e) {
					log.error(e.getMessage(), e);
					throw new AddXadesException(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_2));
				}
        	}
        	
        	// Se añade el nodo generado, si contiene información
        	if (signedDataObjectProperties.getChildNodes().getLength() > 0)
        		propiedadesFirmadasElemento.appendChild(signedDataObjectProperties);
        }

        return null;
    }
    
    private void addXadesEPES(Element elementoPrincipalFirma, String confPolicyManager) throws AddXadesException {
    	if (confPolicyManager == null) {
    		confPolicyManager = ConstantesXADES.LIBRERIAXADES_IMPLIEDPOLICY_MANAGER;
    	}
    	// Se obtiene el manager para la política indicada
    	IFirmaPolicy policyManager = PoliciesManager.getInstance().getEscritorPolicy(confPolicyManager);
    	if (policyManager == null) {
    		// PolicyManager buscado no disponible
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_46) + 
    				ConstantesXADES.ESPACIO + confPolicyManager);
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_47));
    	}
    	
    	XAdESSchemas schema = XAdESSchemas.getXAdESSchema(xadesSchema);
    	if (schema == null) {
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_44) + 
    				ConstantesXADES.ESPACIO + xadesSchema);
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_45));
    	}
    	
    	try {
    		policyManager.writePolicyNode(elementoPrincipalFirma, xmldsigNS, xadesNS, schema);
    	} catch (PolicyException ex) {
    		// Error escribiendo políticas
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_48) + 
    				ConstantesXADES.ESPACIO + ex.getMessage(), ex);
    		throw new AddXadesException(ex.getMessage(), ex);
    	}
    	
    }

    /**
     * Este método añade la implementación para XADES-T
     * @param doc Documento de firma con formato XADES-BES
     * @param firmaID Identificador del nodo de firma
     * @param selloTiempo Respuesta del servidor TSA con el sello de tiempo en formato binario
     * @return Documento de firma con formato XADES-T
     * @throws Exception
     */
    private Document addXadesT(Element firma, String firmaID, byte[] selloTiempo)
    throws AddXadesException    {    	 	

    	Document doc = firma.getOwnerDocument();
    	Element elementoPrincipal = null ;
    	NodeList nodos = firma.getElementsByTagNameNS(xadesSchema, ConstantesXADES.QUALIFYING_PROPERTIES);
    	if(nodos.getLength() != 0)
    		elementoPrincipal = (Element)nodos.item(0);
    	else
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_18)) ;

    	Element propiedadesElementosNoFirmados =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_PROPERTIES);
    	
    	// Creamos los atributos de UnSignedProperties
    	Attr propiedadesNoFirmadasId = doc.createAttributeNS(null, ConstantesXADES.ID);
    	propiedadesNoFirmadasId.setValue(UtilidadTratarNodo.newID(doc, 
    			firmaID  + ConstantesXADES.GUION_UNSIGNED_PROPERTIES));
    	NamedNodeMap atributosSinFirmarPropiedadesElemento =
    		propiedadesElementosNoFirmados.getAttributes();
    	atributosSinFirmarPropiedadesElemento.setNamedItem(propiedadesNoFirmadasId);

    	Element propiedadesSinFirmarFirmaElementos =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
    	
    	// Se buscan otros sellos de tiempo en la firma y se les asigna una Id si no la tienen
    	NodeList sellosPreexistentes = doc.getElementsByTagNameNS(xadesSchema, ConstantesXADES.SIGNATURE_TIME_STAMP);
    	int numSellos = sellosPreexistentes.getLength();
    	for (int i = 0; i < numSellos; ++i) {
    		Element sello = (Element) sellosPreexistentes.item(i);
    		String selloId = sello.getAttribute(ConstantesXADES.ID);
    		if (selloId == null) {
    			Attr informacionElementoSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.ID);
    			selloId = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO);
    	    	informacionElementoSigTimeStamp.setValue(selloId);
    	    	sello.getAttributes().setNamedItem(informacionElementoSigTimeStamp);
    		}
    		// Se almacena su nombre de Id por si es preciso referenciarlos
    		idNodoSelloTiempo.add(selloId); 		
    	}
    	
    	// Se crea el nodo de sello de tiempo
    	Element tiempoSelloElementoFirma =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIGNATURE_TIME_STAMP);
	
    	// Se escribe una Id única
    	Attr informacionElementoSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String idSelloTiempo = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO);
    	informacionElementoSigTimeStamp.setValue(idSelloTiempo);
    	idNodoSelloTiempo.add(idSelloTiempo);
    	tiempoSelloElementoFirma.getAttributes().setNamedItem(informacionElementoSigTimeStamp);

    	// Se incluye un nodo que referencia a la Id de SignatureValue
    	if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema) 
    			|| ConstantesXADES.SCHEMA_XADES_122.equals(xadesSchema)) {
    		
    		String nombreNodoUri = null;
    		String tipoUri = null;
    		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) {
    			nombreNodoUri = ConstantesXADES.HASH_DATA_INFO;
    			tipoUri = ConstantesXADES.URI_MINUS;
    		} else {
    			nombreNodoUri = ConstantesXADES.INCLUDE;
    			tipoUri = ConstantesXADES.URI_MAYUS;
    		}
    		
        	Element informacionElementoHashDatos = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + nombreNodoUri);
    		
    		Attr informacionElementoHashDatosUri = doc.createAttributeNS(null, tipoUri);
    		informacionElementoHashDatosUri.setValue(ConstantesXADES.ALMOHADILLA + idSignatureValue);

    		NamedNodeMap informacionAtributosElementoHashDatos = informacionElementoHashDatos.getAttributes();
    		informacionAtributosElementoHashDatos.setNamedItem(informacionElementoHashDatosUri);

    		tiempoSelloElementoFirma.appendChild(informacionElementoHashDatos) ;
    	}
    	
    	// Se crea el nodo canonicalizationMethod en los esquemas 1.2.2 y 1.3.2
    	if (!ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) {
    		Element canonicalizationElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CANONICALIZATION_METHOD);		
    		Attr canonicalizationAttribute = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
    		canonicalizationAttribute.setValue(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
    		canonicalizationElemento.getAttributes().setNamedItem(canonicalizationAttribute);

    		tiempoSelloElementoFirma.appendChild(canonicalizationElemento);
    	}
		
		// Se crea el nodo del sello de tiempo
		Element tiempoSelloEncapsulado =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ENCAPSULATED_TIME_STAMP);

    	tiempoSelloEncapsulado.appendChild(
    			doc.createTextNode(new String(Base64Coder.encode(selloTiempo))));
    	Attr tiempoSelloEncapsuladoId = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String idEncapsulated = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO_TOKEN);
    	tiempoSelloEncapsuladoId.setValue(idEncapsulated);
    	tiempoSelloEncapsulado.getAttributes().setNamedItem(tiempoSelloEncapsuladoId);
    	
    	
    	tiempoSelloElementoFirma.appendChild(tiempoSelloEncapsulado);

    	propiedadesSinFirmarFirmaElementos.appendChild(tiempoSelloElementoFirma);
    	propiedadesElementosNoFirmados.appendChild(propiedadesSinFirmarFirmaElementos);
    	elementoPrincipal.appendChild(propiedadesElementosNoFirmados);
    	return doc;
    }

    /**
     * Este método añade la implementacion para XADES-C
     * @param doc Documento de firma con formato XADES-T
     * @param tiempoRespuesta Fecha y hora de la respuesta del servidor OCSP
     * @param mensajeRespuesta Valor del OCSPResponse
     * @param certRefs Cadena de Certificación del certificado de firma
     * @return Documento de firma con formato XADES-C
     * @throws Exception
     */
    private Document addXadesC(Element firma,
    		//String tiempoRespuesta,
    		//String respuestaID,
    		ArrayList<RespYCerts> respuestas,
    		XAdESSchemas schema,
    		String algDigestXML, boolean zain)
    throws AddXadesException
    {
    	Document doc = firma.getOwnerDocument();
    	// Recogemos el nodo UnsignedSignatureProperties del cual dependen los nodos
    	// que hay que añadir para completar la firma XADES-C
    	Element elementoPrincipal = null ;
    	ArrayList<X509Certificate> certRefs = null;

		String tipoUri = null;
		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) {
			tipoUri = ConstantesXADES.URI_MINUS;
		} else {
			tipoUri = ConstantesXADES.URI_MAYUS;
		}
    	
    	NodeList nodos = firma.getElementsByTagNameNS(xadesSchema, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
    	if(nodos.getLength() != 0)
    	{
    		elementoPrincipal = (Element)nodos.item(0);
        }
        else
        {
            throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_19)) ;
        }
        

        // Aqui vienen las llamadas para los certificados
        Element certificadosElementosFirma =
                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.COMPLETE_CERTIFICATE_REFS);
        Element revocacionesElementoFirma =
            doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.COMPLETE_REVOCATION_REFS);

        // Construye las referencias del certificado
        int size = respuestas.size();
        if (size > 0) {
        	certRefs = new ArrayList<X509Certificate> (size);
        	for(int x=0; x < size; ++x) {
        		certRefs.add((respuestas.get(x)).getCertstatus().getCertificate());
        	}
        }
        
        if(certRefs != null)
        {
        	// Se le agrega una Id única
        	Attr informacionElementoCertRef = doc.createAttributeNS(null, ConstantesXADES.ID);
        	idNodoCertificateRefs = UtilidadTratarNodo.newID(doc, ConstantesXADES.COMPLETE_CERTIFICATE_REFS);
        	informacionElementoCertRef.setValue(idNodoCertificateRefs);
        	certificadosElementosFirma.getAttributes().setNamedItem(informacionElementoCertRef);
        	
        	Element elementoCertRefs =
                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CERT_REFS);

        	certificadosElementosFirma.appendChild(elementoCertRefs);
        	int longitud = certRefs.size();
        	
        	// Se agrega una id al certificado de firma
        	String idNueva = UtilidadTratarNodo.newID(doc, ConstantesXADES.LIBRERIAXADES_CERT_PATH);
        	respuestas.get(0).setIdCertificado(idNueva);
        	       	
        	for (int i=1; i<longitud; i++) // Se salta el primero porque es el certificado firmante
        	{
        		X509Certificate firmaCertificado = (X509Certificate) certRefs.get(i);
        		Element elementCertRef = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CERT);
        		
        		// Creamos los atributos de UnSignedProperties
            	Attr uris = doc.createAttributeNS(null, tipoUri);
				// AppPerfect: Falso positivo. No son expresiones constantes
            	idNueva = UtilidadTratarNodo.newID(doc, ConstantesXADES.LIBRERIAXADES_CERT_PATH);
            	uris.setValue( ConstantesXADES.ALMOHADILLA + idNueva );
            	respuestas.get(i).setIdCertificado(idNueva);
            	NamedNodeMap atributosURI = elementCertRef.getAttributes();
            	atributosURI.setNamedItem(uris);

    	        Element resumenElementoCert = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CERT_DIGEST);

    	        // Creamos el xades:DigestMethod
    	        Element metodoResumenElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_METHOD);

    	        // Creamos los atributos de DigestMethod
    	        Attr propiedadesFirmaAlgoritmo = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
    	        propiedadesFirmaAlgoritmo.setValue(algDigestXML);
    	        NamedNodeMap cualidadesMetodoResumenElemento =
    	                metodoResumenElemento.getAttributes();
    	        cualidadesMetodoResumenElemento.setNamedItem(propiedadesFirmaAlgoritmo);

    	        // Creamos el xades:DigestValue
    	        String resumenCertificado = ConstantesXADES.CADENA_VACIA;
    	        try
    	        {
        			MessageDigest resumenCertificadoTemp = UtilidadFirmaElectronica.getMessageDigest(algDigestXML);
        			if (resumenCertificadoTemp == null)
        	        	throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_16));
    	            byte[] resumenMensajeByte =resumenCertificadoTemp.digest(firmaCertificado.getEncoded());
    	            resumenCertificado = new String(Base64Coder.encode(resumenMensajeByte));
    	        } catch (CertificateEncodingException e) {
    	        	log.error(e);
    	        	throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_23));				
    	        }

    	        Element elementDigestValue =
    	                doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_VALUE);
    	        elementDigestValue.appendChild(
    	                doc.createTextNode(resumenCertificado));

    	        // Creamos el xades:IssuerSerial
    	        Element elementoEmisorSerial =
    	                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ISSUER_SERIAL);
    	        // Creamos el xades:X509IssuerName
    	        Element elementoX509EmisorNombre =
    	                doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.X_509_ISSUER_NAME);
    	        elementoX509EmisorNombre.appendChild(
    	                doc.createTextNode(firmaCertificado.getIssuerX500Principal().getName()));

    	        // Creamos el xades:X509SerialNumber
    	        Element elementoX509NumeroSerial =
    	                doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.X_509_SERIAL_NUMBER);
    	        elementoX509NumeroSerial.appendChild(
    	                doc.createTextNode(firmaCertificado.getSerialNumber().toString()));

    	        //Add references
    	        elementoEmisorSerial.appendChild(elementoX509EmisorNombre);
    	        elementoEmisorSerial.appendChild(elementoX509NumeroSerial);

    	        resumenElementoCert.appendChild(metodoResumenElemento);
    	        resumenElementoCert.appendChild(elementDigestValue);

    	        elementCertRef.appendChild(resumenElementoCert);
    	        elementCertRef.appendChild(elementoEmisorSerial);

    	        elementoCertRefs.appendChild(elementCertRef);
        	}
        }

    	
    	
    	Element elementOCSPRef = null;
    	String tiempoRespuesta = null;
    	byte[] mensajeRespuesta = null;
    	
        if (size > 0) {
        	
        	// Se le agrega una Id única
        	Attr informacionElementoCertRef = doc.createAttributeNS(null, ConstantesXADES.ID);
        	idNodoRevocationRefs = UtilidadTratarNodo.newID(doc, ConstantesXADES.COMPLETE_REVOCATION_REFS);
        	informacionElementoCertRef.setValue(idNodoRevocationRefs);
        	revocacionesElementoFirma.getAttributes().setNamedItem(informacionElementoCertRef);
        	
        	int nOCSPRefs = 0;
        	int nCRLRefs = 0;
        	
            // Construye el valor de la respuesta del servidor OCSP
            // bajo el nodo completo de la referencia de la revocación
        	Element elementOCSPRefs =
                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.OCSP_REFS);
        	CRLRefs elementCRLRefs = new CRLRefs(schema); 
        	

	        // Modificacion XADES-XL (compatibilidad ZAIN)
//			codigo original        	
//        	for(int x = 0; x < size - 1; ++x) {
        	
        	// validacion ZAIN
        	int sizeResp = size; 
        	if (!zain) {
        		sizeResp--;
        	}

        	for (int x = 0; x < sizeResp; ++x) {
        	
        		RespYCerts respYCert = respuestas.get(x);
        		ICertStatus certStatus = respYCert.getCertstatus();
        		if (certStatus instanceof IOCSPCertStatus) {
        			nOCSPRefs++;
        			IOCSPCertStatus respOcsp = (IOCSPCertStatus) certStatus;

	        		tiempoRespuesta = UtilidadFechas.formatFechaXML(respOcsp.getResponseDate());
	        		IOCSPCertStatus.TYPE_RESPONDER tipoResponder = respOcsp.getResponderType();
	        		String valorResponder = respOcsp.getResponderID();
	        		mensajeRespuesta = respOcsp.getEncoded();
	
	        		elementOCSPRef = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.OCSP_REF);
	        		
	        		// Creamos los atributos de UnSignedProperties
	        		String idNueva = UtilidadTratarNodo.newID(doc, ConstantesXADES.OCSP);
	        		respYCert.setIdRespStatus(idNueva);
	
	            	Element identificadorElementoOCSP = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.OCSP_IDENTIFIER);
	            	Attr uris = doc.createAttributeNS(null, tipoUri);
	            	uris.setValue( ConstantesXADES.ALMOHADILLA + idNueva );
	            	NamedNodeMap atributosURI = identificadorElementoOCSP.getAttributes();
	            	atributosURI.setNamedItem(uris);
	
	        		// Creamos el xades:DigestMethod
	        		Element elementoRespondedorId = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.RESPONDER_ID);
	
	        		
	        		Element responderFinal = elementoRespondedorId;
	        		if (!(ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) && !(ConstantesXADES.SCHEMA_XADES_122.equals(xadesSchema))) {
	        			Element hijo = null;
	        			if (tipoResponder.equals(IOCSPCertStatus.TYPE_RESPONDER.BY_NAME)) {
	        				hijo = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.BY_NAME);
	        			}
	        			else {
	        				hijo = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.BY_KEY);
	        			}
	        			// TODO: tener en cuenta que podria no ser ninguno de estos valores en un futuro
	            		elementoRespondedorId.appendChild(hijo);
	            		responderFinal = hijo;
	        		}
	        		responderFinal.appendChild(doc.createTextNode(valorResponder));
	    			
	
	        		Element elementoProdujoEn = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.PRODUCE_AT);
	
	
	        		elementoProdujoEn.appendChild(doc.createTextNode(tiempoRespuesta));
	
	        		identificadorElementoOCSP.appendChild(elementoRespondedorId);
	        		identificadorElementoOCSP.appendChild(elementoProdujoEn);
	        		Element valorYResumenElemento = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_ALG_AND_VALUE);
	
	        		// Creamos el xades:DigestMethod
	        		Element metodoResumenElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_METHOD);
	
	        		// Creamos los atributos de DigestMethod
	        		Attr propiedadesAlgoritmoFirmado = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
	        		propiedadesAlgoritmoFirmado.setValue(algDigestXML);
	        		NamedNodeMap atributosMetodoResumenElemento = metodoResumenElemento.getAttributes();
	        		atributosMetodoResumenElemento.setNamedItem(propiedadesAlgoritmoFirmado);
	
	        		// Creamos el xades:DigestValue
	        		// El mensaje de la respuesta es el OCSPResponse
	        		String digestCertificado =ConstantesXADES.CADENA_VACIA;
        			MessageDigest resumenCertificadoTemp = UtilidadFirmaElectronica.getMessageDigest(algDigestXML);
        			if (resumenCertificadoTemp == null)
        				throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_20));
        			byte[] resumenMensajeByte = resumenCertificadoTemp.digest(mensajeRespuesta);
        			digestCertificado = new String(Base64Coder.encode(resumenMensajeByte));
	
	        		Element valorResumenElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.DIGEST_VALUE);
	
	        		valorResumenElemento.appendChild(doc.createTextNode(digestCertificado));
	
	        		valorYResumenElemento.appendChild(metodoResumenElemento);
	        		valorYResumenElemento.appendChild(valorResumenElemento);
	
	        		elementOCSPRef.appendChild(identificadorElementoOCSP);
	        		elementOCSPRef.appendChild(valorYResumenElemento);
	
	        		elementOCSPRefs.appendChild(elementOCSPRef);
        		}
        		else if (certStatus instanceof IX509CRLCertStatus) {
        			nCRLRefs++;
        			IX509CRLCertStatus respCRL = (IX509CRLCertStatus) certStatus;
        			try {
						CRLRef crlRef = new CRLRef(schema, algDigestXML, respCRL.getX509CRL());

						String idNueva = UtilidadTratarNodo.newID(doc, ConstantesXADES.CRL);
						crlRef.getCrlIdentifier().setUri(ConstantesXADES.ALMOHADILLA + idNueva);
						respYCert.setIdRespStatus(idNueva);
		
						elementCRLRefs.addCRLRef(crlRef);
        			} catch (InvalidInfoNodeException ex) {
		    			throw new AddXadesException("No se pudo construir las referencias a CRLs", ex);
					}
        		}
        	}

        	if (nCRLRefs > 0) {
        		try {
					Element el = elementCRLRefs.createElement(doc, xmldsigNS, xadesNS);
					revocacionesElementoFirma.appendChild(el);
				} catch (InvalidInfoNodeException ex) {
	    			throw new AddXadesException("No se pudo construir las referencias a CRLs", ex);
				}
        	}
        	
        	if (nOCSPRefs > 0)
            	revocacionesElementoFirma.appendChild(elementOCSPRefs);
        	
        }
        
        elementoPrincipal.appendChild(certificadosElementosFirma);
        elementoPrincipal.appendChild(revocacionesElementoFirma);

        return doc;
    }

    /**
     * Este metodo añade la implementación del sello de tiempo de tipo 1 (implícito) para 
     * XADES-X según los esquemas 1.2.2 y 1.3.2.
     * Los elementos sobre los que se calcula el sello son los siguientes:
	 * 		- SignatureValue
	 * 		- SignatureTimestamp
	 * 		- CompleteCertificateRefs
	 * 		- CompleteRevocationRefs
	 * 	Opcionalmente en el esquema 1.2.2 y 1.3.2:
	 * 		- AttributeCertificateRefs
	 * 		- AttributeRevocationRefs
	 * 
     * @param Element UnsignedSignatureProperties Nodo a partir del cual se añade el nodo SigAndRefsTimeStamp
     * @return Documento de firma con formato XADES-X
     * @throws AddXadesException En caso de error
     */
    private Document addXadesX(Element UnsignedSignatureProperties)
    	throws AddXadesException {
    	
    	// Se obtiene el formato de la constante URI en función del esquema
		String tipoUri = null;
		String nombreNodoUri = null;
		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)){
			nombreNodoUri = ConstantesXADES.HASH_DATA_INFO;
			tipoUri = ConstantesXADES.URI_MINUS;
		} else {
			nombreNodoUri = ConstantesXADES.INCLUDE;
			tipoUri = ConstantesXADES.URI_MAYUS;
		}
    	
    	// Se obtiene el documento que contiene al nodo UnsignedSignatureProperties
    	Document doc = UnsignedSignatureProperties.getOwnerDocument();
    	
    	// Se obtiene el nodo Signature que contiene al nodo UnsignedSignatureProperties (es el 4º padre, según esquema XAdES)
    	Node padre = UnsignedSignatureProperties.getParentNode();
    	for (int i = 0; i < 3; ++i) {
    		if (padre != null)
    			padre = padre.getParentNode();
    		else
    			// No se encuentra el nodo Signature
    			throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
    					ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
    	}
    	
    	Element signatureElement = null;
    	if (padre != null && ConstantesXADES.SIGNATURE.equals(padre.getLocalName()))
    		signatureElement = (Element)padre;
    	else
    		// No se encuentra el nodo Signature
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
    	 
    	// Se crea el nodo SigAndRefsTimeStamp
        Element sigAndRefsTimeStampElement =
        	doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.SIG_AND_REFS_TIME_STAMP);
        
        // Se escribe una Id única
    	Attr informacionElementoSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String idSelloTiempo = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO);
    	informacionElementoSigTimeStamp.setValue(idSelloTiempo);
    	idNodoSelloTiempo.add(idSelloTiempo);
    	sigAndRefsTimeStampElement.getAttributes().setNamedItem(informacionElementoSigTimeStamp);
        
        // Se coloca el nodo creado al final del nodo UnsignedSignatureProperties
        UnsignedSignatureProperties.appendChild(sigAndRefsTimeStampElement);
        
        // Se obtiene el listado de elementos de un sello de tiempo XAdES X de tipo 1
        ArrayList<Element> elementosSelloX = null;
        try {
			elementosSelloX = UtilidadXadesX.obtenerListadoXADESX1imp(xadesSchema, signatureElement, sigAndRefsTimeStampElement);
		} catch (BadFormedSignatureException e) {
			throw new AddXadesException(e.getMessage(), e);
		} catch (FirmaXMLError e) {
			throw new AddXadesException(e.getMessage(), e);
		}
		
		// Se añaden nodos de referencia a los nodos obtenidos para el cálculo del sello (sólo para esquemas 1.2.2 y 1.1.1)
		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema) ||
				ConstantesXADES.SCHEMA_XADES_122.equals(xadesSchema)) {
			// Se obtienen las Ids de los nodos del sello de tiempo X
			ArrayList<String> elementosIdSelloX = UtilidadTratarNodo.obtenerIDs(elementosSelloX);
			
			// Se crea una estructura con los nodos Include (1.2.2) o HashDataInfo (1.1.1) que contienen las URIs que apuntan a estas IDs
			ArrayList<Element> nodosUriReferencia = new ArrayList<Element> (elementosIdSelloX.size());
			Iterator<String> itIds = elementosIdSelloX.iterator();
			while (itIds.hasNext()) {
				String id = itIds.next();
				Element uriNode = 
					doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + nombreNodoUri);
	        	Attr includeNodeUri = doc.createAttributeNS(null, tipoUri);
	        	includeNodeUri.setValue(ConstantesXADES.ALMOHADILLA + id);
	        	NamedNodeMap atributosNodo = uriNode.getAttributes();
	        	atributosNodo.setNamedItem(includeNodeUri);
	        	
	        	nodosUriReferencia.add(uriNode);
			}
			
	        // Se escribe en el nodo SigAndRefsTimeStamp el listado obtenido por orden
			Iterator<Element> itUrisReferencia = nodosUriReferencia.iterator();
			while (itUrisReferencia.hasNext()) {
				Element includeNode = itUrisReferencia.next();			
				sigAndRefsTimeStampElement.appendChild(includeNode);
			}
		}
		
		// Se obtiene el Array de bytes de los nodos obtenidos
		byte[] byteData = null;
		try {
			byteData = UtilidadTratarNodo.obtenerByte(elementosSelloX, CanonicalizationEnum.C14N_OMIT_COMMENTS);
		} catch (FirmaXMLError e) {
			throw new AddXadesException(e.getMessage(), e);
		}
		
		// Calculamos el hash del sello de tiempo y lo escribimos en el nodo como String del array de bytes calculado
		TSCliente tsCli = null;
//        if(estadoProxy) {
//			System.setProperty("http.proxyHost", servidorProxy);
//			System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
//			if (isProxyAuth) {
//				Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
//			} 
//			else {
//				Authenticator.setDefault(null);
//			}
//        } 
        tsCli = new TSCliente(servidorTSA,algoritmoTSA);
        
        try {
			byteData = tsCli.generarSelloTiempo(byteData);
		} catch (TSClienteError e) {
			throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_11) + e.getMessage()) ;
		}
		String hashSelloX = new String(Base64Coder.encode(byteData));
		
		// Se crea el nodo canonicalizationMethod en los esquemas 1.2.2 y 1.3.2
    	if (!ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) {
    		Element canonicalizationElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CANONICALIZATION_METHOD);		
    		Attr canonicalizationAttribute = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
    		canonicalizationAttribute.setValue(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
    		canonicalizationElemento.getAttributes().setNamedItem(canonicalizationAttribute);

    		sigAndRefsTimeStampElement.appendChild(canonicalizationElemento);
    	}
		
		// Escribimos el resultado en el nodo EncapsulatedTimeStamp
		Element encapsulatedTimeStampNode = 
			doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ENCAPSULATED_TIME_STAMP);
		encapsulatedTimeStampNode.appendChild(doc.createTextNode(hashSelloX));	
		sigAndRefsTimeStampElement.appendChild(encapsulatedTimeStampNode);

        return doc;
    }
    
    /**
     * Este metodo añade la implementación del sello de tiempo de tipo 2 (explícito) para 
     * XADES-X según los esquemas 1.1.1, 1.2.2 y 1.3.2.
     * Los elementos sobre los que se calcula el sello son los siguientes:
	 * 		- CompleteCertificateRefs
	 * 		- CompleteRevocationRefs
	 * 	Opcionalmente en el esquema 1.2.2 y 1.3.2:
	 * 		- AttributeCertificateRefs
	 * 		- AttributeRevocationRefs
	 * 
     * @param Element UnsignedSignatureProperties Nodo a partir del cual se añade el nodo RefsOnlyTimeStamp
     * @return Documento de firma con formato XADES-X
     * @throws AddXadesException En caso de error
     */
    private Document addXadesX2(Element UnsignedSignatureProperties)
    	throws AddXadesException
    	{
    	// Se obtiene el formato de la constante URI en función del esquema
		String tipoUri = null;
		String nombreNodoUri = null;
		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)){
			nombreNodoUri = ConstantesXADES.HASH_DATA_INFO;
			tipoUri = ConstantesXADES.URI_MINUS;
		} else {
			nombreNodoUri = ConstantesXADES.INCLUDE;
			tipoUri = ConstantesXADES.URI_MAYUS;
		}
    	
    	// Se obtiene el documento que contiene al nodo UnsignedSignatureProperties
    	Document doc = UnsignedSignatureProperties.getOwnerDocument();
    	
    	// Se obtiene el nodo Signature que contiene al nodo UnsignedSignatureProperties (es el 4º padre, según esquema XAdES)
    	Node padre = UnsignedSignatureProperties.getParentNode();
    	for (int i = 0; i < 3; ++i) {
    		if (padre != null)
    			padre = padre.getParentNode();
    		else
    			// No se encuentra el nodo Signature
    			throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
    					ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
    	}
    	
    	Element signatureElement = null;
    	if (padre != null && ConstantesXADES.SIGNATURE.equals(padre.getLocalName()))
    		signatureElement = (Element)padre;
    	else
    		// No se encuentra el nodo Signature
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
    				ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
    	 
    	// Se crea el nodo RefsOnlyTimeStamp
        Element refsOnlyTimeStampElement =
        	doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.REFS_ONLY_TIME_STAMP);
        
        // Se escribe una Id única
    	Attr informacionElementoSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String idSelloTiempo = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO);
    	informacionElementoSigTimeStamp.setValue(idSelloTiempo);
    	idNodoSelloTiempo.add(idSelloTiempo);
    	refsOnlyTimeStampElement.getAttributes().setNamedItem(informacionElementoSigTimeStamp);
        
        // Se coloca el nodo creado al final del nodo UnsignedSignatureProperties
        UnsignedSignatureProperties.appendChild(refsOnlyTimeStampElement);
        
        // Se obtiene el listado de elementos de un sello de tiempo XAdES X de tipo 2
        ArrayList<Element> elementosSelloX = null;
        try {
			elementosSelloX = UtilidadXadesX.obtenerListadoXADESX2exp(xadesSchema, signatureElement, refsOnlyTimeStampElement);
		} catch (BadFormedSignatureException e) {
			throw new AddXadesException(e.getMessage(), e);
		} catch (FirmaXMLError e) {
			throw new AddXadesException(e.getMessage(), e);
		}
		
		// Se añaden nodos de referencia a los nodos obtenidos para el cálculo del sello (sólo para esquemas 1.2.2 y 1.1.1)
		if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema) ||
				ConstantesXADES.SCHEMA_XADES_122.equals(xadesSchema)) {
			// Se obtienen las Ids de los nodos del sello de tiempo X
			ArrayList<String> elementosIdSelloX = UtilidadTratarNodo.obtenerIDs(elementosSelloX);
			
			// Se crea una estructura con los nodos Include (1.2.2) o HashDataInfo (1.1.1) que contienen las URIs que apuntan a estas IDs
			ArrayList<Element> nodosUriReferencia = new ArrayList<Element> (elementosIdSelloX.size());
			Iterator<String> itIds = elementosIdSelloX.iterator();
			while (itIds.hasNext()) {
				String id = itIds.next();
				Element uriNode = 
					doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + nombreNodoUri);
	        	Attr includeNodeUri = doc.createAttributeNS(null, tipoUri);
	        	includeNodeUri.setValue(ConstantesXADES.ALMOHADILLA + id);
	        	NamedNodeMap atributosNodo = uriNode.getAttributes();
	        	atributosNodo.setNamedItem(includeNodeUri);
	        	
	        	nodosUriReferencia.add(uriNode);
			}
			
	        // Se escribe en el nodo RefsOnlyTimeStamp el listado obtenido, por orden
			Iterator<Element> itUrisReferencia = nodosUriReferencia.iterator();
			while (itUrisReferencia.hasNext()) {
				Element includeNode = itUrisReferencia.next();			
				refsOnlyTimeStampElement.appendChild(includeNode);
			}
		}
		
		// Se obtiene el Array de bytes de los nodos obtenidos
		byte[] byteData = null;
		try {
			byteData = UtilidadTratarNodo.obtenerByte(elementosSelloX, CanonicalizationEnum.C14N_OMIT_COMMENTS);
		} catch (FirmaXMLError e) {
			throw new AddXadesException(e.getMessage(), e);
		}
		
		// Calculamos el hash del sello de tiempo y lo escribimos en el nodo como String del array de bytes calculado
		TSCliente tsCli = null;
//        if(estadoProxy) {
//			System.setProperty("http.proxyHost", servidorProxy);
//			System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
//			if (isProxyAuth) {
//				Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
//			} 
//			else {
//				Authenticator.setDefault(null);
//			}
//        } 
        tsCli = new TSCliente(servidorTSA,algoritmoTSA);
        
        try {
			byteData = tsCli.generarSelloTiempo(byteData);
		} catch (TSClienteError e) {
			throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_11) + e.getMessage()) ;
		}
		String hashSelloX = new String(Base64Coder.encode(byteData));
		
		// Se crea el nodo canonicalizationMethod en los esquemas 1.2.2 y 1.3.2
    	if (!ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) {
    		Element canonicalizationElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CANONICALIZATION_METHOD);		
    		Attr canonicalizationAttribute = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
    		canonicalizationAttribute.setValue(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
    		canonicalizationElemento.getAttributes().setNamedItem(canonicalizationAttribute);

    		refsOnlyTimeStampElement.appendChild(canonicalizationElemento);
    	}
		
		// Escribimos el resultado en el nodo EncapsulatedTimeStamp
		Element encapsulatedTimeStampNode = 
			doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ENCAPSULATED_TIME_STAMP);
		encapsulatedTimeStampNode.appendChild(doc.createTextNode(hashSelloX));	
		refsOnlyTimeStampElement.appendChild(encapsulatedTimeStampNode);

        return doc;
    }

    /**
     * Este metodo añade la implementacion para XADES-XL
     * @param doc Documento de firma con formato XADES-X
     * @param valorCertificado
     * @param valorRevocacion
     * @return Documento de firma con formato XADES-XL
     * @throws Exception
     */
    private Document addXadesXL(Element firma, ArrayList<RespYCerts> respuestas, XAdESSchemas schema, boolean zain)
    	throws AddXadesException
    	{
    	// Recogemos el nodo UnsignedSignatureProperties del cual dependen los nodos
    	// que hay que añadir para completar la firma XADES-XL
    	Document doc = firma.getOwnerDocument();
        Element elementoPrincipal = null ;

        NodeList nodosUnsignedSignatureProperties = firma.getElementsByTagNameNS(schema.getSchemaUri(), ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
        if(nodosUnsignedSignatureProperties.getLength() != 0)
            elementoPrincipal = (Element)nodosUnsignedSignatureProperties.item(0);
        else
        	// No se encuentra el nodo UnsignedSignatureProperties
            throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_19));
        	
        // Se añaden los certificados referenciados en el nodo CertificateValues
        if(respuestas != null) {
        	EncapsulatedX509Certificate encapsulatedX509certificate = null;
        	ArrayList<EncapsulatedX509Certificate> certs = new ArrayList<EncapsulatedX509Certificate> ();       	
	        Iterator<RespYCerts> itResp = respuestas.iterator();
	        boolean hasNext = itResp.hasNext();
	        while (hasNext) {
        		RespYCerts resp = itResp.next();
        		hasNext = itResp.hasNext();
        		encapsulatedX509certificate = new EncapsulatedX509Certificate(schema, resp.getIdCertificado());
	        	try {
					encapsulatedX509certificate.setX509Certificate(resp.getCertstatus().getCertificate());
				} catch (CertificateException e) {
					log.error(e.getMessage(), e);
					throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_23));
				}
	        	certs.add(encapsulatedX509certificate);
	        }

	        CertificateValues certificateValues = new CertificateValues(schema, certs);
	        Element certificateValuesElement = null;
	        try {
	        	certificateValuesElement = certificateValues.createElement(doc, xadesNS);
			} catch (InvalidInfoNodeException e) {
				log.error(e.getMessage(), e);				
				throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_23));
			}  
			
			// Se escribe una Id única
	    	Attr atributoCertVal = doc.createAttributeNS(null, ConstantesXADES.ID);
	    	String idCertVal = UtilidadTratarNodo.newID(doc, ConstantesXADES.CERTIFICATE_VALUES);
	    	atributoCertVal.setValue(idCertVal);
	    	certificateValuesElement.getAttributes().setNamedItem(atributoCertVal);
			
			elementoPrincipal.appendChild(certificateValuesElement);
			
            // Se añade la respuesta del servidor OCSP			
            Element valoresElementosRevocados =
                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.REVOCATION_VALUES);

            Element valorElementOCSP =
                doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.OCSP_VALUES);
            
            CRLValues valorElementoCRL = new CRLValues(schema);

	        int nOcspResps = 0;
	        int nCRLSResps = 0;
	        
	        // Modificacion XADES-XL (compatibilidad ZAIN)
            int sizeResp = respuestas.size();
            
            if (!zain) {
            	sizeResp--;
            }

            // TODO revisar que el orden sigue siendo el mismo que con la implementacion anterior.
        	for (int i = 0; i < sizeResp; i++) {

        		RespYCerts resp = respuestas.get(i);

        		ICertStatus respStatus = resp.getCertstatus();
        		if (respStatus instanceof IOCSPCertStatus) {
        			nOcspResps++;
        			IOCSPCertStatus respOCSP = (IOCSPCertStatus) respStatus;
    	            Element valorElementoEncapsuladoOCSP = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ENCAPSULATED_OCSP_VALUE);
    	            valorElementoEncapsuladoOCSP.appendChild(
    	                    doc.createTextNode(new String(Base64Coder.encode(respOCSP.getEncoded()))));
    	            valorElementoEncapsuladoOCSP.setAttributeNS(null, ConstantesXADES.ID, resp.getIdRespStatus());
    	            valorElementOCSP.appendChild(valorElementoEncapsuladoOCSP);
        		}
        		else if (respStatus instanceof IX509CRLCertStatus) {
        			nCRLSResps++;
        			IX509CRLCertStatus respCRL = (IX509CRLCertStatus) respStatus;
        			try {
        				valorElementoCRL.addCRL(respCRL.getX509CRL(), resp.getIdRespStatus());
					} catch (InvalidInfoNodeException ex) {
						throw new AddXadesException("No se pudo generar nodo EncapsulatedCRLValue", ex);
					}
        		}
	        }

            if (nCRLSResps > 0) {
            	try {
					Element el = valorElementoCRL.createElement(doc, xadesNS);
					valoresElementosRevocados.appendChild(el);
				} catch (InvalidInfoNodeException ex) {
					throw new AddXadesException("No se pudo generar nodo CRLValues", ex);
				}
            }

            if (nOcspResps > 0)
            	valoresElementosRevocados.appendChild(valorElementOCSP);
            
			// Se escribe una Id única
	    	Attr atributoRevVal = doc.createAttributeNS(null, ConstantesXADES.ID);
	    	String idRevVal = UtilidadTratarNodo.newID(doc, ConstantesXADES.REVOCATION_VALUES);
	    	atributoRevVal.setValue(idRevVal);
	    	valoresElementosRevocados.getAttributes().setNamedItem(atributoRevVal);
            
            elementoPrincipal.appendChild(valoresElementosRevocados);
        }

        return doc;
    }
    
    private Document addXadesA (Element firma, byte[] selloTiempo, ArrayList<String> inc) throws Exception {
    	
    	Document doc = firma.getOwnerDocument();
    	
    	ArrayList<Element> UnsignedSignaturePropertiesNodes = UtilidadTratarNodo.obtenerNodos(firma, 4, 
				new NombreNodo(xadesSchema, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES));
    	Element UnsignedSignaturePropertiesNode = null;
    	if(UnsignedSignaturePropertiesNodes.size() == 1)
    		UnsignedSignaturePropertiesNode = (Element)UnsignedSignaturePropertiesNodes.get(0);
    	else
    		// No se encuentra el nodo UnsignedSignatureProperties
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_19)) ;

    	Element archiveTimeStamp =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.ARCHIVE_TIME_STAMP);
    	
    	// Creamos los atributos de ArchiveTimeStamp (Id)
    	Attr archiveTimeStampId = doc.createAttributeNS(null, ConstantesXADES.ID);
    	archiveTimeStampId.setValue(UtilidadTratarNodo.newID(doc, 
    			ConstantesXADES.ARCHIVE_TIME_STAMP + ConstantesXADES.GUION));
    	NamedNodeMap archiveTimeStampAttributesElement =
    		archiveTimeStamp.getAttributes();
    	archiveTimeStampAttributesElement.setNamedItem(archiveTimeStampId);
    	
    	// Se agrega el nodo EncapsulatedTimeStamp, con Id y Encoding como atributos
    	Element encapsulatedTimeStamp =
    		doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + 
    				ConstantesXADES.ENCAPSULATED_TIME_STAMP);
    	
    	// Se escribe una Id única
    	Attr informacionElementoSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String idSelloTiempo = UtilidadTratarNodo.newID(doc, ConstantesXADES.SELLO_TIEMPO_TOKEN);
    	informacionElementoSigTimeStamp.setValue(idSelloTiempo);
    	idNodoSelloTiempo.add(idSelloTiempo);
    	encapsulatedTimeStamp.getAttributes().setNamedItem(informacionElementoSigTimeStamp);
    	
    	// Se agrega el attributo Encoding
//    	Attr idSigTimeStamp = doc.createAttributeNS(null, ConstantesXADES.XADES_TAG_ENCODING);
//    	String encoding = EncodingEnum.DER_ENCODED;
//    	idSigTimeStamp.setValue(encoding);
//    	encapsulatedTimeStamp.getAttributes().setNamedItem(idSigTimeStamp);
    	
    	// Se agrega el CanonicalizationMethod
    	Element canonicalizationElemento = doc.createElementNS(ConstantesXADES.SCHEMA_DSIG, xmldsigNS + ConstantesXADES.DOS_PUNTOS + ConstantesXADES.CANONICALIZATION_METHOD);		
		Attr canonicalizationAttribute = doc.createAttributeNS(null, ConstantesXADES.ALGORITHM);
		canonicalizationAttribute.setValue(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
		canonicalizationElemento.getAttributes().setNamedItem(canonicalizationAttribute);

		archiveTimeStamp.appendChild(canonicalizationElemento);
        
        encapsulatedTimeStamp.appendChild(doc.createTextNode(new String(Base64Coder.encode(selloTiempo))));       
        
        // Se agregan, si existen, los nodos include
        if (inc != null) {
        	Element includeNode = null;
        	for (int i = 0; i < inc.size(); ++i) {
        		includeNode = doc.createElementNS(xadesSchema, xadesNS + ConstantesXADES.DOS_PUNTOS + 
        				ConstantesXADES.INCLUDE);      	
        		includeNode.setAttributeNS(null, ConstantesXADES.URI_MAYUS, inc.get(i));
        		archiveTimeStamp.appendChild(includeNode);
        	}
        }
        
        archiveTimeStamp.appendChild(encapsulatedTimeStamp);
        
        // Se agrega el sello creado a las propiedades no firmadas
        UnsignedSignaturePropertiesNode.appendChild(archiveTimeStamp);
        
    	return doc;
    }

    /**
     * Contrafirma una firma según esquema XAdES
     * 
     * @param firmaCertificado
     * @param xml
     * @param storeManager
     * @param nodoAFirmarId
     * @param esquemaOrigen
     * @param destino
     * @param nombreArchivo
     * @return
     * @throws Exception
     */
    public void countersignFile(X509Certificate firmaCertificado,
            DataToSign xml, IPKStoreManager storeManager, 
            String nodoAFirmarId, String destino, String nombreArchivo, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {
    	
    	PrivateKey pk = storeManager.getPrivateKey(firmaCertificado);
    	Document doc = countersign(firmaCertificado, xml, nodoAFirmarId, pk, storeManager.getProvider(firmaCertificado), truster, tsaOcspUrl, zain);

    	// Se guarda la firma en su destino
    	File fichero = new File(destino + nombreArchivo); 
    	FileOutputStream f2 = new FileOutputStream(fichero);
        
    	try {
    		XMLUtils.outputDOM(doc, f2,true);
    	} catch (Throwable t) {
    		if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
    			throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
    		else
    			throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
    	} finally {
    		f2.close(); 
    	}
    }

    public void countersign2Stream(X509Certificate firmaCertificado,
            DataToSign xml, IPKStoreManager storeManager,
            String nodoAFirmarId, OutputStream salida, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {
    	
    	PrivateKey pk = storeManager.getPrivateKey(firmaCertificado);
    	Document doc = countersign(firmaCertificado, xml, nodoAFirmarId, pk, storeManager.getProvider(firmaCertificado), truster, tsaOcspUrl, zain);

    	// Se guarda la firma en su destino
    	try {
    		XMLUtils.outputDOM(doc, salida,true);
    	} catch (Throwable t) {
    		if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
    			throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
    		else
    			throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
    	}
    }

    /**
     * Contrafirma una firma según esquema XAdES
     * 
     * @param pk .- Clave privada del certificado
     * @param certificadoFirma .- Certificado de firma
     * @param xml .- Contenido a firmar
     * @param destino .- Ruta donde guardar la firma generada
     * @param nombreArchivo .- Nombre del archivo que guarda la firma generada
     */
    private Document countersign(X509Certificate certificadoFirma,
    		DataToSign xml, String nodoAFirmarId, PrivateKey pk,
    		Provider provider, TrustAbstract truster, String tsaOcspUrl, boolean zain) throws Exception {

    	Security.addProvider(new BouncyCastleProvider());
    	Document doc = xml.getDocument();
    	if (doc == null) {
	        try {
	        	InputStream is = xml.getInputStream();
	        	if (is != null) {
	            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            	dbf.setNamespaceAware(true);
	            	DocumentBuilder db = dbf.newDocumentBuilder();
	                db.setErrorHandler(new IgnoreAllErrorHandler());
		            InputSource isour = new InputSource(is);
		            String encoding = xml.getXMLEncoding();
		            isour.setEncoding(encoding);
		            doc = db.parse(isour);
	        	}
	        } catch (IOException ex) {
	        	throw new Exception(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_50));
	        }
    	}
    	
    	// Si no se indica nodo a contrafirmar se contrafirma la última firma disponible
    	Node nodePadreNodoFirmar = null;
    	if (nodoAFirmarId != null) {
        	Element nodoAFirmar = UtilidadTratarNodo.getElementById(doc, nodoAFirmarId);
        	if(nodoAFirmar == null) {
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
        				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
        	}
        	
        	// Se indique el signatureValue o el signature se obtiene el mismo padre
        	if (ConstantesXADES.SIGNATURE_VALUE.equals(nodoAFirmar.getLocalName())) {
        		idSignatureValue = nodoAFirmarId;
        		nodePadreNodoFirmar = nodoAFirmar.getParentNode();
        	} else if (ConstantesXADES.SIGNATURE.equals(nodoAFirmar.getLocalName())) {
        		nodePadreNodoFirmar = nodoAFirmar;
        	} else {
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
        				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
        	}
    	} else {
    		// Busca la última firma
    		NodeList list = doc.getElementsByTagNameNS(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE);
    		if (list.getLength() < 1) {
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
        				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
    		} else {
    			nodePadreNodoFirmar = list.item(list.getLength() - 1);
    		}
    	}
    	String idSignatureValue = null;
    	Element padreNodoFirmar = null;
    	if ((nodePadreNodoFirmar != null) && (nodePadreNodoFirmar.getNodeType() == Node.ELEMENT_NODE)) {
    		padreNodoFirmar = (Element)nodePadreNodoFirmar;
        	ArrayList<Element> listElements = UtilidadTratarNodo.obtenerNodos(padreNodoFirmar, 2, new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE));
        	if (listElements.size() != 1) {
        		// TODO: indicar un error específico (No se puede tener más de un nodo SignatureValue por firma XmlDSig)
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
        				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
        	}
        	idSignatureValue = listElements.get(0).getAttribute(ConstantesXADES.ID);
        	// TODO: Si este nodo no tiene id, identificarlo vía XPATH
        	if (idSignatureValue == null) {
        		// TODO: indicar un error específico (No se puede identificar nodo SignatureValue en firma XmlDSig)
        		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
        				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
        	}
    	}

    	// Se busca si existe el path hasta el nodo raíz CounterSignature. Si no existe, se crea.
    	ArrayList<Element> listElements = UtilidadTratarNodo.obtenerNodos(padreNodoFirmar, 2, ConstantesXADES.QUALIFYING_PROPERTIES);
    	if (listElements.size() != 1) {
    		// TODO: indicar un error específico (No se puede tener más de un nodo Qualifying por firma XAdES 
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) 
    				+ ConstantesXADES.ESPACIO + nodoAFirmarId);
    		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_51));
    	}
    	String esquemaOrigen = listElements.get(0).getNamespaceURI();
    	NodeList nodosUnsigSigProp = (padreNodoFirmar).getElementsByTagNameNS(esquemaOrigen, 
    			ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
    	
    	Element nodoRaiz = null;
    	if (nodosUnsigSigProp != null && nodosUnsigSigProp.getLength() != 0)
    		nodoRaiz = (Element)nodosUnsigSigProp.item(0); // Se toma el primero de la lista
    	else { // Se busca el nodo QualifyingProperties
    		NodeList nodosQualifying = (padreNodoFirmar).getElementsByTagNameNS(esquemaOrigen, ConstantesXADES.QUALIFYING_PROPERTIES);
        	
        	if (nodosQualifying != null && nodosQualifying.getLength() != 0) {
        		Element nodoQualifying = (Element)nodosQualifying.item(0);
        		Element unsignedProperties = null;
        		if (nodoQualifying.getPrefix() != null) {
        			unsignedProperties =
        				doc.createElementNS(esquemaOrigen, nodoQualifying.getPrefix() +
        						ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_PROPERTIES);
        			nodoRaiz = doc.createElementNS(esquemaOrigen, nodoQualifying.getPrefix() +
        					ConstantesXADES.DOS_PUNTOS + ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
        		} else {
        			unsignedProperties =
        				doc.createElementNS(esquemaOrigen, ConstantesXADES.UNSIGNED_PROPERTIES);
        			nodoRaiz = doc.createElementNS(esquemaOrigen, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
        		}
        		
        		unsignedProperties.appendChild(nodoRaiz);
        		nodosQualifying.item(0).appendChild(unsignedProperties);       		
        	} else
        		throw new AddXadesException(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_52));
    	}
    		
    	// Se genera un nuevo nodo Countersignature donde irá la firma
    	Element counterSignature = null;
		if (nodoRaiz.getPrefix() != null) {
			counterSignature = doc.createElementNS(esquemaOrigen, nodoRaiz.getPrefix() +
				ConstantesXADES.DOS_PUNTOS + ConstantesXADES.COUNTER_SIGNATURE);
		} else { 
			counterSignature = doc.createElementNS(esquemaOrigen, ConstantesXADES.COUNTER_SIGNATURE);
		}
		nodoRaiz.appendChild(counterSignature);

    	
    	// Se escribe una Id única
    	Attr counterSignatureAttrib = doc.createAttributeNS(null, ConstantesXADES.ID);
    	String counterSignatureId = UtilidadTratarNodo.newID(doc, ConstantesXADES.COUNTER_SIGNATURE + ConstantesXADES.GUION);
    	counterSignatureAttrib.setValue(counterSignatureId);
    	counterSignature.getAttributes().setNamedItem(counterSignatureAttrib);
    	
    	// Se reemplaza el documento original por el documento preparado para contrafirma
    	xml.setDocument(doc);
    	
        // Se incluye la referencia a la contrafirma
        AbstractObjectToSign obj = null;
		if (XAdESSchemas.XAdES_132.getSchemaUri().equals(xadesSchema)) {
			obj = new SignObjectToSign(idSignatureValue);
		} else {
			obj = new InternObjectToSign(idSignatureValue);
		}
        xml.addObject(new ObjectToSign(obj, null, null, null, null));		


        // Se firma el documento generado, indicando el nodo padre y el identificador del nodo a firmar
        xml.setParentSignNode(counterSignatureId);
    	Object[] res = signFile(certificadoFirma, xml, pk, provider, truster, tsaOcspUrl, zain);
		

    	doc = (Document) res[0];
    	
    	// Se elimina el identificador del nodo CounterSignature
    	counterSignature = UtilidadTratarNodo.getElementById(doc, counterSignatureId);
    	counterSignature.removeAttribute(ConstantesXADES.ID);
    	
    	return doc;
    }

    /**
     *   Sube el nivel XAdES de un InputStream de firma y lo guarda en la dirección indicada
     *   
	 *   @param InputStream Stream que contiene la firma
     *   @param EnumFormatoFirma nivel de firma deseado
     *   @param String Ruta bajo la que se guarda el fichero generado
     *   @param String Nombre bajo el que se guarda el fichero generado
     */
//    public boolean subirNivel(BufferedInputStream firma, EnumFormatoFirma nivelDeseado, 
//    		String path, String nombreArchivo) throws ClienteError{
//		File archivo = null;
//		FileOutputStream fout = null;
//		
//		try {
//			archivo = File.createTempFile(ConstantesXADES.PROCESO_FIRMA, ConstantesXADES.PUNTO_TMP);
//			archivo.deleteOnExit();
//
//			byte[] buffer= new byte[firma.available()];
//			fout = new FileOutputStream(archivo);
//			int firmaDisponible = firma.available();
//			while (firmaDisponible > 0) {
//				int byteLeido = firma.read(buffer);
//				if(byteLeido == -1) break;
//				fout.write(byteLeido);
//				firmaDisponible = firma.available();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new ClienteError(e.getMessage());
//		} finally {
//			try {
//				fout.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				throw new ClienteError(e.getMessage());
//			}
//		}
//		AnalizadorFicheroFirma aff = new AnalizadorFicheroFirma();
//    	aff.analizar(archivo);
//
//        return subirNivel(archivo, nivelDeseado, path, nombreArchivo);
//    }
    
    /**
     *   Sube el nivel XAdES de un archivo de firma y lo sobreescribe
     *   
	 *   @param File archivo que contiene la firma
     *   @param EnumFormatoFirma nivel de firma deseado
     */
//    public boolean subirNivel(File firma, EnumFormatoFirma nivelDeseado) throws Exception{
//		AnalizadorFicheroFirma aff = new AnalizadorFicheroFirma();
//    	aff.analizar(firma);
//        return subirNivel(firma, nivelDeseado, firma.getParent(), firma.getName());
//    }
      
    /**
     *   Sube el nivel XAdES de una firma (No esta preparado para firma múltiple)
     *
     *	 @param InputStream Stream que contiene la firma
     *   @param EnumFormatoFirma Nivel de firma deseado
     *   @param String Ruta bajo la que se guarda el fichero generado
     *   @param String Nombre bajo el que se guarda el fichero generado
     */
//    public boolean subirNivel(File firma, EnumFormatoFirma nivelDeseado, 
//    		String path, String nombreArchivo) throws ClienteError {
//    	
//    	RespuestaOCSP respuesta = null;
//    	ArrayList<RespYCerts> respuestas = new ArrayList<RespYCerts>();
//    	
//    	if (path == null || nombreArchivo == null || nivelDeseado == null || firma == null) {
//			// No se proporcionaron los datos de firma
//			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_31));
//		}
//    	
//    	// Generamos un doc temporal a partir de la lista de nodos de la firma a subir de nivel
//    	
//    	Init.init() ;
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setNamespaceAware(true);
//        DocumentBuilder db = null;
//		try {
//			db = dbf.newDocumentBuilder();
//		} catch (ParserConfigurationException e1) {
//			throw new ClienteError(e1.getMessage());
//		}
//        db.setErrorHandler(new IgnoreAllErrorHandler());
//
//    	FileInputStream fis = null;
//    	
//    	AnalizadorFicheroFirma aff = new AnalizadorFicheroFirma();
//    	aff.analizar(firma);
//    	
//    	try {
//    		fis = new FileInputStream(firma);
//    	} catch(FileNotFoundException e) {
//    		log.error(e.getMessage(), e);
//    	} 
//    	
//        InputSource isour = new InputSource(fis);
//        String encoding = configuracion.getValor(ConstantesXADES.ENCODING_XML) ;
//        isour.setEncoding(encoding);
//        Document doc;
//		try {
//			doc = db.parse(isour);
//		} catch (SAXException e1) {
//			log.error(e1.getMessage(), e1);
//			throw new ClienteError(e1.getMessage());
//		} catch (IOException e1) {
//			log.error(e1.getMessage(), e1);
//			throw new ClienteError(e1.getMessage());
//		} finally {  	
//			try {
//				if (fis != null)
//					fis.close();
//			} catch (IOException e1) {
//				log.error(e1.getMessage(), e1);
//			}
//		}
//		
//		// Se obtienen las firmas contenidas en el documento
//		
//		NodeList listaFirmas = doc.getElementsByTagNameNS(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.LIBRERIAXADES_SIGNATURE);
//		int listaFirmasLength = listaFirmas.getLength();
//		if (listaFirmasLength == 0)
//		{
//			log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_VALIDARFIRMA_ERROR2));
//			// Error en la validación. No se pudo encontrar el nodo de firma
//			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_VALIDARFIRMA_ERROR2));
//		}
//
//		// Si el documento tiene múltiples firmas existe más de un nodo de firma
//		if (listaFirmasLength > 1) {
//			// No se puede subir el nivel de firma. Firmas presentes en el documento:
//			log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_49) + 
//					ConstantesXADES.ESPACIO +  listaFirmasLength);
//			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_49) + 
//					ConstantesXADES.ESPACIO +  listaFirmasLength);
//		}
//		
//		// Se recupera el nodo Signature de la firma
//		Node nodoFirma = listaFirmas.item(0);
//		   	        
//	    // Validamos el documento XADES. Como mínimo será XAdES-BES.
//		
//		ResultadoValidacion resultado = null;
//	    ValidarFirmaXML validacion = null;
//		
//    	try {
//    		validacion = new ValidarFirmaXML(configuracion);
//    		resultado = validacion.validar(firma, null).get(0);
//    	} catch (Exception e) {
//    		log.error(e.getMessage(), e);
//    		return false;
//    	}
//	    
//	    if (resultado.isValidate() && resultado.getDatosFirma() != null && 
//	    		resultado.getDatosFirma().getCadenaFirma() != null &&   
//        		!resultado.getDatosFirma().getCadenaFirma().getCertificates().isEmpty()) 
//	    { // Se continúa sólo si el documento de firma es válido y hay datos del certificado de firma
//
//	    	// Obtenemos los niveles a subir
//	    	ArrayList<EnumFormatoFirma> mejora = new ArrayList<EnumFormatoFirma>();
//	    	EnumFormatoFirma nivel = resultado.getEnumNivel();
//	    	while (true) {
//	    		if ((nivelDeseado).equals(nivel)) {
//	    			break;
//	    		} else {
//	    			if (EnumFormatoFirma.XAdES_BES.equals(nivel)) {
//	    				nivel = EnumFormatoFirma.XAdES_T;
//	    			} else if (EnumFormatoFirma.XAdES_T.equals(nivel)) {
//	    				nivel = EnumFormatoFirma.XAdES_C;
//	    			} else if (EnumFormatoFirma.XAdES_C.equals(nivel)) {
//	    				nivel = EnumFormatoFirma.XAdES_X;
//	    			} else if (EnumFormatoFirma.XAdES_X.equals(nivel)) {
//	    				nivel = EnumFormatoFirma.XAdES_XL;
//	    			} else {
//	    				break;
//	    			}
//	    			mejora.add(nivel);
//	    		}
//	    	}
//	    	 	
//	    	// Subimos los niveles. Están ordenados de menor a mayor
//	    	Iterator<EnumFormatoFirma> niveles = mejora.iterator();
//	    	boolean hasNext = niveles.hasNext();
//	    	while(hasNext) {
//	    		nivel = niveles.next();
//	    		hasNext = niveles.hasNext();
//	    		if ((EnumFormatoFirma.XAdES_T).equals(nivel)) {
//	    			try {
//	    				if (servidorTSA!=null && !servidorTSA.trim().equals(ConstantesXADES.CADENA_VACIA)) {
//	    					// Obtenemos la respuesta del servidor TSA
//	    					TSCliente tsCli = null;
//	    					if(estadoProxy) {
//	    						if(estadoProxy) {
//	        						System.setProperty("http.proxyHost", servidorProxy);
//	        						System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
//	        						if (isProxyAuth) {
//	        							Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
//	        						} 
//	        						else {
//	        							Authenticator.setDefault(null);
//	        						}
//	    						}
//	    					} 
//    						tsCli = new TSCliente(servidorTSA,algoritmoTSA);
//	    					byte[] byteSignature = null;
//							try {
//								byteSignature = UtilidadTratarNodo.obtenerByteNodo((Element)nodoFirma, ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE_VALUE, CanonicalizationEnum.C14N_OMIT_COMMENTS, 5);
//							} catch (FirmaXMLError e) {
//								log.error(e.getMessage(), e);
//								throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_6));
//							}
//	    					try {
//								addXadesT((Element)nodoFirma, configuracion.getValor(ConstantesXADES.SIGNATURE_NODE_ID),tsCli.generarSelloTiempo(byteSignature)) ;
//							} catch (TSClienteError e) {
//								log.error(e.getMessage(), e);
//								throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_6));
//							}
//	    				} else {
//	    					throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_6));
//	    				}
//	    			} catch (AddXadesException e) {
//	    				log.error(e.getMessage(), e);
//	    				throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_7) + e.getMessage()) ;
//	    			}
//
//	    		} else if((EnumFormatoFirma.XAdES_C).equals(nivel)) {
//	    			ArrayList<X509Certificate> certRef = null;
//	    	    	byte[] respuestaOCSP = null;
//	    			respuesta = new RespuestaOCSP();
//	    			try
//	    			{
//	    				if (servidorOCSP!=null && !servidorOCSP.trim().equals(ConstantesXADES.CADENA_VACIA)) {
//	    					// Obtenemos la respuesta del servidor OCSP
//	    					String tiempoRespuesta = ConstantesXADES.CADENA_VACIA;
//	    					OCSPCliente ocspCliente = null;
//	    					RespYCerts bloque = null;
//	    					try {
//	    						if(estadoProxy) {
//	        						System.setProperty("http.proxyHost", servidorProxy);
//	        						System.setProperty("http.proxyPort", Integer.toString(numeroPuertoProxy));
//	        						if (isProxyAuth) {
//	        							Authenticator.setDefault(new SimpleAuthenticator(proxyUser, proxyPass));
//	        						} 
//	        						else {
//	        							Authenticator.setDefault(null);
//	        						}
//	    						}
//    							ocspCliente = new OCSPCliente(servidorOCSP);
//    							
//	    						try {
//									respuesta = ocspCliente.validateCert(
//											(X509Certificate)resultado.getDatosFirma().getCadenaFirma().getCertificates().get(0));
//								} catch (OCSPProxyException e) {
//									log.error(e.getMessage(), e);
//									throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_8) + e.getMessage()) ;
//								}
//	    						tiempoRespuesta = UtilidadFechas.formatFechaXML(respuesta.getTiempoRespuesta());
//	    					}
//	    					catch (OCSPClienteException ex) {
//	    						log.error(ex.getMessage(), ex);
//	    						throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_8) + ex.getMessage()) ;
//	    					}
//
//	    					// Solo continúa si el certificado es válido
//	    					if (respuesta.getNroRespuesta()!=0) {
//	    						throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_9)) ;
//	    					}
//	    					certRef = respuesta.getRefCerts();
//	    					respuestaOCSP = respuesta.getRespuestaEncoded();
//
//	    					bloque = new RespYCerts();
//	    					bloque.setX509Cert(
//	    							(X509Certificate)resultado.getDatosFirma().getCadenaFirma().getCertificates().get(0));
//	    					RespOCSP ocspBloque = new RespOCSP();
//	    					ocspBloque.setRespOCSP(respuestaOCSP);
//	    					ocspBloque.setTiempoRespuesta(tiempoRespuesta);
//	    					ocspBloque.setResponder(respuesta.getValorResponder(), respuesta.getTipoResponder());
//	    					bloque.setCertstatus(ocspBloque);
//	    					respuestas.add(bloque);
//	    					int certRefSize = certRef.size();
//	    					for (int x=1; x < certRefSize; ++x) {
//	    						X509Certificate certificado = certRef.get(x);          					
//	    						try {
//	    							try {
//										respuesta = ocspCliente.validateCert(certificado);
//									} catch (OCSPProxyException e) {
//										throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_8) + e.getMessage()) ;
//									}
//	    							tiempoRespuesta = UtilidadFechas.formatFechaXML(respuesta.getTiempoRespuesta());
//	    						} catch (OCSPClienteException ex) {
//	    							log.error(ex.getMessage(), ex);
//	    							throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_8) + ex.getMessage()) ;
//	    						}
//	    						// Sólo continúa si el certificado es válido
//	    						if (respuesta.getNroRespuesta()!=0) {
//	    							throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_9)) ;
//	    						}
//	    						respuestaOCSP = respuesta.getRespuestaEncoded();
//	    						bloque = new RespYCerts();
//	    						bloque.setX509Cert(certificado);
//		    					RespOCSP ocspBloqueRef = new RespOCSP();
//		    					ocspBloqueRef.setRespOCSP(respuestaOCSP);
//		    					ocspBloqueRef.setTiempoRespuesta(tiempoRespuesta);
//		    					ocspBloqueRef.setResponder(respuesta.getValorResponder(), respuesta.getTipoResponder());
//		    					bloque.setCertstatus(ocspBloqueRef);
//	    						respuestas.add(bloque);
//	    					}
//	    					// Se añaden los elementos propios de la firma XADES-C
//	    					// TODO: recuperar el algoritmo de digest de la configuracion para poder configurarlo
//	    					addXadesC((Element)nodoFirma, respuestas, XAdESSchemas.getXAdESSchema(xadesSchema), UtilidadFirmaElectronica.DIGEST_ALG_SHA1);
//	    				} else {
//	    					throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_10));
//	    				}
//	    			} catch (AddXadesException e) {
//	    				log.error(e.getMessage(), e);
//	    				throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_10) + e.getMessage()) ;
//	    			}
//	    			
//	    			// Si se firma XAdES-C exclusivamente, se guardan los ficheros adjuntos
//	    			if (!niveles.hasNext()) {
//	    				try {
//	    					doc = addURIXadesC((Element)nodoFirma, saveOCSPFiles(respuestas, path, nombreArchivo), path);
//	    				} catch (FirmaXMLError ex) {
//	    					throw new ClienteError("Error al guardar ficheros de estados de certificados", ex);
//	    				}
//	    	        } 
//
//	    		} else if((EnumFormatoFirma.XAdES_X).equals(nivel)) {
//	    			File fichero = null;
//	    		    FileOutputStream f = null;
//	    	        try
//	    	        {
//	    	        	fichero = new File(path + nombreArchivo); 
//	    	        	f = new FileOutputStream(fichero);
//	    	        	XMLUtils.outputDOM(doc, f,true);
//	    	        }
//	    	        catch (Throwable t)
//	    	        {
//	    	        	if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
//	    	        		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
//	    	        	else
//	    	        		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
//	    	        } finally {
//	    	        	try {
//	    	        		if (f != null)
//	    	        			f.close();
//	    				} catch (IOException e) {
//	    					// No ocurre 
//	    					log.error(e.getMessage(), e);
//	    				}
//	    	        }            
//	        		
//	        		// Se obtiene el nodo raíz de la firma
//	        		Element signatureElement = (Element) nodoFirma;
//	        		if (!(new NombreNodo(ConstantesXADES.SCHEMA_DSIG, ConstantesXADES.SIGNATURE).equals(
//	        				new NombreNodo(signatureElement.getNamespaceURI(), signatureElement.getLocalName())))) {
//	        			// No se encuentra el nodo Signature
//	        			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_33) + 
//	        					ConstantesXADES.ESPACIO + ConstantesXADES.SIGNATURE);
//	        		}
//	        		
//	        		// A partir del nodo raíz de la firma se obtiene el nodo UnsignedSignatureProperties
//	        		Element unsignedSignaturePropertiesElement = null;
//	        		NodeList unsignedSignaturePropertiesNodes = 
//	        			signatureElement.getElementsByTagNameNS(xadesSchema, ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES);
//	        		
//	        		if (unsignedSignaturePropertiesNodes.getLength() != 1) {
//	        			// El nodo UnsignedSignatureProperties no existe o no es único 
//	        			log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_36) + ConstantesXADES.ESPACIO + 
//	        					ConstantesXADES.UNSIGNED_SIGNATURE_PROPERTIES +	ConstantesXADES.ESPACIO + 
//	        					I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_37) + ConstantesXADES.ESPACIO +
//	    						unsignedSignaturePropertiesNodes.getLength());
//	        			// El sistema no soporta nodos UnsignedSignatureProperties múltiples
//	        			throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_41));
//	        		} else
//	        			unsignedSignaturePropertiesElement = (Element)unsignedSignaturePropertiesNodes.item(0);
//	        		
//	        		// Se añaden los elementos propios de la firma XADES-X
//	        		try {
//						addXadesX(unsignedSignaturePropertiesElement);
//					} catch (AddXadesException e) {
//						log.error(e.getMessage(), e);
//	 	                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_12) + e.getMessage()) ;
//					}
//
//	    		} else if((EnumFormatoFirma.XAdES_XL).equals(nivel)) {
//	    			 try {            	
//	 	                addXadesXL((Element)nodoFirma, respuestas, XAdESSchemas.getXAdESSchema(xadesSchema));
//	 	            } catch (Exception e) {
//	 	            	log.error(e.getMessage(), e);
//	 	                throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_12) + e.getMessage()) ;
//	 	            }
//	    		}
//	    		
//	    	} // fin del bucle
//	    } // Fin del if isValidate
//	    
//	    // Se salva el fichero generado
//	    File fichero = null;
//	    FileOutputStream f = null;
//        try
//        {
//        	fichero = new File(path + nombreArchivo); 
//        	f = new FileOutputStream(fichero);
//        	XMLUtils.outputDOM(doc, f,true);
//        }
//        catch (Throwable t)
//        {
//        	if (t.getMessage().startsWith(ConstantesXADES.JAVA_HEAP_SPACE))
//        		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_3));
//        	else
//        		throw new ClienteError(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_4));
//        } finally {
//        	try {
//        		if (f != null)
//        			f.close();
//			} catch (IOException e) {
//				// No ocurre 
//				log.error(e.getMessage(), e);
//			}
//        }
//	    
//    	return true;
//    } 
    
    /**
     * Este método se encarga de insertar las URIs de XADES-C en la firma
     * 
     * @param doc, Documento con la firma xml
     * @param listaArchivos, Lista de nombres de la respuestaOCSP y el path de certificación
     * @return Document doc, Documento firmado con las nuevas URI´s
     */
    public Document addURIXadesC(Element firma, ArrayList<NombreElementos> listaArchivos, String baseUri) throws FirmaXMLError
    {
    	
    	Document doc = firma.getOwnerDocument();
    	
    	NodeList completeCertificateRefs = null;
    	NodeList completeRevocationRefs = null;
    	
    	// TODO: MALLLLLL se estan buscando por todo el documento
    	completeCertificateRefs = firma.getElementsByTagNameNS(xadesSchema, ConstantesXADES.COMPLETE_CERTIFICATE_REFS);
    	completeRevocationRefs = firma.getElementsByTagNameNS(xadesSchema, ConstantesXADES.COMPLETE_REVOCATION_REFS);
    	
    	if (completeCertificateRefs.getLength() == 0 || completeRevocationRefs.getLength() == 0) {
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_29));
    		return doc;
    	}
        
        String tipoUri = null;
        if (ConstantesXADES.SCHEMA_XADES_111.equals(xadesSchema)) 
			tipoUri = ConstantesXADES.URI_MINUS;
		else 
			tipoUri = ConstantesXADES.URI_MAYUS;
    	
        // TODO: código parcheado, hay que recodificar relacionando correctamente nombres de ficheros con CRL/OCSP ref relacionado 
        
        // A continuación se sacan las referencias OCSP del nodo OCSPRefs
        NodeList ocspRefs = null;
        NodeList crlRefs = null;
        try {
			ArrayList<Element> listOcspRefs = UtilidadTratarNodo.obtenerNodos((Element)completeRevocationRefs.item(0), null, new NombreNodo(xadesSchema, ConstantesXADES.OCSP_REFS));
			ArrayList<Element> listCRLRefs = UtilidadTratarNodo.obtenerNodos((Element)completeRevocationRefs.item(0), null, new NombreNodo(xadesSchema, ConstantesXADES.CRL_REFS));
			if (listOcspRefs.size() > 1) {
	    		throw new FirmaXMLError("hay demasiados elementos ocsprefs"); 
			}
			if (listCRLRefs.size() > 1) {
	    		throw new FirmaXMLError("hay demasiados elementos crlrefs"); 
			}
			if (listOcspRefs.size() > 0)
				ocspRefs = listOcspRefs.get(0).getChildNodes();
			if (listCRLRefs.size() > 0)
				crlRefs = listCRLRefs.get(0).getChildNodes();
		} catch (FirmaXMLError ex) {
    		throw new FirmaXMLError("error obteniendo elementos ocsprefs y crlrefs"); 
		}
        
    	// Si ha encontrado el nodo OCSPRefs, se pasa a capturar su contenido
		Iterator<NombreElementos> it = listaArchivos.iterator();
		int indexOCSP = 0;
		int indexCRL = 0;
		while (it.hasNext()) {
			NombreElementos nf = it.next();
			if (it.hasNext()) {
				String nameFile = nf.getNameFileCRLResp();
				Node el;
				if (nameFile == null) {
					nameFile = nf.getNameFileOCSPResp();
					if (nameFile == null)
						throw new FirmaXMLError("Fichero de status (OCSP o CRL) sin nombre");
					if ((ocspRefs == null) || (ocspRefs.getLength() <= indexOCSP))
			    		throw new FirmaXMLError("Fichero de status no relacionable con crlref");
					el = ((Element)ocspRefs.item(indexOCSP++)).getElementsByTagNameNS(xadesSchema, ConstantesXADES.OCSP_IDENTIFIER).item(0);
				}
				else {
					if ((crlRefs == null) || (crlRefs.getLength() <= indexCRL))
						throw new FirmaXMLError("Fichero de status no relacionable con crlref");
					el = ((Element)crlRefs.item(indexCRL++)).getElementsByTagNameNS(xadesSchema, ConstantesXADES.XADES_TAG_CRL_IDENTIFIER).item(0);
				}
	   			Attr uri = doc.createAttributeNS(null, tipoUri);
//				uri.setValue(relativizeRute(baseUri,new File(nameFile)));
				uri.setValue(nameFile);
	
				NamedNodeMap nodo = el.getAttributes();
				nodo.setNamedItem(uri);
			}
		}
		
//        if (ocspRefs != null)
//        {
//        	// Se saca la lista de referencias
//        	NodeList refs = ocspRefs.getChildNodes();
//        	int l = refs.getLength();
//        	for (int i=0; i<l; i++)
//           	{
//        		// Sacamos los nodos OCSPRef uno por uno
//           		Element ocspRef = (Element)refs.item(i); // Sacamos OCSPRef
//           		NodeList list = ocspRef.getElementsByTagNameNS(xadesSchema, ConstantesXADES.OCSP_IDENTIFIER);
//           		// Si existe, incluimos la URI de su respuesta OCSP
//           		if (ocspRef != null) {
//           			Attr uri = doc.createAttributeNS(null, tipoUri);
//    				uri.setValue(relativizeRute(baseUri,new File((listaArchivos.get(i)).getNameFileOCSPResp())));
//
//    				NamedNodeMap nodoOCSP = list.item(0).getAttributes();
//    				nodoOCSP.setNamedItem(uri);
//           		}
//           	}        	
//        }
    	
    	// A continuación se sacan los Certificados del nodo CertRefs
    	Node certRefs = (Node)completeCertificateRefs.item(0).getFirstChild();

    	// Si ha encontrado el nodo CertRefs, se pasa a capturar su contenido
    	if (certRefs != null)
    	{
    		// Se saca la lista de certificados
    		NodeList certs = certRefs.getChildNodes();
    		int l = certs.getLength();
    		
    		if (l!=(listaArchivos.size()-1)) {
    			log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_30));
    		}

    		for (int i=0; i<l; i++) 
    		{
    			// Sacamos los nodos Cert uno por uno
    			Node certificado = certs.item(i); // Sacamos cert
    			if (certificado != null) {
    				// incluimos la uri

    				Attr uri = doc.createAttributeNS(null, tipoUri);
//    				uri.setValue(relativizeRute(baseUri,new File(listaArchivos.get(i+1).getNameFileX509Cert()))); // La posicion 0 es del certificado firmante
    				uri.setValue(listaArchivos.get(i+1).getNameFileX509Cert()); // La posicion 0 es del certificado firmante

    				NamedNodeMap nodoCertificado = certificado.getAttributes();
    				nodoCertificado.setNamedItem(uri);
           		}
           	}
        }
    	        
        return doc;
    }
    
    /**
     * Este método se encarga de guardar los archivos OCSP
     * @return un ArrayList con la lista de archivos guardados
     */
    public ArrayList<NombreElementos> saveOCSPFiles(ArrayList<RespYCerts> respuesta, IStoreElements storer)
    {
//    	OutputStream f = null;
//    	File directorio = null;
//    	NombreElementos nameFiles = null;
    	ArrayList<NombreElementos> listaArchivos = new ArrayList<NombreElementos>();
//    	nomFichero = nomFichero.substring(0,nomFichero.indexOf(ConstantesXADES.PUNTO));
//    	
//    	String pathDir = rutaFicheroFirmado + DIR_OCSP;
    	
    	if ((respuesta != null) && (respuesta.size() > 0)) {
    		int i = 0;
    		Iterator<RespYCerts> it = respuesta.iterator();
    		while (it.hasNext()) {
    			RespYCerts respAndCert = it.next();

    			// Datos del certificado
    			X509Certificate certificate = null;
    			String certFile = null;
    			if (i > 0) {
    				certFile = respAndCert.getX509CertFile();
    				if ((certFile == null) || (certFile.trim().length() == 0))
    					certificate = respAndCert.getCertstatus().getCertificate();
    			}
    			// Datos del estado del certificado
    			ICertStatus respCert = null;
    			if (i < respuesta.size() - 1) {
    				respCert = respAndCert.getCertstatus();
    			}
    			// Almacena los datos
    			String[] names = storer.storeCertAndStatus(certificate, respCert);

    			NombreElementos nombreElemento = new NombreElementos();
    			if ((certFile != null) && (certFile.trim().length() > 0)) {
    				nombreElemento.setNameFileX509Cert(certFile);
    			} else {
    				nombreElemento.setNameFileX509Cert(names[0]);
    			}
    			if (respCert instanceof IOCSPCertStatus) {
    				nombreElemento.setNameFileOCSPResp(names[1]);
    			} else if (respCert instanceof IX509CRLCertStatus) {
    				nombreElemento.setNameFileCRLResp(names[1]);
    			}
    			listaArchivos.add(nombreElemento);
    			
    			i++;
    		}
    		
    		
//    		int longitud = respuesta.size();
//    		for (int x = 0; x < longitud; ++x) { // Se incluyen todas las OCSPResp y todos los Cert del path menos el firmante
//    			
//    			RespYCerts respAndCert = respuesta.get(x);
//    			nameFiles = new NombreElementos();
//    			
//				if (x < longitud - 1) {
//	    			IRespCertStatus certStatus = respAndCert.getCertstatus();
//	    			if (certStatus instanceof RespOCSP) {
//	    				RespOCSP respOcsp = (RespOCSP)certStatus;
//	    				
//	    					if (respOcsp.getFilename() == null) {
//				    			// Guardamos la respuesta OCSP
//				    			ByteArrayInputStream respuestaOCSP = new ByteArrayInputStream(respOcsp.getRespOCSP());
//								// AppPerfect: Falsos positivos. No son expresiones constantes
//				    			String nomRespOCSP = DIR_OCSP + ConstantesXADES.FICH_OCSP_RESP + nomFichero + ConstantesXADES.GUION + (x + 1) + ConstantesXADES.EXTENSION_OCS;
//								log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_6) + ConstantesXADES.ESPACIO + rutaFicheroFirmado + nomRespOCSP);
//				    			try {
//				    				directorio = new File(pathDir);
//				    				directorio.mkdir();
//				    				f = new FileOutputStream(rutaFicheroFirmado + nomRespOCSP);
//				    				int ch = respuestaOCSP.read();
//				    				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				    				while ((ch) >= 0) {
//				    					bos.write(ch);
//				    					ch = respuestaOCSP.read();
//				    				}
//				    				f.write(bos.toByteArray());
//				    			} catch (FileNotFoundException ex) {
//				    				log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_3, rutaFicheroFirmado));
//				    				log.debug("", ex);
//				    			} catch (IOException ex) {
//				    				log.error(i18n.getLocalMessage(ConstantsXAdES.I18N_SIGN_4, rutaFicheroFirmado, nomRespOCSP));
//				    				log.debug("", ex);
//				    			} finally {
//				    				try {
//					    				if (f != null) {
//					    					f.flush() ;
//					    					f.close() ;
//					    				}
//				    					nameFiles.setNameFileOCSPResp(rutaFicheroFirmado + nomRespOCSP);
//				    				} catch (IOException e) {
//				    					log.error(e.getMessage());
//				    				}
//				    			}
//		    				}
//		    				else {
//		    					nameFiles.setNameFileOCSPResp(respOcsp.getFilename());
//		    				}
//	    			}
//	    			else if (certStatus instanceof RespCRL) {
//	    				RespCRL respCRL = (RespCRL)certStatus;
//    					if (respCRL.getFilename() == null) {
//    						// TODO: escribir la CRL a disco duro
//    					}
//	    				else {
//	    					nameFiles.setNameFileCRLResp(respCRL.getFilename());
//	    				}
//	    			}
//				}
//    		
//    			if (x!=0) // Se salta el certificado que firma 
//    			{ 
//    				// Guardamos la cadena de certificados
//    				String certFile = respAndCert.getX509CertFile();
//    				if (certFile == null) {
//	    				X509Certificate certificado = respAndCert.getX509Cert();
//	    				String nomCert = DIR_CERTS + ConstantesXADES.FICH_CERT_REF + nomFichero + ConstantesXADES.GUION + (x) + ConstantesXADES.EXTENSION_CER;
//	    				log.debug(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_DEBUG_5) + ConstantesXADES.ESPACIO + rutaFicheroFirmado + nomCert);
//	    				try {
//	    					directorio = new File(rutaFicheroFirmado + DIR_CERTS);
//	        				directorio.mkdir();
//	    					f = new BufferedOutputStream(new FileOutputStream(rutaFicheroFirmado + nomCert));
//	    					f.write(certificado.getEncoded()) ; // CertRef guardado
//	    				} catch (FileNotFoundException e) {
//	    					log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_26) + ConstantesXADES.ESPACIO + e.getMessage());
//	    				} catch (IOException e) {
//	    					log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_26) + ConstantesXADES.ESPACIO + e.getMessage());
//	    				} catch (CertificateEncodingException e) {
//	    					log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_26) + ConstantesXADES.ESPACIO + e.getMessage());
//						} finally {
//	    					try {
//	    						f.flush() ;
//	    						//AppPerfect: Falso positivo
//	    						f.close() ;
//	    						nameFiles.setNameFileX509Cert(rutaFicheroFirmado + nomCert);
//	    					} catch (IOException e) {
//	    						log.error(e.getMessage());
//	    					}
//	    				}
//    				}
//    				else {
//    					nameFiles.setNameFileX509Cert(certFile);
//    				}
//    			} else {
//    				nameFiles.setNameFileX509Cert(ConstantesXADES.CADENA_VACIA);
//    			}
//    			listaArchivos.add(nameFiles);
//    		}
    		
    	} else {
    		log.error(I18n.getResource(ConstantesXADES.LIBRERIAXADES_FIRMAXML_ERROR_27));
    		return null;
    	}
    	
    	return listaArchivos;
    }
}
