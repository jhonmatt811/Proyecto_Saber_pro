# Documentación de la API

Este documento describe los endpoints disponibles para la gestión de personas, usuarios y resultados.

---

## 1. Crear Persona

**Endpoint**

```
POST http://localhost:8080/personas
```

**Body (JSON)**

```json
{
  "cc": 1234567890,
  "primer_nombre": "Juan",
  "segundo_nombre": "Carlos",
  "primer_apellido": "Pérez",
  "segundo_apellido": "Gómez",
  "email": "jctobon11.2@gmail.com",
  "rol_id": 2
}
```

**Respuesta (JSON)**

```json
{
  "id": "57f6ec5e-959f-42c4-b23a-5287fb777581",
  "cc": 1234567890,
  "primer_nombre": "Juan",
  "segundo_nombre": "Carlos",
  "primer_apellido": "Pérez",
  "segundo_apellido": "Gómez",
  "email": "jctobon11.2@gmail.com"
}
```

---

## 2. Crear Usuario

**Endpoint**

```
POST http://localhost:8080/usuarios
```

**Body (JSON)**

```json
{
  "person": {
    "id": "d3dd20b2-3cb1-4d30-bc8c-b11d7f4928dc",
    "cc": 1234567890,
    "primer_nombre": "Juan",
    "segundo_nombre": "Carlos",
    "primer_apellido": "Pérez",
    "segundo_apellido": "Gómez",
    "email": "jctobon11.2@gmail.com"
  },
  "rol_id": 2
}
```

**Respuesta (JSON)**

```json
{
  "status": "OK",
  "message": "Usuario Creado"
}
```

---

## 3. Subir Resultados (File)

**Endpoint**

```
POST http://localhost:8080/resultados/file
```

**Body (JSON) Ejemplo**

```json
[
  {
    "periodo": "20222",
    "aplicacion": "SABER PRO 2022-1",
    "examen": "Saber Pro",
    "tipoDocumento": "CC",
    "documento": 1032483575,
    "nombre": "CLAUDIA MARCELA CAMARGO SIERRA",
    "numeroRegistro": "EK202220401802",
    "tipoEvaluado": "Estudiante",
    "sniesIes": 1119,
    "ies": "UNIVERSIDAD DE LOS LLANOS-VILLAVICENCIO",
    "sniesProgramaAcademico": 53880,
    "programa": "BIOLOGIA",
    "ciudad": "VILLAVICENCIO",
    "idNucleoBasicoConocimiento": 34,
    "nucleoBasicoConocimiento": "BIOLOGÍA, MICROBIOLOGÍA Y AFINES",
    "puntajeGlobal": 142,
    "percentilNacionalGlobal": 49,
    "percentilNacionalNbc": 26,
    "modulo": "COMPETENCIAS CIUDADANAS",
    "puntajeModulo": 139,
    "nivelDesempeño": "2",
    "percentilNacionalModulo": 42,
    "percentilGrupoNbcModulo": 24,
    "novedades": ""
  }
  // ... más objetos con la misma estructura ...
]
```

**Respuesta (JSON)**

```json
[ /* retorna el mismo array enviado */ ]
```

---

## 4. Obtener Resultados

**Endpoint**

```
GET http://localhost:8080/resultados
```

**Respuesta (JSON)**

```json
[
  {
    "periodo": null,
    "tipoDocumento": "CC",
    "documento": 1032483575,
    "nombre": "CLAUDIA MARCELA CAMARGO SIERRA",
    "numeroRegistro": "EK202220401802",
    "tipoEvaluado": "Estudiante",
    "ies": null,
    "sniesProgramaAcademico": 53880,
    "programa": "BIOLOGIA",
    "ciudad": "VILLAVICENCIO",
    "idNucleoBasicoConocimiento": 34,
    "nucleoBasicoConocimiento": "BIOLOGÍA, MICROBIOLOGÍA Y AFINES",
    "puntajeGlobal": 142,
    "percentilNacionalGlobal": 49,
    "percentilNacionalNbc": 26,
    "modulo": "COMPETENCIAS CIUDADANAS",
    "puntajeModulo": 139,
    "nivelDesempeño": "2",
    "percentilNacionalModulo": 42,
    "percentilGrupoNbcModulo": 24,
    "novedades": "Sin novedades"
  },
  {
    "periodo": null,
    "tipoDocumento": "CC",
    "documento": 1121925944,
    "nombre": "JUAN SEBASTIAN ALVAREZ SANABRIA",
    "numeroRegistro": "EK202220995639",
    "tipoEvaluado": "Estudiante",
    "ies": null,
    "sniesProgramaAcademico": 53880,
    "programa": "BIOLOGIA",
    "ciudad": "VILLAVICENCIO",
    "idNucleoBasicoConocimiento": 34,
    "nucleoBasicoConocimiento": "BIOLOGÍA, MICROBIOLOGÍA Y AFINES",
    "puntajeGlobal": 158,
    "percentilNacionalGlobal": 71,
    "percentilNacionalNbc": 45,
    "modulo": "COMPETENCIAS CIUDADANAS",
    "puntajeModulo": 140,
    "nivelDesempeño": "2",
    "percentilNacionalModulo": 42,
    "percentilGrupoNbcModulo": 25,
    "novedades": "Sin novedades"
  }
]
```
