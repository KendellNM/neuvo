# ✅ Formato CORRECTO para STOMP en Postman

## ❌ INCORRECTO (con espacios):

```
SEND    
    destination:/app/delivery/location    
    content-type:application/json        
    {"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Problema:** Los espacios al inicio de las líneas causan error.

---

## ✅ CORRECTO (sin espacios):

```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Importante:**
- ✅ NO espacios al inicio de las líneas
- ✅ Una línea en blanco antes del JSON
- ✅ Cada header en su propia línea

---

## 📋 COPIA Y PEGA ESTO EXACTAMENTE:

### Para SUSCRIBIRSE:

```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

### Para ENVIAR UBICACIÓN:

```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

---

## 🎯 REGLAS DEL FORMATO STOMP:

1. **Primera línea:** Comando (SEND, SUBSCRIBE, etc.)
2. **Siguientes líneas:** Headers en formato `key:value`
3. **SIN espacios** al inicio de las líneas
4. **Línea en blanco** antes del body
5. **Body:** El JSON o contenido

---

## 📝 EJEMPLOS LISTOS PARA COPIAR:

### Ejemplo 1: Suscribirse al pedido 123
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

### Ejemplo 2: Enviar ubicación 1
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

### Ejemplo 3: Enviar ubicación 2
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

### Ejemplo 4: Enviar ubicación 3
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

### Ejemplo 5: Cambiar estado
```
SEND
destination:/app/delivery/status
content-type:application/json

{"pedidoId":123,"estado":"EN_CAMINO","mensaje":"Repartidor en camino"}
```

---

## 🔍 CÓMO VERIFICAR QUE ESTÁ BIEN:

En Postman, el mensaje debe verse así (sin espacios extra):

```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**NO debe verse así:**
```
    SEND
        destination:/app/delivery/location
```

---

## 🎬 PRUEBA COMPLETA PASO A PASO:

### POSTMAN 1 (Cliente - Observador):

**Paso 1:** Conectar
```
ws://localhost:8090/ws-delivery
```

**Paso 2:** Copiar y pegar esto (sin modificar):
```
SUBSCRIBE
id:sub-0
destination:/topic/delivery/123

```

**Paso 3:** Click "Send"

**Paso 4:** Esperar mensajes

---

### POSTMAN 2 (Repartidor - Emisor):

**Paso 1:** Conectar
```
ws://localhost:8090/ws-delivery
```

**Paso 2:** Copiar y pegar esto (sin modificar):
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Paso 3:** Click "Send"

**Paso 4:** Ver en Postman 1 que llegó el mensaje

**Paso 5:** Enviar otra ubicación (copiar y pegar):
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

**Paso 6:** Ver en Postman 1 que llegó el segundo mensaje

---

## ✅ RESULTADO ESPERADO:

### En Postman 1 verás:
```
MESSAGE
destination:/topic/delivery/123
content-type:application/json
subscription:sub-0
message-id:...

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428,"timestamp":"2025-11-20T13:15:00"}
```

Luego:
```
MESSAGE
destination:/topic/delivery/123
content-type:application/json
subscription:sub-0
message-id:...

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450,"timestamp":"2025-11-20T13:15:05"}
```

---

## 🐛 SI SIGUE DANDO ERROR:

1. **Borra todo** el contenido del campo de mensaje en Postman
2. **Copia EXACTAMENTE** uno de los ejemplos de arriba
3. **Pega** en Postman (Ctrl+V)
4. **NO agregues espacios** ni modifiques nada
5. Click "Send"

---

## 💡 CONSEJO:

Guarda estos mensajes en un archivo de texto y cópialos desde ahí cada vez que necesites enviar algo. Así evitas errores de formato.

---

## 📱 MENSAJES LISTOS PARA USAR:

### Set completo de ubicaciones (copia uno por uno):

**Ubicación 1:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0464,"longitud":-77.0428}
```

**Ubicación 2:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0480,"longitud":-77.0440}
```

**Ubicación 3:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0500,"longitud":-77.0450}
```

**Ubicación 4:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0530,"longitud":-77.0470}
```

**Ubicación 5:**
```
SEND
destination:/app/delivery/location
content-type:application/json

{"pedidoId":123,"latitud":-12.0550,"longitud":-77.0480}
```

---

**🎉 ¡Ahora sí funcionará! Solo copia y pega sin modificar.**
