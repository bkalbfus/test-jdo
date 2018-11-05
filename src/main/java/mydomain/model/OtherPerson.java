package mydomain.model;

import javax.jdo.annotations.*;

@PersistenceCapable(detachable="true")
@Extension(vendorName = "datanucleus", key = "datastore", value = "other")
public class OtherPerson
{
    @PrimaryKey
    Long id;

    String name;

    public OtherPerson(long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String newName) {
    	this.name = newName;
    }

}
