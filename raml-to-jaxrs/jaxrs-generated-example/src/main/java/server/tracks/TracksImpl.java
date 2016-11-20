package server.tracks;

import example.jsonschema.Track;
import example.jsonschema.Tracks;
import example.realtypes.Car;
import example.realtypes.CarImpl;
import example.realtypes.Inventory;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class TracksImpl implements Tracks {

    @Override
    public GetResponse get() {

        Track track = new Track();
        track.setAlbumId("17");
        track.setSongTitle("Booyakasha");
        return Tracks.GetResponse.respond200(track);
    }

    @Override
    public PutResponse put(Track entity) {
        return null;
    }
}
