package com.structurizr.example.annotations;

import java.io.StringWriter;

import com.structurizr.Workspace;
import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.StructurizrAnnotationsComponentFinderStrategy;
import com.structurizr.io.dot.DotWriter;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

public class App {
	private static final String DATABASE_TAG = "Database";

	public static void main(String[] args) {
		Workspace workspace = new Workspace("Structurizr for Java Annotations",
				"This is a model of my software system.");
		Model model = workspace.getModel();

		Person user = model.addPerson("User", "A user of my software system.");
		SoftwareSystem softwareSystem = model.addSoftwareSystem("Software System", "My software system.");

		Container webApplication = softwareSystem.addContainer("Web Application", "Provides users with information.",
				"Java");
		Container database = softwareSystem.addContainer("Database", "Stores information.",
				"Relational database schema");
		database.addTags(DATABASE_TAG);

		ComponentFinder componentFinder = new ComponentFinder(webApplication, "com.structurizr.example.annotations",
				new StructurizrAnnotationsComponentFinderStrategy());
		try {
			componentFinder.findComponents();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addImplicitRelationships();

		ViewSet views = workspace.getViews();
		SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext",
				"An example of a System Context diagram.");
		contextView.addAllElements();

		ContainerView containerView = views.createContainerView(softwareSystem, "Containers",
				"The container diagram from my software system.");
		containerView.addAllElements();

		ComponentView componentView = views.createComponentView(webApplication, "Components",
				"The component diagram for the web application.");
		componentView.addAllElements();

		Styles styles = views.getConfiguration().getStyles();
		styles.addElementStyle(Tags.ELEMENT).color("#ffffff");
		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd");
		styles.addElementStyle(Tags.CONTAINER).background("#438dd5");
		styles.addElementStyle(Tags.COMPONENT).background("#85bbf0").color("#000000");
		styles.addElementStyle(Tags.PERSON).background("#08427b").shape(Shape.Person);
		styles.addElementStyle(DATABASE_TAG).shape(Shape.Cylinder);

		StringWriter stringWriter = new StringWriter();
		DotWriter dotWriter = new DotWriter();
		dotWriter.write(workspace, stringWriter);

		System.out.println(stringWriter);

	}

}
