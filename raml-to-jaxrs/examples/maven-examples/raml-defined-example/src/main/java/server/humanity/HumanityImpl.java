/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package server.humanity;

import example.model.*;
import example.types.Humans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/11/16. Just potential zeroes and ones
 */
public class HumanityImpl implements Humans {

  @Override
  public GetHumansResponse getHumans(String type) {
    List<Human> humans = new ArrayList<>();
    PersonImpl pi = new PersonImpl();
    pi.setActualGender(Gender.FEMALE);
    pi.setWeight(180);

    pi.setDateOfBirth(new Date());
    pi.setInstantOfBirth(new Date());
    pi.setTimeOfArrival(new Date());
    pi.setDateOfBirth(new Date());
    pi.setTimeOfBirth(new Date());
    pi.setRequestTime(new Date());

    CorpseImpl ci = new CorpseImpl();
    ci.setDateOfDeath(new Date());
    pi.setSiblings(Collections.<Human>singletonList(ci));
    pi.setLimbs(new LimbImpl(new ArmImpl()));

    CorpseImpl another = new CorpseImpl();
    another.setActualGender(Gender.OTHER);
    another.setDateOfDeath(new Date());

    humans.add(pi);
    humans.add(another);

    return GetHumansResponse.respond200WithApplicationJson(humans,
                                                           GetHumansResponse.headersFor200().withBoo("MyBoo"));
  }

  @Override
  public GetHumansByIdResponse getHumansById(String id, String type) {
    if ("person".equals(type)) {

      PersonImpl pi = new PersonImpl();
      pi.setActualGender(Gender.FEMALE);
      pi.setWeight(180);

      pi.setDateOfBirth(new Date());
      pi.setInstantOfBirth(new Date());
      pi.setTimeOfArrival(new Date());
      pi.setDateOfBirth(new Date());
      pi.setTimeOfBirth(new Date());
      pi.setRequestTime(new Date());

      CorpseImpl ci = new CorpseImpl();
      ci.setDateOfDeath(new Date());
      pi.setSiblings(Collections.<Human>singletonList(ci));
      pi.setLimbs(new LimbImpl(new ArmImpl()));
      return GetHumansByIdResponse.respond200WithApplicationJson(pi);
    } else {

      CorpseImpl ci = new CorpseImpl();
      ci.setActualGender(Gender.OTHER);
      ci.setDateOfDeath(new Date());
      return GetHumansByIdResponse.respond200WithApplicationJson(ci);
    }
  }

  @Override
  public PutHumansByIdResponse putHumansById(String id, Human entity) {

    return PutHumansByIdResponse.respond200(PutHumansByIdResponse.headersFor200().withSomeOtherHeader("Blah"));
  }
}
