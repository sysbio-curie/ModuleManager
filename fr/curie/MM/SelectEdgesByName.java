package fr.curie.MM;
/*
BiNoM Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import java.awt.event.ActionEvent;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;;
/**
 * Select nodes by a name list 
 * @author Daniel.Rovera@curie.fr
 */
public class SelectEdgesByName extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="Select Edges by a Name List";
	public SelectEdgesByName(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		CyNetwork network=applicationManager.getCurrentNetwork();
		SelectByName action=new SelectByName(network,true);
		(new TextFromPasteBox(swingApplication.getJFrame(),
				"Select Edges By Names In "+network.getRow(network).get(CyNetwork.NAME,String.class),"Paste Edge List",action)).setVisible(true);
	}
}

