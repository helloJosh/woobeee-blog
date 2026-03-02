package com.woobeee.back.service;

import com.woobeee.back.dto.request.PostSignUpRequest;
import com.woobeee.back.entity.Tag;
import com.woobeee.back.entity.UserInfo;
import com.woobeee.back.entity.UserTag;
import com.woobeee.back.exception.CustomConflictException;
import com.woobeee.back.repository.InbodyRecordRepository;
import com.woobeee.back.repository.ProfileRepository;
import com.woobeee.back.repository.RunningRecordRepository;
import com.woobeee.back.repository.StrengthRecordRepository;
import com.woobeee.back.repository.TagRepository;
import com.woobeee.back.repository.UserInfoRepository;
import com.woobeee.back.repository.UserTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceImplTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private UserTagRepository userTagRepository;
    @Mock
    private InbodyRecordRepository inbodyRecordRepository;
    @Mock
    private StrengthRecordRepository strengthRecordRepository;
    @Mock
    private RunningRecordRepository runningRecordRepository;

    @InjectMocks
    private UserInfoServiceImpl service;

    @Test
    void signIn_shouldSaveUserInfo() {
        UUID id = UUID.randomUUID();

        service.signIn(id.toString(), "member@test.com");

        ArgumentCaptor<UserInfo> userCaptor = ArgumentCaptor.forClass(UserInfo.class);
        verify(userInfoRepository).save(userCaptor.capture());

        UserInfo saved = userCaptor.getValue();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getLoginId()).isEqualTo("member@test.com");
    }

    @Test
    void signUp_whenConflict_thenThrow() {
        UUID id = UUID.randomUUID();
        PostSignUpRequest request = new PostSignUpRequest(
                id,
                "dup@test.com",
                "nick",
                null,
                null,
                null,
                null,
                null,
                List.of(),
                null,
                null,
                null
        );

        given(userInfoRepository.existsById(id)).willReturn(true);

        assertThatThrownBy(() -> service.signUp(request))
                .isInstanceOf(CustomConflictException.class);

        verify(profileRepository, never()).save(any());
    }

    @Test
    void signUp_withRecordsAndTags_shouldSaveAll() {
        UUID userId = UUID.randomUUID();
        UUID tag1 = UUID.randomUUID();
        UUID tag2 = UUID.randomUUID();

        PostSignUpRequest request = new PostSignUpRequest(
                userId,
                "new@test.com",
                "nick",
                "insta",
                "seoul",
                "intro",
                "ideal",
                2,
                List.of(tag1, tag2),
                new PostSignUpRequest.InbodyRecordRequest(70, 35, 12, 6, LocalDateTime.now(), true, "img"),
                new PostSignUpRequest.StrengthRecordRequest(120, 90, 160, 20260302, true),
                new PostSignUpRequest.RunningRecordRequest(5000, LocalDateTime.now(), false)
        );

        given(userInfoRepository.existsById(userId)).willReturn(false);
        given(profileRepository.existsById(userId)).willReturn(false);
        given(userInfoRepository.existsByLoginId("new@test.com")).willReturn(false);
        given(tagRepository.findAllById(any(Set.class))).willReturn(List.of(
                Tag.builder().id(tag1).name("a").category("x").build(),
                Tag.builder().id(tag2).name("b").category("x").build()
        ));

        service.signUp(request);

        verify(userInfoRepository).save(any());
        verify(profileRepository).save(any());
        verify(inbodyRecordRepository).save(any());
        verify(strengthRecordRepository).save(any());
        verify(runningRecordRepository).save(any());

        ArgumentCaptor<List<UserTag>> userTagsCaptor = ArgumentCaptor.forClass(List.class);
        verify(userTagRepository).saveAll(userTagsCaptor.capture());
        assertThat(userTagsCaptor.getValue()).hasSize(2);
    }
}
