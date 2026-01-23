package com.marcedev.barberapp.service;

import com.marcedev.barberapp.dto.LoginRequest;
import com.marcedev.barberapp.dto.LoginResponse;
import com.marcedev.barberapp.dto.MeResponse;
import com.marcedev.barberapp.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    MeResponse me(Long userId);

    LoginResponse register(RegisterRequest request);


}
