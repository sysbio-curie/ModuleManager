package fr.curie.MM;
import javax.swing.JMenu;

import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
/*
 * Main Application
 */
public class ModuleManager_App extends AbstractCySwingApp{
	public static String name="Module Manager";
	private static CySwingAppAdapter adapter;
	public static CySwingAppAdapter getAdapter(){return adapter;}
	public ModuleManager_App(CySwingAppAdapter adapter){
		super(adapter);
		ModuleManager_App.adapter=adapter;
		JMenu menu = adapter.getCySwingApplication().getJMenu(name);
		adapter.getCySwingApplication().addAction(new CreateModuleNetwork());
		adapter.getCySwingApplication().addAction(new CreateEdgesBetweenModulesAllFromRef());
		adapter.getCySwingApplication().addAction(new CreateEdgesBetweenModulesDistinctEdges());
		adapter.getCySwingApplication().addAction(new PackInModules());
		adapter.getCySwingApplication().addAction(new DisplayWarningFromReference());
		menu.addSeparator();		
		adapter.getCySwingApplication().addAction(new ListNodes());
		adapter.getCySwingApplication().addAction(new ListEdgesLinkingModules());
		adapter.getCySwingApplication().addAction(new FindCommonNodes());
		adapter.getCySwingApplication().addAction(new SelectNodesByName());
		adapter.getCySwingApplication().addAction(new SelectEdgesByName());
		adapter.getCySwingApplication().addAction(new ModuleInNodeAttribute());
		menu.addSeparator();
		adapter.getCySwingApplication().addAction(new NeighborhoodAroundSelectedNodes());
		adapter.getCySwingApplication().addAction(new SCCinAttribute());
		adapter.getCySwingApplication().addAction(new TransferCoordinates());
		adapter.getCySwingApplication().addAction(new ListComponents());
		adapter.getCySwingApplication().addAction(new ModuleVisualStyle());		
	}
}