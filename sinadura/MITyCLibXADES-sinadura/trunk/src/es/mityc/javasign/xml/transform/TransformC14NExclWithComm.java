package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de canonicalización.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformC14NExclWithComm extends Transform {
	/** Constructor. */
	public TransformC14NExclWithComm() {
		super(TransformProxy.TRANSFORM_C14N_EXCL_WITH_COMMENTS, null);
	}
}