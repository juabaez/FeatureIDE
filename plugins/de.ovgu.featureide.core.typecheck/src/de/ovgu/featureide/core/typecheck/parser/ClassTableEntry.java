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
package de.ovgu.featureide.core.typecheck.parser;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;

import AST.BodyDecl;
import AST.ClassDecl;
import AST.CompilationUnit;
import AST.FieldDecl;
import AST.MethodDecl;
import de.ovgu.featureide.fm.core.Feature;

/**
 * TODO description
 * 
 * @author S�nke Holthusen
 */
public class ClassTableEntry
{
	private Feature _feature;
	private CompilationUnit _cu_ast;
	private ClassDecl _class_ast;
	private IFile _class_file;

	private long _class_file_modification_stamp;

	public ClassTableEntry(Feature feature, ClassDecl class_ast, CompilationUnit cu, IFile class_file)
	{
		_feature = feature;
		_class_ast = class_ast;
		_cu_ast = cu;
		_class_file = class_file;

		_class_file_modification_stamp = _class_file.getModificationStamp();
	}

	public String getFeatureName()
	{
		return _feature.getName();
	}

	public String getClassName()
	{
		return _class_ast.fullName();
	}

	public ClassDecl getAST()
	{
		return _class_ast;
	}

	public CompilationUnit getCompilationUnit()
	{
		return _cu_ast;
	}

	public IFile getClassFile()
	{
		return _class_file;
	}

	public ArrayList<MethodDecl> getMethods()
	{
		ArrayList<MethodDecl> methods = new ArrayList<MethodDecl>();

		for (BodyDecl body : _class_ast.getBodyDeclList())
		{
			if (body instanceof MethodDecl)
			{
				methods.add((MethodDecl) body);
			}
		}

		return methods;
	}

	public ArrayList<FieldDecl> getFields()
	{
		ArrayList<FieldDecl> fields = new ArrayList<FieldDecl>();

		for (BodyDecl body : _class_ast.getBodyDeclList())
		{
			if (body instanceof FieldDecl)
			{
				fields.add((FieldDecl) body);
			}
		}

		return fields;
	}

	public String toString()
	{
		return getFeatureName() + "." + getClassName();
	}

	public boolean needsUpdate()
	{
		return _class_file_modification_stamp != _class_file.getModificationStamp();
	}
}
