package pluginx.popup.actions;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class EditorAction implements IEditorActionDelegate {

	private Shell shell;

	public IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	@Override
	public void run(IAction action) {
		 IJavaElement selected= JavaUI.getEditorInputJavaElement(getEditor().getEditorInput());
		 if(!(selected instanceof ICompilationUnit) ){
			 return;
		 }
		 ICompilationUnit unit = (ICompilationUnit)selected;
		 try {
			 SourceType type = (SourceType) unit.getAllTypes()[0];
			 IBuffer buffer = unit.getBuffer();
			 IMethod[] methods = type.getMethods();
			 IBuffer buffer1 = unit.getBuffer();
			 for(int i=0; i<methods.length;i++){
			 IMethod method =methods[i];
			 ISourceRange range = method.getJavadocRange();//获取JavaDoc的范围区间
			 if(range != null){
			 System.out.println(range.getOffset() + " " + range.getLength());
			 System.out.println(buffer1.getText(range.getOffset(),range.getLength()));;
			 }
			 }
			 } catch (JavaModelException e) {
			 e.printStackTrace();
			 }
	}

	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart targetEditor) {
		shell = targetEditor.getSite().getShell();
	}

}
