package com.tidal.refactoring.playlist;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.validations.PlaylistValidator;

import java.util.*;

public class PlaylistBusinessBean {

    private PlaylistDaoBean playlistDaoBean;

    @Inject
    public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean) {
        this.playlistDaoBean = playlistDaoBean;
    }

    /**
     * @param uuid        - unique identifier of playlist
     * @param tracksToAdd - list of tracks to be added
     * @param toIndex     - position in playlist where new tracks will be added
     *                    Method to add tracks in playlist
     */
    List<PlayListTrack> addTracks(String uuid, List<Track> tracksToAdd, int toIndex) throws PlaylistException {
            // Fetching Playlist details by uuid
            PlayList playList = playlistDaoBean.getPlaylistByUUID(uuid);

            // Validates To be added track list
            PlaylistValidator.validateToBeAddedTrackList(tracksToAdd);

            //We do not allow > 500 tracks in new playlists
            PlaylistValidator.validatePlaylistSize(playList.getNrOfTracks() + tracksToAdd.size());

            // The index is out of bounds, put it in the end of the list.
            toIndex = setToIndex(toIndex, playList);
            // Validate Indexes
            if (!PlaylistValidator.validateInputIndexes(toIndex, playList.getNrOfTracks())) {
                return Lists.newArrayList();
            }

            Set<PlayListTrack> originalSet = playList.getPlayListTracks();
            List<PlayListTrack> original = ((originalSet == null) || (originalSet.size() == 0)) ? new ArrayList<>() : new ArrayList<>(originalSet);

            Collections.sort(original);

            List<PlayListTrack> added = new ArrayList<>(tracksToAdd.size());

            // Populate a PlayListTrack Object and add PlayListTrack in original playlist
            for (Track track : tracksToAdd) {
                PlayListTrack playlistTrack = new PlayListTrack();
                playlistTrack.setTrack(track);
                playlistTrack.setTrackPlaylist(playList);
                playlistTrack.setDateAdded(new Date());
                playlistTrack.setTrack(track);
                playList.setDuration(addTrackDurationToPlaylist(playList, track));
                original.add(toIndex, playlistTrack);
                added.add(playlistTrack);
                toIndex++;
            }

            // Resetting index of PlayLisTracks after addition
            resetIndex(original);

            playList.getPlayListTracks().clear();
            playList.getPlayListTracks().addAll(original);
            playList.setNrOfTracks(original.size());

            return added;
    }

    /**
     * @param original - original playlist
     */
    private void resetIndex(List<PlayListTrack> original) {
        int i = 0;
        for (PlayListTrack track : original) {
            track.setIndex(i++);
        }
    }

    /**
     * @param toIndex  - the input index
     * @param playList - playlist object
     * @return toIndex
     */
    private int setToIndex(int toIndex, PlayList playList) {
        int size = playList.getPlayListTracks() == null ? 0 : playList.getPlayListTracks().size();
        if (toIndex > size || toIndex == -1) {
            toIndex = size;
        }
        return toIndex;
    }

    /**
     * @param  uuid - unique identifier of playlist
     * @param  indexes - list of indexes to delete
     * Remove the tracks from the playlist located at the sent indexes
     */
    List<PlayListTrack> removeTracks(String uuid, List<Integer> indexes) throws PlaylistException {
        // Validate input Index list
        PlaylistValidator.validateInputIndexes(indexes);

        // Fetching Playlist details by uuid
        PlayList playList = playlistDaoBean.getPlaylistByUUID(uuid);

        List<PlayListTrack> playListTracks = new ArrayList<>(playList.getPlayListTracks());

        // Validate whether playlist has PlayListTracks to remove
        if(playListTracks.size()==0){
            //No play lists available for removing..
            return Lists.newArrayList();
        }

        // Sorting playListTracks based on index
        Collections.sort(playListTracks,Comparator.comparing(PlayListTrack::getIndex));


        List<PlayListTrack> toBeDeletedTracks = new ArrayList<>();
        for(PlayListTrack playListTrack : playListTracks){
            if(indexes.contains(playListTrack.getIndex()) && !toBeDeletedTracks.contains(playListTrack)){
                toBeDeletedTracks.add(playListTrack);
            }
        }
        playListTracks.removeAll(toBeDeletedTracks);

        // Resetting index of PlayLisTracks after deletion
        resetIndex(playListTracks);

        return toBeDeletedTracks;
    }


    /**
     * @param playList - playlist object
     * @param track - Track object
     * @return track duration
     */
    private float addTrackDurationToPlaylist(PlayList playList, Track track) {
        return (track != null ? track.getDuration() : 0)
                + (playList != null && playList.getDuration() != null ? playList.getDuration() : 0);
    }
}
