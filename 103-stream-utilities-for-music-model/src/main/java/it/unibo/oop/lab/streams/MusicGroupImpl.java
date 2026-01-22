package it.unibo.oop.lab.streams;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new LinkedHashMap<>();
    private final Set<Song> songs = new LinkedHashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(Song::getSongName).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream().filter(a -> a.getValue() == year).map(Map.Entry::getKey);
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().equals(Optional.of(albumName))).count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().map(Song::getAlbumName).filter(Optional::isEmpty).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
            .filter(s -> s.getAlbumName().equals(Optional.of(albumName)))
            .mapToDouble(Song::getDuration).average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream().max(Comparator.comparingDouble(Song::getDuration)).map(Song::getSongName);
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream()
        .filter(s -> s.getAlbumName().isPresent())
        .collect(Collectors.groupingBy(
            s -> s.getAlbumName().get(),                 
            Collectors.summingDouble(Song::getDuration) 
        ))
        .entrySet().stream()
        .max(Comparator.comparingDouble(Map.Entry::getValue))
        .map(Map.Entry::getKey);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = Objects.hash(songName, albumName, duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Song other
                && albumName.equals(other.albumName)
                && songName.equals(other.songName)
                && duration == other.duration;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
