package com.jupeter.pokit.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.jupeter.pokit.auth.dto.LoginRequest;
import com.jupeter.pokit.auth.dto.SignUpRequest;
import com.jupeter.pokit.auth.entity.User;
import com.jupeter.pokit.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequest request) throws Exception {

        // 1. Firebase에 계정 생성
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getNickname());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // 2. 우리 DB에도 저장
        User user = User.builder()
                .firebaseUid(userRecord.getUid())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
    }

    public String login(LoginRequest request) throws Exception {

        // Firebase에서 이메일로 사용자 조회 후 커스텀 토큰 발급
        UserRecord userRecord = FirebaseAuth.getInstance()
                .getUserByEmail(request.getEmail());

        return FirebaseAuth.getInstance()
                .createCustomToken(userRecord.getUid());
    }
}