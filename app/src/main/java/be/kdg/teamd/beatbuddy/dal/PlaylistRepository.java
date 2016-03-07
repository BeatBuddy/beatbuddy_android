package be.kdg.teamd.beatbuddy.dal;

import java.util.List;

import be.kdg.teamd.beatbuddy.model.playlists.Playlist;
import be.kdg.teamd.beatbuddy.model.playlists.SourceType;
import be.kdg.teamd.beatbuddy.model.playlists.Track;
import be.kdg.teamd.beatbuddy.model.playlists.Vote;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaylistRepository {
    @GET("Playlist/{id}")
    Call<Playlist> getPlaylist(@Path("id") long playlistId);

    @GET("Playlist/{id}/live")
    Call<Playlist> getLivePlaylist(@Path("id") long playlistId);

    @POST("Playlist/{id}/addTrack/")
    Call<Track> addTrack(@Path("id") long playlistId, @Query("trackId") String trackId);

    @POST("Playlist/createPlaylist")
    @FormUrlEncoded
    Call<Playlist> createPlaylist(@Field("name") String name, @Field("key") String key, @Field("description") String description, @Field("image") String imageBase64);

    @POST("Playlist/createPlaylist")
    @FormUrlEncoded
    Call<Playlist> createPlaylist(@Field("organisationId") long organisationId, @Field("name") String name, @Field("key") String key, @Field("description") String description, @Field("image") String imageBase64);

    @GET("Playlist/recommendations")
    Call<List<Playlist>> getRecommendedPlaylists(@Query("count") int count);

    @GET("Playlist/searchTrack")
    Call<List<Track>> getTracks(@Query("query") String query);

    @GET("Playlist/{id}/nextTrack")
    Call<Track> getNextTrack(@Path("id") long playlistId);

    @POST("Playlist/{id}/track/{trackId}/upvote")
    Call<Vote> upvoteTrack(@Path("id") long playlistId, @Path("trackId") long trackId);

    @POST("Playlist/{id}/track/{trackId}/downvote")
    Call<Vote> downvoteTrack(@Path("id") long playlistId, @Path("trackId") long trackId);
}