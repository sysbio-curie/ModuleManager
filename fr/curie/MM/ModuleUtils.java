package fr.curie.MM;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;

/**
 * Class of utilities mainly for module manager
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class ModuleUtils {
	final String  previousEdge="previous_edge";
	final String  hasModule="has_nested_network";
	final String  moduleNest="nested_network";
	/**
	 * Several methods to create and update attributes in tables
	 */
	public void checkEdgeColumn(CyNetwork net,String columnTitle,Class<?> type){
		if(net.getDefaultEdgeTable().getColumn(columnTitle)==null) net.getDefaultEdgeTable().createColumn(columnTitle,type,false);
	}
	public void checkNodeColumn(CyNetwork net,String columnTitle,Class<?> type){
		if(net.getDefaultNodeTable().getColumn(columnTitle)==null) net.getDefaultNodeTable().createColumn(columnTitle,type,false);
	}
	void checkModuleColumn(CyNetwork net){
		checkNodeColumn(net,hasModule,Boolean.class);
		checkNodeColumn(net,moduleNest,Long.class);
	}
	/**
	 * Create the node/module/nest pointing to a network 
	 */
	CyNode addModule(CyNetwork inNet,CyNetwork netPointer){
		CyNode newNode=inNet.addNode();
		newNode.setNetworkPointer(netPointer);
		inNet.getRow(newNode).set(CyNetwork.NAME,netPointer.getRow(netPointer).get(CyNetwork.NAME,String.class));			
		inNet.getRow(newNode).set(hasModule,true);
		inNet.getRow(newNode).set(moduleNest,netPointer.getSUID());
		return newNode;
	}
	/**
	 * Create an edge between a source and a target in the case of all edges are kept
	 * Put the name of the previous edge in table
	 * In ModuleUtils because used by 2 classes unlike createDistinctEdge(CyNetwork, CyNode, String, CyNode, HashSet<String>)
	 */
	void createAllEdge(CyNetwork current,CyNode src,String interaction,CyNode tgt,String previousEdgeName){
		CyEdge newEdge=current.addEdge(src,tgt,true);
		String newEdgeName=current.getRow(src).get(CyNetwork.NAME,String.class)+
		"("+interaction+")"+current.getRow(tgt).get(CyNetwork.NAME,String.class);
		current.getRow(newEdge).set(CyNetwork.NAME,newEdgeName);
		current.getRow(newEdge).set(CyEdge.INTERACTION,interaction);
		current.getRow(newEdge).set(previousEdge,previousEdgeName);
	}
	/**
	 * Extract the list of networks which are selected in network panel
	 * Display a warning message when only one network is selected
	 */
	List<CyNetwork> selectNetsFromPanel(CyApplicationManager applicationManager,JFrame desktop){
		List<CyNetwork> selectedNets=applicationManager.getSelectedNetworks();
		if(selectedNets.size()<2){
			if(JOptionPane.showConfirmDialog(desktop,				
					"Less than 2 networks are selected, Confirm to Continue or Cancel\r\n (Selection of Several Networks in Network Panel by Control Click)",
					"List of Selected networks",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.CANCEL_OPTION) return null;
		}
		return selectedNets;
	}
	/**
	 * Select one network in the list of all networks
	 */	
	CyNetwork selectOneNetwork(CyNetworkManager networkManager,JFrame desktop,String title, String label){
		TreeMap<String,CyNetwork> nameToNet=new TreeMap<String,CyNetwork>();
		for(CyNetwork net:networkManager.getNetworkSet()) nameToNet.put(net.getRow(net).get(CyNetwork.NAME,String.class),net);
		String[] netNames=new String[nameToNet.keySet().size()];
		int ni=0;for(String s:nameToNet.keySet()) netNames[ni++]=s;
		String selected=(String)JOptionPane.showInputDialog(desktop,label,title,JOptionPane.PLAIN_MESSAGE,null,netNames,netNames[0]);
		if(selected==null) return null; else return nameToNet.get(selected);
	}
	/**
	 * Map reversing access from nodes to modules,
	 * used to connect modules and modules or nodes
	 */
	TreeMap<String,HashSet<CyNode>> getNodeToModules(CyNetwork modularNet){
		TreeMap<String,HashSet<CyNode>> nodeToModules=new TreeMap<String,HashSet<CyNode>>();
		for(CyNode moduleNode:modularNet.getNodeList()){
			CyNetwork containerNet=moduleNode.getNetworkPointer();
			if(containerNet==null) continue;
			for(CyNode nodeIn:containerNet.getNodeList()){
				String nodeInName=containerNet.getRow(nodeIn).get(CyNetwork.NAME,String.class);
				if(nodeToModules.containsKey(nodeInName)){
					nodeToModules.get(nodeInName).add(moduleNode);
				}else{
					HashSet<CyNode> modules= new HashSet<CyNode>();
					modules.add(moduleNode);
					nodeToModules.put(nodeInName,modules);
				}
			}
		}
		return nodeToModules;
	}
	/**
	 * Return first node with name, return a CyNode 
	 */
	public CyNode getCyNode(CyNetwork network, String name){
		Iterator<CyNode> i = network.getNodeList().iterator();
		while(i.hasNext()){
			CyNode node=i.next();
			if(network.getRow(node).get(CyNetwork.NAME, String.class).equals(name)) return node;
		}		
		return null;
	}
	/**
	 * Return new name of modular network to avoid to give the same name to 2 networks
	 */
	String noSynonymInNets(CyNetworkManager networkManager,String prefix){
		HashSet<String> netNames=new HashSet<String>();
		for(CyNetwork net:networkManager.getNetworkSet()) netNames.add(net.getRow(net).get(CyNetwork.NAME,String.class));
		int ni=0;while(netNames.contains(prefix+ni)) ni++;
		return prefix+ni;
	}
}
