package server.hr;

import example.xmlschema.Hr;
import example.xmlschema.Person;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class HrImpl implements Hr {

    @Override
    public PutHrResponse putHr(Person entity) {
        return null;
    }

    @Override
    public GetHrResponse getHr() {
        Person p = new Person();
        p.setName("Foo");
        Person.Address addr = new Person.Address();
        addr.setPostCode("HFOF");

        p.setAddress(addr);
        return GetHrResponse.respond200WithApplicationXml(p);
    }
}
