package rs.ac.bg.etf.diplomski.hibernate;

import org.hibernate.Session;

@FunctionalInterface
public interface TransactionalCode<T> {

	public T run(final Session session);
}
