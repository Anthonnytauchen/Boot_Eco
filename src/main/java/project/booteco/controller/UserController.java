package project.booteco.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.booteco.pruducer.UserGetResponse;
import project.booteco.pruducer.UserPostRequest;
import project.booteco.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserGetResponse> createdUser(@Valid @RequestBody UserPostRequest request){

        return ResponseEntity.ok(userService.createdUser(request));

    }
}
