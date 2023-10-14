package com.spring.nowwhere.api.v1.entity.bet;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "위치 정보DTO")
public class Location {
    @Schema(description = "위도")
    private double latitude;
    @Schema(description = "경도")
    private double longitude; //경도
    @Schema(description = "이름")
    private String LocationName;

    public Location(double latitude, double longitude, String LocationName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.LocationName = LocationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 && Double.compare(location.longitude, longitude) == 0 && Objects.equals(LocationName, location.LocationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, LocationName);
    }
}
