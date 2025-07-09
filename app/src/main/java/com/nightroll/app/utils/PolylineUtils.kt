package com.nightroll.app.utils

import com.google.android.gms.maps.model.LatLng

object PolylineUtils {
    
    /**
     * Decodes a polyline string into a list of LatLng points
     * Based on Google's polyline algorithm
     */
    fun decode(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            
            shift = 0
            result = 0
            
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        
        return poly
    }
    
    /**
     * Encodes a list of LatLng points into a polyline string
     */
    fun encode(path: List<LatLng>): String {
        var lastLat = 0L
        var lastLng = 0L
        
        val result = StringBuilder()
        
        for (point in path) {
            val lat = Math.round(point.latitude * 1e5)
            val lng = Math.round(point.longitude * 1e5)
            
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            
            encode(dLat, result)
            encode(dLng, result)
            
            lastLat = lat
            lastLng = lng
        }
        
        return result.toString()
    }
    
    private fun encode(v: Long, result: StringBuilder) {
        var value = if (v < 0) (v shl 1).inv() else v shl 1
        
        while (value >= 0x20) {
            result.append(((0x20 or (value and 0x1f)) + 63).toInt().toChar())
            value = value shr 5
        }
        
        result.append((value + 63).toInt().toChar())
    }
}