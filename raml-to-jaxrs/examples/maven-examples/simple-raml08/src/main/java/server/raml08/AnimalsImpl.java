package server.raml08;

import example.simpleraml08.Animals;
import example.simpleraml08.Cats;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class AnimalsImpl implements Animals {

    @Override
    public PutAnimalsResponse putAnimals(Cats entity) {
        return null;
    }
}
