package org.ddosm;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;

@Entity
public class SimulationEntity {

	@Id
	private String id;
	
	private Text parametersHash;
	private Text resultHash;
	
	private long submitted;
	private long started;
	private long finished;
	
	
	public Text getParametersHash() {
		return parametersHash;
	}
	public void setParametersHash(Text parametersHash) {
		this.parametersHash = parametersHash;
	}
	public Text getResultHash() {
		return resultHash;
	}
	public void setResultHash(Text resultHash) {
		this.resultHash = resultHash;
	}
	public long getStarted() {
		return started;
	}
	public void setStarted(long started) {
		this.started = started;
	}
	public long getFinished() {
		return finished;
	}
	public void setFinished(long finished) {
		this.finished = finished;
	}
	public long getSubmitted() {
		return submitted;
	}
	public void setSubmitted(long submitted) {
		this.submitted = submitted;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
