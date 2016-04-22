package fr.curie.MM;

/*
   BiNoM Cytoscape Plugin under GNU Lesser General Public License 
   Copyright (C) 2010-2011 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;

/**
 *  Create a node attribute containing nest name where it is in the current network
 *  
 *  @author Daniel.Rovera@curie.fr
 */
public class ModuleInNodeAttribute extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final public static String title="Assign Module Names to Node Attribute";
	public ModuleInNodeAttribute(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}	
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		CyNetwork current = applicationManager.getCurrentNetwork();
		String nodeAttr="IN_"+ current.getRow(current).get(CyNetwork.NAME, String.class);
		ModuleUtils utils=new ModuleUtils();
		CyNetwork reference=utils.selectOneNetwork(networkManager,swingApplication.getJFrame(),title,"Select network where node attribute will contain module names");
		if (reference==null) return;
		if(reference.getDefaultNodeTable().getColumn(nodeAttr) == null) reference.getDefaultNodeTable().createColumn(nodeAttr, String.class, false);
		for(CyNode module:current.getNodeList()){
			CyNetwork modNet=module.getNetworkPointer();
			if(modNet==null) continue;
			for(CyNode node:modNet.getNodeList()){
				reference.getRow(node).set(nodeAttr, current.getRow(module).get(CyNetwork.NAME, String.class));
			}
		}
		JOptionPane.showMessageDialog(swingApplication.getJFrame(),"created Attribute "+nodeAttr+" contains only one module where is the node",title,JOptionPane.INFORMATION_MESSAGE);
	}	
}