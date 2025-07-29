package com.zenhotel.hrs_api.utils;

import com.zenhotel.hrs_api.entity.Room;
import com.zenhotel.hrs_api.payload.RoomDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class RoomUpdaterUtil {

    private <T> void setValueIfPresent(Supplier<T> getter, Consumer<T> setter) {
        Optional.ofNullable(getter.get()).ifPresent(setter);
    }

    private <T> void setValueIfValid(Supplier<T> getter, Predicate<T> validator, Consumer<T> setter) {
        Optional.ofNullable(getter.get())
                .filter(validator)
                .ifPresent(setter);
    }

    public Room updateRoomDetailsFromDTO(RoomDTO roomDTO, Room existingRoom) {
        setValueIfValid(roomDTO::getRoomNumber, roomNumber -> roomNumber >= 0, existingRoom::setRoomNumber);
        setValueIfValid(roomDTO::getPricePerNight, price -> price.compareTo(BigDecimal.ZERO) >= 0, existingRoom::setPricePerNight);
        setValueIfValid(roomDTO::getCapacity, capacity -> capacity > 0, existingRoom::setCapacity);

        setValueIfPresent(roomDTO::getType, existingRoom::setType);
        setValueIfPresent(roomDTO::getDescription, existingRoom::setDescription);

        return existingRoom;
    }

}
