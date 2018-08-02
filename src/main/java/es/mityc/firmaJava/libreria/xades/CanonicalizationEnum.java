package es.mityc.firmaJava.libreria.xades;

import org.apache.xml.security.transforms.Transforms;

public enum CanonicalizationEnum {
	
	UNKNOWN("unknown"),
	C14N_OMIT_COMMENTS(Transforms.TRANSFORM_C14N_OMIT_COMMENTS),
	C14N_WITH_COMMENTS(Transforms.TRANSFORM_C14N_WITH_COMMENTS),
	C14N_EXCL_OMIT_COMMENTS(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS),
	C14N_EXCL_WITH_COMMENTS(Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
	
	private String value;
	
	private CanonicalizationEnum(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
	
	public static CanonicalizationEnum getCanonicalization(String value) {
		if (value != null) {
			if (Transforms.TRANSFORM_C14N_OMIT_COMMENTS.equals(value))
				return C14N_OMIT_COMMENTS;
			else if (Transforms.TRANSFORM_C14N_WITH_COMMENTS.equals(value))
				return C14N_WITH_COMMENTS;
			else if (Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS.equals(value))
				return C14N_EXCL_OMIT_COMMENTS;
			else if (Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS.equals(value))
				return C14N_EXCL_WITH_COMMENTS;
		}
		return UNKNOWN;
	}

}
