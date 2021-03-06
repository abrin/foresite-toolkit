/*
 * AgentJena.java
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.dspace.foresite.Agent;
import org.dspace.foresite.OREException;
import org.dspace.foresite.Vocab;

/**
 * @Author Richard Jones
 */
public class AgentJena extends OREResourceJena implements Agent
{
    public AgentJena()
    {
        super();
    }

    ///////////////////////////////////////////////////////////////////
    // Methods from OREResourceJena
    ///////////////////////////////////////////////////////////////////

    public void empty()
    {

    }

	public void detach() throws OREException
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	///////////////////////////////////////////////////////////////////
    // Methods from Agent
    ///////////////////////////////////////////////////////////////////

    public void initialise()
    {
        res = model.createResource(); // blank node
		res.addProperty(RDF.type, model.createResource("http://purl.org/dc/terms/Agent"));
	}

	public void initialise(URI uri)
	{
		res = model.createResource(uri.toString());
		res.addProperty(RDF.type, model.createResource("http://purl.org/dc/terms/Agent"));
	}

	/* Refactored out for 0.9
	public List<URI> getSeeAlso()
            throws OREException
    {
        try
        {
            List<URI> ret = new ArrayList<URI>();
            StmtIterator itr = res.listProperties(RDFS.seeAlso);

            while(itr.hasNext())
            {
                Statement statement = itr.nextStatement();
                URI uri = new URI(((Literal) statement.getObject()).getLexicalForm());
                ret.add(uri);
            }
            return ret;
        }
        catch (URISyntaxException e)
        {
            throw new OREException("Object of rdfs:seeAlso is not a valid URI", e);
        }
    }

    public void addSeeAlso(URI uri)
    {
        res.addProperty(RDFS.seeAlso, model.createTypedLiteral(uri));
    }

    public void setSeeAlso(List<URI> uris)
    {
        this.clearSeeAlso();

        for (URI uri : uris)
        {
            this.addSeeAlso(uri);
        }
    }

    public void clearSeeAlso()
    {
        NodeIterator itr = model.listObjectsOfProperty(res, RDFS.seeAlso);
        while (itr.hasNext())
        {
            RDFNode node = itr.nextNode();
            model.removeAll(res, RDFS.seeAlso, node);
        }
    }*/

    public List<String> getNames()
    {
        List<String> names = new ArrayList<String>();
        StmtIterator itr = res.listProperties(FOAF.name);
        while (itr.hasNext())
        {
            Statement statement = itr.nextStatement();
            names.add(statement.getString());
        }
        return names;
    }

    public void setNames(List<String> names)
    {
        for (String name : names)
        {
            this.addName(name);
        }
    }

    public void addName(String name)
    {
        res.addProperty(FOAF.name, model.createTypedLiteral(name));
    }

    public List<URI> getMboxes()
			throws OREException
	{
		try
		{
			List<URI> mboxes = new ArrayList<URI>();
			StmtIterator itr = res.listProperties(FOAF.mbox);
			while (itr.hasNext())
			{
				Statement statement = itr.nextStatement();
				mboxes.add(new URI(((Resource) statement.getObject()).getURI()));
			}
			return mboxes;
		}
		catch (URISyntaxException e)
		{
			throw new OREException(e);
		}
	}

    public void setMboxes(List<URI> mboxes)
    {
        for (URI mbox : mboxes)
        {
            this.addMbox(mbox);
        }
    }

    public void addMbox(URI mbox)
    {
		// ensure the mbox value is an email URI
		/*
		if (!mbox.startsWith("mailto:"))
		{
			mbox = "mailto:" + mbox;
		}*/
		res.addProperty(FOAF.mbox, model.createResource(mbox.toString()));
    }

	public void setTypes(List<URI> types)
    {
        super.setTypes(types);

		// ensure that the required type is still set
		Selector selector = new SimpleSelector(res, RDF.type, model.createResource("http://purl.org/dc/terms/Agent"));
		StmtIterator itr = model.listStatements(selector);
		if (!itr.hasNext())
		{
			res.addProperty(RDF.type, model.createResource("http://purl.org/dc/terms/Agent"));
		}
	}

    public void clearTypes()
    {
		// leave it to OREResource to handle type clearance
		super.clearTypes();

		// ensure that the required type is still set
		res.addProperty(RDF.type, model.createResource("http://purl.org/dc/terms/Agent"));
	}

	public Vocab getOREType() throws OREException
	{
		return Vocab.dcterms_Agent;
	}

	///////////////////////////////////////////////////////////////////
    // override methods from GraphResource
    ///////////////////////////////////////////////////////////////////

	/* moved up to OREResourceJena
	public void setResource(Resource resource)
    {
        StmtIterator itr = resource.listProperties();
        model.removeAll();
        model.add(itr);

        res = (Resource) resource.inModel(model);
    }*/

	// our version of setModel doesn't need to validate the URI
	public void setModel(Model model, URI resourceURI)
			throws OREException
	{
		this.model = model;
        this.res = model.createResource(resourceURI.toString());
    }
}
