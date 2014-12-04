/**
 */
package dslPatterns;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link dslPatterns.Pattern#getRootVariant <em>Root Variant</em>}</li>
 *   <li>{@link dslPatterns.Pattern#getName <em>Name</em>}</li>
 *   <li>{@link dslPatterns.Pattern#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @see dslPatterns.DslPatternsPackage#getPattern()
 * @model
 * @generated
 */
public interface Pattern extends EObject {
	/**
	 * Returns the value of the '<em><b>Root Variant</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root Variant</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root Variant</em>' containment reference.
	 * @see #setRootVariant(ComplexFeature)
	 * @see dslPatterns.DslPatternsPackage#getPattern_RootVariant()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ComplexFeature getRootVariant();

	/**
	 * Sets the value of the '{@link dslPatterns.Pattern#getRootVariant <em>Root Variant</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root Variant</em>' containment reference.
	 * @see #getRootVariant()
	 * @generated
	 */
	void setRootVariant(ComplexFeature value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see dslPatterns.DslPatternsPackage#getPattern_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link dslPatterns.Pattern#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Description</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see dslPatterns.DslPatternsPackage#getPattern_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link dslPatterns.Pattern#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

} // Pattern