/*
 * AggregatedResource.java
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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dspace.foresite.AggregatedResource;
import org.dspace.foresite.Aggregation;
import org.dspace.foresite.OREException;
import org.dspace.foresite.Predicate;
import org.dspace.foresite.Proxy;
import org.dspace.foresite.Triple;
import org.dspace.foresite.TripleSelector;
import org.dspace.foresite.Vocab;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;

/**
 * @Author Richard Jones
 */
public class AggregatedResourceJena extends OREResourceJena implements AggregatedResource
{
    public AggregatedResourceJena()
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

	public List<Triple> listTriples(TripleSelector selector) throws OREException
	{
		selector.setSubjectURI(this.getURI());
		return super.listTriples(selector);
	}

	///////////////////////////////////////////////////////////////////
    // from AggregatedResource
    ///////////////////////////////////////////////////////////////////

    public void initialise(URI uri)
			throws OREException
	{
		// FIXME: are we 100% sure that this is a valid way of determining
		// protocol-based-ness.  See RFC3986 for reference.
		//
		// we need to ensure that the URI is protocol based
		String ident = uri.toString();
		String rx = ".+://.+";
		Pattern p = Pattern.compile(rx);
		Matcher m = p.matcher(ident);
		if (!m.matches())
		{
			throw new OREException("Illegal URI: " + uri.toString() + "; AggregatedResource requires a protocol-based URI");
		}

		res = model.createResource(uri.toString());
        res.addProperty(RDF.type, ORE.AggregatedResource);
    }

    public List<URI> getAggregations()
            throws OREException
    {
        try
        {
            List<URI> uris = new ArrayList<URI>();
            StmtIterator itr = res.listProperties(ORE.isAggregatedBy);
            while (itr.hasNext())
            {
                Statement statement = itr.nextStatement();
                String value = ((Literal) statement.getObject()).getLexicalForm();
                uris.add(new URI(value));
            }
            return uris;
        }
        catch (URISyntaxException e)
        {
            throw new OREException(e);
        }
    }

    public void setAggregations(List<URI> aggregations)
    {
        this.clearAggregations();
        for (URI agg : aggregations)
        {
            this.addAggregation(agg);
        }
    }

    public void addAggregation(URI aggregation)
    {
        res.addProperty(ORE.isAggregatedBy, model.createTypedLiteral(aggregation));
    }

    public void clearAggregations()
    {
		StmtIterator itr = res.listProperties(ORE.isAggregatedBy);
		model.remove(itr);
	}

    public Aggregation getAggregation()
            throws OREException
    {
        // FIXME: this may not work, but it's the principle
        try
        {
            ResIterator itr = model.listSubjectsWithProperty(ORE.aggregates, res);
            if (itr.hasNext())
            {
                Resource resource = itr.nextResource();
                Aggregation agg = JenaOREFactory.createAggregation(model, new URI(resource.getURI()));
                return agg;
            }
            return null;
        }
        catch (URISyntaxException e)
        {
            throw new OREException(e);
        }
    }

    public void setTypes(List<URI> types)
    {
        super.setTypes(types);

		// ensure that the required type is still set
		Selector selector = new SimpleSelector(res, RDF.type, ORE.Aggregation);
		StmtIterator itr = model.listStatements(selector);
		if (!itr.hasNext())
		{
			res.addProperty(RDF.type, ORE.AggregatedResource);
		}
	}

    public void clearTypes()
    {
		// leave it to OREResource to handle type clearance
		super.clearTypes();

		// ensure that the required type is still set
		res.addProperty(RDF.type, ORE.AggregatedResource);
	}

	public List<URI> getResourceMaps()
			throws OREException
	{
		try
		{
			List<URI> rems = new ArrayList<URI>();
			StmtIterator itr = res.listProperties(ORE.isDescribedBy);
			while (itr.hasNext())
			{
				Statement statement = itr.nextStatement();
				Resource resource = (Resource) statement.getObject();
				rems.add(new URI(resource.getURI()));
			}
			return rems;
		}
		catch (URISyntaxException e)
		{
			throw new OREException(e);
		}
	}

	public void setResourceMaps(List<URI> rems) throws OREException
	{
		this.clearResourceMaps();
		for (URI rem : rems)
		{
			this.addResourceMap(rem);
		}
	}

	public void addResourceMap(URI rem)
			throws OREException
	{
		res.addProperty(ORE.isDescribedBy, model.createResource(rem.toString()));
	}

	public void clearResourceMaps() throws OREException
	{
		StmtIterator itr = res.listProperties(ORE.isDescribedBy);
		model.remove(itr);
	}

	public boolean hasProxy()
			throws OREException
	{
		Selector selector = new SimpleSelector(null, ORE.proxyFor, res);
		StmtIterator itr = model.listStatements(selector);
		if (itr.hasNext())
		{
			return true;
		}
		return false;
	}

	public Proxy getProxy()
			throws OREException
	{
		try
		{
			Selector selector = new SimpleSelector(null, ORE.proxyFor, res);
			StmtIterator itr = model.listStatements(selector);
			if (itr.hasNext())
			{
				Statement statement = itr.nextStatement();
				Proxy proxy = JenaOREFactory.createProxy(model, new URI(statement.getSubject().getURI()));
				return proxy;
			}
			return null;
		}
		catch (URISyntaxException e)
		{
			throw new OREException(e);
		}
	}

	public Proxy createProxy(URI proxyURI)
			throws OREException
	{
		Aggregation agg = this.getAggregation();
		if (agg == null)
		{
			throw new OREException("Cannot create a Proxy in an AggregatedResource which does not belong to an Aggregation");
		}

		Proxy proxy = JenaOREFactory.createProxy(model, proxyURI);
		proxy.setProxyForURI(this.getURI());
		proxy.setProxyInURI(agg.getURI());

		return proxy;
	}

	public Vocab getOREType() throws OREException
	{
		return Vocab.ore_AggregatedResource;
	}

    public void addIdentifier(String identifier) throws OREException {
        Predicate pred = new Predicate(Vocab.dcterms_Identifier);
        createTriple(pred, identifier);
        
    }

    public void addFormat(String format) throws OREException {
        Predicate pred = new Predicate(Vocab.dc_format);
        createTriple(pred, format);
    }

    public void addDescription(String description) throws OREException {
        Predicate pred = new Predicate(Vocab.dcterms_Description);
        createTriple(pred, description);
    }

    public void addCreated(Date created) throws OREException {
        Predicate pred = new Predicate(Vocab.dcterms_Created);
        createTriple(pred, created);
    }

    public void addModified(Date modified) throws OREException {
        Predicate pred = new Predicate(Vocab.dcterms_Modified);
        createTriple(pred, modified);
    }
}
