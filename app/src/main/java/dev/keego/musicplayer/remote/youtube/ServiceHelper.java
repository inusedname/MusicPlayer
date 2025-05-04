package dev.keego.musicplayer.remote.youtube;

import static org.schabi.newpipe.extractor.ServiceList.SoundCloud;

import java.util.concurrent.TimeUnit;

public final class ServiceHelper {
    private ServiceHelper() { }

    public static long getCacheExpirationMillis(final int serviceId) {
        if (serviceId == SoundCloud.getServiceId()) {
            return TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
        } else {
            return TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);
        }
    }
}