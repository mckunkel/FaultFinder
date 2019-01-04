/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package faultfinder.objects;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CCDBWireStatusObject implements Comparable<CCDBWireStatusObject> {
	private static final long serialVersionUID = 1L;

	private int sector;
	private int layer;
	private int component;
	private int status;

	public CCDBWireStatusObject() {
	}

	public CCDBWireStatusObject(int sector, int layer, int component, int status) {
		super();
		this.sector = sector;
		this.layer = layer;
		this.component = component;
		this.status = status;
	}

	public int getSector() {
		return sector;
	}

	public void setSector(int sector) {
		this.sector = sector;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getComponent() {
		return component;
	}

	public void setComponent(int component) {
		this.component = component;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("sector", sector);
		builder.append("layer", layer);
		builder.append("component", component);
		builder.append("status", status);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + component;
		result = prime * result + layer;
		result = prime * result + sector;
		result = prime * result + status;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCDBWireStatusObject other = (CCDBWireStatusObject) obj;
		if (component != other.component)
			return false;
		if (layer != other.layer)
			return false;
		if (sector != other.sector)
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public int compareTo(final CCDBWireStatusObject other) {
		return new CompareToBuilder().append(component, other.component).append(layer, other.layer)
				.append(sector, other.sector).append(status, other.status).toComparison();
	}

}
