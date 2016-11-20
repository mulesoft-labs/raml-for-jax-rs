package server.hr;

import example.xmlschema.Hr;
import example.xmlschema.Person;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class HrImpl implements Hr {


    @Override
    public GetResponse get() {
        Person p = new Person();
        p.setName("Foo");
        Person.Address addr = new Person.Address();
        addr.setPostCode("HFOF");

        p.setAddress(addr);
        return GetResponse.respond200(p);
    }

    @Override
    public PutResponse put(Person entity) {
        return null;
    }
}
