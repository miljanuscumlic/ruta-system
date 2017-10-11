package rs.ruta.server;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;


public class Documents
{
	private Queue<CatalogueType> documents;
	private ServletContext sctx;
	private AtomicInteger mapKey;
	private static int queueSize = 10;


	public Documents()
	{
		documents = new ArrayBlockingQueue<CatalogueType>(queueSize);
		mapKey = new AtomicInteger();
	}

	public void setServletContext(ServletContext sctx)
	{
		this.sctx = sctx;
	}

	public ServletContext getServletContext()
	{
		return sctx;
	}

	public void setMap(Queue<CatalogueType> docs)
	{
		documents = docs;
	}

	public Queue<CatalogueType> getMap()
	{
		if (isContextSet())
			return documents;
		else
			return null;
	}

	private boolean isContextSet()
	{
		return sctx == null ? false : true;
	}

	public boolean addDocument(CatalogueType d)
	{
		return documents.offer(d);
	}

	public CatalogueType removeDocument()
	{
		return documents.poll();
	}
}
