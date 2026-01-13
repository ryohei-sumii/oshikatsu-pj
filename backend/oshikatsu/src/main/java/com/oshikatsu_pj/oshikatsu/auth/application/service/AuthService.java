package com.oshikatsu_pj.oshikatsu.auth.application.service;

import com.oshikatsu_pj.oshikatsu.auth.application.dto.request.LoginRequest;
import com.oshikatsu_pj.oshikatsu.auth.application.dto.request.RegisterRequest;
import com.oshikatsu_pj.oshikatsu.auth.application.dto.response.AuthResponse;
import com.oshikatsu_pj.oshikatsu.config.JwtTokenProvider;
import com.oshikatsu_pj.oshikatsu.auth.domain.exception.EmailAlreadyExistsException;
import com.oshikatsu_pj.oshikatsu.auth.domain.exception.UsernameAlreadyExistsException;
import com.oshikatsu_pj.oshikatsu.auth.domain.model.User;
import com.oshikatsu_pj.oshikatsu.auth.domain.repository.UserRepository;
import com.oshikatsu_pj.oshikatsu.auth.domain.service.PasswordValidator;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordValidator passwordValidator;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordValidator passwordValidator,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService userDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * ユーザー登録を行うビジネスロジック
     * @param request 登録用DTO
     * @return 登録結果
     */
    public AuthResponse register(RegisterRequest request) {
        // パスワードの検証
        passwordValidator.validate(request.getPassword());

        // ユーザー名の重複チェック
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("このユーザー名は既に使用されています");
        }

        // メールアドレスの重複チェック
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("このメールアドレスは既に使用されています");
        }

        // パスワードをハッシュ化
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // ユーザーを作成
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword
        );

        // 保存（JpaRepositoryのsaveメソッド）
        User savedUser = userRepository.save(user);

        // UserDetailsよりトークン生成
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails);

        // レスポンスを生成
        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    /**
     * ログインを実施するビジネスロジック
     * @param request ログイン用DTO
     * @return ログイン結果
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Spring Securityを使用して認証
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // UserDetailsを取得
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails == null) {
                throw new RuntimeException("内部エラーが発生しました。");
            }
            // トークン生成
            String token = jwtTokenProvider.generateToken(userDetails);

            // ユーザー情報を取得
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

            // レスポンスを生成
            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("ユーザー名またはパスワードが正しくありません。");
        }
    }
}
