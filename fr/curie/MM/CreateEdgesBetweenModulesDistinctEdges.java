package fr.curie.MM;

import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
/**
 * Create edges between modules compacting edges from the reference network
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class CreateEdgesBetweenModulesDistinctEdges   extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="Create Edges between Modules > Similar Edges Compacted";
	public CreateEdgesBetweenModulesDistinctEdges(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		CreateEdgesBetweenModules cebm=new CreateEdgesBetweenModules(applicationManager,networkManager,swingApplication);
		cebm.actionPerformed(title,false);
	}
}
