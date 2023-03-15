package com.tanhua.api.sso;

import com.tanhua.db.pojo.UserInfo;
import com.tanhua.mongo.dto.RecommendationDto;

import java.util.List;

public interface UserInfoServiceApi {
    void save(UserInfo userInfo);

    void updateAvatar(UserInfo userInfo);

    UserInfo findById(Long userId);

    UserInfo findByIdAndCondation(Long userId, RecommendationDto dto);

    List<UserInfo> findByIds(List<Object> userIds);

    List<UserInfo> findByIdsPageAndKw(List<Long> fid, String keyword, Integer page, Integer pagesize);

    List<UserInfo> findUserInfoByAndGender(List<Long> ids, String gender);

    UserInfo slectById(Long userId);
}
