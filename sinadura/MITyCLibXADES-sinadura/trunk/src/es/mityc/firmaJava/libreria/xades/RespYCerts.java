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

import es.mityc.javasign.certificate.ICertStatus;

/**
 * 
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */

public class RespYCerts {
	
	private String x509CertFile = null;
	private ICertStatus certstatus = null;
	private String idCertificado = null;
	private String idRespStatus = null;
	private String fileName = null;

	
	public RespYCerts() {
		// No hace nada
	}
	
	public String getIdCertificado() {
		return idCertificado;
	}

	public void setIdCertificado(String idCertificado) {
		this.idCertificado = idCertificado;
	}

	public String getIdRespStatus() {
		return idRespStatus;
	}

	public void setIdRespStatus(String idRespStatus) {
		this.idRespStatus = idRespStatus;
	}

	
	public String getX509CertFile() {
		return x509CertFile;
	}

	public void setX509CertFile(String certFile) {
		x509CertFile = certFile;
	}


	public ICertStatus getCertstatus() {
		return certstatus;
	}

	public void setCertstatus(ICertStatus certstatus) {
		this.certstatus = certstatus;
	}
	
	public void setFilename(String filename) {
		this.fileName = filename;
	}
	
	public String getFilename() {
		return fileName;
	}

	
//	public void loadCertFile(File certFile) {
//		try {
//			CertificateFactory cf = CertificateFactory.getInstance("X.509");
//			x509Cert = (X509Certificate)cf.generateCertificate(new FileInputStream(certFile));
//			x509CertFile = certFile.getAbsolutePath();
//		} catch (CertificateException ex) {
//			// TODO: dejar aviso
//		} catch (FileNotFoundException ex) {
//			// TODO: dejar aviso
//		}
//	}
	
}