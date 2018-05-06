package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlType(name = "BugReportSearchCriterion")
@XmlAccessorType(XmlAccessType.NONE)
public class BugReportSearchCriterion extends SearchCriterion
{
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

	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getReportedBy()
	{
		return reportedBy;
	}
	public void setReportedBy(String reportedBy)
	{
		this.reportedBy = reportedBy;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public String getResolution()
	{
		return resolution;
	}
	public void setResolution(String resolution)
	{
		this.resolution = resolution;
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
	public String getJavaVersion()
	{
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion)
	{
		this.javaVersion = javaVersion;
	}
	public String getSummary()
	{
		return summary;
	}
	public void setSummary(String summary)
	{
		this.summary = summary;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
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
}