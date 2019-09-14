package io.github.learnjpahibernate.data;

import java.io.Serializable;

import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

/**
 * This class provides a base class similar to Spring's AbstractPersistable
 * class. However, what is different is that this implementation does not
 * specify what the "Id" is, but at least still provides an implementation of
 * "toString()", "equals()", "hashCode()" methods.
 * 
 * Classes that extend this class should specify an "id" property, or provide a
 * transient getId() method that delegates to what the "id" is of the entity.
 */
public abstract class AbstractEntity<PK extends Serializable> implements Persistable<PK> {

	@Transient
	public boolean isNew() {
		return getId() == null;
	}

	@Override
	public String toString() {
		return String.format("Entity of type: %s with id: %s", this.getClass().getName(), getId());
	}

	@Override
	public boolean equals(Object other) {

		if (other == null) {
			return false;
		}
		if (this == other) {
			return true;
		}
		if (!getClass().equals(ProxyUtils.getUserClass(other))) {
			return false;
		}

		AbstractEntity<?> otherEntity = (AbstractEntity<?>) other;
		return (this.getId() == null) ? false : this.getId().equals(otherEntity.getId());
	}

	@Override
	public int hashCode() {

		int hashCode = 17;
		hashCode += (getId() == null) ? 0 : (getId().hashCode() * 31);
		return hashCode;
	}
}