package org.datanucleus.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.query.ListExpression;

import org.datanucleus.api.jdo.query.JDOQLTypedQueryImpl;
import org.datanucleus.query.JDOQLQueryHelper;
import org.datanucleus.util.NucleusLogger;
import org.junit.After;
import org.junit.Ignore;
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
    public void testArrayParamJDOQLQuery()
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
            // using primitive int data type causes error.  bad omition in Arrays.asList() IMO.  https://stackoverflow.com/a/53730866/1639527
//            int[] idsList = new int[] {1,3};
            // using wrapped Integer data type works as expected
            Integer[] idsList = new Integer[] {1,3};
            List listIdsList = Arrays.asList(idsList);
            assertTrue("Arrays.asList({an array}) should return a list of the elements in the array!",listIdsList.size() == 2);
            Query query = pm.newQuery("SELECT FROM mydomain.model.Person WHERE !:ids.contains(id)");
            query.setParameters(Arrays.asList(idsList));
            List<Person> result = query.executeList();
            assertNotNull(result);
            assertTrue("result should have the one id not in the list",result.size() == 1);
            
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

	@Test 
	    public void testArrayParamTypedQuery()
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
            // using primitive int data type causes error.  bad omition in Arrays.asList() IMO.  https://stackoverflow.com/a/53730866/1639527
	//            int[] idsList = new int[] {1,3};
	            // using wrapped Integer data type works as expected
	            Integer[] idsList = new Integer[] {1,3};
	            List listIdsList = Arrays.asList(idsList);
	            assertTrue("Arrays.asList({an array}) should return a list of the elements in the array!",listIdsList.size() == 2);
	            
	            
	            JDOQLTypedQueryImpl<Person> query = new JDOQLTypedQueryImpl<>(pm, Person.class);
	            QPerson cand = QPerson.candidate();
	            query.filter(
	            		((ListExpression<List<Number>, Number> ) query.listParameter("ids")).contains(cand.id).not()
	            		);
	            query = (JDOQLTypedQueryImpl<Person>) query.setParameter("ids", Arrays.asList(idsList));
	            List<Person> result = query.executeList();
	            assertNotNull(result);
	            assertTrue("result should have the one id not in the list",result.size() == 1);
	            
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
