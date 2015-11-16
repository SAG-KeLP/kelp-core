package it.uniroma2.sag.kelp.learningalgorithm.clustering.kmeans;

import com.fasterxml.jackson.annotation.JsonTypeName;

import it.uniroma2.sag.kelp.data.clustering.ClusterExample;
import it.uniroma2.sag.kelp.data.example.Example;

@JsonTypeName("linearkmeansexample")
public class LinearKMeansExample extends ClusterExample {

	private static final long serialVersionUID = 4309082543662353543L;

	public LinearKMeansExample() {
		super();
	}

	public LinearKMeansExample(Example e, float dist) {
		super(e, dist);
	}

	@Override
	public Example getExample() {
		return example;
	}

}
