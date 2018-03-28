package rs.ruta.client.gui;

import java.awt.Color;

public class ConsoleData
{
	private StringBuilder message;
	private Color color;

	public ConsoleData()
	{}

	public ConsoleData(StringBuilder message, Color color)
	{
		this.message = message;
		this.color = color;
	}

	public StringBuilder getMsg()
	{
		return message;
	}

	public void setMsg(StringBuilder msg)
	{
		this.message = msg;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
}