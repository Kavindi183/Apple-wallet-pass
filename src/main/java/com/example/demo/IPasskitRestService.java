package com.example.demo;

public interface IPasskitRestService {
    public byte[] createPasskit(User user);
    public String getFileName(User user);
}
