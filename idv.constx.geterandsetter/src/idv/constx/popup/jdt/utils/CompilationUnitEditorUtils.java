package idv.constx.popup.jdt.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;

public class CompilationUnitEditorUtils {

	static String breakChars = " \r\n\t,;{}[]()/*";
	
	static String[] fieldModifier = {"public", "protected", "private", "static", "final", "transient", "volatile"};
	
	public static String getSelectedWord(CompilationUnitEditor editor) throws BadLocationException{
		ISelection selected =editor.getSelectionProvider().getSelection();  
		if(selected == null){
			return null;
		}
		if (!(selected instanceof ITextSelection)) {
			return null;
		}
		ITextSelection st = (ITextSelection)selected;
		String text = st.getText().trim();  
		int offset = st.getOffset();
		IDocument document = editor.getViewer().getDocument();
		
		int begein = 0;
		int end = 0;
		for (int i =offset-1; i > 0 ; i--) {
			char pro = document.getChar(i);
            if(breakChars.indexOf(pro) != -1){
            	begein = i + 1;
            	break;
			}
		}
		
		for (int i =offset+text.length(); i < document.getLength() ; i++) {
			char pro = document.getChar(i);
            if(breakChars.indexOf(pro) != -1){
            	end = i;
            	break;
			}
		}
		
		return document.get(begein, end - begein);
	}
	
	
	public static String getFieldDoc(SourceField field,CompilationUnitEditor editor,ICompilationUnit unit) throws JavaModelException, BadLocationException{
		ISourceRange doc = field.getJavadocRange();
		IDocument document = editor.getViewer().getDocument();
		String comment = null;
		if(doc == null){
			ISourceRange fr = field.getSourceRange();
			List<String> ms = getFieldModifier(field,editor);
			String c = document.get(fr.getOffset(), fr.getLength());
			int end = 0;
			if(ms.isEmpty()){
				String type = getFieldType(field.getTypeSignature());
				end = c.indexOf(" "+type+" ");
				if(end == -1 ){
					end = c.indexOf("\t"+type+" ");
				}
				if(end == -1 ){
					end = c.indexOf("\n"+type+" ");
				}
			}else{
				end = c.indexOf(" "+ms.get(0)+" ");
				if(end == -1 ){
					end = c.indexOf("\t"+ms.get(0)+" ");
				}
				if(end == -1 ){
					end = c.indexOf("\n"+ms.get(0)+" ");
				}
			}
			c = c.substring(0, end);
			c = c.replaceAll("\r", " ");
			c = c.replaceAll("\n", " ");
			c = c.replaceAll("\t", " ");
			c = c.replaceAll("/", "");
			c = c.replaceAll("\\*", "");
			comment = c;
		}else{
			String c = document.get(doc.getOffset(), doc.getLength());
			c = c.replaceAll("\r", " ");
			c = c.replaceAll("\n", " ");
			c = c.replaceAll("\t", " ");
			c = c.replaceAll("/", "");
			c = c.replaceAll("\\*", "");
			comment = c.trim();
		}
		return comment;
	}
	
	public static List<String> getFieldModifier(IField field,CompilationUnitEditor editor) throws JavaModelException, BadLocationException {
		List<String> ms = new ArrayList<>();
		ISourceRange sr = field.getSourceRange();
		IDocument document = editor.getViewer().getDocument();
		String c = document.get(sr.getOffset(), sr.getLength());
		for (String string : fieldModifier) {
			if(c.contains(string)){
				ms.add(string);
			}
		}
		return ms;
	}
	
	public static String getFieldType(String typeSignature) throws JavaModelException {
		switch(typeSignature){
			case "I": return "int" ;
			case "J": return "long" ;
			case "D": return "double" ;
			case "S": return "short" ;
			case "F": return "float" ;
			case "C": return "char" ;
			case "B": return "byte" ;
			case "Z": return "boolean" ;
		}
		if(typeSignature.startsWith("Q") && typeSignature.endsWith(";")){
			typeSignature = typeSignature.substring(1, typeSignature.length() - 1);
		}
		if(typeSignature.contains("<")){
			StringBuilder sb = new StringBuilder();
			int begin = 0;
			boolean over = false;
			int cp = 0;
			for (int i = 0; i < typeSignature.length(); i++) {
				char ch = typeSignature.charAt(i);
				if(ch == '<'){
					if(!over){
						begin = i;
						over = true;
					}
					cp++;
				}else if(ch == '>'){
					cp--;
					if(cp == 0){
						int end = i;
						String ele = typeSignature.substring(begin+1, end);
						sb.append("<");
						sb.append(getFieldType(ele)).append(",");
						sb.deleteCharAt(sb.length()-1);
						sb.append(">");
						over= false;
					}
				}else if(!over){
					sb.append(ch);
				}
			}
			typeSignature = sb.toString();
			typeSignature = typeSignature.replaceAll(";Q", ",");
			return typeSignature;
		}
		return typeSignature;
	}
	
	
	public static FieldInfo getFieldDeclaration(IField field,CompilationUnitEditor editor,ICompilationUnit unit) throws JavaModelException, BadLocationException{
		FieldInfo info = new FieldInfo();
		info.setCommets(getFieldDoc((SourceField)field, editor, unit));
		info.setModifiers(getFieldModifier(field,editor));
		info.setName(field.getElementName());
		info.setType(getFieldType(field.getTypeSignature()));
		info.setField(field);
		return info;
	}
	
	public static void write(CompilationUnitEditor editor,ICompilationUnit originalUnit,Integer offset,Integer replaceLen, String content) throws JavaModelException, BadLocationException{
		if(offset == null || offset < 0){
			ISelection selected =editor.getSelectionProvider().getSelection();  
			if(selected != null && !selected.isEmpty()){
				ITextSelection st = (ITextSelection)selected;
				offset = st.getOffset() + st.getLength();
			}
		}
		if(replaceLen == null || replaceLen < 0){
			replaceLen = 0;
		}
		WorkingCopyOwner owner = new WorkingCopyOwner() {};
	    // Create working copy
	    ICompilationUnit workingCopy = originalUnit.getWorkingCopy(owner, null);
	    // Modify buffer and reconcile
	    IBuffer buffer = ((IOpenable)workingCopy).getBuffer();
	    if(offset < 0){
	    	buffer.append(content);
	    }else{
	    	buffer.replace(offset, replaceLen, content);
	    }
	    workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
	    // Commit changes
	    workingCopy.commitWorkingCopy(false, null);
	    // Destroy working copy
	    workingCopy.discardWorkingCopy();
	}
	
	
}
