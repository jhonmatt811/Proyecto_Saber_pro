package com.icfes_group.security;

import com.icfes_group.model.IcfesTest.ReferenceGroup;
import com.icfes_group.repository.IcfesTestRepository.ReferenceGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReferenceGroupService {
    private final ReferenceGroupRepository groupsReferencesRepository;
    public List<ReferenceGroup> getAllGroups() throws Exception{
        List<ReferenceGroup> response = groupsReferencesRepository.findAll();
        if(response.isEmpty()){
            throw new Exception("No hay grupos registrados");
        }
        return response;
    }
}
