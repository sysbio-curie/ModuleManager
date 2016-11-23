package fr.curie.MM;
/*
BiNoM Cytoscape Plugin under GNU Lesser General Public License 
Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
*/
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
/**
 * Select node or edge ifEdge=true
 * @author Daniel.Rovera@curie.fr
 */
public class SelectByName{
	CyNetwork network;
	ModuleUtils utils;
	boolean ifEdge;
	public SelectByName(CyNetwork network, boolean ifEdge){
		this.network=network;
		this.ifEdge=ifEdge;
		utils=new ModuleUtils();
	}
	void setSelected(String name){
		if(ifEdge){
			CyEdge edge=utils.getCyEdge(network,name);
			if(edge!=null) network.getRow(edge).set("selected",true);
		}else{
			CyNode node=utils.getCyNode(network,name);
			if(node!=null) network.getRow(node).set("selected",true);
		}
	}
}
