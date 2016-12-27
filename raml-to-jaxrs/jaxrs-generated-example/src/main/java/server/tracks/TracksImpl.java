package server.tracks;

import example.jsonschema.Track;
import example.jsonschema.Tracks;
import example.realtypes.Car;
import example.realtypes.CarImpl;
import example.realtypes.Inventory;
import example.xmlschema.Hr;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class TracksImpl implements Tracks {


    @Override
    public GetTracksResponse getTracks() {
        Track track = new Track();
        track.setAlbumId("17");
        track.setSongTitle("Booyakasha");
        return GetTracksResponse.respond200WithApplicationJson(track);
    }

    @Override
    public void putTracks(Track entity) {
    }
}
