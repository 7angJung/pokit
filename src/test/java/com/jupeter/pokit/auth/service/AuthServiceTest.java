package com.jupeter.pokit.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.jupeter.pokit.auth.dto.LoginRequest;
import com.jupeter.pokit.auth.dto.SignUpRequest;
import com.jupeter.pokit.auth.entity.User;
import com.jupeter.pokit.auth.repository.UserRepository;
import com.jupeter.pokit.auth.service.AuthService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공 시 Firebase 계정 생성 및 DB 저장")
    void signUp_success() throws Exception {

        // given (준비)
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@test.com");
        request.setPassword("123456");
        request.setNickname("테스트유저");

        UserRecord mockUserRecord = mock(UserRecord.class);
        when(mockUserRecord.getUid()).thenReturn("firebase-uid-123");

        // when & then (실행 + 검증)
        try (MockedStatic<FirebaseAuth> mockedFirebase = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
            mockedFirebase.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);
            when(mockFirebaseAuth.createUser(any())).thenReturn(mockUserRecord);

            authService.signUp(request);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Test
    @DisplayName("로그인 성공 시 커스텀 토큰 반환")
    void login_success() throws Exception {

        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        UserRecord mockUserRecord = mock(UserRecord.class);
        when(mockUserRecord.getUid()).thenReturn("firebase-uid-123");

        // when & then
        try (MockedStatic<FirebaseAuth> mockedFirebase = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
            mockedFirebase.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);
            when(mockFirebaseAuth.getUserByEmail("test@test.com")).thenReturn(mockUserRecord);
            when(mockFirebaseAuth.createCustomToken("firebase-uid-123")).thenReturn("mock-token");

            String token = authService.login(request);

            verify(mockFirebaseAuth, times(1)).getUserByEmail("test@test.com");
            verify(mockFirebaseAuth, times(1)).createCustomToken("firebase-uid-123");
        }
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외 발생")
    void signUp_fail_duplicateEmail() throws Exception {

        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test@test.com");
        request.setPassword("123456");
        request.setNickname("테스트유저");

        try (MockedStatic<FirebaseAuth> mockedFirebase = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
            mockedFirebase.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            // Firebase가 이미 존재하는 이메일이라고 예외를 던지는 상황
            when(mockFirebaseAuth.createUser(any()))
                    .thenThrow(new RuntimeException("이미 존재하는 이메일입니다"));

            // when & then
            assertThrows(RuntimeException.class, () -> authService.signUp(request));

            // DB 저장은 호출되지 않아야 함
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
    void login_fail_userNotFound() throws Exception {

        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("notexist@test.com");
        request.setPassword("123456");

        try (MockedStatic<FirebaseAuth> mockedFirebase = mockStatic(FirebaseAuth.class)) {
            FirebaseAuth mockFirebaseAuth = mock(FirebaseAuth.class);
            mockedFirebase.when(FirebaseAuth::getInstance).thenReturn(mockFirebaseAuth);

            // Firebase가 존재하지 않는 유저라고 예외를 던지는 상황
            when(mockFirebaseAuth.getUserByEmail("notexist@test.com"))
                    .thenThrow(new RuntimeException("존재하지 않는 사용자입니다"));

            // when & then
            assertThrows(RuntimeException.class, () -> authService.login(request));
        }
    }

    @Test
    @DisplayName("비밀번호 6자 미만 시 DTO 검증 실패")
    void signUp_fail_shortPassword() {

        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test2@test.com");
        request.setPassword("123");  // 6자 미만
        request.setNickname("테스트유저");

        // when
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        var violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("비밀번호는 6자 이상이어야 합니다")));
    }

    @Test
    @DisplayName("닉네임 10자 초과 시 DTO 검증 실패")
    void signUp_fail_longNickname() {

        // given
        SignUpRequest request = new SignUpRequest();
        request.setEmail("test3@test.com");
        request.setPassword("123456");
        request.setNickname("열글자초과닉네임테스트");  // 10자 초과

        // when
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        var violations = validator.validate(request);

        // then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("닉네임은 2자 이상 10자 이하여야 합니다")));
    }
}