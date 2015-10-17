package edu.rochester.kanishk.apriori;

/**
 * The item class represents a single item in a transaction.
 */
public class Item implements Comparable<Item> {
	/** The category of the item. Age, hours_per_week etc.*/
	String category;
	
	/** The type of item. This is used to identify a unique item in transaction.
	 * The continous values group is also stored in this field.*/
	String itemType;

	/**The actual value of the item. For example age value the itemType will hold the group value(
	 * youth, senior etc.). This field will hold the actual numeric value. 
	 * */
	String value;
	
	public Item(String category, String itemType, String value) {
		super();
		this.category = category;
		this.itemType = itemType;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
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
		Item other = (Item) obj;
		if (itemType == null) {
			if (other.itemType != null)
				return false;
		} else if (!itemType.equals(other.itemType))
			return false;
		return true;
	}

	@Override
	public int compareTo(Item o) {
		return itemType.compareTo(o.itemType);
	}
	
	public boolean isLessThan(Item o) {
		return compareTo(o) < 0;
	}
	
}
