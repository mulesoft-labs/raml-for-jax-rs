/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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

import example.model.ArmImpl;
import example.model.CorpseImpl;
import example.model.Gender;
import example.model.Human;
import example.model.Limb;
import example.model.PersonImpl;
import example.types.Humanity;

import java.util.Collections;
import java.util.Date;

/**
 * Created by Jean-Philippe Belanger on 11/11/16. Just potential zeroes and ones
 */
public class HumanityImpl implements Humanity {


  @Override
  public PutHumanityResponse putHumanity(Human entity) {
    return PutHumanityResponse.respond200();
  }

  @Override
  public GetHumanityResponse getHumanity(String type) {

    if ("person".equals(type)) {

      PersonImpl pi = new PersonImpl();
      pi.setGender(Gender.FEMALE);
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
      pi.setLimbs(new Limb(new ArmImpl()));
      return GetHumanityResponse.respond200WithApplicationJson(pi);
    } else {

      CorpseImpl ci = new CorpseImpl();
      ci.setGender(Gender.OTHER);
      ci.setDateOfDeath(new Date());
      return GetHumanityResponse.respond200WithApplicationJson(ci);
    }
  }
}
