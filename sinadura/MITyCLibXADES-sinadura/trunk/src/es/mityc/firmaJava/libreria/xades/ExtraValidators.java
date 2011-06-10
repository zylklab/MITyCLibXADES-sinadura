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

import java.util.List;

import es.mityc.javasign.certificate.ICertStatusRecoverer;
import es.mityc.javasign.trust.TrustAbstract;
import es.mityc.javasign.xml.xades.policy.IValidacionPolicy;

/**
 * Esta clase contiene los validadores adicionales que se utilizarán al validar la firma XAdES.
 * 
 * <br/><br/>Estos validadores contemplan la validación de:
 * <ul>
 * 	<li>Validación de política de firma: comprueban que la firma se ajuste a las políticas indicadas.</li>
 * 	<li>Validación de certificado: si la firma es válida y no incluye información de estado de certificado, comprueba el estado del 
 * certificado con este validador.</li>
 * 	<li>Confianza: comprueban que los elementos de la firma sean de entidades de confianza (certificados de firma, 
 * respuestas de estados de certificados, sellos de tiempo, etc).</li>
 * </ul>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class ExtraValidators {
	
	private List<IValidacionPolicy> policies;
	private ICertStatusRecoverer certStatus;
	private TrustAbstract trusterOCSP;
	private TrustAbstract trusterCRL;
	private TrustAbstract trusterCerts;
	private TrustAbstract trusterTSA;
	
	
	/**
	 * @param policies
	 * @param certStatus
	 * @param trusterCerts
	 */
	public ExtraValidators(List<IValidacionPolicy> policies, ICertStatusRecoverer certStatus, TrustAbstract trusterCerts) {
		super();
		this.policies = policies;
		this.certStatus = certStatus;
		this.trusterCerts = trusterCerts;
	}
	
	/**
	 * @return the policies
	 */
	public List<IValidacionPolicy> getPolicies() {
		return policies;
	}
	/**
	 * @param policies the policies to set
	 */
	public void setPolicies(List<IValidacionPolicy> policies) {
		this.policies = policies;
	}
	/**
	 * @return the certStatus
	 */
	public ICertStatusRecoverer getCertStatus() {
		return certStatus;
	}
	/**
	 * @param certStatus the certStatus to set
	 */
	public void setCertStatus(ICertStatusRecoverer certStatus) {
		this.certStatus = certStatus;
	}
	/**
	 * @return the trusterOCSP
	 */
	public TrustAbstract getTrusterOCSP() {
		return trusterOCSP;
	}
	/**
	 * @param trusterOCSP the trusterOCSP to set
	 */
	public void setTrusterOCSP(TrustAbstract trusterOCSP) {
		this.trusterOCSP = trusterOCSP;
	}
	/**
	 * @return the trusterCRL
	 */
	public TrustAbstract getTrusterCRL() {
		return trusterCRL;
	}
	/**
	 * @param trusterCRL the trusterCRL to set
	 */
	public void setTrusterCRL(TrustAbstract trusterCRL) {
		this.trusterCRL = trusterCRL;
	}
	/**
	 * @return the trusterCerts
	 */
	public TrustAbstract getTrusterCerts() {
		return trusterCerts;
	}
	/**
	 * @param trusterCerts the trusterCerts to set
	 */
	public void setTrusterCerts(TrustAbstract trusterCerts) {
		this.trusterCerts = trusterCerts;
	}
	/**
	 * @return the trusterTSA
	 */
	public TrustAbstract getTrusterTSA() {
		return trusterTSA;
	}
	/**
	 * @param trusterTSA the trusterTSA to set
	 */
	public void setTrusterTSA(TrustAbstract trusterTSA) {
		this.trusterTSA = trusterTSA;
	}

}
