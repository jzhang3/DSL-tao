package org.mondo.editor.ui.utils.dragdrop;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.mondo.editor.graphiti.diagram.utils.ModelUtils;
import org.mondo.editor.ui.utils.patterns.PatternUtils;

import dslPatterns.ClassInterface;
import dslPatterns.FeatureInterface;
import dslPatterns.FeatureType;
import dslPatterns.MMInterface;
import dslPatterns.ReferenceInterface;

/**
 * Model class to work with mminterfaces and their meta-model elements related.
 * 
 * @author miso partner AnaPescador
 *
 */
public class MMInterfaceRelDiagram {

	private MMInterface mmInterface;
	private String elementDiagram;
	private int order;
	private int orderPointer = 0;

	
	public MMInterface getMmInterface() {
		return mmInterface;
	}

	public void setMmInterface(MMInterface mmInterface) {
		this.mmInterface = mmInterface;
	}

	public String getElementDiagram() {
		return elementDiagram;
	}

	public void setElementDiagram(String elementDiagram) {
		this.elementDiagram = elementDiagram;
	}
	
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrderPointer() {
		return orderPointer;
	}

	public void setOrderPointer(int orderPointer) {
		this.orderPointer = orderPointer;
	}

	/**
	 * Method that return information about the inheritance within the pattern.
	 * @return a String with the information.
	 */
	public String getAdditionalInformation() {
		if (this.mmInterface instanceof ClassInterface){
			EClass eclass = PatternUtils.getEClass((ClassInterface)mmInterface);
			String cad ="";
			for (EClass parent: eclass.getESuperTypes()){
				if (cad.isEmpty()) cad += parent.getName(); 
				else cad += ","+parent.getName();
			}
			if (cad.isEmpty())
				if (eclass.getEPackage()!=null)
				for (EClass child:ModelUtils.getChildren(eclass.getEPackage(), eclass)){
					if (cad.isEmpty()) cad += child.getName(); 
					else cad += ","+child.getName();
				}
			return cad;
			
		}return "";
	}
	
	/**
	 * Method that return the FeatureType and ReferenceType EType.
	 * @return String 
	 */
	public String getEType(){
		if (this.mmInterface instanceof FeatureType){
			EAttribute att = PatternUtils.getEAttribute((FeatureType)mmInterface);
			if (att != null) return (att.getEType() != null ? ":"+att.getEType().getName():"");
		} else if (this.mmInterface instanceof ReferenceInterface){
			EReference ref = PatternUtils.getEReference((ReferenceInterface)mmInterface);
			if (ref != null) return (ref.getEType() != null ? ":"+ref.getEType().getName():"")+"["+(ref.getUpperBound() == -1 ? "*":ref.getUpperBound())+"]";
		}return "";	
	}


	/**
	 * Constructor
	 * @param mmInterface
	 * @param elementDiagram
	 */
	public MMInterfaceRelDiagram( MMInterface mmInterface, String elementDiagram) {
	    this.mmInterface = mmInterface;
	    this.elementDiagram = elementDiagram;
	    this.order = 0;
	}
	
	/**
	 * Constructor
	 * @param mmInterface
	 * @param elementDiagram
	 * @param order
	 */
	public MMInterfaceRelDiagram( MMInterface mmInterface, String elementDiagram, int order) {
	    this.mmInterface = mmInterface;
	    this.elementDiagram = elementDiagram;
	    this.order = order;
	}
	
	/**
	 * Constructor
	 * @param mmInterface
	 * @param elementDiagram
	 * @param order
	 * @param orderPoint
	 */
	public MMInterfaceRelDiagram( ReferenceInterface mmInterface, String elementDiagram, int order, int orderPoint) {
	    this.mmInterface = mmInterface;
	    this.elementDiagram = elementDiagram;
	    this.order = order;
	    this.orderPointer = orderPoint;
	}
	
	
	/**
	 * Method that returns the complete text about the interface.
	 * @return text
	 */
	public String getTextMMInterfaceRelDiagram (){
		if (mmInterface instanceof ClassInterface){
      		if (((ClassInterface)mmInterface).getRef().size() !=0){
      			EClass eClass = (EClass)((ClassInterface)mmInterface).getRef().get(0);	
      			return eClass.getName();
      		}
      	} else if (mmInterface instanceof FeatureInterface){
      		if (((FeatureInterface)mmInterface).getRef().size() !=0){
      			EAttribute eAttr = (EAttribute)((FeatureInterface)mmInterface).getRef().get(0);
      			return eAttr.getEContainingClass().getName()+"/"+eAttr.getName();
      		}
      	} else if (mmInterface instanceof ReferenceInterface)	{	
      		EReference eRef = (EReference)((ReferenceInterface)mmInterface).getRef();
      		return eRef.getEContainingClass().getName()+"/"+eRef.getName();
      	}return "";
	}
	
	/**
	 * Method that returns the text with only the interface name and it depends on the parameter, the EType.
	 * @param type - EType
	 * @return text
	 */
	public String getTextMMInterfaceRelDiagramOnlyName (boolean type){
		if (mmInterface instanceof ClassInterface){
      		if (((ClassInterface)mmInterface).getRef().size() !=0){
      			EClass eClass = (EClass)((ClassInterface)mmInterface).getRef().get(0);
      			return eClass.getName();
      		}
      	} else if (mmInterface instanceof FeatureInterface){
      		if (((FeatureInterface)mmInterface).getRef().size() !=0){
      			EAttribute eAttr = (EAttribute)((FeatureInterface)mmInterface).getRef().get(0);
      			String attName = eAttr.getName();
      			if (mmInterface instanceof FeatureType){
      			EAttribute att = PatternUtils.getEAttribute((FeatureType)mmInterface);
    			if (att != null) return attName+ (type?getEType():"");
      			}else return attName;
      		}
      	} else if (mmInterface instanceof ReferenceInterface)	{	
      		EReference eRef = (EReference)((ReferenceInterface)mmInterface).getRef();
      		String attRef = eRef.getName();
  			return attRef+ (type?getEType():"");
      	}return "";
	}
	
	/**
	 * Method that returns the parent name of the object.
	 * @return text
	 */
	public String getTextMMInterfaceRelDiagramParentName (){
		if (mmInterface instanceof FeatureInterface){
      		if (((FeatureInterface)mmInterface).getRef().size() !=0){
      			EAttribute eAttr = (EAttribute)((FeatureInterface)mmInterface).getRef().get(0);
      			return eAttr.getEContainingClass().getName();
      		}
      	} else if (mmInterface instanceof ReferenceInterface)	{	
      		EReference eRef = (EReference)((ReferenceInterface)mmInterface).getRef();
      		return eRef.getEContainingClass().getName();
      	}return "";
	}
	
	/**
	 * Method that returns the max cardinality of the object
	 * @return maxValue
	 */
	public int getMaxValue (){
		if (getMmInterface().getMaxCard() != null)
			return getMmInterface().getMaxCard().intValue();
		else return 0;
	}
	
	/**
	 * Method that returns the min cardinality of the object
	 * @return minValue
	 */
	public int getMinValue (){
		if (getMmInterface().getMinCard() != null)
			return getMmInterface().getMinCard().intValue();
		else return 0;
	}
	
	/**
	 * Method that returns an string with the cardinality text of the object
	 * @return text
	 */
	public String getCardText (){
		String cad = "";
		if (getMmInterface().getMinCard() != null){
			int value = getMmInterface().getMinCard().intValue();
			if (value==-1) cad = "(*";
			else cad = "("+value;
		}else cad = "(0";
		
		if (getMmInterface().getMaxCard() != null){
			int value = getMmInterface().getMaxCard().intValue();
			if (value==-1) cad += "..*)";
			else  cad += ".."+value+")";
		}else cad += "..0)";
		return cad;
	}

}
