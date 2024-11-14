## Tạo endpoint chuyên nghiệp
- Tạo CRUD như bình thường (trả về ResponseEntity)
```java
@PostMapping()
    public ResponseEntity<ThanhVien> createThanhVien(@RequestBody ThanhVien thanhVien) {
        ThanhVien tv = this.thanhVienService.saveThanhVien(thanhVien);
        return ResponseEntity.status(HttpStatus.CREATED).body(tv);
    }
```
- Sử dụng ```@ControllerAdvice``` và kế thừa ```ResponseBodyAdvice``` để có thể đưa API trả về theo một đinh dạng chung
```java
@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        if (body instanceof String) {
            return body;
        }

        if (status >= 400) {
            return body;
        } else {
            // case success
            res.setData(body);
            res.setMessage("Call api success");
        }

        return res;
    }
}
```
- Sử dụng ```@ControllerAdvice``` và ```@HandleException``` để bắt các ngoại lên bên Controller để trả về ngoại lệ cho client
```java
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<RestResponse<Object>> handleIdException(IdInvalidException e) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(e.getMessage());
        res.setMessage("Id invalid");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
```

## Quy trình cài đặt JWT vào dự án
1. Thêm dependence [spring-boot-starter-oauth2-resource-server](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-oauth2-resource-server) vào file cấu hình
2. Tạo các biến môi trường khai báo key và thời gian sống của JWT trong ```application.properties```
3. Tạo một ***TokenService khai*** báo thuật toán mã hóa, gọi các biến môi trường ra bằng ```@Value("${name_variable}")```
4. Tạo một ***JwtConfiguration*** cấu hình sử dụng JWT
5. tại ***TokenService** viết hàm tạo token:
``` java
public String createToken(Authentication authentication) {
   Instant now = Instant.now();
   Instant validity = now.plus(this.jwtExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("dangthuc", authentication)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
   }
```
6. sử dụng trong endpoint login:
``` java
@PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@RequestBody LoginDTO loginDto) {

        // nạp input gồm username và password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // create a token
        String access_token = this.tokenService.createToken(authentication);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setAccess_token(access_token);
        return ResponseEntity.ok(resLoginDTO);
    }
```
7. Cấu hình JWT trong SecurityConfiguration:
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(c -> c.disable())
            .authorizeHttpRequests(
                    auth -> auth
                            .requestMatchers("/api/login", "/").permitAll()
                            .anyRequest().authenticated()
            )
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
            .formLogin(f -> f.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
}
```
8. Cấu hình ```decoder```
```java
@Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(getSecretKey())
                .macAlgorithm(TokenService.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }
```
## Exception JWT
1. Xử lý exception khi đăng nhập sai
```java
@ExceptionHandler(value = {
            NullPointerException.class,
            AuthenticationException.class
    })
    public ResponseEntity<RestResponse<Object>> handleIdInvalidException(Exception ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Thong tin tai khoan ko hop le");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
```
bắt exception trong hàm loadUserByUsername:
```java
if(thanhVien == null) {
    throw new UsernameNotFoundException(username);
}
```
2. Exception JWT.
- add to SecurityFilterChain
```java
http.exceptionHandling(
        exceptions -> exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
        )
```
- create class implements AuthenticationEntryPoint
```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request, response, authException);
        response.setContentType("application/json;charset=UTF-8");

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = Optional.ofNullable(authException.getCause()) // NULL
                .map(Throwable::getMessage)
                .orElse(authException.getMessage());
        res.setError(errorMessage);

        res.setMessage("Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header)...");

        mapper.writeValue(response.getWriter(), res);
    }
}
```
- add class to SecurityFilterChain
```java
http.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
        .authenticationEntryPoint(customAuthenticationEntryPoint))
```