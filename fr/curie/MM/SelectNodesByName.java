package fr.curie.MM;
/*
BiNoM Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * Select nodes by a name list
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class SelectNodesByName extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="Select Nodes by a Name List";
	CyNetwork network;
	ModuleUtils utils;
	public SelectNodesByName(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	void setSelected(String nodeName){
		CyNode node=utils.getCyNode(network,nodeName);
		if(node!=null) network.getRow(node).set("selected",true); 
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		network=applicationManager.getCurrentNetwork();
		utils=new ModuleUtils();	
		(new TextFromPasteBox(swingApplication.getJFrame(),
				"Select Nodes By Names In "+network.getRow(network).get(CyNetwork.NAME,String.class),"Paste Node List",this)).setVisible(true);
	}
}
