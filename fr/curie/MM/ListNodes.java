package fr.curie.MM;
import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * List nodes in the current network
 * If an node points to a network (nested network), list nodes inside the network
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ListNodes extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final static String title="List Nodes & Within Nodes";
	public ListNodes(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		String text="Nodes\tWithin_Nodes\r\n";			
		for(CyNode node:network.getNodeList())
			if(node.getNetworkPointer()==null) text=text+network.getRow(node).get(CyNetwork.NAME,String.class)+"\r\n"; 
			else for(CyNode nodeIn:node.getNetworkPointer().getNodeList())  text=text+network.getRow(node).get(CyNetwork.NAME,String.class)+
					"\t"+node.getNetworkPointer().getRow(nodeIn).get(CyNetwork.NAME,String.class)+"\r\n";
		new TextBox(swingApplication.getJFrame(),title+" of "+network.getRow(network).get(CyNetwork.NAME,String.class),text).setVisible(true);
	}
}