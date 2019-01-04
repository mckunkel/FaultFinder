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

import java.sql.Timestamp;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class StatusChangeDB implements Comparable<StatusChangeDB> {
	private static final long serialVersionUID = 1L;

	private int statchangeid;
	private Timestamp dateofentry;
	private int runno;
	private String status_change_type;
	// private Status_change_type status_change_type;
	private String problem_type;
	private String region;
	private String sector;
	private String superlayer;
	private String loclayer;
	private String locwire;
	private String hvcrateid;
	private String hvslotid;
	private String hvchannelid;
	private String hvpinid_region;
	private String hvpinid_quad;
	private String hvpinid_doublet;
	private String hvpinid_doublethalf;

	private String hvpinid_pin;
	private String dcrbconnectorid_slot;
	private String dcrbconnectorid_connector;
	private String lvfuseid_row;
	private String lvfuseid_col;

	public StatusChangeDB() {
	}

	public int getStatchangeid() {
		return statchangeid;
	}

	public void setStatchangeid(int statchangeid) {
		this.statchangeid = statchangeid;
	}

	public Timestamp getDateofentry() {
		return dateofentry;
	}

	public void setDateofentry(Timestamp dateofentry) {
		this.dateofentry = dateofentry;
	}

	public int getRunno() {
		return runno;
	}

	public void setRunno(int runno) {
		this.runno = runno;
	}

	public String getStatus_change_type() {
		return status_change_type;
	}

	public void setStatus_change_type(String status_change_type) {

		this.status_change_type = status_change_type;

	}

	// public Status_change_type getStatus_change_type() {
	// return status_change_type;
	// }
	//
	// public void setStatus_change_type(Status_change_type status_change_type)
	// {
	// this.status_change_type = status_change_type.toString();
	// }

	public String getProblem_type() {
		return problem_type;
	}

	public void setProblem_type(String problem_type) {
		this.problem_type = problem_type;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getSuperlayer() {
		return superlayer;
	}

	public void setSuperlayer(String superlayer) {
		this.superlayer = superlayer;
	}

	public String getLoclayer() {
		return loclayer;
	}

	public void setLoclayer(String loclayer) {
		this.loclayer = loclayer;
	}

	public String getLocwire() {
		return locwire;
	}

	public void setLocwire(String locwire) {
		this.locwire = locwire;
	}

	public String getHvcrateid() {
		return hvcrateid;
	}

	public void setHvcrateid(String hvcrateid) {
		this.hvcrateid = hvcrateid;
	}

	public String getHvslotid() {
		return hvslotid;
	}

	public void setHvslotid(String hvslotid) {
		this.hvslotid = hvslotid;
	}

	public String getHvchannelid() {
		return hvchannelid;
	}

	public void setHvchannelid(String hvchannelid) {
		this.hvchannelid = hvchannelid;
	}

	public String getHvpinid_region() {
		return hvpinid_region;
	}

	public void setHvpinid_region(String hvpinid_region) {
		this.hvpinid_region = hvpinid_region;
	}

	public String getHvpinid_quad() {
		return hvpinid_quad;
	}

	public void setHvpinid_quad(String hvpinid_quad) {
		this.hvpinid_quad = hvpinid_quad;
	}

	public String getHvpinid_doublet() {
		return hvpinid_doublet;
	}

	public void setHvpinid_doublet(String hvpinid_doublet) {
		this.hvpinid_doublet = hvpinid_doublet;
	}

	public String getHvpinid_doublethalf() {
		return hvpinid_doublethalf;
	}

	public void setHvpinid_doublethalf(String hvpinid_doublethalf) {
		this.hvpinid_doublethalf = hvpinid_doublethalf;
	}

	public String getHvpinid_pin() {
		return hvpinid_pin;
	}

	public void setHvpinid_pin(String hvpinid_pin) {
		this.hvpinid_pin = hvpinid_pin;
	}

	public String getDcrbconnectorid_slot() {
		return dcrbconnectorid_slot;
	}

	public void setDcrbconnectorid_slot(String dcrbconnectorid_slot) {
		this.dcrbconnectorid_slot = dcrbconnectorid_slot;
	}

	public String getDcrbconnectorid_connector() {
		return dcrbconnectorid_connector;
	}

	public void setDcrbconnectorid_connector(String dcrbconnectorid_connector) {
		this.dcrbconnectorid_connector = dcrbconnectorid_connector;
	}

	public String getLvfuseid_row() {
		return lvfuseid_row;
	}

	public void setLvfuseid_row(String lvfuseid_row) {
		this.lvfuseid_row = lvfuseid_row;
	}

	public String getLvfuseid_col() {
		return lvfuseid_col;
	}

	public void setLvfuseid_col(String lvfuseid_col) {
		this.lvfuseid_col = lvfuseid_col;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("statchangeid", statchangeid);
		builder.append("dateofentry", dateofentry);
		builder.append("runno", runno);
		builder.append("status_change_type", status_change_type);
		builder.append("problem_type", problem_type);
		builder.append("region", region);
		builder.append("sector", sector);
		builder.append("superlayer", superlayer);
		builder.append("loclayer", loclayer);
		builder.append("locwire", locwire);
		builder.append("hvcrateid", hvcrateid);
		builder.append("hvslotid", hvslotid);
		builder.append("hvchannelid", hvchannelid);
		builder.append("hvpinid_region", hvpinid_region);
		builder.append("hvpinid_quad", hvpinid_quad);
		builder.append("hvpinid_doublet", hvpinid_doublet);
		builder.append("hvpinid_doublethalf", hvpinid_doublethalf);
		builder.append("hvpinid_pin", hvpinid_pin);
		builder.append("dcrbconnectorid_slot", dcrbconnectorid_slot);
		builder.append("dcrbconnectorid_connector", dcrbconnectorid_connector);
		builder.append("lvfuseid_row", lvfuseid_row);
		builder.append("lvfuseid_col", lvfuseid_col);
		return builder.toString();
	}

	@Override
	public int compareTo(final StatusChangeDB other) {
		return new CompareToBuilder().append(statchangeid, other.statchangeid).append(dateofentry, other.dateofentry)
				.append(runno, other.runno).append(status_change_type, other.status_change_type)
				.append(region, other.region).append(sector, other.sector).append(superlayer, other.superlayer)
				.append(loclayer, other.loclayer).append(locwire, other.locwire).append(hvcrateid, other.hvcrateid)
				.append(hvslotid, other.hvslotid).append(hvchannelid, other.hvchannelid)
				.append(hvpinid_region, other.hvpinid_region).append(hvpinid_quad, other.hvpinid_quad)
				.append(hvpinid_doublet, other.hvpinid_doublet).append(hvpinid_doublethalf, other.hvpinid_doublethalf)
				.append(hvpinid_pin, other.hvpinid_pin).append(dcrbconnectorid_slot, other.dcrbconnectorid_slot)
				.append(dcrbconnectorid_connector, other.dcrbconnectorid_connector)
				.append(lvfuseid_row, other.lvfuseid_row).append(lvfuseid_col, other.lvfuseid_col).toComparison();
		// .append(problem_type, other.problem_type)
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateofentry == null) ? 0 : dateofentry.hashCode());
		result = prime * result + ((dcrbconnectorid_connector == null) ? 0 : dcrbconnectorid_connector.hashCode());
		result = prime * result + ((dcrbconnectorid_slot == null) ? 0 : dcrbconnectorid_slot.hashCode());
		result = prime * result + ((hvchannelid == null) ? 0 : hvchannelid.hashCode());
		result = prime * result + ((hvcrateid == null) ? 0 : hvcrateid.hashCode());
		result = prime * result + ((hvpinid_doublet == null) ? 0 : hvpinid_doublet.hashCode());
		result = prime * result + ((hvpinid_doublethalf == null) ? 0 : hvpinid_doublethalf.hashCode());
		result = prime * result + ((hvpinid_pin == null) ? 0 : hvpinid_pin.hashCode());
		result = prime * result + ((hvpinid_quad == null) ? 0 : hvpinid_quad.hashCode());
		result = prime * result + ((hvpinid_region == null) ? 0 : hvpinid_region.hashCode());
		result = prime * result + ((hvslotid == null) ? 0 : hvslotid.hashCode());
		result = prime * result + ((loclayer == null) ? 0 : loclayer.hashCode());
		result = prime * result + ((locwire == null) ? 0 : locwire.hashCode());
		result = prime * result + ((lvfuseid_col == null) ? 0 : lvfuseid_col.hashCode());
		result = prime * result + ((lvfuseid_row == null) ? 0 : lvfuseid_row.hashCode());
		// result = prime * result + ((problem_type == null) ? 0 :
		// problem_type.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + runno;
		result = prime * result + ((sector == null) ? 0 : sector.hashCode());
		result = prime * result + statchangeid;
		result = prime * result + ((status_change_type == null) ? 0 : status_change_type.hashCode());
		result = prime * result + ((superlayer == null) ? 0 : superlayer.hashCode());
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
		StatusChangeDB other = (StatusChangeDB) obj;
		if (dateofentry == null) {
			if (other.dateofentry != null)
				return false;
		} else if (!dateofentry.equals(other.dateofentry))
			return false;
		if (dcrbconnectorid_connector == null) {
			if (other.dcrbconnectorid_connector != null)
				return false;
		} else if (!dcrbconnectorid_connector.equals(other.dcrbconnectorid_connector))
			return false;
		if (dcrbconnectorid_slot == null) {
			if (other.dcrbconnectorid_slot != null)
				return false;
		} else if (!dcrbconnectorid_slot.equals(other.dcrbconnectorid_slot))
			return false;
		if (hvchannelid == null) {
			if (other.hvchannelid != null)
				return false;
		} else if (!hvchannelid.equals(other.hvchannelid))
			return false;
		if (hvcrateid == null) {
			if (other.hvcrateid != null)
				return false;
		} else if (!hvcrateid.equals(other.hvcrateid))
			return false;
		if (hvpinid_doublet == null) {
			if (other.hvpinid_doublet != null)
				return false;
		} else if (!hvpinid_doublet.equals(other.hvpinid_doublet))
			return false;
		if (hvpinid_doublethalf == null) {
			if (other.hvpinid_doublethalf != null)
				return false;
		} else if (!hvpinid_doublethalf.equals(other.hvpinid_doublethalf))
			return false;
		if (hvpinid_pin == null) {
			if (other.hvpinid_pin != null)
				return false;
		} else if (!hvpinid_pin.equals(other.hvpinid_pin))
			return false;
		if (hvpinid_quad == null) {
			if (other.hvpinid_quad != null)
				return false;
		} else if (!hvpinid_quad.equals(other.hvpinid_quad))
			return false;
		if (hvpinid_region == null) {
			if (other.hvpinid_region != null)
				return false;
		} else if (!hvpinid_region.equals(other.hvpinid_region))
			return false;
		if (hvslotid == null) {
			if (other.hvslotid != null)
				return false;
		} else if (!hvslotid.equals(other.hvslotid))
			return false;
		if (loclayer == null) {
			if (other.loclayer != null)
				return false;
		} else if (!loclayer.equals(other.loclayer))
			return false;
		if (locwire == null) {
			if (other.locwire != null)
				return false;
		} else if (!locwire.equals(other.locwire))
			return false;
		if (lvfuseid_col == null) {
			if (other.lvfuseid_col != null)
				return false;
		} else if (!lvfuseid_col.equals(other.lvfuseid_col))
			return false;
		if (lvfuseid_row == null) {
			if (other.lvfuseid_row != null)
				return false;
		} else if (!lvfuseid_row.equals(other.lvfuseid_row))
			return false;
		// if (problem_type == null) {
		// if (other.problem_type != null)
		// return false;
		// } else if (!problem_type.equals(other.problem_type))
		// return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (runno != other.runno)
			return false;
		if (sector == null) {
			if (other.sector != null)
				return false;
		} else if (!sector.equals(other.sector))
			return false;
		if (statchangeid != other.statchangeid)
			return false;
		if (status_change_type == null) {
			if (other.status_change_type != null)
				return false;
		} else if (!status_change_type.equals(other.status_change_type))
			return false;
		if (superlayer == null) {
			if (other.superlayer != null)
				return false;
		} else if (!superlayer.equals(other.superlayer))
			return false;
		return true;
	}

}
