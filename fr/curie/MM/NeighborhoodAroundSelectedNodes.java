package fr.curie.MM;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
/**
 * Complete a column by the nearest nodes of selected nodes
 * the distance is under a nearness, 0 is not connected
 * Nearness is a node table, column ON_NodeName, row nearness as double
 * Table is used as orientedNearness[sources in columns][targets/nodes in rows]
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class NeighborhoodAroundSelectedNodes extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final static String title="Neighborhood Around Selected Nodes";
	final String orientedNearnessID= "ON_";
	final String ResultColID= "NearBy";
	final String message=" is the completed column by the nearest nodes according this table above\n\t";
	CyNetwork network;
	ArrayList<CyNode> nodes;
	int srcNb;
	double[][] orientedNearness;
	public NeighborhoodAroundSelectedNodes(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	boolean setOrientedNearness(){
		ModuleUtils utils=new ModuleUtils();
		nodes=new ArrayList<CyNode>();
		ArrayList<String> columns=new ArrayList<String>();
		Collection<CyColumn> columnList = network.getDefaultNodeTable().getColumns();		
		CyColumn col=null;
		Iterator<CyColumn> iter=columnList.iterator();		
		while(iter.hasNext()) {
			col=iter.next();
			String colName;
			if(col.getType()!=Double.class) continue; else colName=col.getName();
			if(colName.substring(0,orientedNearnessID.length()).equals(orientedNearnessID)){
				CyNode node=utils.getCyNode(network,colName.substring(orientedNearnessID.length()));
				if(node==null) return false;
				else{
					nodes.add(node);
					columns.add(colName);
				}
			}
		}							
		if(nodes.isEmpty()) return false;
		srcNb=columns.size();
		orientedNearness=new double[srcNb][nodes.size()];
		for(int c=0;c<columns.size();c++) for(int n=0;n<nodes.size();n++) orientedNearness[c][n]=network.getRow(nodes.get(n)).get(columns.get(c),Double.class);
		return true;
	}
	String orientedNearnessTxt(){
		StringBuffer txt=new StringBuffer();
		for(int s=0;s<srcNb;s++) {
			txt.append("\t");
			txt.append(network.getRow(nodes.get(s)).get(CyNetwork.NAME, String.class));
		}
		txt.append("\r\n");
		for(int t=0;t<nodes.size();t++){
			txt.append(network.getRow(nodes.get(t)).get(CyNetwork.NAME, String.class));
			for(int s=0;s<nodes.size();s++){
				txt.append("\t");
				txt.append(orientedNearness[s][t]);
			}
			txt.append("\r\n");
		}
		System.out.print(txt);
		return txt.toString();
	}
	public void actionPerformed(ActionEvent e){
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		network=applicationManager.getCurrentNetwork();
		List<CyNode> selected=CyTableUtil.getNodesInState(network,"selected",true);
		if(selected.size()<2){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Select more than 1 node",title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!setOrientedNearness()){
			JOptionPane.showMessageDialog(swingApplication.getJFrame(),"Table of oriented nearness is not correct\nCheck column title and node names",title,JOptionPane.ERROR_MESSAGE);
			return;
		}
		String resultCol=ResultColID;
		int ni=0;while(network.getDefaultNodeTable().getColumn(resultCol+ni)!=null) ni++;
		resultCol=resultCol+ni;
		network.getDefaultNodeTable().createColumn(resultCol,String.class,false);
		ArrayList<Integer> startNodes=new ArrayList<Integer>(selected.size());
		ArrayList<String> startNames=new ArrayList<String>(selected.size());
		Iterator<CyNode> iter=selected.iterator();
		while(iter.hasNext()){
			CyNode startNode=iter.next();
			int node=nodes.indexOf(startNode);
			if(node==-1){
				JOptionPane.showMessageDialog(swingApplication.getJFrame(),"A selected node does not exist in sources\nColumn: node name followed by "
						+orientedNearnessID,title,JOptionPane.ERROR_MESSAGE);
				return;	
			}
			startNodes.add(node);
			startNames.add(network.getRow(startNode).get(CyNetwork.NAME,String.class));			
		}	
		for(int t=0;t<nodes.size();t++){
			double nearness=0.0;
			int near=0;
			for(int s=0;s<startNodes.size();s++)if(orientedNearness[startNodes.get(s)][t]>nearness){
				nearness=orientedNearness[startNodes.get(s)][t];
				near=s;
			}
			if(nearness>0.0) network.getRow(nodes.get(t)).set(resultCol,startNames.get(near));
			else network.getRow(nodes.get(t)).set(resultCol,"_far_");		
		}
		StringBuffer txt=new StringBuffer();
		txt.append("Selected start nodes:");
		txt.append(startNames.get(0));
		for(int s=1;s<startNodes.size();s++){txt.append(",");txt.append(startNames.get(s));}
		txt.append("\r\n");
		new TextBox(swingApplication.getJFrame(),title,txt+resultCol+message+orientedNearnessTxt()).setVisible(true);
	}
}
