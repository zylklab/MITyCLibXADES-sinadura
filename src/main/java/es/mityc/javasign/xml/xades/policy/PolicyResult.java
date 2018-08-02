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

package es.mityc.javasign.xml.xades.policy;

import java.net.URI;

/**
 * Estructura para la validación de políticas de firma
 *
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 0.9 beta
 */

public class PolicyResult {
	
	public enum StatusValidation { unknown, valid, invalid };
	
	public class DownloadPolicy {
		public URI uri;
		public StatusValidation status;
		public DownloadPolicy(URI uri, StatusValidation status) {
			this.uri = uri;
			this.status = status;
		}
	}
	
	private StatusValidation result;		// Almacena el resultado de la validacion
	private String descriptionResult;		// Almacena una cadena descriptiva del resultado de la validación
	private String description;
	private URI policyID;
	private URI[] documentation;
	private DownloadPolicy[] downloable;
	private String[] notices;
	private IValidacionPolicy policyVal;	// Almacena el validador de la policy
	
	public PolicyResult(){
		result = StatusValidation.unknown;
	}
	
	public DownloadPolicy newDownloadPolicy(URI uri, StatusValidation status) {
		return new DownloadPolicy(uri, status);
	}
	
	public StatusValidation getResult() {
		return result;
	}

	public void setResult(StatusValidation result) {
		this.result = result;
	}

	public String getDescriptionResult() {
		return descriptionResult;
	}

	public void setDescriptionResult(String descriptionResult) {
		this.descriptionResult = descriptionResult;
	}

	public URI getPolicyID() {
		return policyID;
	}

	public void setPolicyID(URI policyID) {
		this.policyID = policyID;
	}

	public URI[] getDocumentation() {
		return documentation;
	}

	public void setDocumentation(URI[] documentation) {
		this.documentation = documentation;
	}

	public DownloadPolicy[] getDownloable() {
		return downloable;
	}

	public void setDownloable(DownloadPolicy[] downloable) {
		this.downloable = downloable;
	}

	public String[] getNotices() {
		return notices;
	}

	public void setNotices(String[] notices) {
		this.notices = notices;
	}

	/**
	 * Get clase validadora de la policy
	 * @return Instancia al validador de la policy
	 */
	public IValidacionPolicy getPolicyVal() {
		return policyVal;
	}

	/**
	 * Set clase validadora de la policy
	 * @param Instancia del validador de la policy
	 */
	public void setPolicyVal(IValidacionPolicy policyVal) {
		this.policyVal = policyVal;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof IValidacionPolicy) && (policyVal != null)) {
			IValidacionPolicy val = (IValidacionPolicy)obj;
			if (policyVal.getIdentidadPolicy().equals(val.getIdentidadPolicy()))
				return true;
			return false;
		}
		else
			return super.equals(obj);
	}
	
	public void copy(PolicyResult pr) {
		setResult(pr.getResult());
		setPolicyID(pr.getPolicyID());
		setDescriptionResult(pr.getDescriptionResult());
		setDocumentation(pr.getDocumentation());
		setDownloable(pr.getDownloable());
		setNotices(pr.getNotices());
	}
}
