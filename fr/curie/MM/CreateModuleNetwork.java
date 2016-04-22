package fr.curie.MM;

import java.util.*;
import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.*;
import org.cytoscape.view.model.*;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Create a new network composed of modules from networks selected in the network panel
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class CreateModuleNetwork extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final static String title="Create Network of Modules";
	public CreateModuleNetwork(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public static void gridLayout(CyNetworkView networkView) {
	    Iterator<CyNode> i=networkView.getModel().getNodeList().iterator();	    
	    final int xOffset=64;
	    final int yOffset=64;
	    final int maxByCol=8;
	    int row_num=0;
	    int col_num=0;
	    while (i.hasNext()){
			CyNode node = (CyNode) i.next();
		    View<CyNode> nodeView=networkView.getNodeView(node);
		    double newY=row_num*yOffset;
		    double newX=col_num*xOffset;
		    nodeView.setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,newX);
        	nodeView.setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,newY);    				
			col_num++;			
			if (col_num==maxByCol) {
			    col_num=0;
			    row_num++;
			}			
	    }
	}
	public void actionPerformed(ActionEvent e){
		// Define variables required to call Cytoscape API (similar to bundle services)
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CyNetworkFactory networkFactory=ModuleManager_App.getAdapter().getCyNetworkFactory();
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CyNetworkViewFactory viewFactory=ModuleManager_App.getAdapter().getCyNetworkViewFactory();
		CyNetworkViewManager viewManager=ModuleManager_App.getAdapter().getCyNetworkViewManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		ModuleUtils utils=new ModuleUtils();// class which contains common functions
		// List of networks selected at least 2
		List<CyNetwork> selectedNets=utils.selectNetsFromPanel(applicationManager,swingApplication.getJFrame());
		if(selectedNets==null) return;
		// Create the modular network, check if no synonym exist and if columns giving module information are ok
		CyNetwork modularNet=networkFactory.createNetwork();		
		utils.checkModuleColumn(modularNet);
		modularNet.getRow(modularNet).set(CyNetwork.NAME,utils.noSynonymInNets(networkManager,"Modular"));
		networkManager.addNetwork(modularNet);
		for(CyNetwork netNode:selectedNets) utils.addModule(modularNet,netNode); // add modules
		// create view with grid layout
		CyNetworkView view=viewFactory.createNetworkView(modularNet);
		viewManager.addNetworkView(view);
		gridLayout(view);
		view.updateView();
	}
}
