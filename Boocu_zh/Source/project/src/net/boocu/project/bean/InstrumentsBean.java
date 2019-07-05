package net.boocu.project.bean;

import java.util.ArrayList;
import java.util.List;

import net.boocu.project.entity.InstrumentEntity;

public class InstrumentsBean {
	
	private List<InstrumentEntity> instruments = new ArrayList<InstrumentEntity>();

	public List<InstrumentEntity> getInstruments() {
		return instruments;
	}

	public void setInstruments(List<InstrumentEntity> instruments) {
		this.instruments = instruments;
	}
	
}
