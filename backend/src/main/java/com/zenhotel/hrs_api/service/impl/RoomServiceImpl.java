package com.zenhotel.hrs_api.service.impl;

import com.zenhotel.hrs_api.entity.Room;
import com.zenhotel.hrs_api.enums.RoomType;
import com.zenhotel.hrs_api.enums.UploadType;
import com.zenhotel.hrs_api.exception.DuplicateResourceException;
import com.zenhotel.hrs_api.exception.InvalidBookingException;
import com.zenhotel.hrs_api.exception.ResourceNotFoundException;
import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payload.PageRequestDTO;
import com.zenhotel.hrs_api.payload.PagedResponse;
import com.zenhotel.hrs_api.payload.RoomDTO;
import com.zenhotel.hrs_api.repository.RoomRepository;
import com.zenhotel.hrs_api.service.RoomService;
import com.zenhotel.hrs_api.upload.cloudinary.UploadService;
import com.zenhotel.hrs_api.upload.cloudinary.payload.UploadSignatureRequest;
import com.zenhotel.hrs_api.utils.ImageUtil;
import com.zenhotel.hrs_api.utils.RoomUpdaterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final UploadService uploadService;
    private final ImageUtil imageUtil;
    private final RoomUpdaterUtil roomUpdaterUtil;
    private final ModelMapper modelMapper;

    @Override
    public boolean existsRoomWithRoomNumber(Integer roomNumber) {
        return roomRepository.existsByRoomNumber(roomNumber);
    }

    @Override
    @Transactional
    public ApiResponse addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        // Check if room with same room number already exists
        Integer roomNumber = roomDTO.getRoomNumber();
        if (existsRoomWithRoomNumber(roomNumber)) {
            throw new DuplicateResourceException("Room with room number [%s] already exists".formatted(roomNumber));
        }

        Room roomToSave = modelMapper.map(roomDTO, Room.class);
        Room savedRoom = roomRepository.save(roomToSave);

        if (imageFile != null && !imageFile.isEmpty()) {
            // Update room image
            Room roomWithImage = updateRoomImage(savedRoom.getId(), imageFile);

            // Save room with image
            roomRepository.save(roomWithImage);
        }

        return ApiResponse.builder()
                .status(201)
                .message("Room added successfully")
                .build();
    }

    @Override
    public ApiResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id [%s] not found".formatted(roomId)));

        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return ApiResponse.builder()
                .status(200)
                .message("success")
                .payload(Map.of("room", roomDTO))
                .build();
    }

    @Override
    public PagedResponse<RoomDTO> getAllRooms(PageRequestDTO pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        Page<Room> roomPage = roomRepository.findAll(pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                roomPage,
                modelMapper,
                new TypeToken<List<RoomDTO>>() {
                });
    }

    @Override
    public PagedResponse<RoomDTO> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate,
                                                    RoomType roomType, PageRequestDTO pageRequest) {
        validateBookingDates(checkInDate, checkOutDate);

        Pageable pageable = pageRequest.toPageable();
        Page<Room> roomPage = roomRepository.findAvailableRooms(checkInDate, checkOutDate,
                roomType, pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                roomPage,
                modelMapper,
                new TypeToken<List<RoomDTO>>() {
                });
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return List.of(RoomType.values());
    }

    @Override
    public PagedResponse<RoomDTO> searchRoom(String query, PageRequestDTO pageRequest) {
        Pageable pageable = pageRequest.toPageable();
        Page<Room> roomPage = roomRepository.searchRooms(query, pageable);

        return PagedResponse.fromPage(
                200,
                "success",
                roomPage,
                modelMapper,
                new TypeToken<List<RoomDTO>>() {
                });
    }

    @Override
    @Transactional
    public ApiResponse updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        Long roomId = roomDTO.getId();
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id [%s] not found".formatted(roomId)));

        // Check if room with same room number already exists
        Integer roomNumber = roomDTO.getRoomNumber();
        if (existsRoomWithRoomNumber(roomNumber)) {
            throw new DuplicateResourceException("Room with room number [%s] already exists".formatted(roomNumber));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String prevImageUploadId = existingRoom.getImageUploadId();

            // Update room image
            existingRoom = updateRoomImage(roomId, imageFile);

            // Delete previous room image resource
            removeRoomImageResource(prevImageUploadId);
        }

        // Update room details from DTO
        Room updatedRoom = roomUpdaterUtil.updateRoomDetailsFromDTO(roomDTO, existingRoom);

        roomRepository.save(updatedRoom);

        return ApiResponse.builder()
                .status(200)
                .message("Room updated successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id [%s] not found".formatted(roomId)));

        // Delete room image resource
        removeRoomImageResource(room.getImageUploadId());

        roomRepository.deleteById(roomId);

        return ApiResponse.builder()
                .status(200)
                .message("Room deleted successfully")
                .build();
    }

    private void validateBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        // validate check in and check out dates
        List<String> errors = Stream.of(
                        checkInDate.isBefore(LocalDate.now()) ? "Check in date cannot be in the past" : null,
                        checkOutDate.isBefore(checkInDate) ? "Check out date cannot be before check in date" : null,
                        checkInDate.isEqual(checkOutDate) ? "Check in and check out dates cannot be the same" : null
                ).filter(Objects::nonNull)
                .toList();

        if (!errors.isEmpty()) {
            throw new InvalidBookingException(errors.size() == 1
                    ? errors.getFirst() : errors.toString());
        }
    }

    public Room updateRoomImage(Long roomId, MultipartFile imageFile) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id [%s] not found".formatted(roomId)));

        // Get image bytes from image file
        byte[] imageBytes = imageUtil.getImageBytesFromMultipartFile(imageFile);

        // Upload room image to cloudinary and get the public ID and secure URL
        UploadSignatureRequest signatureRequest = new UploadSignatureRequest(room.getId(), UploadType.ROOM.name());
        Map<String, String> uploadedImageResult = uploadService.uploadImageResourceToCloudinary(imageBytes, signatureRequest);
        String publicId = uploadedImageResult.get("publicId");
        String secureUrl = uploadedImageResult.get("secureUrl");

        // Update room imageUploadId and imageUrl with the public ID and secure URL from cloudinary
        room.setImageUploadId(publicId);
        room.setImageUrl(secureUrl);

        // Return the room with updated image
        return room;
    }

    private void removeRoomImageResource(String roomImageUploadId) {
        if (roomImageUploadId != null && !roomImageUploadId.isBlank()) {
            // Delete room image resource from cloudinary
            uploadService.deleteCloudinaryImageResourceByPublicId(roomImageUploadId, true);
        }
    }

}
