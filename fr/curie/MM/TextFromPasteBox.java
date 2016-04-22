package fr.curie.MM;
/*
	   BiNoM Cytoscape Plugin under GNU Lesser General Public License 
	   Copyright (C) 2015-2016 Institut Curie, 26 rue d'Ulm, 75005 Paris - FRANCE   
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Not modal dialog selecting nodes from a list paste from clipboard
 * 
 * @author Daniel.Rovera@curie.fr
 */
public class TextFromPasteBox extends GridBagDialog implements ActionListener,ClipboardOwner{
	private static final long serialVersionUID = 1L;
	final static int cx[]={0,0,1,2};
	final static int cy[]={0,1,1,1};
	final static int cw[]={3,1,1,1};
	final static int ch[]={1,1,1,1};
	final static int xw[]={8,1,1,1};
	final static int yw[]={8,0,0,0} ;
	final static int cf[]={B,H,H,H};
	final int width=360;
	final int height=480;
	private JTextArea dtext;
	private JButton pasteButton,actionButton,exitButton ;
	SelectNodesByName action;
	public TextFromPasteBox(JFrame parent,String title,String text,SelectNodesByName action){
		super(parent,title,false,cx,cy,cw,ch,xw,yw,cf);
		this.action=action;
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		dtext=new JTextArea(text);
		addWithConstraints(0,new JScrollPane(dtext));
		pasteButton = new JButton ("Clear and Paste From Clipboard");
		addWithConstraints(1,pasteButton);		
		pasteButton.addActionListener(this);
		actionButton = new JButton ("Select");
		addWithConstraints(2,actionButton);
		actionButton.addActionListener(this);
		exitButton = new JButton ("Exit");
		addWithConstraints(3,exitButton);
		exitButton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void actionPerformed (ActionEvent e){
		if (e.getSource()==pasteButton){
			Transferable clipData = getToolkit().getSystemClipboard().getContents(this);
			try {
				String clipString=(String)clipData.getTransferData(DataFlavor.stringFlavor);
				dtext.setText(clipString);
			}catch (Exception ex){
				dtext.setText("Cannot paste, not String flavor");
			}
		}
		if (e.getSource()==actionButton){
			ArrayList<String> list=new ArrayList<String>();
			StringTokenizer st=new StringTokenizer(dtext.getText(),"\r\n");
			while(st.hasMoreTokens()) list.add(st.nextToken());	
			for(String s:list) action.setSelected(s);
		}
		if (e.getSource()==exitButton) {
			dispose();
		}
	}
	public void lostOwnership(Clipboard clipboard, Transferable contents){}
}

