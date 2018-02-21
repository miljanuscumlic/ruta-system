package rs.ruta.client;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.InstanceFactory;

@XmlAccessorType(XmlAccessType.FIELD)
public class Search<T>
{
	@XmlTransient
	private static long num; // number of the next search
	@XmlElement(name = "SearchName")
	private String searchName;
	@XmlElement(name = "Timestamp")
	private XMLGregorianCalendar timestamp;
	@XmlElement(name = "SearchCriterion")
	private CatalogueSearchCriterion criterion;
	@XmlElement(name= "SearchResult")
	private List<T> results;
	@XmlElement(name = "ResultType")
	private Class<T> resultType;

	public Search() {}

	public Search(String searchName)
	{
		this.searchName = searchName;
	}

	public Search(String searchName, CatalogueSearchCriterion criterion, List<T> results, Class<T> resultType)
	{
		this.searchName = searchName;
		this.criterion = criterion;
/*		if(results == null)
			this.results = new ArrayList<T>();
		else*/
			this.results = results;
		this.resultType = resultType;
	}

	/**Gets the number of the next search.
	 * @return
	 */
	public static long getSearchNum()
	{
		return num;
	}

	/**Sets the number of the next search.
	 * @param num
	 */
	public static void setSearchNumber(long num)
	{
		Search.num = num;
	}

	/**Decreases number of searches but not to less tnah zero.
	 *
	 */
	public static void decreaseSearchNumber()
	{
		Search.num = Search.num != 0 ? Search.num - 1 : 0;
	}

	/**Gets the next search name based on the next search number. Also increments the counter.
	 * @return
	 */
	public static String getNextSearchName()
	{
		return "Search_" + getNextSearchNumber();
	}

	/**Gets the next search number and increments the counter.
	 * @return
	 */
	public static long getNextSearchNumber()
	{
		return num++;
	}

	public CatalogueSearchCriterion getCriterion()
	{
		return criterion;
	}

	public void setCriterion(CatalogueSearchCriterion criterion)
	{
		this.criterion = criterion;
	}

	public List<T> getResults()
	{
		return results;
	}

	/**
	 * Gets the {@link Class} object of the containing search result.
	 * @return search result's {@code Class} object
	 */
	public Class<T> getResultType()
	{
		return resultType;
	}

	public void setResultType(Class<T> resultType)
	{
		this.resultType = resultType;
	}

	/**
	 * Sets the {@link List} of results. If passed argument is a {@code null}, appropriate empty
	 * list is set instead.
	 * @param results list of reselts to be set
	 */
	public void setResults(List<T> results)
	{
/*		if(results == null)
			this.results = new ArrayList<T>();
		else*/
			this.results = results;
	}

	/**
	 * Gets the number of results in a {@code Search}.
	 * @return size of the result list
	 */
	public int getResultCount()
	{
		return results != null ? results.size() : 0;
	}

	/**Get the name of the search.
	 * @return
	 */
	public String getSearchName()
	{
		return searchName;
	}

	/**Sets the name of the search.
	 * @param name
	 */
	public void setSearchName(String name)
	{
		this.searchName = name;
	}

	/* Returns the name of the search. Used as the node name in tree models.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return searchName;
	}

	public XMLGregorianCalendar getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(XMLGregorianCalendar timestamp)
	{
		this.timestamp = timestamp;
	}

	/**Sets the time of the search to now.
	 */
	public void setTimestamp()
	{
		timestamp = InstanceFactory.getDate();
	}

	/**Gets the String represantation of the search timestamp.
	 * @return
	 */
	public String getTimestampAsString()
	{
		return InstanceFactory.getLocalDateTimeAsString(timestamp);
	}

}
