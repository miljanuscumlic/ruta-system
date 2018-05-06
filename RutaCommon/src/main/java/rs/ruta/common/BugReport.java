package rs.ruta.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**Class containing data about a bug reported by the user. Most of the fields are
 *filled by the user submitting the bug, but some are added on the service side.
 */
@XmlRootElement(name = "BugReport")
@XmlType(name = "BugReport")
@XmlAccessorType(XmlAccessType.NONE)
public class BugReport
{
//	@XmlTransient
	private static final String[] STATUS = {"NEW", "UNCONFIRMED", "CONFIRMED", "ASSIGNED", "REOPENED", "VERIFIED", "RESOLVED", "CLOSED"};
//	@XmlTransient
	private static final String[] SEVERITY = {"BLOCKER", "CRITICAL", "MAJOR", "NORMAL", "MINOR", "TRIVIAL", "ENHANCEMENT"};
//	@XmlTransient
	private static final String[] RESOLUTION = {"FIXED", "DUPLICATE", "WONTFIX", "WORKSFORME", "INVALID"};
	@XmlElement(name = "ID")
	private String id;
	@XmlElement(name= "ReportedBy")
	private String reportedBy;
	@XmlElement(name = "Status")
	private String status;
	@XmlElement(name = "Resolution")
	private String resolution;
	@XmlElement(name = "Product")
	private String product;
	@XmlElement(name = "Component")
	private String component;
	@XmlElement(name = "Version")
	private String version;
	@XmlElement(name = "Platform")
	private String platform;
	@XmlElement(name = "OperatingSystem")
	private String os;
	@XmlElement(name ="JavaVersion")
	private String javaVersion;
	@XmlElement(name = "Summary")
	private String summary;
	@XmlElement(name = "Description")
	private String description;
	@XmlElement(name = "Reported")
	private XMLGregorianCalendar reported;
	@XmlElement(name = "Modified")
	private XMLGregorianCalendar modified;
	@XmlElement(name = "Priority")
	private String priority;
	@XmlElement(name = "Severity")
	private String severity;
	@XmlElement(name = "Attachment")
	private List<ReportAttachment> attachments;
	@XmlElement(name = "Comment")
	private List<ReportComment> comments;
	@XmlElement(name = "NextCommentNumber")
	private int nextCommentNum;

	public BugReport() { }

	/**Initialize some {@code BugReport} fiedls to default values
	 */
	public void initialize()
	{
		product = "Ruta";
		platform = platform();
		os = os();
		javaVersion = java();
		status = "UNCONFIRMED";
		nextCommentNum = 0;
	}

	public int getCommentCount()
	{
		return nextCommentNum;
	}

	public void setCommentCount(int commentCount)
	{
		this.nextCommentNum = commentCount;
	}

	public String getJavaVersion()
	{
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion)
	{
		this.javaVersion = javaVersion;
	}

	public XMLGregorianCalendar getReported()
	{
		return reported;
	}

	public void setReported(XMLGregorianCalendar reported)
	{
		this.reported = reported;
	}

	public XMLGregorianCalendar getModified()
	{
		return modified;
	}

	public void setModified(XMLGregorianCalendar modified)
	{
		this.modified = modified;
	}

/*	public XMLGregorianCalendar getCreated()
	{
		return created;
	}

	public void setCreated(XMLGregorianCalendar created)
	{
		this.created = created;
	}*/

	public String getSummary()
	{
		return summary;
	}
	public void setSummary(String summary)
	{
		this.summary = summary;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getProduct()
	{
		return product;
	}
	public void setProduct(String product)
	{
		this.product = product;
	}
	public String getComponent()
	{
		return component;
	}
	public void setComponent(String component)
	{
		this.component = component;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getPlatform()
	{
		return platform;
	}
	public void setPlatform(String platform)
	{
		this.platform = platform;
	}
	public String getOs()
	{
		return os;
	}
	public void setOs(String os)
	{
		this.os = os;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**Gets the list of appended {@link ReportAttachment}s to the {@code BugReport}.
	 * If the list has not been created before the moment of invocation, this method creates an empty list.
	 * @return list of attachemnts
	 */
	public List<ReportAttachment> getAttachments()
	{
		if(attachments == null)
			attachments = new ArrayList<>();
		return attachments;
	}

	public void setAttachments(List<ReportAttachment> attachments)
	{
		this.attachments = attachments;
	}

	/**Gets the list of appended {@link ReportComment}s to the {@code BugReport}.
	 * If the list has not been created before the moment of invocation, this method creates an empty list.
	 * @return list of comments
	 */
	public List<ReportComment> getComments()
	{
		if(comments == null)
			comments = new ArrayList<>();
		return comments;
	}

	public synchronized void setComments(List<ReportComment> comments)
	{
		this.comments = comments;
	}

	public void addComment(ReportComment comment)
	{
		comment.setId(nextCommentId());
		getComments().add(comment);
	}

	public synchronized boolean removeComment(ReportComment comment)
	{
		boolean removed = false;
		List<ReportComment> comms = getComments();
		if(comms.size() != 0)
		{
			removed = comms.remove(comment);
			previousCommentId();
		}
		return removed;
	}

	public synchronized int nextCommentId()
	{
		return nextCommentNum++;
	}

	public synchronized int previousCommentId()
	{
		return --nextCommentNum;
	}

	public String getReportedBy()
	{
		return reportedBy;
	}

	public void setReportedBy(String reportedBy)
	{
		this.reportedBy = reportedBy;
	}

	public String getResolution()
	{
		return resolution;
	}

	public void setResolution(String resolution)
	{
		this.resolution = resolution;
	}

	public String getPriority()
	{
		return priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public String getSeverity()
	{
		return severity;
	}

	public void setSeverity(String severity)
	{
		this.severity = severity;
	}

	public String getID()
	{
		return id;
	}

	public void setID(String id)
	{
		this.id = id;
	}

	/**Gets operation system specific data of the client machine.
	 * @return string containing OS identification
	 */
	private String os()
	{
		return new StringBuilder(System.getProperty("os.name")).append(" | ").
				append(System.getProperty("os.arch")).append(" | ").
				append(System.getProperty("os.version")).toString();
	}

	/**Gets data about java of the client machine.
	 * @return string containing java identification
	 */
	private String java()
	{
		return new StringBuilder(System.getProperty("java.vendor")).append(" | ").append(System.getProperty("java.version")).
				append(" | ").append(System.getProperty("sun.arch.data.model")).append("bit").toString();
	}

	/**Gets platform i.e hardware specific data of the client machine.
	 * @return string containing hardware identification
	 */
	private String platform()
	{
		return new StringBuilder(System.getenv("PROCESSOR_IDENTIFIER")).append(" | ").
				append(System.getenv("PROCESSOR_ARCHITECTURE")).append(" | ").
				append(System.getenv("PROCESSOR_ARCHITEW6432")).append(" | ").
				append(System.getenv("NUMBER_OF_PROCESSORS")).toString();
	}
}
