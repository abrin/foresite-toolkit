/*
 * JenaOREParser.java
 *
 * Copyright (c) 2008, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package org.dspace.foresite.jena;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.dspace.foresite.OREException;
import org.dspace.foresite.OREParser;
import org.dspace.foresite.OREParserException;
import org.dspace.foresite.ResourceMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @Author Richard Jones
 */
public class JenaOREParser implements OREParser
{
    private String type = "RDF/XML";

    public ResourceMap parse(InputStream is)
			throws OREParserException
    {
        try
        {
            Model model = this.parseToModel(is);
            Selector selector = new SimpleSelector(null, ORE.describes, (RDFNode) null);
            StmtIterator itr = model.listStatements(selector);
            if (itr.hasNext())
            {
                Statement statement = itr.nextStatement();
                Resource resource = (Resource) statement.getSubject();
                ResourceMap rem = JenaOREFactory.createResourceMap(model, new URI(resource.getURI()));
                return rem;
            }

            return null;
        }
        catch (URISyntaxException e)
        {
            throw new OREParserException(e);
        }
		catch (OREException e)
		{
			throw new OREParserException(e);
		}
	}

	public ResourceMap parse(InputStream is, URI uri)
			throws OREParserException
	{
		try
		{
			Model model = this.parseToModel(is);
			ResourceMap rem = JenaOREFactory.createResourceMap(model, uri);
			return rem;
		}
		catch (OREException e)
		{
			throw new OREParserException(e);
		}
	}

	public void configure(Properties properties)
    {
        this.type = properties.getProperty("type");
    }

	///////////////////////////////////////////////////////////////////
	// Private methods
	///////////////////////////////////////////////////////////////////

	private Model parseToModel(InputStream is)
	{
		Model model = ModelFactory.createDefaultModel();
        model = model.read(is, null, type);
		return model;
	}
}
