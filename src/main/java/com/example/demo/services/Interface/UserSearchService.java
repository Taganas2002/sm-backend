package com.example.demo.services.Interface;

import com.example.demo.dto.request.UserSearchRequest;
import com.example.demo.dto.response.UserSearchResponse;

import java.util.List;

public interface UserSearchService {
    /**
     * Searches for users based on the given query (phone or email).
     *
     * @param request contains the search query.
     * @return a list of matching user responses.
     */
    List<UserSearchResponse> searchUsers(UserSearchRequest request);
}
