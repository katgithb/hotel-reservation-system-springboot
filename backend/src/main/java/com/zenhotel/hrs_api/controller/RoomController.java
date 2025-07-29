package com.zenhotel.hrs_api.controller;

import com.zenhotel.hrs_api.enums.RoomType;
import com.zenhotel.hrs_api.payload.*;
import com.zenhotel.hrs_api.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> addRoom(
            @Valid @ModelAttribute RoomRequest request) {

        RoomDTO roomDTO = RoomDTO.builder()
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .createdAt(OffsetDateTime.now())
                .build();

        return new ResponseEntity<>(roomService.addRoom(roomDTO, request.getImageFile()),
                HttpStatus.CREATED);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedResponse<RoomDTO>> getAllRooms(
            @ModelAttribute PageRequestDTO pageRequest) {

        return ResponseEntity.ok(roomService.getAllRooms(pageRequest));
    }

    @GetMapping("/available")
    public ResponseEntity<PagedResponse<RoomDTO>> getAvailableRooms(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) String type,
            @ModelAttribute PageRequestDTO pageRequest
    ) {
        RoomType roomType = Optional.ofNullable(type)
                .map(RoomType::from)
                .orElse(null);

        return ResponseEntity.ok(roomService.getAvailableRooms(checkInDate, checkOutDate,
                roomType, pageRequest));
    }

    @GetMapping("/types")
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<RoomDTO>> searchRoom(
            @RequestParam("q") String query,
            @ModelAttribute PageRequestDTO pageRequest) {

        return ResponseEntity.ok(roomService.searchRoom(query, pageRequest));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> updateRoom(
            @RequestParam(value = "id", required = true) Long roomId,
            @ModelAttribute RoomRequest request
    ) {
        RoomDTO roomDTO = RoomDTO.builder()
                .id(roomId)
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .build();

        return ResponseEntity.ok(roomService.updateRoom(roomDTO, request.getImageFile()));
    }

    @DeleteMapping("/delete/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }

}
