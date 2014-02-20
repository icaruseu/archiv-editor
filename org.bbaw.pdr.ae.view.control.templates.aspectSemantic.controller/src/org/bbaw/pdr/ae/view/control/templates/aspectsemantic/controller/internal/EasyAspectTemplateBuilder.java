/**
 * This file is part of Archiv-Editor.
 * 
 * The software Archiv-Editor serves as a client user interface for working with
 * the Person Data Repository. See: pdr.bbaw.de
 * 
 * The software Archiv-Editor was developed at the Berlin-Brandenburg Academy
 * of Sciences and Humanities, Jägerstr. 22/23, D-10117 Berlin.
 * www.bbaw.de
 * 
 * Copyright (C) 2010-2013  Berlin-Brandenburg Academy
 * of Sciences and Humanities
 * 
 * The software Archiv-Editor was developed by @author: Christoph Plutte.
 * 
 * Archiv-Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Archiv-Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Archiv-Editor.  
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.bbaw.pdr.ae.view.control.templates.aspectsemantic.controller.internal;

import org.bbaw.pdr.ae.config.model.SemanticTemplate;
import org.bbaw.pdr.ae.model.Aspect;
import org.bbaw.pdr.ae.model.Person;
import org.bbaw.pdr.ae.view.control.customSWTWidges.AspectMarkupTemplate;
import org.bbaw.pdr.ae.view.control.interfaces.IAEBasicEditor;
import org.bbaw.pdr.ae.view.control.interfaces.IEasyAspectEditor;
import org.bbaw.pdr.ae.view.control.templates.aspectsemantic.controller.AspectSemanticTemplateBuilder;
import org.eclipse.swt.widgets.Composite;

public class EasyAspectTemplateBuilder
{

	public IEasyAspectEditor buildEasyAspectEditor(IAEBasicEditor parentEditor, SemanticTemplate semanticTemplate,
			Person currentPerson, Aspect currentAspect,
			Composite control, int style)
	{
		EasyAspectTemplateEditor editor = new EasyAspectTemplateEditor(parentEditor, currentPerson, currentAspect,
				control,
				style);
		AspectMarkupTemplate template = new AspectSemanticTemplateBuilder().buildTemplate(editor, semanticTemplate,
				editor.getMarkupControl());
		editor.setMarkupTemplate(template);
		return editor;
	}

}