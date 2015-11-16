/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.data.label;

/**
 * It value consisting of a real value. It can be used for a regression tasks or
 * in classification task where classes are identified with numbers
 * 
 * @author Simone Filice
 */
public class NumericLabel implements Label {
	
	private static final long serialVersionUID = 9106999750323023354L;
	private float value;
	private Label property;

	/**
	 * Initializes a NumericLabel whose value is <code>labelValue</code> and
	 * whose name is <code>name</code>
	 * 
	 * @param name
	 *            the property name of the label
	 * @param labelValue
	 *            the value of the label
	 */
	public NumericLabel(Label property, float labelValue) {
		this.property = property;
		this.value = labelValue;
	}

	public NumericLabel() {

	}

	/**
	 * Returns the value of the value
	 * 
	 * @return the value of the value
	 */
	public float getValue() {
		return this.value;
	}

	/**
	 * Returns the property
	 * 
	 * @return the property
	 */
	public Label getProperty() {
		return this.property;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(float value) {
		this.value = value;
	}

	/**
	 * @param property
	 *            the property to set
	 */
	public void setProperty(Label property) {
		this.property = property;
	}

	// @Override
	// public boolean equals(Object label){
	// if(label==null){
	// return false;
	// }
	// if(this==label){
	// return true;
	// }
	// if(label instanceof NumericLabel){
	// NumericLabel that = (NumericLabel) label;
	// return (that.getValue()==this.getValue() &&
	// this.labelName.equals(that.getLabelName()));
	// }
	//
	// return false;
	// }

	@Override
	public String toString() {
		String ret = this.property.toString() + LabelFactory.NAME_VALUE_SEPARATOR;
		return ret + Float.toString(this.value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumericLabel other = (NumericLabel) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

}
