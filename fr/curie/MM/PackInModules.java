package fr.curie.MM;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
/**
 * Pack nodes inside selected networks in module/nest pointing to these networks
 * Position of nest are the mean position of nodes
 * Edges with 1 or 2 end in module is created between modules or a node
 * Other edges are not modified
 * All nodes inside modules are destroyed
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class PackInModules extends AbstractCyAction {
	private static final long serialVersionUID = 1L;
	final public static String title="Pack Network In Modules";
	public PackInModules() {
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent v){
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CyEventHelper eventHelper=ModuleManager_App.getAdapter().getCyEventHelper();
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();	
		ModuleUtils utils=new ModuleUtils();
		List<CyNetwork> selectedNets=utils.selectNetsFromPanel(applicationManager,swingApplication.getJFrame());
		if(selectedNets==null) return;
		CyNetwork netToPack=utils.selectOneNetwork(networkManager,swingApplication.getJFrame(),title,"Select the Network to Pack");
		utils.checkEdgeColumn(netToPack,utils.previousEdge,String.class);	
		utils.checkModuleColumn(netToPack);
		HashMap<String,CyNode> nameToNode=new HashMap<String,CyNode>();
		for(CyNode node:netToPack.getNodeList()) nameToNode.put(netToPack.getRow(node).get(CyNetwork.NAME,String.class),node);
		HashMap<CyNode,HashSet<CyNode>> nodeToModules=new HashMap<CyNode,HashSet<CyNode>>();
		applicationManager.setCurrentNetwork(netToPack);
		CyNetworkView view=applicationManager.getCurrentNetworkView();
		for(CyNetwork moduleNet:selectedNets){
			CyNode newNode=utils.addModule(netToPack,moduleNet);
			double x=0.0,y=0.0;
			for(CyNode nodeInModule:moduleNet.getNodeList()){
				String name=moduleNet.getRow(nodeInModule).get(CyNetwork.NAME,String.class);
				CyNode node=nameToNode.get(name);
				if(node==null) continue;
				x=x+view.getNodeView(node).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
				y=y+view.getNodeView(node).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
				if(nodeToModules.containsKey(node)){
					nodeToModules.get(node).add(newNode);
				}else{
					HashSet<CyNode> modules= new HashSet<CyNode>();
					modules.add(newNode);
					nodeToModules.put(node,modules);
				}
			}			
			x=x/moduleNet.getNodeCount();
			y=y/moduleNet.getNodeCount();						
			eventHelper.flushPayloadEvents();
			view.getNodeView(newNode).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,x);
			view.getNodeView(newNode).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,y);
		}
		for(CyEdge edge:netToPack.getEdgeList()){
			String edgeName=netToPack.getRow(edge).get(CyNetwork.NAME,String.class);
			CyNode src=edge.getSource();
			CyNode tgt=edge.getTarget();
			String interaction=netToPack.getRow(edge).get(CyEdge.INTERACTION,String.class);		
			HashSet<CyNode> srcModules=nodeToModules.get(src);			
			HashSet<CyNode> tgtModules=nodeToModules.get(tgt);			
			if((srcModules!=null)&(tgtModules!=null)){			
				for(CyNode srcModule:srcModules)for(CyNode tgtModule:tgtModules){
					utils.createAllEdge(netToPack,srcModule,interaction,tgtModule,edgeName);
				}
				continue;
			}
			if((srcModules==null)&(tgtModules!=null)){
				for(CyNode tgtModule:tgtModules){
				utils.createAllEdge(netToPack,src,interaction,tgtModule,edgeName);
				}
				continue;
			}
			if((srcModules!=null)&(tgtModules==null)){
				for(CyNode srcModule:srcModules){
					utils.createAllEdge(netToPack,srcModule,interaction,tgt,edgeName);
				}
				continue;				
			}			
		}
		netToPack.removeNodes(nodeToModules.keySet());
		netToPack.getRow(netToPack).set(CyNetwork.NAME,utils.noSynonymInNets(networkManager,netToPack.getRow(netToPack).get(CyNetwork.NAME,String.class)+"_packed"));
		view.updateView();
	}
}
