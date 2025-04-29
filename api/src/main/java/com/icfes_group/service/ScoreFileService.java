package com.icfes_group.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.repository.IcfesTestRepository.*;
import com.icfes_group.model.IcfesTest.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ScoreFileService {
    
    @Autowired
    TypeIdentityCardRepository typeCardRepository;
    @Autowired
    EvaluatedRepository evalRepository;
    @Autowired
    EvaluatedTypeRepository evalTypeRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    AcademicProgramRepository academicRepository;
    @Autowired
    ModuleCatalogRepository moduleCatalogRepository;
    @Autowired
    ReferenceGroupRepository referenceGroupRepository;
    @Autowired
    TestRegistrationRepository testRegistrationRepository;
    
    @Autowired
    GlobalResultRepository globalRespository;
    // Método para agrupar los datos por documento
    private Map<Long, List<ScoreFileDTO>> groupByDocument(ScoreFileDTO[] dto) {
        return Arrays.stream(dto)
                .collect(Collectors.groupingBy(ScoreFileDTO::getDocumento));
    }

    private Evaluated saveEvaluated(ScoreFileDTO dto, List<TypeIdentityCard> bankTypeIdCard) {
        // Buscar si ya existe en los precargados
        Optional<TypeIdentityCard> optionalTipo = bankTypeIdCard.stream()
            .filter(t -> t.getNombre().equalsIgnoreCase(dto.getTipoDocumento()))
            .findFirst();

        TypeIdentityCard typeIdCard;

        if (optionalTipo.isPresent()) {
            typeIdCard = optionalTipo.get();
        } else {
            // Crear y guardar el nuevo tipo
            typeIdCard = new TypeIdentityCard();
            typeIdCard.setNombre(dto.getTipoDocumento());
            typeIdCard = typeCardRepository.save(typeIdCard);

            // (Opcional) actualizar el banco en memoria si se sigue reutilizando
            bankTypeIdCard.add(typeIdCard);
        }

        Evaluated eval = new Evaluated(dto, typeIdCard);
        return evalRepository.save(eval);
    }

    
    // Método para guardar tipos de documentos
    public void saveTypeDocuments(Set<String> typesDocs) {
        // Buscar los que ya existen
        Set<String> existentes = typeCardRepository.findByNombreIn(typesDocs)
                .stream()
                .map(TypeIdentityCard::getNombre)
                .collect(Collectors.toSet());

        // Filtrar solo los nuevos
        List<TypeIdentityCard> nuevos = typesDocs.stream()
            .filter(nombre -> !existentes.contains(nombre))
            .map(nombre -> {
                TypeIdentityCard card = new TypeIdentityCard();
                card.setNombre(nombre);
                return card;
            })
            .collect(Collectors.toList());

        typeCardRepository.saveAll(nuevos);
    }

    
    // Método para guardar tipos de evaluados
    private void saveTypesEvals(Set<String> typesEvals) {
        Set<String> existentes = evalTypeRepository.findByNombreIn(typesEvals)
            .stream()
            .map(EvaluatedType::getNombre)
            .collect(Collectors.toSet());

        List<EvaluatedType> nuevos = typesEvals.stream()
            .filter(nombre -> !existentes.contains(nombre))
            .map(nombre -> {
                EvaluatedType type = new EvaluatedType();
                type.setNombre(nombre);
                return type;
            })
            .collect(Collectors.toList());

        evalTypeRepository.saveAll(nuevos);
    }

    private void saveCities(Set<String> cities) {
        Set<String> existentes = cityRepository.findByNombreIn(cities)
            .stream()
            .map(City::getNombre)
            .collect(Collectors.toSet());

        List<City> nuevos = cities.stream()
            .filter(nombre -> !existentes.contains(nombre))
            .map(nombre -> {
                City city = new City();
                city.setNombre(nombre);
                return city;
            })
            .collect(Collectors.toList());

        cityRepository.saveAll(nuevos);
    }

    private void saveAcademicPrograms(Set<String> academicProg) {
        Set<String> existentes = academicRepository.findByNombreIn(academicProg)
            .stream()
            .map(AcademicProgram::getNombre)
            .collect(Collectors.toSet());

        List<AcademicProgram> nuevos = academicProg.stream()
            .filter(nombre -> !existentes.contains(nombre))
            .map(nombre -> {
                AcademicProgram program = new AcademicProgram();
                program.setNombre(nombre);
                return program;
            })
            .collect(Collectors.toList());

        academicRepository.saveAll(nuevos);
    }

    private void saveModulesCatalogs(Set<String> modules) {
        Set<String> existentes = moduleCatalogRepository.findByNombreIn(modules)
            .stream()
            .map(ModuleCatalog::getNombre)
            .collect(Collectors.toSet());

        List<ModuleCatalog> nuevos = modules.stream()
            .filter(nombre -> !existentes.contains(nombre))
            .map(nombre -> {
                ModuleCatalog module = new ModuleCatalog();
                module.setNombre(nombre);
                return module;
            })
            .collect(Collectors.toList());

        moduleCatalogRepository.saveAll(nuevos);
    }


    // Método para guardar grupos de referencia
    /*private void saveReferencesGroups(Set<String> referenceGroup) {
        List<ReferenceGroup> listReferenceGroup = referenceGroup.stream()
            .map(nombre -> {
                ReferenceGroup group = new ReferenceGroup();
                group.setNombre(nombre);
                return group;
            })
            .collect(Collectors.toList());
        referenceGroupRepository.saveAll(listReferenceGroup);
    }*/

    
   private TestRegistration saveTestRegistrarion(
        ScoreFileDTO dto,
        List<AcademicProgram> programsBank,
        List<City> citiesBank,
        List<EvaluatedType> evalTypesBank) {

        Optional<TestRegistration> optionalTest = testRegistrationRepository.findById(dto.getNumeroRegistro());

        if (optionalTest.isPresent()) {
            return optionalTest.get(); // Ya existe, lo retornamos
        }

        // No existe, creamos uno nuevo
        City city = citiesBank.stream()
            .filter(t -> t.getNombre().equalsIgnoreCase(dto.getCiudad()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Ciudad no encontrada: " + dto.getCiudad()));

        AcademicProgram program = programsBank.stream()
            .filter(t -> t.getNombre().equalsIgnoreCase(dto.getPrograma()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Programa no encontrada: " + dto.getPrograma()));

        EvaluatedType eval = evalTypesBank.stream()
            .filter(t -> t.getNombre().equalsIgnoreCase(dto.getTipoEvaluado()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Tipo no encontrado: " + dto.getTipoEvaluado()));

        TestRegistration test = new TestRegistration(dto, program.getId(), eval.getId(), city.getId());
        return testRegistrationRepository.save(test);
    }
    
    private GlobalResult saveGlobalResuslt(ScoreFileDTO dto, TestRegistration testRegistred){
        Optional<GlobalResult> optionalResult = globalRespository.findById(dto.getNumeroRegistro());
        if(optionalResult.isPresent()){
            return optionalResult.get();
        }
        GlobalResult result = new GlobalResult(dto,testRegistred);
        return globalRespository.save(result);
    }
    
    // Método para guardar los datos del archivo
    public ScoreFileDTO[] saveDataFile(ScoreFileDTO[] dto) {
    // Agrupar por documento
        Map<Long, List<ScoreFileDTO>> agrupados = groupByDocument(dto);

        // Extraer tipos de documento y evaluado
        Set<String> tiposDocumento = Arrays.stream(dto)
            .map(ScoreFileDTO::getTipoDocumento)
            .collect(Collectors.toSet());

        Set<String> typesEvals = Arrays.stream(dto)
            .map(ScoreFileDTO::getTipoEvaluado)
            .collect(Collectors.toSet());
        Set<String> cities = Arrays.stream(dto)
            .map(ScoreFileDTO::getCiudad)
            .collect(Collectors.toSet());

        Set<String> academicPrograms = Arrays.stream(dto)
            .map(ScoreFileDTO::getPrograma)
            .collect(Collectors.toSet());

        Set<String> modulesCatalog = Arrays.stream(dto)
            .map(ScoreFileDTO::getModulo)
            .collect(Collectors.toSet());

        //Set<String> referenceGroups = Arrays.stream(dto)
//            .map(ScoreFileDTO::getGrupoReferencia)
  //          .collect(Collectors.toSet());

        // Guardar documentos, evaluados y demás
        saveTypeDocuments(tiposDocumento);
        saveTypesEvals(typesEvals);
        saveCities(cities);
        saveAcademicPrograms(academicPrograms);
        saveModulesCatalogs(modulesCatalog);

        // Obtener los tipos de documentos guardados
        List<TypeIdentityCard> bankTypeIdCard = typeCardRepository.findAll();       
//        saveReferencesGroups(referenceGroups);
        List<City> citiesBank = cityRepository.findAll();
        List<AcademicProgram> programsBank = academicRepository.findAll();
        List<ModuleCatalog> modulesBank = moduleCatalogRepository.findAll();            
        List<EvaluatedType> evalTypesBank = evalTypeRepository.findAll();
        // Guardar Evaluated para cada grupo de documento       
        agrupados.forEach((documento, lista) -> {
            ScoreFileDTO dtoIter = lista.get(0);
            saveEvaluated(dtoIter, bankTypeIdCard); // Guardar el Evaluado
            TestRegistration test = saveTestRegistrarion(dtoIter,programsBank,citiesBank,evalTypesBank);
            saveGlobalResuslt(dtoIter, test);
        });
         
        return dto; // Retornar los datos procesados
    }
}
