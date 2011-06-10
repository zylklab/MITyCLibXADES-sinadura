package es.mityc.javasign.xades.examples.validations;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.w3c.dom.Element;

import es.mityc.firmaJava.libreria.xades.ResultadoValidacion;
import es.mityc.javasign.xml.xades.policy.IValidacionPolicy;
import es.mityc.javasign.xml.xades.policy.PolicyResult;

public class MyPolicy implements IValidacionPolicy {

	public String getIdentidadPolicy() {
		return "Política de ejemplo";
	}

	/**
	 * <p>Se valida que el certificado empleado en la firma se encuentre en el periodo de validez.</p>
	 * 
	 * @param element Nodo de firma
	 * @param resultadoValidacion Estructura de datos de resultado de validación 
	 */
	public PolicyResult validaPolicy(Element element, ResultadoValidacion resultadovalidacion) {
		PolicyResult pr = new PolicyResult();
		
		X509Certificate cert = (X509Certificate) resultadovalidacion.getDatosFirma().getCadenaFirma().getCertificates().get(0);
		
		try {
			cert.checkValidity(new Date());
			pr.setResult(PolicyResult.StatusValidation.valid);
			System.out.println("Validación de política superada.");
		} catch (CertificateExpiredException e) {
			pr.setResult(PolicyResult.StatusValidation.invalid);
			pr.setDescriptionResult(e.getMessage());
			System.out.println("Validación de política NO superada. Certificado caducado");
		} catch (CertificateNotYetValidException e) {
			pr.setResult(PolicyResult.StatusValidation.invalid);
			pr.setDescriptionResult(e.getMessage());
			System.out.println("Validación de política NO superada. Certificado aún no válido");
		}
		
		return pr;
	}
}
