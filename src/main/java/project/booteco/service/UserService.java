package project.booteco.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project.booteco.mapper.UserMapper;
import project.booteco.pruducer.UserGetResponse;
import project.booteco.pruducer.UserPostRequest;

import project.booteco.pruducer.UserPutResponse;
import project.booteco.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final UserMapper mapper;

    public UserGetResponse createdUser (UserPostRequest request){
        if(repo.findByPhoneWhatsapp(request.phoneWhatsapp()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"This number already exists");
        }
        var user = mapper.toEntity(request);

        repo.save(user);

        return mapper.toResponse(user);
    }

    public UserGetResponse findByPhone(String phoneWhatsapp){
        var user = repo.findByPhoneWhatsapp(phoneWhatsapp).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found."));

        return  mapper.toResponse(user);
    }
    public UserGetResponse updateUser(UserPutResponse request){
        var user = repo.findById(request.id()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found."));

        mapper.updateEntity(request,user);

        repo.save(user);

        return mapper.toResponse(user);
    }
}
