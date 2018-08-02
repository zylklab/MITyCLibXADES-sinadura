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

package es.mityc.firmaJava.libreria.xades;

import java.net.URI;
import java.util.ArrayList;

import org.w3c.dom.Document;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.javasign.certificate.ICertStatus;

/**
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */

public class ResultadoValidacion { // implements ConstantesXADES
	
	private boolean validado;
	private ResultadoEnum resultado;
	private String log;
	private String nivelValido;
	private EnumFormatoFirma EnumNivel; // Último nivel validado
	private Document doc;
	private DatosFirma datosFirma;
	private URI baseURI;
	private ArrayList<String> firmados;
	private ArrayList<ResultadoValidacion> contrafirmadoPor;
	private ICertStatus certStatus = null;

	/**
	 * Crea una nueva instancia de ValidationResult()
	 * 
	 * Los datos que contiene son:
	 * 	boolean validado.- Estado de validez de la firma
	 *	ResultadoEnum resultado.- Estructura de datos de resultado de validación
	 *	String log .- Almacén de mensajes a mostrar en cliente
	 *	String nivelValido .- Mensaje con el último nivel XAdES válido
	 *	EnumFormatoFirma EnumNivel.- Nivel XAdES de la firma (sin validar)
	 *	Document doc .- El documento de firma
	 *	DatosFirma datosFirma.- Estructura de datos de firma
	 *	URI baseURI .- URI de base de la firma (ruta dónde se encuentra)
	 *	ArrayList<String> firmados.- Ficheros firmados por la firma
	 *	ArrayList<ResultadoValidacion> contrafirmadoPor .- Nodos que contrafirman a ésta firma
	 *  String certRevokedMsg .- Causa de invalidez de un certificado
	 */
	public ResultadoValidacion()
	{
		this.validado 		= false;
		this.resultado 		= ResultadoEnum.UNKNOWN; 
		this.log			= ConstantesXADES.CADENA_VACIA;
		this.nivelValido	= ConstantesXADES.CADENA_VACIA;
		this.firmados		= new ArrayList<String> ();
		this.contrafirmadoPor = new ArrayList<ResultadoValidacion> ();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLog() {
		return log;
	}
	
	/**
	 * 
	 * @param log
	 */
	public void setLog(String log) {
		this.log = log;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValidate() {
		return validado;
	}
	
	/**
	 * 
	 * @param validado
	 */
	public void setValidate(boolean validado) {
		this.validado = validado;
	}
	
	public ResultadoEnum getResultado() {
		return resultado;
	}

	public void setResultado(ResultadoEnum resultado) {
		this.resultado = resultado;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNivelValido() {
		return nivelValido;
	}
	
	/**
	 * 
	 * @param log
	 */
	public void setNivelValido(String nivelValido) {
		this.nivelValido = nivelValido;
	}

	/**
	 * 
	 * @return
	 */
	public DatosFirma getDatosFirma() {
		return datosFirma;
	}

	/**
	 * 
	 * @param datosFirma
	 */
	public void setDatosFirma(DatosFirma datosFirma) {
		this.datosFirma = datosFirma;
	}

	/**
	 * Último nivel validado
	 * @return EnumNivel
	 */
	public EnumFormatoFirma getEnumNivel() {
		return EnumNivel;
	}

	/**
	 *  Último nivel validado
	 * @param enumNivel
	 */
	public void setEnumNivel(EnumFormatoFirma enumNivel) {
		EnumNivel = enumNivel;
	}

	/**
	 *  Devuelve la URI de base del XML de firma
	 * @return URI
	 */
	public URI getBaseURI() {
		return baseURI;
	}

	/**
	 *  Establece la URI de base del XML de firma
	 * @param URI
	 */
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
	}

	/**
	 * Devuelve la lista de nodos (externos a la firma) firmados
	 * @return ArrayList<String> .- ArrayList de URIs
	 */
	public ArrayList<String> getFirmados() {
		return firmados;
	}

	/**
	 * Establece la lista de nodos (externos a la firma) firmados
	 */
	public void setFirmados(ArrayList<String> firmados) {
		this.firmados = firmados;
	}
	
	/**
	 * Array de resultados de validación de las firmas que contrafirman a ésta
	 * @return ArrayList<ResultadoValidación>
	 */
	public ArrayList<ResultadoValidacion> getContrafirmadoPor() {
		return contrafirmadoPor;
	}
	
	/**
	 * Establece el array de resultados de validación de las firmas que contrafirman a ésta
	 * @param ArrayList<ResultadoValidación>
	 */
	public void setContrafirmadoPor(ArrayList<ResultadoValidacion> contrafirmadoPor) {
		this.contrafirmadoPor = contrafirmadoPor;
	}
	
	/**
	 * Añade al array de resultados de validación de las firmas que contrafirman a ésta 
	 * otro resultado de validación
	 * @param ResultadoValidación
	 */
	public void addContrafirmadoPor(ResultadoValidacion contrafirmadoPor) {
		if (contrafirmadoPor != null)
			this.contrafirmadoPor.add(contrafirmadoPor);
	}

	/**
	 * Recupera el estado del certificado de firma.
	 * @return Estado del certificado de firma, o <code>null</code> si no se ha obtenido.
	 */
	public ICertStatus getCertStatus() {
		return certStatus;
	}

	/**
	 * Establece el estado del certificado firmante.
	 * @param certStatus Estado del certificado firmante, <code>null</code> si no se ha obtenido
	 */
	public void setCertStatus(ICertStatus certStatus) {
		this.certStatus = certStatus;
	}
}