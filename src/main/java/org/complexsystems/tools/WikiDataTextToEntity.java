package org.complexsystems.tools;

import org.complexsystems.interfaces.TextToEntity;

public class WikiDataTextToEntity implements TextToEntity{

	private String string;
	
	public WikiDataTextToEntity(String text)
	{
		this.setString(text);
	}
	
	@Override
	public String getEntity() {
		return this.string;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}