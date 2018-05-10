package rs.ruta.client.correspondence;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class NamespaceTest
{
	public static void main(String[] args)
	{
		try
		{
			BuyingCorrespondence corr = BuyingCorrespondence.newInstance(null, null, "234", true);
			JAXBContext jaxbContext = JAXBContext.newInstance(BuyingCorrespondence.class);
			Marshaller m = (Marshaller) jaxbContext.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(new ObjectFactory().createBuyingCorrespondence(corr), System.out);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}