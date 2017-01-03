package server.humanity.cars;

import example.types.ArmImpl;
import example.types.CorpseImpl;
import example.types.Human;
import example.types.Humanity;
import example.types.Limb;
import example.types.PersonImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class HumanityImpl implements Humanity {


    @Override
    public PutHumanityResponse putHumanity(Human entity) {
        return PutHumanityResponse.respond200();
    }

    @Override
    public GetHumanityResponse getHumanity(String type) {

        if ( "person".equals(type) ) {

            PersonImpl pi = new PersonImpl();
            pi.setMale(true);
            pi.setWeight(180);
            CorpseImpl ci = new CorpseImpl();
            ci.setDateOfDeath(new Date());
            pi.setSiblings(Collections.<Human>singletonList(ci));
            pi.setLimbs(new Limb(new ArmImpl()));
            return GetHumanityResponse.respond200WithApplicationJson(pi);
        } else {

            CorpseImpl ci = new CorpseImpl();
            ci.setMale(false);
            ci.setDateOfDeath(new Date());
            return GetHumanityResponse.respond200WithApplicationJson(ci);
        }
    }
}
