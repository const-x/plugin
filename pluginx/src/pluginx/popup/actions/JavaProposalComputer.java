package pluginx.popup.actions;




import java.util.ArrayList;
import java.util.List;
//import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class JavaProposalComputer implements IJavaCompletionProposalComputer{

	@Override
	public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
//		try {
//			SynchronizableDocument document = (SynchronizableDocument)context.getDocument();
//			int cursorposition = context.getInvocationOffset();
//			String text = document.get();
//			String nowText = text.substring(0, cursorposition);
//			if(nowText.lastIndexOf("\n") != -1) {
//				nowText = nowText.substring(nowText.lastIndexOf("\n"), nowText.length());
//			}
//			// 处理类注释 整体
//			String forClassText = nowText + text.substring(cursorposition, cursorposition + 1);
//			if(forClassText.contains(" class ") || forClassText.contains(" interface ") 
//					|| forClassText.contains(" enum ") || forClassText.contains(" @interface ")) {
//				return AssistUtil.getClassAnnotation(context, cursorposition);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return proposals;
	}

	@Override
	public List<IContextInformation> computeContextInformation(ContentAssistInvocationContext arg0,
			IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sessionEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionStarted() {
		// TODO Auto-generated method stub
		
	}

}
