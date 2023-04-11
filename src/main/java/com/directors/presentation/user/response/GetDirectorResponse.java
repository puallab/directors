package com.directors.presentation.user.response;

import com.directors.domain.region.Address;
import com.directors.domain.schedule.Schedule;
import com.directors.domain.specialty.Specialty;

import java.util.List;

public record GetDirectorResponse(
        String name,
        String nickname,
        Address address,
        List<Specialty> specialtyList,
        List<Schedule> scheduleList
) {
}
