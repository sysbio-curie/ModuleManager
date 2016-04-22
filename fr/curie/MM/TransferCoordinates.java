package fr.curie.MM;
import java.awt.event.ActionEvent;
import java.util.TreeMap;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Transfer coordinates from networks to networks
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class TransferCoordinates extends AbstractCyAction{	
	private static final long serialVersionUID = 1L;
	final static String title="Transfer Network Coordinates";
	public TransferCoordinates(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent e) {
		CyNetworkManager networkManager=ModuleManager_App.getAdapter().getCyNetworkManager();
		CySwingApplication swingApplication=ModuleManager_App.getAdapter().getCySwingApplication();
		CyApplicationManager applicationManager=ModuleManager_App.getAdapter().getCyApplicationManager();
		ModuleUtils utils=new ModuleUtils();
		TreeMap<String,CyNetwork> nameToNet=new TreeMap<String,CyNetwork>();
		for(CyNetwork net:networkManager.getNetworkSet()) nameToNet.put(net.getRow(net).get(CyNetwork.NAME,String.class),net);
		String[] netNames=new String[nameToNet.keySet().size()];
		int ni=0;for(String s:nameToNet.keySet()) netNames[ni++]=s;
		StringBuffer fromName=new StringBuffer();
		StringBuffer toName=new StringBuffer();
		TwoComboBoxes dialog=new TwoComboBoxes(swingApplication.getJFrame(),title,"Select From Network ","Select To Network ",netNames,netNames);
		if(!dialog.launchDialog(fromName,toName)) return;
		CyNetwork fromNet=nameToNet.get(fromName.toString());
		CyNetwork toNet=nameToNet.get(toName.toString());
		double x=0.0,y=0.0;
		CyNetworkView view=null;
		CyNode to=null;
		String nodeName;
		for(CyNode from:fromNet.getNodeList()){
			applicationManager.setCurrentNetwork(fromNet);
			view=applicationManager.getCurrentNetworkView();
			nodeName=fromNet.getRow(from).get(CyNetwork.NAME,String.class);
			x=view.getNodeView(from).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			y=view.getNodeView(from).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			applicationManager.setCurrentNetwork(toNet);
			view=applicationManager.getCurrentNetworkView();
			to=utils.getCyNode(toNet,nodeName);
			if(to!=null){
				view.getNodeView(to).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,x);
				view.getNodeView(to).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,y);
			}
		}
		view.updateView();
	}
}
