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
package es.mityc.firmaJava;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.mityc.firmaJava.libreria.xades.ExtraValidators;
import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.firmaJava.libreria.xades.ValidarFirmaXML;
import es.mityc.firmaJava.libreria.xades.errores.FirmaXMLError;
import es.mityc.javasign.xml.xades.policy.IValidacionPolicy;

/**
 * Métodos base para la carga y validación de firmas 
 */
public abstract class ValidationBase {
	
	protected static final Log LOGGER = LogFactory.getLog(ValidationBase.class);
	
	protected InputStream loadRes(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		if (is == null) {
			fail("El recurso indicado (" + path + ") no está disponible");
		}
		return is;
	}
	
	protected String getBaseUri(String path) {
		URL url = this.getClass().getResource(path);
		if (url == null) {
			fail("El recurso indicado (" + path + ") no está disponible");
		}
		return url.toString();
	}

	protected File cargaFichero(String ruta) {
		File file = new File(ruta);
		if (!file.exists()) {
			fail("Fichero indicado no existe: " + ruta);
		}
		return file;
	}
	
	protected boolean validaFichero(File file, IValidacionPolicy policy) {
		try
		{
			ArrayList<IValidacionPolicy> policies = null;
			if (policy != null) {
				policies = new ArrayList<IValidacionPolicy>(1);
				policies.add(policy);
			}
			ValidarFirmaXML vXml = new ValidarFirmaXML();
			ExtraValidators extra = new ExtraValidators(policies, null, null);
			List<ResultadoValidacion> results = vXml.validar(file, extra);
			return results.get(0).isValidate();
		} catch (Exception ex) {
		}
		return false;
	}

	protected boolean validateStream(InputStream is, String baseUri, IValidacionPolicy policy) {
		try
		{
			ArrayList<IValidacionPolicy> policies = null;
			if (policy != null) {
				policies = new ArrayList<IValidacionPolicy>(1);
				policies.add(policy);
			}
			ValidarFirmaXML vXml = new ValidarFirmaXML();
			ExtraValidators extra = new ExtraValidators(policies, null, null);
			List<ResultadoValidacion> results = vXml.validar(is, baseUri, extra);
			return results.get(0).isValidate();
		} catch (Exception ex) {
			LOGGER.info(ex.getMessage());
			LOGGER.info("", ex);
		}
		return false;
	}

	protected boolean validateDoc(Document doc, String baseUri, IValidacionPolicy policy) {
		try
		{
			ArrayList<IValidacionPolicy> policies = null;
			if (policy != null) {
				policies = new ArrayList<IValidacionPolicy>(1);
				policies.add(policy);
			}
			ValidarFirmaXML vXml = new ValidarFirmaXML();
			ExtraValidators extra = new ExtraValidators(policies, null, null);
			List<ResultadoValidacion> results = vXml.validar(doc, baseUri, extra);
			
			// alfredo
			// Se muestra por consola el resultado de la validación
	        ResultadoValidacion result = null;
	        Iterator<ResultadoValidacion> it = results.iterator();
	        while (it.hasNext()) {
	            result = it.next();
	            boolean isValid = result.isValidate();
	            System.out.println("-----------------");
	            System.out.println("--- RESULTADO ---");
	            System.out.println("-----------------");
	            if(isValid){
	                // El método getNivelValido devuelve el último nivel XAdES válido
	                System.out.println("La firma es valida.\n" + result.getNivelValido() 
	                        + "\nCertificado: " + ((X509Certificate) result.getDatosFirma().getCadenaFirma().getCertificates().get(0)).getSubjectDN()
	                        + "\nFirmado el: " + result.getDatosFirma().getFechaFirma()
	                        + "\nEstado de confianza: " + result.getDatosFirma().esCadenaConfianza()
	                        + "\nNodos firmados: " + result.getFirmados());
	            } else {
	                // El método getLog devuelve el mensaje de error que invalidó la firma
	                System.out.println("La firma NO es valida\n" + result.getLog());
	            }
	        }
			// alfredo fin
			
			return results.get(0).isValidate();
			
		} catch (Exception ex) {
			LOGGER.info(ex.getMessage());
			LOGGER.info("", ex);
		}
		return false;
	}

	protected boolean validateStreamThrowable(InputStream is, String baseUri, IValidacionPolicy policy) throws FirmaXMLError {
		ArrayList<IValidacionPolicy> policies = null;
		if (policy != null) {
			policies = new ArrayList<IValidacionPolicy>(1);
			policies.add(policy);
		}
		ValidarFirmaXML vXml = new ValidarFirmaXML();
		ExtraValidators extra = new ExtraValidators(policies, null, null);
		List<ResultadoValidacion> results = vXml.validar(is, baseUri, extra);
		return results.get(0).isValidate();
	}
	
	protected Document loadDoc(String res) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			doc = dbf.newDocumentBuilder().parse(this.getClass().getResourceAsStream(res));
		} catch (ParserConfigurationException ex) {
			fail("No se puede generar document para firmar: " + ex.getMessage());
		} catch (SAXException ex) {
			fail("No se puede generar document para firmar: " + ex.getMessage());
		} catch (IOException ex) {
			fail("No se puede generar document para firmar: " + ex.getMessage());
		} catch (IllegalArgumentException ex) {
			fail("No se ha encontrado el recurso con el documento xml");
		}
		assertNotNull("No se ha podido generar el documento xml", doc);
		return doc;
	}

}
