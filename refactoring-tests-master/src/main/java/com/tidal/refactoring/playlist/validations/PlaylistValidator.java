package com.tidal.refactoring.playlist.validations;

import com.tidal.refactoring.playlist.constants.PlaylistConstants;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;

import java.util.List;

/**
 *  Validator class for Playlist
 */
public class PlaylistValidator {


    /**
     * @param tracksTobeAdded
     * Validates to be added track list
     */
    public static void validateToBeAddedTrackList(List<Track> tracksTobeAdded){
        if(tracksTobeAdded == null || tracksTobeAdded.size()==0){
            throw new PlaylistException("Input track list cannot be empty");
        }
    }

    /**
     * @param indexes
     * Validates input indexes
     */
    public static void validateInputIndexes(List<Integer> indexes){
        if(indexes == null || indexes.size()==0){
            throw new PlaylistException("Input Indexes cannot be empty");
        }
    }

    /**
     * @param size
     * Validates size of playlist before adding new track
     */
    public static void validatePlaylistSize(int size){
        if(size > PlaylistConstants.MAX_PLAYLIST_SIZE_ALLOWED){
            throw new PlaylistException("Playlist cannot have more than " + PlaylistConstants.MAX_PLAYLIST_SIZE_ALLOWED + " tracks");
        }
    }

    /**
     * @param toIndex index to validate
     * @param length max length
     * @return
     * Method to check whether index is valid or not
     */
    public static boolean validateInputIndexes(int toIndex, int length) {
        return toIndex >= 0 && toIndex <= length;
    }
}
