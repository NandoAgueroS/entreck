package com.entreck.pos.application.dto;

/**
 * DTO for contact information.
 *
 * @param email the contact email address
 * @param phone the contact phone number
 */
public record ContactDto(String email, String phone) {}
