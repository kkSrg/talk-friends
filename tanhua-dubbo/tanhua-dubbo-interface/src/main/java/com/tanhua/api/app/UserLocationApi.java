package com.tanhua.api.app;

import java.util.List;

public interface UserLocationApi {
    Boolean updateLocation(Long id, Double longitude, Double latitude, String address);

    List<Long> search(Long uid, Double distance);
}
