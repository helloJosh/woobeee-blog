package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostSignUpRequest;
import com.woobeee.back.entity.Profile;
import com.woobeee.back.entity.RunningRecord;
import com.woobeee.back.entity.StrengthRecord;
import com.woobeee.back.entity.Tag;
import com.woobeee.back.entity.InbodyRecord;
import com.woobeee.back.entity.UserInfo;
import com.woobeee.back.entity.UserTag;
import com.woobeee.back.exception.CustomConflictException;
import com.woobeee.back.exception.CustomNotFoundException;
import com.woobeee.back.exception.ErrorCode;
import com.woobeee.back.repository.ProfileRepository;
import com.woobeee.back.repository.RunningRecordRepository;
import com.woobeee.back.repository.StrengthRecordRepository;
import com.woobeee.back.repository.TagRepository;
import com.woobeee.back.repository.InbodyRecordRepository;
import com.woobeee.back.repository.UserInfoRepository;
import com.woobeee.back.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final ProfileRepository profileRepository;
    private final TagRepository tagRepository;
    private final UserTagRepository userTagRepository;
    private final InbodyRecordRepository inbodyRecordRepository;
    private final StrengthRecordRepository strengthRecordRepository;
    private final RunningRecordRepository runningRecordRepository;

    @Override
    public void signIn(String id, String loginId) {
        userInfoRepository.save(new UserInfo(loginId, UUID.fromString(id)));
    }

    @Override
    public void signUp(PostSignUpRequest request) {
        if (userInfoRepository.existsById(request.id())
                || profileRepository.existsById(request.id())
                || userInfoRepository.existsByLoginId(request.loginId())) {
            throw new CustomConflictException(ErrorCode.signUp_userConflict);
        }

        userInfoRepository.save(
                UserInfo.builder()
                        .id(request.id())
                        .loginId(request.loginId())
                        .build()
        );

        profileRepository.save(
                Profile.builder()
                        .id(request.id())
                        .nickname(request.nickname())
                        .instargramId(request.instargramId())
                        .preferredRegion(request.preferredRegion())
                        .introText(request.introText())
                        .idealTypeText(request.idealTypeText())
                        .yearsOfTraining(request.yearsOfTraining())
                        .build()
        );

        if (request.inbodyRecord() != null) {
            inbodyRecordRepository.save(
                    InbodyRecord.builder()
                            .id(UUID.randomUUID())
                            .weight(request.inbodyRecord().weight())
                            .skeletalMuscle(request.inbodyRecord().skeletalMuscle())
                            .bodyFatPercent(request.inbodyRecord().bodyFatPercent())
                            .visceralFat(request.inbodyRecord().visceralFat())
                            .measuredAt(request.inbodyRecord().measuredAt())
                            .isVerified(request.inbodyRecord().isVerified())
                            .imageUrl(request.inbodyRecord().imageUrl())
                            .profilesId(request.id())
                            .build()
            );
        }

        if (request.strengthRecord() != null) {
            strengthRecordRepository.save(
                    StrengthRecord.builder()
                            .id(UUID.randomUUID())
                            .squat(request.strengthRecord().squat())
                            .bench(request.strengthRecord().bench())
                            .deadlift(request.strengthRecord().deadlift())
                            .recordedAt(request.strengthRecord().recordedAt())
                            .isVerified(request.strengthRecord().isVerified())
                            .profilesId(request.id())
                            .build()
            );
        }

        if (request.runningRecord() != null) {
            runningRecordRepository.save(
                    RunningRecord.builder()
                            .id(UUID.randomUUID())
                            .records(request.runningRecord().records())
                            .recordedAt(request.runningRecord().recordedAt())
                            .isVerified(request.runningRecord().isVerified())
                            .profilesId(request.id())
                            .build()
            );
        }

        if (request.tagIds() == null || request.tagIds().isEmpty()) {
            return;
        }

        Set<UUID> uniqueTagIds = new LinkedHashSet<>(request.tagIds());
        Set<UUID> existingTagIds = new LinkedHashSet<>(
                tagRepository.findAllById(uniqueTagIds).stream()
                        .map(Tag::getId)
                        .toList()
        );

        if (existingTagIds.size() != uniqueTagIds.size()) {
            throw new CustomNotFoundException(ErrorCode.signUp_tagNotFound);
        }

        List<UserTag> userTags = new ArrayList<>();
        for (UUID tagId : uniqueTagIds) {
            userTags.add(
                    UserTag.builder()
                            .id(
                                    UserTag.UserTagId.builder()
                                            .profilesId(request.id())
                                            .tagsId(tagId)
                                            .build()
                            )
                            .build()
            );
        }
        userTagRepository.saveAll(userTags);
    }
}
