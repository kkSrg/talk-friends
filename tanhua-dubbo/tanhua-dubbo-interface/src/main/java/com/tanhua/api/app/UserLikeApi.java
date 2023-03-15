package com.tanhua.api.app;

import java.util.List;

public interface UserLikeApi {
    Boolean saveOrUpdate(Long userId, Long likeUserId, boolean isLike);

    List<Long> findById(Long id);
}
