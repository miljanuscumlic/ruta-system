package rs.ruta.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**Attachment added to a {@link BugReport}.
 */
@XmlRootElement(name = "ReportAttachment", namespace = "urn:rs:ruta:common")
@XmlType(name = "ReportAttachment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportAttachment
{

	@XmlTransient
	private byte[] rawData; //MMM: this field is not used ???

	@XmlMimeType("application/octet-stream")
	@XmlElement(name = "DataHandler")
	private DataHandler dataHandler;

	@XmlElement (name = "Name")
	private String name;

	/** attachment's id */
	@XmlElement(name = "ID")
	private String id;

	/** report owner's id */
	@XmlElement(name = "OwnerID")
	private String ownerId;

	public ReportAttachment() { }

/*	MMM: NOT USED
 	public ReportAttachment(byte[] data, String name)
	{
		rawData = data;
		this.name = name;
	}

	public ReportAttachment(byte[] data, String name, String id, String ownerId)
	{
		this(data, name);
		this.id = id;
		this.ownerId = ownerId;
	}*/

	/**Constructs the {@code ReportAttachment} from the {@code File}.
	 * @param file file to be set as attachment
	 * @param name file's name
	 * @throws IOException if the data could not be retrieved from the file
	 */
	public ReportAttachment(File file, String name) throws IOException //MMM: this constructor is only user for the test sending image to the CDR - delete it
	{
		setRawData(file);
		setDataHandler(file);
		this.name = name;
	}

	/**Constructs the {@code ReportAttachment} from the {@code File}.
	 * @param file file to be set as attachment
	 * @throws IOException if the data could not be retrieved from the file
	 */
	public ReportAttachment(File file) throws IOException
	{
		setRawData(file);
		setDataHandler(file);
		this.name = file.getName();
	}

	/**Sets the raw data of the {@code ReportAttachment} passed as a byte array.
	 * @param rawData raw data to be set
	 */
	public void setRawData(byte[] rawData)
	{
		this.rawData = rawData;
	}

	/**Sets the name of the {@code ReportAttachment}.
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**Sets the raw data as byte array from the {@link File}.
	 * @param file {@code File} which contents is examined
	 * @throws IOException if the data could not be retrieved from the file
	 */
	private void setRawData(File file) throws IOException
	{
		long length = file.length();
		String path = file.getPath();
        FileDataSource fileDataSource = new FileDataSource(path);
        DataHandler dataHandler = new DataHandler(fileDataSource);
        BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
        rawData = new byte[(int)length];
        bin.read(rawData);
        bin.close();
	}

	/**Sets the {@link DataHandler} of the passed {@link File}.
	 * @param file {@code File} to which {@code DataHandler} is hooked
	 * @throws IOException if the data could not be retrieved from the file
	 */
	private void setDataHandler(File file) throws IOException
	{
		String path = file.getPath();
        FileDataSource fileDataSource = new FileDataSource(path);
        dataHandler = new DataHandler(fileDataSource);
	}

	/**Creates a {@link File} representing the {@code ReportAttachment} writing it to a file system.
	 * @param filePath path of the file in the file system
	 * @return created {@code File}
	 * @throws IOException if file could not be opened or created, or data could be read from the source or written to a file
	 */
	public File createFile(String filePath) throws IOException
	{
		File file = new File(filePath);
		try(BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
				BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream()))
		{
			byte buffer[] = new byte[1024];
			int n = 0;
			while((n = bin.read(buffer)) != -1)
				bout.write(buffer, 0, n);
		}
		return file;
	}

	/**Gets {@code ReportAttachment}'s ID.
	 * @return ID
	 */
	public String getId()
	{
		return id;
	}

	/**Sets {@code ReportAttachment}'s ID.
	 * @param id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**Gets the {@code ReportAttachment}'s owner ID.
	 * @return owner's ID
	 */
	public String getOwnerId()
	{
		return ownerId;
	}

	/**Sets the {@code ReportAttachment}'s owner ID.
	 * @param ownerId owner's ID
	 */
	public void setOwnerId(String ownerId)
	{
		this.ownerId = ownerId;
	}

	/**Gets the raw data as byte array.
	 * @return byte array representing the {@code Attachment} contents
	 */
	public byte[] getRawData()
	{
		return rawData;
	}

	/**Gets the name of the {@code ReportAttachment}.
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**Gets the {@link DataHandler} object representing the raw data of the {@code ReportAttachment} file.
	 * @return {@link DataHandler} object
	 */
	public DataHandler getDataHandler()
	{
		return dataHandler;
	}

	/**Sets the {@link DataHandler} object representing the raw data of the {@code ReportAttachment} file.
	 */
	public void setDataHandler(DataHandler dataHandler)
	{
		this.dataHandler = dataHandler;
	}

}
