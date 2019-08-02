package idv.constx.popup.actions;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import idv.constx.popup.jdt.utils.CompilationUnitEditorUtils;

public class ToStringAction implements IEditorActionDelegate {

	private Shell shell;

	
	
	public IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	@Override
	public void run(IAction action) {
		try {
			IJavaElement element = JavaUI.getEditorInputJavaElement(getEditor().getEditorInput());
			if (!(element instanceof ICompilationUnit)) {
				return;
			}
			CompilationUnitEditor editor = (CompilationUnitEditor)getEditor();
			ICompilationUnit unit = (ICompilationUnit) element;
			
			IType type = unit.getAllTypes()[0];
			IField[] fields = type.getFields();
			IMethod[] methods = type.getMethods();
			IMethod toString = null;
			for (IMethod iMethod : methods) {
				if(iMethod.getElementName().equals("toString") || iMethod.getParameterTypes().length == 0){
					toString = iMethod;
				}
			}
			StringBuilder sb = new StringBuilder();
			if(toString == null){
				sb.append("\n\t");
			}
			sb.append("@Override\n\tpublic String toString() {\n\t\treturn \"{\" +");
			int counter = 0;
			int max = 0;
			for (IField f : fields) {
				if(f == null || Flags.isStatic(f.getFlags()) || Flags.isSynchronized(f.getFlags()) || Flags.isFinal(f.getFlags()) || ((IField) f).isEnumConstant() || !((IField) f).exists()){
					continue;
				}
				if(f.getElementName().length() > max){
					max = f.getElementName().length();
				}
			}

			int curLine = 0;
			for (IField f : fields) {
				if(f == null || Flags.isStatic(f.getFlags()) || Flags.isSynchronized(f.getFlags()) || Flags.isFinal(f.getFlags()) || ((IField) f).isEnumConstant() || !((IField) f).exists()){
					continue;
				}
				String name = f.getElementName();
				String upper = 	name.substring(0, 1).toUpperCase() + name.substring(1);
				String ele = "\"\\\""+name+"\\\":\""+" + "+name+" + "+"\",\""+" + ";
				if(counter%2 == 0){
					sb.append("\n\t\t\t       ");
					curLine = ele.length();
				}else{
					int s = max*2 + 20 - curLine;
					for (int i = 0; i < s; i++) {
						sb.append(" ");
					}
				}
				counter++;
				sb.append(ele);
			}
			sb.append("\n\t\t       \"}\";\n\t}");
			if(toString != null){
				CompilationUnitEditorUtils.write(editor,unit, toString.getSourceRange().getOffset(),toString.getSourceRange().getLength(), sb.toString());
			}else{
				sb.append("\n");
				CompilationUnitEditorUtils.write(editor,unit, -1,0, sb.toString());
			}
			
		} catch (Exception e) {
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
