package es.mityc.firmaJava.libreria.xades;


/**
 * Clase de configuracion estatica.
 *
 */
public class XadesConfigUtil {

	private static boolean checkNodeName = true;

	public static void setCheckNodeName(boolean checkNodeName) {
		XadesConfigUtil.checkNodeName = checkNodeName;
	}

	public static boolean isCheckNodeName() {
		return checkNodeName;
	}

}
	