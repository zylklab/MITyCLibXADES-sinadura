package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de canonicalización.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformC14N11 extends Transform {
	/** Constructor. */
	public TransformC14N11() {
		super(TransformProxy.TRANSFORM_C14N11_OMIT_COMMENTS, null);
	}
}