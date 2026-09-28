package com.scholartrack.service;

import com.scholartrack.model.dto.AuthRequest;
import com.scholartrack.model.dto.AuthResponse;
import com.scholartrack.model.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
}
