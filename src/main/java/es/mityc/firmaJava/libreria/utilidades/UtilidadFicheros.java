/**
 * 
 */
package es.mityc.firmaJava.libreria.utilidades;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author dsantose
 *
 */
public class UtilidadFicheros {
	
	public static byte[] readFile(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int length = (int)file.length();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
			byte[] buffer = new byte[4096];
			int i = 0;
			while (i < length) {
				int j = fis.read(buffer);
				baos.write(buffer, 0, j);
				i += j;
			}
			return baos.toByteArray();
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
				}
			}
		}
		return null;
	}
	
    /**
     * <p>Devuelve la ruta a un fichero relativa a la base indicada.</p> 
     * @param baseUri Base sobre la que se relativiza la ruta
     * @param file Fichero del que se calcula la ruta
     * @return ruta relativizada
     */
	public static String relativizeRute(String baseUri, File file) {
		String strFile = null;
    	try {
			URI relative = new URI(URIEncoder.encode(baseUri, "UTF-8"));
			URI uri = file.toURI();//new URI("file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8"));
			System.out.println("file a uri: " + uri);
			strFile = URIEncoder.relativize(relative.toString(), uri.toString());
		} catch (UnsupportedEncodingException e) {
//			try {
				strFile = file.toURI().toString();//"file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
//			} catch (UnsupportedEncodingException ex) {
//				// TODO: lanzar aviso
//			}
		} catch (URISyntaxException e) {
//			try {
				strFile = file.toURI().toString();//"file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
//				strFile = "file:///" +  URIEncoder.encode(file.getAbsolutePath().replace("\\", "/"), "UTF-8");
//			} catch (UnsupportedEncodingException ex) {
//				// TODO: lanzar aviso
//			}
		}
		
    	return strFile;
    }


}
