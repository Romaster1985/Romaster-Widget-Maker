package com.romaster.rwm.data

import android.content.Context

class FormulaEngine(private val context: Context, private val dataSourceManager: DataSourceManager) {
    
    fun evaluate(expression: String): Any {
        return try {
            when {
                // Expresión simple con datos
                expression.matches(Regex("""\$[a-zA-Z_]+\.[a-zA-Z_]+\$""" )) -> {
                    evaluateDataSourceExpression(expression)
                }
                // Texto plano
                else -> expression
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
    
    private fun evaluateDataSourceExpression(expression: String): Any {
        val cleanExpr = expression.removeSurrounding("$")
        val parts = cleanExpr.split(".")
        if (parts.size != 2) {
            throw IllegalArgumentException("Invalid expression: $expression")
        }
        
        val sourceId = parts[0]
        val property = parts[1]
        
        return dataSourceManager.getValue(sourceId, property)
    }
}