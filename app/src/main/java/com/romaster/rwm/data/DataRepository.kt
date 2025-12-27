package com.romaster.rwm.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepository(context: Context) {
    
    private val dataSourceManager = DataSourceManager(context)
    private val formulaEngine = FormulaEngine(context, dataSourceManager)
    
    // Obtener valor de una fuente de datos
    fun getData(sourceId: String, property: String): Any {
        return dataSourceManager.getValue(sourceId, property)
    }
    
    // Evaluar fórmula
    fun evaluateFormula(formula: String): Any {
        return formulaEngine.evaluate(formula)
    }
    
    // Obtener datos comunes predefinidos
    fun getBatteryLevel(): Flow<Int> {
        return kotlinx.coroutines.flow.flow {
            while (true) {
                val level = dataSourceManager.getValue("battery", "level") as? Int ?: 0
                emit(level)
                kotlinx.coroutines.delay(60000) // Actualizar cada minuto
            }
        }
    }
    
    fun getCurrentTime(): Flow<String> {
        return kotlinx.coroutines.flow.flow {
            while (true) {
                val time = dataSourceManager.getValue("time", "formatted") as? String ?: "00:00"
                emit(time)
                kotlinx.coroutines.delay(1000) // Actualizar cada segundo
            }
        }
    }
    
    // Registrar fuente de datos personalizada
    fun registerCustomDataSource(id: String, getter: () -> Any) {
        val customSource = object : DataSource {
            override val id: String = id
            override val type: DataSourceType = DataSourceType.CUSTOM
            
            override fun getValue(property: String): Any {
                return getter()
            }
            
            override fun startObserving() {
                // No hacer nada para fuentes personalizadas simples
            }
            
            override fun stopObserving() {
                // No hacer nada
            }
        }
        
        dataSourceManager.registerDataSource(customSource)
    }
    
    // Limpiar recursos
    fun cleanup() {
        dataSourceManager.cleanup()
    }
}