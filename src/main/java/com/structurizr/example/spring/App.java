package com.structurizr.example.spring;

import java.io.StringWriter;
import java.util.Set;

import com.structurizr.Workspace;
import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.ReferencedTypesSupportingTypesStrategy;
import com.structurizr.analysis.SpringComponentFinderStrategy;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.io.dot.DotWriter;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.model.Container;
import com.structurizr.model.ContainerInstance;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.Relationship;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.util.MapUtils;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

public class App {
	private static final String VERSION = "ffa967c94b65a70ea6d3b44275632821838d9fd3";

	public static void main(String[] args) throws Exception {

		Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("org.springframework.samples.petclinic.vet.Vet");
		System.out.println(clazz);

		Workspace workspace = new Workspace("Spring PetClinic",
				"This is a C4 representation of the Spring PetClinic sample app (https://github.com/spring-projects/spring-petclinic/)");
		workspace.setVersion(VERSION);
		Model model = workspace.getModel();

		// create the basic model (the stuff we can't get from the code)
		SoftwareSystem springPetClinic = model.addSoftwareSystem("Spring PetClinic",
				"Allows employees to view and manage information regarding the veterinarians, the clients, and their pets.");
		Person clinicEmployee = model.addPerson("Clinic Employee", "An employee of the clinic");
		clinicEmployee.uses(springPetClinic, "Uses");

		Container webApplication = springPetClinic.addContainer("Web Application",
				"Allows employees to view and manage information regarding the veterinarians, the clients, and their pets.",
				"Java and Spring");
		webApplication.addProperty("Deployable artifact name", "petclinic.war");
		Container relationalDatabase = springPetClinic.addContainer("Database",
				"Stores information regarding the veterinarians, the clients, and their pets.",
				"Relational Database Schema");
		clinicEmployee.uses(webApplication, "Uses", "HTTPS");
		webApplication.uses(relationalDatabase, "Reads from and writes to", "JDBC");

		// and now automatically find all Spring @Controller, @Component, @Service and
		// @Repository components

		SpringComponentFinderStrategy springComponentFinderStrategy = new SpringComponentFinderStrategy(
				new ReferencedTypesSupportingTypesStrategy(false));
		springComponentFinderStrategy.setIncludePublicTypesOnly(false);

		ComponentFinder componentFinder = new ComponentFinder(webApplication, "org.springframework.samples",
				springComponentFinderStrategy);

		Set<?> set = componentFinder.findComponents();
		System.out.println(set);
		// connect the user to all of the Spring MVC controllers
		webApplication.getComponents().stream()
				.filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_MVC_CONTROLLER))
				.forEach(c -> clinicEmployee.uses(c, "Uses", "HTTP"));

		// connect all of the repository components to the relational database
		webApplication.getComponents().stream()
				.filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_REPOSITORY))
				.forEach(c -> c.uses(relationalDatabase, "Reads from and writes to", "JDBC"));

		// finally create some views
		ViewSet views = workspace.getViews();
		SystemContextView contextView = views.createSystemContextView(springPetClinic, "context",
				"The System Context diagram for the Spring PetClinic system.");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

		ContainerView containerView = views.createContainerView(springPetClinic, "containers",
				"The Container diagram for the Spring PetClinic system.");
		containerView.addAllPeople();
		containerView.addAllSoftwareSystems();
		containerView.addAllContainers();

		ComponentView componentView = views.createComponentView(webApplication, "components",
				"The Component diagram for the Spring PetClinic web application.");
		componentView.addAllComponents();
		componentView.addAllPeople();
		componentView.add(relationalDatabase);

		// rather than creating a component model for the database, let's simply link to
		// the DDL
		// (this is really just an example of linking an arbitrary element in the model
		// to an external resource)
		relationalDatabase.setUrl("https://github.com/spring-projects/spring-petclinic/tree/" + VERSION
				+ "/src/main/resources/db/hsqldb");
		relationalDatabase.addProperty("Schema name", "petclinic");

		// tag and style some elements
		springPetClinic.addTags("Spring PetClinic");
		webApplication.getComponents().stream()
				.filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_MVC_CONTROLLER))
				.forEach(c -> c.addTags("Spring MVC Controller"));
		webApplication.getComponents().stream()
				.filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_SERVICE))
				.forEach(c -> c.addTags("Spring Service"));
		webApplication.getComponents().stream()
				.filter(c -> c.getTechnology().equals(SpringComponentFinderStrategy.SPRING_REPOSITORY))
				.forEach(c -> c.addTags("Spring Repository"));
		relationalDatabase.addTags("Database");

		DeploymentNode developerLaptop = model.addDeploymentNode("Developer Laptop", "A developer laptop.",
				"Windows 7+ or macOS");
		developerLaptop.addDeploymentNode("Docker Container - Web Server", "A Docker container.", "Docker")
				.addDeploymentNode("Apache Tomcat", "An open source Java EE web server.", "Apache Tomcat 7.x", 1,
						MapUtils.create("Xmx=256M", "Xms=512M", "Java Version=8"))
				.add(webApplication);
		

		developerLaptop.addDeploymentNode("Docker Container - Database Server", "A Docker container.", "Docker")
				.addDeploymentNode("Database Server", "A development database.", "HSQLDB").add(relationalDatabase);

		DeploymentNode stagingServer = model.addDeploymentNode("Staging Server", "A server hosted at Amazon AWS EC2",
				"Ubuntu 12.04 LTS", 1, MapUtils.create("AWS instance type=t2.medium", "AWS region=us-west-1"));
		stagingServer.addDeploymentNode("Apache Tomcat", "An open source Java EE web server.", "Apache Tomcat 7.x", 1,
				MapUtils.create("Xmx=512M", "Xms=1024M", "Java Version=8")).add(webApplication);
		stagingServer.addDeploymentNode("MySQL", "The staging database server.", "MySQL 5.5.x", 1)
				.add(relationalDatabase);
		;

		DeploymentNode liveWebServer = model.addDeploymentNode("Web Server",
				"A server hosted at Amazon AWS EC2, accessed via Elastic Load Balancing.", "Ubuntu 12.04 LTS", 2,
				MapUtils.create("AWS instance type=t2.small", "AWS region=us-west-1"));
		liveWebServer.addDeploymentNode("Apache Tomcat", "An open source Java EE web server.", "Apache Tomcat 7.x", 1,
				MapUtils.create("Xmx=512M", "Xms=1024M", "Java Version=8")).add(webApplication);

		DeploymentNode primaryDatabaseServer = model
				.addDeploymentNode("Database Server - Primary", "A server hosted at Amazon AWS EC2.",
						"Ubuntu 12.04 LTS", 1, MapUtils.create("AWS instance type=t2.medium", "AWS region=us-west-1"))
				.addDeploymentNode("MySQL - Primary", "The primary, live database server.", "MySQL 5.5.x");
		primaryDatabaseServer.add(relationalDatabase);

		DeploymentNode secondaryDatabaseServer = model
				.addDeploymentNode("Database Server - Secondary", "A server hosted at Amazon AWS EC2.",
						"Ubuntu 12.04 LTS", 1, MapUtils.create("AWS instance type=t2.small", "AWS region=us-east-1"))
				.addDeploymentNode("MySQL - Secondary", "A secondary database server, used for failover purposes.",
						"MySQL 5.5.x");
		ContainerInstance secondaryDatabase = secondaryDatabaseServer.add(relationalDatabase);

		model.getRelationships().stream().filter(r -> r.getDestination().equals(secondaryDatabase))
				.forEach(r -> r.addTags("Failover"));
		Relationship dataReplicationRelationship = primaryDatabaseServer.uses(secondaryDatabaseServer,
				"Replicates data to", "");
		secondaryDatabase.addTags("Failover");

		DeploymentView developmentDeploymentView = views.createDeploymentView(springPetClinic, "developmentDeployment",
				"An example development deployment scenario for the Spring PetClinic software system.");
		developmentDeploymentView.add(developerLaptop);

		DeploymentView stagingDeploymentView = views.createDeploymentView(springPetClinic, "stagingDeployment",
				"An example staging deployment scenario for the Spring PetClinic software system.");
		stagingDeploymentView.add(stagingServer);

		DeploymentView liveDeploymentView = views.createDeploymentView(springPetClinic, "liveDeployment",
				"An example live deployment scenario for the Spring PetClinic software system.");
		liveDeploymentView.add(liveWebServer);
		liveDeploymentView.add(primaryDatabaseServer);
		liveDeploymentView.add(secondaryDatabaseServer);
		liveDeploymentView.add(dataReplicationRelationship);

		StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
		template.addContextSection(springPetClinic, Format.Markdown,
				"This is the context section for the Spring PetClinic System...\n![](embed:context)");
		template.addContainersSection(springPetClinic, Format.Markdown,
				"This is the containers section for the Spring PetClinic System...\n![](embed:containers)");
		template.addComponentsSection(webApplication, Format.Markdown,
				"This is the components section for the Spring PetClinic web application...\n![](embed:components)");
		template.addDeploymentSection(springPetClinic, Format.Markdown,
				"This is the deployment section for the Spring PetClinic web application...\n### Staging environment\n![](embed:stagingDeployment)\n### Live environment\n![](embed:liveDeployment)");
		template.addDevelopmentEnvironmentSection(springPetClinic, Format.Markdown,
				"This is the development environment section for the Spring PetClinic web application...\n![](embed:developmentDeployment)");

		Styles styles = views.getConfiguration().getStyles();
		styles.addElementStyle("Spring PetClinic").background("#6CB33E").color("#ffffff");
		styles.addElementStyle(Tags.PERSON).background("#519823").color("#ffffff").shape(Shape.Person);
		styles.addElementStyle(Tags.CONTAINER).background("#91D366").color("#ffffff");
		styles.addElementStyle("Database").shape(Shape.Cylinder);
		styles.addElementStyle("Spring MVC Controller").background("#D4F3C0").color("#000000");
		styles.addElementStyle("Spring Service").background("#6CB33E").color("#000000");
		styles.addElementStyle("Spring Repository").background("#95D46C").color("#000000");
		styles.addElementStyle("Failover").opacity(25);
		styles.addRelationshipStyle("Failover").opacity(25).position(70);

		
	
		
		PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
		StringWriter stringWriter = new StringWriter();
		plantUMLWriter.write(workspace, stringWriter);
		System.out.println(stringWriter);

	}

}
