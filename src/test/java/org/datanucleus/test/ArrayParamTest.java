package org.datanucleus.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.query.ListExpression;

import org.datanucleus.api.jdo.query.JDOQLTypedQueryImpl;
import org.datanucleus.util.NucleusLogger;
import org.junit.After;
import org.junit.Test;

import mydomain.model.OtherPerson;
import mydomain.model.Person;
import mydomain.model.QPerson;

public class ArrayParamTest
{
	
	@After
	public void tearDown() {
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");
		PersistenceManager pm = pmf.getPersistenceManager();
		JDOQLTypedQueryImpl<Person> personQuery = new JDOQLTypedQueryImpl(pm,Person.class);
		JDOQLTypedQueryImpl<OtherPerson> otherPersonQuery = new JDOQLTypedQueryImpl(pm,OtherPerson.class);
	    Transaction tx = pm.currentTransaction();
	    tx.begin();
	    personQuery.deletePersistentAll();
	    otherPersonQuery.deletePersistentAll();
	}

    @Test
    public void testArrayParam()
    {
        NucleusLogger.GENERAL.info(">> test START");
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
				Person person1 = new Person(1,"Mr Foo");
				pm.makePersistent(person1);
            tx.commit();
            tx.begin();
				Person person2 = new Person(2,"Mr Foo2");
				pm.makePersistent(person2);
            tx.commit();
            tx.begin();
				Person person3 = new Person(3,"Mr Foo3");
				pm.makePersistent(person3);
            tx.commit();
            Integer[] idsList = new Integer[] {1,3};
            JDOQLTypedQueryImpl<Person> query = new JDOQLTypedQueryImpl<>(pm, Person.class);
            QPerson cand = QPerson.candidate();
            query.filter(
            		((ListExpression<List<Number>, Number> ) query.listParameter("ids")).contains(cand.id)
            		);
            query = (JDOQLTypedQueryImpl<Person>) query.setParameter("ids", Arrays.asList(idsList));
            List<Person> result = query.executeList();
            assertNotNull(result);
            
        }
        catch (Throwable thr)
        {
        	System.out.println(thr.getMessage());
        	thr.printStackTrace();
            NucleusLogger.GENERAL.error(">> Exception in test", thr);
            fail("Failed test : " + thr.getMessage());
        }
        finally 
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        pmf.close();
        NucleusLogger.GENERAL.info(">> test END");
    }
}
