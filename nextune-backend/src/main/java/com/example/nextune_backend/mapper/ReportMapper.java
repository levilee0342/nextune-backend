package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.ReportRequest;
import com.example.nextune_backend.dto.request.ReportUpdateRequest;
import com.example.nextune_backend.dto.response.ReportResponse;
import com.example.nextune_backend.entity.Report;
import com.example.nextune_backend.entity.User;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "createdDate", expression = "java(new java.util.Date())")
    @Mapping(target = "entityId", source = "request.entityId")
    @Mapping(target = "reportType", source = "request.reportType")
    @Mapping(target = "status", ignore = true)
    Report toEntity(ReportRequest request, User user);

    @Mapping(target = "userId", source = "user.id")
    ReportResponse toResponse(Report entity);

    List<ReportResponse> toResponseList(List<Report> entities);

    // chỉ cho phép sửa content & reportType
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "entityId", ignore = true)
    void updateFromRequest(ReportUpdateRequest request, @MappingTarget Report entity);
}
