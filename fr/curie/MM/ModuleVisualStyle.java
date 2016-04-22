package fr.curie.MM;
/*
   BiNoM Cytoscape Plugin under GNU Lesser General Public License 
   Copyright (C) 2010-2011 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE  
*/
// Must be rewritten without depreciated classes (it is not a critical class)
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Set;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
/**
 * Create a style to visualize nest networks
 * 
 * @author Daniel.Rovera@curie.fr 
 */
public class ModuleVisualStyle extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="Create Module Style";
	public ModuleVisualStyle(){
		super(title,ModuleManager_App.getAdapter().getCyApplicationManager(),"network",ModuleManager_App.getAdapter().getCyNetworkViewManager());		
		setPreferredMenu(ModuleManager_App.name);
	}
	public void actionPerformed(ActionEvent e) {
		create();		
	}
	static final String NAME = "Module Style";
	static VisualStyle vsModule = null;	
	public VisualStyle getVisualStyle(){
		if(vsModule == null) create();		
		return vsModule;
	}	
	public void create(){			
		Set<VisualStyle> visualStyles = ModuleManager_App.getAdapter().getVisualMappingManager().getAllVisualStyles();
		ArrayList visualStylesNames = new ArrayList();
		for(VisualStyle vs: visualStyles){
			visualStylesNames.add(vs.getTitle());
			if(vs.getTitle().compareTo(NAME) == 0) vsModule = vs;		
		}	
		if(vsModule == null) vsModule= ModuleManager_App.getAdapter().getVisualStyleFactory().createVisualStyle(NAME);		
		PassthroughMapping pm = (PassthroughMapping) ModuleManager_App.getAdapter().getVisualMappingFunctionPassthroughFactory().createVisualMappingFunction("name", String.class, BasicVisualLexicon.NODE_LABEL);
		vsModule.addVisualMappingFunction(pm); 				
		DiscreteMapping nodeShape = (DiscreteMapping) ModuleManager_App.getAdapter().getVisualMappingFunctionDiscreteFactory().createVisualMappingFunction("has_nested_network", Boolean.class, BasicVisualLexicon.NODE_SHAPE);
		nodeShape.putMapValue(true, NodeShapeVisualProperty.OCTAGON);
		vsModule.addVisualMappingFunction(nodeShape); 
		DiscreteMapping nodeColour = (DiscreteMapping) ModuleManager_App.getAdapter().getVisualMappingFunctionDiscreteFactory().createVisualMappingFunction("has_nested_network", Boolean.class, BasicVisualLexicon.NODE_FILL_COLOR);
		nodeColour.putMapValue(true, Color.GREEN);
		vsModule.addVisualMappingFunction(nodeColour); 
		DiscreteMapping arrowShape = (DiscreteMapping) ModuleManager_App.getAdapter().getVisualMappingFunctionDiscreteFactory().createVisualMappingFunction("interaction", String.class, BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
		arrowShape.putMapValue("MOLECULEFLOW", ArrowShapeVisualProperty.ARROW);
		arrowShape.putMapValue("RIGHT", ArrowShapeVisualProperty.ARROW);
		arrowShape.putMapValue("LEFT", ArrowShapeVisualProperty.ARROW);
		arrowShape.putMapValue("CATALYSIS", ArrowShapeVisualProperty.CIRCLE);
		arrowShape.putMapValue("ACTIVATION", ArrowShapeVisualProperty.CIRCLE);
		arrowShape.putMapValue("INHIBITION", ArrowShapeVisualProperty.T);	
		vsModule.addVisualMappingFunction(arrowShape); 		
		DiscreteMapping arrowColour = (DiscreteMapping) ModuleManager_App.getAdapter().getVisualMappingFunctionDiscreteFactory().createVisualMappingFunction("interaction", String.class, BasicVisualLexicon.EDGE_UNSELECTED_PAINT);
		arrowColour.putMapValue("MOLECULEFLOW", Color.BLACK);
		arrowColour.putMapValue("RIGHT", Color.BLACK);
		arrowColour.putMapValue("LEFT", Color.BLACK);
		arrowColour.putMapValue("CATALYSIS", Color.RED);
		arrowColour.putMapValue("ACTIVATION", Color.RED);
		arrowColour.putMapValue("INHIBITION", Color.BLUE);
		arrowColour.putMapValue("INTERSECTION", Color.GREEN);
		vsModule.addVisualMappingFunction(arrowColour); 		
		ModuleManager_App.getAdapter().getVisualMappingManager().addVisualStyle(vsModule);
	}
}
