package rs.ruta.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import rs.ruta.common.SearchCriterion;

@XmlAccessorType(XmlAccessType.FIELD)
public class Search<T>
{
	@XmlElement(name = "SearchName")
	private String searchName;
	@XmlTransient
	private static long num; // number of the next search
	@XmlElement(name = "SearchCriterion")
	private SearchCriterion criterion;
	@XmlElement(name= "SearchResult")
	private List<T> results;

	public Search() {}

	public Search(String searchName)
	{
		this.searchName = searchName;
	}

	public Search(String searchName, SearchCriterion criterion, List<T> results)
	{
		this.searchName = searchName;
		this.criterion = criterion;
		this.results = results;
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

	public void setResults(List<T> results)
	{
		this.results = results;
	}

	public Class<?> getResultClass()
	{
		return results.get(0).getClass();
	}

	public int size()
	{
		return results.size();
	}

	/**Get the name of teh search
	 * @return
	 */
	public String getSearchName()
	{
		return searchName;
	}

	/**Sets the name of the search
	 * @param name
	 */
	public void setSearchName(String name)
	{
		this.searchName = name;
	}

	/**Gets the number of results in a {@code Search}.
	 * @return
	 */
	public int getResultCount()
	{
		return results.size();
	}

	/* Returns the name of the search. Used as the node name in tree models.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return searchName; //MMM: temporary solution with "Search" string
	}

}
