package rs.ruta.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

@XmlRootElement(name = "ReportAttachment", namespace = "urn:rs:ruta:common")
@XmlType(name = "ReportAttachment")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportAttachment
{

	@XmlTransient
	private byte[] rawData;

	@XmlMimeType("application/octet-stream")
	@XmlElement(name = "DataHandler")
	private DataHandler dataHandler;

	@XmlElement (name = "Name")
	private String name;

	@XmlElement(name = "ID")
	private String id; // attachment's id

	@XmlElement(name = "OwnerID")
	private String ownerId; // report owner's id

	public ReportAttachment() { }

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
	}

	/**Constructs the {@code Attachment} from the {@code File}.
	 * @param file file to be set as attachment
	 * @param name file's name
	 * @throws IOException if the data could not be retrieved from the file
	 */
	public ReportAttachment(File file, String name) throws IOException
	{
		setRawData(file);
		setDataHandler(file);
		this.name = name;
	}

	/**Constructs the {@code Attachment} from the {@code File}.
	 * @param file file to be set as attachment
	 * @throws IOException if the data could not be retrieved from the file
	 */
	public ReportAttachment(File file) throws IOException
	{
		setRawData(file);
		setDataHandler(file);
		this.name = file.getName();
	}

	public void setRawData(byte[] rawData)
	{
		this.rawData = rawData;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**Gets the raw data as byte array from the {@link File}.
	 * @param file {@code File} which contents is examined
	 * @return byte array representing the File contents
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

	/**Gets the raw data as byte array from the {@link File}.
	 * @param file {@code File} which contents is examined
	 * @return byte array representing the File contents
	 * @throws IOException if the data could not be retrieved from the file
	 */
	private void setDataHandler(File file) throws IOException
	{
		String path = file.getPath();
        FileDataSource fileDataSource = new FileDataSource(path);
        dataHandler = new DataHandler(fileDataSource);
	}


	/**Constructs a file from the {@link DataHandler} field.
	 * @return a file
	 * @throws IOException if file coud not be constructed
	 */
	public File getFile() throws IOException
	{
		File file = new File(name);
/*		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			bout.write(rawData);
		bout.close();*/

		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
		BufferedInputStream bin = new BufferedInputStream(dataHandler.getInputStream());
		byte buffer[] = new byte[1024];
		int n = 0;
		while((n = bin.read(buffer)) != -1)
			bout.write(buffer, 0, n);
		bin.close();
		bout.close();

		return file;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(String ownerId)
	{
		this.ownerId = ownerId;
	}

	public byte[] getRawData()
	{
		return rawData;
	}

	public String getName()
	{
		return name;
	}

	public DataHandler getDataHandler()
	{
		return dataHandler;
	}

	public void setDataHandler(DataHandler dataHandler)
	{
		this.dataHandler = dataHandler;
	}
}
