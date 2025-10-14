# NovaBook - Documentación de Pruebas Unitarias con JUnit 5

## Resumen de la Implementación

Este proyecto ahora incluye una configuración completa de pruebas unitarias utilizando JUnit 5 con las siguientes características:

### 🛠️ Configuración Técnica

**Herramientas de Testing Configuradas:**
- **JUnit 5** (v5.10.0) - Framework principal de pruebas
- **JUnit Jupiter Engine** - Motor de ejecución para JUnit 5
- **JUnit Jupiter Params** - Para pruebas parametrizadas
- **Mockito Core** (v5.5.0) - Framework para mocking y simulación
- **AssertJ** (v3.24.2) - Librería de aserciones fluidas más expresivas

**Configuración de Maven:**
- Plugin Maven Surefire (v3.1.2) configurado para ejecutar pruebas JUnit 5
- Compilador configurado para Java 11
- Detección automática de archivos de pruebas (`*Test.java`, `*Tests.java`)

### 📁 Estructura de Directorios de Pruebas

```
src/
├── main/java/          # Código fuente principal
└── test/java/          # Código de pruebas unitarias
    └── com/codeup/novabook/Models/Entity/
        ├── BookTest.java      # Pruebas para entidad Book
        └── LoanTest.java      # Pruebas para entidad Loan
```

### 📊 Pruebas Implementadas

#### 1. BookTest.java (16 pruebas)
**Funcionalidades Testeadas:**
- ✅ Creación de libros con datos válidos
- ✅ Validación de campos requeridos (título, autor, ISBN)
- ✅ Validación de formato ISBN-10 y ISBN-13
- ✅ Gestión de disponibilidad de libros
- ✅ Validación de año de publicación
- ✅ Manejo de fechas de alta
- ✅ Casos edge y valores límite

**Ejemplos de Pruebas:**
- Validación de ISBN con diferentes formatos válidos
- Verificación de que libros no disponibles no pueden ser prestados
- Validación de campos obligatorios y sus restricciones

#### 2. LoanTest.java (12 pruebas)
**Funcionalidades Testeadas:**
- ✅ Creación de préstamos con datos válidos
- ✅ Cálculo de multas por retraso (lógica de negocio crítica)
- ✅ Identificación de préstamos vencidos
- ✅ Gestión del proceso de devolución de libros
- ✅ Manejo de fechas nulas para préstamos activos
- ✅ Diferenciación entre préstamos activos y devueltos

**Lógica de Negocio Implementada:**
- **Período de préstamo estándar:** 14 días
- **Multa diaria por retraso:** $1.0 por día
- **Cálculo automático** de días de retraso y multas correspondientes

### 🎯 Casos de Prueba Destacados

#### Pruebas Parametrizadas (con @ParameterizedTest)
```java
@ParameterizedTest
@CsvSource({
    "15, 1.0",   // 1 día de retraso: $1.0
    "16, 2.0",   // 2 días de retraso: $2.0
    "20, 6.0",   // 6 días de retraso: $6.0
    "28, 14.0"   // 14 días de retraso: $14.0
})
void testFineCalculationForOverdueLoans(int daysAgo, double expectedFine)
```

#### Pruebas con AssertJ (Sintaxis Fluida)
```java
// En lugar de: assertEquals(expected, actual)
// Usamos la sintaxis más expresiva:
assertThat(loan.isDevuelto()).isTrue();
assertThat(fine).isEqualTo(expectedFine);
assertThat(book.getIsbn()).matches("^978-\\d{10}$");
```

### 🚀 Comandos de Ejecución

#### Ejecutar todas las pruebas:
```bash
mvn test
```

#### Ejecutar pruebas específicas:
```bash
mvn test -Dtest=BookTest
mvn test -Dtest=LoanTest
```

#### Ejecutar con reporte detallado:
```bash
mvn test -Dtest=LoanTest -Dtest.output.detail=true
```

#### Limpiar y ejecutar:
```bash
mvn clean test
```

### 📈 Resultados de Ejecución

**Última Ejecución Exitosa:**
```
Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
- BookTest: 16 pruebas ✅
- LoanTest: 12 pruebas ✅
Total time: 9.245 s
Status: BUILD SUCCESS
```

### 🎨 Patrones y Buenas Prácticas Implementadas

#### 1. Patrón AAA (Arrange-Act-Assert)
```java
@Test
void testLoanCreation() {
    // Given (Arrange)
    // setUp method provides a valid loan

    // When (Act)
    // loan is created (done in setUp)

    // Then (Assert)
    assertThat(loan.getId()).isEqualTo(1);
    assertThat(loan.getLibro()).isEqualTo(book);
}
```

#### 2. Uso de @DisplayName para mayor claridad
```java
@Test
@DisplayName("Should calculate no fine for loan returned on time")
void testNoFineForOnTimeLoan() { /* ... */ }
```

#### 3. Métodos @BeforeEach para configuración
```java
@BeforeEach
void setUp() {
    // Inicialización común para todas las pruebas
    book = new Book();
    partner = new Partner();
    loan = new Loan();
}
```

#### 4. Métodos auxiliares para lógica de negocio
```java
private double calculateFine(Loan loan) {
    // Implementación del cálculo de multas
}

private boolean isLoanOverdue(Loan loan) {
    // Verificación de préstamos vencidos
}
```

### 🔄 Próximos Pasos Sugeridos

1. **Ampliar Cobertura de Pruebas:**
   - Crear pruebas para `PartnerTest.java`
   - Crear pruebas para `UserTest.java` y `RolTest.java`

2. **Pruebas de Integración:**
   - Pruebas para DAOs con base de datos en memoria (H2)
   - Pruebas para Controllers y servicios

3. **Pruebas de Rendimiento:**
   - Pruebas de carga para operaciones críticas
   - Medición de tiempo de respuesta

4. **Mocking Avanzado:**
   - Simulación de operaciones de base de datos
   - Pruebas de controladores con Mockito

### 📚 Tecnologías y Dependencias

```xml
<!-- En pom.xml -->
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <!-- ... otras dependencias de testing ... -->
</dependencies>
```

### ✨ Conclusión

La implementación de JUnit 5 en el proyecto NovaBook proporciona:
- **Cobertura robusta** de las entidades principales
- **Validación de lógica de negocio crítica** (multas por retraso)
- **Base sólida** para futuras expansiones de pruebas
- **Configuración profesional** lista para entornos de CI/CD

**Autor:** Luis Alfredo - Clan Ciénaga  
**Fecha:** Octubre 2024  
**Estado:** ✅ Implementación Completada y Testeada