package org.mondo.editor.extensionpoints;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import runtimePatterns.PatternInstance;
import runtimePatterns.PatternInstances;

import dslPatterns.MMInterface;

/**
 * Class of utility functions to evaluate extensions.
 * 
 * @author miso partner AnaPescador
 *
 */
public class EvaluateExtensionPoint {

	private static final String IPATTERN_IMPLEMENTATION_ID ="org.mondo.editor.extensionpoints.patternImplementation";
	private static final String IPATTERN_IMPLEMENTATION_ATTRIBUTE = "pattern";
	
	/**
	 * Method that searches the plug-ins that implement the functionality and execute the associated
	 * @param pi - pattern implementation
	 * @param patterns - pattern instances
	 * @param patternName -  name of the pattern.
	 * @param ePack - meta-model initial package
	 * @param iPath - IPath diagram 
	 * @return information about the validation and success
	 */
	public static ExecuteInfo evaluateExecutePattern(IPatternImplementation pi,EObject patterns, String patternName, EPackage ePack, IPath iPath) {
		if (patterns instanceof PatternInstances){
			PatternInstance pattern = getPatternInstance((PatternInstances)patterns, patternName);
			if ((pi!= null)&&(pattern !=null)) return executeExecuteExtension(pi, ePack, (PatternInstance)pattern, iPath);
		}
		return null;
		
	}
	
	/**
	 * Method that searches the plug-ins that implement the functionality and execute "validate pattern."
	 * @param pi - pattern implementation
	 * @param patterns - pattern instances.
	 * @param patternName - name of the pattern.
	 * @param ePack - meta-model initial package.
	 * @return information about the validation
	 */
	public static ValidationInfo evaluateValidatePattern(IPatternImplementation pi, EObject patterns, String patternName, EPackage ePack) {
		if (patterns instanceof PatternInstances){
			PatternInstance pattern = getPatternInstance((PatternInstances)patterns, patternName);
			if ((pi!= null)&&(pattern !=null)) return executeValidateExtension(pi, ePack, (PatternInstance)pattern);
		}
		return null;
	}
	
	/**
	 * Static method that returns instance of pattern given by its name.
	 * @param patterns - pattern instances
	 * @param name - name of the pattern
	 * @return pattern instance object
	 */
	private static PatternInstance getPatternInstance(PatternInstances patterns, String name){
		for (PatternInstance pattern: patterns.getAppliedPatterns()){
			if (pattern.getIdent().compareToIgnoreCase(name)==0) return pattern;
		}
		return null;
	}
	
	
	/**
	 * Method that searches the plug-ins that implement the functionality and execute "getOptimalElements"
	 * @param registry - extension registry
	 * @param pattern - pattern name
	 * @param ePack  - meta-model initial package
	 * @param mmInterface - mmInterface target
	 * @return List of optimal elements.
	 */
	public static List<ENamedElement> evaluateGetOptimalElements(IExtensionRegistry registry, String pattern, EPackage ePack,MMInterface mmInterface) {
		IPatternImplementation o = getInstanceIPattern(registry, pattern);
		if (o!= null) return executeGetOptimalElementsExtension(o, ePack,  mmInterface);
		else return null;
	}
	
	/**
	 * Method that returns the instance that implements the interface "IPattern"
	 * @param registry - extension registry
	 * @param pattern - pattern name of the extension.
	 * @return IPatternImplementation
	 */
	public static IPatternImplementation getInstanceIPattern(IExtensionRegistry registry, String pattern){
		IConfigurationElement[] config = registry.getConfigurationElementsFor(IPATTERN_IMPLEMENTATION_ID);
		pattern = pattern.replaceAll("\\d","");
		try {
			for (IConfigurationElement e : config) {				
				if (e.getAttribute(IPATTERN_IMPLEMENTATION_ATTRIBUTE).compareTo(pattern)==0){
					final Object o = e.createExecutableExtension("class");
					if (o instanceof IPatternImplementation) {		
						//one extension
						return (IPatternImplementation)o;
					}
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	/**
	 * Method that executes the method validation and, if it isn't errors, execute the method "execute" of the extension and returns the information about the execution.
	 * @param o - instance that implements the interface "IPattern"
	 * @param ePack - meta-model initial package.
	 * @param pattern - pattern instance
	 * @param iPath - IPath diagram
	 * @return CreationInfo
	 */
	private static ExecuteInfo executeExecuteExtension(final Object o,final EPackage ePack, PatternInstance pattern, IPath iPath) {
		ExecutePatternRunnable runnable = new ExecutePatternRunnable((IPatternImplementation)o, ePack, pattern, iPath);
		SafeRunner.run(runnable);
		return runnable.getCreationInfo();
		
	}
	
	/**
	 * Method that executes the method validation of the extension and returns the information about the validation.
	 * @param o - instance that implements the interface "IPattern"
	 * @param ePack - meta-model initial package.
	 * @param patterns - pattern instances
	 * @return ValidationInfo
	 */
	private static ValidationInfo executeValidateExtension(final Object o,final EPackage ePack, PatternInstance pattern) {
		ValidatePatternRunnable runnable = new ValidatePatternRunnable((IPatternImplementation)o, ePack, pattern);
		SafeRunner.run(runnable);
		return runnable.getValidationInfo();
	}
	
	/**
	 * Method that executes the method getOptimalElements of the extension and returns a list with the elements.
	 * @param o - instance that implements the interface "IPattern"
	 * @param ePack - meta-model initial package.
	 * @param mminterface MMInterface target
	 * @return List<ENamedElement>
	 */
	private static List<ENamedElement> executeGetOptimalElementsExtension(final Object o,final EPackage ePack, MMInterface mminterface) {
		GetOptimalElementsRunnable runnable = new GetOptimalElementsRunnable((IPatternImplementation)o, ePack, mminterface);
		SafeRunner.run(runnable);
		return runnable.getOptimalElements();
		
	}
	

}