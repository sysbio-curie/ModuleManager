package fr.curie.MM;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;

/**
 * Display warning which may disturb modularization
 * nodes in several modules, nodes not belonging to the reference network
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class DisplayWarningFromReference extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final static String title="Display Warning From Reference";
	public DisplayWarningFromReference(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	boolean displayWarning(JFrame desktop,CyNetwork current,CyNetwork reference,TreeMap<String,HashSet<CyNode>> nodeToModules){
		boolean	warning=false;
		String warningText=new String("");
		for(String node:nodeToModules.keySet()){			
			HashSet<CyNode> nodeList=nodeToModules.get(node);
			if(nodeList.size()>1){
				warningText=warningText+"Node In Several Modules\t"+node;
				for(CyNode module:nodeToModules.get(node)){
					warningText=warningText+"\t"+current.getRow(module).get(CyNetwork.NAME,String.class);
				}
				warningText=warningText+"\r\n";
				warning=true;
			}			
		}
		HashSet<String> nodesInRef=new HashSet<String>();
		for(CyNode node:reference.getNodeList()) nodesInRef.add(reference.getRow(node).get(CyNetwork.NAME,String.class));
		for(String node:nodeToModules.keySet()) if(!nodesInRef.contains(node)){
			warningText=warningText+"Node In Modules But Not In Reference Network\t"+node+"\r\n";
			warning=true;
		}
		for(String node:nodesInRef) if(!nodeToModules.keySet().contains(node)){
			warningText=warningText+"Node In Reference Network But Not In Modules\t"+node+"\r\n";
			warning=true;
		}
		if(warning) new TextBox(desktop,"Warning about Contents of Modules",warningText).setVisible(true);
		else JOptionPane.showMessageDialog(desktop,"No Warning",title,JOptionPane.INFORMATION_MESSAGE);
		return warning;
	}
	public void actionPerformed(ActionEvent e) {
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		ModuleUtils utils=new ModuleUtils();
		CyNetwork reference=utils.selectOneNetwork(networkManager,swingApplication.getJFrame(),title,"Select a network as reference");
		if (reference==null) return;
		CyNetwork current=applicationManager.getCurrentNetwork();
		TreeMap<String,HashSet<CyNode>> nodeToModules=utils.getNodeToModules(current);
		displayWarning(swingApplication.getJFrame(),current,reference,nodeToModules);		
	}
}
