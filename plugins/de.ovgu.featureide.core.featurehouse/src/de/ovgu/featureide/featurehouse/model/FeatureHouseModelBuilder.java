/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2011  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.featurehouse.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;
import de.ovgu.featureide.core.IFeatureProject;
import de.ovgu.featureide.core.fstmodel.FSTClass;
import de.ovgu.featureide.core.fstmodel.FSTFeature;
import de.ovgu.featureide.core.fstmodel.FSTModel;


/**
 * This builder builds the FSTModel for FeatureHouseProjects, by extracting features, 
 * methods and fields from classes to build. 
 * 
 * @author Jens Meinicke
 */
public class FeatureHouseModelBuilder {

	private static final String NODE_TYPE_FEATURE = "Feature";
	private static final String NODE_TYPE_CLASS = "EOF Marker";
	private static final String NODE_TYPE_CLASS_DECLARATION = "ClassDeclaration";
	private static final String NODE_TYPE_FIELD = "FieldDecl";
	private static final String NODE_TYPE_METHOD = "MethodDecl";
	private static final String NODE_TYPE_CONSTRUCTOR = "ConstructorDecl";

	private FSTModel model;

	private IFeatureProject featureProject;
	
	private FSTFeature currentFeature = null;
	private FSTClass currentClass = null;
	private IFile currentFile = null;

	public FeatureHouseModelBuilder(IFeatureProject featureProject) {
		if (featureProject == null) {
			return;
		}
		FSTModel oldModel = featureProject.getFSTModel();
		if (oldModel != null)
			oldModel.markObsolete();

		model = new FSTModel(featureProject.getProjectName());
		featureProject.setFSTModel(model);
		this.featureProject = featureProject;
	}
	
	public IFile getCurrentFile() {
		return currentFile;
	}
	
	public FSTClass getCurrentClass() {
		return currentClass;
	}

	public void buildModel(List<FSTNode> nodes) {
		model.classesMap = new HashMap<IFile, FSTClass>();
		model.classes = new HashMap<String, FSTClass>();
		model.features = new HashMap<String, FSTFeature>();

		for (FSTNode node : nodes) {
			if (node.getType().equals(NODE_TYPE_FEATURE)) {
				caseAddFeature(node);
			} else if (node.getType().equals(NODE_TYPE_CLASS)) {
				caseAddClass(node);
			} else if (node.getType().equals(NODE_TYPE_CLASS_DECLARATION)) {
				caseClassDeclaration(node);
			}
		}
	}
	
	private void caseAddFeature(FSTNode node) {
		if (currentFeature != null && model.features.containsKey(node.getName())) {
			currentFeature = model.features.get(node.getName());
		} else {
			currentFeature = new FSTFeature(node.getName());
			model.features.put(node.getName(), currentFeature);
		}
	}
	
	private void caseAddClass(FSTNode node) {
		String className = node.getName().substring(
				node.getName().lastIndexOf(File.separator) + 1);
		currentClass = new FSTClass(className);
		currentFile = getFile(node.getName());
		if (!canCompose()) {
			return;
		}
		currentClass.setFile(currentFile);
		model.classes.put(className, currentClass);
		addClass(className, node.getName());
		currentFeature.classes.put(className, currentClass);
	}

	/**
	 * @return
	 */
	private boolean canCompose() {
		return featureProject.getComposer().extensions()
				.contains("." + currentFile.getFileExtension());
	}

	private void caseClassDeclaration(FSTNode node) {
		if (node instanceof FSTNonTerminal && canCompose()) {
			for (FSTNode child : ((FSTNonTerminal) node).getChildren()) {
				if (child instanceof FSTTerminal) {
					FSTTerminal terminal = (FSTTerminal) child;
					if (terminal.getType().equals(FeatureHouseModelBuilder.NODE_TYPE_FIELD)) {
						ClassBuilder.getClassBuilder(currentFile, this)
								.caseFieldDeclaration(terminal);
					} else if (terminal.getType().equals(FeatureHouseModelBuilder.NODE_TYPE_METHOD)) {
						ClassBuilder.getClassBuilder(currentFile, this)
								.caseMethodDeclaration(terminal);
					} else if (terminal.getType().equals(FeatureHouseModelBuilder.NODE_TYPE_CONSTRUCTOR)) {
						ClassBuilder.getClassBuilder(currentFile, this)
								.caseConstructorDeclaration(terminal);
					}
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private IFile getFile(String name) {
		String projectName = featureProject.getProjectName();
		name = name.substring(name.indexOf(projectName) + projectName.length() + 1);
		return featureProject.getProject().getFile(new Path(name));
	}

	/**
	 * Adds a class to the java project model
	 * 
	 */
	private void addClass(String className, String source) {
		FSTClass currentClass = null;
		
		if (model.classes.containsKey(className)) {
			currentClass = model.classes.get(className);
		} else {
			currentClass = new FSTClass(className);
			model.classes.put(className, currentClass);
		}
		if (!model.classesMap.containsKey(source)) {
			
			model.classesMap.put(null, currentClass);
		}
	}

}
