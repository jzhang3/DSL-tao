package org.mondo.editor.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.mondo.editor.graphiti.diagram.utils.IResourceUtils;
import org.mondo.editor.graphiti.diagram.utils.ModelUtils;
import org.mondo.editor.ui.utils.dragdrop.MMInterfaceRelDiagram;
import org.mondo.editor.ui.utils.patterns.PatternUtils;
import org.mondo.editor.ui.utils.patterns.RuntimePatternsModelUtils;
import org.mondo.editor.ui.wizards.PatternWizard;

import dslPatterns.Category;
import dslPatterns.ComplexFeature;
import dslPatterns.Pattern;
import dslPatterns.PatternSet;

/**
 * View to work with patterns.
 * 
 * @author miso  partner AnaPescador
 *
 */

public class PatternsView extends ViewPart {
	
	public static final String ID = "org.mondo.editor.ui.views.PatternsView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action doubleClickAction;
	
	private EPackage ecoreDiagram = null;
	 
	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		private Pattern pattern;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public void setPattern(Pattern pattern) {
			this.pattern = pattern;
		}
		public Pattern getPattern() {
			return pattern;
		}
		
		public String toString() {
			return getName();
		}
		@SuppressWarnings("rawtypes")
		public Object getAdapter(Class key) {
			return null;
		}
		public boolean isLeaf(){
			return !(this instanceof TreeParent);
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList<TreeObject>();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}
		
	class ViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			invisibleRoot = null;
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot==null) initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}

		private void loadTree (TreeParent parent, Category cat){
			TreeParent catParent = new TreeParent(cat.getName());
			TreeObject to = null;
			if (cat.getSubcategories().size()==0){
				for (Pattern pat : cat.getPatterns()){
					to = new TreeObject(pat.getName());
					to.setPattern(pat);
					catParent.addChild(to);
				}
			} else {
				for (Category subCat: cat.getSubcategories()){
					loadTree (catParent, subCat);
				}
			}
			parent.addChild(catParent);
		}

		private void initialize() {	
			
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDiagramContainerUI editor = (IDiagramContainerUI)activePage.getActiveEditor();
			
			PatternSet patternModel = PatternUtils.getPatternSetModel(IResourceUtils.getProject(editor.getDiagramTypeProvider().getDiagram().eResource()));	
			if (patternModel != null){
				TreeParent root = new TreeParent("Patterns");
				for (Category cat: patternModel.getCategories()){
					loadTree (root, cat);
				}
				invisibleRoot = new TreeParent("");
				invisibleRoot.addChild(root);
			}
		}
	}
		
	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	/**
	 * The constructor.
	 */
	public PatternsView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.mondo.patterns.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PatternsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				TreeObject obj = (TreeObject)((IStructuredSelection)selection).getFirstElement();
				if (obj.isLeaf()) {
					Pattern pattern = obj.getPattern();
					ComplexFeature cf = pattern.getRootVariant();
					if (cf != null) {
						List<MMInterfaceRelDiagram> patternRelDiagram = new ArrayList<MMInterfaceRelDiagram>();
						IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDiagramContainerUI editor = (IDiagramContainerUI)activePage.getActiveEditor();
						
						int numIns = RuntimePatternsModelUtils.getNumPatternInstances(editor.getDiagramBehavior(), pattern);
						int maxNumIns = pattern.getMaxInstances();
						
						if ( (maxNumIns==-1) || (numIns<maxNumIns)){
						
							IProject project= IResourceUtils.getProject(editor.getDiagramTypeProvider().getDiagram().eResource());
							
							WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new PatternWizard(pattern, ecoreDiagram, patternRelDiagram, project))
							{
								@Override
						        protected void configureShell(Shell newShell) {
								super.configureShell(newShell);
								newShell.setSize(950, 675);
								}
							};
							dialog.create();
							
							dialog.getShell().setText("PATTERNS");
							int result = dialog.open();
							if ((result == Window.OK)&&(patternRelDiagram.size()!=0)){
								PatternUtils.applyPattern(patternRelDiagram, editor.getDiagramBehavior(), pattern);	
							}	
						} else showMessage("Max. number of pattern instances");
					}
					else showMessage("Pattern not available");				    
				}
				else showMessage("Select a pattern, please.");

			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Pattern View",
			message);
	}
	
	public void setFocus() {
		refresh();
	}
	
	public void refresh(){
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage.getActiveEditor() instanceof IDiagramContainerUI){
			IEditorPart editor = activePage.getActiveEditor();	
			if (editor instanceof IDiagramContainerUI){
				if (viewer!= null){
					Diagram diagram = ((IDiagramContainerUI)editor).getDiagramBehavior().getDiagramTypeProvider().getDiagram();
					if (ModelUtils.existsPackage(diagram))
						ecoreDiagram = ModelUtils.getBusinessModel(diagram);
					viewer.setInput(getViewSite());
					
					viewer.getControl().setFocus();
				}
			}		
		} else viewer.setInput(null);
	}

}