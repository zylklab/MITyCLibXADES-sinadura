package es.mityc.firmaJava;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class TestIAKValidation extends ValidationBase {
	@Test public void validaIAIKBesValid() {
		assertTrue("Firma de IAIK, XAdES-BES no validada", validateStream(loadRes("/IAIK/BES.xml"), getBaseUri("/IAIK/"), null));
	}
	
	@Test public void validaIAIKCValid() {
		assertTrue("Firma de IAIK, XAdES-C no validada", validateStream(loadRes("/IAIK/XAdES-C-OCSP.xml"), getBaseUri("/IAIK/"), null));
	}

	@Test public void validaIAIKXValid() {
		assertTrue("Firma de IAIK, XAdES-X no validada", validateStream(loadRes("/IAIK/XAdES-X-OCSP-2.xml"), getBaseUri("/IAIK/"), null));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestIAKValidation.class);
	}

}
