package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de canonicalización.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformC14N11WithComm extends Transform {
	/** Constructor. */
	public TransformC14N11WithComm() {
		super(TransformProxy.TRANSFORM_C14N11_WITH_COMMENTS, null);
	}
}