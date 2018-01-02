package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**Comment appended to {@link BugReport}.
 */
@XmlType(name = "ReportComment")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReportComment
{
	@XmlElement(name = "ID")
	private int id;
	@XmlElement(name = "Text")
	private String text;

	public ReportComment() {	}

	public ReportComment(String text)
	{
		this.text = text;
	}

	/**Gets the {@link ReportCommnet}'s ID.
	 * @return ID
	 */
	public int getId()
	{
		return id;
	}

	/**Sets the {@link ReportCommnet}'s ID.
	 * @param id
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**Gets the contents of the {@link ReportComment}.
	 * @return text {@code String}
	 */
	public String getText()
	{
		return text;
	}

	/**Sets the contents of the {@link ReportComment}.
	 * @param text contents of the {@code ReportComment} as {@code String}
	 */
	public void setText(String text)
	{
		this.text = text;
	}

}
