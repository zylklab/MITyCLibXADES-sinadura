package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de firma enveloped.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformEnveloped extends Transform {
	/** Constructor. */
	public TransformEnveloped() {
		super(TransformProxy.TRANSFORM_ENVELOPED_SIGNATURE, null);
	}
}