package com.zenhotel.hrs_api.service;

import com.zenhotel.hrs_api.enums.RoomType;
import com.zenhotel.hrs_api.payload.ApiResponse;
import com.zenhotel.hrs_api.payload.PageRequestDTO;
import com.zenhotel.hrs_api.payload.PagedResponse;
import com.zenhotel.hrs_api.payload.RoomDTO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    boolean existsRoomWithRoomNumber(Integer roomNumber);

    ApiResponse addRoom(RoomDTO roomDTO, MultipartFile imageFile);

    ApiResponse getRoomById(Long roomId);

    PagedResponse<RoomDTO> getAllRooms(PageRequestDTO pageRequest);

    PagedResponse<RoomDTO> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate,
                                             RoomType roomType, PageRequestDTO pageRequest);

    List<RoomType> getAllRoomTypes();

    PagedResponse<RoomDTO> searchRoom(String query, PageRequestDTO pageRequest);

    ApiResponse updateRoom(RoomDTO roomDTO, MultipartFile imageFile);

    ApiResponse deleteRoom(Long roomId);
}
