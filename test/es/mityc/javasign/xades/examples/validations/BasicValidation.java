/*
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
 * Copyright 2007 Ministerio de Industria, Turismo y Comercio
 * 
 */
package es.mityc.javasign.xades.examples.validations;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.firmaJava.libreria.xades.ValidarFirmaXML;

/**
 * <p>
 * Clase de ejemplo para realizar la validación básica (es decir, sin comprobar
 * la cadena de confianza ni ninguna política) de una firma XAdES utilizando la
 * librería XADES
 * </p>
 * 
 * @author Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class BasicValidation {
	
    private final static String FICHERO_XADES_VALIDO = "/examples/FicheroFirmadoXADESValido.xml";
    private final static String FICHERO_XADES_NO_VALIDO = "/examples/FicheroFirmadoXADESNoValido.xml";

    /**
     * <p>
     * Punto de entrada al programa
     * </p>
     * 
     * @param args
     *            Argumentos del programa
     */
    public static void main(String[] args) {
        BasicValidation validation = new BasicValidation();
        System.out.println("\nValidando una firma válida:");
        validation.validarFichero(BasicValidation.class.getResourceAsStream(FICHERO_XADES_VALIDO));
        System.out.println("\n-------------------------------------------------------\n");
        System.out.println("Validando una firma inválida:");
        validation.validarFichero(BasicValidation.class.getResourceAsStream(FICHERO_XADES_NO_VALIDO));
        System.out.println("\n-------------------------------------------------------\n");
    }

    /**
     * <p>
     * Método que realiza la validación de firma digital XAdES a un fichero y
     * muestra el resultado
     * </p>
     * 
     * @param fichero
     *            Fichero a validar
     */
    public void validarFichero(InputStream fichero) {

        // Se declara la estructura de datos que almacenará el resultado de la validación
        ArrayList<ResultadoValidacion> results = null;

        // Se convierte el InputStream a Document
        Document doc = parseaDoc(fichero);
        if (doc == null) {
            System.out.println("Error de validación. No se pudo parsear la firma.");
            return;
        }
		
        // Se instancia el validador y se realiza la validación
        try {
            ValidarFirmaXML vXml = new ValidarFirmaXML();
            results = vXml.validar(doc, "./", null) ;
        } catch(Exception e){
            e.printStackTrace();
        }
		
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
                        + "\nNodos firmados: " + result.getFirmados());
            } else {
                // El método getLog devuelve el mensaje de error que invalidó la firma
                System.out.println("La firma NO es valida\n" + result.getLog());
            }
        }
    }

    /**
     * <p>
     * Parsea un documento XML y lo introduce en un DOM.
     * </p>
     * 
     * @param uriFirma
     *            URI al fichero XML
     * @return Docuemnto parseado
     */
    private Document parseaDoc(InputStream fichero) {
		
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true) ;

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            System.out.println("Error interno al parsear la firma");
            ex.printStackTrace();
            return null;
        }

        Document doc = null;
        try {
            doc = db.parse(fichero);
            return doc;
        } catch (SAXException ex) {
            doc = null;
        } catch (IOException ex) {
            System.out.println("Error interno al validar firma");
            ex.printStackTrace();
        } finally {
            dbf = null;
            db = null; 
        }
		
        return null;
    }
}