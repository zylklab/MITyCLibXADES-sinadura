/**
 * LICENCIA LGPL:
 * 
 * Esta librería es Software Libre; Usted puede redistribuirlo y/o modificarlo
 * bajo los términos de la GNU Lesser General Public License (LGPL)
 * tal y como ha sido publicada por la Free Software Foundation; o
 * bien la versión 2.1 de la Licencia, o (a su elección) cualquier versión posterior.
 * 
 * Esta librería se distribuye con la esperanza de que sea útil, pero SIN NINGUNA
 * GARANTÍA; tampoco las implícitas garantías de MERCANTILIDAD o ADECUACIÓN A UN
 * PROPÓSITO PARTICULAR. Consulte la GNU Lesser General Public License (LGPL) para más
 * detalles
 * 
 * Usted debe recibir una copia de la GNU Lesser General Public License (LGPL)
 * junto con esta librería; si no es así, escriba a la Free Software Foundation Inc.
 * 51 Franklin Street, 5º Piso, Boston, MA 02110-1301, USA o consulte
 * <http://www.gnu.org/licenses/>.
 *
 * Copyright 2008 Ministerio de Industria, Turismo y Comercio
 * 
 */

package es.mityc.javasign.xml.xades.policy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.mityc.firmaJava.libreria.ConstantesXADES;
import es.mityc.javasign.ConstantsXAdES;
import es.mityc.javasign.i18n.I18nFactory;
import es.mityc.javasign.i18n.II18nManager;

/**
 * <p>Manager que gestiona las instancias de validadores de policies.</p>
 * 
 * <p>Obtiene los managers disponibles para la validación de políticas a través de los ficheros de configuración disponibles en
 * "<code>META-INF/xades/policy.properties</code>".</p>
 * 
 * <p>Los ficheros de propiedades han de cumplir el formato:
 * <pre>
 * # En este fichero se indica qué clases son las encargadas de validar policies
 * # específicas
 * # Formato:
 * #   &lt;clave&gt;=&lt;clase&gt;
 * # donde clave puede ser cualquier string que no contenga un código especial
 * # (por ejemplo un hash en hexadecimal de la policy), y clase es la clase
 * # que implementa el interfaz es.mityc.firmaJava.policy.IValidacionPolicy en
 * # el caso de un validador de políticas y el interfaz 
 * # es.mityc.firmaJava.policy.IFirmaPolicy en el caso de un escritor de
 * # políticas</pre>
 * </p>
 *
 * @author  Ministerio de Industria, Turismo y Comercio
 * @version 0.9 beta
 * 
 * TODO: implementar mecanismo para la búsqueda de los managers mediante el identificador y no únicamente por la clave textual.
 */
public class PoliciesManager { 
	
	/**
	 * <p>Clave para buscar el manager asociado a esa política.</p>
	 * @author  Ministerio de Industria, Turismo y Comercio
	 * @version 1.0
	 */
	public class PolicyKey {
		/** URI que identifica la política. */
		public URI uri;
		/** clave textual para identificar la política. */
		public String hash;
		/**
		 * Constructor.
		 * @param uri Uri identificativa de la política
		 * @param hash Clave textual identificativa de la política
		 */
		public PolicyKey(URI uri, String hash) {
			this.uri = uri;
			this.hash = hash;
		}
	}
	
	/**
	 * <p>Crea una nueva instancia de una clave identificadora de política.</p>
	 * @param uri Uri que identifica la política
	 * @param hash Clave textual que identifica la política
	 * @return Clave identificadora para buscar una política
	 */
	public PolicyKey newPolicyKey(URI uri, String hash) {
		return new PolicyKey(uri, hash);
	}
	
	/** Logger. */
	private static final Log logger = LogFactory.getLog(PoliciesManager.class);
	/** Internacionalizador. */
	private static final II18nManager i18n = I18nFactory.getI18nManager(ConstantsXAdES.LIB_NAME);
	
	/** Instancia del manager. */
	private static PoliciesManager instance;
	private Properties props = null;
	
	/** Nombre del fichero de propiedades para la configuración de los managers de política. */
	private final static String POLICY_FILE_CONF = "META-INF/xades/policy.properties";
	

	/**
	 * Constructor.
	 *
	 */
	private PoliciesManager() {
		// Carga los gestionadores de políticas
		loadManagers();
	}
	
	/**
	 * Carga los managers configurados en los ficheros de propiedades disponibles en 
	 * <code>META-INF/xades/policy.properties</code>
	 */
	private void loadManagers() {
		ClassLoader cl = getClassLoader();
		try {
			// cambia el orden del listado de recursos
			ArrayList<URL> resources = new ArrayList<URL>();
			Enumeration<URL> en = cl.getResources(POLICY_FILE_CONF);
			while (en.hasMoreElements()) {
				resources.add(0, en.nextElement());
			}
			// carga cada conjunto de propiedades de atrás hacia adelante para respetar el orden del classpath
			Properties base = null;
			Iterator<URL> itResources = resources.iterator();
			while (itResources.hasNext())
			{
				URL url = itResources.next();
				try {
					InputStream is = url.openStream();
					Properties properties = new Properties(base);
					properties.load(is);
					base = properties;
				} catch (IOException ex) {
					logger.error(i18n.getLocalMessage(ConstantsXAdES.I18N_POLICY_2, url, ex.getMessage()));
				}
			}
			props = base;
		} catch (IOException ex) {
			logger.error(i18n.getLocalMessage(ConstantsXAdES.I18N_POLICY_1, ex.getMessage()));
		}
		
	}
	
	private static ClassLoader getClassLoader() {
		try {
			ClassLoader cl = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
			    public ClassLoader run() {
					ClassLoader classLoader = null;
					try {
					    classLoader = Thread.currentThread().getContextClassLoader();
					} catch (SecurityException ex) {
					}
					return classLoader;
			    }});
			if (cl != null) {
				return cl;
			}
		} catch (Exception ex) {
		}
		return PoliciesManager.class.getClassLoader();
	}
	
	/**
	 * Para evitar problemas de sincronismo se instancia la primera vez que se referencia
	 */
	static {
		instance = getInstance();
	}
	
	/**
	 * Devuelve una instancia del manager de policies.
	 *  
	 * @return
	 */
	public static PoliciesManager getInstance() {
		if (instance == null) {
			instance = new PoliciesManager();
		}
		return instance;
	}
	
	/**
	 * Devuelve el validador de policy asociado a la clave indicada. Funciona como una factory que instancia un nuevo validador en cada
	 * llamada.
	 *  
	 * @param clave Clave que tiene asociada un validador
	 * @return Una instancia del validador de policy asociado o <code>null</code> si no hay ninguno asociado o no se puede instanciar.
	 * 
	 * TODO: permitir funcionar a la factory en varios modos de trabajo (instanciador, cache, singleton, instanciador propio del validador)
	 */
	public IValidacionPolicy getValidadorPolicy(PolicyKey clave) {
		return getValidadorPolicy(clave, true);
	}
	
	/**
	 * Devuelve el validador de policy asociado a la clave indicada. Funciona como una factory que instancia un nuevo validador en cada
	 * llamada.
	 *  
	 * @param clave Clave que tiene asociada un validador
	 * @param defaultManager indica si se debe devolver un PolicyManager que informe sobre la política aunque sea desconocida
	 * @return Una instancia del validador de policy asociado o <code>null</code> si no hay ninguno asociado o no se puede instanciar.
	 * 
	 * TODO: permitir funcionar a la factory en varios modos de trabajo (instanciador, cache, singleton, instanciador propio del validador)
	 */
	public IValidacionPolicy getValidadorPolicy(PolicyKey clave, boolean defaultManager) {
		IValidacionPolicy policyManager = null;
		if (props != null) {
			try {
				String classname = props.getProperty(clave.hash);
				if (classname != null) {
					try {
						ClassLoader cl = getClassLoader();
						Class<?> manager = null;
						if (cl != null) {
							manager = cl.loadClass(classname);
						} else {
							manager = Class.forName(classname);
						}
						if (manager != null) {
							policyManager = (IValidacionPolicy) manager.newInstance();
						}
					} catch (InstantiationException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_INSTANCIA + clave.hash + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (IllegalAccessException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_PERMISOS + clave.hash + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (ClassNotFoundException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_CLAVE + clave.hash + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (ClassCastException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_TIPO + clave.hash + ConstantesXADES.ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					}
					// Si no consigue un manager, al menos informa con el genérico
					if (policyManager == null)
						policyManager = new GeneralPolicyManager();
				}
			} catch (MissingResourceException ex) {
				logger.error(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_VALIDADOR + clave);
			}
			// Si no consigue un manager, al menos informa con el genérico
			if (policyManager == null)
				policyManager = new GeneralPolicyManager();
		}
		return policyManager;
	}

	/**
	 * Devuelve el escritor de policy asociado a la clave indicada. Funciona como una factory que instancia un nuevo escritor en cada
	 * llamada.
	 *  
	 * @param clave Clave que tiene asociada un escritor
	 * @return Una instancia del escritor de policy asociado o <code>null</code> si no hay ninguno asociado o no se puede instanciar.
	 * 
	 * TODO: permitir funcionar a la factory en varios modos de trabajo (instanciador, cache, singleton, instanciador propio del escritor)
	 */
	public IFirmaPolicy getEscritorPolicy(String clave) {
		IFirmaPolicy policyManager = null;
		if (props != null) {
			try {
				String classname = props.getProperty(clave);
				if (classname != null) {
					try {
						ClassLoader cl = getClassLoader();
						Class<?> manager = null;
						if (cl != null) {
							manager = cl.loadClass(classname);
						} else {
							manager = Class.forName(classname);
						}
						if (manager != null) {
							policyManager = (IFirmaPolicy) manager.newInstance();
						}
					} catch (InstantiationException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_INSTANCIA + clave + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (IllegalAccessException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_PERMISOS + clave + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (ClassNotFoundException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_CLAVE + clave + ConstantesXADES.COMA_ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					} catch (ClassCastException e) {
						logger.warn(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_TIPO + clave + ConstantesXADES.ESPACIO + classname + ConstantesXADES.CIERRA_PARENTESIS);
						if (logger.isDebugEnabled())
							logger.debug("", e);
					}
				}
			} catch (MissingResourceException ex) {
				logger.error(ConstantesXADES.LIBRERIAXADES_POLICY_MANAGER_NO_VALIDADOR + clave);
			}
		}
		return policyManager;
	}
}
