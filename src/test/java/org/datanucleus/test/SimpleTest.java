package org.datanucleus.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

import org.datanucleus.api.jdo.query.JDOQLTypedQueryImpl;
import org.datanucleus.util.NucleusLogger;
import org.junit.Test;

import mydomain.model.OtherPerson;
import mydomain.model.Person;

public class SimpleTest
{
    @Test
    public void testSimple()
    {
        NucleusLogger.GENERAL.info(">> test START");
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person person = new Person(1,"Mr Foo");
            pm.makePersistent(person);
            OtherPerson otherPerson = new OtherPerson(1, "Mr Other");
            pm.makePersistent(otherPerson);
            tx.commit();
            
            tx.begin();
            person.setName("Mr FooBobble");
            assertTrue("person should be dirty",JDOHelper.isDirty(person));
            otherPerson.setName("Mr OtherBobble");
            assertTrue("otherPerson should be dirty",JDOHelper.isDirty(otherPerson));
            tx.commit();
            
            assertEquals("name of person should be changed", person.getName(), "Mr FooBobble");
            assertFalse("person should not be dirty", JDOHelper.isDirty(person));
            assertEquals("name of otherPerson should be changed", otherPerson.getName(), "Mr OtherBobble");
            assertFalse("otherPerson should not be dirty", JDOHelper.isDirty(otherPerson));
            
            
            
            JDOQLTypedQueryImpl<Person> personQuery = new JDOQLTypedQueryImpl<>(pm, Person.class);
            JDOConnection personConnection = personQuery.getPersistenceManager().getDataStoreConnection();
            // personConnection.getNativeConnection().toString() should be something like: 1806431167, URL=jdbc:hsqldb:mem:test, UserName=SA, HSQL Database Engine Driver
            System.out.println("personConnection: " + personConnection.getNativeConnection().toString());
            assertEquals("personConnection should point to 'jdbc:hsqldb:mem:test'", " URL=jdbc:hsqldb:mem:test", personConnection.getNativeConnection().toString().split(",")[1]);
            personConnection.close();
            
            Person queriedPerson = personQuery.executeUnique();
            assertNotNull("queriedPerson should not be null", queriedPerson);
            assertEquals("person should be equal to queriedPerson", person, queriedPerson);
            personQuery.close();
            
            // Describe connection used for OtherPerson:
            JDOQLTypedQueryImpl<OtherPerson> otherPersonQuery = new JDOQLTypedQueryImpl<>(pm, OtherPerson.class);
            JDOConnection otherPersonConnection = otherPersonQuery.getPersistenceManager().getDataStoreConnection();
            // otherPersonConnection.getNativeConnection().toString() should be something like: 1392794732, URL=jdbc:hsqldb:mem:test_other, UserName=SA, HSQL Database Engine Driver
            System.out.println("otherPersonConnection: " + otherPersonConnection.getNativeConnection().toString());
            
            // Comment out the assertion on the following line to see the pragmatic assert fail below
            assertEquals("otherPersonConnection should point to 'jdbc:hsqldb:mem:test_other'", " URL=jdbc:hsqldb:mem:test_other", otherPersonConnection.getNativeConnection().toString().split(",")[1]);
            otherPersonConnection.close();
            
            OtherPerson queriedOtherPerson = otherPersonQuery.executeUnique();
            otherPersonQuery.close();
            assertNotNull("queriedOtherPerson should not be null", queriedOtherPerson);
            assertEquals("queriedOtherPerson should be equal to otherPerson", otherPerson, queriedOtherPerson);
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
