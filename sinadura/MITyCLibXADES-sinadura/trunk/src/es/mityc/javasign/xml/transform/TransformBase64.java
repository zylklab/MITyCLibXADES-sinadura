package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de conversión de base64 a binario.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformBase64 extends Transform {
	/** Constructor. */
	public TransformBase64() {
		super(TransformProxy.TRANSFORM_BASE64_DECODE, null);
	}
}