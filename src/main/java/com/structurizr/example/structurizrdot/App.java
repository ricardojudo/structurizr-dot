package com.structurizr.example.structurizrdot;

import java.io.StringWriter;

import com.structurizr.Workspace;
import com.structurizr.io.dot.DotWriter;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Workspace workspace = new Workspace("Getting Started", "This is a model of my software system.");
    	Model model = workspace.getModel();
    	
    	Person user = model.addPerson("User", "A user of my software system.");
    	SoftwareSystem softwareSystem = model.addSoftwareSystem("Software System", "My software system.");
    	user.uses(softwareSystem, "Uses");
    	
    	ViewSet views = workspace.getViews();
    	SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext", "An example of a System Context diagram.");
    	contextView.addAllSoftwareSystems();
    	contextView.addAllPeople();
    	   
    	
    	Styles styles = views.getConfiguration().getStyles();
    	styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
    	styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
    	
    	
    	StringWriter stringWriter = new StringWriter();
    	DotWriter dotWriter = new DotWriter();
    	dotWriter.write(workspace, stringWriter);
    	System.out.println(stringWriter);
    }
}
