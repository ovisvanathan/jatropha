package com.exalto.UI.grid;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface ActionsHandler {

	public HashMap getGridCommands();
	
	public HashMap getDomToTreeMap();

	public Document getDocument();

    public HashMap getNamespaces();

    public Node getSelectedNode();
}
