package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.UserGenreRequest;
import com.example.nextune_backend.dto.response.UserGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.entity.UserGenre;
import com.example.nextune_backend.entity.UserGenreId;
import com.example.nextune_backend.mapper.UserGenreMapper;
import com.example.nextune_backend.repository.GenreRepository;
import com.example.nextune_backend.repository.UserGenreRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.UserGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserGenreServiceImpl implements UserGenreService {

    private final UserGenreRepository userGenreRepository;
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final UserGenreMapper userGenreMapper;

    @Override
    public UserGenreResponse addGenreToUser(UserGenreRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        UserGenreId id = new UserGenreId(request.getUserId(), request.getGenreId());
        if (userGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre already assigned to user");
        }

        UserGenre entity = userGenreMapper.toEntity(request, user, genre);
        return userGenreMapper.toResponse(userGenreRepository.save(entity));
    }

    @Override
    public void removeGenreFromUser(String userId, String genreId) {
        UserGenreId id = new UserGenreId(userId, genreId);
        if (!userGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found for this user");
        }
        userGenreRepository.deleteById(id);
    }

    @Override
    public List<UserGenreResponse> getGenresByUser(String userId) {
        List<UserGenre> entities = userGenreRepository.findByUser_Id(userId);
        return userGenreMapper.toResponseList(entities);
    }
}

