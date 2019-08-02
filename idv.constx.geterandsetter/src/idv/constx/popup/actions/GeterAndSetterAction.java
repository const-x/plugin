package idv.constx.popup.actions;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import idv.constx.popup.jdt.utils.CompilationUnitEditorUtils;
import idv.constx.popup.jdt.utils.FieldInfo;

public class GeterAndSetterAction implements IEditorActionDelegate {

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
			IDocument document = editor.getViewer().getDocument();
			
			int offset = 0;
			ISelection selected =editor.getSelectionProvider().getSelection();  
			if(selected != null && !selected.isEmpty()){
				ITextSelection st = (ITextSelection)selected;
				offset = st.getOffset();
			}
			
			IType type = unit.getAllTypes()[0];
			IField[] fields = type.getFields();
			IMethod[] methods = type.getMethods();
			
			HashMap<String,IMethod> methodMap = new HashMap<>();
			for (IMethod method : methods) {
				String[] params = method.getParameterTypes();
				if(params.length == 1){
					methodMap.put(method.getElementName() +"-"+ CompilationUnitEditorUtils.getFieldType(params[0]), method);
				}else if(params.length == 0){
					System.out.println(method.getElementName() );
					methodMap.put(method.getElementName(), method);
				}
			}
			ArrayList<FieldInfo> fieldInfos = new ArrayList<>();
			for (IField f : fields) {
				if(f == null || Flags.isStatic(f.getFlags()) || Flags.isSynchronized(f.getFlags()) || Flags.isFinal(f.getFlags()) || ((IField) f).isEnumConstant() || !((IField) f).exists()){
					continue;
				}
				fieldInfos.add(CompilationUnitEditorUtils.getFieldDeclaration(f, editor, unit));
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			for (FieldInfo f : fieldInfos) {
				String upper = 	f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
				String set = "set" + upper  +"-"+f.getType();
				String get = "get" + upper;
				String alias = f.getCommets() == null?f.getName():f.getCommets();
				
				if(!methodMap.containsKey(set)){
					sb.append("\n");
					sb.append(this.createSetter(f.getName(), upper, f.getType(), alias));
				}
				if(!methodMap.containsKey(get)){
					sb.append("\n");
					sb.append(this.createGetter(f.getName(), upper, f.getType(), alias));
				}
			}
			CompilationUnitEditorUtils.write(editor,unit, offset,0, sb.toString());
			
			
			for (FieldInfo f : fieldInfos) {
				String upper = 	f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
				String set = "set" + upper  +"-"+f.getType();
				String get = "get" + upper;
				String alias = f.getCommets() == null?f.getName():f.getCommets();
				
				if(f.getField().getJavadocRange() == null){
					String modifier = f.getModifiers().isEmpty() ? "private":f.getModifiers().get(0);
					String fs = "/** "+alias+ "*/\n\t"+modifier+" "+ f.getType() + " "+f.getName()+";";
					CompilationUnitEditorUtils.write(editor,unit, f.getField().getSourceRange().getOffset(),f.getField().getSourceRange().getLength(), fs);
				}
				if(methodMap.containsKey(set)){
					IMethod method = methodMap.get(set);
					String m = document.get(method.getSourceRange().getOffset(), method.getSourceRange().getLength());
					if(method.getJavadocRange() == null && !m.startsWith("/")){
						String setter = this.createSetter(f.getName(), upper, f.getType(), alias);
						setter = setter.substring(1);
						CompilationUnitEditorUtils.write(editor,unit, method.getSourceRange().getOffset(),method.getSourceRange().getLength(), setter);
					}
				}
				if(methodMap.containsKey(get)){
					IMethod method = methodMap.get(get);
					String m = document.get(method.getSourceRange().getOffset(), method.getSourceRange().getLength());
					if(method.getJavadocRange() == null && !m.startsWith("/")){
						String getter = this.createGetter(f.getName(), upper, f.getType(), alias);
						getter = getter.substring(1);
						CompilationUnitEditorUtils.write(editor,unit, method.getSourceRange().getOffset(),method.getSourceRange().getLength(), getter);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	protected  String createSetter(String field,String methodName, String type, String alias) {
		String setField = "value";
		StringBuilder str = new StringBuilder();
		str.append("\t/** \n\t* 设置 " + alias + "\n\t* @param " + type + "\n\t*/ \n");
		str.append("\tpublic void set" + methodName + "(" + type + " " + setField + ") {\n");
		str.append("\t\tthis." + field + " = " + setField + "; \n");
		str.append("\t} \n");
		return str.toString();
	}	
	
	protected  String createGetter(String field,String methodName, String type, String alias) {
		StringBuilder str = new StringBuilder();
		if (type.equalsIgnoreCase("boolean")) {
			str.append("\t/** \n\t * 是否 " + alias + "\n\t * @return " + type + " \n\t */ \n");
			str.append("\tpublic " + type + " get" + methodName + "() {\n");
			str.append("\t\treturn  this." + field + "; \n");
			str.append("\t} \n");
		} else {
			str.append("\t/** \n\t * 获取 " + alias + "\n\t * @return " + type + " \n\t */ \n");
			str.append("\tpublic " + type + " get" + methodName + "() {\n");
			str.append("\t\treturn  this." + field + "; \n");
			str.append("\t} \n");
		}
		return str.toString();
	}
	
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
	}

	@Override
	public void setActiveEditor(IAction arg0, IEditorPart targetEditor) {
		shell = targetEditor.getSite().getShell();
	}

}
