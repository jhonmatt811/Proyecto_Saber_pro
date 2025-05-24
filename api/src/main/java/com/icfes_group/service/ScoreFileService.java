package com.icfes_group.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.repository.IcfesTestRepository.*;
import com.icfes_group.model.IcfesTest.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.text.Normalizer;

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
    ModuleResultRepository moduleResultRepository;
    @Autowired
    GlobalResultRepository globalRespository;
    // Método para agrupar los datos por documento

    private static String normalizar(String texto) {
        if (texto == null) return null;
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toUpperCase()
                .trim();
    }

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

    private void saveAcademicPrograms(Set<AcademicProgram> academicProg) { 
        // Obtener los nombres de los programas ya existentes en la BD
        Set<String> existentes = academicRepository.findByNombreIn(
                academicProg.stream()
                    .map(AcademicProgram::getNombre)
                    .collect(Collectors.toSet())
            )
            .stream()
            .map(AcademicProgram::getNombre)
            .collect(Collectors.toSet());

        // Filtrar los programas que no existen aún y prepararlos para guardar
        List<AcademicProgram> nuevos = academicProg.stream()
            .filter(prog -> !existentes.contains(prog.getNombre()))
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
    private void saveReferencesGroups(List<ReferenceGroup> referenceProp) {
        // Normalizar los nombres y eliminar duplicados
        Set<ReferenceGroup> noRepeat = referenceProp.stream()
                .map(group -> new ReferenceGroup(
                        normalizar(group.getNombre()) // Normalizamos el nombre
                ))
                .distinct()
                .collect(Collectors.toSet());

        // Obtener todos los grupos existentes en el banco de datos
        List<ReferenceGroup> bankGroups = referenceGroupRepository.findAll();

        // Crear un set con solo los nombres de los grupos existentes
        Set<String> existingNames = bankGroups.stream()
                .map(ReferenceGroup::getNombre)
                .collect(Collectors.toSet());

        // Filtrar solo los grupos que no existen en el banco (por nombre)
        List<ReferenceGroup> nuevos = noRepeat.stream()
                .filter(group -> !existingNames.contains(group.getNombre())) // Compara solo el nombre
                .collect(Collectors.toList());

        // Guardar los nuevos grupos en la base de datos
        if(!nuevos.isEmpty()){
            referenceGroupRepository.saveAll(nuevos);
        }
    }

    private TestRegistration saveTestRegistrarion(
        ScoreFileDTO dto,
        List<AcademicProgram> programsBank,
        List<City> citiesBank,
        List<EvaluatedType> evalTypesBank) {

        Optional<TestRegistration> optionalTest = testRegistrationRepository.findByNumeroRegistroAndYear(dto.getNumeroRegistro(), dto.getYear());

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

    private GlobalResult saveGlobalResult(ScoreFileDTO dto, TestRegistration testRegistred, List<ReferenceGroup> bankGroups) {
        // Usamos el 'id' de testRegistred (que ya es un UUID) para buscar el GlobalResult
        Optional<GlobalResult> optionalResult = globalRespository.findById(testRegistred.getId());

        if (optionalResult.isPresent()) {
            return optionalResult.get(); // Ya existe, lo retornamos
        }

        // Si no existe, buscamos el grupo de referencia (si no lo encontramos, se lanza una excepción)
        ReferenceGroup referenceGroup = bankGroups.stream()
                .filter(group -> group.getNombre().equalsIgnoreCase(normalizar(dto.getNucleoBasicoConocimiento())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Grupo de referencia no encontrado: " + dto.getNucleoBasicoConocimiento()));

        // Creamos el nuevo GlobalResult usando el TestRegistration y el grupo de referencia
        GlobalResult result = new GlobalResult(dto, testRegistred, referenceGroup);

        return globalRespository.save(result);
    }

    public ModuleResult saveModuleResult (ScoreFileDTO dto,GlobalResult globalResult){
        Optional<ModuleCatalog> moduleCatalog = moduleCatalogRepository.findByNombre(dto.getModulo());
        Optional<ModuleResult> optionalModuleResult = moduleResultRepository.findByGlobalRsltAndCatId(moduleCatalog.get().getId(),globalResult.getId());
        if(optionalModuleResult.isPresent()){
            return optionalModuleResult.get();
        }
        ModuleResult result = new ModuleResult(dto,globalResult,moduleCatalog.get());
        return moduleResultRepository.save(result);
    }

    // Método para guardar los datos del archivo
    @Async
    public void saveDataFile(ScoreFileDTO[] dto) {
        Map<Long, List<ScoreFileDTO>> agrupados = groupByDocument(dto);

        // Recolección de valores únicos
        Set<String> tiposDocumento = Arrays.stream(dto).map(ScoreFileDTO::getTipoDocumento).collect(Collectors.toSet());
        Set<String> typesEvals = Arrays.stream(dto).map(ScoreFileDTO::getTipoEvaluado).collect(Collectors.toSet());
        Set<String> cities = Arrays.stream(dto).map(ScoreFileDTO::getCiudad).collect(Collectors.toSet());
        Set<AcademicProgram> academicPrograms = Arrays.stream(dto)
            .map(d -> {
                AcademicProgram ap = new AcademicProgram();
                ap.setSnies(d.getSniesProgramaAcademico());
                ap.setNombre(d.getPrograma());
                return ap;
            })
            .collect(Collectors.toSet());

        Set<String> modulesCatalog = Arrays.stream(dto).map(ScoreFileDTO::getModulo).collect(Collectors.toSet());

        List<ReferenceGroup> referenceGroups = Arrays.stream(dto).map(scoreDto -> {
            ReferenceGroup group = new ReferenceGroup();
            group.setNombre(scoreDto.getNucleoBasicoConocimiento());
            return group;
        }).collect(Collectors.toList());

        // Guardar referencias
        saveTypeDocuments(tiposDocumento);
        saveTypesEvals(typesEvals);
        saveCities(cities);
        saveAcademicPrograms(academicPrograms);
        saveModulesCatalogs(modulesCatalog);
        saveReferencesGroups(referenceGroups);

        // Obtener bancos de datos
        List<TypeIdentityCard> bankTypeIdCard = typeCardRepository.findAll();       
        List<City> citiesBank = cityRepository.findAll();
        List<AcademicProgram> programsBank = academicRepository.findAll();
        List<ModuleCatalog> modulesBank = moduleCatalogRepository.findAll();            
        List<EvaluatedType> evalTypesBank = evalTypeRepository.findAll();
        List<ReferenceGroup> groupReferencesBank = referenceGroupRepository.findAll();

        agrupados.forEach((documento, lista) -> {
            ScoreFileDTO dtoIter = lista.get(0);

            // Guardar evaluado
            Evaluated evaluado = saveEvaluated(dtoIter, bankTypeIdCard);

            // Registrar evaluación
            TestRegistration test = saveTestRegistrarion(dtoIter, programsBank, citiesBank, evalTypesBank);

            // Resultado global
            GlobalResult globalResult = saveGlobalResult(dtoIter, test,groupReferencesBank);

            // Resultado de módulo
            ModuleResult resultadoModulo = saveModuleResult(dtoIter, globalResult);

            // Recorremos todos los DTOs para guardar cada módulo
            lista.forEach(d -> saveModuleResult(d, globalResult));
        });
        }

}
