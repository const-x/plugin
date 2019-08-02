package idv.constx.handler;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenExplorerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelection sel = window.getSelectionService().getSelection();
		if (sel instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) sel).getFirstElement();
			String path = null;
			// common resource file
			if (obj instanceof IFile) {
				IResource resource = (IResource) obj;
				path = resource.getLocation().toOSString();
			}
			// other resource such as folder,project
			else if (obj instanceof IResource) {
				IResource resource = (IResource) obj;
				path = resource.getLocation().toOSString();
			}
			// explorer java element, containfield,method,package
			else if (obj instanceof IJavaElement) {
				IResource res = ((IJavaElement) obj).getResource();
				if(res != null){
					path = res.getLocation().toOSString();
				}else{
					// jar resource is null
					if (obj instanceof JarPackageFragmentRoot) {
						path = ((IPackageFragmentRoot) obj).getPath().toOSString();
					} else if (obj instanceof IPackageFragmentRoot) {
						String prjPath = ((IPackageFragmentRoot) obj).getJavaProject().getProject().getParent()
								.getLocation().toOSString();
						path = prjPath + ((IPackageFragmentRoot) obj).getPath().toOSString();
					} else if (obj instanceof IPackageFragment) {// other : package
						IResource resource = ((IPackageFragment) obj).getResource();
						path = resource.getLocation().toOSString();
					} else if (obj instanceof ClassFile) {// other : package
						IJavaElement root  = ((ClassFile) obj).getParent();
						path = root.getPath().toOSString();
					} 
				}
			}
			if (path != null) {
				try {
					Runtime.getRuntime().exec("explorer /select, " + path); 
				} catch (IOException e) {
					//
				}
			}
		}
		return null;
	}

}