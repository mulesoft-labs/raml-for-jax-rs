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
package server.zoo;

import example.model.*;
import example.resources.Animals;

import javax.ws.rs.core.GenericEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 2/21/17. Just potential zeroes and ones
 */
public class AnimalsImpl implements Animals {

  @Override
  public GetAnimalsResponse getAnimals(String type) {

    AnimalImpl animal = new AnimalImpl();
    animal.setGender(Gender.FEMALE);
    animal.setAnimalType("Dog");

    List<Animal> animals = new ArrayList<>();
    animals.add(animal);
    return GetAnimalsResponse.respond200WithApplicationXml(animals);
  }

  @Override
  public GetAnimalsByIdResponse getAnimalsById(String id) {
    AnimalImpl animal = new AnimalImpl();
    animal.setGender(Gender.FEMALE);
    animal.setAnimalType("Dog");

    AnimalImpl cat = new AnimalImpl();
    cat.setGender(Gender.FEMALE);
    cat.setAnimalType("Cat");

    InsectImpl insect = new InsectImpl();
    insect.setGender(Gender.OTHER);
    insect.setAnimalType("Insect");
    insect.setIcky(true);

    List<Animal> animals = new ArrayList<>();
    animals.add(cat);
    animals.add(insect);

    animal.setMother(insect);
    animal.setSiblings(animals);

    animal.setAncestor(new AncestorImpl(insect));

    return GetAnimalsByIdResponse.respond200WithApplicationXml(animal);
  }

  @Override
  public PutAnimalsByIdResponse putAnimalsById(String id, Animal entity) {
    return null;
  }
}
