package org.mondo.editor.ui.utils.dragdrop;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.mondo.editor.graphiti.diagram.utils.DataTypeUtils.DataType;
import org.mondo.editor.ui.utils.ModelsUtils;
import org.mondo.editor.ui.utils.patterns.PatternUtils;

import dslPatterns.ClassInterface;
import dslPatterns.FeatureInstance;
import dslPatterns.FeatureType;
import dslPatterns.ReferenceInterface;

/**
 * Class that implement a listener for drop operations
 * 
 * @author miso partner AnaPescador
 *
 */
public class MyDropListener extends ViewerDropAdapter {

	private final Viewer viewer;
	private final EPackage modelPack;
	private List<MMInterfaceRelDiagram> content;


	  public MyDropListener(Viewer viewer, EPackage pack, List<MMInterfaceRelDiagram> content) {
	    super(viewer);
	    this.viewer = viewer;
	    this.modelPack = pack;
	    this.content = content;
	  }
	  
	  public void setContent(List<MMInterfaceRelDiagram> content){
		  this.content = content;
	  }

	  @Override
	  public void drop(DropTargetEvent event) {
		MMInterfaceRelDiagram target = (MMInterfaceRelDiagram) determineTarget(event);
	    
		if (target.getMmInterface() instanceof ClassInterface){
			for (MMInterfaceRelDiagram mmird : PatternUtils.getChildren(content, target)){
				if (!(mmird.getMmInterface() instanceof FeatureInstance)) mmird.setElementDiagram("");
			}
			 
			for (MMInterfaceRelDiagram mmird : PatternUtils.getMMInterfaceRelDiagramRefsEClass(content, (ClassInterface)target.getMmInterface() , target.getOrder())){
				 mmird.setElementDiagram("");
			}
		}
		
		target.setElementDiagram(event.data.toString());
	    
	    super.drop(event);
	  }


	  @Override
	  public boolean performDrop(Object data) {
		  viewer.refresh();
		  return false;
	  }

	  @Override
	  public boolean validateDrop(Object target, int operation,TransferData transferType) {
		  Object data =  TextTransfer.getInstance().nativeToJava(transferType);
		  EObject object = ModelsUtils.getEObject(modelPack,data.toString());
		 
		  if (target instanceof MMInterfaceRelDiagram){
			  String parentName = ((MMInterfaceRelDiagram)target).getTextMMInterfaceRelDiagramParentName();
			  MMInterfaceRelDiagram parent = PatternUtils.getMMInterfaceRelDiagram(content, parentName, ((MMInterfaceRelDiagram)target).getOrder());
			  
			  String[] datas = data.toString().split("/");
			  
			  EClass eClassParent = (EClass)ModelsUtils.getEObject(modelPack, datas[0]);
			  EList<EClass> eClassesParent =eClassParent.getEAllSuperTypes();
			  EClass eClassParentRel = null;
			  if (parent != null) 
				  eClassParentRel = (EClass)ModelsUtils.getEObject(modelPack,parent.getElementDiagram());
			 
			  if (((((MMInterfaceRelDiagram)target).getMmInterface() instanceof ClassInterface)&&(object instanceof EClass))
				    	|| ( ((((MMInterfaceRelDiagram)target).getMmInterface() instanceof FeatureType) &&(object instanceof EAttribute)) && (!parent.getElementDiagram().isEmpty())
				    			&&((datas[0].compareTo(parent.getElementDiagram())==0)||(eClassesParent.contains(eClassParentRel)))) 
				    	|| ( ((((MMInterfaceRelDiagram)target).getMmInterface() instanceof ReferenceInterface) &&(object instanceof EReference)) && (!parent.getElementDiagram().isEmpty()) && (!parent.getElementDiagram().isEmpty())
				    			&&((datas[0].compareTo(parent.getElementDiagram())==0) || (eClassesParent.contains(eClassParentRel))))){		  	
				  if ((((MMInterfaceRelDiagram)target).getMmInterface() instanceof ReferenceInterface) &&(object instanceof EReference)){
					  return (PatternUtils.isETypeTarget(((ReferenceInterface)((MMInterfaceRelDiagram)target).getMmInterface()), (EClass)((EReference)object).getEType(), content, modelPack, ((MMInterfaceRelDiagram)target).getOrderPointer())
							  &&(PatternUtils.areCompatibleReferences((ReferenceInterface)((MMInterfaceRelDiagram)target).getMmInterface(), (EReference)object)   
								&& (PatternUtils.areCompatibleEOppositeReferences(content, (MMInterfaceRelDiagram)target, (EReference)object)) ));
				  }
				  if (((MMInterfaceRelDiagram)target).getMmInterface() instanceof FeatureType){
					  if (PatternUtils.getEType((FeatureType)((MMInterfaceRelDiagram)target).getMmInterface()) == DataType.EJAVAOBJECT.getEDataType()) return true;
					  else return PatternUtils.getEType((FeatureType)((MMInterfaceRelDiagram)target).getMmInterface()) == ((EAttribute)object).getEAttributeType();
				  
				  } if (((MMInterfaceRelDiagram)target).getMmInterface() instanceof ClassInterface)	
					  return (PatternUtils.areCompatibleClasses(content, (MMInterfaceRelDiagram)target, (EClass) object));		  
				  else return true;
			  } else return false;
		  }
		  return false;
	  }
}