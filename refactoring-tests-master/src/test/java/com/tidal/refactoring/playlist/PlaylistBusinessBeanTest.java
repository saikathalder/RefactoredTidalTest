package com.tidal.refactoring.playlist;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tidal.refactoring.playlist.constants.PlaylistConstants;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.assertTrue;


/**
 * Test class for PlaylistBusinessBean
 */
@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

    @Inject
    PlaylistBusinessBean playlistBusinessBean;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void setUp() throws Exception {

    }

    /**
     * @throws Exception
     */
    @AfterMethod
    public void tearDown() throws Exception {

    }

    /**
     * test method to test addTracks method
     * test scenario : success case
     */
    @Test
    public void testAddTracks()  {
        List<Track> trackList = new ArrayList<>();

        Track track = new Track();
        track.setArtistId(4);
        track.setTitle("A brand new track");
        track.setId(76868);

        trackList.add(track);

        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(null, trackList, 5);

        assertTrue(playListTracks.size() > 0);
    }

    /**
     * test method to test addTracks method
     * test scenario : when input trackList is empty
     */
    @Test
    public void testAddTracksWithEmptyList() {
        try {
            List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(null, null, 5);
        }catch(PlaylistException ex){
            assertTrue(ex.getMessage().equalsIgnoreCase("Input track list cannot be empty"));
        }

    }

    /**
     * test method to test addTracks method
     * test scenario : when tracks added over allowed playlist size
     */
    @Test
    public void testAddTracksOverAllowedPlaylistSize() {
        try {
            List<Track> trackList = new ArrayList<>();
            for(int i=0;i<150;i++){
                Track track = new Track();
                track.setArtistId(4);
                track.setTitle("A brand new track");
                track.setId(76868+i);

                trackList.add(track);
            }
            List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks("UUID", trackList, 15);
        }catch(PlaylistException ex){
            assertTrue(ex.getMessage().equalsIgnoreCase("Playlist cannot have more than " + PlaylistConstants.MAX_PLAYLIST_SIZE_ALLOWED + " tracks"));
        }

    }

    /**
     * test method to test removeTracks method
     * test scenario : success case, remove tracks called for valid case
     */
    @Test
    public void testDeleteTracks() {
        List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(null, Lists.newArrayList(1,2));
        assertTrue(removedTracks.size()>0);
        assertTrue(removedTracks.size()== 2);
    }

    /**
     * test method to test removeTracks method
     * test scenario : when input indexes are not passed
     */
    @Test
    public void testDeleteTracksWithNoIndex(){
        try {
            List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks(null, Lists.newArrayList());
        }catch (PlaylistException ex){
            assertTrue(ex.getMessage().equalsIgnoreCase("Input Indexes cannot be empty"));
        }
    }

    /**
     * test method to test removeTracks method
     * test scenario : when input indexes are not valid
     */
    @Test
    public void testDeleteTracksWithInvalidIndex(){
        List<PlayListTrack> removedTracks = playlistBusinessBean.removeTracks("UUID", Lists.newArrayList(900));
        assertTrue(removedTracks.size() == 0);
    }
}