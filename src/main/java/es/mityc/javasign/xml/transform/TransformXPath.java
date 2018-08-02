package es.mityc.javasign.xml.transform;

import es.mityc.javasign.xml.xades.TransformProxy;

/**
 * <p>Transformada de XPath.</p>
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 1.0
 */
public class TransformXPath extends Transform {
	
	/** Listado de paths de la transformada. */
	private XPathTransformData data = new XPathTransformData();
	
	/** Constructor. */
	public TransformXPath() {
		super(TransformProxy.TRANSFORM_XPATH, null);
		setTransformData(data);
	}
	
	/**
	 * <p>Incluye un path en los paths que aplicará esta transformada.</p>
	 * @param path
	 */
	public void addPath(String path) {
		data.addPath(path);
	}
}