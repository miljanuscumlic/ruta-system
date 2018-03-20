package rs.ruta;

import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import rs.ruta.client.MyParty;

public class ClientHandlerResolver implements HandlerResolver
{
	private MyParty myParty;

	public ClientHandlerResolver(MyParty myParty)
	{
		this.myParty = myParty;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo)
	{
		List<Handler> handlerChain = new ArrayList<Handler>();
//		handlerChain.add(new ClientExcludeHandler());
		handlerChain.add(new ClientSignatureHandler(myParty));
		return handlerChain;
	}

}
