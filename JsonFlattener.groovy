/**
 * Script de Transformación JSON a CSV y JSON
 * Autor: [Bernardo Luscher]
 * Versión: 1.0
 *
 * Este script realiza la transformación de un JSON de entrada con la estructura del servicio
 * a dos formatos de archivo: CSV (output.csv) para usar con BFPS y JSON (output.json) para usaer con la version v0 del API.
 * 
 * Instrucciones de uso:
 * - Asegúrate de tener el archivo JSON de entrada (input.json) en la misma carpeta que este script.
 * - Ejecuta este script haciendo doble clic en run.bat.
 * - Los archivos de salida (output.csv y output.json) estarán disponibles para pruebas de servicios.
 */

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class JsonFlattener {

        static void main(String[] args) {
            try {
                def json = loadJsonFromFile('input.json')
                def flattenedJson = flattenJson(json)

                // Guardar como JSON
                saveToJsonFile(flattenedJson, 'output.json')
                println "Transformación y guardado completados. Resultado guardado en output.json"

                // Guardar como CSV
                saveToCsvFile(flattenedJson, 'output.csv')
                println "Guardado como CSV completado. Resultado guardado en output.csv"
            } catch (Exception e) {
                logError("Error durante la ejecución del script: ${e.message}")
                e.printStackTrace()
            }
        }

        static String loadJsonFromFile(String fileName) {
            try {
                logInfo("Cargando JSON desde el archivo: $fileName")
                return new File(fileName).text
            } catch (Exception e) {
                logError("Error al cargar el JSON desde el archivo: $fileName. ${e.message}")
                throw e
            }
        }

        static Map<String, Object> flattenJson(String json) {
            try {
                logInfo("Iniciando el proceso de aplanamiento del JSON.")
                def slurper = new JsonSlurper()
                def jsonObject = slurper.parseText(json)
                return flattenObject(jsonObject)
            } catch (Exception e) {
                logError("Error al parsear el JSON: ${e.message}")
                throw e
            }
        }

        static Map<String, Object> flattenObject(Map<String, Object> obj, String prefix = '') {
            def result = [:]

            obj.each { key, value ->
                def newKey = prefix.isEmpty() ? key : "${prefix}.${key}"

                if (value instanceof Map) {
                    result.putAll(flattenObject(value, newKey))
                } else if (value instanceof List) {
                    value.eachWithIndex { item, index ->
                        result.putAll(flattenObject(item, "${newKey}[${index +1}]"))
                    }
                } else {
                    result[newKey] = value
                }
            }

            return result
        }

    static void saveToJsonFile(Map<String, Object> jsonContent, String fileName) {
        try {
            logInfo("Guardando JSON transformado en el archivo: $fileName")
            def jsonBuilder = new JsonBuilder(jsonContent)
            new File(fileName).text = jsonBuilder.toPrettyString()
        } catch (Exception e) {
            logError("Error al guardar el JSON transformado en el archivo: $fileName. ${e.message}")
            throw e
        }
    }

    static void saveToCsvFile(Map<String, Object> jsonContent, String fileName) {
        try {
            logInfo("Guardando como CSV en el archivo: $fileName")
            def headers = jsonContent.keySet().collect { key -> key.toString() }
            def values = jsonContent.values().collect { value -> value.toString() }

            def csvContent = headers.join(',') + '\n' + values.join(',')
            new File(fileName).text = csvContent
        } catch (Exception e) {
            logError("Error al guardar como CSV en el archivo: $fileName. ${e.message}")
            throw e
        }
    }

        static void logInfo(String message) {
            println "[INFO] $message"
        }

        static void logError(String message) {
            println "[ERROR] $message"
        }


}
