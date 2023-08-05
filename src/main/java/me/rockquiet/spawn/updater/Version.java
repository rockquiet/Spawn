package me.rockquiet.spawn.updater;

public class Version {

    public final int major;
    public final int minor;
    public final int patch;

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

        if (!version.matches("\\d+(\\.\\d+)*")) {
            throw new IllegalArgumentException("Version string has an invalid format");
        }

        String[] versionSegments = version.split("\\.");
        int length = versionSegments.length;

        return new Version(
                Integer.parseInt(versionSegments[0]),
                (length < 2 ? 0 : Integer.parseInt(versionSegments[1])),
                (length < 3 ? 0 : Integer.parseInt(versionSegments[2]))
        );
    }

    // 1    ->  version is newer than version2
    // 0    ->  version = version2
    // -1   ->  version is older than version2
    public int compareTo(Version version2) {
        if (version2 == null) {
            return 1;
        }

        if (major == version2.major && minor == version2.minor && patch == version2.patch) {
            return 0;
        }

        if ((major != version2.major && major > version2.major) || (minor != version2.minor && minor > version2.minor) || (patch != version2.patch && patch > version2.patch)) {
            return 1;
        } else {
            return -1;
        }
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
