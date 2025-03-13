package me.rockquiet.spawn.updater;

public class Version {

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version parse(String version) {
        if (version == null) throw new IllegalArgumentException("Version string cannot be null");
        if (version.isEmpty()) throw new IllegalArgumentException("Version string cannot be empty");

        // remove commonly used "v" at start of version number
        version = version.replaceFirst("^[Vv]", "");

        // remove suffixes like "-SNAPSHOT"
        if (version.contains("-") && !version.startsWith("-")) {
            final int suffixIndex = version.indexOf('-');
            if (suffixIndex != -1) {
                version = version.substring(0, suffixIndex);
            }
        }

        String[] versionSegments = version.split("\\.");
        int length = versionSegments.length;

        return new Version(
                parseInt(versionSegments[0]),
                length < 2 ? 0 : parseInt(versionSegments[1]),
                length < 3 ? 0 : parseInt(versionSegments[2])
        );
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    // 1    ->  version is newer than version2
    // 0    ->  version = version2
    // -1   ->  version is older than version2
    public int compareTo(Version version2) {
        if (version2 == null) return 1;

        int compareMajor = Integer.compare(major, version2.major);
        if (compareMajor != 0) return compareMajor;

        int compareMinor = Integer.compare(minor, version2.minor);
        if (compareMinor != 0) return compareMinor;

        return Integer.compare(patch, version2.patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
