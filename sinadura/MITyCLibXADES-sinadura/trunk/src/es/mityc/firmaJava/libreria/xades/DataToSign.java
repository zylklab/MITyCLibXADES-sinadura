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
package es.mityc.firmaJava.libreria.xades;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;

import es.mityc.firmaJava.libreria.utilidades.UtilidadFirmaElectronica;
import es.mityc.firmaJava.role.IClaimedRole;
import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.certificate.ICertStatusRecoverer;
import es.mityc.javasign.xml.refs.ObjectToSign;
import es.mityc.javasign.xml.xades.IStoreElements;
import es.mityc.javasign.xml.xades.LocalFileStoreElements;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class DataToSign {
	
	public enum XADES_X_TYPES { TYPE_1, TYPE_2 };
	
	private Date signDate;
	private File xmlInFileToSign;
	private String xmlToSign;
	private String basePath;
	private boolean enveloped;
	private ArrayList<ObjectToSign> objects;
	private File signingCert;
	private String[] productionPlace;
	private ArrayList<IClaimedRole> claimedRoles;
	private ICertStatusRecoverer certStatusManager;
	private String algDigestXmlDSig = UtilidadFirmaElectronica.DIGEST_ALG_SHA1;
	private String algDigestTSA = UtilidadFirmaElectronica.DIGEST_ALG_SHA1;
	private XAdESSchemas esquema = XAdESSchemas.XAdES_132;
//	private boolean XadesA = false;
	private IStoreElements storer;
	private boolean addPolicy = false;
	private String policyKey;
	private EnumFormatoFirma xadesFormat = EnumFormatoFirma.XAdES_BES;
	private Document doc;
	private String parentSignNode;
	private XADES_X_TYPES xadesXType = XADES_X_TYPES.TYPE_1;
	private String encoding = ConstantsXAdES.DEFAULT_XML_ENCODING;
	
	
	public DataToSign(File xmlToSign) {
		this.xmlInFileToSign = xmlToSign;
		basePath = xmlToSign.getParent();
		signDate = new Date();
	}
	
	public DataToSign(String xmlToSign) {
		this.xmlToSign = xmlToSign;
		signDate = new Date();
	}
	
	public DataToSign() {
		signDate = new Date();
	}
	
	/**
	 * <p>Establece la codificación del XML que se quiere firmar.</p>
	 * <p>La recomendación del estándar es que el XML a firmar esté en UTF-8 (valor que toma este campo por defecto).</p>
	 * @param _encoding Codificación del xml
	 */
	public void setXMLEncoding(String _encoding) {
		this.encoding = _encoding;
	}
	
	/**
	 * <p>Devuelve la codificación del XML a firmar.</p>
	 * <p>Por defecto devuelve UTF-8.</p>
	 * @return codificación del XML
	 */
	public String getXMLEncoding() {
		return this.encoding;
	}
	
	/**
	 * <p>Establece el tipo de XAdES-X que se quiere aplicar en la firma.</p>
	 * @param _xadesXType Tipo de XAdES-X
	 */
	public void setXAdESXType(XADES_X_TYPES _xadesXType) {
		this.xadesXType = _xadesXType;
	}
	
	/**
	 * <p>Devuelve el tipo de XAdES-X que se quiere aplicar.</p>
	 * <p>Por defecto el tipo es el 1.</p>
	 * @return tipo de XAdES-X
	 */
	public XADES_X_TYPES getXAdESXType() {
		return xadesXType;
	}
	
	/**
	 * Devuelve el nodo padre que se ha configurado para que contenga la firma. 
	 * @return nodo padre configurado, <code>null</code> si se quiere que la firme cuelgue del nodo raíz
	 */
	public String getParentSignNode() {
		return parentSignNode;
	}
	
	/**
	 * Establece la identidad del nodo padre que se quiere que contenga la firma.
	 * @param parentNode ID del nodo que se quiere configurar, <code>null</code> si se quiere que se cuelgue del nodo raíz
	 */
	public void setParentSignNode(String parentNode) {
		this.parentSignNode = parentNode;
	}
	
	/**
	 * Devuelve el formato de firma XAdES que se quiere aplicar en la firma. Por defecto se tiene establecido XAdES-BES.
	 * 
	 * @return
	 * @see es.mityc.firmaJava.configuracion.EnumFormatoFirma
	 */
	public EnumFormatoFirma getXadesFormat() {
		return xadesFormat;
	}
	
	/**
	 * Establece el formato de firma XAdES que se quiere aplicar a la firma
	 * 
	 * @param xadesFormat
	 * @see es.mityc.firmaJava.configuracion.EnumFormatoFirma
	 */
	public void setXadesFormat(EnumFormatoFirma xadesFormat) {
		this.xadesFormat = xadesFormat;
	}
	
	/**
	 * Indica si la firma ha de llevar política. Por defecto se tiene establecido que no se añade política.
	 * 
	 * @return
	 */
	public boolean hasPolicy() {
		return addPolicy;
	}
	
	/**
	 * Devuelve la clave de la política que se quiere aplicar a la firma (<code>null</code> si se quiere la implícita).
	 * 
	 * @return
	 */
	public String getPolicyKey() {
		return policyKey;
	}
	
	/**
	 * Establece si se quiere incluir una política en la firma
	 * @param addPolicy <b>true</b> si se quiere incluir política, <b>false</b> si no
	 */
	public void setAddPolicy(boolean addPolicy) {
		this.addPolicy = addPolicy;
	}
	
	/**
	 * Clave identificativa de la política que se quiere incluir. 
	 * 
	 * <br/><br/> Las claves disponibles dependerán de los módulos disponibles de políticas de firma (por ejemplo MITyCLibPolicy). Para
	 * política implícita se debe utilizar <code>null</code>.
	 *  
	 * @param policyKey
	 */
	public void setPolicyKey(String policyKey) {
		this.policyKey = policyKey;
	}
	
	/**
	 * Establece cual sera la instancia encargada de almacenar los elementos externos a una firma XAdES-X/XAdES-X (certificados y estados).
	 * 
	 * @param storer
	 */
	public void setElementsStorer(IStoreElements storer) {
		this.storer = storer;
	}
	
	public IStoreElements getElementsStorer() {
		if (storer == null) {
			// TODO: instanciar el storer configurado por defecto
			IStoreElements nullStorer = new LocalFileStoreElements();
			nullStorer.init(getBaseURI());
			storer = nullStorer;
		}
		return storer;
	}
	
	public boolean isEnveloped() {
		return enveloped;
	}
	
	public void setEnveloped(boolean enveloped) {
		this.enveloped = enveloped;
	}
	
	public InputStream getInputStream() throws IOException {
		InputStream is = null;
		if (enveloped) {
			if (xmlInFileToSign != null) {
		        is = new FileInputStream(xmlInFileToSign);
			}
			else if (xmlToSign != null) {
				is = new ByteArrayInputStream(xmlToSign.getBytes());
			}
		}
		return is;
	}
	
	/**
	 * Establece el documento que se quiere firmar
	 * @param doc
	 */
	public void setDocument(Document doc) {
		this.doc = doc;
	}
	
	public Document getDocument() {
		return this.doc;
	}
	
	public String getBaseURI() {
		if (basePath != null)
			return basePath;
		else
			return "";
	}
	
	public void setBaseURI(String basePath) {
		this.basePath = basePath;
	}
	
	public void addObject(ObjectToSign object) {
		if (objects == null)
			objects = new ArrayList<ObjectToSign>();
		objects.add(object);
	}
	
	public ArrayList<ObjectToSign> getObjects() {
		return objects;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public File getSigningCert() {
		return signingCert;
	}

	public void setSigningCert(File signingCert) {
		this.signingCert = signingCert;
	}
	
	public void setProductionPlace(String city, String state, String postalCode, String country) {
		if ((city == null) && (state == null) && (postalCode == null) && (country == null))
			productionPlace = null;
		else {
			productionPlace = new String[4];
			productionPlace[0] = city;
			productionPlace[1] = state;
			productionPlace[2] = postalCode;
			productionPlace[3] = country;
		}
	}
	
	public String[] getProductionPlace() {
		return productionPlace;
	}
	
	public void addClaimedRol(IClaimedRole data) {
		if (claimedRoles == null)
			claimedRoles = new ArrayList<IClaimedRole>();
		claimedRoles.add(data);
	}
	
	public ArrayList<IClaimedRole> getClaimedRoles() {
		return claimedRoles;
	}

	public ICertStatusRecoverer getCertStatusManager() {
		return certStatusManager;
	}

	public void setCertStatusManager(ICertStatusRecoverer certStatusManager) {
		this.certStatusManager = certStatusManager;
	}

	public File getXmlInFileToSign() {
		return xmlInFileToSign;
	}

	public String getAlgDigestXmlDSig() {
		return algDigestXmlDSig;
	}

	public void setAlgDigestXmlDSig(String algDigestXmlDSig) {
		if (algDigestXmlDSig != null)
			this.algDigestXmlDSig = algDigestXmlDSig;
	}

	public String getAlgDigestTSA() {
		return algDigestTSA;
	}

	public void setAlgDigestTSA(String algDigestTSA) {
		this.algDigestTSA = algDigestTSA;
	}

	public XAdESSchemas getEsquema() {
		return esquema;
	}

	public void setEsquema(XAdESSchemas esquema) {
		this.esquema = esquema;
	}

//	public boolean isXadesA() {
//		return XadesA;
//	}
//
//	public void setXadesA(boolean xadesA) {
//		XadesA = xadesA;
//	}
	
}
