package rs.ruta.client;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.XMLGregorianCalendar;

import rs.ruta.common.InstanceFactory;
import rs.ruta.common.SearchCriterion;

@XmlRootElement(name = "Search")
@XmlAccessorType(XmlAccessType.NONE)
public class Search<T>
{
	@XmlTransient
	private static long num; // number of the next search
	@XmlElement(name = "SearchName")
	private String searchName;
	@XmlElement(name = "Timestamp")
	private XMLGregorianCalendar timestamp;
	@XmlElement(name = "SearchCriterion")
	private SearchCriterion criterion;
	@XmlElement(name= "SearchResult")
	private List<T> results;
	@XmlElement(name = "ResultType")
	private Class<T> resultType;
	@XmlElement(name = "ID")
	private String id;

	public Search() {}

	public Search(String searchName, SearchCriterion criterion, List<T> results, Class<T> resultType)
	{
		this.searchName = searchName;
		this.criterion = criterion;
/*		if(results == null)
			this.results = new ArrayList<T>();
		else*/
			this.results = results;
		this.resultType = resultType;
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public Search<T> clone()
	{
		Search<T> newSearch = new Search<T>();
		newSearch.searchName = searchName;
		newSearch.timestamp = timestamp;
		//newSearch.criterion = criterion.clone();
		newSearch.resultType = resultType;
		newSearch.id = id;
		//MMM: not finished - have to test it


		return newSearch;
	}

	public Search<T> cloneTo(Search<T> newSearch)
	{
		newSearch.searchName = searchName;
		newSearch.timestamp = timestamp;
		//newSearch.criterion = criterion.clone();
		newSearch.resultType = resultType;
		newSearch.id = id;
		//MMM: not finished - have to test it

		return newSearch;
	}

	/**
	 * Transforms a list of elements of generic type {@link Search}{@code <T>} to a list of elements of type
	 * {@code U} that extends {@code Search<T>} generic class.
	 * @param oldList list to transform
	 * @param <T> type of objects that are contained in an element of {@code Search<T>} type
	 * @param <U> type that extends {@code Search<T>} type to which list elements are casted in this method
	 * @return transformed list or {@code null} if input list has a {@code null} value
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> List<U> fromLisfOfGenerics(List<Search<T>> oldList)
	{
		List<U> newList = null;
		if(oldList != null)
			newList = oldList.stream().map(search -> (U) search).collect(Collectors.toList());
		return newList;
	}

	/**
	 * Transforms a list of elements of type {@code U} to a list of elements of generic type {@link Search}{@code <T>}.
	 * @param oldList list to transform
	 * @param <T> type of objects that are contained in an element of {@code Search<T>} type
	 * @param <U> type that extends {@code Search<T>} type to which list elements are casted in this method
	 * @return transformed list or {@code null} if input list has a {@code null} value
	 */
	@SuppressWarnings("unchecked")
	public static <T, U> List<Search<T>> toListOfGenerics(List<U> oldList)
	{
		List<Search<T>> newList = null;
		if(oldList != null)
			newList = oldList.stream().map(search -> (Search<T>) search).collect(Collectors.toList());
		return newList;
	}

	/**
	 * Gets the number of the next search.
	 * @return
	 */
	public static long getSearchNum()
	{
		return num;
	}

	/**
	 * Sets the ordinal number of the next search.
	 * @param num
	 */
	public static void setSearchNumber(long num)
	{
		Search.num = num;
	}

	/**
	 * Decreases number of searches but not to less than zero.
	 */
	public static void decreaseSearchNumber()
	{
		Search.num = Search.num != 0 ? Search.num - 1 : 0;
	}

	/**
	 * Gets the next search name based on the next search number. Also increments the counter.
	 * @return search name
	 */
	public static String getNextSearchName()
	{
		return "Search_" + getNextSearchNumber();
	}

	/**
	 * Gets the next search number and increments the counter.
	 * @return
	 */
	private static long getNextSearchNumber()
	{
		return num++;
	}

	/**
	 * Sets the next search number to 0.
	 */
	public static void resetSearchNumber()
	{
		num = 0;
	}

/*	*//**
	 * Sets the ID. This method must be called after {@link #getNextSearchName()} static method has been called,
	 * not before. I don't like this call dependence!
	 *//*
	public void setNextId()
	{
		uuid = num;
	}*/

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Sets uuid by generating new {@link UUID}.
	 */
	public void setId()
	{
		id = UUID.randomUUID().toString();
	}

	public SearchCriterion getCriterion()
	{
		return criterion;
	}

	public void setCriterion(SearchCriterion criterion)
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

	/**
	 * Gets the name of the search.
	 * @return
	 */
	public String getSearchName()
	{
		return searchName;
	}

	/**
	 * Sets the name of the search.
	 * @param name
	 */
	public void setSearchName(String name)
	{
		this.searchName = name;
	}

	/*
	 * Returns the name of the search. Used as the node name in tree models.
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

	/**
	 * Sets the time of the search to now.
	 */
	public void setTimestamp()
	{
		timestamp = InstanceFactory.getDate();
	}

	/**
	 * Gets the String represantation of the search timestamp.
	 * @return
	 */
	public String getTimestampAsString()
	{
		return InstanceFactory.getLocalDateTimeAsString(timestamp);
	}

}
